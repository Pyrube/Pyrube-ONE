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

/**
 * Application cache event listener interface. When publish a cache event the cache manager will
 * notify the configured cache event listener. This provides a way to trigger other customized actions.
 * The cache event listener class must: <br>
 * 1. implement this interface. <br>
 * 2. thread-safe. The CacheManager will create one instance of a listener, call init() once, 
 *    then use publishEvent() in multiple threads. <br>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public interface CacheEventListener {

	/**
	 * Initialize the listener
	 * 
	 * @param params is init parameters
	 */
	public void init(Map<String, ?> params);
	
	/**
	 * Publish a refresh notification event to application caches.
	 * 
	 * @param eventName is the event name
	 * @param groupKey is optional. For Group type of cache, it will be used to 
	 * identify the item in the group. If it is null, then refresh whole group.
	 */
	public void publishEvent(String eventName, Serializable groupKey);

}
