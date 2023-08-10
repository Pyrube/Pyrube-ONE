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

import java.io.Serializable;

import com.pyrube.one.app.i18n.format.FormatManager;
import com.pyrube.one.app.logging.Logger;

/**
 * Jobable abstract class. All job classes must extend this class and 
 * implement run method and provide default non-argument constructor.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public abstract class Jobable implements Serializable {
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 8708076334460298874L;

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(Jobable.class.getName());

	/**
	 * the job data
	 */
	private Job job = null;

	/**
	 * default constructor
	 */
	public Jobable() { }

	/**
	 * constructor
	 * @param job
	 */
	public Jobable(Job job) {
		this.job = job;
	}
	/**
	 * run the job task. The parameters (getJob().getParams()) can be used in the method. <br>
	 * also in order to monitor the job running progress, it calls logRunningProgress(...) during the running process. <br>
	 * During the running process, it can call isStopRequested() to check whether administrator requested it to stop, if Yes, then end the job.
	 * @param RunResult containing the totalItems, finishedItems(okItems), errorItems, warningItems processed in this job
	 */
	abstract public RunResult run();
	
	/**
	 * obtain the running result from database
	 * @return RunResult
	 */
	protected RunResult obtainRunResult() {
		RunResult rr = null;
		try {
			rr = JobManager.getInstance(job.getTimezoneId()).retrieveJobQueueResult(job.getQueueId());
		} catch (Throwable e) {
			logger.warn("Failed to obtain job running result for job queue ID " + job.getQueueId(), e);
		}
		return(rr);
	}
	
	/**
	 * generate a new job running thread id used as the name of the job running thread
	 * @return
	 */
	public String generateRunningId() {
		return (job.getName() + FormatManager.dateFormatOf("yyyyMMdd").format(job.getRunDate()));
	}
	
	/**
	 * log job running progress in JOBQ_LOG and update JOBQUEUE. 
	 * make sure this is done in its own transaction (committed after it is finished) by setting the EJB method requires New Transaction.
	 * 
	 * @param message
	 */
	public void logRunningProgress(RunMessage message) {
		try {
			RunResult rr = new RunResult(RunResult.STATUS_RUNNING, null, message);
			JobManager.getInstance(job.getTimezoneId()).logProgress(job.getQueueId(), job.getQueueGroupId(), rr);
		} catch (Throwable e) {
			logger.error("Failed to log job running status for job queue ID " + job.getQueueId(), e);
		}
	}

	/**
	 * @return the job
	 */
	public Job getJob() {
		return job;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(Job job) {
		this.job = job;
	}

}
