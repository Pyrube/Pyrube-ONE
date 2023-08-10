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

import com.pyrube.one.app.AppException;

/**
 * Cache Keys to hold multiple keys
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class CacheKeys<K extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * the array list of keys, the item must be Serializable.
	 * ArrayList is Serializable
	 */
	private ArrayList<K> keys = null;
	
	/**
	 * default constructor
	 */
	public CacheKeys() {
	}
	
	/**
	 * constructor
	 * @param cacheKeys the item of the array must be Serializable
	 * @throws AppException if item is not Serializable
	 */
	public CacheKeys(ArrayList<K> cacheKeys) throws AppException {
		if (cacheKeys != null) {
			for (Iterator<K> it = cacheKeys.iterator(); it.hasNext(); ) {
				if (!(it.next() instanceof Serializable)) throw new AppException("message.error.invalid-cache-key");
			}
		}
		this.keys = cacheKeys;
	}
	
	/**
	 * constructor
	 * @param cacheKeys array of cache keys
	 */
	public CacheKeys(K[] cacheKeys) {
		if (cacheKeys != null && cacheKeys.length > 0) {
			keys = new ArrayList<K>();
			for (int i = 0; i < cacheKeys.length; ++i) keys.add(cacheKeys[i]);
		}
	}
	
	/**
	 * add a cache key 
	 * @param cacheKey
	 */
	public void add(K cacheKey) {
		if (keys == null) keys = new ArrayList<K>();
		keys.add(cacheKey);
	}
	
	/**
	 * remove a cache key
	 * @param cacheKey
	 */
	public void remove(K cacheKey) {
		if (keys != null) keys.remove(cacheKey);
	}
	
	/**
	 * get all keys for one cache
	 * @return ArrayList
	 */
	public ArrayList<K> getKeys() {
		return(keys);
	}
	
	/**
	 * whether it contains keys
	 * @return boolean
	 */
	public boolean hasKeys() {
		return(keys != null && keys.size() > 0);
	}
	
	/**
	 * get an iterator to go through all the keys
	 * @return Iterator or null if there are no any keys
	 */
	public Iterator<K> iterator() {
		return(keys != null ? keys.iterator() : null);
	}
	
	/**
	 * toString
	 * @return String
	 */
	public String toString() {
		if (keys == null || keys.size() == 0) return("");
		StringBuffer sb = new StringBuffer();
		sb.append(keys.get(0).toString()).append("...").append(keys.size());
		return(sb.toString());
	}
}
