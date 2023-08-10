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

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyrube.one.app.config.ConfigParameter;

/**
 * Logger, which is delegation of Log4j Logger. the logger xml config file is 
 * provided by respective application, and named pyrube-log4x.xml or pyrube-config.xml 
 * in classpath config/pyrube/. if neither one is found, then use log4j default configuration. 
 * <p>
 * the element name in the xml file is "log4j:configuration". it could be the 
 * document root element or any element in the document. if the whole xml file 
 * is for the logger, it is the root element, this is the logger.xml or log4j 
 * xml config file. if the xml config file also contains information other 
 * than logger config, it is the child element of the document. for the 
 * config.xml file used by one.config.ConfigManager utility, the logger XPath 
 * is "/pyrube-config/configData/logger/log4j:configuration" 
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class Logger implements Serializable {
	
	/**
	 * config file for <code>Logger</code> is XML format.
	 */
	public static final String CONFIG_FILE_LOG4X = "config/pyrube/pyrube-log4x.xml";
	
	/**
	 * default config file for <code>Logger</code>
	 */
	public static final String DEFAULT_CONFIG_FILE_LOGGER = CONFIG_FILE_LOG4X;

	/**
	 * serial verion uid
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * loggers cache, [loggerName, a logger instance]
	 */
	private static Hashtable<String, Logger> loggers 
										= new Hashtable<String, Logger>();
	
	/**
	 * logger factory for <pro>Pyrube-ONE</pro>
	 */
	//private static LoggerFactory myFactory = new LoggerFactory();
	
	/**
	 * the log4j <code>Logger</code> for the <pro>Pyrube-ONE</pro> 
	 * <code>Logger</code>
	 */
	private org.apache.log4j.Logger logger = null;
	
	/** 
	 * static for initialization
	 */
	static {
		URL url = null;
		ClassLoader clzLoader = Logger.class.getClassLoader();
		try {
			// load configuration for Logger
			if (clzLoader == null) 
				url = ClassLoader.getSystemResource(DEFAULT_CONFIG_FILE_LOGGER);
			else 
				url = clzLoader.getResource(DEFAULT_CONFIG_FILE_LOGGER);
			if (url != null) {
				Element elmt = forLog4j(new File(url.getFile()));
				if (elmt != null)
					// use this configuration to initialize log4j
					org.apache.log4j.xml.DOMConfigurator.configure(elmt);
			}
		} catch (Throwable t) {
			System.err.println(
					"initializing Logger failed due to " + t.toString());
		}
	}
	
	/**
	 * constructs a new <code>Logger</code> with Log4j <code>Logger</code>
	 * @param pLogger is the log4j Logger
	 */
	protected Logger(org.apache.log4j.Logger pLogger) {
		this.logger = pLogger;
	}
	
	/**
	 * returns a <code>Logger</code> instance with a logger name. 
	 * @param loggerName String. 
	 *        the name of the <code>Logger</code> to get. it could be a class 
	 *        name.
	 */
	public static Logger getInstance(String loggerName) {
		Logger logger = (Logger) loggers.get(loggerName);
		if (logger == null) {
			logger = new Logger(org.apache.log4j.Logger.getLogger(loggerName));
			loggers.put(loggerName, logger);
		}
		return(logger);
	}
	
	/**
	 * returns a <code>Logger</code> instance with a logger-owned class. 
	 * @param clz Class. a Class object.
	 */
	public static Logger getInstance(Class<?> clz) {
		return(getInstance(clz.getName()));
	}

	/**
	 * returns this <code>Logger</code> name.
	 * @return this <code>Logger</code> name.
	 */
	public String getName() { return(logger.getName()); }
	
	/**
	 * check whether this logger is enabled for the INFO priority
	 */
	public boolean isInfoEnabled() { return(logger.isInfoEnabled()); }
	
	/**
	 * check whether this logger is enabled for the DEBUG priority
	 */
	public boolean isDebugEnabled() { return(logger.isDebugEnabled()); }
	
	/**
	 * log a message object with the INFO priority
	 * @param msg Object.
	 */
	public void info(Object msg) { logger.info(msg); }
	
	/**
	 * log a message object with the INFO priority including the stack trace 
	 * of the Throwable t passed as parameter
	 * @param msg Object.
	 * @param t Throwable.
	 */
	public void info(Object msg, Throwable t) { logger.info(msg, t); }
	
	/**
	 * log a message object with the DEBUG priority
	 */
	public void debug(Object msg) { logger.debug(msg); }
	
	/**
	 * log a message object with the DEBUG priority including the stack trace 
	 * of the Throwable t passed as parameter
	 */
	public void debug(Object msg, Throwable t) { logger.debug(msg, t); }
	
	/**
	 * log a message object with the WARN priority
	 */
	public void warn(Object msg) { logger.warn(msg); }
	
	/**
	 * log a message object with the WARN priority including the stack trace 
	 * of the Throwable t passed as parameter
	 */
	public void warn(Object msg, Throwable t) { logger.warn(msg, t); }
	
	/**
	 * log a message object with the ERROR priority
	 */
	public void error(Object msg) { logger.error(msg); }
	
	/**
	 * log a message object with the ERROR priority including the stack trace 
	 * of the Throwable t passed as parameter
	 */
	public void error(Object msg, Throwable t) { logger.error(msg, t); }
	
	/**
	 * log a message object with the FATAL priority
	 */
	public void fatal(Object msg) { logger.fatal(msg); }
	
	/**
	 * log a message object with the FATAL priority including the stack trace 
	 * of the Throwable t passed as parameter
	 */
	public void fatal(Object msg, Throwable t) { logger.fatal(msg, t); }
	
	/**
	 * returns Logger config element from the XML config file. and the element 
	 * name is "log4j:configuration". it could be the document root element or 
	 * any element in the document. if the whole XML file is for the logger, 
	 * it is the root element. this is the pyrube-log4x.xml or log4j xml config 
	 * file. if the XML config file also contains information other than logger 
	 * config, it is the child element of the document. for the 
	 * pyrube-config.xml file used by app.config.ConfigManager class, the logger 
	 * XPath is "/pyrube-config/config-content/log4x/log4j:configuration". 
	 *
	 * @param isXml
	 * @return
	 */
	private static Element forLog4j(File isXml) {
		Element elmt = null;
		Document doc = null;
		try {
			javax.xml.parsers.DocumentBuilderFactory factory 
					= javax.xml.parsers.DocumentBuilderFactory.newInstance();
			doc = factory.newDocumentBuilder().parse(isXml);

			// find config parameters
			Properties params = null;
			NodeList parmNodes = doc.getElementsByTagName("parameters");
			if (parmNodes != null && parmNodes.getLength() > 0) {
				params = obtainParams((Element) parmNodes.item(0));
			}
			
			// find all configuration contents for log4x
			NodeList log4jConfs 
					= doc.getElementsByTagName("log4j:configuration");
			if (log4jConfs != null && log4jConfs.getLength() > 0) {
				// just get first one
				elmt = (Element) log4jConfs.item(0);
			}
			
			// substitute values
			if (elmt != null) substitute(elmt, params);
			
			return(elmt);
		} catch (Throwable t) {
			System.err.println(
				"parsing Logger configuration failed due to " + t.toString());
			return(null);
		}
	}
	
	/**
	 * obtain parameters &lt;param name="paramName"&gt;paramValue&lt;/param&gt;
	 * @param ctx is the Node contains the param tags
	 */
	private static Properties obtainParams(Element ctx) throws Exception {
		if (ctx == null) return(null);
		NodeList parmNodes = ctx.getElementsByTagName("param");
		if (parmNodes == null) return(null);
		Properties parms = new Properties();
		for (int i = 0; i < parmNodes.getLength(); ++i) {
			Element parmNode = (Element) parmNodes.item(i);
			String parmName = parmNode.getAttribute("name");
			if (parmName != null && parmName.length() > 0) {
				String parmValue = "";
				Node pvNode = parmNode.getFirstChild();
				if (pvNode != null) {
					parmValue = pvNode.getNodeValue();
					if (parmValue == null) parmValue = "";
				}
				parms.setProperty(parmName, parmValue);
			}
		}
		return(parms.size() > 0 ? parms : null);
	}
	
	/**
	 * substitute config values of text and attributes of the Element
	 * @param cfgElm the log4j config element
	 * @param defaultValues is the default values of parameters
	 */
	private static void substitute(Element cfgElm, Properties defaultValues) {
		try {
			if (cfgElm == null) return;
			
			ArrayList<Node> elms = new ArrayList<Node>();	// list of elements
			elms.add(cfgElm);
			for (int i = 0; i < elms.size(); ++i) {
				Element elm = (Element) elms.get(i);
				Node childNode = elm.getFirstChild();
				NamedNodeMap attNodes = elm.getAttributes();
				if (attNodes != null && attNodes.getLength() > 0) {
					for (int j = 0; j < attNodes.getLength(); ++j) {
						Attr attNode = (Attr) attNodes.item(j);
						String attValue = attNode.getValue();
						if (attValue != null && attValue.length() > 0) {
							String attValueNew = ConfigParameter.substitute(attValue, defaultValues);
							if (!attValue.equals(attValueNew)) attNode.setValue(attValueNew);
						}
					}
				}
				while (childNode != null) {
					short nodeType = childNode.getNodeType();
					if (nodeType == Node.ELEMENT_NODE) {
						elms.add(childNode);	// add to element list
					} else if (nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE) {
						String txtValue = childNode.getNodeValue();
						if (txtValue != null && txtValue.length() > 0) {
							String txtValueNew = ConfigParameter.substitute(txtValue, defaultValues);
							if (!txtValue.equals(txtValueNew)) {
								childNode.setNodeValue(txtValueNew);
							}
						}
					}
					childNode = childNode.getNextSibling();
				}
			}
		} catch (Throwable e) {
			System.err.println("substituting config values error. " + e.toString());
		}
	}
}
