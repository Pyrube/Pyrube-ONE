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

/**
 * Cacheables for internal use. Do NOT use it in your application
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Cacheables extends Cacheable {

	/**
	 * constructor
	 */
	public Cacheables() {
		object = (Serializable) Collections.synchronizedMap(new HashMap());
	}
	
	/**
	 * refresh all items in the cache set
	 * @see com.pyrube.one.app.cache.Cacheable#refresh()
	 * @exception AppException
	 */
	protected void refresh() throws AppException {
		if (object != null) {
			// refresh all items in the group. If at least one item has exception, then throws the last exception out.
			Map objs = (Map) object;
			Cacheable[] cobjs = new Cacheable[objs.size()];
			objs.values().toArray(cobjs);
			AppException ex = null;
			for (int i = 0; i < cobjs.length; ++i) {
				try {
					cobjs[i].refresh();
				} catch (AppException e) {
					ex = e;
				} catch (Throwable e) {
					ex = new AppException("message.error.cache-refresh-failed", e);
				}
			}
			if (ex != null) throw ex;
		}
	}

}
