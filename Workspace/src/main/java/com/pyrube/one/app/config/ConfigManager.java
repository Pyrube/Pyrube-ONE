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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.util.crypto.PwdEncoder;
import com.pyrube.one.util.xml.XmlUtility;

/**
 * Centralized Configuration Manager.<br>
 * It will get the init file from system property 
 * "com.pyrube.one.app.config.ConfigManagerInit" or "configManager.properties". 
 * if neither system property is defined, then use the default init file named configManager.properties. 
 * the init file could be an OS file or a resource file reachable in classpath.
 * in the init file each line provides a config file and each config file will have a ConfigManager instance. 
 * the config file could be an OS file or a resource file reachable in classpath. <br>
 * if the init file is not found, then use default config file. It will get the default config file from 
 * system property "com.pyrube.one.app.config.ConfigManager" or "pyrube-config.xml", 
 * if neither system property is defined, then use the default file named pyrube-config.xml. <br>
 * If more config files provided in the init file, the one with ",default" is the default config file. <br>
 * <pre>
 * A sample init file: 
 *   config1=app1_config.xml 
 *   config2=app2_config.xml,default 
 *   config3=com/abc/app/app3_config.xml 
 *   config4=/opt/app/app4_config.xml 
 * <br>
 * in this example, the default Config Manager is app2_config.xml.
 * 
 * The config file has following format:
 * &lt;pyrube-config name="myApp"&gt;
 *   &lt;parameters&gt;
 *     &lt;param name="APP_LOG_PATH"&gt;/tmp/logs&lt;/param&gt;
 *     &lt;param name="APP_LOG_LEVEL"&gt;debug&lt;/param&gt;
 *   &lt;/parameters&gt;
 *   &lt;configurators&gt;
 *     &lt;configurator name="conf1" default="true"&gt;
 *       &lt;class&gt;mypkg.MyConfigurator&lt;/class&gt;
 *       &lt;configPath name=""&gt;myData&lt;/configPath&gt;
 *     &lt;/configurator&gt;
 *     &lt;configurator name="conf2"&gt;
 *       &lt;class&gt;mypkg.MyConfigurator2&lt;/class&gt;
 *       &lt;configPath name="" import="config-MyConfig2.xml"/&gt;
 *     &lt;/configurator&gt;
 *     &lt;configurator name="conf3"&gt;
 *       &lt;class&gt;mypkg.MyConfigurator3&lt;/class&gt;
 *       &lt;configPath name=""&gt;myData3&lt;/configPath&gt;
 *     &lt;/configurator&gt;
 *   &lt;/configurators&gt;
 *   &lt;config-content&gt;
 *     &lt;myData&gt;
 *       &lt;database&gt;
 *         &lt;dataSource&gt;myDataSource_Name&lt;/dataSource&gt;
 *       &lt;/database&gt;
 *     &lt;/myData&gt;
 *     &lt;myData3 import="config-MyConfig3.xml"/&gt;
 *   &lt;/config-content&gt;
 * &lt;/pyrube-config&gt;
 * The section "parameters" define any parameters which can be used in the config content.
 * The section "configurators" register all the configurators.
 * The section "config-content" contains the actual config content for each configurator. Also the
 * actual config data could be imported from external xml files. In the exampe above, config
 * data of configurator conf1 is defined within this file config-content/myData, config content of 
 * configurator conf2 is imported from config-MyConfig2.xml file, config data of configurator 
 * conf3 is imported from config-MyConfig3.xml file. 
 * 
 * <br>
 * write your configurator mypkg.MyConfigurator:
 * public class MyConfigurator implements Configurator {
 *   private String myDataSourceName;
 *   public void loadConfig(String cfgName, Node cfgData) throws Exception {
 *     myDataSourceName = ConfigManager.getSingleValue(cfgData, "database/dataSource");
 *   }
 *   public String getMyDataSourceName() { return(myDataSourceName); }
 * }
 * 
 * then add this configurator in configuration file config.xml
 * &lt;pyrube-config name="myApp"&gt;
 *   &lt;parameters&gt;
 *     &lt;param name="APP_LOG_PATH"&gt;/tmp/logs&lt;/param&gt;
 *     &lt;param name="APP_LOG_LEVEL"&gt;debug&lt;/param&gt;
 *   &lt;/parameters&gt;
 *   &lt;configurators&gt;
 *     ...
 *     &lt;configurator name="conf1" default="true"&gt;
 *       &lt;class&gt;mypkg.MyConfigurator&lt;/class&gt;
 *       &lt;configPath name=""&gt;myData&lt;/configPath&gt;
 *     &lt;/configurator&gt;
 *   &lt;/configurators&gt;
 *   &lt;config-content&gt;
 *     &lt;myData&gt;
 *       &lt;database&gt;
 *         &lt;dataSource&gt;myDataSource_Name&lt;/dataSource&gt;
 *       &lt;/database&gt;
 *     &lt;/myData&gt;
 *     ...
 *   &lt;/config-content&gt;
 * &lt;/pyrube-config&gt;
 * 
 * Use your configurator to get config information.
 * MyConfigurator mc = (MyConfigurator)MyConfigurator.getInstance();
 *  or 
 * MyConfigurator mc = (MyConfigurator)MyConfigurator.getInstance("conf1");
 * mc.getMyDataSourceName();
 * 
 * 
 * <b>Note</b>: the attribute and text values support string substitution using format ${paramName}. 
 * see ConfigParameter class for details. the custom configurator should use methods of ConfigManager 
 * (getSingleValue(), getTextValue(), getValues(), getAttributeValue()) to get attribute and text 
 * values to ensure the value is substituted. 
 * 
 * for password, you may use utility com.pyrube.one.util.pwd.PwdEncoder to encode password and put the encoded
 * password in this configuration file and define the element as password by providing attribute isPassword 
 * with value of true. examples: 
 * &lt;password isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/password&gt; 
 * &lt;param name="password" isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/param&gt; 
 * the configManager will automatically decode the password when you use method getSingleValue() or getTextValue(). 
 *
 * </pre>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class ConfigManager {
	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(ConfigManager.class.getName());
	
	/**
	 * config manager initialization file reachable in classpath. presence of this file is optional.
	 */
	private static final String CONFIGMANAGER_INIT_FILE = "configManager.properties";
	
	/**
	 * default configuration file reachable in classpath
	 */
	private static final String DEFAULT_CONFIG_FILE = "config/pyrube/pyrube-config.xml";
	
	/**
	 * configurator path
	 */
	private static final String CONFIGURATOR_PATH = "/pyrube-config/configurators/configurator";
	
	/**
	 * configuration content path
	 */
	private static final String CONFIGCONTENT_PATH = "/pyrube-config/config-content";
	
	/**
	 * password attribute. if an element has attribute "isPassword" of value true, 
	 * then its text value is password which could be encoded using PwdUtil. The ConfigManager
	 * will decode the encoded password.
	 */
	private static final String PASSWORD_ATT = "isPassword";
	
	/** the default configuration manager instance */
	private static ConfigManager cfgMgr = null;
	
	/** registered config managers */
	private static HashMap<String, ConfigManager> cfgMgrs = new HashMap<String, ConfigManager>();
	
	/** configuration file for current config manager */
	private String configFile = DEFAULT_CONFIG_FILE;

	/** the configuration DOM object for current config manager */
	private Document cfgDom = null;

	/** registered configurator instances by name in current config manager */
	private HashMap<String, Configurator> configurators = new HashMap<String, Configurator>();

	/** default configurator instance for current config manager */
	private Configurator defaultConfigurator = null;
	
	/** parameters default values. each config manager has its own default values */
	private static Properties defaultParamValues = null;
	
	/** flag to avoid reentry into init() */
	private static boolean isInitializing = false;
	
	/**
	 * constructor
	 */
	private ConfigManager() {}

	/**
	 * get the default config manager instance
	 * @return the config manager instance
	 */
	public static ConfigManager getInstance() {
		try {
			if (cfgMgr == null) init();
			if (cfgMgr == null) {
				logger.error("default config manager not found");
				System.out.println("default config manager not found");
			}
			return(cfgMgr);
		} catch (Throwable e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			return(null);
		}
	}
	
	/**
	 * get a named config manager instance
	 * @param pConfigName is the configuration name
	 * @return the config manager instance
	 */
	public static ConfigManager getInstance(String pConfigName) {
		try {
			if (cfgMgr == null) init();
			ConfigManager mgr = (ConfigManager) cfgMgrs.get(pConfigName);
			if (mgr == null) {
				logger.error("config manager (" + pConfigName + ") not found");
				System.out.println("config manager (" + pConfigName + ") not found");
			}
			return(mgr);
		} catch (Exception e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			return(null);
		}
	}
	
	/**
	 * load configuration. 
	 * It will try pConfigFile as a file first, if it fails, then try pConfigFile 
	 * as a resource that is reachable in classpath. If the config contains imported
	 * config data from external, then the external files should be under the same 
	 * directory or package.
	 * @param pConfigFile is the configuration file. it could be a file or a resource. If it 
	 * is a file, it could be like this, pyrube-config.xml, or /tmp/pyrube-config.xml, or C:\tmp\pyrube-config.xml,
	 * or C:/tmp/pyrube-config.xml. If it is a resource, it must be reachable in classpath, it could be 
	 * like this, config/pyrube/pyrube-config.xml, or config\pyrube\pyrube-config.xml (it must not start 
	 * with / or \).
	 * @throws AppException
	 */
	private void loadConfig(String pConfigFile) throws AppException {
		logger.info("start loading configuration...");
		configFile = pConfigFile;
		java.io.InputStream is = null;
		try {
			// isResource and prefix are used to import external config data xml
			boolean isResource = true;
			String prefix = null;
			try {
				java.io.File f = new java.io.File(pConfigFile);
				is = new FileInputStream(f);
				// get the file directory
				isResource = false;
				prefix = f.getParent();
				if (prefix == null) {
					prefix = "";
				} else if (!prefix.endsWith(java.io.File.separator)) {
					prefix = prefix + java.io.File.separator;
				}
			} catch (Throwable ex) {
				// file not found
				if (logger.isDebugEnabled()) {
					logger.debug("try to load config file " + pConfigFile + " from resource.");
					System.out.println("try to load config file " + pConfigFile + " from resource.");
				}
				
				ClassLoader clsLoader = ConfigManager.class.getClassLoader();
				if (clsLoader == null) is = ClassLoader.getSystemResourceAsStream(configFile);
				else is = clsLoader.getResourceAsStream(configFile);
				// get the package of the resource
				isResource = true;
				int iPos = configFile.lastIndexOf("/");
				if (iPos >= 0) {
					prefix = configFile.substring(0, iPos + 1);
				} else {
					iPos = configFile.lastIndexOf("\\");
					if (iPos >= 0) {
						prefix = configFile.substring(0, iPos + 1);
					} else {
						prefix = "";
					}
				}
			}
			if (is == null) throw new Exception("config file " + pConfigFile + " not found.");
			cfgDom = XmlUtility.createDocument(is);
			defaultParamValues = getConfigParameterDefaultValues(cfgDom.getDocumentElement());
			Node cfgMgrNameNode = XmlUtility.selectSingleNode(cfgDom.getDocumentElement(), "/pyrube-config/@name");
			String cfgMgrName = (cfgMgrNameNode == null) ? ConfigManager.class.getName() : cfgMgrNameNode.getNodeValue();
			NodeList cfgtors = XmlUtility.selectNodeList(cfgDom.getDocumentElement(), CONFIGURATOR_PATH);
			configurators.clear();
			for (int i = 0; i < cfgtors.getLength(); ++i) {
				Node cfgtor = cfgtors.item(i);
				Node cfgNameNode = XmlUtility.selectSingleNode(cfgtor, "@name");
				Node cfgDefault = XmlUtility.selectSingleNode(cfgtor, "@default");
				Node clsNode = XmlUtility.selectSingleNode(cfgtor, "class/text()");
				NodeList xpaths = XmlUtility.selectNodeList(cfgtor, "configPath");
				if (clsNode != null) {
					String className = clsNode.getNodeValue();
					if (className != null && className.length() > 0) {
						if (logger.isDebugEnabled()) logger.debug("start loading configurator " + className);
						Configurator cfg = (Configurator)Class.forName(className).newInstance();
						Node cfgDataPath = null;
						if (xpaths != null) {
							for (int j = 0; j < xpaths.getLength(); ++j) {
								Node p = xpaths.item(j);
								String importFile = filterValue(((Element)p).getAttribute("import"));
								String path1Name = ((Element)p).getAttribute("name");
								Node p1Vn = XmlUtility.selectSingleNode(p, "./text()");
								String path1 = (p1Vn == null ? null : p1Vn.getNodeValue());
								cfgDataPath = null;
								if (importFile != null && importFile.length() > 0) {
									// import from external xml
									cfgDataPath = importConfigData(isResource, prefix, importFile);
									if (logger.isDebugEnabled()) logger.debug("loading config from " + (path1Name != null && path1Name.length() > 0 ? path1Name + " - " : "") + importFile);
								} else if (path1 != null && path1.length() > 0) {
									cfgDataPath = XmlUtility.selectSingleNode(cfgDom.getDocumentElement(), CONFIGCONTENT_PATH + "/" + path1);
									if (cfgDataPath != null) {
										importFile = filterValue(((Element)cfgDataPath).getAttribute("import"));
										if (importFile != null && importFile.length() > 0) {
											cfgDataPath = importConfigData(isResource, prefix, importFile);
											if (logger.isDebugEnabled()) logger.debug("loading config from " + (path1Name != null && path1Name.length() > 0 ? path1Name + " - " : "") + importFile);
										} else {
											if (logger.isDebugEnabled()) logger.debug("loading config from " + (path1Name != null && path1Name.length() > 0 ? path1Name + " - " : "") + path1);
										}
									}
								}
								//cfg.addCfgDataNode(cfgDataPath);
								cfg.loadConfig(path1Name, cfgDataPath);
							}
						} else {
							cfg.loadConfig(null, cfgDataPath);
						}
						String cfgName = (cfgNameNode == null) ? null : cfgNameNode.getNodeValue();
						if (cfgName == null) cfgName = className;
						configurators.put(cfgName, cfg);
						// check if it is default configurator or not. the default configurator is the first one or the one with default attribute specified.
						if (defaultConfigurator == null || 
							(cfgDefault != null && "true".equalsIgnoreCase(cfgDefault.getNodeValue())))
							defaultConfigurator = cfg;
						if (logger.isDebugEnabled()) logger.debug("end loading configurator " + className);
					}
				}
			}
			synchronized(cfgMgrs) {
				cfgMgrs.put(cfgMgrName, this);
				if (cfgMgr == null) cfgMgr = this;	// first mgr could be the default
			}
		} catch (Exception e) {
			logger.error("error", e);
			System.out.println("error"+ e);
			throw new AppException("message.error.unknown-config-exception", e);
		} catch (Throwable e) {
			logger.error("error", e);
			System.out.println("error"+ e);
		} finally {
			try { if (is != null) is.close(); } catch (Exception e) {};
			logger.info("end loading configuration");
		}
	}
	
	/**
	 * import external config data xml file. 
	 * <pre>
	 * The external data file content will replace the config data element under <config-content>. 
	 * for example there is a config as follows:
	 *   <config-content>
	 *     <WeaConfig import="config-WeaConfig.xml"/>
	 *   </config-content>
	 * then the external file config-WeaConfig.xml must be in same directory as the main 
	 * config xml file and its content will replace element <WeaConfig>. The config-WeaConfig.xml is:
	 *     <WeaConfig>
	 *       <myProp1>xxx<myProp1>
	 *       <myProp2>xxx<myProp2>
	 *     </WeaConfig>
	 * 
	 * Or directly import the external config data from the <configurator> section. for example:
	 *   <configurator name="WeaConfig">
	 *     <class>com.pyrube.wea.WeaConfig</class>
	 *     <configPath name="WeaConfig" import="config-WeaConfig.xml"/>
	 *   </configurator>
	 * 
	 * </pre>
	 * 
	 * @param isResource whether the main config.xml is resource or file
	 * @param prefix the prefix. the main config.xml file prefix (directory or package)
	 * @param importFile the external file name. If it is an absolute file name, then read it as a 
	 * file directly. otherwise it must be under same directory or package as the main config.xml 
	 * file (using isResource and prefix)
	 * @return Node config data node
	 * @throws Exception
	 */
	private Node importConfigData(boolean isResource, String prefix, String importFile) throws Exception {
		java.io.InputStream is = null;
		String cfgDataFile = importFile;
		if ((new File(importFile)).isAbsolute()) {
			isResource = false;
		} else {
			if (prefix != null) cfgDataFile = prefix + cfgDataFile;
		}
		if (logger.isDebugEnabled()) logger.debug("importing config data file " + cfgDataFile);
		if (isResource) {
			ClassLoader clsLoader = ConfigManager.class.getClassLoader();
			if (clsLoader == null) {
				is = ClassLoader.getSystemResourceAsStream(cfgDataFile);
			} else {
				is = clsLoader.getResourceAsStream(cfgDataFile);
			}
		} else {
			is = new java.io.FileInputStream(cfgDataFile);
		}
		if (is == null) throw new Exception("config data file " + cfgDataFile + " not found.");
		
		Node retDataNode = null;
		Element extData = cfgDom.createElement("extData");
		XmlUtility.streamToNode(is, extData);
		NodeList childList = extData.getChildNodes();
		if (childList != null) {
			for (int i = 0; i < childList.getLength(); ++i) {
				if (childList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					retDataNode = childList.item(i);
					break;
				}
			}
		}
		return(retDataNode);
	}
	
	/**
	 * Initialize configuration manager. 
	 * It will try pConfigFile as a file first, if it fails, then try pConfigFile 
	 * as a resource that is reachable in classpath.
	 * @param pConfigFile is the configuration file
	 * @throws Exception
	 */
	private static ConfigManager init(String pConfigFile) throws Exception {
		ConfigManager mgr = new ConfigManager();
		mgr.loadConfig(pConfigFile);
		return(mgr);
	}
	
	/**
	 * initialize configuration manager. 
	 * It will get the config init file from system property 
	 * "com.pyrube.one.app.config.ConfigManagerInit" or "configManager.properties". 
	 * if neither of these two system properties is defined, then use the default init file named configManager.properties. 
	 * the init file could be an OS file or a resource file reachable in classpath.
	 * @throws Exception
	 */
	public static synchronized void init() throws Exception {
		if (cfgMgr != null) return;
		if (isInitializing) throw new Exception("It is already in the initialization process.");
		isInitializing = true;
		
		logger.info("start initializing configuration");
		java.io.InputStream is = null;
		try {
			String initFile = System.getProperty(ConfigManager.class.getName() + "Init");	// get init file from system property com.pyrube.one.app.config.ConfigManagerInit
			if (initFile == null || initFile.length() == 0) initFile = System.getProperty(CONFIGMANAGER_INIT_FILE);	// get init file from system property configManager.properties
			if (initFile == null || initFile.length() == 0) {
				initFile = CONFIGMANAGER_INIT_FILE;	// use default init file
				if (logger.isDebugEnabled()) logger.debug("system property " + ConfigManager.class.getName() + "Init or " + CONFIGMANAGER_INIT_FILE + " not definded. use default config init file " + CONFIGMANAGER_INIT_FILE);
			}
			if (logger.isDebugEnabled()) logger.debug("config init file is " + initFile);
			try {
				try {
					is = new FileInputStream(initFile);
				} catch (Throwable ex) {
					// file not found
					ClassLoader clsLoader = ConfigManager.class.getClassLoader();
					if (clsLoader == null) is = ClassLoader.getSystemResourceAsStream(initFile);
					else is = clsLoader.getResourceAsStream(initFile);
				}
			} catch (Exception e) {
				is = null;
			}
			if (is == null) {	// no init file found, then load default config
				if (logger.isDebugEnabled()) logger.debug("default config will be used.");
				initDefaultConfig();
			} else {
				Properties props = new Properties();
				try {
					props.load(is);
				} catch (Exception e) {
					if (logger.isDebugEnabled()) logger.debug("config init file " + initFile + " error. " + e.toString() + ". default config will be used.");
					props.clear();
				}
				if (props.size() == 0) {	// use default config
					initDefaultConfig();
				} else {
					for (Enumeration<Object> keys = props.keys(); keys.hasMoreElements();) {
						String key = (String)keys.nextElement();
						if (key != null && key.startsWith("config")) {
							String val = props.getProperty(key);
							if (val != null && val.length() > 0) {
								String cfgFile = val;
								boolean isDefault = false;
								int iPos = val.indexOf(",");
								if (iPos > 0) {
									cfgFile = val.substring(0, iPos);
									String def = "";
									if (val.length() > iPos) def = val.substring(iPos + 1).trim();
									if ("default".equalsIgnoreCase(def)) isDefault = true;
								}
								ConfigManager mgr = init(cfgFile);
								if (isDefault) cfgMgr = mgr;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			throw e;
		} catch (Throwable e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			throw new Exception(e.getMessage());
		} finally {
			try { if (is != null) is.close(); } catch (Exception e) {};
			isInitializing = false;
			logger.info("end initializing configuration");
		}
	}
	
	/**
	 * load default config.
	 * It will get the config file from system property "com.pyrube.one.app.config.ConfigManager" or "config.xml", 
	 * if neither of these two system properties is defined, 
	 * then use the default file named pyrube-config.xml.
	 */
	private static void initDefaultConfig() throws Exception {
		String cfgFile = System.getProperty(ConfigManager.class.getName());	// get config file from system property com.pyrube.one.app.config.ConfigManager
		if (cfgFile == null || cfgFile.length() == 0) cfgFile = System.getProperty(DEFAULT_CONFIG_FILE);	// get config file from system property pyrube-config.xml
		if (cfgFile == null || cfgFile.length() == 0) {
			cfgFile = DEFAULT_CONFIG_FILE;
			if (logger.isDebugEnabled()) logger.debug("system property " + ConfigManager.class.getName() + " or " + DEFAULT_CONFIG_FILE + " not defined. use default config file " + DEFAULT_CONFIG_FILE);
		}
		if (logger.isDebugEnabled()) logger.debug("config file is " + cfgFile);
		init(cfgFile);
	}
	
	/**
	 * get default configurator instance. 
	 * @return the default configurator instance
	 */
	public Configurator getConfigurator() {
		return(defaultConfigurator);
	}

	/**
	 * get a configurator instance by name
	 * @param name is the configurator name
	 * @return the named configurator
	 */
	public Configurator getConfigurator(String configName) {
		return((Configurator)configurators.get(configName));
	}
	
	/**
	 * get a Node list of a config parameter
	 * @param ctx is the context Node
	 * @param paramPath is the Xpath of the parameter in the context
	 * @return the NodeList of the parameter
	 */
	public static NodeList getNodeList(Node ctx, String paramPath) {
		if (ctx == null || paramPath == null) return(null);
		try {
			NodeList nl = XmlUtility.selectNodeList(ctx, paramPath);
			return(nl);
		} catch (Exception e) {
			logger.error(e.toString());
			return(null);
		}
	}

	/**
	 * get a Node of a config parameter
	 * @param ctx is the context Node
	 * @param paramPath is the Xpath of the parameter in the context
	 * @return the Node of the parameter
	 */
	public static Node getNode(Node ctx, String paramPath) {
		if (ctx == null || paramPath == null) return(null);
		try {
			return(XmlUtility.selectSingleNode(ctx, paramPath));
		} catch (Exception e) {
			logger.error("error", e);
			return(null);
		}
	}

	/**
	 * filter the string. it is string substitution, replace "${paramName}" with the value of parameter paramName. 
	 * it searches the parameter paramName in following order: <br>
	 *  1. system property  <br>
	 *  2. in properties file defined by "configParameters.properties" system property. or
	 *     in default file configParameters.properties which is reachable in classpath. <br>
	 *  3. the default value set in the config file. <br>
	 *  if parameter paramName is not found, then leave it without substitution. <br>
	 */
	public static String filterValue(String str) {
		return(ConfigParameter.substitute(str, defaultParamValues));
	}
	
	/**
	 * decode password
	 * @param encPwd is the encoded password.
	 * @return decoded password, if encPwd is not encoded, then returns it self.
	 */
	public static String decodePassword(String encPwd) {
		if (!PwdEncoder.isEncoded(encPwd)) return(encPwd);
		return(new String(PwdEncoder.decode(encPwd)));
	}
	
	/**
	 * get a single text value of a config parameter element. 
	 * if the value is password (the element has attribute isPassword with value of true), it will be decoded. <br>
	 * example, <br>
	 * &lt;password isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/password&gt; <br>
	 * &lt;param name="password" isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/param&gt; <br>
	 *
	 * @param ctx is the context Node
	 * @param paramPath is the Xpath of the parameter in the context
	 * @return the text value of the parameter element. 
	 *   if the value contains parameters "${paramName}", they are also replaced with their values. 
	 */
	public static String getSingleValue(Node ctx, String paramPath) {
		String val = filterValue(XmlUtility.getSingleValue(ctx, paramPath));
		boolean isPwd = Boolean.valueOf(XmlUtility.getAttributeValue(ctx, paramPath, PASSWORD_ATT)).booleanValue();
		return(isPwd ? decodePassword(val) : val);
	}

	/**
	 * get text value of a Node. 
	 * if the value is password (the element has attribute isPassword with value of true), it will be decoded. <br>
	 * example, <br>
	 * &lt;password isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/password&gt; <br>
	 * &lt;param name="password" isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/param&gt; <br>
	 *
	 * @param node is the Node
	 * @return the text value of the Node.
	 *   if the value contains parameters "${paramName}", they are also replaced with their values. 
	 */
	public static String getTextValue(Node node) {
		if (node == null) return(null);
		try {
			Node t = XmlUtility.selectSingleNode(node, "text()");
			if (t == null) return(null);
			String val = filterValue(t.getNodeValue());
			boolean isPwd = Boolean.valueOf(((Element)node).getAttribute(PASSWORD_ATT)).booleanValue();
			return(isPwd ? decodePassword(val) : val);
		} catch (Exception e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			return(null);
		}
	}

	/**
	 * get a list of values of a config parameter
	 * if value is password (the element has attribute isPassword with value of true), it will be decoded. <br>
	 * example, <br>
	 * &lt;password isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/password&gt; <br>
	 * &lt;param name="password" isPassword="true"&gt;{xor}0uMDAVISFw==&lt;/param&gt; <br>
	 *
	 * @param ctx is the context Node
	 * @param paramPath is the Xpath of the parameter in the context
	 * @return a String List of values of the parameter, or null if no values.  
	 *   if the value contains parameters "${paramName}", they are also replaced with their values. 
	 */
	public static List<String> getValues(Node ctx, String paramPath) {
		if (ctx == null || paramPath == null) return(null);
		try {
			ArrayList<String> values = new ArrayList<String>();
			NodeList nodeList = XmlUtility.selectNodeList(ctx, paramPath);
			if (nodeList != null) {
				for (int i = 0; i < nodeList.getLength(); ++i) {
					String v = getTextValue(nodeList.item(i));
					if (v != null) values.add(v);
				}
			}
			return(values.size() > 0 ? values : null);
		} catch (Throwable e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			return(null);
		}
	}

	/**
	 * get attribute value of a config parameter
	 * @param elm is the Element
	 * @param attrName is the attribute name
	 * @return the value of the attribute. 
	 *   if the value contains parameters "${paramName}", they are also replaced with their values. 
	 */
	public static String getAttributeValue(Element elm, String attrName) {
		try {
			return(filterValue(elm.getAttribute(attrName)));
		} catch (Throwable e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			return(null);
		}
	}

	/**
	 * get attribute value of a config parameter
	 * @param ctx is the context Node
	 * @param paramPath is the Xpath of the parameter in the context
	 * @param attrName is the attribute name
	 * @return the value of the attribute. 
	 *   if the value contains parameters "${paramName}", they are also replaced with their values. 
	 */
	public static String getAttributeValue(Node ctx, String paramPath, String attrName) {
		return(filterValue(XmlUtility.getAttributeValue(ctx, paramPath, attrName)));
	}

	/**
	 * get a list of same attribute values of a repeated config parameter. <br>
	 * In following example we want to get a list of cache names. <br>
	 * <pre>
	 * &lt;caches&gt; <br>
	 *   &lt;cache name="cache1"/&gt; <br>
	 *   &lt;cache name="cache2"/&gt; <br>
	 *   &lt;cache name="cache3"/&gt; <br>
	 * &lt;/caches&gt; <br>
	 * </pre>
	 *
	 * @param ctx is the context Node
	 * @param paramPath is the Xpath of the parameter in the context
	 * @param attrName is the attribute name of the parameter
	 * @return a String List of values of the parameter attribute, or null if no values.  
	 *   if the value contains parameters "${paramName}", they are also replaced with their values. 
	 */
	public static List<String> getAttributeValues(Node ctx, String paramPath, String attrName) {
		if (ctx == null || paramPath == null) return(null);
		try {
			ArrayList<String> values = new ArrayList<String>();
			NodeList nodeList = XmlUtility.selectNodeList(ctx, paramPath);
			if (nodeList != null) {
				for (int i = 0; i < nodeList.getLength(); ++i) {
					String v = getAttributeValue((Element) nodeList.item(i), attrName);
					if (v != null && v.length() > 0) values.add(v);
				}
			}
			return(values.size() > 0 ? values : null);
		} catch (Throwable e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			return(null);
		}
	}

	/**
	 * get config parameters
	 * @param pCfgNode the Node containing the config parameters
	 */
	private static Properties getConfigParameterDefaultValues(Node pCfgNode) {
		try {
			Node parmsNode = getNode(pCfgNode, "parameters");
			if (parmsNode == null) return(null);
			return(getParams(parmsNode));
		} catch (Exception e) {
			logger.error("error", e);
			System.out.println("error:"+e);
			return(null);
		}
	}
	
	/**
	 * get parameters &lt;param name="paramName"&gt;paramValue&lt;/param&gt;
	 * @param ctx is the Node contains the param tags
	 * @throws AppException
	 */
	public static Properties getParams(Node ctx) throws AppException {
		if (ctx == null) return(null);
		NodeList parmNodes = getNodeList(ctx, "param");
		if (parmNodes == null) return(null);
		Properties parms = new Properties();
		for (int i = 0; i < parmNodes.getLength(); ++i) {
			Element parmNode = (Element) parmNodes.item(i);
			String parmName = getAttributeValue(parmNode, "name");
			if (parmName != null && parmName.length() > 0) {
				String parmValue = getTextValue(parmNode);	//XMLUtility.getSingleValue(parmNode, ".");
				if (parmValue == null) parmValue = "";
				parms.setProperty(parmName, parmValue);
			}
		}
		return(parms.size() > 0 ? parms : null);
	}
	
	/**
	 * get deep parameters <br>
	 * <pre>
	 * &lt;ctx&gt;
	 *   &lt;param name="p1Name"&gt;p1Value&lt;/param&gt;
	 *   &lt;param name="p2Name"&gt;p2Value&lt;/param&gt;
	 *   &lt;param name="p3Name"&gt;
	 *     &lt;param name="p31Name"&gt;p31Value&lt;/param&gt;
	 *   &lt;/param&gt;
	 * &lt;/ctx&gt;
	 * </pre>
	 * @param ctx is the Node contains the param tags
	 * @return Map of parameters. it is (name, value) pair, where value could be String, List or Map.
	 * @throws AppException
	 */
	public static Map<String, Object> getDeepParams(Node ctx) throws AppException {
		if (ctx == null) return(null);
		NodeList parmNodes = getNodeList(ctx, "param");
		if (parmNodes == null || parmNodes.getLength() == 0) return(null);
		Map<String, Object> parms = new HashMap<String, Object>();
		for (int i = 0; i < parmNodes.getLength(); ++i) {
			Element parmNode = (Element) parmNodes.item(i);
			String parmName = getAttributeValue(parmNode, "name");
			if (parmName != null && parmName.length() > 0) {
				Object val = getDeepParams(parmNode);	// Map
				if (val == null) {	// leaf value
					val = getTextValue(parmNode);	// string
					if (val == null) val = "";
				}
				Object oldVal = parms.get(parmName);
				if (oldVal == null) {	// this is the value
					parms.put(parmName, val);
				} else {	// already has value (multiple values)
					if (oldVal instanceof List<?>) {	// add to existing list
						((List<Object>)oldVal).add(val);
					} else {	// create list
						List<Object> lv = new ArrayList<Object>();
						lv.add(oldVal);
						lv.add(val);
						parms.put(parmName, lv);
					}
				}
			}
		}
		return(parms.size() > 0 ? parms : null);
	}
}
