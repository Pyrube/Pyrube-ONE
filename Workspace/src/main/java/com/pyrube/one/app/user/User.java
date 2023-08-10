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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

import com.pyrube.one.app.Apps;
import com.pyrube.one.lang.Strings;

/**
 * the <code>User</code> describes the user basic information. the different 
 * security application may need more attributes which can be put in 
 * Attributes list.
 *
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class User implements Serializable {

	/**
	 * JDK1.1 serial version uid
	 */
	private static final long serialVersionUID = 794804247572307159L;

	/**
	 * guest user unique key
	 */	
	private static String GUEST_UUK = "0";

	/**
	 * guest username for login
	 */	
	private static String GUEST_USERNAME = "GUEST";

	/**
	 * guest user
	 */
	public static User GUEST = GUEST();

	/**
	 * unique user key
	 * it's unique identification of user for current application. and it 
	 * should be the primary key of a <db>USER</db> table in database.
	 */
	private String uuk = null;

	/**
	 * the login name/mobile number/id no./email address
	 */
	private String login = null;

	/**
	 * the user full name
	 */
	private String name = null;

	/**
	 * the mobile number
	 */
	private String mobile = null;

	/**
	 * the email address
	 */
	private String email = null;

	/**
	 * the id number
	 */
	private String idNum = null;

	/**
	 * credentials 
	 */
	private transient String credentials = null;

	/**
	 * the 2-letter country code
	 */
	private String country = null;

	/**
	 * the locale that user currently is using. its default value is set 
	 * based on setting for user
	 */
	private Locale locale = null;

	/**
	 * the timezone that user currently is with. its default value is set 
	 * based on setting for user
	 */
	private TimeZone timezone = null;

	/**
	 * the last log-on time (GMT00) when user logs-on successfully
	 */
	private Date lastLogonTime = null;

	/**
	 * plus 1 when user logs-on unsuccessfully due to bad credentials.
	 * according to enabled security policy, user will be locked once
	 * reaches to maximum attempt times. once log-on successfully, 
	 * make it zero.
	 */
	private int attemptTimes = 0;

	/**
	 * the last attempt time (GMT00) when user logs-on unsuccessfully 
	 * due to bad credentials. once log-on successfully, make it null.
	 */
	private Date lastAttemptTime = null;

	/**
	 * the user status for security authentication
	 */
	private SecurityStatus status = null;

	/**
	 * user other attributes, (key, value) pairs
	 * use HashMap instead of Map. HashMap is Serializable
	 */
	private HashMap<String, Object> attributes = null;

	/**
	 * an array of user rights
	 * use HashSet instead of Set. HashSet is Serializable
	 */
	private HashSet<String> rights;

	/**
	 * user extension information
	 */
	private UserExt ext;

	/**
	 * constructs a new <code>User</code> with the EMPTY key
	 */
	public User() {
		this(Strings.EMPTY);
	}

	/**
	 * constructs a new <code>User</code> with the specified key
	 * @param uuk String. the user key.
	 */
	public User(String uuk) {
		this(uuk, uuk);
	}

	/**
	 * constructs a new <code>User</code> with the specified key
	 * and the specified name for login
	 * @param uuk String. the user key.
	 * @param login. the user name for login
	 */
	public User(String uuk, String login) {
		this(uuk, login, login);
	}

	/**
	 * constructs a new <code>User</code> with the specified key
	 * and the specified name for login
	 * @param uuk String. the user key.
	 * @param login. the user name for login
	 * @param name. the user full name
	 */
	public User(String uuk, String login, String name) {
		this.uuk = uuk;
		this.login = login;
		this.name = (Strings.isEmpty(name) ? login : name);
	}
	
	/**
	 * return a guest <code>User</code> instance.
	 */
	public static User GUEST() {
		User guest = new User(GUEST_UUK, GUEST_USERNAME);
		guest.setLocale(Apps.the.sys_default.locale().value());
		guest.setTimezone(TimeZone.getDefault());
		return(guest);
	}
	
	/**
	 * whether user is a guest
	 */
	public boolean isGuest() {
		return (this.equals(User.GUEST));
	}
	
	/**
	 * add more rights
	 * @param rights
	 * @return
	 */
	public User rights(HashSet<String> rights) {
		if (this.rights == null) this.rights = new HashSet<String>();
		this.rights.addAll(rights);
		return this;
	}

	/**
	 * returns the user uuk
	 * @return String
	 */
	public String uuk() {
		return uuk;
	}

	/**
	 * returns the user login name
	 * @return String
	 */
	public String loginame() {
		return login;
	}

	/**
	 * returns the user locale
	 * @return Locale
	 */
	public Locale locale() {
		return locale;
	}

	/**
	 * returns the user time zone
	 * @return TimeZone
	 */
	public TimeZone timezone() {
		return timezone;
	}

	/**
	 * returns the user attempt times
	 * @return int
	 */
	public int attempt_times() {
		return attemptTimes;
	}

	/**
	 * @deprecated
	 * @return the user key
	 * @see use <code>User{@link #uuk()}</code> instead
	 */
	public String getUuk() {
		return uuk;
	}

	/**
	 * @param uuk the uuk to set
	 */
	public void setUuk(String uuk) {
		this.uuk = uuk;
	}

	/**
	 * @deprecated
	 * @return the user login name
	 * @see use <code>User{@link #loginame()}</code> instead
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the user name
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
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the idNum
	 */
	public String getIdNum() {
		return idNum;
	}

	/**
	 * @param idNum the idNum to set
	 */
	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	/**
	 * @return the credentials
	 */
	public String getCredentials() {
		return credentials;
	}

	/**
	 * @param credentials the credentials to set
	 */
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @deprecated
	 * @return the locale
	 * @see use <code>User{@link #locale()}</code> instead
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @deprecated
	 * @return the timezone
	 * @see use <code>User{@link #timezone()} instead
	 */
	public TimeZone getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the lastLogonTime
	 */
	public Date getLastLogonTime() {
		return lastLogonTime;
	}

	/**
	 * @param lastLogonTime the lastLogonTime to set
	 */
	public void setLastLogonTime(Date lastLogonTime) {
		this.lastLogonTime = lastLogonTime;
	}

	/**
	 * @deprecated
	 * @return the attempt times
	 * @see use <code>User{@link #attempt_times()} instead
	 */
	public int getAttemptTimes() {
		return attemptTimes;
	}

	/**
	 * @param attemptTimes the attemptTimes to set
	 */
	public void setAttemptTimes(int attemptTimes) {
		this.attemptTimes = attemptTimes;
	}

	/**
	 * @return the lastAttemptTime
	 */
	public Date getLastAttemptTime() {
		return lastAttemptTime;
	}

	/**
	 * @param lastAttemptTime the lastAttemptTime to set
	 */
	public void setLastAttemptTime(Date lastAttemptTime) {
		this.lastAttemptTime = lastAttemptTime;
	}

	/**
	 * @return the status
	 */
	public SecurityStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SecurityStatus status) {
		this.status = status;
	}
	
	/**
	 * whether user is in the given status
	 * @param status
	 * @return boolean
	 */
	public boolean is(SecurityStatus status) {
		return (status == null) ? false : status.equals(this.status);
	}

	/**
	 * @return the attributes
	 */
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the rights
	 */
	public HashSet<String> getRights() {
		return rights;
	}

	/**
	 * @param rights the rights to set
	 */
	public void setRights(HashSet<String> rights) {
		this.rights = rights;
	}

	/**
	 * @return the ext
	 */
	public UserExt getExt() {
		return ext;
	}

	/**
	 * @param ext the ext to set
	 */
	public void setExt(UserExt ext) {
		this.ext = ext;
	}

	/**
	 * sets a user attribute
	 * @param attrName String. the attribute name
	 * @param attrValue Object. the attribute value
	 */
	public void setAttribute(String attrName, Object attrValue) {
		if (attrName == null || attrValue == null) return;
		if (attributes == null) attributes = new HashMap<String, Object>();
		attributes.put(attrName, attrValue);
	}

	/**
	 * gets a user attribute
	 * @param attrName String. the attribute name
	 * @return Object
	 */
	public Object getAttribute(String attrName) {
		return(attributes == null ? null : attributes.get(attrName));
	}

	/**
	 * removes attribute
	 * @param attrName String. the attribute name
	 */
	public void removeAttribute(String attrName) {
		if (attributes != null && attrName != null) attributes.remove(attrName);
	}

	/**
	 * clears all attributes
	 */
	public void clearAttributes() {
		if (attributes != null) attributes.clear();
	}

	/**
	 * check if user has the right
	 * @param right String. the right code
	 * @return
	 */
	public boolean hasRight(String right) {
		if (rights == null)
			return (false);
		return rights.contains(right);
	}
	
	/**
	 * Compares two users for equality.
	 * 
	 * @param another the object to compare with.
	 * @return <code>true</code> if the objects are the same;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object another) {
		return(another instanceof User && uuk().equals(((User) another).uuk()));
	}

	@Override
	public String toString() {
		return(this.getClass().getName()
			+ "[uuk=" + uuk + "login=" + login + "name=" + name
			+ "mobile=" + mobile  + "email=" + email + ",locale=" + locale
			+ ",attributes=" + attributes + ",rights=" + rights + "]");
	}
}