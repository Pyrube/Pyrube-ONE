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

import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.pyrube.one.app.Apps;
import com.pyrube.one.app.i18n.I18nConfig;
import com.pyrube.one.app.i18n.locale.AppLocaleManager;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.app.user.User;

/**
 * <pre>
 * Format manager. It is the factory to create date/time and number formats for locales. 
 * 
 * First get a <code>FormatBuilder</code>, then use the <code>FormatBuilder</code> to 
 * build <code>Format</code>.
 *
 * There are two types of <code>FormatBuilder</code>, one is for system use (such as inside the application it saves 
 * date in log or internal message, etc.), another one is for end user (such as display on the web pages,
 * reports, email content, etc.).
 * 
 * System <code>FormatBuilder</code>: one of normal format builders is configured for system use in pyrube-config.xml. 
 * Normal <code>FormatBuilder</code> is based on current locale (such as user selected locale, report locale, etc.).
 * 
 * Sample usage:
 *  DateFormat dateFormat = FormatManager.dateFormatOf(...);
 *  NumberFormat dateFormat = FormatManager.numberFormatOf(...);
 *  
 * </pre>
 * Note: Format implementation (i.e. SimpleDateFormat and DecimalFormat, etc.) are not 
 * thread-safe. If you want to use a format multiple times in the code, you can save the 
 * format temporarily, but don't save a format used for multiple threads (such as save it 
 * as a static variable). Do not define static Format (Format, DateFormat, SimpleDateFormat, 
 * NumberFormat, DecimalFormat, etc.) variables in a class.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class FormatManager {
	
	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(FormatManager.class.getName());
	
	/**
	 * format category: date/number
	 */
	public static final String FORMAT_CATEGORY_DATE = "date",
								FORMAT_CATEGORY_NUMBER = "number";
	
	/**
	 * Date Format name of Long Timestamp with Zone. i.e. "yyyy-MM-dd HH:mm:ss:SSS Z"
	 */
	public static final String DFN_LONGTIMESTAMPZ = "longTimestampZ";
	
	/**
	 * Date Format name of Long Timestamp without Zone. i.e. "yyyy-MM-dd HH:mm:ss:SSS"
	 */
	public static final String DFN_LONGTIMESTAMP = "longTimestamp";
	
	/**
	 * Date Format name of Timestamp without Zone. i.e. "yyyy-MM-dd HH:mm:ss"
	 */
	public static final String DFN_TIMESTAMP = "timestamp";
	
	/**
	 * Date Format name of Date. i.e. "yyyy-MM-dd"
	 */
	public static final String DFN_DATE = "date";
	
	/**
	 * Date Format name of Month. i.e. "yyyy-MM"
	 */
	public static final String DFN_MONTH = "month";
	
	/**
	 * Date Format name of Year. i.e. "yyyy"
	 */
	public static final String DFN_YEAR = "year";

	/**
	 * Date Format name of Long Time. i.e. "HH:mm:ss:SSS"
	 */
	public static final String DFN_LONGTIME = "longTime";

	/**
	 * Date Format name of Time. i.e. "HH:mm:ss"
	 */
	public static final String DFN_TIME = "time";

	/**
	 * Date Format name of Short Time. i.e. "HH:mm"
	 */
	public static final String DFN_SHORTTIME = "shortTime";
	
	/**
	 * Number Format name of Integer. i.e. "#0", "#,##0"
	 */
	public static final String NFN_INTEGER = "integer";
	
	/**
	 * Number Format name of Float. i.e. "#0.0##############"
	 */
	public static final String NFN_FLOAT = "float";
	
	/**
	 * Number Format name of Money. i.e. "#,##0.00#"
	 */
	public static final String NFN_MONEY = "money";
	
	/**
	 * Number Format name of Money without decimals. i.e. "#,##0"
	 */
	public static final String NFN_MONEY0 = "money0";
	
	/**
	 * Number Format name of Money with one decimal. i.e. "#,##0.0"
	 */
	public static final String NFN_MONEY1 = "money1";
	
	/**
	 * Number Format name of Money with two decimals. i.e. "#,##0.00"
	 */
	public static final String NFN_MONEY2 = "money2";
	
	/**
	 * Number Format name of Money with three decimals. i.e. "#,##0.000"
	 */
	public static final String NFN_MONEY3 = "money3";
	
	/**
	 * the locale-based format builders. 
	 * key=localeCode, value=Map {key=category (date/number), value=FormatBuilder}
	 */
	private static Map<String, Map<String, FormatBuilder>> formatBuilders 
				= new HashMap<String, Map<String, FormatBuilder>>();
	
	/**
	 * the locale-based format builder factory instance
	 */
	private static FormatBuilderFactory formatBuilderFactory = null;

	/**
	 * system-used date/number format builder
	 */
	private static FormatBuilder sysDateFormatBuilder = null, 
								sysNumberFormatBuilder = null;
	
	static {
		I18nConfig theConfig = I18nConfig.getI18nConfig();
		try {
			// format factory instance
			FormatBuilderFactory factory = (FormatBuilderFactory) Class.forName(theConfig.getFormatBuilderFactoryClzName()).newInstance();
			factory.init(theConfig.getFormatBuilderClzNames(), theConfig.getFormatPatternParams());
			formatBuilderFactory = factory;
			
			sysDateFormatBuilder = getFormatBuilder(theConfig.getSysFormatLocaleCode(), FORMAT_CATEGORY_DATE);
			sysNumberFormatBuilder = getFormatBuilder(theConfig.getSysFormatLocaleCode(), FORMAT_CATEGORY_NUMBER);
		} catch (Throwable e) {
			logger.warn("Failed to initialize FormatBuilderFactory: " + theConfig.getFormatBuilderFactoryClzName(), e);
		}
	}
	
	/**
	 * Constructor
	 */
	private FormatManager() {
	}

	/**
	 * return FormatBuilder for a given locale and category. If the format is not available for them, then returns 
	 * the system-used FormatBuilder.
	 * @param localeCode the locale code
	 * @return FormatInfo
	 */
	public static FormatBuilder getFormatBuilder(String localeCode, String category) {
		Map<String, FormatBuilder> multiFormatBuilders = formatBuilders.get(localeCode);
		if (multiFormatBuilders == null) {
			multiFormatBuilders = new HashMap<String, FormatBuilder>();
			formatBuilders.put(localeCode, multiFormatBuilders);
		}
		FormatBuilder formatBuilder = multiFormatBuilders.get(category);
		if (formatBuilder == null) {
			synchronized(formatBuilders) {
				formatBuilder = (FormatBuilder) multiFormatBuilders.get(category);
				if (formatBuilder == null) {
					if (formatBuilderFactory != null) {
						FormatBuilder formatBuilder0 = formatBuilderFactory.newFormatBuilder(localeCode, category);
						if (formatBuilder0 == null) {
							if (FORMAT_CATEGORY_DATE.equals(category)) {
								formatBuilder0 = sysDateFormatBuilder;
							} else if (FORMAT_CATEGORY_NUMBER.equals(category)) {
								formatBuilder0 = sysNumberFormatBuilder;
							} else {
								logger.warn("No system-used FormatBuilder for " + category);
							}
						}
						multiFormatBuilders.put(category, formatBuilder0);
						formatBuilder = formatBuilder0;
					}
				}
			}
		}
		return(formatBuilder);
	}

	/**
	 * get format info for the current locale (locale in current running context. if it is not 
	 * available then use application default locale) and the given category. 
	 * If the format is not available for the locale, then returns the internal/default format info.
	 * 
	 * @param category the category (date/number)
	 * @return FormatInfo
	 */
	public static FormatBuilder getFormatBuilder(String category) {
		return(getFormatBuilder(getLocale().toString(), category));
	}
	
	/**
	 * return system-used date format builder (which is used for internal formating)
	 * @return FormatBuilder
	 */
	public static FormatBuilder getSysDateFormatBuilder() {
		return(sysDateFormatBuilder);
	}
	
	/**
	 * return system-used number format builder (which is used for internal formating)
	 * @return FormatBuilder
	 */
	public static FormatBuilder getSysNumberFormatBuilder() {
		return(sysNumberFormatBuilder);
	}
	
	/**
	 * return a concrete <code>Format</code> for a given locale, category and pre-defined name or pattern.
	 * @param localeCode the locale code
	 * @param category the format category. date/number/customized
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @param others other arguments. it is optional
	 * @return <code>Format</code>
	 */
	public static Format formatOf(String localeCode, String category, String nameOrPattern, Object... others) {
		FormatBuilder formatBuilder = getFormatBuilder(localeCode, category);
		return formatBuilder.formatOf(nameOrPattern, others);
	}
	
	/**
	 * return a concrete <code>Format</code> for the user/default locale, category and pre-defined name or pattern.
	 * @param category the format category. date/number/customized
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @param others other arguments. it is optional
	 * @return <code>Format</code>
	 */
	public static Format formatOf(String category, String nameOrPattern, Object... others) {
		return formatOf(getLocale().toString(), category, nameOrPattern, others);
	}
	
	/**
	 * return <code>DateFormat</code> for a given locale and pre-defined name or pattern.
	 * @param localeCode the locale code
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @param z the time zone
	 * @return <code>DateFormat</code>
	 */
	public static DateFormat dateFormatOf(String localeCode, String nameOrPattern, TimeZone z) {
		return (DateFormat) formatOf(localeCode, FORMAT_CATEGORY_DATE, nameOrPattern, z);
	}
	
	/**
	 * return <code>DateFormat</code> for a given locale and pre-defined name or pattern.
	 * @param localeCode the locale code
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @return <code>DateFormat</code>
	 */
	public static DateFormat dateFormatOf(String localeCode, String nameOrPattern) {
		return dateFormatOf(localeCode, nameOrPattern, (TimeZone)null);
	}
	
	/**
	 * return <code>DateFormat</code> for the user/default locale and pre-defined name or pattern.
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @param z the time zone
	 * @return <code>DateFormat</code>
	 */
	public static DateFormat dateFormatOf(String nameOrPattern, TimeZone z) {
		return dateFormatOf(getLocale().toString(), nameOrPattern, z);
	}
	
	/**
	 * return <code>DateFormat</code> for the user/default locale and pre-defined name or pattern.
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @return <code>DateFormat</code>
	 */
	public static DateFormat dateFormatOf(String nameOrPattern) {
		return dateFormatOf(nameOrPattern, (TimeZone) null);
	}
	
	/**
	 * return <code>NumberFormat</code> for a given locale and pre-defined name or pattern.
	 * @param localeCode the locale code
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @return <code>NumberFormat</code>
	 */
	public static NumberFormat numberFormatOf(String localeCode, String nameOrPattern) {
		return (NumberFormat) formatOf(localeCode, FORMAT_CATEGORY_NUMBER, nameOrPattern);
	}
	
	/**
	 * return <code>NumberFormat</code> for the user/default locale and pre-defined name or pattern.
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @return <code>NumberFormat</code>
	 */
	public static NumberFormat numberFormatOf(String nameOrPattern) {
		return numberFormatOf(getLocale().toString(), nameOrPattern);
	}
	
	/**
	 * return user locale in running context. If it is not available then use default application locale
	 * @return Locale
	 */
	private static Locale getLocale() {
		Locale locale = null;
		User user = Apps.the.user();
		if (user != null) locale = user.locale();
		if (locale == null) locale = AppLocaleManager.getDefaultLocale();
		return(locale);
	}
	
}
