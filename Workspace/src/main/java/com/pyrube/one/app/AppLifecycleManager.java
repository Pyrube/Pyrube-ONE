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

import com.pyrube.one.app.logging.Logger;

/**
 * <pre>
 * Application life cycle manager. This will be called by a Singleton Startup bean (EJB3.1+) or a web app context listener.
 * 
 * Startup Singleton bean in each Ear: (change package wea to the service code such as two, thr, etc.)
 * 
 *     @Startup
 *     @Singleton
 *     @LocalBean
 *     public class com.pyrube.wea.services.AppConfigLoader {
 *     	 public AppConfigLoader() {}
 *     
 *       @PostConstruct
 *       public void init() { AppLifecycleManager.startup(); }
 *       
 *       @PreDestroy
 *       public void cleanup() { AppLifecycleManager.shutdown(); }
 *     }
 *  Then the Application server will automatically load and create the instance when application starts.
 * 
 * Web Application Context listener, see com.pyrube.wea.WeaConfigLoader
 *     public class com.pyrube.wea.WeaConfigLoader implements ServletContextListener {
 *       public WeaConfigLoader() {}
 *       public void contextDestroyed(ServletContextEvent event) {
 *         AppLifecycleManager.shutdown();
 *       }
 *       public void contextInitialized(ServletContextEvent event) {
 *         AppLifecycleManager.startup();
 *       }
 *     }
 *   add it in web.xml:
 *     <listener>
 *       <listener-class>com.pyrube.wea.WeaConfigLoader</listener-class>
 *     </listener>
 *   then when web module starts, it will be called.
 *   
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class AppLifecycleManager {

	/**
	 * the logger. This is also used to trigger loading the logger configuration
	 */
	private static Logger logger = Logger.getInstance(AppLifecycleManager.class.getName());
	
	/**
	 * constructor
	 */
	private AppLifecycleManager() {
	}

	/**
	 * application startup
	 */
	public static void startup() {
		if (logger.isInfoEnabled()) logger.info("Loading application configurations ...");
		// this triggers loading all configurations
		AppConfig cfg = AppConfig.getAppConfig();
		if (cfg == null) {
			logger.warn("Failed loading application configurations");
			return;
		}
		// notify application listeners
		AppListenerManager.afterStarted();
		if (logger.isInfoEnabled()) logger.info("Configurations are loaded for application " + cfg.getAppName());
	}
	
	/**
	 * application shutdown
	 */
	public static void shutdown() {
		AppConfig cfg = AppConfig.getAppConfig();
		if (logger.isInfoEnabled()) logger.info("Stopping application " + (cfg != null ? cfg.getAppName() : ""));
		// notify application listeners
		AppListenerManager.beforeShutdown();
	}
}
