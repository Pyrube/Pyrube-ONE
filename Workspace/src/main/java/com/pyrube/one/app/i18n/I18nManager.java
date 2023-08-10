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

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.pyrube.one.app.Apps;
import com.pyrube.one.app.i18n.locale.AppLocaleManager;
import com.pyrube.one.app.logging.Logger;

/**
 * Manager for resource bundle of multiple-languages, application-supported locales 
 * and date/number formatting (internationalization). 
 * <pre>
 * It supports multiple languages (internationalization) resources, and customized resources. 
 * See classes <code>MessageBundle</code>, <code>AppLocaleManager</code>,
 * <code>FormatManager</code> and <code>I18nConfig</code> for the configuration. 
 * 
 *    MessageBundle mb = I18nManager.getMessageBundle();
 *    String errCode = "message.error";
 *    String[] params = new String[] {"name"};
 *    String msg = mb.getMessage(errCode, params);
 *    msg = mb.getMessage(errCode, params, I18nManager.getDefaultLocale());
 *    
 * </pre>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class I18nManager {

	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(I18nManager.class.getName());
	
	/**
	 * default message bundle
	 */
	private static MessageBundle messageBundle = null;

	/**
	 * message bundle <code>Class</code>
	 */
	private static Class<?> messageBundleClass = null;
	
	static {
		try {
			I18nConfig cfg = I18nConfig.getI18nConfig();
			String implClass = cfg.getMessageBundleClzName();
			if (implClass == null) {
				if (logger.isDebugEnabled()) 
					logger.warn("Message bundle implementation class is not provided. The default will be used.");
				messageBundleClass = FileMessageBundle.class;
			} else {
				messageBundleClass = Class.forName(implClass);
			}
		} catch (Throwable e) {
			logger.warn("Failed to initialize internationalization manager.", e);
		}
	}
	
	/**
	 * constructor
	 */
	private I18nManager() {
	}
	
	/**
	 * create a new message bundle
	 * @param resourceBundleName the logical bundle name
	 * @return
	 */
	private static MessageBundle newMessageBundle() {
		MessageBundle mb = null;
		try {
			mb = (MessageBundle) messageBundleClass.newInstance();
		} catch (Exception e) {
			logger.warn("Failed to new message bundle: " + messageBundleClass.getName() + ".", e);
		}
		return(mb);
	}

	/**
	 * get application default message bundle 
	 */
	public static MessageBundle getMessageBundle() {
		if (messageBundle == null) {
			messageBundle = newMessageBundle();
		}
		return (messageBundle);
	}

	/**
	 * return resource bundle (java.util.ResourceBundle) for the application default message 
	 * bundle and a given locale
	 * @param locale the Locale. null means the default locale
	 * @return java.util.ResourceBundle or null if the default message bundle is not defined in
	 *   config or the bundle is not found
	 */
	public static ResourceBundle getResourceBundle(Locale locale) {
		MessageBundle mb = getMessageBundle();
		if (mb == null) return (null);
		return (mb.getResourceBundle(locale));
	}

	/**
	 * clear cached resource bundles
	 */
	public static void clearCache() {
		MessageBundle mb = getMessageBundle();
		mb.clearCache();
		if (logger.isInfoEnabled()) logger.info("Resource bundles cache has been cleared");
	}
	
	/**
	 * return resource message under default bundle and default locale
	 * @param msgCode the message code
	 * @return
	 */
	public static String getMessage(String msgCode) {
		MessageBundle mb = getMessageBundle();
		return(mb != null ? mb.getMessage(msgCode) : msgCode);
	}
	
	/**
	 * return resource message under default bundle
	 * @param msgCode the message code
	 * @param locale the Locale
	 * @return
	 */
	public static String getMessage(String msgCode, Locale locale) {
		MessageBundle mb = getMessageBundle();
		return(mb != null ? mb.getMessage(msgCode, locale) : msgCode);
	}
	
	/**
	 * get resource message under default bundle and default locale
	 * @param msgCode the message code
	 * @param params the message parameters
	 * @return
	 */
	public static String getMessage(String msgCode, Object[] params) {
		MessageBundle mb = getMessageBundle();
		return(mb != null ? mb.getMessage(msgCode, params) : msgCode);
	}
	
	/**
	 * return resource message under default bundle
	 * @param msgCode the message code
	 * @param params the message parameters
	 * @param locale the Locale
	 * @return the formatted message. if the msg code not found or format error, then return the msg code.
	 *    it never returns null. if msgCode is null, then return empty string.
	 */
	public static String getMessage(String msgCode, Object[] params, Locale locale) {
		MessageBundle mb = getMessageBundle();
		return(mb != null ? mb.getMessage(msgCode, params, locale) : 
				(msgCode != null ? msgCode : ""));
	}
	
	/**
	 * return resource message under default bundle
	 * @param msgCode the message code
	 * @param params the message parameters
	 * @param defaultMessage  default message if code not found. it could contain parameters 
	 * @param locale the Locale
	 * @return the formatted message. if the message code not found or format error, then return the 
	 *    default message or the message code if default message not provided.
	 *    it never returns null. if msgCode is null, then return empty string.
	 */
	public static String getMessage(String msgCode, Object[] params, String defaultMessage, Locale locale) {
		MessageBundle mb = getMessageBundle();
		return(mb != null ? mb.getMessage(msgCode, params, defaultMessage, locale) : 
				defaultMessage != null ? defaultMessage : 
					msgCode != null ? msgCode : "");
	}
	
	/**
	 * return resource message under default bundle and default locale
	 * @param msgCode
	 * @param params
	 * @return
	 */
	public static String getMessage(String msgCode, List<?> params) {
		MessageBundle mb = getMessageBundle();
		return(mb != null ? mb.getMessage(msgCode, params) : msgCode);
	}
	
	/**
	 * return resource message under default bundle
	 * @param msgCode
	 * @param params
	 * @param locale
	 * @return
	 */
	public static String getMessage(String msgCode, List<?> params, Locale locale) {
		MessageBundle mb = getMessageBundle();
		return(mb != null ? mb.getMessage(msgCode, params, locale) : msgCode);
	}

	
	/**
	 * return default <code>Locale</code>.
	 * The default locale is configured in <locales>, if it is not 
	 * configured, then it is the system default locale.
	 * @return Locale
	 */
	public static Locale getDefaultLocale() {
		return AppLocaleManager.getDefaultLocale();
	}
	
}
