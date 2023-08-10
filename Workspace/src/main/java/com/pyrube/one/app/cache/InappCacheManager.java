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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.cache.CacheConfig.CacheInfo;
import com.pyrube.one.app.logging.Logger;


/**
 * In-app cache manager
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class InappCacheManager extends CacheManager {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(InappCacheManager.class.getName());

	/**
	 * application cached objects. 
	 * key=cache name, value=Cacheable object which is the cache 
	 * object or a map for Group type of cache
	 */
	private Map<String, Cacheable<?>> cacheObjectStore = null;
	
	/**
	 * the instance
	 */
	private static InappCacheManager instance = new InappCacheManager();
	
	/**
	 * get instance
	 * @return InappCacheManager
	 */
	public static InappCacheManager getInstance() {
		return(instance);
	}
	
	/**
	 * constructor
	 */
	private InappCacheManager() {
		this.setSessionScope(false);
		cacheObjectStore = (Map<String, Cacheable<?>>) Collections.synchronizedMap(new HashMap<String, Cacheable<?>>());
		// load preload application caches
		CacheManagerHelper.preloadCaches(this);
	}
	
	/**
	 * @see com.pyrube.one.app.cache.CacheManager#getCacheStore()
	 */
	protected Map<String, Cacheable<?>> getCacheStore() {
		return (cacheObjectStore);
	}

	/**
	 * @see com.pyrube.one.app.cache.CacheManager#getObject(java.lang.String)
	 */
	public Serializable getObject(String cacheName) throws AppException {
		Cacheable<?> cobj = CacheManagerHelper.getCacheable(this, cacheName);
		return(cobj != null && !cobj.isMultiple() ? cobj.getObject() : null);
	}

	/**
	 * @see com.pyrube.one.app.cache.CacheManager#getObject(java.lang.String, java.io.Serializable)
	 */
	public Serializable getObject(String cacheName, Serializable cacheKey) throws AppException {
		Cacheable<?> cobj = CacheManagerHelper.getCacheable(this, cacheName);
		return(cobj != null && cobj.isMultiple() ? cobj.getObject(cacheKey) : null);
	}

	/**
	 * @see com.pyrube.one.app.cache.CacheManager#refreshNotify(java.lang.String, java.io.Serializable)
	 */
	public void refreshNotify(String cacheName, Serializable cacheKey) {
		if (cacheObjectStore == null) return;
		try {
			CacheInfo cacheInfo = CacheConfig.getCacheConfig().getApplicationCacheInfo(cacheName);
			if (cacheInfo == null) return;
			Cacheable<?> cobj = null;
			if (cacheInfo.isLazyRefresh()) {
				// only refresh caches already loaded in the cacheObjectStore
				cobj = (Cacheable<?>) cacheObjectStore.get(cacheName);
			} else {
				// load it if it is not in the cache yet
				cobj = CacheManagerHelper.getCacheable(this, cacheName);
			}
			if (cobj != null) cobj.notifyCache(cacheKey);
		} catch (Throwable e) {
			logger.error("message.error.notification-refresh-failed", e);
		}
	}

}
