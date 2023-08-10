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
 * the <code>UserProfile</code> contains user security information and user 
 * preferences
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class UserProfile implements Serializable {
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 9068062279858565096L;

	/**
	 * user info
	 */
	private User user = null;

	/**
	 * indicate user could auto login
	 */
	private Boolean autoLogin = Boolean.FALSE;

	/**
	 * method to log in
	 * @category byID
	 * @category the others
	 */
	private String loginMethod = null;
	
	/**
	 * user key. 
	 * it could be anything, such as ID, code, email address, mobile number, 
	 * etc. but it must be unique for current application.
	 */
	private String userKey = null;

	/**
	 * user password
	 */
	private String password = null;

	/** 
	 * session id 
	 */
	private String session = null;

	/**
	 * constructs a new <code>UserProfile</code> with user information.
	 * @param user
	 */
	public UserProfile(User user) {
		this.user = user;
	}

	/**
	 * get user
	 * @return User
	 */
	public User getUser() {
		return(user);
	}

	/**
	 * set user. 
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;	
	}
	
	/**
	 * @return the autoLogin
	 */
	public Boolean getAutoLogin() {
		return autoLogin;
	}

	/**
	 * @param autoLogin the autoLogin to set
	 */
	public void setAutoLogin(Boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

	/**
	 * @return the loginMethod
	 */
	public String getLoginMethod() {
		return loginMethod;
	}

	/**
	 * @param loginMethod the loginMethod to set
	 */
	public void setLoginMethod(String loginMethod) {
		this.loginMethod = loginMethod;
	}

	/**
	 * @return the userKey
	 */
	public String getUserKey() {
		return userKey;
	}

	/**
	 * @param userKey the userKey to set
	 */
	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the session
	 */
	public String getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(String session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return(this.getClass().getName() 
			+ "[autoLogin=" + autoLogin + ",loginMethod=" + loginMethod
			+ ",userKey=" + userKey + ",user=" + user + "]");
	}
}