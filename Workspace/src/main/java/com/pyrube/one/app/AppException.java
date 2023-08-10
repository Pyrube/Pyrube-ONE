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

import com.pyrube.one.lang.Exceptionable;

/**
 * <code>AppException</code> is the root exception for current application 
 * and extends <code>Exceptionable</code>. 
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class AppException extends Exceptionable {

	/** 
	 * serial version uid 
	 */
	private static final long serialVersionUID = 1098420166811649085L;
	
	/**
	 * constructs a new throwable with the specified message code. the
	 * cause is not initialized.
	 * @param code String. the message code/the detail message.
	 */
	public AppException(String code) {
		this(code, null, null);
	}
	
	/**
	 * constructs a new throwable with the specified cause and a detail
	 * message of <tt>(cause == null ? null : cause.toString())</tt>
	 * @param cause Throwable. the cause.
	 */
	public AppException(Throwable cause) {
		this("message.error.unexpected-exception", cause);
	}
	
	/**
	 * constructs a new throwable with the specified message code and
	 * cause. 
	 * @param code String. the message code/the detail message.
	 * @param cause Throwable. the cause.
	 */
	public AppException(String code, Throwable cause) {
		this(code, new Object[] {cause.getMessage()}, cause);
	}
	
	/**
	 * constructs a new throwable with the specified message code and
	 * the first parameter for more details. 
	 * @param code String. the message code/the detail message.
	 * @param param Object. the first parameter for more details
	 */
	public AppException(String code, Object param) {
		this(code, new Object[] {param}, null);
	}
	
	/**
	 * constructs a new throwable with the specified message code and
     * an array of parameters for more details. 
	 * @param code String. the message code/the detail message.
	 * @param params Object[]. an array of parameters for more details
	 */
	public AppException(String code, Object[] params) {
		this(code, params, null);
	}
	
	/**
	 * constructs a new throwable with the specified message code and
	 * an array of parameters for more details and cause. 
	 * @param code String. the message code/the detail message.
	 * @param params Object[]. an array of parameters for more details
	 * @param cause Throwable. the cause.
	 */
	public AppException(String code, Object[] params, Throwable cause) {
		super(code, params, cause);
	}
	
	/**
	 * return an <code>AppException</code> due the specified message code
	 * @param code String. the message code/the detail message.
	 * @return
	 */
	public static AppException due(String code) {
		return new AppException(code);
	}
	
	/**
	 * return an <code>AppException</code> due the specified cause
	 * @param cause Throwable. the cause.
	 * @return
	 */
	public static AppException due(Throwable cause) {
		return new AppException(cause);
	}
	
	/**
	 * return an <code>AppException</code> due the specified message code
	 * and cause
	 * @param code String. the message code/the detail message.
	 * @param cause Throwable. the cause.
	 * @return
	 */
	public static AppException due(String code, Throwable cause) {
		return new AppException(code, cause);
	}
	
	/**
	 * specify the <code>AppException</code> a given parameter
	 * @param param Object. the first parameter for more details
	 * @return
	 */
	public AppException param(Object param) {
		this.setParams(new Object[] {param});
		return(this);
	}
	
	/**
	 * specify the <code>AppException</code> given parameters
	 * @param params Object[]. an array of parameters for more details
	 * @return
	 */
	public AppException params(Object...params) {
		this.setParams(params);
		return(this);
	}

}