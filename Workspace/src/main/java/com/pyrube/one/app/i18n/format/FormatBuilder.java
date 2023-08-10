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

package com.pyrube.one.app.i18n.format;

import java.text.Format;
import java.util.Locale;
import java.util.Map;

import com.pyrube.one.lang.Strings;

/**
 * <pre>
 * <code>FormatBuilder</code> is a super class to build <code>Format</code> 
 * for such as Date, Number or Customized based on a given locale. The subclasses 
 * will implement the method <code>formatOf(...)</code> to return a respective 
 * <code>Format</code>, and need configure as below:
 * 
 * <pre>
 *  <I18nConfig>
 *    <formats system="en_US">
 *      <builders factory="com.pyrube.one.app.i18n.format.FormatBuilderFactory">
 *        <param name="date">com.pyrube.one.app.i18n.format.DateFormatBuilder</param>
 *        <param name="number">com.pyrube.one.app.i18n.format.NumberFormatBuilder</param>
 *      </builders>
 *    </formats>
 *  </I18nConfig>
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
abstract public class FormatBuilder {
	
	/**
	 * the locale of this format builder
	 */
	private Locale locale = null;
	
	/**
	 * the format separators. 
	 * key=name (dateSeparator, timeSeparator, groupSeparator, decimalSeparator); 
	 * value=separator char ('-', ':', ',', '.')
	 */
	private Map<String, String> separators = null;

	/**
	 * the format patterns. 
	 * key=format name (date, timestamp...; integer, float, money2...); 
	 * value=pattern (yyyy-MM-dd, yyyy-MM-dd HH:mm:ss; #0, #0.0###, #,##0.00)
	 */
	private Map<String, ?> patterns = null;
	
	/**
	 * constructor
	 * @param locale the locale
	 * @param separators the four separators in it: dateSeparator and timeSeparator ones for date format
	 *                                              groupSeparator and decimalSeparator ones for number format
	 * @param patterns the format patterns
	 * @throws AppException
	 */
	public FormatBuilder(Locale locale, Map<String, String> separators, Map<String, ?> patterns) {
		this.locale = locale;
		this.separators = separators;
		this.patterns = patterns;
	}
	
	/**
	 * return the locale of the format builder
	 * @return Locale
	 */
	public Locale getLocale() {
		return(locale);
	}
	
	/**
	 * return the separators of the format builder
	 * @return Map
	 */
	public Map<String, String> getSeparators() {
		return(separators);
	}
	
	/**
	 * return the patterns of the format builder
	 * @return Map
	 */
	public Map<String, ?> getPatterns() {
		return(patterns);
	}
	
	/**
	 * return date/number format separator with given name
	 * @param separatorName the separator name, such as date, time, group or decimal
	 * @return String or "" if the separator name is not found
	 */
	public String whichSeparator(String separatorName) {
		if (this.separators == null) return Strings.EMPTY;
		String separator = (String) this.separators.get(separatorName);
		return(Strings.isEmpty(separator) ? Strings.EMPTY : separator);
	}
	
	/**
	 * return date/number format pattern with given format name
	 * @param fmtName the format name (see constants DFN_XXXX, NFN_XXXX)
	 * @return String or "" if the format name is not found
	 */
	public String whichPattern(String fmtName) {
		String pattern = (String) this.patterns.get(fmtName);
		return(Strings.isEmpty(pattern) ? Strings.EMPTY : pattern);
	}
	
	/**
	 * return the <code>Format</code> for a given pre-defined format name or pattern
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @param args
	 * @return
	 */
	abstract public Format formatOf(String nameOrPattern, Object... args);
	
}
