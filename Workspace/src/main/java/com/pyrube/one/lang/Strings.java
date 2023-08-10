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

import java.util.regex.Pattern;

/**
 * the <code>Strings</code> contains various methods for manipulating strings.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class Strings {
	
	/**
	 * an empty <code>String</code>
	 */
	public static final String EMPTY = "";
	
	/**
	 * suppresses default constructor, ensuring non-instantiability.
	 */
	private Strings() {}

	/**
	 * converts a comma-delimited string into an array of strings.
	 * 
	 * @param s a delimited string
	 * @param delim a delimiter
	 * @return String[]
	 */
	public static String[] toArray(String s) {
		return toArray(s, ",");
	}

	/**
	 * converts a delimited string into an array of strings.
	 * 
	 * @param s a delimited string
	 * @param delim a delimiter
	 * @return String[]
	 */
	public static String[] toArray(String s, String delim) {
		if (s == null) return new String[0];
		if (delim == null) return new String[] { s };
		return s.split(delim);
	}

	/**
	 * returns <tt>true</tt> if a string is null or its length is zero. 
	 * otherwise, returns <tt>false</tt>.
	 * @param s String. the string to be parsed
	 * @return <tt>true</tt> for the string is null or empty
	 */
	public static boolean isEmpty(String s) {
		return(s == null || s.length() == 0);
	}

	/**
	 * returns <tt>true</tt> if a string just only contains numeric.
	 * otherwise, returns <tt>false</tt>.
	 * @param s String. the string to be parsed
	 * @return <tt>true</tt> for the string just only contains numeric.
	 */
	public static boolean isNumeric(String s) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return(pattern.matcher(s).matches());
	}

	/**
	 * capitalizes a <code>String</code>, changing the first letter to
	 * upper case. No other letters are changed.
	 * @param s the <code>String</code> to capitalize
	 * @return the capitalized <code>String</code>
	 */
	public static String capitalize(String s) {
		if (isEmpty(s)) return s;

		char baseChar = s.charAt(0);
		char updatedChar = Character.toUpperCase(baseChar);
		if (baseChar == updatedChar) {
			return s;
		}

		char[] chars = s.toCharArray();
		chars[0] = updatedChar;
		return new String(chars, 0, chars.length);
	}
}
