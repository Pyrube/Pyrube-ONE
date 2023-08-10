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

package com.pyrube.one.app.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * the <code>AppContextManager</code> is to directly use application context 
 * and beans with Spring framework
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
@Component
public class AppContextManager implements ApplicationContextAware {
	
	/**
	 * application context
	 */
	private static ApplicationContext appContext;
	
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		if (AppContextManager.appContext == null) {
			AppContextManager.appContext = appContext;
		}
	}
	
	/**
	 * return bean for a given bean name
	 * @param name the bean name.
	 * @return
	 */
	public static Object findBean(String name) {
		return appContext.getBean(name);
	}

}
