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

package com.pyrube.one.app.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.pyrube.one.app.AppException;
import com.pyrube.one.lang.Strings;
import com.pyrube.one.util.crypto.base64.Base64;

/**
 * the abstract <code>Data</code> is a super class for all entities or models.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public abstract class Data<ID> implements Serializable {
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 7377434557236951018L;

	/**
	 * comments
	 */
	private String comments;
	
	/**
	 * token
	 */
	private String token = null;
	
	/**
	 * excluded IDs to filter
	 */
	private ID[] excludeds = null;
	
	/**
	 * timezone offset. {key=property name (such as updateTime), value=timezone offset in minutes for local timezone}
	 */
	private Map<String, Integer> timezoneOffsets = new HashMap<String, Integer>();
	
	/**
	 * timezone offset. {key=property name (such as updateTime), value=formated date with local timezone}
	 */
	private Map<String, String> localDates = new HashMap<String, String>();
	
	/**
	 * serialize this <code>Data</code> object to a string
	 */
	public String serialize() throws AppException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.flush();
			byte[] array = baos.toByteArray();
			return Base64.encodeNoWrap(array);
		} catch (Exception e) {
			throw AppException.due("message.error.stream-exception");
		}
	}
	
	/**
	 * objectize a base64-encoded string to a <code>Data</code> object
	 * @param enc
	 * @return
	 * @throws AppException
	 */
	public static Data<?> objectize(String enc) throws AppException {
		if (Strings.isEmpty(enc)) return null;
		try {
			byte[] array = Base64.decode(enc);
			ByteArrayInputStream bais = new ByteArrayInputStream(array);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (Data<?>) ois.readObject();
		} catch (Exception e) {
			throw AppException.due("message.error.stream-exception");
		}
	}
	
	/**
	 * return ID of this <code>Data</code>.
	 * subclass need override this method.
	 * @return
	 */
	public ID getId() {
		return null;
	}
	
	/**
	 * return status of this <code>Data</code>.
	 * subclass need override this method.
	 * @return
	 */
	public String getStatus() {
		return null;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the excludeds
	 */
	public ID[] getExcludeds() {
		return excludeds;
	}

	/**
	 * @param excludeds the excludeds to set
	 */
	public void setExcludeds(ID[] excludeds) {
		this.excludeds = excludeds;
	}

	/**
	 * @return the timezoneOffsets
	 */
	public Map<String, Integer> getTimezoneOffsets() {
		return timezoneOffsets;
	}

	/**
	 * @param timezoneOffsets the timezoneOffsets to set
	 */
	public void setTimezoneOffsets(Map<String, Integer> timezoneOffsets) {
		this.timezoneOffsets = timezoneOffsets;
	}

	/**
	 * @return the localDates
	 */
	public Map<String, String> getLocalDates() {
		return localDates;
	}

	/**
	 * @param localDates the localDates to set
	 */
	public void setLocalDates(Map<String, String> localDates) {
		this.localDates = localDates;
	}
	
}
