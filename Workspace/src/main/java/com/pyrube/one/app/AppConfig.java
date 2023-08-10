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

package com.pyrube.one.app;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyrube.one.app.config.ConfigManager;
import com.pyrube.one.app.config.Configurator;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.app.security.AppPolicy;

/**
 * Application configurator <br>
 * <pre>
 * 		<AppConfig>
 * 			<!-- application information -->
 * 			<application name="ONE" env="${DEPLOYMENT_ENV}" desc="ONE Demo">
 * 				<properties>
 * 					<param name="p1">p1value</param>
 * 					<param name="p2">
 * 						<param name="p21">p21value</param>
 * 						<param name="p22">p22value</param>
 * 					</param>
 * 					<param name="p3">p3value1</param>
 * 					<param name="p3">p3value2</param>
 * 				</properties>
 * 				<!-- application life cycle listeners implementing AppListner -->
 * 				<appListeners>
 * 					<listener>
 * 						<class>com.pyrube.one.app.AppCachePreloader</class>
 * 					</listener>
 * 				</appListeners>
 * 			</application>
 * 			<!-- application security -->
 * 			<security>
 * 				<manager class="com.pyrube.one.app.security.VirtualSecurityManager">
 * 				</manager>
 * 				<!-- password policy -->
 * 				<passwordPolicy enabled="true">
 * 					<minimumLength>1</minimumLength>
 * 					<maximumLength>16</maximumLength>
 * 					<specialChars><![CDATA[~`!@#$%^&*()-_=+{[]}\|;:'",<>.]]></specialChars>
 * 					<expiryAge>3</expiryAge> <!-- months -->
 * 					<maximumAttempts>3</maximumAttempts>
 * 					<lockingPeriod>15</lockingPeriod> <!-- minutes -->
 * 				</passwordPolicy>
 * 			</security>
 * 		</AppConfig>
 * </pre>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class AppConfig extends Configurator {
	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(AppConfig.class.getName());

	/**
	 * the configurator
	 */
	private static AppConfig appConfig = null;

	/**
	 * application name. 
	 */
	private String appName = null;

	/**
	 * application description
	 */
	private String appDesc = null;
	/**
	 * application deployment environment: DEV, SIT, UAT, PROD, etc.
	 */
	private String appEnv = null;

	/**
	 * application properties. key=propName, value=String or Map
	 */
	private Map<String, ?> appProperties = null;

	/**
	 * application life cycle listeners 
	 */
	private ListenerInfo[] appListeners = null;

	/**
	 * full java class name of the security manager
	 */
	private String securityMgrClassName = null;

	/**
	 * optional parameters of the security manager
	 */
	private Map<String, ?> securityMgrParams = null;

	/**
	 * password policy
	 */
	private AppPolicy passwordPolicy = null;
	
	/**
	 * get the application configurator
	 * @return AppConfig
	 */
	public static AppConfig getAppConfig() {
		if (appConfig == null) {
			synchronized(AppConfig.class) {
				if (appConfig == null) {
					AppConfig tmpCfg = (AppConfig) getInstance("AppConfig");
					if (tmpCfg == null) logger.error("Configurator named AppConfig is not found. Please check configuration file.");
					appConfig = tmpCfg;
				}
			}
		}
		return (appConfig);
	}

	/**
	 * constructor
	 */
	public AppConfig() {
	}

	/**
	 * @see com.pyrube.one.app.config.Configurator#loadConfig(java.lang.String, org.w3c.dom.Node)
	 */
	public void loadConfig(String cfgName, Node cfgNode) throws AppException {
		// application info
		obtainApplicationInfo(ConfigManager.getNode(cfgNode, "application"));
		// application security related
		obtainApplicationSecurity(ConfigManager.getNode(cfgNode, "security"));
	}

	/**
	 * obtain application information
	 * @param ctx
	 * @throws AppException
	 */
	private void obtainApplicationInfo(Node ctx) throws AppException {
		Element appNode = (Element) ctx;
		appName = ConfigManager.getAttributeValue(appNode, "name");
		appDesc = ConfigManager.getAttributeValue(appNode, "desc");
		appEnv = ConfigManager.getAttributeValue(appNode, "env");
		appProperties = ConfigManager.getDeepParams(ConfigManager.getNode(appNode, "properties"));
		appListeners = obtainListeners(ConfigManager.getNode(ctx, "appListeners"));
	}
	
	/**
	 * obtain listeners info under ctx
	 * @param ctx
	 * @return 
	 * @throws AppException
	 */
	private ListenerInfo[] obtainListeners(Node ctx) throws AppException {
		ListenerInfo[] lsrs = null;
		NodeList nl = ConfigManager.getNodeList(ctx, "listener");
		if (nl != null && nl.getLength() > 0) {
			ArrayList<ListenerInfo> list = new ArrayList<ListenerInfo>();
			for (int i = 0; i < nl.getLength(); ++i) {
				String clsName = ConfigManager.getSingleValue(nl.item(i), "class");
				if (clsName != null && clsName.length() > 0) {
					Map<?, ?> params = ConfigManager.getParams(nl.item(i));
					list.add(new ListenerInfo(clsName, (Map<String, ?>) params));
				}
			}
			if (list.size() > 0) {
				lsrs = new ListenerInfo[list.size()];
				list.toArray(lsrs);
			}
		}
		return(lsrs);
	}


	/**
	 * obtain application security
	 * @param ctx
	 * @throws AppException
	 */
	private void obtainApplicationSecurity(Node ctx) throws AppException {
		if (ctx == null) throw new AppException("message.error.security.not-configured");
		Element mgrElm = (Element) ConfigManager.getNode(ctx, "manager");
		if (mgrElm == null) throw new AppException("message.error.security.manager-not-configured");
		securityMgrClassName = ConfigManager.getAttributeValue(mgrElm, "class");
		securityMgrParams = ConfigManager.getDeepParams(mgrElm);
		Element policyElm = (Element) ConfigManager.getNode(ctx, "passwordPolicy");
		if (policyElm != null) {
			passwordPolicy = new AppPolicy();
			passwordPolicy.setEnabled(Boolean.valueOf(ConfigManager.getAttributeValue(policyElm, "enabled")).booleanValue());
			passwordPolicy.setMinimumLength(Integer.parseInt(ConfigManager.getSingleValue(policyElm, "minimumLength")));
			passwordPolicy.setMaximumLength(Integer.parseInt(ConfigManager.getSingleValue(policyElm, "maximumLength")));
			passwordPolicy.setSpecialChars(ConfigManager.getSingleValue(policyElm, "specialChars").toCharArray());
			passwordPolicy.setExpiryAge(Integer.parseInt(ConfigManager.getSingleValue(policyElm, "expiryAge")));
			passwordPolicy.setMaximumAttempts(Integer.parseInt(ConfigManager.getSingleValue(policyElm, "maximumAttempts")));
			passwordPolicy.setLockingPeriod(Integer.parseInt(ConfigManager.getSingleValue(policyElm, "lockingPeriod")));
			
		}
	}
	
	/**
	 * get application name
	 * 
	 * @return String application code
	 */
	public String getAppName() {
		return(appName);
	}

	/**
	 * get application description
	 * @return String
	 */
	public String getAppDesc() {
		return(appDesc);
	}
	/**
	 * get application deployment environment
	 * @return String
	 */
	public String getAppEnv() {
		return(appEnv);
	}
	
	/**
	 * get application properties
	 * @return Map
	 */	
	public Map<String, ?> getAppProperties() {
		return(appProperties);
	}

	/**
	 * get the given application property
	 * @param propName the property name. it could be a slash (/) separated path if it is in a hierarchy. i.e. commProps/host
	 * @return Object the value of the property. it could be String, List or Map.
	 */
	public Object getAppProperty(String propName) {
		if (appProperties == null || propName == null) return(null);
		Object obj = null;
		if (propName.indexOf("/") < 0) {
			obj = appProperties.get(propName);
		} else {
			StringTokenizer tok = new StringTokenizer(propName, "/");
			obj = appProperties;
			String pName = null;
			while (tok.hasMoreTokens()) {
				pName = tok.nextToken();
				if (pName.length() > 0) {
					if (obj instanceof Map<?, ?>) {
						obj = ((Map<String, ?>) obj).get(pName);
					} else {
						// invalid property path
						return(null);
					}
				}
			}
		}
		return(obj);
	}
	
	/**
	 * get value of a configuration parameter. It searches in following order.
	 * 1. System property 
	 * 2. JNDI local name space lookup 
	 * 3. property set in configParameters.properties file 
	 * 4. default values 
	 * if it is not found above, then return empty string "". 
	 * 
	 * Based on this method, you can add a new parameter in JVM or configParameters.properties file, then 
	 * directly access it using this method without code change.
	 * 
	 * @param paramName the parameter name
	 * @return String value of the parameter. if it is not found, returns empty string ""
	 */
	public String getAppConfigParameter(String paramName) {
		return(paramName != null ? ConfigManager.filterValue("${" + paramName + "}") : "");
	}
	
	/**
	 * get application life cycle listeners
	 * @return ListenerInfo[]
	 */
	public ListenerInfo[] getAppListeners() {
		return(appListeners);
	}
	
	/**
	 * get security manager class name
	 * @return the security manager class name
	 */
	public String getSecurityManagerClassName() {
		return(securityMgrClassName);
	}

	/**
	 * get security manager parameters
	 * @return Map the parameters
	 */
	public Map<String, ?> getSecurityManagerParams() {
		return(securityMgrParams);
	}

	/**
	 * returns application password policy
	 * @return AppPolicy
	 */
	public AppPolicy getPasswordPolicy() {
		return(passwordPolicy);
	}
	
	/**
	 * Application and session listener configuration info
	 * 
	 * @author Aranjuez
	 * @version Dec 01, 2009
	 * @since Pyrube-ONE 1.0
	 */
	public class ListenerInfo {

		/**
		 * full java class name
		 */
		private String className = null;
		
		/**
		 * optional parameters
		 */
		private Map<String, ?> params = null;
		
		/**
		 * Constructor
		 * 
		 */
		public ListenerInfo(String className) {
			this.className = className;
		}

		/**
		 * Constructor
		 * 
		 */
		public ListenerInfo(String className, Map<String, ?> params) {
			this.className = className;
			this.params = params;
		}
		
		/**
		 * @return
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * @return
		 */
		public Map<String, ?> getParams() {
			return params;
		}

		/**
		 * @param string
		 */
		public void setClassName(String string) {
			className = string;
		}

		/**
		 * @param params
		 */
		public void setParams(Map<String, ?> params) {
			this.params = params;
		}

	}

}
