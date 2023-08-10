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

package com.pyrube.one.app;

import java.sql.DriverManager;
import java.util.Map;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.app.task.TaskConfig;
import com.pyrube.one.lang.Finalizable;
import com.pyrube.one.util.concurrent.ConcurrentRunner;
import com.pyrube.one.util.concurrent.ConcurrentRunnerPool;

/**
 * Application resources finalizer to release thread, jdbc connection pool, etc.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class AppResourceFinalizer implements AppListener {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(AppResourceFinalizer.class.getName());
	
	/**
	 * constructor
	 */
	public AppResourceFinalizer() {
		super();
	}

	/**
	 * @see com.pyrube.one.app.AppListener#init(java.util.Map)
	 */
	@Override
	public void init(Map<String, ?> params) { }

	/**
	 * @see com.pyrube.one.app.AppListener#afterStarted()
	 */
	@Override
	public void afterStarted() {}

	/**
	 * @see com.pyrube.one.app.AppListener#beforeShutdown()
	 */
	@Override
	public void beforeShutdown() {
		if (logger.isInfoEnabled()) logger.info("Finalize appliction resources.");
		try {
			while (DriverManager.getDrivers().hasMoreElements()) {
				DriverManager.deregisterDriver(DriverManager.getDrivers().nextElement());
			}
			AbandonedConnectionCleanupThread.shutdown();
				
		} catch (Exception e) {
			logger.warn("Failed to clean up JDBC connection pool.", e);
		}
		
		try {
			ConcurrentRunnerPool pool = ConcurrentRunner.findPool(TaskConfig.getTaskConfig().getRunnerPoolName());
			if (pool != null && pool instanceof Finalizable) ((Finalizable) pool).finalize();
		} catch (Exception e) {
			logger.warn("Failed to finalize concurrent runner pool.");
		}
	}

}
