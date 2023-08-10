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
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.config.ConfigManager;
import com.pyrube.one.app.config.Configurator;
import com.pyrube.one.app.logging.Logger;

/**
 * Cache management configurator
 * <pre>
 * 		<CacheConfig>
 * 			<notifier enabled="true">
 * 				<eventPublisher name="default" isAsynch="false">
 * 					<class>com.pyrube.one.app.cache.InappCacheEventPublisher</class>
 * 				</eventPublisher>
 * 				<eventListener enabled="true" isAsynch="false">
 * 					<class>com.pyrube.one.app.cache.DummyCacheEventListener</class>
 * 					<param name="param1">param1Value</param>
 * 				</eventListener>
 * 				<event name="sampleUpdated">
 * 					<cache name="sample"/>
 * 				</event>
 * 			</notifier>
 * 			<applicationCache>
 * 				<!-- local application cache manager type: direct, localEjb, remoteEjb. jndiName is the ejb cache manager home for types of localEjb and remoteEjb. -->
 * 				<local>
 * 					<!-- negative refreshInterval means no auto-refresh -->
 * 					<cache name="sample" class="com.pyrube.one.sample.cache.SampleCacheable" multiple="false" preloaded="false" refreshInterval="600"/>
 * 					<cache name="multiSamples" class="com.pyrube.one.sample.cache.MultiSamplesCacheable" multiple="true" preloaded="true" refreshInterval="3600">
 * 						<preloadKeys>
 * 							<key>key1</key>
 * 							<key>key2</key>
 * 						</preloadKeys>
 * 						<param name="param1">param1_value</param>
 * 					</cache>
 * 				</local>
 * 			</applicationCache>
 * 		</CacheConfig>
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class CacheConfig extends Configurator {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(CacheConfig.class.getName());

	/**
	 * the configurator
	 */
	private static CacheConfig cacheConfig = null;

	/**
	 * whether refresh notification is enabled
	 */
	private boolean notifierEnabled = false;
	
	/**
	 * notification events. <br>
	 * key=event name  <br>
	 * value=string array of cach names to be notified when the event is ocurred. <br>
	 */
	private Map<String, ?> notificationEvents = null;

	/**
	 * application cache event publisher name
	 */
	private String eventPublisherName = null;
	
	/**
	 * application cache event publisher class name
	 */
	private String eventPublisherClass = null;
	
	/**
	 * application cache event publisher init parameters
	 */
	private Map<String, ?> eventPublisherParams = null;

	/**
	 * whether the publisher runs asynch (in a different thread). default is false
	 */
	private boolean publisherAsynch = false;
	
	/**
	 * cache event listener class name
	 */
	private String eventListenerClass = null;
	
	/**
	 * application cache event listener init parameters
	 */
	private Map<String, ?> eventListenerParams = null;
	
	/**
	 * whether the listener runs asynch (in a different thread). default is false
	 */
	private boolean listenerAsynch = false;
	
	/**
	 * whether the listener is enabled
	 */
	private boolean listenerEnabled = false;
	
	/**
	 * local Application cache infos, (key=cacheName, value=CacheInfo) 
	 */
	private Map<String, ?> applicationCacheInfos = null;

	/**
	 * Session cache infos, (key=cacheName, value=CachetInfo)
	 */
	private Map<String, ?> sessionCacheInfos = null;
	
	/**
	 * local application cache manager info
	 */
	private CacheManagerInfo localAppCacheManagerInfo = null;
	
	/**
	 * return the cache configurator
	 */
	public static CacheConfig getCacheConfig() {
		if (cacheConfig == null) {
			synchronized(CacheConfig.class) {
				if (cacheConfig == null) {
					CacheConfig cfg = (CacheConfig) getInstance("CacheConfig");
					if (cfg == null) logger.warn("configurator named " + "CacheConfig" + " is not found. please check configuration file.");
					cacheConfig = cfg;
				}
			}
		}
		return (cacheConfig);
	}

	/**
	 * load cache configuration
	 * @param cfgName the config name
	 * @param cfgNode the configuration Node
	 * @see Configurator#loadConfig(String, Node)
	 */
	public void loadConfig(String cfgName, Node cfgNode) throws AppException {
		// notifier
		obtainNotifier(ConfigManager.getNode(cfgNode, "notifier"));
		
		// application cache
		obtainApplicationCache(ConfigManager.getNode(cfgNode, "applicationCache"));
		
		// session cache
		obtainSessionCache(ConfigManager.getNode(cfgNode, "sessionCache"));
	}

	/**
	 * obtain refresh notification configuration
	 * @param ctx
	 * @throws AppException
	 */
	private void obtainNotifier(Node ctx) throws AppException {
		notifierEnabled = Boolean.valueOf(ConfigManager.getAttributeValue((Element)ctx, "enabled")).booleanValue();
		Element pubElm = (Element) ConfigManager.getNode(ctx, "eventPublisher");
		if (pubElm != null) {
			eventPublisherName = ConfigManager.getAttributeValue(pubElm, "name");
			publisherAsynch = Boolean.valueOf(ConfigManager.getAttributeValue(pubElm, "isAsynch")).booleanValue();
			eventPublisherClass = ConfigManager.getSingleValue(pubElm, "class");
			eventPublisherParams = ConfigManager.getDeepParams(pubElm);
			if (eventPublisherClass != null && eventPublisherClass.trim().length() == 0) eventPublisherClass = null;
		}
		Element lsnElm = (Element) ConfigManager.getNode(ctx, "eventListener");
		if (lsnElm != null) {
			listenerEnabled = Boolean.valueOf(ConfigManager.getAttributeValue(lsnElm, "enabled")).booleanValue();
			listenerAsynch = Boolean.valueOf(ConfigManager.getAttributeValue(lsnElm, "isAsynch")).booleanValue();
			eventListenerClass = ConfigManager.getSingleValue(lsnElm, "class");
			eventListenerParams = ConfigManager.getDeepParams(lsnElm);
			if (eventListenerClass != null && eventListenerClass.trim().length() == 0) eventListenerClass = null;
			if (eventListenerClass == null) listenerEnabled = false;
		}
		
		NodeList nl = ConfigManager.getNodeList(ctx, "event");
		if (nl != null && nl.getLength() > 0) {
			Map<String, String[]> events = new HashMap<String, String[]>();
			for (int i = 0; i < nl.getLength(); ++i) {
				Element eventElm = (Element) nl.item(i);
				String eventName = ConfigManager.getAttributeValue(eventElm, "name");
				if (eventName != null && eventName.length() > 0) {
					List<?> cacheList = ConfigManager.getAttributeValues(eventElm, "cache", "name");
					if (cacheList != null && cacheList.size() > 0) {
						String[] caches = new String[cacheList.size()];
						cacheList.toArray(caches);
						events.put(eventName, caches);
					}
				}
			}
			if (events.size() > 0) notificationEvents = events;
		}
	}
	
	/**
	 * obtain application cache
	 * @param ctx
	 * @throws AppException
	 */
	private void obtainApplicationCache(Node ctx) throws AppException {
		Element localElm = (Element) ConfigManager.getNode(ctx, "local");
		applicationCacheInfos = obtainCacheInfos(localElm);
		String name = null;
		String type = null;
		boolean notifierEnabled = true;
		if (applicationCacheInfos != null) {
			name = "";
			type = ConfigManager.getAttributeValue(localElm, "type");
			localAppCacheManagerInfo = new CacheManagerInfo(name, type, notifierEnabled);
		}
	}
	
	/**
	 * obtain session cache
	 * @param ctx
	 * @throws AppException
	 */
	private void obtainSessionCache(Node ctx) throws AppException {
		sessionCacheInfos = obtainCacheInfos((Element) ctx);
	}
	
	/**
	 * obtain cache infos
	 * 
	 * @param ctx
	 * @return
	 * @throws AppException
	 */
	private Map<String, ?> obtainCacheInfos(Node ctx) throws AppException {
		try {
			NodeList nodes = ConfigManager.getNodeList(ctx, "cache");
			if (nodes == null || nodes.getLength() == 0) return (null);

			Map<String, CacheConfig.CacheInfo> caches = new HashMap<String, CacheConfig.CacheInfo>();
			for (int i = 0; i < nodes.getLength(); ++i) {
				CacheInfo info = new CacheInfo();
				Element node = (Element) nodes.item(i);
				info.setName(ConfigManager.getAttributeValue(node, "name"));
				info.setClassName(ConfigManager.getAttributeValue(node, "class"));
				info.setMultiple(Boolean.valueOf(ConfigManager.getAttributeValue(node, "multiple")).booleanValue());
				String tmp = ConfigManager.getAttributeValue(node, "lazyRefresh");
				if (tmp != null && tmp.trim().length() > 0) info.setLazyRefresh(Boolean.valueOf(tmp).booleanValue());
				info.setPreloaded(Boolean.valueOf(ConfigManager.getAttributeValue(node, "preloaded")).booleanValue());

				try{
					info.setRefreshInterval(1000 * Long.parseLong(ConfigManager.getAttributeValue(node, "refreshInterval")));
				} catch (NumberFormatException e) {
					info.setRefreshInterval(-1);
				}
				if (info.isMultiple()) {
					List<?> preloadedKeys = ConfigManager.getValues(node, "preloadKeys/key");
					if (preloadedKeys != null && preloadedKeys.size() > 0) {
						String[] keys = new String[preloadedKeys.size()];
						preloadedKeys.toArray(keys);
						info.setPreloadKeys(keys);
					}
				}
				Map params = ConfigManager.getParams(node);
				if (params != null && params.size() > 0) info.setParams(params);
				
				caches.put(info.getName(), info);
			}
			
			return (caches.size() > 0 ? caches : null); 
		} catch (Exception e) {
			logger.error("error", e);
			throw new AppException("message.error.unknown-config-exception", e);
		}
    }

	/**
	 * get all cache infos for a given scope
	 * @param isSessionScope
	 * @return Map
	 */
	public Map<String, ?> getCacheInfos(boolean isSessionScope) {
		return(isSessionScope ? sessionCacheInfos : applicationCacheInfos);
	}
	
	/**
	 * get a cache information in a given scope
	 * @param cacheName
	 * @param isSessionScope
	 * @return CachetInfo
	 */
	public CacheInfo getCacheInfo(String cacheName, boolean isSessionScope) {
		if (isSessionScope)
			return(sessionCacheInfos != null ? (CacheInfo) sessionCacheInfos.get(cacheName) : null);
		else
			return(applicationCacheInfos != null ? (CacheInfo)applicationCacheInfos.get(cacheName) : null);
	}
	
	/**
	 * get all application cache infos
	 * @return map of application cache infos
	 */
	public Map<String, ?> getApplicationCacheInfos() {
		return applicationCacheInfos;
	}

	/**
	 * get an application cache information
	 * @param cacheName
	 * @return CacheInfo
	 */
	public CacheInfo getApplicationCacheInfo(String cacheName) {
		return(applicationCacheInfos != null ? (CacheInfo)applicationCacheInfos.get(cacheName) : null);
	}
	
	/**
	 * get all session cache infos
	 * @return map of session cache infos
	 */
	public Map<String, ?> getSessionCacheInfos() {
		return sessionCacheInfos;
	}

	/**
	 * get a session cache information
	 * @param cacheName
	 * @return CacheInfo
	 */
	public CacheInfo getSessionCacheInfo(String cacheName) {
		return(sessionCacheInfos != null ? (CacheInfo) sessionCacheInfos.get(cacheName) : null);
	}
	
	/**
	 * check whether refresh notification is enabled
	 * @return boolean
	 */
	public boolean isNotifierEnabled() {
		return notifierEnabled;
	}

	/**
	 * get application cache event publisher name
	 * @return String
	 */
	public String getEventPublisherName() {
		return(eventPublisherName);
	}

	/**
	 * get application cache event publisher class name
	 * @return String
	 */
	public String getEventPublisherClass() {
		return(eventPublisherClass);
	}
	
	/**
	 * get application cache event publisher init parameters
	 * @return Map
	 */
	public Map<String, ?> getEventPublisherParams() {
		return(eventPublisherParams);
	}

	/**
	 * whether the publisher runs asynch (in new thread)
	 * @return boolean
	 */
	public boolean isPublisherAsynch() {
		return(publisherAsynch);
	}
	
	/**
	 * get cache event listener class name
	 * @return String
	 */
	public String getEventListenerClass() {
		return(eventListenerClass);
	}
	
	/**
	 * get cache event listener init parameters
	 * @return Map
	 */
	public Map<String, ?> getEventListenerParams() {
		return(eventListenerParams);
	}
	
	/**
	 * whether the cache event listener enabled
	 * @return boolean
	 */
	public boolean isListenerEnabled() {
		return(listenerEnabled);
	}
	
	/**
	 * whether the listener runs asynch (in new thread)
	 * @return boolean
	 */
	public boolean isListenerAsynch() {
		return(listenerAsynch);
	}
	
	/**
	 * get notification events. 
	 * @return map of notification events. key=event name, value=array of cache names to be notified
	 */
	public Map<String, ?> getNotificationEvents() {
		return notificationEvents;
	}

	/**
	 * return list of caches to be notified for a given event
	 * @param eventName the event
	 * @return String array of cache names. null if there are no caches to be notified for the event.
	 */
	public String[] cachesOn(String eventName) {
		if (notificationEvents == null) return(null);
		return((String[]) notificationEvents.get(eventName));
	}
	
	/**
	 * get local application cache manager info
	 * @return CahceManagerInfo
	 */
	public CacheManagerInfo getLocalAppCacheManagerInfo() {
		return(localAppCacheManagerInfo);
	}
	
	/**
	 * Cache manager configuration info
	 * 
	 * @author Aranjuez
	 * @version Dec 01, 2009
	 * @since Pyrube-ONE 1.0
	 */
	public class CacheManagerInfo implements Serializable {
		
		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = 1L;
	
		/**
		 * cache manager type: In-app
		 */
		public final static String TYPE_INAPP = "in-app";
	
		/**
		 *  cache manager name. for local application cache manager, its name is "".
		 */
		private String name = null;
		
		/**
		 * cache manager type
		 */
		private String type = TYPE_INAPP;
		
		/**
		 * whether this cache manager accept refresh notification
		 */
		private boolean notifierEnabled = false;
	
		/**
		 * constructor
		 * @param name
		 * @param type
		 * @param notifierEnabled
		 * @throws AppException
		 */
		public CacheManagerInfo(String name, String type, boolean notifierEnabled) throws AppException {
			if (type == null || type.length() == 0) type = TYPE_INAPP;
			this.name = name;
			this.type = type;
			this.notifierEnabled = notifierEnabled;
		}
		
		/**
		 * get manager name
		 * @return String
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * get manager type
		 * @return String
		 */
		public String getType() {
			return type;
		}
	
		/**
		 * whether the manager allow notification
		 * @return boolean
		 */
		public boolean isNotifierEnabled() {
			return notifierEnabled;
		}
	
	}
	/**
	 * Cache configuration info. <br>
	 * There are two types of cacheable objects, single and group. <br>
	 * Single type means the cached object is a single java object which can be of any java type/class. <br>
	 * Multiple type means the cache is a set of objects of same java class, each object has its name (key in the group). <br>
	 * 
	 * @author Aranjuez
	 * @version Dec 01, 2009
	 * @since Pyrube-ONE 1.0
	*/
	public class CacheInfo implements Serializable {
		
		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * the cache object name. 
		 */
		private String name = null;
		
		/**
		 * the full class implementing the Cacheable
		 */
		private String className = null;
		
		/**
		 * whether the cache is Multiple type
		 */
		private boolean multiple = false;
		
		/**
		 * whether it is lazy-refreshed after it is notified.
		 * If it is true, then after it is notified it will be refreshed when next time it is used.
		 * If it is false, then it will be refreshed when it is notified.
		 * By default it is true;
		 */
		private boolean lazyRefresh = true;
		
		/**
		 * whether the object is preloaded (loaded when cache manager starts)
		 */
		private boolean preloaded = false;
		
		/**
		 * refresh interval in milliseconds. default is -1 (there is no auto-refresh)
		 */
		private long refreshInterval = -1L;

		/**
		 * preload keys for Group type of cache
		 */
		private Serializable[] preloadKeys = null;

		/**
		 * parameters, (key=paramName, value=Object)
		 */
		private Map<String, ?> params = null;
		
		/**
		 * default constructor
		 */
		public CacheInfo() {
		}

		/**
		 * get cacheable full class name
		 * @return String
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * whether cache is Multiple type
		 * @return boolean
		 */
		public boolean isMultiple() {
			return multiple;
		}

		/**
		 * get cache name
		 * @return String
		 */
		public String getName() {
			return name;
		}

		/**
		 * get cache parameters
		 * @return Map
		 */
		public Map<String, ?> getParams() {
			return params;
		}

		/**
		 * get value of given parameter
		 * @param paramName
		 * @return
		 */
		public Object getParam(String paramName) {
			return(params == null ? null : params.get(paramName));
		}
		
		/**
		 * whether it is lazy-refresh
		 * @return boolean
		 */
		public boolean isLazyRefresh() {
			return lazyRefresh;
		}
		
		/**
		 * whether it is preloaded
		 * @return boolean
		 */
		public boolean isPreloaded() {
			return preloaded;
		}

		/**
		 * get preload keys in the group for Group type of cache
		 * @return Object[]
		 */
		public Serializable[] getPreloadKeys() {
			return preloadKeys;
		}

		/**
		 * get refresh interval in milliseconds
		 * @return long
		 */
		public long getRefreshInterval() {
			return refreshInterval;
		}

		/**
		 * set cacheable full class name
		 * @param className
		 */
		public void setClassName(String className) {
			this.className = className;
		}

		/**
		 * set whether cache is multiple
		 * @param multiple
		 */
		public void setMultiple(boolean multiple) {
			this.multiple = multiple;
		}

		/**
		 * set cache name
		 * @param name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * set cache parameters
		 * @param params
		 */
		public void setParams(Map<String, ?> params) {
			this.params = params;
		}

		/**
		 * set lazyRefresh
		 * @param lazyRefresh
		 */
		public void setLazyRefresh(boolean lazyRefresh) {
			this.lazyRefresh = lazyRefresh;
		}
		
		/**
		 * set preloaded
		 * @param preloaded
		 */
		public void setPreloaded(boolean preloaded) {
			this.preloaded = preloaded;
		}

		/**
		 * set preload keys
		 * @param preloadKeys
		 */
		public void setPreloadKeys(Serializable[] preloadKeys) {
			this.preloadKeys = preloadKeys;
		}

		/**
		 * set refresh interval in milliseconds
		 * @param refreshInterval
		 */
		public void setRefreshInterval(long refreshInterval) {
			this.refreshInterval = refreshInterval;
		}

	}
}
