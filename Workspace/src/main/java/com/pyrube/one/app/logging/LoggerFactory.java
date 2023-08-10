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

package com.pyrube.one.app.logging;

/**
 * logger factory to make new <code>Logger</code> instance.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class LoggerFactory {

	/**
	 * constructs a new <code>LoggerFactory</code>
	 */
	public LoggerFactory() {
		super();
	}

	/**
	 * returns a new Log4j <code>Logger</code> instance.
	 * @param loggerName String. the logger name.
	 * @return
	 */
	public org.apache.log4j.Logger getLogger(String loggerName) {
		return (new DefaultLogger(loggerName));
	}
}
