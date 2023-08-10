/*******************************************************************************
 * Copyright 2019, 2023 Aranjuez Poon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pyrube.one.util.concurrent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.util.concurrent.ConcurrentConfig.RunnerPoolInfo;

/**
 * the <code>ConcurrentRunner</code> supports multi-threaded runner to run in separate thread, and
 * the underlying thread pool management is configurable, which could be: 
 * normal thread, 
 * or Async EJB invocation,
 * or Message-Driven-Bean (JMS Queue), 
 * or others. <br>
 * 
 * Multiple pools can be configured in ConcurrentConfig. The default pool will be used by default.
 * You can use other configured pool by its name. 
 * 
 * <pre>
 * Usage:
 *   if you don't want to check the status after the new thread is started, then call 
 *          runner.startAndForget(..)
 *   if you want to check the result and status after the new thread is started, then call 
 *          Future<?> result = runner.start()
 *          if (result.isDone()) result.get();
 *          ...
 *   if you want to use named pool instead of default, then call
 *   		runner.startAndForget(poolName)
 *   		or
 *   		Future<?> result = runner.start(poolName) 
 *   
 * </pre>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class ConcurrentRunner implements ConcurrentRunnable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 8163655381800152057L;

	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(ConcurrentRunner.class.getName());
	
	/**
	 * runner pools. {key: poolName, value: ConcurrentRunnerPool}
	 */
	private static Map<String, ConcurrentRunnerPool> pools = null;
	
	/**
	 * default pool
	 */
	private static ConcurrentRunnerPool defPool = null;
	
	/**
	 * pool (the pool for this runner)
	 */
	private ConcurrentRunnerPool pool = null;
	
	/**
	 * the runnable (optional)
	 */
	private ConcurrentRunnable runnable = null;
	
	/**
	 * the runner name (optional)
	 */
	private String name = null;
	
	static {
		try {
			List<RunnerPoolInfo> poolInfos = ConcurrentConfig.getConcurrentConfig().getPoolInfos();
			ConcurrentRunnerPool firstPool = null;
			if (poolInfos != null) {
				pools = new HashMap<String, ConcurrentRunnerPool>();
				for (RunnerPoolInfo poolInfo : poolInfos) {
					ConcurrentRunnerPool pool = null;
					try {
						pool = (ConcurrentRunnerPool) Class.forName(poolInfo.getClassName()).newInstance();
						pool.init(poolInfo.getParams());
					} catch (ClassNotFoundException e) {
						pool = null;
						logger.error("ConcurrentRunnerPool class not found for " + poolInfo.getClassName(), e);
					} catch (Exception e) {
						pool = null;
						logger.error("ConcurrentRunnerPool initialization error for " + poolInfo.getClassName(), e);
					}
					if (pool != null) {
						pools.put(poolInfo.getName(), pool);
						if (pools.size() == 1) firstPool = pool;
						if (logger.isInfoEnabled()) logger.info("Runner pool (" + poolInfo.getName() + ") is initialized.");
					}
				}
				if (pools.size() == 0) pools = null;
			}
			if (pools != null) {
				String defPoolName = ConcurrentConfig.getConcurrentConfig().getDefaultPoolName();
				if (defPoolName != null) defPool = pools.get(defPoolName);
				if (defPool == null) {
					if (firstPool != null) {
						defPool = firstPool;
					} else {
						defPool = new NativeConcurrentRunnerPool();
						defPool.init(null);
					}
				}
				// if there is only one pool, then it must be same as the defPool, so just set pools to null
				if (pools.size() <= 1) pools = null;
			} else {
				defPool = new NativeConcurrentRunnerPool();
				defPool.init(null);
			}
			if (logger.isInfoEnabled()) logger.info("Default runner pool class is " + defPool.getClass().getName());
		} catch (Exception e) {
			logger.error("Initializing runner pools error.", e);
		}
	}
	
	/**
	 * default constructor using default pool
	 */
	public ConcurrentRunner() {
		pool = defPool;
	}
	
	/**
	 * constructor using default pool
	 * @param runnerName the runner name
	 */
	public ConcurrentRunner(String runnerName) {
		pool = defPool;
		this.name = runnerName;
	}
	
	/**
	 * constructor using given pool
	 * @param runnerName the runner name
	 * @param poolName the runner pool name
	 */
	public ConcurrentRunner(String runnerName, String poolName) {
		this.name = runnerName;
		setPoolName(poolName);
	}
	
	/**
	 * constructor using default pool
	 * @param runnable the runnable
	 */
	public ConcurrentRunner(ConcurrentRunnable runnable) {
		pool = defPool;
		this.runnable = runnable;
	}
	
	/**
	 * constructor using default pool
	 * @param runnable the runnable
	 * @param runnerName the runner name
	 */
	public ConcurrentRunner(ConcurrentRunnable runnable, String runnerName) {
		pool = defPool;
		this.runnable = runnable;
		this.name = runnerName;
	}
	
	/**
	 * constructor using default pool
	 * @param runnable the runnable
	 * @param runnerName the runner name
	 * @param poolName the runner pool name
	 */
	public ConcurrentRunner(ConcurrentRunnable runnable, String runnerName, String poolName) {
		this.runnable = runnable;
		this.name = runnerName;
		setPoolName(poolName);
	}
	
	/**
	 * return a pool for a given pool name
	 * @param poolName
	 * @return
	 */
	public static ConcurrentRunnerPool findPool(String poolName) {
		ConcurrentRunnerPool pool = null;
		if (poolName != null && pools != null) pool = pools.get(poolName);
		if (pool == null) pool = defPool;
		return(pool);
	}
	
	/**
	 * set runner pool
	 * @param poolName the runner pool name
	 */
	public final void setPoolName(String poolName) {
		if (poolName != null && pools != null) pool = pools.get(poolName);
		if (pool == null) pool = defPool;
	}
	
	/**
	 * set runner name
	 * @param runnerName
	 */
	public void setName(String runnerName) {
		this.name = runnerName;
	}
	
	/**
	 * get runner name
	 * @return
	 */
	public String getName() {
		return(name);
	}
	
	/**
	 * start the runner and return a <code>Future</code> result to be checked later
	 * @return Future
	 * @exception AppException
	 */
	public final Future<?> start() throws AppException {
		try {
			return(pool.start(this, name));
		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Starting runner error.", e);
			throw AppException.due("message.error.runner-starting", e);
		}
	}
	
	/**
	 * start to run in the given runner pool
	 * @param poolName the runner pool name
	 * @return Future
	 * @exception AppException
	 */
	public final Future<?> start(String poolName) throws AppException {
		setPoolName(poolName);
		return(start());
	}
	
	/**
	 * default run(). 
	 * If there is a ConcurrentRunnable, then runs the runnable.run(), otherwise does nothing.
	 * But application can write class to inherit this class and override run() to do its own task.
	 */
	public void run() {
		if (runnable != null) runnable.run();
	}

	/**
	 * @see {@link com.pyrube.one.util.concurrent.ConcurrentRunnable#done()}
	 */
	public Serializable done() {
		return(runnable != null ? runnable.done() : null);
	}
}
