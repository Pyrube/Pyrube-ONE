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
import java.util.Iterator;
import java.util.Map;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.cache.CacheConfig.CacheInfo;
import com.pyrube.one.app.logging.Logger;

/**
 * Cacheable abstract class. All cache object class must extends this class 
 * and implement one of the refresh method. <br>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public abstract class Cacheable<T extends Serializable> implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(Cacheable.class.getName());
	
	/**
	 * the cached object id. <br>
	 * for Single type of cache, the id is the cache name. <br>
	 * for Multiple type of cache, the id of the object in the cache is the key.
	 */
	protected Serializable id = null;
	
	/**
	 * the timestamp in milliseconds when it must be refreshed next time.
	 * Initial value must be 0
	 */
	private long nextRefreshTime = 0;
	
	/**
	 * the last timestamp in milliseconds when it was accessed
	 */
	private long lastAccessTime = 0;

	/**
	 * number of accesses
	 */
	private long numAccesses = 0;
	
	/**
	 * Notification flag indicating refreshing is required
	 */
	private boolean notified = false;
	
	/**
	 * indicate whether it is Multiple type of cache
	 */
	private boolean multiple = false;

	/**
	 * the actual object being cached. <br>
	 * for Single type of cache, it is the java object. <br>
	 * for Group type of cache, it is a Map which contains the objects in the group with the group object key as the key in Map. 
	 */
	protected T object = null;
	
	/**
	 * the cached object information
	 */
	protected CacheInfo objectInfo = null;
	
	/**
	 * refresh cached object. It will be called by cache manager.
	 * The subclass should use this method to load/refresh object 
	 * from the source and instantiate the field "object". 
	 * for Group type of cache, the key of the item in the group can 
	 * be obtained by getId().
	 * @exception AppSystemException if object can not be refreshed.
	 */
	protected abstract void refresh() throws AppException;
	
	/**
	 * check whether the cache needs refresh or not. Now there are two cases it needs refresh. <br>
	 * 1. it is notified from outside to refresh. <br>
	 * 2. the configured refresh interval is not negative and time runs out. <br>
	 * @return  boolean if the cahce needs refresh
	 */
	public final boolean needRefresh() {
		return (notified || (nextRefreshTime >=0 && System.currentTimeMillis() > nextRefreshTime));
	}

	/**
	 * get cache object id <br>
	 * for Single type of cache, the id is the cache name. <br>
	 * for Group type of cache, the id of the object in the group is the key.
	 * @return Serializable
	 */
	public final Serializable getId() {
		return(id);
	}
	
	/**
	 * get the nextRefreshTime.
	 * @return long
	 */
	public final long getNextRefreshTime() {
		return (nextRefreshTime);
	}
	
	/**
	 * get the last access time
	 * @return long the last access time in milliseconds
	 */
	public final long getLastAccessTime() {
		return(lastAccessTime);
	}
	
	/**
	 * get total number of accesses
	 * @return long
	 */
	public final long getNumAccesses() {
		return(numAccesses);
	}

	/**
	 * Returns the notified.
	 * @return boolean
	 */
	public final boolean isNotified() {
		return (notified);
	}

	/**
	 * Whether it is Multiple type of cache
	 * @return boolean 
	 */
	public final boolean isMultiple() {
		return(multiple);
	}
	
	/**
	 * get cached object configuration info
	 * @return CacheObjectInfo
	 */
	public final CacheInfo getObjectInfo() {
		return(objectInfo);
	}
	
	/**
	 * get the cached object.
	 * @return Serializable
	 * @throws AppException
	 */
	public synchronized final T getObject() throws AppException {
		if (needRefresh()) {
			try {
				// If it is Single type cache, then refresh the object. 
				// If it is Multiple type cache, then refresh whole group.
				refresh();	// the refresh() will reload object
				if (nextRefreshTime >= 0) {
					long refreshInterval = (isMultiple() ? (-1L) : objectInfo.getRefreshInterval());
					nextRefreshTime = (refreshInterval >= 0 ? (System.currentTimeMillis() + refreshInterval) : refreshInterval);
				}
				notified = false;
			} catch (AppException e) {
				notified = true;	// it needs refresh when it is hit next time.
				throw e;
			} catch (Throwable e) {
				notified = true;	// it needs refresh when it is hit next time.
				logger.error("error", e);
				throw new AppException("message.error.unknown-cache-exception", e);
			}
		}
		++numAccesses;
		lastAccessTime = System.currentTimeMillis();
		return (object);
	}

	/**
	 * get a Cacheable object in the cache set with a given key. 
	 * This method is only for Multiple type cache.
	 * @param cacheKey the key in the cache set.
	 * @return Cacheable. it returns null if current cache is not Multiple type.
	 * @exception AppException
	 */
	private final Cacheable<T> getCacheable(Serializable cacheKey) throws AppException {
		if (!isMultiple() || cacheKey == null) return(null);
		Map<Serializable, Cacheable<T>> cacheObj = (Map<Serializable, Cacheable<T>>) getObject();  // the Map is a Synchronized Map
		Cacheable<T> obj = (Cacheable<T>) cacheObj.get(cacheKey);
		if (obj == null) {
			// not found, then try to initialize it. each object in the cache set use the same cacheable class
			try {
				obj = (Cacheable<T>) (Class.forName(objectInfo.getClassName()).newInstance());
				obj.setId(cacheKey);  // the id is the cache key
				obj.setMultiple(false);  // the object in the cache set is Single type of cache
				obj.setObjectInfo(objectInfo);  // the object in the cache set has the same configuration info
				cacheObj.put(cacheKey, obj);
			} catch (Exception e) {
				logger.error("error", e);
				throw new AppException("message.error.unknown-cache-exception", e);
			}
		}
		return (obj);
	}
	
	/**
	 * get an object in the cache set with a given key. 
	 * This method is only for Multiple type cache.
	 * @param cacheKey the key in the cache set.
	 * @return Serializable. it returns null if current cache is not Multiple type.
	 * @exception AppException
	 */
	public final T getObject(Serializable cacheKey) throws AppException {
		Cacheable<T> cobj = getCacheable(cacheKey);
		return (cobj != null ? cobj.getObject() : null);
	}
	
	/**
	 * set cache object id
	 * for Single type of cache, the id is the cache name. <br>
	 * for Multiple type of cache, the id of the object in the cache set is the key.
	 * @param objectId
	 */
	public final void setId(Serializable objectId) {
		id = objectId;
	}
	
	/**
	 * set whether it is multiple
	 * @param multiple
	 */
	public final void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	/**
	 * set the object configuration info.
	 * @param objInfo
	 */
	public final void setObjectInfo(CacheInfo objInfo) {
		objectInfo = objInfo;
	}
	
	/**
	 * notify cache to refresh. If current cacheable is Multiple type, then 
	 * notify all existing items in the group. <br>
	 * If the cache is not lazy refresh, then refresh it now.
	 */
	public final void notifyCache() {
		if (this.multiple) {
			// notify all items in the group
			if (object != null) {
				Map<Serializable, Cacheable<T>> map = (Map<Serializable, Cacheable<T>>) object;
				Cacheable<T>[] itemValues = new Cacheable[map.size()];
				map.values().toArray(itemValues);
				for (int i = 0; i < itemValues.length; ++i) {
					itemValues[i].notifyCache();
				}
			}
		} else {
			notified = true;
			if (!objectInfo.isLazyRefresh()) {
				// it is not lazy refresh so refresh immediately
				try {
					getObject();
				} catch (Throwable e) {
					// it is already logged in getObject()
				}
			}
		}
	}
	
	/**
	 * notify an item of Multiple type of cache to refresh, or <br>
	 * If cacheKey is null, then refresh all items in the group, or <br>
	 * If cacheKey is instance of CacheKeys, then refresh the items in the keys list. <br>
	 * @param cacheKey the group item key or CacheKeys of group items' keys or null
	 */
	public final void notifyCache(Serializable cacheKey) {
		if (cacheKey == null || !this.multiple) {
			notifyCache();
		} else {
			try {
				Map<Serializable, Cacheable<T>> map = (Map<Serializable, Cacheable<T>>)object;
				Cacheable<T> cobj = null;
				if (cacheKey instanceof CacheKeys) {
					// multiple keys
					CacheKeys keys = (CacheKeys)cacheKey;
					if (keys.hasKeys()) {
						Serializable key = null;
						for (Iterator it = keys.iterator(); it.hasNext(); ) {
							key = (Serializable) it.next();
							if (objectInfo.isLazyRefresh()) {
								// lazy refresh, so don't initialize it if not loaded yet
								cobj = (Cacheable) map.get(key);
							} else {
								// not lazy refresh, so initialize it if not loaded yet
								cobj = getCacheable(key);
							}
							if (cobj != null) cobj.notifyCache();
						}
					}
				} else {
					// single key
					if (objectInfo.isLazyRefresh()) {
						// lazy refresh, so don't initialize it if not loaded yet
						cobj = (Cacheable) map.get(cacheKey);
					} else {
						// not lazy refresh, so initialize it if not loaded yet
						cobj = getCacheable(cacheKey);
					}
					if (cobj != null) cobj.notifyCache();
				}
			} catch (AppException e) {
				logger.error("error", e);
			}
		}
	}

	/**
	 * toString
	 * @return String
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Cache Name: ").append(objectInfo.getName());
		buf.append("\nObject Id: ").append(id.toString());
		buf.append("\nNext Refresh Time: ").append(nextRefreshTime);
		buf.append("\nLast Access Time: ").append(lastAccessTime);
		buf.append("\nNumber of Accesses: ").append(numAccesses);
		buf.append("\nIs LazyRefresh: ").append(objectInfo.isLazyRefresh());
		buf.append("\nIs Multiple: ").append(multiple);
		buf.append("\nIs Notified: ").append(notified);
		buf.append("\nObject: ").append(object.toString());
		return (buf.toString());
	}

}
