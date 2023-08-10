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

package com.pyrube.one.app.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import com.pyrube.one.app.AppConfig;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.i18n.locale.AppLocaleManager;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * message bundle <br>
 * message code is string with format: x.y.z    <br>
 * It uses resource bundle, for example bundle base is com.pyrube.resources.I18nMessages, you may
 * put resource files I18nMessages.properties, I18nMessages_en_US.properties in package com/pyrube/resources. <br>
 * A sample line of the resource bundle file: <br>
 * db.sql.error=Database SQL statement error with code {0}.
 * <br>
 * usage: <br>
 * <pre>
 *    MessageBundle mb = I18nManager.getMessageBundle();
 *    String errCode = "db.sql.error";
 *    String[] params = new String[] {"SQL-2001"};
 *    String msg = mb.getMessage(errCode, params);
 *    msg = mb.getMessage(errCode, params, AppLocaleFactory.getDefaultLocale());
 * </pre>
 * <pre>
 * The message bundle can be customized. 
 * The same resource key could be customized to have different resource 
 * content for different customization name. for example, an online 
 * application supports multiple banks in same language (i.e. English), 
 * but they want to use different terms for same data such as "Invoice" 
 * may be called "Receivable". This is the customization in addition to 
 * the locale. The customization name will be prefixed to the resource 
 * base name as the final resource name. 
 * For example, if the base name is com.pyrube.resources.I18nMessages (which is 
 * the default base name), and the customization is city code (CITY1 and CITY2),
 * then the resource bundle base name for city 1 is CITY1.com.pyrube.resources.I18nMessages.
 * If the customized resource bundle is not found, then use the default 
 * bundle com.pyrube.resources.I18nMessages.
 * </pre>
 *
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public abstract class MessageBundle {
	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(MessageBundle.class.getName());

	/**
	 * default resource bundle base name: {AppName}-I18nMessages
	 */
	private static String DEFAULT_RESOURCE_BUNDLE_BASE = "I18nMessages";

	/**
	 * message bundle basename for this instance
	 */
	protected String bundleBasename = null;

	/**
	 * message bundles for different locales, key=customized_locale_name, value=ResourceBundle
	 */
	protected Map<String, ResourceBundle> localeBundles = new ConcurrentHashMap<String, ResourceBundle>();

	static {
		try {
			String appName = AppConfig.getAppConfig().getAppName();
			if (!Strings.isEmpty(appName)) DEFAULT_RESOURCE_BUNDLE_BASE = appName + "-" + DEFAULT_RESOURCE_BUNDLE_BASE;
		} catch (Exception e) {
			logger.warn("Failed to initialize MessageBundle.", e);
		}
	}

	/**
	 * Constructor
	 */
	public MessageBundle() {
		this.bundleBasename = I18nConfig.getI18nConfig().getResourceBundleBase();
		if (Strings.isEmpty(this.bundleBasename)) this.bundleBasename = DEFAULT_RESOURCE_BUNDLE_BASE;
	}

	/**
	 * return message for a given message code using the application default locale.
	 * it is equivalent to getMessage(msgCode, null, ResourceBundleManager.getDefaultLocale())
	 */
	public String getMessage(String msgCode) {
		return (getMessage(msgCode, (Object[])null, AppLocaleManager.getDefaultLocale()));
	}

	/**
	 * return message for a given message code and locale.
	 * it is equivalent to getMessage(msgCode, null, locale)
	 */
	public String getMessage(String msgCode, Locale locale) {
		return (getMessage(msgCode, (Object[])null, locale));
	}

	/**
	 * return message for a given message code and parameters using the application default locale.
	 * it is equivalent to getMessage(msgCode, params, ResourceBundleManager.getDefaultLocale())
	 */
	public String getMessage(String msgCode, Object[] params) {
		return (getMessage(msgCode, params, AppLocaleManager.getDefaultLocale()));
	}

	/**
	 * return message for a given message code and locale
	 * @param msgCode the message code
	 * @param params the message parameters
	 * @param locale the Locale
	 * @return the formatted message. if the msg code not found or format error, then return the msg code.
	 *    it never returns null. if msgCode is null, then return empty string.
	 */
	public String getMessage(String msgCode, Object[] params, Locale locale) {
		return getMessage(msgCode, params, null, locale);
	}

	/**
	 * return message for a given message code and locale
	 * @param msgCode the message code
	 * @param params the message parameters
	 * @param defaultMessage  default message if code not found. it could contain parameters 
	 * @param locale the Locale
	 * @return the formatted message. if the msg code not found or format error, then return the 
	 *    default message or the message code if default message not provided.
	 *    it never returns null. if msgCode is null, then return empty string.
	 */
	public String getMessage(String msgCode, Object[] params, String defaultMessage, Locale locale) {
		if (msgCode == null) return Strings.EMPTY;
		String msg = null;
		try {
			if (locale == null) locale = AppLocaleManager.getDefaultLocale();
			ResourceBundle bundle = getResourceBundle(locale);
			if (bundle == null) {
				if (defaultMessage != null) {
					msg = defaultMessage;
				} else {
					return (getRawMessage(msgCode, params));
				}
			} else {
				try {
					msg = bundle.getString(msgCode);
				} catch (MissingResourceException e1) {
					// resource not found, then just return the default message or the msgCode
					if (defaultMessage != null) {
						msg = defaultMessage;
					} else {
						return (getRawMessage(msgCode, params));
					}
				}
			}
			if (params == null || params.length == 0 || msg == null || msg.length() == 0)
				return (msg != null ? msg : "");

			MessageFormat fmt = new MessageFormat(msg);
			fmt.setLocale(locale);
			return (fmt.format(params, (new StringBuffer()), null).toString());
		} catch (Exception e) {
			if (msg == null) msg = getRawMessage(msgCode, params);
			logger.warn("Failed to return message for " + msgCode, e);
			return (msg);
		}
	}

	/**
	 * return message for a given msg code and parameters using application default locale.
	 * it is equivalent to getMessage(msgCode, params, ResourceBundleManager.getDefaultLocale())
	 */
	public String getMessage(String msgCode, List<?> params) {
		return (getMessage(msgCode, params, AppLocaleManager.getDefaultLocale()));
	}

	/**
	 * return message for a given msg code and parameters.
	 * @param msgCode the message code
	 * @param params the message parameters
	 * @param locale the Locale
	 * @return the formatted message. if the msg code not found, then return the msg code.
	 *    if format is incorrect, then return null.
	 */
	public String getMessage(String msgCode, List<?> params, Locale locale) {
		Object[] objParams = null;
		if (params != null && params.size() > 0) {
			objParams = new Object[params.size()];
			params.toArray(objParams);
		}
		return (getMessage(msgCode, objParams, locale));
	}

	/**
	 * get raw message from code and parameters
	 * @param msgCode the message code
	 * @param params the message parameters
	 * @return String of code and parameters
	 */
	private String getRawMessage(String msgCode, Object[] params) {
		if (params == null) {
			return(msgCode);
		} else {
			StringBuffer sb = new StringBuffer(msgCode);
			for (int i = 0; i < params.length; ++i) {
				sb.append("; ").append(params[i] != null ? params[i].toString() : "null");
			}
			return(sb.toString());
		}
	}

	/**
	 * get message resource bundle (java.util.ResourceBundle) for a given locale
	 * @param locale the Locale. if it is null, then use application default locale
	 * @return java.util.ResourceBundle or null if it is not found
	 */
	public final ResourceBundle getResourceBundle(Locale locale) {
		if (locale == null) locale = AppLocaleManager.getDefaultLocale();
		return(getResourceBundle(locale, null));
	}
	
	/**
	 * clear cached resource bundles
	 */
	public void clearCache() {
		localeBundles = new ConcurrentHashMap<String, ResourceBundle>();
		ResourceBundle.clearCache();
	}

	/**
	 * get message resource bundle (java.util.ResourceBundle) for a given locale with the customization.
	 * 
	 * @param locale the Locale. if it is null, then use application default locale
	 * @param customizationName the customization info such as level1.level2, etc.
	 * @return java.util.ResourceBundle or null if it is not found
	 */
	protected abstract ResourceBundle getResourceBundle(Locale locale, String customizationName);
}
