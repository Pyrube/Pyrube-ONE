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

import java.util.Map;

import com.pyrube.one.app.cache.CacheManager;
import com.pyrube.one.app.logging.Logger;

/**
 * Application cache pre-loader to load configured caches after application started.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class AppCachePreloader implements AppListener {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(AppCachePreloader.class.getName());
	
	/**
	 * constructor
	 */
	public AppCachePreloader() {
		super();
	}

	/**
	 * @see com.pyrube.one.app.AppListener#init(java.util.Map)
	 */
	@Override
	public void init(Map<String, ?> params) { }

	/**
	 * @see com.pyrube.one.app.AppListener#afterStarted()
	 */
	@Override
	public void afterStarted() {
		try {
			if (logger.isInfoEnabled()) logger.info("Loading pre-configured caches ...");
			// get the configured local application cache manager, 
			// then try to load a dummy cache to trigger the preloading-cache process.
			CacheManager cacheMgr = CacheManager.getAppCacheManager();
			if (cacheMgr != null) cacheMgr.getObject("dummyCache");
		} catch (AppException e) {
			logger.error("error: " + e.getMessage(), e);
		} catch (Throwable e) {
			logger.error("error", e);
		}
	}

	/**
	 * @see com.pyrube.one.app.AppListener#beforeShutdown()
	 */
	@Override
	public void beforeShutdown() { }

}
