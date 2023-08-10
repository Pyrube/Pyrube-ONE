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

package com.pyrube.one.app.security;

import java.util.Map;
import java.util.StringTokenizer;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.app.AppConfig;

/**
 * Security manager factory
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class SecurityManagerFactory {

	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(SecurityManagerFactory.class.getName());

	/**
	 * default security manager
	 */
	private static final String DEFAULT_SECURITY_MANAGER = "com.pyrube.one.app.security.VirtualSecurityManager";

	/**
	 * the instance
	 */
	private static SecurityManager securityManager = null;

	static {
		try {
			AppConfig cfg = AppConfig.getAppConfig();
			String clsName = cfg.getSecurityManagerClassName();
			Map<String, ?> params = null;
			if (clsName != null) {
				params = cfg.getSecurityManagerParams();
				if (logger.isDebugEnabled()) logger.debug("security manager is " + clsName + ". parameters: " + (params == null ? null : params.toString()));
			} else {
				clsName = DEFAULT_SECURITY_MANAGER;
				if (logger.isDebugEnabled()) logger.debug("security manager not configured. the default security manager will be used " + DEFAULT_SECURITY_MANAGER);
			}
			
			securityManager = (SecurityManager) (Class.forName(clsName).newInstance());
			securityManager.init(params);
			if (logger.isDebugEnabled()) logger.debug("Security Manager is initialized.");
			
		} catch (AppException e) {
			logger.error("initializing security manager error", e);
		} catch (Exception e) {
			logger.error("initializing security manager error", e);
		}
	}
	
	/**
	 * constructor
	 */
	private SecurityManagerFactory() {}

	/**
	 * return the security manager instance
	 */
	public static SecurityManager getSecurityManager() throws AppException {
		return(securityManager);
	}

	/**
	 * get a security manager parameter value
	 * @param paramName the parameter name. If the parameter is in 
	 * a hierarchical structure, its name is a slash (/) separated 
	 * path such as "ssoHandler/ssoIdentifier".
	 * @return Object. it is a String if the parameter has one value, 
	 * or List if parameter has multiple values, or Map if parameter 
	 * has multiple sub-parameters.
	 */
	public static Object getManagerParameter(String paramName) {
		if (paramName == null) return(null);
		Object paramVal = null;
		AppConfig cfg = AppConfig.getAppConfig();
		Map<String, ?> params = cfg.getSecurityManagerParams();
		if (params != null) {
			StringTokenizer tok = new StringTokenizer(paramName, "/");
			while (tok.hasMoreTokens()) {
				String pName = tok.nextToken();
				
				// if param name path is invalid, then return null
				if (pName == null || pName.length() == 0) return(null);
				
				// find pName from params
				if (params == null) return(null);
				paramVal = params.get(pName);
				if (paramVal == null) return(null);
				
				if (paramVal instanceof Map) {
					// could have more level
					params = (Map) paramVal;
				} else {
					params = null;
				}
			}
		}
		return(paramVal);
	}
}
