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

package com.pyrube.one.app.user;

import com.pyrube.one.app.persistence.Data;

/**
 * the <code>Onboarding</code> data for the authentication as below:
 * 1. sign-up
 * 
 * @author Aranjuez
 * @version Oct 01, 2023
 * @since Pyrube-ONE 1.1
 */
public class Onboarding extends Data<String> {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1805136364259912333L;

	/**
	 * instance fields
	 */
	private String id;
	private String status;
	
	private Authen authen;
	private Home details;
	
	/**
	 * Constructor
	 */
	public Onboarding() { }

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public Onboarding id(String id) {
		this.id = id;
		return(this);
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the authen
	 */
	public Authen getAuthen() {
		return authen;
	}

	/**
	 * @param authen the authen to set
	 */
	public void setAuthen(Authen authen) {
		this.authen = authen;
	}

	/**
	 * @return the details
	 */
	public Home getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(Home details) {
		this.details = details;
	}

}
