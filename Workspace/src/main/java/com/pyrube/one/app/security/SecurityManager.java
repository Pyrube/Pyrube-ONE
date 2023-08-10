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

package com.pyrube.one.app.security;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.inquiry.SearchCriteria;
import com.pyrube.one.app.memo.Note;
import com.pyrube.one.app.user.SecurityStatus;
import com.pyrube.one.app.user.User;

/**
 * Security manager interface.
 * In the implementation class, following code can be used to access the http request and response:
 * WebContextHolder.getWebContext().getRequest()
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public interface SecurityManager {

	/**
	 * initialize the security manager with the properties
	 * @param params is the security manager parameters
	 */
	public void init(Map<String, ?> params) throws AppException;

	/**
	 * return user information of given user id. this User info will be used for authentication.
	 * 
	 * @param userKey String. To identify this user, it could be id number, mobile number or login name.
	 * @return User which includes user name, credentials, rights, company, etc.
	 * @throws AppException if any errors. the error msgCode in the exception indicates different errors. 
	 * following are some message codes.
	 * SecurityStatus.NOT_FOUND - user is not found
	 * SecurityStatus.INVALID_CREDENTIAL  - invalid password. <br>
	 * SecurityStatus.PWD_EXPIRED - authenticated but password expired so that user needs change password. <br>
	 * SecurityStatus.INACTIVE - user is inactive.
	 * SecurityStatus.LOCKED - user is locked. <br>
	 * SecurityStatus.EXPIRED - user is expired. <br>
	 * SecurityStatus.INVALID_CAPTCHA - captcha is invalid. <br>
	 * SecurityStatus.TOO_MANY_TRIES - too many tries failed. <br>
	 * SecurityStatus.AUTHEN_SUCCESS  - it is authenticated. <br>
	 */
	public User findUser(String userKey) throws AppException;
	
	/**
	 * returns user rights of given user id
	 * @param userKey
	 * @return
	 * @throws AppException
	 */
	public HashSet<String> findUserRights(String userKey) throws AppException;
	
	/**
	 * return holidays of a given year for a given country
	 * 
	 * @param countryCode the 2-letter country code
	 * @param year the given year.
	 * @return array of Date
	 * @throws AppException
	 */
	public abstract List<Date> findHolidays(String countryCode, int year) throws AppException;
	
	/**
	 * return notes of a given search criteria
	 * 
	 * @param searchCeriteria
	 * @return array of Note
	 * @throws AppException
	 */
	public abstract List<Note> findNotes(SearchCriteria<Note> searchCeriteria) throws AppException;
	
	/**
	 * leaves a note
	 * 
	 * @param note
	 * @return Note
	 * @throws AppException
	 */
	public abstract Note leaveNote(Note note) throws AppException;
	
	/**
	 * this is called after the user signs on to perform additional tasks
	 * 
	 * @param user the authenticated user
	 * @param moreProps any more properties
	 * @throws AppException
	 */
	public void afterSignon(User user, Map<String, Object> moreProps) throws AppException;
	
	/**
	 * this is called after the user fails signing on to perform additional tasks
	 * 
	 * @param userKey the failed-authentication user key
	 * @param failedStatus SecurityStatus
	 * @throws AppException
	 */
	public void failedSignon(String userKey, SecurityStatus failedStatus) throws AppException;
	
	/**
	 * modify the current user details
	 * @param details User
	 * @throws AppException
	 */
	public User updateUserDetails(User details) throws AppException;
	
	/**
	 * modify the current user's password. 
	 * @param user
	 * @param password
	 * @throws AppException
	 */
	public User changePassword(User user, String password) throws AppException;
	
	/**
	 * modify the current user's mobile. 
	 * @param user
	 * @param mobile
	 * @throws AppException
	 */
	public User changeMobile(User user, String mobile) throws AppException;
	
	/**
	 * modify the current user's email. 
	 * @param user
	 * @param email
	 * @throws AppException
	 */
	public User changeEmail(User user, String email) throws AppException;
}
