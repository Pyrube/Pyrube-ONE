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
import java.util.HashMap;
import java.util.Map;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.logging.Logger;

/**
 * cache manager. <br>
 * Use following calls to get cached object from application caches. <br>
 * <pre>
 *   Object obj = CacheManager.applicationGet(cacheName);
 * or
 *   Object obj = CacheManager.applicationGet(cacheName, groupKey);
 * </pre>
 * 
 * Use following calls to get cached object from session and application caches. <br>
 * <pre>
 *   CacheManager mgr = new SessionCacheManager();
 *   // store the manager mgr into session
 *   ...
 *   // get session cache manager from session
 *   ...
 *   Object obj = mgr.get(cacheName);
 * or
 *   Object obj = mgr.get(cacheName, groupKey);
 * </pre>
 * 
 * Use following calls to publish refresh-event to application caches. <br>
 * <pre>
 *   CacheManager.applicationPublishEvent(eventName, groupKey);
 * </pre>
 * 
 * Use following calls to publish refresh-event to session and application caches. <br>
 * <pre>
 *   CacheManager mgr = null;
 *   // get session cache manager from session
 *   ...
 *   mgr.publishEvent(eventName, groupKey);
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public abstract class CacheManager implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(CacheManager.class.getName());

	/**
	 * whether it is session cache manager
	 */
	private boolean sessionScope = false;

	/**
	 * get local application cache manager
	 * @return CacheManager or null if there are no any local application cache objects configured.
	 */
	public static CacheManager getAppCacheManager() {
		return(CacheManagerHelper.getLocalAppManager());
	}
	
	/**
	 * get a named remote application cache manager
	 * @param managerName
	 * @return CacheManager or null if named manager is not found
	 */
	public static CacheManager getAppCacheManager(String managerName) { 
		HashMap mgrs = null; //CacheManagerHelper.getNamedRemoteAppManagers();
		return(mgrs == null ? null : (CacheManager) mgrs.get(managerName));
	}
	
	/**
	 * get session cache manager. <br>
	 * the actual session cache manager will implement it.
	 * @return CacheManager
	 */
	public CacheManager getSessionCacheManager() {
		return(null);
	}

	/**
	 * get the cache store for the cache manager
	 * @return Map
	 */
	protected abstract Map<String, Cacheable<?>> getCacheStore();
	
	/**
	 * get the cached object. <br>
	 * it first searches in Session cache, 
	 * if found, then returns it. otherwise it searches local application cache,
	 * if found, then returns it. otherwise it searches remote application caches in the order configured.
	 * @param cacheName is the chache name
	 * @return Serializable Object
	 * @throws AppException
	 */	
	public final Serializable get(String cacheName) throws AppException {
		Serializable obj = sessionGet(cacheName);
		if (obj == null) obj = applicationGet(cacheName);
		return(obj);
	}

	/**
	 * get the cached object in a group. <br>
	 * it first searches in Session cache, 
	 * if found, then returns it. otherwise it searches local application cache,
	 * if found, then returns it. otherwise it searches remote application caches in the order configured.
	 * @param cacheName is the chache name
	 * @param groupKey is the key in the group
	 * @return Serializable Object
	 * @throws AppException
	 */	
	public final Serializable get(String cacheName, Serializable groupKey) throws AppException {
		Serializable obj = sessionGet(cacheName, groupKey);
		if (obj == null) obj = applicationGet(cacheName, groupKey);
		return(obj);
	}

	/**
	 * get cached object in application caches.
	 * it first searches in local application cache,
	 * if found, then returns it. otherwise it searches remote application caches in the order configured.
	 * 
	 * @param cacheName
	 * @return Serializable Object the actual object for Single type of cache
	 * @throws AppException
	 */	
	public static Serializable applicationGet(String cacheName) throws AppException {
		CacheManager mgr = CacheManagerHelper.getLocalAppManager();
		Serializable obj = (mgr != null ? mgr.getObject(cacheName) : null);
		return(obj);
	}

	/**
	 * get cached object with given key in the group for Group type of cache in application caches.
	 * it first searches in local application cache,
	 * if found, then returns it. otherwise it searches remote application caches in the order configured.
	 * @param cacheName
	 * @param groupKey
	 * @return Serializable Object
	 * @throws AppException
	 */	
	public static Serializable applicationGet(String cacheName, Serializable groupKey) throws AppException {
		CacheManager mgr = CacheManagerHelper.getLocalAppManager();
		Serializable obj = (mgr != null ? mgr.getObject(cacheName, groupKey) : null);
		return(obj);
	}
	
	/**
	 * get cached object in session cache if this cache manager is session cache manager.
	 * @param cacheName
	 * @return Serializable Object the actual object for Single type of cache. or Map containing all objects in the grojup for Group type of cache
	 * @throws AppException
	 */	
	public final Serializable sessionGet(String cacheName) throws AppException {
		CacheManager mgr = getSessionCacheManager();
		return(mgr != null ? mgr.getObject(cacheName) : null);
	}
	
	/**
	 * get cached object with given key in the group for Group type of cache in session cache if this cache manager is session cache manager.
	 * @param cacheName
	 * @param groupKey
	 * @return Serializable Object
	 * @throws AppException
	 */	
	public final Serializable sessionGet(String cacheName, Serializable groupKey) throws AppException {
		CacheManager mgr = getSessionCacheManager();
		return(mgr != null ? mgr.getObject(cacheName, groupKey) : null);
	}

	/**
	 * get cached object in this cache manager for Single type of cache.
	 * If the cacheName is Group type of cahce, then returns null.
	 * @param cacheName
	 * @return Serializable
	 * @throws AppException
	 */
	public abstract Serializable getObject(String cacheName) throws AppException;
	
	/**
	 * get cached object in a group in this cache manager for Group type of cache.
	 * If the cacheName is a Single type of cahce, then returns null.
	 * @param cacheName
	 * @param groupKey
	 * @return Serializable
	 * @throws AppException
	 */
	public abstract Serializable getObject(String cacheName, Serializable groupKey) throws AppException;
	
	/**
	 * publish a refresh notification event to application caches. <br>
	 * It will notify all related (subscribed) caches using configured event publisher.
	 * @param eventName the event name
	 * @param groupKey is optional. For Group type of cache, it will be used to 
	 * identify the item in the group. If it is null then refresh whole group. If it is an 
	 * instance of GroupCacheKeys then refresh the items whose keys are in the object.
	 */
	public static void applicationPublishEvent(String eventName, Serializable groupKey) {
		if (!isNotifierEnabled()) return;
		CacheEventPublisher eventPublisher = CacheManagerHelper.getCacheEventPublisher();
		if (eventPublisher != null) {
			eventPublisher.publishEvent(eventName, groupKey);
			if (logger.isDebugEnabled()) 
				logger.debug("application published event (" + eventName + (groupKey != null ? (", " + groupKey.toString()) : "") + ")");
		}
		CacheEventListener eventListener = CacheManagerHelper.getCacheEventListener();
		if (eventListener != null) {
			eventListener.publishEvent(eventName, groupKey);
			if (logger.isDebugEnabled()) 
				logger.debug("application notified listener for event (" + eventName + (groupKey != null ? (", " + groupKey.toString()) : "") + ")");
		}
	}
	
	/**
	 * publish a refresh notification event to application caches without notifying listener. <br>
	 * It will notify all related (subscribed) caches using configured event publisher.
	 * @param eventName the event name
	 * @param groupKey is optional. For Group type of cache, it will be used to 
	 * identify the item in the group. If it is null then refresh whole group. If it is an 
	 * instance of GroupCacheKeys then refresh the items whose keys are in the object.
	 */
	public static void applicationPublishEventInternal(String eventName, Serializable groupKey) {
		if (!isNotifierEnabled()) return;
		CacheEventPublisher eventPublisher = CacheManagerHelper.getCacheEventPublisher();
		if (eventPublisher != null) {
			eventPublisher.publishEvent(eventName, groupKey);
			if (logger.isDebugEnabled()) 
				logger.debug("application published event (" + eventName + (groupKey != null ? (", " + groupKey.toString()) : "") + ")");
		}
	}
	
	/**
	 * publish a refresh notification event to session and application caches. <br>
	 * It will notify all related (subscribed) caches.
	 * It will notify session caches directly, but notify application caches using configured event publisher.
	 * @param eventName the event name
	 * @param groupKey is optional. For Group type of cache, it will be used to 
	 * identify the item in the group. If it is null then refresh whole group. If it is an 
	 * instance of GroupCacheKeys then refresh the items whose keys are in the object.
	 */
	public final void publishEvent(String eventName, Serializable groupKey) {
		if (!isNotifierEnabled()) return;
		
		// session cache manager
		CacheManager mgr = getSessionCacheManager();
		if (mgr != null) {
			// get caches listening on this event
			String[] cacheNames = CacheConfig.getCacheConfig().cachesOn(eventName);
			if (cacheNames != null) {
				for (int i = 0; i < cacheNames.length; ++i) {
					mgr.refreshNotify(cacheNames[i], groupKey);
				}
			}
			if (logger.isDebugEnabled()) 
				logger.debug("session published event (" + eventName + (groupKey != null ? (", " + groupKey.toString()) : "") + ")");
		}
		// application caches
		applicationPublishEvent(eventName, groupKey);
	}
	
	/**
	 * notify the cache to refresh in this cache manager. If cacheName is Group type of cahce, then groupKey can 
	 * be provided to identify the item in the group. If it is Group type of cache and groupKey is 
	 * null, then refresh whole group. If it is Group type of cache and groupKey is instance of 
	 * GroupCacheKeys then refresh the items whose keys are in the object.
	 * @param cacheName
	 * @param groupKey is the key in the group for Group type of cache. If it is null then refresh whole group. If it is an 
	 * instance of GroupCacheKeys then refresh the items whose keys are in the object.
	 */
	public abstract void refreshNotify(String cacheName, Serializable groupKey);

	/**
	 * set session scope
	 * @param sessionScope
	 */
	public final void setSessionScope(boolean sessionScope) {
		this.sessionScope = sessionScope;
	}
	
	/**
	 * is it a session cache manager?
	 * @return boolean
	 */
	public final boolean isSessionScope() {
		return(sessionScope);
	}
	
	/**
	 * whether refresh notification enabled
	 * @return boolean
	 */
	public static boolean isNotifierEnabled() {
		return(CacheManagerHelper.isNotifierEnabled());
	}
}
