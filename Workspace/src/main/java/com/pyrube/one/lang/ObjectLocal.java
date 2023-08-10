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

package com.pyrube.one.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * the <code>ObjectLocal</code> is a singleton instance. it's for the local 
 * objects available for the current thread. each object has a name in the 
 * ONE's. 
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class ObjectLocal {

	/**
	 * the instance
	 */
	private static ObjectLocal instance = new ObjectLocal();

	/**
	 * object holder for current thread
	 */
	private ThreadLocal<Map<String, Object>> holder 
									= new ThreadLocal<Map<String, Object>>();
	
	/**
	 * constructs a private instance.
	 */
	private ObjectLocal() {
	}

	/**
	 * returns the <code>ObjectLocal</code> instance.
	 */
	public static ObjectLocal getInstance() {
		return(instance);
	}
	
	/**
	 * put an object into the local object pool
	 * @param objName String. the object name
	 * @param objValue Object. the object value
	 */
	public void putObject(String objName, Object objValue) {
		getObjects().put(objName, objValue);
	}
	
	/**
	 * get an object from the local object pool
	 * @param objName String. the object name
	 * @return the object or null if it is not found
	 */
	public Object getObject(String objName) {
		return(getObjects().get(objName));
	}
	
	/**
	 * remove an object from the local object pool
	 * @param objName String. the object name
	 */
	public void removeObject(String objName) {
		getObjects().remove(objName);
	}
	
	/**
	 * returns a may of all objects
	 * @return the Map held by the holder, it never be null
	 */
	private Map<String, Object> getObjects() {
		Map<String, Object> map = holder.get();
		if (map == null) {
			map = new HashMap<String, Object>();
			holder.set(map);
		}
		return(map);
	}
}