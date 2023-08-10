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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.pyrube.one.app.Apps;
import com.pyrube.one.app.i18n.I18nConfig;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * Application-supported locale manager. 
 * <pre>
 * The implementation could be customized such as on site level.
 * The application locales could be configured in config file, or from database.
 * 
 * It manages the locales application uses including the application default locale. 
 * In the application code should use following method to get the default locale : 
 *   Locale defLocale = AppLocaleManager.getDefaultLocale();
 * Don't use Locale.getDefault(), because it is always the system default locale. 
 * The application default locale could be different from the system default locale.
 * 
 * </pre>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class AppLocaleManager {
	/**
	 * locale code can be obtained by locale.toString(), following is 
	 * a list of avialable locales in JVM. <br>
	 * 
	ar     Arabic
	ar_AE     Arabic (United Arab Emirat
	ar_BH     Arabic (Bahrain)
	ar_DZ     Arabic (Algeria)
	ar_EG     Arabic (Egypt)
	ar_IQ     Arabic (Iraq)
	ar_JO     Arabic (Jordan)
	ar_KW     Arabic (Kuwait)
	ar_LB     Arabic (Lebanon)
	ar_LY     Arabic (Libya)
	ar_MA     Arabic (Morocco)
	ar_OM     Arabic (Oman)
	ar_QA     Arabic (Qatar)
	ar_SA     Arabic (Saudi Arabia)
	ar_SD     Arabic (Sudan)
	ar_SY     Arabic (Syria)
	ar_TN     Arabic (Tunisia)
	ar_YE     Arabic (Yemen)
	hi_IN     Hindi (India)
	iw     Hebrew
	iw_IL     Hebrew (Israel)
	ja     Japanese
	ja_JP     Japanese (Japan)
	ko     Korean
	ko_KR     Korean (South Korea)
	th     Thai
	th_TH     Thai (Thailand)
	th_TH_TH     Thai (Thailand,TH)
	zh     Chinese
	zh_CN     Chinese (China)
	zh_HK     Chinese (Hong Kong)
	zh_TW     Chinese (Taiwan)
	be     Byelorussian
	be_BY     Byelorussian (Belarus)
	bg     Bulgarian
	bg_BG     Bulgarian (Bulgaria)
	ca     Catalan
	ca_ES     Catalan (Spain)
	cs     Czech
	cs_CZ     Czech (Czech Republic)
	da     Danish
	da_DK     Danish (Denmark)
	de     German
	de_AT     German (Austria)
	de_CH     German (Switzerland)
	de_DE     German (Germany)
	de_LU     German (Luxembourg)
	el     Greek
	el_GR     Greek (Greece)
	en_AU     English (Australia)
	en_CA     English (Canada)
	en_GB     English (United Kingdom)
	en_IE     English (Ireland)
	en_IN     English (India)
	en_NZ     English (New Zealand)
	en_ZA     English (South Africa)
	es     Spanish
	es_AR     Spanish (Argentina)
	es_BO     Spanish (Bolivia)
	es_CL     Spanish (Chile)
	es_CO     Spanish (Colombia)
	es_CR     Spanish (Costa Rica)
	es_DO     Spanish (Dominican Republi
	es_EC     Spanish (Ecuador)
	es_ES     Spanish (Spain)
	es_GT     Spanish (Guatemala)
	es_HN     Spanish (Honduras)
	es_MX     Spanish (Mexico)
	es_NI     Spanish (Nicaragua)
	es_PA     Spanish (Panama)
	es_PE     Spanish (Peru)
	es_PR     Spanish (Puerto Rico)
	es_PY     Spanish (Paraguay)
	es_SV     Spanish (El Salvador)
	es_UY     Spanish (Uruguay)
	es_VE     Spanish (Venezuela)
	et     Estonian
	et_EE     Estonian (Estonia)
	fi     Finnish
	fi_FI     Finnish (Finland)
	fr     French
	fr_BE     French (Belgium)
	fr_CA     French (Canada)
	fr_CH     French (Switzerland)
	fr_FR     French (France)
	fr_LU     French (Luxembourg)
	hr     Croatian
	hr_HR     Croatian (Croatia)
	hu     Hungarian
	hu_HU     Hungarian (Hungary)
	is     Icelandic
	is_IS     Icelandic (Iceland)
	it     Italian
	it_CH     Italian (Switzerland)
	it_IT     Italian (Italy)
	lt     Lithuanian
	lt_LT     Lithuanian (Lithuania)
	lv     Latvian (Lettish)
	lv_LV     Latvian (Lettish) (Latvia)
	mk     Macedonian
	mk_MK     Macedonian (Macedonia)
	nl     Dutch
	nl_BE     Dutch (Belgium)
	nl_NL     Dutch (Netherlands)
	no     Norwegian
	no_NO     Norwegian (Norway)
	no_NO_NY     Norwegian (Norway,Nynor
	pl     Polish
	pl_PL     Polish (Poland)
	pt     Portuguese
	pt_BR     Portuguese (Brazil)
	pt_PT     Portuguese (Portugal)
	ro     Romanian
	ro_RO     Romanian (Romania)
	ru     Russian
	ru_RU     Russian (Russia)
	sh     Serbo-Croatian
	sh_YU     Serbo-Croatian (Yugoslavia
	sk     Slovak
	sk_SK     Slovak (Slovakia)
	sl     Slovenian
	sl_SI     Slovenian (Slovenia)
	sq     Albanian
	sq_AL     Albanian (Albania)
	sr     Serbian
	sr_YU     Serbian (Yugoslavia)
	sv     Swedish
	sv_SE     Swedish (Sweden)
	tr     Turkish
	tr_TR     Turkish (Turkey)
	uk     Ukrainian
	uk_UA     Ukrainian (Ukraine)
	en     English
	en_US     English (United States)
	*/

	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(AppLocaleManager.class.getName());
	
	/**
	 * application locale factory instance
	 */
	private static AppLocaleFactory localeFactory = null;

	/**
	 * key=localeCode, value=Locale <br>
	 * localeCode is lang_country <br>
	 * lang is lowercase two-letter ISO-639 code <br>
	 * country is uppercase two-letter ISO-3166 code <br>
	 * <br>
	 */
	private static Map<String, Locale> locales = new HashMap<String, Locale>();
	
	/**
	 * return Locale object for a given locale code. 
	 * localeCode is language_COUNTRY <br>
	 * language is lowercase two-letter ISO-639 code <br>
	 * COUNTRY is uppercase two-letter ISO-3166 code
	 * @param localeCode
	 * @return Locale the locale or null if it is not valid locale code
	 */
	public static Locale localeOf(String localeCode) {
		if (localeCode == null) return(getDefaultLocale());
		Locale locale = (Locale) locales.get(localeCode);
		if (locale == null) {
			synchronized(locales) {
				locale = (Locale) locales.get(localeCode);
				if (locale == null) {
					if (localeCode.length() >= 5) {
						locale = new Locale(localeCode.substring(0, 2), localeCode.substring(3,5));
					} else if (localeCode.length() >= 2) {
						locale = new Locale(localeCode.substring(0, 2), "");
					}
					if (locale != null) locales.put(localeCode, locale);
				}
			}
		}
		return(locale);
	}
	
	/**
	 * return Locale object for given language and country
	 * @param language lowercase two-letter ISO-639 code
	 * @param country uppercase two-letter ISO-3166 code
	 * @return Locale the locale or null if it is not valid locale code
	 */
	public static Locale localeOf(String language, String country) {
		if (Strings.isEmpty(language)) return(null);
		String localeCode = language;
		if (!Strings.isEmpty(country)) localeCode += "_" + country;
		return(localeOf(localeCode));
	}
	
	/**
	 * whether supports a given loale 
	 * @param localeCode
	 * @return
	 */
	public static boolean supports(String localeCode) {
		String[] localeCodes = getLocaleCodes();
		for (String temp : localeCodes) {
			if (temp.equals(localeCode)) return true;
		}
		return false;
	}
	
	/**
	 * return default <code>Locale</code>.
	 * The default locale is configured in <locales>, if it is not 
	 * configured, then it is the system default locale.
	 * @return Locale
	 */
	public static Locale getDefaultLocale() {
		if (localeFactory == null) initAppLocaleFactoy();
		return(localeFactory.getDefaultLocale());
	}
	
	/**
	 * return default locale code
	 * @return String
	 */
	public static String getDefaultLocaleCode() {
		if (localeFactory == null) initAppLocaleFactoy();
		return(localeFactory.getDefaultLocaleCode());
	}
	
	/**
	 * return application-supported locale codes
	 * @return String[]
	 */
	public static String[] getLocaleCodes() {
		if (localeFactory == null) initAppLocaleFactoy();
		return(localeFactory.getLocaleCodes());
	}
	
	/**
	 * initialize application locale factory
	 */
	private static synchronized void initAppLocaleFactoy() {
		if (localeFactory != null) return;
		String clzName = I18nConfig.getI18nConfig().getLocaleFactoryClzName();
		if (clzName != null) {
			try {
				AppLocaleFactory fac = (AppLocaleFactory) Class.forName(clzName).newInstance();
				fac.init(I18nConfig.getI18nConfig().getLocaleFactoryClzParams());
				localeFactory = fac;
			} catch (Throwable e) {
				logger.warn("Failed to initialize AppLocaleFactory (" + clzName + ").", e);
			}
		}
	}
}
