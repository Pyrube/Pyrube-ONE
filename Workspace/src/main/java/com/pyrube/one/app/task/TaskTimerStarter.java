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

package com.pyrube.one.app.task;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.pyrube.one.app.AppListener;
import com.pyrube.one.app.logging.Logger;

/**
 * Task timer starter to start up Pyrube-ONE scheduler to run at each minute
 * after application started.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class TaskTimerStarter implements AppListener {
	
	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(TaskTimerStarter.class.getName());
	
	/**
	 * main timer with JAVA timer
	 */
	private Timer timer = null;
	
	/**
	 * timer task to trigger Pyrube-ONE jobs
	 */
	private TimerTask timerTask = null;

	/**
	 * constructor
	 */
	public TaskTimerStarter() {
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
	public void afterStarted() {
		if (logger.isInfoEnabled()) logger.info("Starting Pyrube-ONE task timer ...");
		if (timer == null) timer = new Timer();
		if (timerTask == null)
			timerTask = new TimerTask() {
				@Override
				public void run() {
					long runTime = this.scheduledExecutionTime();
					JobManager.triggerRootJobGroups(runTime);
				}
				
			};
		// starts with first time (system time + delay) without seconds and millis
		// and at fixed rate - one minute
		long timeMillisAndDelay = ((System.currentTimeMillis() + 60 * 1000) / ( 60 * 1000 )) * (60 * 1000);
		timer.scheduleAtFixedRate(timerTask, new Date(timeMillisAndDelay), 60 * 1000);
	}

	/**
	 * @see com.pyrube.one.app.AppListener#beforeShutdown()
	 */
	@Override
	public void beforeShutdown() {
		if (timerTask != null) timerTask.cancel();
		if (timer != null) timer.cancel();
		if (logger.isInfoEnabled()) logger.info("End Pyrube-ONE task timer.");
	}

}
