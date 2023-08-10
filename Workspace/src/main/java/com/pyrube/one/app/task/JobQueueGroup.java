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
import java.util.Date;

import com.pyrube.one.app.AppMessage;
/** 
 * the <code>JobQueueGroup</code> data
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class JobQueueGroup implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1606780194752475357L;
	private String id = null;
	private String jobGroupName = null;
	private String timezoneId = null;
	private Date runDate = null;
	private String runStatus = null;
	private Date startTime = null;
	private AppMessage message = null;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the jobGroupName
	 */
	public String getJobGroupName() {
		return jobGroupName;
	}
	/**
	 * @param jobGroupName the jobGroupName to set
	 */
	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
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
	 * @return the runDate
	 */
	public Date getRunDate() {
		return runDate;
	}
	/**
	 * @param runDate the runDate to set
	 */
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	/**
	 * @return the runStatus
	 */
	public String getRunStatus() {
		return runStatus;
	}
	/**
	 * @param runStatus the runStatus to set
	 */
	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the message
	 */
	public AppMessage getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(AppMessage message) {
		this.message = message;
	}
}
