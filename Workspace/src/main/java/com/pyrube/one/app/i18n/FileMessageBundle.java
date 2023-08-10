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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.pyrube.one.app.logging.Logger;

/**
 * message bundle default implementation using properties file <br>
 * message code is string with format: x.y.z    <br>
 * It uses resource bundle, for example bundle base is com.pyrube.resources.I18nMessages, you may
 * put resource files I18nMessages.properties, I18nMessages_en_US.properties in package com/pyrube/resources. <br>
 * A sample line of the resource bundle file: <br>
 * db.sql.error=Database SQL statement error with code {0}.
 * <br>
 * 
 * <pre>
 *  <I18nConfig>
 *    <messageBundle class="com.pyrube.one.app.i18n.FileMessageBundle"/>
 *  </I18nConfig>
 * </pre>
 *
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class FileMessageBundle extends MessageBundle {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(FileMessageBundle.class.getName());
	
	/**
	 * Constructor for MessageBundle using default resource bundle base.
	 */
	public FileMessageBundle() {
		super();
	}

	/**
	 * return message resource bundle (java.util.ResourceBundle) for a given locale with the customization.
	 * 
	 * @param locale the Locale. if it is null, then use application default locale
	 * @param customizationName the customization info such as level1.level2, etc.
	 * @return java.util.ResourceBundle or null if it is not found
	 */
	protected ResourceBundle getResourceBundle(Locale locale, String customizationName) {
		// locale code, such as "en", "en_US"
		String localeCode = locale.toString();
		
		// prefix the customization name to the locale code and the resource base name. 
		// the customization name could be multiple levels like "level1.level2"
		if (customizationName != null) localeCode = customizationName + "/" + localeCode;
		
		ResourceBundle bundle = localeBundles.get(localeCode);
		if (bundle == null) {
			// this is the first time to get this resource, so try to obtain it and save it in cache
			bundle = obtainCustomizedResourceBundle(locale, customizationName);
		}
		return (bundle);
	}
	
	/**
	 * obtain the customized resource bundle for the locale and save in cache. 
	 * It tries to find the customized resource bundle. The base name with prefix of customizationName
	 * if it is not found, then try the upper-level until the original base name.
	 * @param locale
	 * @param customizationName the name could be multiple levels separated by comma such as "LEVEL1.LEVEL2"
	 * @return
	 */
	private ResourceBundle obtainCustomizedResourceBundle(Locale locale, String customizationName) {
		String localeCode = locale.toString();
		if (customizationName != null) localeCode = customizationName + "/" + localeCode;
		
		ResourceBundle bundle = localeBundles.get(localeCode);
		if (bundle == null) {
			ResourceBundle customBundle = null;
			String customBundleBasename = bundleBasename;
			try {
				if (customizationName != null) {
					// try to find the customized resource bundle. base name with prefix of customizeName
					// if it is not found, then try the upper-level until the original base name.
					// Note: customizeName could be multiple levels like "LEVEL1.LEVEL2" 
					try {
						customBundleBasename = customizationName + "." + customBundleBasename;
						customBundle = ResourceBundle.getBundle(customBundleBasename, locale);
					} catch (MissingResourceException e) {
						customBundle = null;
					}
					if (customBundle == null) {
						// try upper-level
						String customizeName1 = null;
						int iPos = customizationName.lastIndexOf('.');
						if (iPos > 0) customizeName1 = customizationName.substring(0, iPos);
						customBundle = obtainCustomizedResourceBundle(locale, customizeName1);
					}
				} else {
					customBundle = ResourceBundle.getBundle(customBundleBasename, locale);
				}
				synchronized (localeBundles) {
					if ((bundle = localeBundles.get(localeCode)) == null) {
						bundle = customBundle;
						localeBundles.put(localeCode, bundle);
						if (logger.isDebugEnabled()) 
							logger.debug("Loaded bundle for locale (" + localeCode + ") with basename (" + 
									customBundleBasename + ") at @" + System.identityHashCode(bundle));
					}
				}
			} catch (MissingResourceException e) {
				logger.warn("Message bundle for locale (" + localeCode + ") not found with basename " + customBundleBasename);
			}
		}
		return (bundle);
	}
}
