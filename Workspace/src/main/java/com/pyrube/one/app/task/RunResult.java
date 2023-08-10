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

/**
 * Job running result
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 *
 */
public class RunResult implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 2573453677927678612L;
	/**
	 * job status: pending for depending jobs to finish
	 */
	public static final String STATUS_PENDING = "P";
	/**
	 * job status: Running
	 */
	public static final String STATUS_RUNNING = "R";
	/**
	 * job status: finished with errors
	 */
	public static final String STATUS_ERROR = "E";
	/**
	 * job status: finished
	 */
	public static final String STATUS_FINISHED = "F";
	/**
	 * next step: continue to run jobs that depend on this job
	 */
	public final static String NEXTSTEP_CONTINUE = "C";
	/**
	 * next step: pause jobs that depend on this job
	 */
	public final static String NEXTSTEP_PAUSE = "P";
	/**
	 * job running status
	 */
	private String status = null;
	/**
	 * next step
	 */
	private String nextStep = null;
	/**
	 * job running message if any
	 */
	private RunMessage message = null;
	/**
	 * constructor
	 * @param status job status. check the STATUS constants
	 * @param nextStep next step. check the NEXTSTEP constants
	 */
	public RunResult(String status, String nextStep) {
		this.status = status;
		this.nextStep = nextStep;
	}
	/**
	 * constructor
	 * @param status job status. check the STATUS constants
	 * @param nextStep next step. check the NEXTSTEP constants
	 * @param message
	 */
	public RunResult(String status, String nextStep, RunMessage message) {
		this.status = status;
		this.nextStep = nextStep;
		this.message = message;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
	 * @return the message
	 */
	public RunMessage getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(RunMessage message) {
		this.message = message;
	}

}
