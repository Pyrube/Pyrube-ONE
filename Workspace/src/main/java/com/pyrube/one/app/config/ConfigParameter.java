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

package com.pyrube.one.app.config;

import java.io.*;
import java.util.*;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

/**
 * Configuration Parameter substitution.
 * it replaces "${paramName}" with the value of parameter paramName.
 * it searches the parameter paramName in following order: <br>
 *  1. system property  <br>
 *  2. JNDI local name space lookup with jndi name  prefix + "/" + paramName  <br>
 *  3. in properties file defined by "configParameters.properties" system property. or
 *     in default file configParameters.properties which is reachable in classpath. <br>
 *  4. in defaultValues (properties).  <br>
 * if parameter paramName is not found in all above, then set it to empty string "". <br>
 * <br>
 * <br>
 * <b>Note</b>: don't use Logger in this implementation to logger information because Logger
 * uses this class during its initialization.
 * <br>
 * if you want to see debug information for this class, set system property configParametersDebug to true.
 * <br>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class ConfigParameter {

	/**
	 * param start/end flag
	 */
	private static final char PARAM_FLAG = '$';
	private static final String PARAM_START = PARAM_FLAG + "{";
	private static final String PARAM_END = "}";
	
	/**
	 * parameter JNDI lookup name prefix (i.e. string/fscs). The parameter JNDI name is  paramJndiPrefix + "/" + paramName
	 * It looks for this prefix setting in following order:
	 *    system property
	 *    in x.config-params.properties file
	 *    in default parameters
	 * If it is empty or null, then don't lookup the parameters from JNDI name space
	 * If default parameters contain this prefix with a value, and system property also has this prefix but with empty value, then
	 * the system property overrides the default setting and because its value is empty so don't lookup parameters from jndi.
	 */
	private static String paramJndiPrefix = null;
	private static final String PARAM_JNDI_PREFIX = "configParametersJndiPrefix";
	
	private static PrintStream outStream = System.out;

	/**
	 * default config parameters properties file
	 */
	public static final String CONFIG_PARAMETER_FILE = "x.config-params.properties";

	/**
	 * parameters in config parameters file
	 */
	private static Properties cfgParams = null;
	
	private static boolean isDebug = false;

	static {
		InputStream is = null;
		try {
			isDebug = Boolean.valueOf(System.getProperty("configParametersDebug")).booleanValue();

			String paramFile = System.getProperty(CONFIG_PARAMETER_FILE);
			if (paramFile != null) {
				// get parameters from file
				try {
					if (isDebug) dispMsg("DEBUG", "opening config parameters file " + paramFile);
					is = new FileInputStream(paramFile);
				} catch (Exception e) {
					dispMsg("ERROR", "config parameters file " + paramFile + " not found. " + e.toString());
					is = null;
				}
			} else {
				// get parameters from default resource file
				try {
					paramFile = CONFIG_PARAMETER_FILE;
					if (isDebug) dispMsg("DEBUG", "looking up config parameters file " + paramFile + " in classpath");
					ClassLoader clsLoader = ConfigParameter.class.getClassLoader();
					if (clsLoader == null) is = ClassLoader.getSystemResourceAsStream(paramFile);
					else is = clsLoader.getResourceAsStream(paramFile);
				} catch (Exception e) {
					dispMsg("ERROR", "config parameters resource " + paramFile + " not found. " + e.toString());
					is = null;
				}
			}
			if (is != null) {
				cfgParams = new Properties();
				cfgParams.load(is);
				if (isDebug) {
					dispMsg("DEBUG", "following config parameters are loaded:");
					for (Enumeration keys = cfgParams.keys(); keys.hasMoreElements();) {
						String key = (String) keys.nextElement();
						dispMsg("DEBUG", key + "=" + cfgParams.getProperty(key));
					}
				}
			} else {
				if (isDebug) dispMsg("DEBUG", "config parameters resource " + paramFile + " not found.");
			}
			
			// get the parameter jndi name prefix
			paramJndiPrefix = System.getProperty(PARAM_JNDI_PREFIX);
			if (paramJndiPrefix == null && cfgParams != null) paramJndiPrefix = cfgParams.getProperty(PARAM_JNDI_PREFIX);
		} catch (Throwable t) {
			dispMsg("ERROR", "loading config parameters error. " + t.toString());
		} finally {
			if (is != null) try { is.close(); } catch (Exception e) {}
		}
	}

	/**
	 * output message
	 */
	private static void dispMsg(String level, String msg) {
		outStream.println(level + " " + ConfigParameter.class.getName() + " - " + msg);
	}

	/**
	 * constructor
	 */
	private ConfigParameter() {}

	/**
	 * substitute a string with parameters' values.
	 * it replace "${paramName}" with the value of parameter paramName. the paramName must not include $, { or }.
	 * if parameter paramName is not found, then leave it without change.
	 * because "${" is used to identify parameter in the string, in order to include "${" as
	 * part of the string, use "$${" to escape. <br>
	 * example: <br>
	 *   parameter paramName=myValue  <br>
	 *   "a${paramName}b" -> "amyValueb"  <br>
	 *   "a${paramNameb"  -> "a${paramNameb"  <br>
	 *   "a$${paramName}b"  -> "a${paramName}b"  <br>
	 *   "a$paramName}b"  -> "a$paramName}b"  <br>
	 *   "a${paramNameb e${paramName}"  -> "a${paramNameb e${paramName}"  <br>
	 *   "a${}b" -> "a${}b" <br>
	 * @param str the original string
	 * @return a string with parameters replaced
	 */
	public static String substitute(String str) {
		return(substitute(str, null));
	}
	
	/**
	 * substitute a string with parameters' values.
	 * it replace "${paramName}" with the value of parameter paramName. the paramName must not include $, { or }.
	 * if parameter paramName is not found, then set param's value to empty string.
	 * because "${" is used to identify parameter in the string, in order to include "${" as
	 * part of the string, use "$${" to escape. <br>
	 * example: <br>
	 *   parameter paramName=myValue  <br>
	 *   "a${paramName}b" -> "amyValueb"  <br>
	 *   "a${paramNameb"  -> "a${paramNameb"  <br>
	 *   "a$${paramName}b"  -> "a${paramName}b"  <br>
	 *   "a$paramName}b"  -> "a$paramName}b"  <br>
	 *   "a${paramNameb e${paramName}"  -> "a${paramNameb e${paramName}"  <br>
	 *   "a${}b" -> "a${}b" <br>
	 * @param str the original string
	 * @param defaultValues the default values of all possible parameters. it could be null meaning no default value.
	 * @return a string with parameters replaced
	 */
	public static String substitute(String str, Properties defaultValues) {
		int iPos1 = -1;
		if (str == null || str.length() == 0 || (iPos1 = str.indexOf(PARAM_START)) < 0) return(str);
		while (iPos1 >= 0) {
			if (iPos1 > 0 && str.charAt(iPos1 - 1) == PARAM_FLAG) {
				// escape $${, then remove the first $
				str = str.substring(0, iPos1 - 1) + str.substring(iPos1);
				iPos1 = str.indexOf(PARAM_START, iPos1 + (PARAM_START.length() - 1));
			} else {
				// try to replace the parameter
				int iPos2 = str.indexOf(PARAM_END, iPos1 + PARAM_START.length());
				if (iPos2 > 0)  {
					// end is found
					String paramValue = getParameterValue(str.substring(iPos1 + PARAM_START.length(), iPos2), defaultValues);
					// replace
					str = str.substring(0, iPos1) + paramValue + str.substring(iPos2 + PARAM_END.length());
					iPos2 = iPos1 + paramValue.length();
					iPos1 = str.indexOf(PARAM_START, iPos2);
				} else {
					// no end, then exit
					iPos1 = -1;
				}
			}
		}
		return(str);
	}
	
	/**
	 * get parameter value. it searches the parameter in following order: <br>
	 * 1. System property <br>
	 * 2. JNDI local name space lookup <br>
	 * 3. property set in configParameters.properties file <br>
	 * 4. default values <br>
	 * if it is not found above, then return empty string "". <br>
	 * @param paramName the parameter name
	 * @param defaultValues the default values of all possible parameters. it could be null meaning no default value.
	 * @return the value or empty string "" if it is not found.
	 */
	private static String getParameterValue(String paramName, Properties defaultValues) {
		if (paramName == null || paramName.length() == 0) return("");
		String val = System.getProperty(paramName);
		if (val == null) {
			String jndiName = paramJndiPrefix;
			if (jndiName == null && defaultValues != null) jndiName = defaultValues.getProperty(PARAM_JNDI_PREFIX);
			if (jndiName != null && jndiName.length() > 0) {
				jndiName = jndiName + "/" + paramName;
				try {
					// lookup the string value binding in local jndi name space
					val = (String) (new InitialContext()).lookup(jndiName);
				} catch (NamingException e) {
					if (e instanceof NameNotFoundException) {
						if (isDebug) dispMsg("DEBUG", "jndi name " + jndiName + " not found");
					} else {
						dispMsg("ERROR", "jndi lookup error " + e.toString());
					}
				} catch (Throwable e) {
					dispMsg("ERROR", "jndi lookup error " + e.toString());
				}
			}
		}
		if (val == null && cfgParams != null) val = cfgParams.getProperty(paramName);
		if (val == null && defaultValues != null) val = defaultValues.getProperty(paramName);
		if (val == null) val = "";
		return(val);
	}

}
