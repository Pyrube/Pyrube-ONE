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

package com.pyrube.one.app;

import java.util.Map;

/**
 *
 * Application life cycle listener interface. All application listeners must: <br>
 * 1. implement this interface. <br>
 * 2. be thread-safe. Each listener will have only one instance. <br>
 * 3. provide no-argument default constructor. <br>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public interface AppListener {

	/**
	 * init. it is called once
	 * @param params
	 */
	public void init(Map<String, ?> params);
	
	/**
	 * it is called right after the application started
	 */
	public void afterStarted();

	/**
	 * it is called right before the application is shut down.
	 */
	public void beforeShutdown();
}
