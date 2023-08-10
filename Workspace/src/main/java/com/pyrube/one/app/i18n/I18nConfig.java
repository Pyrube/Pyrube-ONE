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

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.config.ConfigManager;
import com.pyrube.one.app.config.Configurator;
import com.pyrube.one.app.i18n.format.FormatBuilderFactory;
import com.pyrube.one.app.i18n.locale.AppLocaleFactory;
import com.pyrube.one.app.logging.Logger;

/**
 * resource bundle, locale and format configurator <br>
 * <pre>
 *  <I18nConfig>
 *    <messageBundle class="com.pyrube.one.app.i18n.FileMessageBundle">
 *      <param name="base">com.pyrube.resources.I18nMessages</param>
 *    </messageBundle>
 *    <locales factory="com.pyrube.one.app.i18n.locale.AppLocaleFactory">
 *      <param name="locale">en_US</param>
 *      <param name="locale">zh_CN</param>
 *      <param name="default">en_US</param>
 *    </locales>
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
 * @since Pyrube-ONE 1.0
 */
public class I18nConfig extends Configurator {
	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(I18nConfig.class.getName());

	/**
	 * the configurator
	 */
	private static I18nConfig theConfig = null;

	/**
	 * mesage bundle implementation class name and parameters
	 */
	private String messageBundleClzName = null;
	private Map<String, ?> messageBundleClzParams = null;

	/**
	 * application locale factory class name
	 */
	private String localeFactoryClzName = null;
	
	/**
	 * the application locale factory init parameters
	 */
	private Map<String, ?> localeFactoryClzParams = null;
	
	/**
	 * the system-used format locale code
	 */
	private String sysFormatLocaleCode = null;

	/**
	 * the format builder factory class name
	 */
	private String formatBuilderFactoryClzName = null;
	
	/**
	 * the format builder class names. 
	 * key=category (date/number), value=builder class name
	 */
	private Map<String, ?> formatBuilderClzNames = null;
	
	/**
	 * the format pattern parameters
	 */
	private Map<String, ?> formatPatternParams = null;
	
	/**
	 * get the cache configurator
	 */
	public static I18nConfig getI18nConfig() {
		if (theConfig == null) {
			synchronized(I18nConfig.class) {
				if (theConfig == null) {
					theConfig = (I18nConfig) getInstance("I18nConfig");
					if (theConfig == null) logger.warn("Configurator named I18nConfig is not found. Please check configuration file.");
				}
			}
		}
		return (theConfig);
	}

	/**
	 * @see com.pyrube.one.app.config.Configurator#loadConfig(java.lang.String, org.w3c.dom.Node)
	 */
	@Override
	public void loadConfig(String cfgName, Node cfgNode) throws AppException {
		Element implElem = (Element) ConfigManager.getNode(cfgNode, "messageBundle");
		if (implElem != null) {
			messageBundleClzName = ConfigManager.getAttributeValue(implElem, "class");
			messageBundleClzParams = ConfigManager.getDeepParams(implElem);
		}

		// application supported locales
		obtainAppLocales(ConfigManager.getNode(cfgNode, "locales"));

		// locale-based supported formats
		obtainLocaleFormats(ConfigManager.getNode(cfgNode, "formats"));
	}
	
	/**
	 * obtain application supported locales
	 */
	private void obtainAppLocales(Node ctx) throws AppException {
		if (ctx == null) return;
		localeFactoryClzName = ConfigManager.getAttributeValue((Element)ctx, "factory");
		localeFactoryClzParams = ConfigManager.getDeepParams(ctx);
	}
	
	/**
	 * obtain locale-based formats
	 */
	private void obtainLocaleFormats(Node ctx) throws AppException {
		if (ctx == null) return;
		sysFormatLocaleCode = ConfigManager.getAttributeValue((Element)ctx, "system");
		Node buildersNode = ConfigManager.getNode(ctx, "builders");
		formatBuilderFactoryClzName = ConfigManager.getAttributeValue((Element)buildersNode, "factory");
		formatBuilderClzNames = ConfigManager.getDeepParams(buildersNode);
		Node patternsNode = ConfigManager.getNode(ctx, "patterns");
		formatPatternParams = ConfigManager.getDeepParams(patternsNode);
	}
	
	/**
	 * return resource bundle basename
	 * @return String
	 */
	public String getResourceBundleBase() {
		String basename = null;
		if (messageBundleClzParams != null) {
			basename = (String) messageBundleClzParams.get("base");
		}
		return(basename);
	}
	
	/**
	 * return message bundle implementation class name
	 * @return String
	 */
	public String getMessageBundleClzName() {
		return(messageBundleClzName);
	}
	
	/**
	 * return message bundle implementation class init parameters
	 * @return Map
	 */
	public Map<String, ?> getMessageBundleClzParams() {
		return(messageBundleClzParams);
	}
	
	/**
	 * return locale factory class name. default is AppLocaleFactory
	 * @return String
	 */
	public String getLocaleFactoryClzName() {
		return(localeFactoryClzName != null ? localeFactoryClzName : AppLocaleFactory.class.getName());
	}
	
	/**
	 * return locale factory parameter
	 * @return Map
	 */
	public Map<String, ?> getLocaleFactoryClzParams() {
		return(localeFactoryClzParams);
	}
	
	/**
	 * return system-used format locale code.
	 * @return String
	 */
	public String getSysFormatLocaleCode() {
		return(sysFormatLocaleCode);
	}
	
	/**
	 * return format builder factory class name. default is FormatBuilderFactory
	 * @return String
	 */
	public String getFormatBuilderFactoryClzName() {
		return(formatBuilderFactoryClzName != null ? formatBuilderFactoryClzName : FormatBuilderFactory.class.getName());
	}
	
	/**
	 * return format builder class names
	 * @return Map
	 */
	public Map<String, ?> getFormatBuilderClzNames() {
		return(formatBuilderClzNames);
	}
	
	/**
	 * return format patterns
	 * @return Map
	 */
	public Map<String, ?> getFormatPatternParams() {
		return(formatPatternParams);
	}
}
