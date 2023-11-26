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

import java.io.Serializable;

/**
 * User security authentication status
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class SecurityStatus implements Serializable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 5359663321213146104L;

	/**
	 * Authentication success
	 */
	public static final SecurityStatus AUTHEN_SUCCESS = new SecurityStatus(0);

	/**
	 * Authentication failed
	 */
	public static final SecurityStatus AUTHEN_FAILED = new SecurityStatus(-1);
	/**
	 * user is enabled.
	 */
	public static final SecurityStatus ENABLED = new SecurityStatus(0);

	/**
	 * invalid password. retry logon if the maximum tries is not reached yet. display the error message.
	 */
	public static final SecurityStatus INVALID_CREDENTIAL = new SecurityStatus(11);

	/**
	 * password initialized to force user to change password. Forward to force password-change page
	 */
	public static final SecurityStatus PWD_INITIALIZED = new SecurityStatus(12);

	/**
	 * password expired to force user to change password. Forward to force password-change page
	 */
	public static final SecurityStatus PWD_EXPIRED = new SecurityStatus(14);

	/**
	 * user is not found.
	 */
	public static final SecurityStatus NOT_FOUND = new SecurityStatus(20);

	/**
	 * user already exists.
	 */
	public static final SecurityStatus DUPLICATED = new SecurityStatus(21);

	/**
	 * user is not active yet. pending for activation
	 */
	public static final SecurityStatus INACTIVE = new SecurityStatus(22);

	/**
	 * user account is locked out. such as too many tries to log in
	 */
	public static final SecurityStatus LOCKED = new SecurityStatus(23);

	/**
	 * user account is expired. such as long time not to log in
	 */
	public static final SecurityStatus EXPIRED = new SecurityStatus(24);

	/**
	 * user account is disabled. such as due to illegal attempts detected, system/administrator forces to disable this user
	 */
	public static final SecurityStatus DISABLED = new SecurityStatus(25);

	/**
	 * captcha is invalid.
	 */
	public static final SecurityStatus INVALID_CAPTCHA = new SecurityStatus(31);

	/**
	 * too many tries. user is locked. please call administrator
	 */
	public static final SecurityStatus TOO_MANY_TRIES = new SecurityStatus(32);

	/**
	 * status code
	 */
	private Integer code = null;

	/**
	 * constructor
	 * @param code is the status code
	 */
	private SecurityStatus(Integer code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * 
	 * @param status
	 * @return boolean
	 */
	public boolean equals(Integer status) {
		return this.code == status;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof SecurityStatus) return(this.equals(((SecurityStatus) other).code));
		else return(false);
	}
}
