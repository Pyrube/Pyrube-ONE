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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.cache.CacheConfig.CacheInfo;
import com.pyrube.one.app.cache.CacheConfig.CacheManagerInfo;
import com.pyrube.one.app.logging.Logger;

/**
 * Cahce Manager Helper
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class CacheManagerHelper {
	
	/**
	 * logger
	 */
	private static final Logger logger = Logger.getInstance(CacheManagerHelper.class.getName());

	/**
	 * cache event scope: Local  - local server only
	 */
	public static final String EVENTSCOPE_LOCAL = "Local";
	
	/**
	 * cache event scope: Cluster  - cluster without event listener
	 */
	public static final String EVENTSCOPE_CLUSTER = "Cluster";
	
	/**
	 * cache event scope: Global  - cluster with event listener
	 */
	public static final String EVENTSCOPE_GLOBAL = "Global";
	
	/**
	 * local application cache manager
	 */
	private static CacheManager localAppManager = null;
	
	/**
	 * whether the refresh notification is enabled
	 */
	private static boolean notifierEnabled = false;
	
	/**
	 * the application cache event publisher
	 */
	private static CacheEventPublisher eventPublisher = null;
	
	/**
	 * whether the publisher runs asynch (in a different thread). default is false
	 */
	private static boolean isPublisherAsynch = false;
	
	/**
	 * the application cache event listener
	 */
	private static CacheEventListener eventListener = null;
	
	/**
	 * whether the listener runs asynch (in a different thread). default is false
	 */
	private static boolean isListenerAsynch = false;
	
	/**
	 * 
	 */
	private static boolean initialized = false;
	
	// static initialization 
	static {
		try {
			CacheConfig cfg = CacheConfig.getCacheConfig();
			notifierEnabled = cfg.isNotifierEnabled();
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	/**
	 * initialize
	 */
	private static void init() {
		if (initialized) return;
		synchronized(CacheManagerHelper.class) {
			if (initialized) return;
			try {
				CacheConfig cfg = CacheConfig.getCacheConfig();
			
				CacheManagerInfo mgrInfo = cfg.getLocalAppCacheManagerInfo();
				localAppManager = createCacheManager(mgrInfo);
				
				if (notifierEnabled) {
					isPublisherAsynch = cfg.isPublisherAsynch();
					if (cfg.getEventPublisherClass() == null) {
						eventPublisher = new InappCacheEventPublisher();
						eventPublisher.init(null);
					} else {
						try {
							eventPublisher = (CacheEventPublisher) (Class.forName(cfg.getEventPublisherClass()).newInstance());
							eventPublisher.init(cfg.getEventPublisherParams());
						} catch (Exception e1) {
							eventPublisher = null;
							throw e1;
						}
					}
					
					if (cfg.isListenerEnabled() && cfg.getEventListenerClass() != null) {
						try {
							isListenerAsynch = cfg.isListenerAsynch();
							eventListener = (CacheEventListener) Class.forName(cfg.getEventListenerClass()).newInstance();
							eventListener.init(cfg.getEventListenerParams());
						} catch (Exception e1) {
							eventListener = null;
							throw e1;
						}
					}
				}
			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				initialized = true;
			}
		}
	}

	/**
	 * get local application cache manager
	 * @return CacheManager
	 */
	public static CacheManager getLocalAppManager() {
		init();
		return(localAppManager);
	}
	
	/**
	 * whether refresh notification enabled
	 * @return boolean
	 */
	public static boolean isNotifierEnabled() {
		return(notifierEnabled);
	}
	
	/**
	 * constructor
	 */
	private CacheManagerHelper() {
	}
	
	/**
	 * create cache manager
	 * @param mgrInfo
	 * @return CacheManager
	 */
	private static CacheManager createCacheManager(CacheManagerInfo mgrInfo) {  
		if (mgrInfo == null) return(null);
		CacheManager mgr = null;
		if (CacheManagerInfo.TYPE_INAPP.equals(mgrInfo.getType())) {
			mgr = InappCacheManager.getInstance();
		}
		
		return(mgr);
	}
	
	/**
	 * preload configured cache objects. 
	 * For session cache manager implementation, it should be called 
	 * in the actual cache manager constructor as the last task.
	 * @param mgr the cache manager
	 */
	public static void preloadCaches(CacheManager mgr) {
		try {
			CacheConfig cfg = CacheConfig.getCacheConfig();
			if (cfg == null) {
				logger.warn("Cache configuration is not available");
			} else {
				Map<String, ?> objs = cfg.getCacheInfos(mgr.isSessionScope());
				if (objs != null && objs.size() > 0) {
					for (Iterator<String> it = objs.keySet().iterator(); it.hasNext(); ) {
						String cacheName = it.next();
						CacheInfo info = (CacheInfo) objs.get(cacheName);
						if (info.isPreloaded()) {
							if (!info.isMultiple()) {
								mgr.getObject(cacheName);
							} else {
								Serializable[] grpKeys = info.getPreloadKeys();
								if (grpKeys != null && grpKeys.length > 0) {
									for (int i = 0; i < grpKeys.length; ++i) 
										mgr.getObject(cacheName, grpKeys[i]);
								}
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			logger.error("preload cache error.", e);
		}
	}
	
	/**
	 * get the cacheable object. If it is not in the cache, then try to load it.
	 * @param mgr the cache manager
	 * @param cacheName is the cache name
	 * @return Cacheable . For Single type of cache it is the 
	 * cacheable containing the cached object. For Multiple type 
	 * of cache it is the cacheable containing its all objects.
	 * @throws AppException
	 */	
	public static Cacheable<?> getCacheable(CacheManager mgr, String cacheName) throws AppException {
		Map<String, Cacheable<?>> store = mgr.getCacheStore();
		if (store == null) return(null);
		Cacheable<?> cacheObj = store.get(cacheName);
		if (cacheObj == null) {
			CacheInfo cacheInfo = null;
			if (mgr.isSessionScope()) 
				cacheInfo = CacheConfig.getCacheConfig().getSessionCacheInfo(cacheName);
			else
				cacheInfo = CacheConfig.getCacheConfig().getApplicationCacheInfo(cacheName);

			if (cacheInfo != null) {
				try {
					// if it is Multiple type, use Cacheables
					Cacheable<?> obj = null;
					if (cacheInfo.isMultiple())
						obj = new Cacheables();
					else 
						obj = (Cacheable<?>) (Class.forName(cacheInfo.getClassName()).newInstance());
					obj.setId(cacheName);		// the id is the cache name
					obj.setMultiple(cacheInfo.isMultiple());
					obj.setObjectInfo(cacheInfo);
					synchronized(store) {
						cacheObj = store.get(cacheName);
						if (cacheObj == null) {
							store.put(cacheName, obj);
							cacheObj = obj;
						}
					}
				} catch (Exception e) {
					logger.error("error", e);
					throw new AppException("message.error.unknown-cache-exception", e);
				}
			}
		}
		return (cacheObj);
	}

	/**
	 * notify cache through event or cache. either event name or cache name must be provided. if both are provided then use event name.
	 * @param eventName event name
	 * @param cacheName cache name.
	 * @param groupKey the optional group key for group type of cache. It could be a single Serializable or Serializable[] or ArrayList of multiple keys.
	 * @param eventScope event scope: 
	 * 		EVENTSCOPE_LOCAL   ("Local") - local server only; 
	 * 		EVENTSCOPE_CLUSTER ("Cluster") - cluster without event listener; 
	 * 		EVENTSCOPE_GLOBAL  ("Global") - cluster with event listener
	 *   default is EVENTSCOPE_GLOBAL.
	 * @return String  error message. null if successful
	 */
	public static String notifyCache(String eventName, String cacheName, Serializable groupKey, String eventScope) {
		if (groupKey != null && !(groupKey instanceof CacheKeys)) {
			if (groupKey instanceof Serializable[]) {
				Serializable[] keys = (Serializable[]) groupKey;
				if (keys.length == 0) {
					groupKey = null;
				} else if (keys.length == 1) {
					groupKey = keys[0];
				} else {
					groupKey = new CacheKeys<>(keys);
				}
			} else if (groupKey instanceof ArrayList) {
				ArrayList<?> keys = (ArrayList<?>) groupKey;
				if (keys.size() == 0) {
					groupKey = null;
				} else if (keys.size() == 1) {
					groupKey = (Serializable) keys.get(0);
				} else {
					try {
						groupKey = new CacheKeys(keys);
					} catch (Exception e) {
						return("Item of the key list must be Serializable");
					}
				}
			}
		}
		if (eventName != null) {
			if (logger.isDebugEnabled()) 
				logger.debug("notifying event(" + eventName + (groupKey != null ? (", " + groupKey.toString()) : "") + ")" + 
						(eventScope != null ? " with scope " + eventScope : ""));
			if (EVENTSCOPE_LOCAL.equalsIgnoreCase(eventScope)) {
				InappCacheEventPublisher publisher = new InappCacheEventPublisher();
				publisher.publishEvent(eventName, groupKey);
			} else if (EVENTSCOPE_CLUSTER.equalsIgnoreCase(eventScope)) {
				CacheManager.applicationPublishEventInternal(eventName, groupKey);
			} else {
				CacheManager.applicationPublishEvent(eventName, groupKey);
			}
		} else if (cacheName != null) {
			// notify the cache only
			if (logger.isDebugEnabled()) 
				logger.debug("notifying cache(" + cacheName + (groupKey != null ? (", " + groupKey.toString()) : "") + ")");
			CacheManager mgr = CacheManagerHelper.getLocalAppManager();
			if (mgr != null) mgr.refreshNotify(cacheName, groupKey);
		} else {
			return("Either eventName or cacheName must be provided.");
		}
		
		return(null);
	}
	
	/**
	 * get the configured application cache event publisher
	 * @return CacheEventPublisher
	 */
	public static CacheEventPublisher getCacheEventPublisher() {
		init();
		return((eventPublisher != null && isPublisherAsynch) ? new PublisherAsynchWrapper(eventPublisher) : eventPublisher);
	}
	
	/**
	 * get the configured application cache event listener
	 * @return CacheEventListener
	 */
	public static CacheEventListener getCacheEventListener() {
		init();
		return((eventListener != null && isListenerAsynch) ? new ListenerAsynchWrapper(eventListener) : eventListener);
	}
	
	
	/**
	 * Cache Event Publisher Asynch Thread Wrapper
	 * Run a publisher in a new thread
	 */
	public static class PublisherAsynchWrapper implements CacheEventPublisher, Runnable {
		private CacheEventPublisher eventPublisher = null;
		private String eventName = null;
		private Serializable cacheKey = null;
		public PublisherAsynchWrapper(CacheEventPublisher eventPublisher) {
			this.eventPublisher = eventPublisher;
		}
		
		public void init(Map<String, ?> params) {
		}

		public void publishEvent(String eventName, Serializable cacheKey) {
			this.eventName = eventName;
			this.cacheKey = cacheKey;
			// start the thread
			(new Thread(this, "CacheEventPublisher")).start();
		}

		public void run() {
			if (eventPublisher != null) eventPublisher.publishEvent(eventName, cacheKey);
		}
	}
	
	/**
	 * Cache Event Listener Asynch Thread Wrapper
	 * Run a listener in a new thread
	 */
	public static class ListenerAsynchWrapper implements CacheEventListener, Runnable {
		private CacheEventListener eventListener = null;
		private String eventName = null;
		private Serializable cacheKey = null;
		public ListenerAsynchWrapper(CacheEventListener eventListener) {
			this.eventListener = eventListener;
		}
		
		public void init(Map<String, ?> params) {
		}

		public void publishEvent(String eventName, Serializable cacheKey) {
			this.eventName = eventName;
			this.cacheKey = cacheKey;
			// start the thread
			(new Thread(this, "CacheEventListener")).start();
		}

		public void run() {
			if (eventListener != null) eventListener.publishEvent(eventName, cacheKey);
		}
	}
}
