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
import java.util.Date;
import java.util.Map;

import com.pyrube.one.app.AppException;
/**
 * the <code>Job</code> data
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Job implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 6940206224898894425L;
	/**
	 * the job running queue id
	 */
	private String queueId = null;
	/**
	 * job running queue group id
	 */
	private String queueGroupId = null;
	/**
	 * timezone ID
	 */
	private String timezoneId = null;
	/**
	 * job name
	 */
	private String name = null;
	/**
	 * job run date (no time)
	 */
	private Date runDate = null;
	/**
	 * job start time in GMT:00
	 */
	private Date startTime = null;
	/**
	 * initial job status
	 */
	private String initStatus = null;
	/**
	 * the full java class name of the job, which extends Job.
	 */
	private String className = null;
	/**
	 * the job parameters in xml string
	 */
	private String paramsXml = null;
	/**
	 * job parameters in name/value pairs
	 */
	protected Map<String, ?> params = null;
	/**
	 * job names what this job depends on
	 */
	private String[] dependencies = null;
	/**
	 * constructor
	 * @param queueId job queue id
	 * @param name the job name
	 * @param timezoneId job id (SITE_CDOE:JOB_NAME)
	 * @param runDate run date
	 * @param startTime job start time in GMT:00
	 * @param initStatus the initial job status
	 * @param className the full java class name of the job which extends Job
	 * @param paramsXml the job parameters in XML string. it can be null.
	 */
	public Job(
		String queueId,
		String name,
		String timezoneId,
		Date runDate,
		Date startTime,
		String initStatus,
		String className,
		String paramsXml) {
		this.queueId = queueId;
		this.name = name;
		this.timezoneId = timezoneId;
		this.runDate = runDate;
		this.startTime = startTime;
		this.initStatus = initStatus;
		this.className = className;
		this.paramsXml = paramsXml;
	}
	/**
	 * constructor
	 * @param queueId job queue id
	 * @param name the job name
	 * @param timezoneId timezone ID
	 * @param runDate run date
	 * @param startTime job start time in GMT:00
	 * @param initStatus the initial job status
	 * @param className the full java class name of the job which extends Job
	 * @param paramsXml the job parameters in XML string. it can be null.
	 */
	public Job(
		String queueId,
		String name,
		String timezoneId,
		Date runDate,
		Date startTime,
		String initStatus,
		String className,
		Map<String, ?> params) {
		this.queueId = queueId;
		this.name = name;
		this.timezoneId = timezoneId;
		this.runDate = runDate;
		this.startTime = startTime;
		this.initStatus = initStatus;
		this.className = className;
		this.params = params;
	}
	/**
	 * get job parameters in name/value pairs
	 * @return
	 * @exception AppException
	 */
	public Map<String, ?> getParams() throws AppException {
		if (params == null) {
			// parse the xml <params><param name="param1Name" type="java.util.Date" format="yyyy-MM-dd">param1Value</param><param name="param2Name">param2Value</param></params>
			try {
				params = JobManager.parseParams(paramsXml);
			} catch (Throwable e) {
				throw new AppException("message.error.invalid-xml", e.getMessage());
			}
		}
		return (params);
	}
	
	/**
	 * get a job parameter
	 * @param paramName
	 * @return Object
	 * @throws AppException
	 */
	public Object getParamValue(String paramName) throws AppException {
		Map<String, ?> params = getParams();
		return(params == null ? null : params.get(paramName));
	}
	
	/**
	 * whether this job depends on a given job
	 * @param name job name
	 */
	public boolean dependsOn(String name) {
		if (dependencies == null || dependencies.length == 0) return false;
		return Arrays.asList(dependencies).contains(name);
	}
	
	/**
	 * whether this job depends on no job
	 */
	public final boolean dependsOnNothing() {
		return (dependencies == null || dependencies.length == 0);
	}
	
	/**
	 * @return the queueId
	 */
	public String getQueueId() {
		return queueId;
	}
	/**
	 * @param queueId the queueId to set
	 */
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	/**
	 * @return the queueGroupId
	 */
	public String getQueueGroupId() {
		return queueGroupId;
	}
	/**
	 * @param queueGroupId the queueGroupId to set
	 */
	public void setQueueGroupId(String queueGroupId) {
		this.queueGroupId = queueGroupId;
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
	 * @return the initStatus
	 */
	public String getInitStatus() {
		return initStatus;
	}
	/**
	 * @param initStatus the initStatus to set
	 */
	public void setInitStatus(String initStatus) {
		this.initStatus = initStatus;
	}
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return the paramsXml
	 */
	public String getParamsXml() {
		return paramsXml;
	}
	/**
	 * @param paramsXml the paramsXml to set
	 */
	public void setParamsXml(String paramsXml) {
		this.paramsXml = paramsXml;
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
	 * toString
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (
			"Job: "
				+ "queueId="
				+ queueId
				+ ", timezoneId="
				+ timezoneId
				+ ", name="
				+ name
				+ ", runDate="
				+ runDate
				+ ", startTime(GMT)="
				+ startTime
				+ ", initStatus="
				+ initStatus
				+ ", className="
				+ className
				+ ", params="
				+ paramsXml);
	}
}
