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

package com.pyrube.one.app.cache;

import java.io.Serializable;
import java.util.Map;

import com.pyrube.one.app.logging.Logger;


/**
 * Direct application cache event publisher. 
 * <br>
 * A sample configuration: <br>
 * <pre>
 * 		<CacheConfig>
 * 			<notifier enabled="true">
 * 				<eventPublisher name="default">
 * 					<class>com.pyrube.one.app.cache.InappCacheEventPublisher</class>
 * 				</notifier>
 * 				...
 * 			</refreshNotification>
 * 			...
 * 		</CacheConfig>
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class InappCacheEventPublisher implements CacheEventPublisher {

	/**
	 * logger
	 */
	private static final Logger logger = Logger.getInstance(InappCacheEventPublisher.class.getName());
	
	/**
	 * constructor
	 */
	public InappCacheEventPublisher() {
		super();
	}

	/** 
	 * @see com.pyrube.one.app.cache.CacheEventPublisher#init(java.util.Map)
	 */
	public void init(Map<String, ?> params) {
	}

	/** 
	 * @see com.pyrube.one.app.cache.CacheEventPublisher#publishEvent(java.lang.String, java.io.Serializable)
	 */
	public void publishEvent(String eventName, Serializable cacheKey) {
		// get caches listening on this event
		String[] cacheNames = CacheConfig.getCacheConfig().cachesOn(eventName);
		if (cacheNames != null) {
			for (int i = 0; i < cacheNames.length; ++i) {
				// local application cache
				CacheManager mgr = CacheManagerHelper.getLocalAppManager();
				if (mgr != null) mgr.refreshNotify(cacheNames[i], cacheKey);
				if (logger.isDebugEnabled()) logger.debug("Cache (" + cacheNames[i] + ") has been refreshed.");
			}
		}
	}

}
