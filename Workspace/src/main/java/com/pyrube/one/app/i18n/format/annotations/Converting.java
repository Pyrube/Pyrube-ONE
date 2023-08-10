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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.pyrube.one.app.i18n.format.annotations.FormatName.DATE;

/**
 * format annotation which is applied to bean class to indicate 
 * that date/number field is able to be converted from front-end
 * <pre>
 * usage sample:
 *   @Converting(FormatName.LONGTIMESTAMPZ)
 *   private Date updateTime;
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Converting {
	
	/**
	 * format name for data-in conversion
	 * @return
	 */
	FormatName format() default DATE;
	
	/**
	 * indicates timestamp field is able to change to local time zone
	 * @return
	 */
	boolean local() default false;
}
