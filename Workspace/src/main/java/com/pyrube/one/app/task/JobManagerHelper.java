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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.pyrube.one.app.AppException;
import com.pyrube.one.util.Dates;

/**
 * the <code>JobManagerHelper</code> super class operates the underlying job and schedule data
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public abstract class JobManagerHelper {

	/**
	 * initializing
	 * @param params
	 */
	public abstract void init(Map<String, ?> params);
	
	/**
	 * return root job groups which do not depend on other job groups. these are the job groups to run initially.
	 * because this will be called every minute, we may use cache to improve performance.
	 * 
	 * Note : call service to get list of job groups and schedule. the groups have no dependencies, or have dependencies but runAsap=N
	 * 
	 * @return list of JobGroup (jobGroupName and schedule are important)
	 * @throws AppException
	 */
	public abstract List<JobGroup> findRootJobGroups() throws AppException;
	
	/**
	 * return job groups that depend on a given job queue group
	 * @param jobQueueGroupId
	 * @param runDate
	 * @return
	 * @throws AppException
	 */
	public abstract List<JobGroup> findNextJobGroups(String jobQueueGroupId, Date runDate) throws AppException;
	
	/**
	 * return the <code>JobGroup</code> instance for a given name and timezone ID
	 * @param jobGroupName
	 * @param timezoneId
	 * @return
	 * @throws AppException
	 */
	public abstract JobGroup findJobGroup(String jobGroupName, String timezoneId) throws AppException;
	
	/**
	 * start a given job group on running date
	 * @param jobGroup
	 * @param runDate
	 * @param startTime
	 * @return
	 * @throws AppException
	 */
	public abstract String startJobGroup(JobGroup jobGroup, Date runDate, Date startTime) throws AppException;
	
	/**
	 * end a given running job group
	 * @param jobQueueGroupId
	 * @throws AppException
	 */
	public abstract void endJobGroup(String jobQueueGroupId) throws AppException;
	
	/**
	 * return a list of jobs without depending on other jobs for a given job queue group
	 * @param jobQueueGroupId
	 * @return
	 * @throws AppException
	 */
	public abstract List<Job> findRootJobs(String jobQueueGroupId) throws AppException;

	/**
	 * return list of jobs which depend on the given job and ready to run
	 * Note: the list of jobs which depend on the given job and ready to run(status is P), and all its depending jobs' next step is C) 
	 * 
	 * @param jobQueueId
	 * @param jobQueueGroupId
	 * @return
	 * @throws AppException
	 */
	public abstract List<Job> findNextJobs(String jobQueueId, String jobQueueGroupId) throws AppException;
	
	/**
	 * count the not-finished jobs in a given job queue group
	 * @param jobQueueGroupId
	 * @return
	 * @throws AppException
	 */
	public abstract int countUnfinishedJobs(String jobQueueGroupId) throws AppException;
	
	/**
	 * return the <code>Schedule</code> instance for a given name and timezone ID
	 * @param jobGroupName
	 * @param timezoneId
	 * @return
	 * @throws AppException
	 */
	public abstract Schedule findSchedule(String jobGroupName, String timezoneId) throws AppException;
	
	/**
	 * return the JobQueueGroup data for a given job queue group id
	 * @param jobQueueGroupId
	 * @return
	 * @throws AppException
	 */
	public abstract JobQueueGroup findJobQueueGroup(String jobQueueGroupId) throws AppException;

	/**
	 * return the JobQueue data for a given job queue id
	 *  
	 * @param jobQueueId
	 * @param jobQueueGroupId
	 * @return
	 * @throws AppException
	 */
	public abstract JobQueue findJobQueue(String jobQueueId, String jobQueueGroupId) throws AppException;
	
	/**
	 * retrieve the job queue status result
	 * 
	 * @param jobQueueId
	 * @return
	 * @throws AppException
	 */
	public abstract RunResult retrieveJobQueueResult(String jobQueueId) throws AppException;
	
	/**
	 * log job progress
	 * @param jobQueueId
	 * @param jobQueueGroupId
	 * @param rr
	 * @throws AppException
	 */
	public abstract void logJobProgress(String jobQueueId, String jobQueueGroupId, RunResult rr) throws AppException;
	/**
	 * check whether a given date is a scheduled date for a given job group
	 * @param jobGroupName the job group name to check
	 * @param timezoneId the timezone ID
	 * @param date the date to check
	 * @return true - the date is a scheduled date for the job group.
	 * @throws AppException
	 */
	public boolean isDateScheduled(String jobGroupName, String timezoneId, Date date) throws AppException {
		Schedule schd = findSchedule(jobGroupName, timezoneId);
		return (isDateScheduled(schd, date));
	}
	/**
	 * check whether a given date is a scheduled date
	 * @param schedule the schedule to check against
	 * @param date the date to check
	 * @return true - the date is a scheduled date
	 * @throws AppException
	 */
	public boolean isDateScheduled(Schedule schedule, Date date) throws AppException {
		Calendar calDate = Calendar.getInstance(schedule.getTimezone());
		// date is obtained based on host default timezone, so need to adjust it to a right date in schedule timezone
		date = Dates.changeTimezone(date, TimeZone.getDefault(), calDate.getTimeZone());
		calDate.setTime(date);
		return (schedule.meetsDate(calDate));
	}
	/**
	 * check whether a given time is a scheduled time
	 * @param schedule the schedule to check against
	 * @param datetime the datetime to check
	 * @return true - the time is a scheduled time
	 * @throws AppException
	 */
	public boolean isDatetimeScheduled(Schedule schedule, Date datetime) throws AppException {
		Calendar calTime = Calendar.getInstance(schedule.getTimezone());
		// time is obtained based on host default time zone, so need to adjust it to a right date in schedule Time zone
		datetime = Dates.changeTimezone(datetime, TimeZone.getDefault(), calTime.getTimeZone());
		calTime.setTime(datetime);
		return (schedule.meetsDatetime(calTime));
	}
	
	/**
	 * build a new Job data from JobQueue
	 * @param jobQueue
	 * @return
	 */
	protected Job buildJob(JobQueue jobQueue) {
		Job job = new Job(
				jobQueue.getId(),
				jobQueue.getJobName(),
				jobQueue.getTimezoneId(),
				jobQueue.getRunDate(),
				jobQueue.getStartTime(),
				jobQueue.getJobStatus(),
				jobQueue.getJobClass(),
				jobQueue.getJobParams());
		job.setQueueGroupId(jobQueue.getGroupId());
		return job;
	}
}
