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

import com.pyrube.one.app.AppConfig.ListenerInfo;
import com.pyrube.one.app.logging.Logger;

/**
 * Application listener manager in the life cycle
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class AppListenerManager {

	/**
	 * logger
	 */
	private static final Logger logger = Logger.getInstance(AppListenerManager.class.getName());
	
	/**
	 * whether it is initialized
	 */
	private static boolean initialized = false;
	
	/**
	 * the application listeners
	 */
	private static AppListener[] listeners = null;
	
	/**
	 * check the listeners
	 */
	private static void checkListeners() {
		if (!initialized) {
			synchronized(AppListenerManager.class) {
				if (!initialized) {
					ListenerInfo[] appListeners = AppConfig.getAppConfig().getAppListeners();
					if (appListeners != null) {
						ArrayList<AppListener> lsns = new ArrayList<AppListener>();
						for (int i = 0; i < appListeners.length; ++i) {
							try {
								AppListener lsn = (AppListener) Class.forName(appListeners[i].getClassName()).newInstance();
								lsn.init(appListeners[i].getParams());
								lsns.add(lsn);
							} catch (Exception e) {
								logger.warn("application listener " + appListeners[i].getClassName() + " will be ignored.", e);
							}
						}
						if (lsns.size() > 0) {
							listeners = new AppListener[lsns.size()];
							lsns.toArray(listeners);
						}
					}
					initialized = true;
				}
			}
		}
	}
	
	/**
	 * Constructor
	 */
	private AppListenerManager() {
		super();
	}

	/**
	 * call listeners right after the application started
	 */
	public static void afterStarted() {
		checkListeners();
		if (listeners != null) {
			for (int i = 0; i < listeners.length; ++i) {
				listeners[i].afterStarted();
			}
		}
	}

	/**
	 * call listeners right before the application is shut down.
	 */
	public static void beforeShutdown() {
		checkListeners();
		if (listeners != null) {
			for (int i = 0; i < listeners.length; ++i) {
				listeners[i].beforeShutdown();
			}
		}
	}

}
