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
import java.math.BigDecimal;
import java.util.Date;

import com.pyrube.one.app.i18n.format.FormatManager;

/** 
 * the <code>JobQueue</code> data
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class JobQueue implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 4009341632506011835L;
	private String id = null;
	private String groupId = null;
	private String jobGroupName = null;
	private String jobName = null;
	private String timezoneId = null;
	private Date runDate = null;
	private Date startTime = null;
	private long itemsTotal = 0;
	private long itemsFinished = 0;
	private long itemsError = 0;
	private String jobStatus = null;
	private String nextStep = null;
	private String progress = null;
	private String dependJob = null;
	private String jobClass = null;
	private String jobParams = null;

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
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
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
	 * @return the itemsTotal
	 */
	public long getItemsTotal() {
		return itemsTotal;
	}

	/**
	 * @param itemsTotal the itemsTotal to set
	 */
	public void setItemsTotal(long itemsTotal) {
		this.itemsTotal = itemsTotal;
	}
	
	/**
	 * add number of items total
	 * @param itemsTotal
	 * @return
	 */
	public JobQueue addItemsTotal(long itemsTotal) {
		this.itemsTotal += itemsTotal;
		return this;
	}

	/**
	 * @return the itemsFinished
	 */
	public long getItemsFinished() {
		return itemsFinished;
	}

	/**
	 * @param itemsFinished the itemsFinished to set
	 */
	public void setItemsFinished(long itemsFinished) {
		this.itemsFinished = itemsFinished;
	}
	
	/**
	 * add number of items finished
	 * @param itemsFinished
	 * @return
	 */
	public JobQueue addItemsFinished(long itemsFinished) {
		this.itemsFinished += itemsFinished;
		return this;
	}

	/**
	 * @return the itemsError
	 */
	public long getItemsError() {
		return itemsError;
	}

	/**
	 * @param itemsError the itemsError to set
	 */
	public void setItemsError(long itemsError) {
		this.itemsError = itemsError;
	}
	
	/**
	 * add number of items error
	 * @param itemsError
	 * @return
	 */
	public JobQueue addItemsError(long itemsError) {
		this.itemsError += itemsError;
		return this;
	}

	/**
	 * @return the jobStatus
	 */
	public String getJobStatus() {
		return jobStatus;
	}

	/**
	 * @param jobStatus the jobStatus to set
	 */
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	/**
	 * @return the nextStep
	 */
	public String getNextStep() {
		return nextStep;
	}

	/**
	 * @param nextStep the nextStep to set
	 */
	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	/**
	 * @return the dependJob
	 */
	public String getDependJob() {
		return dependJob;
	}

	/**
	 * @param dependJob the dependJob to set
	 */
	public void setDependJob(String dependJob) {
		this.dependJob = dependJob;
	}

	/**
	 * @return the jobClass
	 */
	public String getJobClass() {
		return jobClass;
	}

	/**
	 * @param jobClass the jobClass to set
	 */
	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	/**
	 * @return the jobParams
	 */
	public String getJobParams() {
		return jobParams;
	}

	/**
	 * @param jobParams the jobParams to set
	 */
	public void setJobParams(String jobParams) {
		this.jobParams = jobParams;
	}
	
	/**
	 * @return the progress
	 */
	public String getProgress() {
		BigDecimal t = new BigDecimal(0d);
		if (this.getItemsTotal() != 0) {
			// expression like :{[(a+b+c)/d]}*100
			BigDecimal finishedItems = new BigDecimal(this.getItemsError() + this.getItemsFinished());
			BigDecimal totalItems = new BigDecimal(this.getItemsTotal());
			t = new BigDecimal((100 * totalItems.doubleValue()) / finishedItems.doubleValue());
			//t = Arith.div(finItems.multiply(new BigDecimal(100)), totalItems);
		}

		return FormatManager.numberFormatOf("float").format(t.doubleValue());
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(String progress) {
		this.progress = progress;
	}
}