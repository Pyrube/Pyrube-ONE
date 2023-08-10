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

package com.pyrube.one.app.config;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.logging.Logger;

/**
 * the <code>Configurator</code> is a super class to load all configurations. 
 * all configurators must extends this class. this class also provides some 
 * factory methods <code>getInstance()</code> to get different configurators.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public abstract class Configurator {

	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(Configurator.class.getName());
	
	/**
	 * the first configuration data content
	 */
	private Node dataContent = null;
	
	/**
	 * an array of more configuration data contents
	 */
	private List<Node> moreDataContents = null;
	
	/**
	 * returns the default <code>Configurator</code> instance of the default 
	 * <code>ConfigManager</code>.
	 * @return the default <code>Configurator</code> instance of the default 
	 *         <code>ConfigManager</code>.
	 */
	public static Configurator getInstance() {
		ConfigManager mgr = ConfigManager.getInstance();
		Configurator cfg = (mgr == null ? null : mgr.getConfigurator());
		if (cfg == null) 
			logger.error( "default configurator " 
						+ "of the default manager not found" );
		return(cfg);
	}
	
	/**
	 * returns the named <code>Configurator</code> instance with a 
	 * configurator name of the default <code>ConfigManager</code>.
	 * @param configuratorName String. 
	 *        the name of the <code>Configurator</code>
	 * @return the named <code>Configurator</code> instance of the default 
	 *         <code>ConfigManager</code>.
	 */
	public static Configurator getInstance(String configuratorName) {
		ConfigManager mgr = ConfigManager.getInstance();
		Configurator cfg = (mgr == null) 
			? null : mgr.getConfigurator(configuratorName);
		if (cfg == null) 
			logger.error( "the configurator (" + configuratorName + ") " 
						+ "of the default manager not found" );
		return(cfg);
	}
	
	/**
	 * returns the named <code>Configurator</code> instance with a 
	 * configurator name of the named <code>ConfigManager</code> with a config 
	 * manager name.
	 * @param configName String.
	 *        the name of the <code>ConfigManager</code>
	 * @param configuratorName String. 
	 *        the name of the <code>Configurator</code>
	 * @return the named configurator of the named config manager
	 */
	public static Configurator getInstance(String configName, String configuratorName) {
		ConfigManager mgr = ConfigManager.getInstance(configName);
		Configurator cfg = (mgr == null) 
			? null : mgr.getConfigurator(configuratorName);
		if (cfg == null) 
			logger.error( "the configurator (" + configuratorName + ") " 
						+ "of the manager (" + configName + ") not found");
		return(cfg);
	}
	
	/**
	 * adds configuration data content, called by <code>ConfigManager</code>
	 * @param pDataContent Node.
	 *        the configuration data content
	 */
	public final void addDataContent(Node pDataContent) {
		if (dataContent == null) {
			dataContent = pDataContent;
		} else {
			if (moreDataContents == null) 
				moreDataContents = new ArrayList<Node>();
			moreDataContents.add(pDataContent);
		}
	}
	
	/**
	 * returns the value on the data path specified.
	 * @param dataPath String.
	 *        the data path based on XML format
	 * @return the value on the data path specified.
	 */
	public final String valueOn(String dataPath) {
		String value = null;
		if (dataContent != null) 
			value = ConfigManager.getSingleValue(dataContent, dataPath);
		if (value == null) {
			if (moreDataContents != null) {
				for (int i = 0; i < moreDataContents.size(); ++i) {
					value = ConfigManager.getSingleValue(
								moreDataContents.get(i), dataPath);
					if (value != null) break;
				}
			}
		}
		return(value);
	}
	
	/**
	 * loads configuration, which will be called by <code>ConfigManager</code>.
	 * it could be called several times if you configure more than one config 
	 * path for this <code>Configurator</code>. for each config path, you may 
	 * provide a name which can be identified by the configurator.
	 * @param cfgName String. 
	 *        the name for the current config path
	 * @param cfgNode is the configuration Node
	 * @throws AppException
	 */
	public abstract void loadConfig(String cfgName, Node cfgNode) throws AppException;
}
