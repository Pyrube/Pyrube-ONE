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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.util.Dates;
import com.pyrube.one.util.concurrent.ConcurrentRunner;
import com.pyrube.one.util.xml.XmlUtility;

/**
 * Each timezone has its own <JobManager> instance.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class JobManager {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(JobManager.class.getName());
	
	/**
	 * the instances of the supported timezones. {key: timezoneId, value: JobManager}
	 */
	private static Map<String, JobManager> jobManagers = new HashMap<String, JobManager>();
	
	/**
	 * the singleton of <code>JobManagerHelper</code>
	 */
	private static JobManagerHelper jobManagerHelper = null;
	static {
		try {
			TaskConfig theConfig = TaskConfig.getTaskConfig();
			String helperClassName = theConfig.getManagerHelperClass();
			jobManagerHelper = (JobManagerHelper) Class.forName(helperClassName).newInstance();
			jobManagerHelper.init(theConfig.getManagerHelperParams());
		} catch (Exception e) {
			logger.error("Initializing JobManager error.", e);
		}
	}
	
	/**
	 * timezone ID
	 */
	private String timezoneId = null;
	
	/**
	 * constructor
	 * @param timezoneId
	 */
	private JobManager(String timezoneId) {
		this.timezoneId = timezoneId;
	}
	
	/**
	 * return a <code>JobManager</code> instance for a given timezone ID.
	 * @param timezoneId
	 * @return
	 */
	public synchronized static JobManager getInstance(String timezoneId) {
		if (timezoneId == null) throw new NullPointerException("Null timezone ID.");
		JobManager jobManager = (JobManager) jobManagers.get(timezoneId);
		if (jobManager == null) {
			jobManager = new JobManager(timezoneId);
			jobManagers.put(timezoneId, jobManager);
		}
		return (jobManager);
	}

	/**
	 * trigger all root job groups in different threads at given run time. This is
	 * called every minute by scheduler. It triggers each root job group in
	 * RootJobGroupRunner in a thread, the runner will check the job group schedule.
	 * The actual execution is done only when it is the scheduled time.
	 * 
	 * @param runTime
	 *            the run time which is on a minute
	 * @throws AppException
	 */
	public static void triggerRootJobGroups(long runTime) throws AppException {
		if (logger.isDebugEnabled()) logger.debug("Trigger root job groups in " + runTime);
		try {
			List<JobGroup> jobGroups = jobManagerHelper.findRootJobGroups();
			if (jobGroups == null || jobGroups.size() == 0) return;
			List<RootJobGroupRunner> jobGroupRunners = new ArrayList<RootJobGroupRunner>();
			Map<String, Long> timezoneTimes = new HashMap<String, Long>();
			String poolName = TaskConfig.getTaskConfig().getRunnerPoolName();
			for (JobGroup jobGroup : jobGroups) {
				// get timezone's time based on EOD date.
				String timezoneId = jobGroup.getTimezoneId();
				Long timezoneTime = timezoneTimes.get(timezoneId);
				if (timezoneTime == null) {
					Calendar eodDate = JobManager.getInstance(timezoneId).getEodDate(); // year, month and day
					Calendar timezoneCal = Calendar.getInstance(jobGroup.getSchedule().getTimezone());
					timezoneCal.setTimeInMillis(runTime);
					timezoneCal.get(Calendar.YEAR); // force to recalculate
					timezoneCal.set(eodDate.get(Calendar.YEAR), eodDate.get(Calendar.MONTH),
							eodDate.get(Calendar.DAY_OF_MONTH));
					timezoneTime = new Long(timezoneCal.getTimeInMillis());
					timezoneTimes.put(timezoneId, timezoneTime);
				}
				RootJobGroupRunner jobGroupRunner = new RootJobGroupRunner(jobGroup, timezoneTime.longValue());
				jobGroupRunners.add(jobGroupRunner);
				try {
					jobGroupRunner.start(poolName);
				} catch (Exception e) {
					logger.error("Failed to start job group " + jobGroup.getName() + "@" + timezoneId, e);
				}
			}
		} catch (AppException e) {
			logger.error("error", e);
			throw e;
		} catch (Throwable e) {
			logger.error("error", e);
			throw AppException.due("message.error.task.trigger-root-groups", e);
		}
	}
	
	public void triggerNextJobGroups(String queueGroupId, Date runDate) throws AppException {
		List<JobGroup> jobGroups = jobManagerHelper.findNextJobGroups(queueGroupId, runDate);
		if (jobGroups == null || jobGroups.size() == 0) return;
		String poolName = TaskConfig.getTaskConfig().getRunnerPoolName();
		for (JobGroup jobGroup : jobGroups) {
			if (jobManagerHelper.isDateScheduled(jobGroup.getName(), jobGroup.getTimezoneId(), runDate)) {
				JobGroupStarter jobGroupStarter = new JobGroupStarter(jobGroup.getName(), jobGroup.getTimezoneId());
				try {
					jobGroupStarter.start(poolName);
				} catch (Exception e) {
					logger.error("Failed to start job group " + jobGroup.getName() + "@" + jobGroup.getTimezoneId(), e);
				}
			}
		}
	}

	/**
	 * start a job group through scheduler automatically
	 * 
	 * @param jobGroupName
	 * @return jobQueueGroupId (job running queue group id)
	 * @throws AppException
	 */
	public String startJobGroup(String jobGroupName) throws AppException {
		return startJobGroup0(jobGroupName);
	}

	/**
	 * dispatch root jobs in a job group
	 * @param queueGroupId
	 * @throws AppException
	 */
	public void dispatchRootJobs(String queueGroupId) throws AppException {
		if (logger.isDebugEnabled()) logger.debug("Dispatches all root jobs of job queue group (" + queueGroupId + ").");
		List<Job> jobs = jobManagerHelper.findRootJobs(queueGroupId);
		runJobs0(queueGroupId, jobs);
	}

	/**
	 * dispatch next ready-to-run jobs after given job
	 * @param queueId
	 * @param queueGroupId
	 * @throws AppException
	 */
	public void dispatchNextJobs(String queueId, String queueGroupId) throws AppException {
		List<Job> jobs = jobManagerHelper.findNextJobs(queueId, queueGroupId);
		runJobs0(queueGroupId, jobs);
	}
	
	/**
	 * log job progress status
	 * 
	 * @param queueId
	 * @param queueGroupId
	 * @param rr
	 */
	public void logProgress(String queueId, String queueGroupId, RunResult rr) {
		try {
			jobManagerHelper.logJobProgress(queueId, queueGroupId, rr);
		} catch (Throwable e) {
			logger.error("Failed to log job progress.", e);
		}
	}

	/**
	 * utility to parse parameters in XML <br>
	 * 
	 * <pre>
	 * <params>
	 * 		<param name="param1Name" type="java.util.Date" format="yyyy-MM-dd">param1Value</param>
	 * 		<param name="param2Name">param2Value</param>
	 * </params>
	 * </pre>
	 * 
	 * default type is String. and type could be <br>
	 * java.util.Date <br>
	 * java.lang.String <br>
	 * java.lang.Boolean <br>
	 * java.lang.Double <br>
	 * java.lang.Long <br>
	 * java.lang.Short <br>
	 * java.lang.Integer <br>
	 * java.lang.Byte <br>
	 * java.lang.Float <br>
	 * java.lang.String <br>
	 * <br>
	 * format could be SimpleDateFormat allowed standard formats. <br>
	 * <br>
	 * if multiple parameters have same name, then they are a list.
	 * 
	 * @param paramsXml
	 * @return HashMap which are parameter name/value pairs (name is String. value
	 *         is String or List of String). if multiple parameters have same name,
	 *         then the value is List of String.
	 * @throws Exception
	 */
	public static Map<String, ?> parseParams(String paramsXml) throws Exception {
		if (paramsXml == null) return (null);
		return (XmlUtility.parseDeepProperties(paramsXml, "param"));
	}
	
	/**
	 * return EOD date of the timezone
	 * 
	 * @return Calendar. it is in default time zone. its Year, Month and Day are the
	 *         EOD date.
	 */
	public Calendar getEodDate() {
		Calendar eodCal = Calendar.getInstance(TimeZone.getTimeZone(timezoneId));
		eodCal.setTime(Dates.getDate(TimeZone.getDefault()));
		return (eodCal);
	}

	/**
	 * retrieve the job queue status result
	 * 
	 * @param jobQueueId
	 * @return RunResult
	 * @throws AppException
	 */
	public RunResult retrieveJobQueueResult(String jobQueueId) throws AppException {
		RunResult rr = null;
		try {
			rr = jobManagerHelper.retrieveJobQueueResult(jobQueueId);
		} catch (Throwable e) {
			logger.warn("Failed to retrieve job queue result.", e);
		}
		return (rr);
	}
	
	/**
	 * start job group. it's for internal use.
	 * @param jobGroupName
	 * @return
	 * @throws AppException
	 */
	private String startJobGroup0(String jobGroupName) throws AppException {
		if (logger.isDebugEnabled()) logger.debug("Starting the job group named " + jobGroupName + ".");
		String queueGroupId = null;
		JobGroup jobGroup = jobManagerHelper.findJobGroup(jobGroupName, timezoneId);
		Calendar eodCal = Calendar.getInstance(TimeZone.getTimeZone(timezoneId));
		Date eodDate = eodCal.getTime();
		boolean canRunToday = jobManagerHelper.isDateScheduled(jobGroupName, timezoneId, eodDate);
		if (!canRunToday) return queueGroupId;
		queueGroupId = jobManagerHelper.startJobGroup(jobGroup, eodDate, Dates.getGmtLongTimestamp());
		// dispatch the top-level jobs without depending on other jobs
		dispatchRootJobs(queueGroupId);
		return queueGroupId;
	}
	
	/**
	 * end the job queue group if all jobs in this group are finished
	 * @param queueGroupId
	 * @throws AppExceptino
	 */
	private void endJobGroup0(String queueGroupId) throws AppException {
		int count = jobManagerHelper.countUnfinishedJobs(queueGroupId);
		if (count != 0) return;
		jobManagerHelper.endJobGroup(queueGroupId);
		
		JobQueueGroup queueGroup = jobManagerHelper.findJobQueueGroup(queueGroupId);
		triggerNextJobGroups(queueGroupId, queueGroup.getRunDate());
	}
	
	/**
	 * run jobs. it's for internal use
	 * @param queueGroupId
	 * @param jobs
	 * @throws AppException
	 */
	private void runJobs0(String queueGroupId, List<Job> jobs) throws AppException {
		if (jobs != null && jobs.size() > 0) {
			List<Jobable> jobables = new ArrayList<Jobable>();
			for (Job job : jobs) {
				try {
					Jobable jobable = (Jobable) (Class.forName(job.getClassName()).newInstance());
					jobable.setJob(job);
					jobables.add(jobable);
				} catch (Exception e) {
					RunResult rr = new RunResult(RunResult.STATUS_ERROR, RunResult.NEXTSTEP_CONTINUE);
					rr.setMessage(new RunMessage("message.error.jobable-not-found", new Object[] {job.getClassName()}).withErrors());
					JobManager.getInstance(job.getTimezoneId()).logProgress(job.getQueueId(), job.getQueueGroupId(), rr);
					jobables.add(null);
				}
			}
			String poolName = TaskConfig.getTaskConfig().getRunnerPoolName();
			for (Jobable jobable : jobables) {
				if (jobable != null) {
					Job job = jobable.getJob();
					if (logger.isDebugEnabled()) logger.debug("Running the job named " + job.getName() + ".");
					try {
						new JobRunner(jobable).start(poolName);
					} catch (Exception e) {
						RunResult rr = new RunResult(RunResult.STATUS_ERROR, RunResult.NEXTSTEP_CONTINUE);
						rr.setMessage(new RunMessage("message.error.jobable-exception").withErrors());
						JobManager.getInstance(jobable.getJob().getTimezoneId()).logProgress(job.getQueueId(), job.getQueueGroupId(), rr);
					}
				}
			}
		}
		// end this job group if it is finished
		this.endJobGroup0(queueGroupId);
	}

	/**
	 * Root job group runner to runner the top level job groups (which don't depend
	 * on other job groups). This is the starting point to run job groups. It checks
	 * the schedule of the job group, and runs the job group only when the time
	 * (theTime) is the scheduled time.
	 */
	static class RootJobGroupRunner extends ConcurrentRunner {
		
		/**
		 * serial verion uid
		 */
		private static final long serialVersionUID = 4221066853975713171L;

		/**
		 * logger
		 */
		private static Logger logger = Logger.getInstance(RootJobGroupRunner.class.getName());
		
		/**
		 * job group data
		 */
		private JobGroup jobGroup = null;

		/**
		 * run time in milliseconds of GMT
		 */
		private long runTime = 0;

		/**
		 * constructor
		 * @param jobGroup
		 * @param runTime
		 */
		public RootJobGroupRunner(JobGroup jobGroup, long runTime) {
			super("JobGroup-" + jobGroup.getName() + "@" + jobGroup.getTimezoneId());
			this.jobGroup = jobGroup;
			this.runTime = runTime;
		}

		/**
		 * @see {@link com.pyrube.one.util.concurrent.ConcurrentRunner#run()}
		 */
		public void run() {
			try {
				if (jobGroup.onSchedule(runTime)) {
					// it is the scheduled time, then start the job group
					if (logger.isDebugEnabled())
						logger.debug("Run root job group: " + jobGroup.getName() + "@" + jobGroup.getTimezoneId());
					JobManager.getInstance(jobGroup.getTimezoneId()).startJobGroup(jobGroup.getName());
				}
			} catch (Throwable t) {
				logger.error("Error to run job group.", t);
			}
		}
	}

	/**
	 * Job group starter to be used by job manager to start the 
	 * job groups that depend on other job group.
	 */
	static class JobGroupStarter extends ConcurrentRunner {
		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = 273708304268509901L;
		/**
		 * logger
		 */
		private static Logger logger = Logger.getInstance(JobGroupStarter.class.getName());
		/**
		 * job group name
		 */
		private String jobGroupName = null;
		/**
		 * timezone ID
		 */
		private String timezoneId   = null;

		/**
		 * constructor
		 * @param jobGroupName
		 * @param timezoneId
		 */
		public JobGroupStarter(String jobGroupName, String timezoneId) {
			super("JobGroup-" + jobGroupName + "@" + timezoneId);
			this.jobGroupName = jobGroupName;
			this.timezoneId   = timezoneId;
		}

		/**
		 * @see {@link com.pyrube.one.util.concurrent.ConcurrentRunner#run()}
		 */
		public void run() {
			try {
				if (logger.isDebugEnabled())
					logger.debug("Run root job group: " + jobGroupName + "@" + timezoneId);
				JobManager.getInstance(timezoneId).startJobGroup(jobGroupName);
			} catch (Throwable t) {
				logger.error("Error to run job group.", t);
			}
		}
	}

}
