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

package com.pyrube.one.app.i18n.format.annotations;

import com.pyrube.one.app.i18n.format.FormatManager;

/**
 * defines the date/number format names for data-in/out conversion
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public enum FormatName {
	
	/**
	 * date format name of long timestamp with zone
	 */
	LONGTIMESTAMPZ(FormatManager.DFN_LONGTIMESTAMPZ),
	
	/**
	 * date format name of long timestamp without zone
	 */
	LONGTIMESTAMP(FormatManager.DFN_LONGTIMESTAMP),
	
	/**
	 * date format name of timestamp without zone
	 */
	TIMESTAMP(FormatManager.DFN_TIMESTAMP),
	
	/**
	 * date format name of date
	 */
	DATE(FormatManager.DFN_DATE);
	
	/**
	 * format name
	 */
	private String name;
	
	/**
	 * constructor
	 * @param name
	 */
	private FormatName(String name) {
		this.name = name;
	}
	
	/**
	 * return format name
	 * @return
	 */
	public String getName() {
		return this.name;
	}
}
