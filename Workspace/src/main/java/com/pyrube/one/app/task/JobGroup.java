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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;

import com.pyrube.one.app.AppException;

/**
 * the <code>JobGroup</code> data
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class JobGroup implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 3339558003222725999L;
	private String name = null;
	private String timezoneId = null;
	private boolean enabled = true;
	
	private Schedule schedule = null;
	
	private List<Job> jobs = null;
	
	private String[] dependencies = null;
	
	// the following fields are for runtime checking whether on-schedule
	/**
	 * currently active time frame (milliseconds) within which the scheduled times are calculated.
	 * time frame is [activeTimeFrameFrom, activeTimeFrameTo]
	 */
	private long activeTimeFrameFrom = 0;
	private long activeTimeFrameTo = 0;
	
	/**
	 * scheduled times within current active time frame. it is Long of milliseconds.
	 * If a time is contained in scheduledTimes, then it is scheduled time.
	 * If a time is within the active time frame but not contained in scheduledTimes, then it is not a scheduled time.
	 * If a time is out of the active time frame, then try to adjust the active time frame and recalculate scheduledTimes, and then check it in the new scheduledTimes again.
	 */
	private TreeSet<Long> scheduledTimes = null;
	
	/**
	 * constructor
	 * @param name
	 * @param timezoneId
	 */
	public JobGroup(String name, String timezoneId) {
		this.name = name;
		this.timezoneId = timezoneId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the timezoneId
	 */
	public String getTimezoneId() {
		return timezoneId;
	}

	/**
	 * @param timezoneId the timezoneId to set
	 */
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the dependencies
	 */
	public String[] getDependencies() {
		return dependencies;
	}

	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(String[] dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the jobs
	 */
	public List<Job> getJobs() {
		return jobs;
	}

	/**
	 * @param jobs the jobs to set
	 */
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	/**
	 * @return the activeTimeFrameFrom
	 */
	public long getActiveTimeFrameFrom() {
		return activeTimeFrameFrom;
	}

	/**
	 * @param activeTimeFrameFrom the activeTimeFrameFrom to set
	 */
	public void setActiveTimeFrameFrom(long activeTimeFrameFrom) {
		this.activeTimeFrameFrom = activeTimeFrameFrom;
	}

	/**
	 * @return the activeTimeFrameTo
	 */
	public long getActiveTimeFrameTo() {
		return activeTimeFrameTo;
	}

	/**
	 * @param activeTimeFrameTo the activeTimeFrameTo to set
	 */
	public void setActiveTimeFrameTo(long activeTimeFrameTo) {
		this.activeTimeFrameTo = activeTimeFrameTo;
	}

	/**
	 * @return the scheduledTimes
	 */
	public TreeSet<Long> getScheduledTimes() {
		return scheduledTimes;
	}

	/**
	 * @param scheduledTimes the scheduledTimes to set
	 */
	public void setScheduledTimes(TreeSet<Long> scheduledTimes) {
		this.scheduledTimes = scheduledTimes;
	}
	
	/**
	 * whether this job group depends on a given job group
	 * @param name job group name
	 */
	public final boolean dependsOn(String name) {
		if (dependencies == null || dependencies.length == 0) return false;
		return Arrays.asList(dependencies).contains(name);
	}
	
	/**
	 * whether this job group depends on no job group
	 */
	public final boolean dependsOnNothing() {
		return (dependencies == null || dependencies.length == 0);
	}

	/**
	 * check whether the time is on schedule to run
	 * @param theTime in milliseconds. This time in the site time zone should be on a minute (both second and millis are 0) 
	 *                                 because the schedule is in each timezone and based on minute unit.
	 * @return
	 * @throws AppException
	 */
	public final synchronized boolean onSchedule(long theTime) throws AppException {
		boolean onTime = false;
		if (theTime >= activeTimeFrameFrom && theTime <= activeTimeFrameTo) {
			onTime = (scheduledTimes != null) ? scheduledTimes.contains(new Long(theTime)) : false;
		} else {
			// adjust the active time frame
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezoneId));
			cal.setTime(new Date(theTime - 1));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			activeTimeFrameFrom = cal.getTime().getTime();
			TaskConfig theConfig = TaskConfig.getTaskConfig();
			int endYear = cal.get(Calendar.YEAR) + theConfig.getRunningYears();
			int runningCount = theConfig.getRunningCount();
			scheduledTimes = new TreeSet<Long>();
			cal = schedule.nextScheduledTime(cal);
			for (int i = 0; i < runningCount && cal != null && cal.get(Calendar.YEAR) <= endYear; ++i) {
				scheduledTimes.add(cal.getTime().getTime());
				cal.add(Calendar.MINUTE, 1);
				cal = schedule.nextScheduledTime(cal);
			}
			if (scheduledTimes.size() == 0) {
				// schedule is over
				scheduledTimes = null;
				activeTimeFrameTo = activeTimeFrameFrom + (theConfig.getRunningYears() * 2 * 365 * 24 * 3600 * 1000L);
			} else if (cal == null) {
				// schedule is over
				activeTimeFrameTo = activeTimeFrameFrom + (theConfig.getRunningYears() * 2 * 365 * 24 * 3600 * 1000L);
			} else {
				activeTimeFrameTo = cal.getTime().getTime();
			}
			
			onTime = onSchedule(theTime);
		}
		
		return(onTime);
	}
	
}
