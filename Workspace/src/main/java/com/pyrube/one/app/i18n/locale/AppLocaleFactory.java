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

package com.pyrube.one.app.i18n.locale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.pyrube.one.app.Apps;
import com.pyrube.one.app.logging.Logger;

/**
 * Application-supported locale factory
 * <pre>
 *  <parameters>
 *    <param name="APP_LOCALE_DEFAULT">en_US</param>
 *  </parameters>
 *  <I18nConfig>
 *    <locales factory="com.pyrube.one.app.i18n.locale.AppLocaleFactory">
 *      <param name="locale">en_US</param>
 *      <param name="locale">zh_CN</param>
 *      <param name="default">${APP_LOCALE_DEFAULT}</param>
 *    </locales>
 *  </I18nConfig>
 * </pre>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class AppLocaleFactory {

	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(AppLocaleFactory.class.getName());
	
	/**
	 * application locale codes
	 */
	private String[] localeCodes = null;
	
	/**
	 * default locale code
	 */
	private String defaultLocaleCode = null;

	/**
	 * the default locale
	 */
	private Locale defaultLocale = null;
	
	/**
	 * default constructor
	 */
	public AppLocaleFactory() {
	}
	
	/**
	 * init
	 */
	public void init(Map<String, ?> params) {
		ArrayList<String> aLocaleCodes = new ArrayList<String>();
		if (params != null && params.size() > 0) {
			Object localeCode_s_ = params.get("locale");
			if (localeCode_s_ != null) {
				if (localeCode_s_ instanceof String) {
					String localeCode = (String)localeCode_s_;
					if (AppLocaleManager.localeOf(localeCode) != null) {
						aLocaleCodes.add(localeCode);
					} else {
						logger.warn(localeCode + "is an invalid locale.");
					}
				} else if (localeCode_s_ instanceof List) {
					for (Object localeCode : (List<?>)localeCode_s_) {
						if (AppLocaleManager.localeOf((String) localeCode) != null) {
							if (!aLocaleCodes.contains((String) localeCode)) {
								aLocaleCodes.add((String) localeCode);
							} else {
								if (logger.isDebugEnabled()) logger.debug("Duplicate locale: " + (String) localeCode);
							}
						} else {
							logger.warn((String) localeCode + "is an invalid locale.");
						}
					}
				}
			}
			defaultLocaleCode = (String) params.get("default");
		}
		if (aLocaleCodes.size() == 0) aLocaleCodes.add(Locale.getDefault().toString());
		if (defaultLocaleCode == null) {
			defaultLocaleCode = (String) aLocaleCodes.get(0);
		} else {
			if (!aLocaleCodes.contains(defaultLocaleCode)) {
				logger.warn("invalid default locale " + defaultLocaleCode + ". The the default locale will be set to " + (String) aLocaleCodes.get(0));
				defaultLocaleCode = (String) aLocaleCodes.get(0);
			}
		}
		localeCodes = new String[aLocaleCodes.size()];
		aLocaleCodes.toArray(localeCodes);
		defaultLocale = AppLocaleManager.localeOf(defaultLocaleCode);
		
		if (logger.isDebugEnabled()) {
			logger.debug(toString());
		}
	}

	/**
	 * return the default locale code
	 * @return String
	 */
	public String getDefaultLocaleCode() {
		return defaultLocaleCode;
	}

	/**
	 * return the default locale
	 * @return Locale
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * return locale codes supported in current application
	 * @return String[]
	 */
	public String[] getLocaleCodes() {
		return localeCodes;
	}
	
	/**
	 * toString
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("Application locales: ");
		for (int i = 0; i < localeCodes.length; ++i) buf.append(localeCodes[i]).append(";");
		buf.append(" The default locale is ").append(defaultLocaleCode);
		return(buf.toString());
	}
}
