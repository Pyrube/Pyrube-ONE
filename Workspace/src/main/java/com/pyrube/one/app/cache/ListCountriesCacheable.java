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

import java.util.ArrayList;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;
import com.pyrube.one.util.Option;

/**
 * Simple cacheable for country option list
 * The object is a list of <code>Option</code>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class ListCountriesCacheable extends Cacheable<ArrayList<Option>> {
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(ListCountriesCacheable.class.getName());
	
	/**
	 * @see com.pyrube.one.app.cache.Cacheable#refresh()
	 */
	protected void refresh() throws AppException {
		ArrayList<Option> l = null;
		if (object != null) {
			l = (ArrayList<Option>) object;
			l.clear();
		} else {
			l = new ArrayList<Option>();
			object = l;
		}
		
		String countries = (String) objectInfo.getParam("countries");
		if (!Strings.isEmpty(countries)) {
			String[] aCountries = countries.split(",");
			for (String sCountry : aCountries) {
				Option option = Apps.an.option.of(sCountry);
				l.add(option);
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("ListCountriesCacheable is refreshed.");
		}
	}

}
