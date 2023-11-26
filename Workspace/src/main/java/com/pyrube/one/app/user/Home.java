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

import java.util.Date;

import com.pyrube.one.app.i18n.format.annotations.Converting;
import com.pyrube.one.app.i18n.format.annotations.FormatName;
import com.pyrube.one.app.persistence.Data;
import com.pyrube.one.app.user.User;

/**
 * the <code>Home</code> data for the information presentation as below:
 * 1. login profile
 * 2. user details
 * 
 * @author Aranjuez
 * @version Oct 01, 2023
 * @since Pyrube-ONE 1.1
 */
public class Home extends Data<String> {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 3848338229313782367L;

	private User profile;
	private User details;

	private String loginame;
	private String nick;
	private String firstname;
	private String lastname;
	private String gender;
	@Converting(format = FormatName.DATE)
	private Date birthdate;
	private String country;
	private String locale;
	/**
	 * @return the profile
	 */
	public User getProfile() {
		return profile;
	}
	/**
	 * @param profile the profile to set
	 */
	public void setProfile(User profile) {
		this.profile = profile;
	}
	/**
	 * @return the details
	 */
	public User getDetails() {
		return details;
	}
	/**
	 * @param details the details to set
	 */
	public void setDetails(User details) {
		this.details = details;
	}
	/**
	 * @return the loginame
	 */
	public String getLoginame() {
		return loginame;
	}
	/**
	 * @param loginame the loginame to set
	 */
	public void setLoginame(String loginame) {
		this.loginame = loginame;
	}
	/**
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}
	/**
	 * @param nick the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}
	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the birthdate
	 */
	public Date getBirthdate() {
		return birthdate;
	}
	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
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
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
}
