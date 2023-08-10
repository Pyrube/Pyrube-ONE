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

package com.pyrube.one.util.concurrent;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.config.ConfigManager;
import com.pyrube.one.app.config.Configurator;
import com.pyrube.one.app.logging.Logger;

/**
 * the <code>ConcurrentConfig</code> configurator is for concurrent management.
 * <pre>
 * 		<configurator name="ConcurrentConfig">
 * 			<class>com.pyrube.one.util.concurrent.ConcurrentConfig</class>
 * 			<configPath name="ConcurrentConfig">ConcurrentConfig</configPath>
 * 		</configurator>
 * 		
 * 		<ConcurrentConfig>
 * 			<runnerPools>
 * 				<runnerPool name="native" default="true" helperClass="com.pyrube.one.util.concurrent.DefaultConcurrentRunnerPool"/>
 * 			</runnerPools>
 * 		</ConcurrentConfig>
 * 
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class ConcurrentConfig extends Configurator {
	
	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(ConcurrentConfig.class.getName());
	
	private static ConcurrentConfig theConfig = null;
	private static boolean initialized = false;
	
	private List<RunnerPoolInfo> poolInfos = null;
	
	/**
	 * default pool name
	 */
	private String defaultPoolName = null;
	
	/**
	 * return the concurrent configurator
	 * @return
	 */
	public static ConcurrentConfig getConcurrentConfig() {
		if (!initialized) {
			synchronized(ConcurrentConfig.class) {
				if (!initialized) {
					theConfig = (ConcurrentConfig) getInstance("ConcurrentConfig");
					if (theConfig != null) {
						initialized = true;
					} else {
						logger.error("Configurator named ConcurrentConfig is not found. Please check configuration file.");
					}
				}
			}
		}
		return (theConfig);
	}
		
	@Override
	public void loadConfig(String cfgName, Node cfgNode) throws AppException {
		NodeList nl = ConfigManager.getNodeList(cfgNode, "runnerPools/runnerPool");
		if (nl != null && nl.getLength() > 0) {
			poolInfos = new ArrayList<RunnerPoolInfo>();
			String name = null;
			String className = null;
			Map<?, ?> params = null;
			for (int i = 0; i < nl.getLength(); ++i) {
				Element elm = (Element) nl.item(i);
				name = ConfigManager.getAttributeValue(elm, "name");
				className = ConfigManager.getAttributeValue(elm, "class");
				params = ConfigManager.getDeepParams(elm);
				if (name != null && name.length() > 0 && className != null && className.length() > 0) {
					poolInfos.add(new RunnerPoolInfo(name, className, params));
					if (poolInfos.size() == 1 || Boolean.valueOf(ConfigManager.getAttributeValue(elm, "default")).booleanValue()) {
						defaultPoolName = name;
					}
				} else {
					logger.warn("Runner pool will be ignored. Runner pool name and class must be provided.");
				}
			}
			if (poolInfos.size() == 0) poolInfos = null;
		}
	}

	/**
	 * get runner pool infos
	 * @return
	 */
	public List<RunnerPoolInfo> getPoolInfos() {
		return(poolInfos);
	}
	
	/**
	 * get default runner pool name
	 * @return
	 */
	public String getDefaultPoolName() {
		return(defaultPoolName);
	}
	
	/**
	 * Runner pool configuration info
	 * 
	 * @author Aranjuez
	 * @version Dec 01, 2009
	 * @since Pyrube-ONE 1.0
	 */
	public class RunnerPoolInfo implements Serializable {

		/**
		 * serial version uid
		 */
		private static final long serialVersionUID = 9098402244434784803L;

		/**
		 * the runner pool name
		 */
		private String name = null;
		
		/**
		 * pool class name
		 */
		private String className = null;
		
		/**
		 * any pool parameters
		 */
		private Map<?, ?> params = null;

		/**
		 * default constructor
		 */
		public RunnerPoolInfo() {
		}
		
		/**
		 * constructor
		 * @param name
		 * @param className
		 * @param helperParams
		 */
		public RunnerPoolInfo(String name, String className, Map<?, ?> params) {
			super();
			this.name = name;
			this.className = className;
			this.params = params;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the className
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * @param className the className to set
		 */
		public void setClassName(String className) {
			this.className = className;
		}

		/**
		 * @return the params
		 */
		public Map<?, ?> getParams() {
			return params;
		}

		/**
		 * @param params the params to set
		 */
		public void setParams(Map<?, ?> params) {
			this.params = params;
		}
	}
}
