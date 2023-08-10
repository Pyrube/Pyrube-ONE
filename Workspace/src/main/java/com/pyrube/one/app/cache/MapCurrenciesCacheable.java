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

import java.util.HashMap;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;
import com.pyrube.one.util.Currency;

/**
 * Simple cacheable for linked currency map
 * The object is a list of <code>Currency</code>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class MapCurrenciesCacheable extends Cacheable<HashMap<String, Currency>> {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1804017982177178186L;
	
	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(MapCurrenciesCacheable.class.getName());
	
	/**
	 * @see com.pyrube.one.app.cache.Cacheable#refresh()
	 */
	protected void refresh() throws AppException {
		HashMap<String, Currency> m = null;
		if (object != null) {
			m = object;
			m.clear();
		} else {
			m = new HashMap<String, Currency>();
			object = m;
		}
		
		String currencies = (String) objectInfo.getParam("currencies");
		if (!Strings.isEmpty(currencies)) {
			String[] aCurrencies = currencies.split(",");
			for (String sCurrency : aCurrencies) {
				String[] ccyProps = sCurrency.split("\\|");
				Currency currency = new Currency(ccyProps[0], Integer.valueOf(ccyProps[1]), (ccyProps.length == 3 ? ccyProps[2] : null));
				m.put(ccyProps[0], currency);
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("MapCurrenciesCacheable is refreshed.");
		}
	}

}
