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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.i18n.locale.AppLocaleManager;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * Locale format builder factory. It will create a new instance of subclass of 
 * <code>FormatBuilder</code> based on locale and category (configured in pyrube-config.xml), 
 * and with pattern configuration in it.
 * <pre>
 *  <I18nConfig>
 *    <formats system="en_US">
 *      <builders factory="com.pyrube.one.app.i18n.format.FormatBuilderFactory">
 *        <param name="date">com.pyrube.one.app.i18n.format.DateFormatBuilder</param>
 *        <param name="number">com.pyrube.one.app.i18n.format.NumberFormatBuilder</param>
 *      </builders>
 *      <patterns>
 *        <param name="en_US">
 *          <param name="separators">
 *            <param name="dateSeparator">-</param>
 *            <param name="timeSeparator">:</param>
 *            <param name="groupSeparator">,</param>
 *            <param name="decimalSeparator">.</param>
 *          </param>
 *          <param name="date">
 *            <param name="longTimestamp">yyyy-MM-dd HH:mm:ss:SSS</param>
 *            <param name="timestamp">yyyy-MM-dd HH:mm:ss</param>
 *            <param name="date">yyyy-MM-dd</param>
 *            <param name="month">yyyy-MM</param>
 *            <param name="year">yyyy</param>
 *            <param name="longTime">HH:mm:ss:SSS</param>
 *            <param name="time">HH:mm:ss</param>
 *            <param name="shortTime">HH:mm</param>
 *          </param>
 *          <param name="number">
 *            <param name="integer">#0</param>
 *            <param name="float">#0.0##############</param>
 *            <param name="money">#,##0.00#</param>
 *            <param name="money0">#,##0</param>
 *            <param name="money1">#,##0.0</param>
 *            <param name="money2">#,##0.00</param>
 *            <param name="money3">#,##0.000</param>
 *          </param>
 *        </param>
 *      </patterns>
 *    </formats>
 *  </I18nConfig>
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class FormatBuilderFactory {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(FormatBuilderFactory.class.getName());
	
	/**
	 * format builder class names. {key=category(date/number), value=builderClass}
	 */
	private Map<String, String> formatBuilderClzNames = new HashMap<String, String>();
	
	/**
	 * format categories (date/number)
	 */
	private List<String> categories = new ArrayList<String>();
	
	/**
	 * format separators
	 * key=localeCode, 
	 * value=Map {key=dateSeparator/timeSeparator/groupSeparator/decimalSeparator, 
	 *            value=separator char}
	 */
	private Map<String, Map<String, String>> separators
					= new HashMap<String, Map<String, String>>();
	
	/**
	 * format builder configuration infos
	 * key=localeCode, value=Map {key=category, value=Map {key=formatName, value=pattern}}
	 */
	private Map<String, Map<String, Map<String, String>>> formatBuilderInfos 
					= new HashMap<String, Map<String, Map<String, String>>>();

	/**
	 * initialize the factory with configured parameters.
	 * this is called once.
	 * @param params the initialization parameters
	 */
	public void init(Map<String, ?> builderClzNames, Map<String, ?> patterns) {
		// format builder class
		if (builderClzNames != null && builderClzNames.size() > 0) {
			Set<String> categoryNames = builderClzNames.keySet();
			for (String categoryName : categoryNames) {
				categories.add(categoryName);
				formatBuilderClzNames.put(categoryName, (String) builderClzNames.get(categoryName));
			}
		}
		// format separators and patterns
		if (patterns != null && patterns.size() > 0) {
			Set<String> localeCodes = patterns.keySet();
			for (String localeCode : localeCodes) {
				Map<String, Map<String, String>> m = (Map<String, Map<String, String>>) patterns.get(localeCode);
				separators.put(localeCode, (Map<String, String>) m.get("separators"));
				Map<String, Map<String, String>> categoryPatterns = new HashMap<String, Map<String, String>>();
				for (String category : categories) {
					categoryPatterns.put(category, (Map<String, String>) m.get(category));
				}
				formatBuilderInfos.put(localeCode, categoryPatterns);
			}
		}
	}
	
	/**
	 * return format builder for a given locale and category
	 * 
	 * @param localeCode the locale code such as en_US
	 * @param category the category such as date, number
	 * @return FormatBuilder or null if it can not get format builder for the locale and the category
	 */
	public FormatBuilder newFormatBuilder(String localeCode, String category) throws AppException {
		if (logger.isDebugEnabled()) {
			logger.debug("New FormatBuilder for the given locale: " + localeCode + " and category: " + category);
		}
		if (Strings.isEmpty(localeCode) || Strings.isEmpty(category)) return null;
		String formatBuilderClzName = formatBuilderClzNames.get(category);
		if (Strings.isEmpty(formatBuilderClzName)) throw new AppException("messag.error.format.builder-not-defined", category);
		Map<String, Map<String, String>> localeFormatBuilderInfos = (Map<String, Map<String, String>>) formatBuilderInfos.get(localeCode);
		if (localeFormatBuilderInfos == null) {
			if (logger.isDebugEnabled()) logger.debug("Format patterns are not configured for " + localeCode);
			return null;
		}
		try {
			Class<?> clz = Class.forName(formatBuilderClzName);
			Constructor<?> cons = clz.getConstructor(Locale.class, Map.class, Map.class);
			return (FormatBuilder) cons.newInstance(AppLocaleManager.localeOf(localeCode), (Map<String, String>) separators.get(localeCode), (Map) localeFormatBuilderInfos.get(category));
		} catch (Throwable t) {
			logger.error("Failed to new FormatBuilder for the given locale: " + localeCode + " and category: " + category, t);
			throw new AppException("message.error.format.new-builder-failure", new Object[] {localeCode, category}, t);
		}
	}

}
