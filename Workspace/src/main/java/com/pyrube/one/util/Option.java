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

package com.pyrube.one.util;

import java.io.Serializable;

/**
 * Mainly used for JSEA select-field.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Option implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 9156433469416477563L;
	
	/**
	 * the option value
	 */
	private String value;
	
	/**
	 * the option label to display
	 */
	private String label;

	/**
	 * Constructor
	 * @param value
	 * @param label
	 */
	private Option(String value) {
		this(value, value);
	}
	
	/**
	 * Constructor
	 * @param value
	 * @param label
	 */
	private Option(String value, String label) {
		this.value = value;
		this.label = label;
	}

	/**
	 * returns an <code>Option</code> of a value string
	 * @param value String
	 * @return <code>Option</code>
	 */
	public static Option of(String value) {
		return of(value, value);
	}
	/**
	 * returns an <code>Option</code> of a value string and label string
	 * @param value String
	 * @param label String
	 * @return <code>Option</code>
	 */
	public static Option of(String value, String label) {
		return new Option(value, label);
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getClass().getName() + "[");
		buf.append("value=" + value);
		buf.append(", ");
		buf.append("label=" + label);
		buf.append("]");
		return buf.toString();
	}	
}

