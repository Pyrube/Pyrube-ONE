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

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Finalizable;


/**
 * the <code>NativeConcurrentRunnerPool</code> is the default implementation of runner 
 * pool using the normal thread
 * <pre>
 * configuration
 * 
 *   <runnerPool name="native" default="true" helperClass="com.pyrube.one.util.concurrent.NativeConcurrentRunnerPool"/>
 * 
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class NativeConcurrentRunnerPool implements ConcurrentRunnerPool, Finalizable {

	/**
	 * serial version uid 
	 */
	private static final long serialVersionUID = 9005520349460924335L;

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(NativeConcurrentRunnerPool.class.getName());
	
	/**
	 * thread pool
	 */
	private ExecutorService threadPool = null;

	/**
	 * constructor
	 */
	public NativeConcurrentRunnerPool() { }
	
	/**
	 * initialize
	 * @param params
	 */
	public void init(Map<?, ?> params) throws AppException {
		threadPool = Executors.newCachedThreadPool();
	}
	
	/**
	 * @see {@link com.pyrube.one.util.concurrent.ConcurrentRunnerPool#start(ConcurrentRunnable, String)}
	 */
	public Future<?> start(ConcurrentRunnable runnable, String runnerName) throws AppException {
		try {
			// the result object could be updated within the runnable
			return(threadPool.submit(runnable, runnable.done()));
		} catch (Exception e) {
			logger.error("Runner (" + runnerName + ") starts with error.", e);
			throw new AppException("message.error.runner-not-started", e);
		}
	}
	
	/**
	 * @see {@link com.pyrube.one.util.concurrent.ConcurrentRunnerPool#startIgnoreStat(ConcurrentRunnable, String)}
	 */
	public void startIgnoreStat(ConcurrentRunnable runnable, String runnerName) throws AppException {
		try {
			// the result object could be updated within the runnable
			threadPool.submit(runnable, runnable.done());
		} catch (Exception e) {
			logger.error("Runner (" + runnerName + ") starts with error.", e);
			throw new AppException("message.error.runner-not-started", e);
		}
	}

	/**
	 * @see {@link com.pyrube.one.lang.Finalizable#finalize()}
	 */
	@Override
	public void finalize() {
		try {
			if (threadPool != null) threadPool.shutdown();
			if (logger.isInfoEnabled()) logger.info("Runner pool has been shutdown.");
		} catch (Throwable e) {}
	}
}
