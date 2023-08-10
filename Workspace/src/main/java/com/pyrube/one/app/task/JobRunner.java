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

import com.pyrube.one.app.Apps;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.util.concurrent.ConcurrentRunner;

/**
 * the <code>JobRunner</code> is to run job in one single thread
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class JobRunner extends ConcurrentRunner {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1146218087039248407L;
	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(JobRunner.class.getName());
	/**
	 * the actual job to run
	 */
	private Jobable jobable;
	
	/**
	 * constructor
	 * @param jobable
	 */
	public JobRunner(Jobable jobable) {
		super("JOB-" + jobable.generateRunningId());
		this.jobable = jobable;
	}
	
	/**
	 * run this job
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Job job = jobable.getJob();
		if (logger.isDebugEnabled()) 
			logger.debug("Start running job (" + job.getName() + "-" + job.getQueueId() + ") at timezone (" + job.getTimezoneId() + ").");
		jobable.logRunningProgress(new RunMessage());
		RunResult rr = null;
		try {
			rr = jobable.run();
			if (rr != null) {
				if (rr.getMessage().getItemsError() > 0) rr.setStatus(RunResult.STATUS_ERROR);
				rr.setNextStep(RunResult.NEXTSTEP_CONTINUE);
			}
		} catch (Exception e) {
			logger.warn("Failed to run this job.", e);
			rr = new RunResult(RunResult.STATUS_ERROR, RunResult.NEXTSTEP_CONTINUE);
			rr.setMessage(new RunMessage("message.error.job-running").withErrors());
		}
		// just log the job final status and nextStep and message
		JobManager.getInstance(job.getTimezoneId()).logProgress(job.getQueueId(), job.getQueueGroupId(), rr);
		// dispatch jobs depending on this job in new threads.
		JobManager.getInstance(job.getTimezoneId()).dispatchNextJobs(job.getQueueId(), job.getQueueGroupId());
	}

}
