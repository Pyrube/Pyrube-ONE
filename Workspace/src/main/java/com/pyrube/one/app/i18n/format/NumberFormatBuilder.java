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

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import com.pyrube.one.lang.Strings;

/**
 * <code>NumberFormatBuilder</code> is a concrete class for building <code>NumberFormat</code>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class NumberFormatBuilder extends FormatBuilder {
	
	/**
	 * constructor
	 * 
	 * @param locale
	 * @param separators
	 * @param patterns
	 */
	public NumberFormatBuilder(Locale locale, Map<String, String> separators, Map<String, ?> patterns) {
		super(locale, separators, patterns);
	}

	/**
	 * return a new NumberFormat based on the given pattern and the locale of this FormatBuider
	 * 
	 * @param nameOrPattern the pre-defined format name or the format pattern
	 * @param args not provided for this FormatBuilder
	 * @return Format
	 */
	@Override
	public Format formatOf(String nameOrPattern, Object... args) {
		if (Strings.isEmpty(nameOrPattern)) return null;
		String pattern = this.whichPattern(nameOrPattern);
		if (Strings.isEmpty(pattern)) pattern = nameOrPattern;
		NumberFormat numberFormat = NumberFormat.getNumberInstance(getLocale());
		((DecimalFormat) numberFormat).applyPattern(pattern);
		return numberFormat;
	}

}
