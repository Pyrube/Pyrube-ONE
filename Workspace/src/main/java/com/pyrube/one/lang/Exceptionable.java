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

package com.pyrube.one.lang;

import com.pyrube.one.app.i18n.I18nManager;

/**
 * <code>Exceptionable</code> is an abstract class and extends 
 * <code>RuntimeException</code>. 
 * <p>
 * all exceptions that can be thrown in all <pro>Pyrube</pro> solutions 
 * must extend <code>Exceptionable</code>.
 * <p>
 * <code>Exceptionable</code> is the superclass of those 
 * exceptions that can be thrown during the normal operation of the 
 * Java Virtual Machine. 
 * <p>
 * a method is not required to declare in its <code>throws</code> 
 * clause any subclasses of <code>Exceptionable</code> that might 
 * be thrown during the execution of the method but not caught. 
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public abstract class Exceptionable extends RuntimeException {
	
	/** 
	 * serial version uid 
	 */
	private static final long serialVersionUID = 7998102444702083274L;

	/** 
	 * the throwable that caused this throwable to get thrown, or null if this
	 * throwable was not caused by another throwable, or if the causative
	 * throwable is unknown.  If this field is equal to this throwable itself,
	 * it indicates that the cause of this throwable has not yet been
	 * initialized.
	 */
	protected Throwable cause;
	
	/**
	 * the message code to be localized by default locale
	 */
	protected String code;

	/** 
	 * parameters for more details 
	 */
	protected Object[] params;
	
	/**
	 * constructs a new throwable with <code>null</code> as its detail message.
	 * the cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public Exceptionable() {
		this("global.error.unexpected-exception");
	}
	
	/**
	 * constructs a new throwable with the specified message code. the
	 * cause is not initialized.
	 * @param code String. the message code/the detail message.
	 */
	public Exceptionable(String code) {
		this(code, null, null);
	}
	
	/**
	 * constructs a new throwable with the specified cause and a detail
	 * message of <tt>(cause == null ? null : cause.toString())</tt>
	 * @param cause Throwable. the cause.
	 */
	public Exceptionable(Throwable cause) {
		this((cause == null ? null : cause.toString()), null, cause);
	}
	
	/**
	 * constructs a new throwable with the specified message code and
	 * cause. 
	 * @param code String. the message code/the detail message.
	 * @param cause Throwable. the cause.
	 */
	public Exceptionable(String code, Throwable cause) {
		this(code, null, cause);
	}
	
	/**
	 * constructs a new throwable with the specified message code and
	 * its parameters. 
	 * @param code String. the message code/the detail message.
	 * @param params Object[]. an array of parameters.
	 */
	public Exceptionable(String code, Object[] params) {
		this(code, params, null);
	}
	
	/**
	 * constructs a new throwable with the specified detail message and
	 * its parameters and cause.
	 * @param code String. the message code/the detail message.
	 * @param params Object[]. an array of parameters.
	 * @param cause Throwable. the cause.
	 */
	public Exceptionable(String code, Object[] params, Throwable cause) {
		super(I18nManager.getMessage(code, params), cause);
		this.code = code;
		this.params = params;
		this.cause = cause;
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
}