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

package com.pyrube.one.app;

import java.io.Serializable;

/**
 * Application message to represent information on model layer, such as model
 * validation error information, other messages to be shown on page
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class AppMessage implements Serializable {
	/**
	 * serial verion uid
	 */
	private static final long serialVersionUID = 5806230521521271540L;
	/**
	 * fatal level message
	 */
	public final static String LEVEL_FATAL = "fatal";
	/**
	 * error level message
	 */
	public final static String LEVEL_ERROR = "error";
	/**
	 * warn level message
	 */
	public final static String LEVEL_WARN = "warn";
	/**
	 * debug level message
	 */
	public final static String LEVEL_DEBUG = "debug";
	/**
	 * info level message
	 */
	public final static String LEVEL_INFO = "info";
	/**
	 * success message
	 */
	public final static String LEVEL_SUCCESS = "success";
	/**
	 * failed message
	 */
	public final static String LEVEL_FAILURE = "failure";
	/**
	 * the object name on which the message is. For form field validation, it is
	 * effected object in the form
	 */
	private String objectName;
	/**
	 * the affected field of the object
	 */
	private String field;
	/**
	 * true indicating it is an field error
	 */
	private boolean fieldError;
	/**
	 * the level of message severity
	 */
	private String level = LEVEL_ERROR;
	/**
	 * message code
	 */
	private String code;
	
	/**
	 * message parameters
	 */
	private Object[] params;

	/**
	 * default constructor
	 */
	public AppMessage() {
	}

	/**
	 * constructor
	 * @param code the message code
	 */
	public AppMessage(String code) {
		this(code, null);
	}

	/**
	 * constructor
	 * @param code the message code
	 * @param param the message parameter
	 */
	public AppMessage(String code, Object param) {
		this(code, new Object[] { param });
	}

	/**
	 * constructor
	 * @param code the message code
	 * @param params the message parameters
	 */
	public AppMessage(String code, Object[] params) {
		this.code = code;
		this.params = params;
	}
	
	/**
	 * return an array of <code>AppMessage</code>s
	 * @param appMessages
	 * @return
	 */
	public static AppMessage[] arrayOf(AppMessage...appMessages) {
		return appMessages;
	}
	
	/**
	 * return an <code>AppMessage</code> with code
	 * @param code
	 * @return
	 */
	public static AppMessage with(String code) {
		return new AppMessage(code);
	}

	/**
	 * return an <code>AppMessage</code> with code and info-level
	 * @param code
	 * @return
	 */
	public static AppMessage info(String code) {
		return with(code).level(LEVEL_INFO);
	}

	/**
	 * return an <code>AppMessage</code> with code and warn-level
	 * @param code
	 * @return
	 */
	public static AppMessage warn(String code) {
		return with(code).level(LEVEL_WARN);
	}

	/**
	 * return an <code>AppMessage</code> with code and error-level
	 * @param code
	 * @return
	 */
	public static AppMessage error(String code) {
		return with(code).level(LEVEL_ERROR);
	}

	/**
	 * return a successful <code>AppMessage</code> with code
	 * @param code
	 * @return
	 */
	public static AppMessage success(String code) {
		return with(code).level(LEVEL_SUCCESS);
	}

	/**
	 * return a unsuccessful <code>AppMessage</code> with code
	 * @param code
	 * @return
	 */
	public static AppMessage failure(String code) {
		return with(code).level(LEVEL_FAILURE);
	}

	/**
	 * specify the <code>AppMessage</code> a given level
	 * @param param
	 * @return
	 */
	public AppMessage level(String level) {
		this.level = level;
		return(this);
	}
	
	/**
	 * specify the <code>AppMessage</code> a parameter
	 * @param param
	 * @return
	 */
	public AppMessage param(Object param) {
		this.params = new Object[] { param };
		return(this);
	}

	/**
	 * specify the <code>AppMessage</code> parameters
	 * @param param
	 * @return
	 */
	public AppMessage params(Object... params) {
		this.params = params;
		return(this);
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the fieldError
	 */
	public boolean isFieldError() {
		return fieldError;
	}

	/**
	 * @param fieldError the fieldError to set
	 */
	public void setFieldError(boolean fieldError) {
		this.fieldError = fieldError;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the params
	 */
	public Object[] getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Object[] params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "ModelMessage [objectName="
			+ objectName
			+ ", field="
			+ field
			+ ", fieldError="
			+ fieldError
			+ ", level="
			+ level
			+ ", code="
			+ code
			+ ", params="
			+ params
			+ "]";
	}
}
