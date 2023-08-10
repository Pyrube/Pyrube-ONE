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

package com.pyrube.one.app.i18n.format;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.pyrube.one.lang.Strings;

/**
 * <code>DateFormatBuilder</code> is a concrete class for building <code>DateFormat</code>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class DateFormatBuilder extends FormatBuilder {
	
	/**
	 * constructor
	 * 
	 * @param locale
	 * @param separators
	 * @param patterns
	 */
	public DateFormatBuilder(Locale locale, Map<String, String> separators, Map<String, ?> patterns) {
		super(locale, separators, patterns);
	}

	/**
	 * return a new DateFormat for a given pattern and given timezone in the locale of this FormatBuilder
	 * 
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @param args the timezone. if it is a String, it is a timezone id
	 * @return Format
	 */
	@Override
	public Format formatOf(String nameOrPattern, Object... args) {
		if (Strings.isEmpty(nameOrPattern)) return null;
		String pattern = this.whichPattern(nameOrPattern);
		if (Strings.isEmpty(pattern)) pattern = nameOrPattern;
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, getLocale());
		TimeZone z = null;
		if (args != null && args.length > 0) {
			if (args[0] instanceof String) {
				z = TimeZone.getTimeZone((String) args[0]);
			} else if (args[0] instanceof TimeZone) {
				z = (TimeZone) args[0];
			}
		}
		if (z != null) dateFormat.setTimeZone(z);
		return(dateFormat);
	}

}
