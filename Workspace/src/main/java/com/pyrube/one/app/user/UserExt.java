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

/**
 * the <code>UserExt</code> describes the user extension information. 
 *
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class UserExt implements Serializable {

	/**
	 * JDK1.1 serial version uid
	 */
	private static final long serialVersionUID = 4183351854790309110L;

	/**
	 * the user nick name
	 */
	private String nick = null;

	/**
	 * the user gender
	 */
	private String gender = null;

	/**
	 * the 2-letter country code
	 */
	private String country = null;

	/**
	 * the birth-date
	 */
	private Date birthdate = null;

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

	@Override
	public String toString() {
		return(this.getClass().getName()
			+ "[nick=" + nick + "gender=" + gender + "country=" + country + "]");
	}
}