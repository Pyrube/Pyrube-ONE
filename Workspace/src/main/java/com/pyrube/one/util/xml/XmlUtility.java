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

package com.pyrube.one.util.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.Templates;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.util.crypto.PwdEncoder;

/**
 * XML utility. Please use this utility to do XML operations in your application.
 * this will make it easier to upgrade the XML parser and other tools (XSLT, etc) in the future.
 * it needs JAXP compliant implementation such as Xerces 1.3 or later and Xalan 2.4 or later. <br> <br>
 *
 * for XSLT transformerFactory, uses the following ordered lookup procedure to determine the TransformerFactory
 * implementation class to load: <br>
 *  1. Use the javax.xml.transform.TransformerFactory system property. <br>
 *  2. Use the properties file "lib/jaxp.properties" in the JRE directory.
 *     This configuration file is in standard java.util.Properties format and contains the
 *     fully qualified name of the implementation class with the key being the system
 *     property defined above. <br>
 *  3. Use the Services API (as detailed in the JAR specification), if available, to determine
 *     the classname. The Services API will look for a classname in the file
 *     META-INF/services/javax.xml.transform.TransformerFactory in jars available to the runtime. <br>
 *  4. Platform default TransformerFactory instance. <br>
 * <br>
 * <pre>
 * in XSLT stylesheet, we can use &lt;xsl:output att=""/&gt; to set transformation output properties.
 *  attributes of tag xsl:output could be:
 *    method = "xml" | "html" | "text"    - The method attribute identifies the overall method that should be used for outputting the result tree.
 *    encoding = "UTF-8" | otherEncodingName    - specifies the preferred character encoding that the Transformer should use to encode sequences of characters as sequences of bytes. The value of the attribute should be treated case-insensitively.
 *    omit-xml-declaration = "yes" | "no"    - specifies whether the XSLT processor should output an XML declaration.
 *    cdata-section-elements = elmNames - specifies a whitespace delimited list of the names of elements whose text node children should be output using CDATA sections.
 *    indent = "yes" | "no"  - specifies whether the Transformer may add additional whitespace when outputting the result tree
 *    media-type = string  - specifies the media type (MIME content type) of the data that results from outputting the result tree.
 *  please refer javax.xml.transform.OutputKeys class for more information.
 * <br>
 * example:
 * &lt;xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"&gt;
 *   &lt;xsl:variable name="currUrl"&gt;value&lt;/xsl:variable&gt;
 *   &lt;xsl:output method="html" indent="yes" omit-xml-declaration="yes"/&gt;
 *   ...
 * &lt;/xsl:stylesheet&gt;
 *
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class XmlUtility {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(XmlUtility.class.getName());

	/**
	 * transformerFactory. <br>
	 * uses the following ordered lookup procedure to determine the TransformerFactory
	 * implementation class to load: <br>
	 *  1. Use the javax.xml.transform.TransformerFactory system property. <br>
	 *  2. Use the properties file "lib/jaxp.properties" in the JRE directory.
	 *     This configuration file is in standard java.util.Properties format and contains the
	 *     fully qualified name of the implementation class with the key being the system
	 *     property defined above. <br>
	 *  3. Use the Services API (as detailed in the JAR specification), if available, to determine
	 *     the classname. The Services API will look for a classname in the file
	 *     META-INF/services/javax.xml.transform.TransformerFactory in jars available to the runtime. <br>
	 *  4. Platform default TransformerFactory instance.
	 */
	private static javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();

	/**
	 * transformer templates cache
	 */
	private static HashMap<String, Templates> transformerTemplates = new HashMap<String, Templates>();

	/**
	 * turn XSLT transformation template cache on/off.
	 * turn on cache for better performance.
	 * turn off cache for debug/test
	 */
	private static boolean isTemplatesCacheOn = true;

	/**
	 * when parse a XML string to DOM, we can turn on/off the XML validation against the doc system id.
	 */
	private static boolean isValidation = false;

	/**
	 * valid system URL protocols
	 */
	private static String[] urlProtocols = new String[] {
			"http://", "file://", "ftp://", "https://"
		};

	/**
	 * constructor
	 */
	protected XmlUtility() {}

	/**
	 * set transformation templates cache on/off
	 */
	public static void setTemplateCache(boolean cacheOn) {
		isTemplatesCacheOn = cacheOn;
	}

	/**
	 * turn on/off xml parsing validation
	 */
	public static void setValidation(boolean validationOn) {
		isValidation = validationOn;
	}

	/**
	 * convert xml String into a new Document object
	 * @param xmlString is the XML string
	 * @return new DOM object
	 * @exception Exception
	 */
	public static Document stringToDocument(String xmlString) throws Exception {
		return(createDocument(xmlString));
	}

	/**
	 * create XML Document object from an xml string
	 * @param xmlString is the XML string
	 * @return XML Document object
	 * @exception Exception
	 */
	public static Document createDocument(String xmlString) throws Exception {
		return(createDocument(new org.xml.sax.InputSource(new StringReader(xmlString))));
	}

	/**
	 * create XML Document object from an xml inputstream
	 * @param xmlInputStream is the XML input stream in UTF-8 encoding bytes
	 * @return XML Document object
	 * @exception Exception
	 */
	public static Document createDocument(InputStream xmlInputStream) throws Exception {
		return(createDocument(new org.xml.sax.InputSource(xmlInputStream)));
	}

	/**
	 * create XML Document object from an xml inputstream
	 * @param xmlInputStream is the XML input stream
	 * @param encoding is the character encoding such as UTF-8
	 * @return XML Document object
	 * @exception Exception
	 */
	public static Document createDocument(InputStream xmlInputStream, String encoding) throws Exception {
		org.xml.sax.InputSource src = new org.xml.sax.InputSource(xmlInputStream);
		if (encoding != null) src.setEncoding(encoding);
		return(createDocument(src));
	}

	/**
	 * create XML Document object from an xml characters reader
	 * @param xmlReader is the XML characters reader
	 * @return XML Document object
	 * @exception Exception
	 */
	public static Document createDocument(Reader xmlReader) throws Exception {
		return(createDocument(new org.xml.sax.InputSource(xmlReader)));
	}

	/**
	 * create XML Document object from a InputSource
	 * @param xmlInputSource is the XML input source
	 * @return XML Document object
	 * @exception Exception
	 */
	public static Document createDocument(org.xml.sax.InputSource xmlInputSource) throws Exception {
		try {
			Document doc = null;

			// JAXP, Apache xerces 1.3 or later
			javax.xml.parsers.DocumentBuilderFactory fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			fact.setValidating(isValidation);
			doc = fact.newDocumentBuilder().parse(xmlInputSource);

			return(doc);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * create an empty XML Document object
	 * @param docType is the document type
	 * @param systemID is the SYSTEM ID
	 * @return a new document object
	 * @exception Exception
	 */
	public static Document createDocument(String docTypeName, String systemID) throws Exception {
		try {
			javax.xml.parsers.DocumentBuilderFactory fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			DOMImplementation domImpl = fact.newDocumentBuilder().getDOMImplementation();
			DocumentType docType = domImpl.createDocumentType(docTypeName, null, systemID);
			Document doc = domImpl.createDocument(null, docTypeName, docType);	// DOM Level 2

			return(doc);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * create an empty XML Document object
	 * @return a new document object
	 * @exception Exception
	 */
	public static Document createDocument() throws Exception {
		try {
			// JAXP, Apache xerces 1.3 or later
			javax.xml.parsers.DocumentBuilderFactory fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			Document doc = null;
			doc = fact.newDocumentBuilder().newDocument();
			return(doc);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * serialize Document object to a xml string
	 * @param doc is a Document object
	 * @return XML string
	 * @exception Exception
	 */
	public static String serializeDocument(Document doc) throws Exception {
		try {
			return(serializeNode(doc));
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * serialize Node object to an xml string
	 * @param node is Node object
	 * @return XML string
	 * @exception Exception
	 */
	public static String serializeNode(Node node) throws Exception {
		try {
			return(transformToString(node, null));
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * serialize Node object to an xml string without XML declaration header
	 * @param node is Node object
	 * @param includeXmlHeader if it is true, then same as serializeNode(Node). if it is false, then same as serializeNode(Node) except without XML declaration header.
	 * @return XML string
	 * @exception Exception
	 */
	public static String serializeNode(Node node, boolean includeXmlHeader) throws Exception {
		try {
			if (includeXmlHeader) {
				return(transformToString(node, null));
			} else {
				javax.xml.transform.Transformer transformer = createTransformer(null);
				transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");

				StringWriter writer = new StringWriter();
				javax.xml.transform.Source source = new javax.xml.transform.dom.DOMSource(node);
				javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(writer);
				transformer.transform(source, result);
				writer.flush();
				return(writer.getBuffer().toString());
			}
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * convert a xml Stream into a given Node
	 * @param xmlStream is the XML InputStream in UTF-8 encoding bytes
	 * @param holderNode is the Node to contain the tree. it could be Document, Element, or DocumentFragment Nodes.
	 * @exception Exception
	 */
	public static void streamToNode(InputStream xmlStream, Node holderNode) throws Exception {
		javax.xml.transform.Result result = new javax.xml.transform.dom.DOMResult(holderNode);
		transformToResult(xmlStream, null, result);	// copy to result
	}

	/**
	 * convert a xml Reader into a given Node
	 * @param xmlReader is the XML characters reader
	 * @param holderNode is the Node to contain the tree. it could be Document, Element, or DocumentFragment Nodes.
	 * @exception Exception
	 */
	public static void readerToNode(Reader xmlReader, Node holderNode) throws Exception {
		javax.xml.transform.Result result = new javax.xml.transform.dom.DOMResult(holderNode);
		transformToResult(xmlReader, null, result);	// copy to result
	}

	/**
	 * convert a xml string into a given Node
	 * @param xmlString is the XML string
	 * @param holderNode is the Node to contain the tree. it could be Document, Element, or DocumentFragment Nodes.
	 * @exception Exception
	 */
	public static void stringToNode(String xmlString, Node holderNode) throws Exception {
		javax.xml.transform.Result result = new javax.xml.transform.dom.DOMResult(holderNode);
		transformToResult(xmlString, null, result);	// copy to result
	}

	/**
	 * convert Node object to an xml string
	 * @param node is Node object
	 * @return XML string
	 * @exception Exception
	 */
	public static String nodeToString(Node node) throws Exception {
		return(serializeNode(node));
	}

	/**
	 * convert Node object to an xml string
	 * @param node is Node object
	 * @param includeXmlHeader if it is true, then same as nodeToString(Node). if it is false, then same as nodeToString(Node) except without XML declaration header.
	 * @return XML string
	 * @exception Exception
	 */
	public static String nodeToString(Node node, boolean includeXmlHeader) throws Exception {
		return(serializeNode(node, includeXmlHeader));
	}

	/**
	 * convert Document object to a xml string
	 * @param doc is a Document object
	 * @return XML string
	 * @exception Exception
	 */
	public static String documentToString(Document doc) throws Exception {
		return(serializeDocument(doc));
	}

	/**
	 * create child node path. if it already exists then just get it, otherwise create it.
	 * @param parent is the parent Node
	 * @param childPathToken is the child Node path token
	 * @return the child Node
	 * @exception Exception
	 */
	public static Node createChildPath(Node parent, StringTokenizer childPathToken) throws Exception {
		if (parent == null || childPathToken == null) return(null);
		Document doc = parent.getOwnerDocument();
		if (doc == null) doc = (Document) parent;	// if it is null, then the Node itself is the Document. (it is also possible DocumentType, but normally itis Document)
		while (childPathToken.hasMoreTokens()) {
			String name = childPathToken.nextToken();
			if (name.length() == 0) continue;
			Node nod = selectSingleNode(parent, name);
			if (nod == null) {
				nod = doc.createElement(name);
				parent.appendChild(nod);
			}
			parent = nod;
		}
		return(parent);
	}

	/**
	 * get or create (if it doesn't exist) a child node
	 * @param parent is the parent Node
	 * @param childPath is the child Node. example abc/def/g
	 * @return the child Node
	 * @exception Exception
	 */
	public static Node createChildPath(Node parent, String childPath) throws Exception {
		if (parent == null || childPath == null || childPath.length() == 0) return(null);
		return(createChildPath(parent, childPath, (String)null));
	}

	/**
	 * get or create (if it doesn't exist) a child node
	 * @param parent is the current Node
	 * @param childPath is the child Node. example abc.def.g
	 * @param pathSeparator is the node path separator. example ".". if it is null, then use default "/".
	 * @return the child Node
	 * @exception Exception
	 */
	public static Node createChildPath(Node parent, String childPath, String pathSeparator) throws Exception {
		if (parent == null || childPath == null || childPath.length() == 0) return(null);
		if (pathSeparator == null) pathSeparator = "/";
		StringTokenizer tok = new StringTokenizer(childPath, pathSeparator);
		return(createChildPath(parent, tok));
	}

	/**
	 * add single element to a node. it doesn't check if an element exists with the same name.
	 * @param contextNode is the node to which the element is added
	 * @param elmName is the new element name, not XPath.
	 * @return the new Element
	 */
	public static Element addSingleElement(Node contextNode, String elmName) throws Exception {
		Element elm = contextNode.getOwnerDocument().createElement(elmName);
		contextNode.appendChild(elm);
		return(elm);
	}

	/**
	 * add single element to a node. it doesn't check if an element exists with the same name.
	 * @param contextNode is the node to which the element is added
	 * @param elmName is the new element name, not XPath.
	 * @param elmValue is the text value of the element.
	 * @return the new Element
	 */
	public static Element addSingleElement(Node contextNode, String elmName, String elmValue) throws Exception {
		Element elm = addSingleElement(contextNode, elmName);
		if (elmValue != null) elm.appendChild(contextNode.getOwnerDocument().createTextNode(elmValue));
		return(elm);
	}

	/**
	 * add multiple elements to a node. it doesn't check if an element exists with the same name.
	 * @param contextNode is the node to which the element is added
	 * @param elmName is the new element name, not XPath.
	 * @param elmValues are the text values of the elements.
	 */
	public static void addElements(Node contextNode, String elmName, String elmValues[]) throws Exception {
		if (elmValues == null) return;
		for (int i = 0; i < elmValues.length; ++i)
			addSingleElement(contextNode, elmName, elmValues[i]);
	}

	/**
	 * add multiple elements to a node. it doesn't check if an element exists with the same name.
	 * @param contextNode is the node to which the element is added
	 * @param elmName is the new element name, not XPath.
	 * @param elmValues are the text values of the elements.
	 */
	public static void addElements(Node contextNode, String elmName, List<String> elmValues) throws Exception {
		if (elmValues == null) return;
		for (int i = 0; i < elmValues.size(); ++i)
			addSingleElement(contextNode, elmName, (String) elmValues.get(i));
	}

	/**
	 * remove Nodes
	 * @param contextNode is the context node
	 * @param childPath is the XPath of the Node(s) to be removed
	 */
	public static void removeNodes(Node contextNode, String childPath) throws Exception {
		NodeList nl = selectNodeList(contextNode, childPath);
		if (nl != null && nl.getLength() > 0) {
			Node pn = nl.item(0).getParentNode();
			for (int i = 0; i < nl.getLength(); ++i) pn.removeChild(nl.item(i));
		}
	}

	/**
	 * remove one Node. if more than one node exist, then remove the first one.
	 * @param contextNode is the context node
	 * @param childPath is the XPath of the Node to be removed
	 */
	public static void removeNode(Node contextNode, String childPath) throws Exception {
		Node n = selectSingleNode(contextNode, childPath);
		if (n != null) n.getParentNode().removeChild(n);
	}

	/**
	 * set element text value
	 * @param elm is the element
	 * @param value is the new value of text of the element. if it is null, then remove the element's Text child.
	 * @return the Text node of the value
	 */
	public static Text setElementValue(Element elm, String value) throws Exception {
		Text valNode = (Text)selectSingleNode(elm, "text()");
		if (valNode != null) {
			if (value != null) {
				valNode.setNodeValue(value);
			} else {
				elm.removeChild(valNode);
				valNode = null;
			}
		} else if (value != null) {
			valNode = elm.getOwnerDocument().createTextNode(value);
			elm.appendChild(valNode);
		}
		return(valNode);
	}

	/**
	 * set element text values
	 * @param elm is the element
	 * @param values is the new values of text of the element
	 */
	public static void setElementValue(Element elm, String[] values) throws Exception {
		NodeList valNodes = selectNodeList(elm.getParentNode(), elm.getNodeName());
		int i = 0;
		while (i < valNodes.getLength() && i < values.length) {
			setElementValue((Element)(valNodes.item(i)), values[i]);
			++i;
		}
		if (i == valNodes.getLength()) {	// add more values
			Document doc = elm.getOwnerDocument();
			Node parent = elm.getParentNode();
			String elmName = elm.getNodeName();
			while (i < values.length) {
				elm = doc.createElement(elmName);
				parent.appendChild(elm);
				elm.appendChild(doc.createTextNode(values[i]));
				++i;
			}
		} else {	// remove more old values
			Node parent = elm.getParentNode();
			while (i < valNodes.getLength()) {
				parent.removeChild(valNodes.item(i));
				++i;
			}
		}
	}

	/**
	 * set element text values
	 * @param elm is the element
	 * @param values is the new values of text of the element
	 */
	public static void setElementValue(Element elm, List<String> values) throws Exception {
		String[] strValues = new String[values.size()];
		values.toArray(strValues);
		setElementValue(elm, strValues);
	}

	/**
	 * set element text value
	 * @param contextNode is the context Node in which the element to be set
	 * @param elmName is the element name, or XPath of the element
	 * @param value is the value of text of the element
	 * @return the Element of the elmName
	 */
	public static Element setElementValue(Node contextNode, String elmName, String value) throws Exception {
		Element elm = (Element)selectSingleNode(contextNode, elmName);
		if (elm == null) elm = (Element)createChildPath(contextNode, elmName);
		setElementValue(elm, value);
		return(elm);
	}

	/**
	 * set element text values
	 * @param contextNode is the context Node in which the element to be set
	 * @param elmName is the element name, or Xpath of the element
	 * @param values is the values of text of the element
	 */
	public static void setElementValue(Node contextNode, String elmName, String[] values) throws Exception {
		Element elm = (Element)selectSingleNode(contextNode, elmName);
		if (elm == null) elm = (Element)createChildPath(contextNode, elmName);
		setElementValue(elm, values);
	}

	/**
	 * set element text values
	 * @param contextNode is the context Node in which the element to be set
	 * @param elmName is the element name
	 * @param values is the values of text of the element
	 */
	public static void setElementValue(Node contextNode, String elmName, List<String> values) throws Exception {
		Element elm = (Element)selectSingleNode(contextNode, elmName);
		if (elm == null) elm = (Element)createChildPath(contextNode, elmName);
		setElementValue(elm, values);
	}

	/**
	 * select a Node list in context Node, namespace is from contextNode.
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @return a Node list
	 */
	public static NodeList selectNodeList(Node contextNode, String childNodePath) throws Exception {
		return(XPathAPI.selectNodeList(contextNode, childNodePath));
	}

	/**
	 * select a Node list in context Node
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @param namespaceNode is the name space node
	 * @return a Node list
	 */
	public static NodeList selectNodeList(Node contextNode, String childNodePath, Node namespaceNode) throws Exception {
		return(XPathAPI.selectNodeList(contextNode, childNodePath, namespaceNode));
	}

	/**
	 * select a single Node in context Node, namespace is from contextNode.
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @return the first Node found in the context node
	 */
	public static Node selectSingleNode(Node contextNode, String childNodePath) throws Exception {
		return(XPathAPI.selectSingleNode(contextNode, childNodePath));
	}

	/**
	 * select a single Node in context Node
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @param namespaceNode is the name space node
	 * @return the first Node found in the context node
	 */
	public static Node selectSingleNode(Node contextNode, String childNodePath, Node namespaceNode) throws Exception {
		return(XPathAPI.selectSingleNode(contextNode, childNodePath, namespaceNode));
	}

	/**
	 * select a Node list in context Node, namespace is from contextNode.
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @return a Node list as NodeIterator
	 */
	public static NodeIterator selectNodeIterator(Node contextNode, String childNodePath) throws Exception {
		return(XPathAPI.selectNodeIterator(contextNode, childNodePath));
	}

	/**
	 * select a Node list in context Node
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @param namespaceNode is the name space node
	 * @return a Node list as NodeIterator
	 */
	public static NodeIterator selectNodeIterator(Node contextNode, String childNodePath, Node namespaceNode) throws Exception {
		return(XPathAPI.selectNodeIterator(contextNode, childNodePath, namespaceNode));
	}

	/**
	 * check if a child node exists in a context node, namespace is from contextNode.
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @return true if it exists.
	 */
	public static boolean doesNodeExist(Node contextNode, String childNodePath) {
		try {
			return(selectSingleNode(contextNode, childNodePath) != null);
		} catch (Throwable e) {
			logger.error("error", e);
			return(false);
		}
	}
	/**
	 * check if a child node exists in a context node.
	 * @param contextNode is the context Node in which to select child nodes
	 * @param childNodePath is the child node XPath
	 * @param namespaceNode is the name space node
	 * @return true if it exists.
	 */
	public static boolean doesNodeExist(Node contextNode, String childNodePath, Node namespaceNode) {
		try {
			return(selectSingleNode(contextNode, childNodePath, namespaceNode) != null);
		} catch (Throwable e) {
			logger.error("error", e);
			return(false);
		}
	}

	/**
	 * get a single value of the text node of a element
	 * @param contextNode is the context Node
	 * @param paramPath is the Xpath of the element in the context
	 * @return the value of the element in String
	 */
	public static String getSingleValue(Node contextNode, String elmPath) {
		if (contextNode == null || elmPath == null) return(null);
		try {
			Node nd = selectSingleNode(contextNode, elmPath + "/text()");
			return(nd == null ? null : nd.getNodeValue());
		} catch (Throwable e) {
			logger.error("error", e);
			return(null);
		}
	}

	/**
	 * get a list of values of elements with same tag name
	 * @param contextNode is the context Node
	 * @param elmPath is the Xpath of the elements in the context
	 * @return a String List of values of the elements
	 */
	public static List<String> getValues(Node contextNode, String elmPath) {
		if (contextNode == null || elmPath == null) return(null);
		ArrayList<String> values = new ArrayList<String>();
		try {
			NodeList nodeList = selectNodeList(contextNode, elmPath);
			if (nodeList != null) {
				for (int i = 0; i < nodeList.getLength(); ++i) {
					Node nd = selectSingleNode(nodeList.item(i), "text()");
					if (nd != null) {
						String val = nd.getNodeValue();
						if (val != null) values.add(val);
					}
				}
			}
			return(values);
		} catch (Throwable e) {
			logger.error("error", e);
			return(null);
		}
	}

	/**
	 * get attribute value of node
	 * @param contextNode is the context Node
	 * @param childNodePath is the Xpath of the child Node in the context
	 * @param attrName is the attribute name of the child node
	 * @return the value of the attribute in String
	 */
	public static String getAttributeValue(Node contextNode, String childNodePath, String attrName) {
		if (contextNode == null || childNodePath == null || attrName == null) return(null);
		try {
			Element nd = (Element)selectSingleNode(contextNode, childNodePath);
			return(nd == null ? null : nd.getAttribute(attrName));
		} catch (Throwable e) {
			logger.error("error", e);
			return(null);
		}
	}

	/**
	 * set attribute value of element
	 * @param elm is the Element
	 * @param attrName is the attribute name of the child node
	 * @param attrValue is the attribute value. if it is null, then remove this attribute.
	 */
	public static void setAttributeValue(Element elm, String attrName, String attrValue) throws Exception {
		if (elm == null || attrName == null) return;
		try {
			if (attrValue == null) {	// remove this attribute
				elm.removeAttribute(attrName);
				return;
			}
			elm.setAttribute(attrName, attrValue);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * set attribute value of node
	 * @param contextNode is the context Node
	 * @param childNodePath is the Xpath of the child Node in the context
	 * @param attrName is the attribute name of the child node
	 * @param attrValue is the attribute value. if it is null, then remove this attribute.
	 */
	public static void setAttributeValue(Node contextNode, String childNodePath, String attrName, String attrValue) throws Exception {
		if (contextNode == null || childNodePath == null || attrName == null) return;
		try {
			if (attrValue == null) {	// remove this attribute
				removeAttribute(contextNode, childNodePath, attrName);
				return;
			}
			Element elm = (Element)selectSingleNode(contextNode, childNodePath);
			if (elm == null) elm = (Element)createChildPath(contextNode, childNodePath);
			elm.setAttribute(attrName, attrValue);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * remove attribute of node
	 * @param contextNode is the context Node
	 * @param childNodePath is the Xpath of the child Node in the context
	 * @param attrName is the attribute name of the child node
	 */
	public static void removeAttribute(Node contextNode, String childNodePath, String attrName) throws Exception {
		if (contextNode == null || childNodePath == null || attrName == null) return;
		try {
			Element nd = (Element)selectSingleNode(contextNode, childNodePath);
			if (nd != null) nd.removeAttribute(attrName);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * remove attribute of node
	 * @param node is the Node (Element)
	 * @param attrName is the attribute name
	 */
	public static void removeAttribute(Node node, String attrName) throws Exception {
		if (node == null || attrName == null) return;
		try {
			((Element)node).removeAttribute(attrName);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML String into DOM with given XSL
	 * @param xmlString is the XML String
	 * @param xslSystemID is the XSL System URI
	 * @return a DOM object
	 * @exception Exception
	 */
	public static Document transformToDOM(String xmlString, String xslSystemID) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			Document doc = createDocument();
			transformToResult(xmlString, xslSystemID, new javax.xml.transform.dom.DOMResult(doc));
			return(doc);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Stream into DOM with given XSL
	 * @param xmlStream is the XML Stream in UTF-8 encoding bytes
	 * @param xslSystemID is the XSL System URI
	 * @return a DOM object
	 * @exception Exception
	 */
	public static Document transformToDOM(InputStream xmlStream, String xslSystemID) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			Document doc = createDocument();
			transformToResult(xmlStream, xslSystemID, new javax.xml.transform.dom.DOMResult(doc));
			return(doc);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Reader into DOM with given XSL
	 * @param xmlReader is the XML characters Reader
	 * @param xslSystemID is the XSL System URI
	 * @return a DOM object
	 * @exception Exception
	 */
	public static Document transformToDOM(Reader xmlReader, String xslSystemID) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			Document doc = createDocument();
			transformToResult(xmlReader, xslSystemID, new javax.xml.transform.dom.DOMResult(doc));
			return(doc);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform a Node into DOM with given XSL
	 * @param xmlNode is the Node
	 * @param xslSystemID is the XSL System URI
	 * @return a DOM object
	 * @exception Exception
	 */
	public static Document transformToDOM(Node xmlNode, String xslSystemID) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			Document doc = createDocument();
			transformToResult(xmlNode, xslSystemID, new javax.xml.transform.dom.DOMResult(doc));
			return(doc);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML String into a Writer with given XSL
	 * @param xmlString is the XML String
	 * @param xslSystemID is the XSL System URI
	 * @param outputWriter is the character output writer. (writer operates on char)
	 * @exception Exception
	 */
	public static void transformToWriter(String xmlString, String xslSystemID, Writer outputWriter) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlString, xslSystemID, new javax.xml.transform.stream.StreamResult(outputWriter));
			outputWriter.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Stream into a Writer with given XSL
	 * @param xmlStream is the XML Stream in UTF-8 encoding bytes
	 * @param xslSystemID is the XSL System URI
	 * @param outputWriter is the character output writer. (writer operates on char)
	 * @exception Exception
	 */
	public static void transformToWriter(InputStream xmlStream, String xslSystemID, Writer outputWriter) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlStream, xslSystemID, new javax.xml.transform.stream.StreamResult(outputWriter));
			outputWriter.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Reader into a Writer with given XSL
	 * @param xmlReader is the XML characters Reader
	 * @param xslSystemID is the XSL System URI
	 * @param outputWriter is the character output writer. (writer operates on char)
	 * @exception Exception
	 */
	public static void transformToWriter(Reader xmlReader, String xslSystemID, Writer outputWriter) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlReader, xslSystemID, new javax.xml.transform.stream.StreamResult(outputWriter));
			outputWriter.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform a Node into a Writer with given XSL
	 * @param xmlNode is the Node
	 * @param xslSystemID is the XSL System URI
	 * @param outputWriter is the output writer. (writer operates on char)
	 * @exception Exception
	 */
	public static void transformToWriter(Node xmlNode, String xslSystemID, Writer outputWriter) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlNode, xslSystemID, new javax.xml.transform.stream.StreamResult(outputWriter));
			outputWriter.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML String into an OutputStream in UTF-8 bytes with given XSL
	 * @param xmlString is the XML String
	 * @param xslSystemID is the XSL System URI
	 * @param outputStream is the output stream to which the UTF-8 bytes will be written.
	 * @exception Exception
	 */
	public static void transformToStream(String xmlString, String xslSystemID, OutputStream outputStream) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlString, xslSystemID, new javax.xml.transform.stream.StreamResult(outputStream));
			outputStream.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Stream into an OutputStream in UTF-8 bytes with given XSL
	 * @param xmlStream is the XML InputStream in UTF-8 encoding bytes
	 * @param xslSystemID is the XSL System URI
	 * @param outputStream is the output stream to which the UTF-8 bytes will be written.
	 * @exception Exception
	 */
	public static void transformToStream(InputStream xmlStream, String xslSystemID, OutputStream outputStream) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlStream, xslSystemID, new javax.xml.transform.stream.StreamResult(outputStream));
			outputStream.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Reader into an OutputStream in UTF-8 bytes with given XSL
	 * @param xmlReader is the XML characters Reader
	 * @param xslSystemID is the XSL System URI
	 * @param outputStream is the output stream to which the UTF-8 bytes will be written.
	 * @exception Exception
	 */
	public static void transformToStream(Reader xmlReader, String xslSystemID, OutputStream outputStream) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlReader, xslSystemID, new javax.xml.transform.stream.StreamResult(outputStream));
			outputStream.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform a Node into an OutputStream in UTF-8 bytes with given XSL
	 * @param xmlNode is the Node
	 * @param xslSystemID is the XSL System URI
	 * @param outputStream is the output stream to which the UTF-8 bytes will be written.
	 * @exception Exception
	 */
	public static void transformToStream(Node xmlNode, String xslSystemID, OutputStream outputStream) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			transformToResult(xmlNode, xslSystemID, new javax.xml.transform.stream.StreamResult(outputStream));
			outputStream.flush();
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML String into a String with given XSL
	 * @param xmlString is the XML String
	 * @param xslSystemID is the XSL System URI
	 * @return output String
	 * @exception Exception
	 */
	public static String transformToString(String xmlString, String xslSystemID) throws Exception {
		try {
			StringWriter writer = new StringWriter();
			transformToWriter(xmlString, xslSystemID, writer);
			return(writer.getBuffer().toString());
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Stream into a String with given XSL
	 * @param xmlStream is the XML Stream in UTF-8 encoding bytes
	 * @param xslSystemID is the XSL System URI
	 * @return output String
	 * @exception Exception
	 */
	public static String transformToString(InputStream xmlStream, String xslSystemID) throws Exception {
		try {
			StringWriter writer = new StringWriter();
			transformToWriter(xmlStream, xslSystemID, writer);
			return(writer.getBuffer().toString());
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML Reader into a String with given XSL
	 * @param xmlReader is the XML Reader
	 * @param xslSystemID is the XSL System URI
	 * @return output String
	 * @exception Exception
	 */
	public static String transformToString(Reader xmlReader, String xslSystemID) throws Exception {
		try {
			StringWriter writer = new StringWriter();
			transformToWriter(xmlReader, xslSystemID, writer);
			return(writer.getBuffer().toString());
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform a Node into a String with given XSL
	 * @param xmlNode is the Node
	 * @param xslSystemID is the XSL System URI
	 * @return output String
	 * @exception Exception
	 */
	public static String transformToString(Node xmlNode, String xslSystemID) throws Exception {
		try {
			StringWriter writer = new StringWriter();
			transformToWriter(xmlNode, xslSystemID, writer);
			return(writer.getBuffer().toString());
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * transform an XML String into a Result with given XSL
	 * @param xmlString is the XML String
	 * @param xslSystemID is the XSL System URI
	 * @param result is the Result
	 * @exception Exception
	 */
	private static void transformToResult(String xmlString, String xslSystemID, javax.xml.transform.Result result) throws Exception {
		transformToResult((new javax.xml.transform.stream.StreamSource(new StringReader(xmlString))), xslSystemID, result);
	}

	/**
	 * transform an XML Stream into a Result with given XSL
	 * @param xmlStream is the XML InputStream in UTF-8 bytes
	 * @param xslSystemID is the XSL System URI
	 * @param result is the Result
	 * @exception Exception
	 */
	private static void transformToResult(InputStream xmlStream, String xslSystemID, javax.xml.transform.Result result) throws Exception {
		transformToResult((new javax.xml.transform.stream.StreamSource(xmlStream)), xslSystemID, result);
	}

	/**
	 * transform an XML Reader into a Result with given XSL
	 * @param xmlReader is the XML Reader. (Reader operates on char)
	 * @param xslSystemID is the XSL System URI
	 * @param result is the Result
	 * @exception Exception
	 */
	private static void transformToResult(Reader xmlReader, String xslSystemID, javax.xml.transform.Result result) throws Exception {
		transformToResult((new javax.xml.transform.stream.StreamSource(xmlReader)), xslSystemID, result);
	}

	/**
	 * transform a Node into a Result with given XSL
	 * @param xmlNode is the Node, it could be Document, Element, Node, Text, etc.
	 * @param xslSystemID is the XSL System URI
	 * @param result is the Result
	 * @exception Exception
	 */
	private static void transformToResult(Node xmlNode, String xslSystemID, javax.xml.transform.Result result) throws Exception {
		transformToResult((new javax.xml.transform.dom.DOMSource(xmlNode)), xslSystemID, result);
	}

	/**
	 * transform a Source into a Result with given XSL
	 * @param source is the Source.
	 * @param xslSystemID is the XSL System URI
	 * @param result is the Result
	 * @exception Exception
	 */
	private static void transformToResult(javax.xml.transform.Source source, String xslSystemID, javax.xml.transform.Result result) throws Exception {
		try {
			// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
			javax.xml.transform.Transformer transformer = createTransformer(xslSystemID);
			transformer.transform(source, result);
		} catch (Exception e) {
			logger.error("error", e);
			throw e;
		}
	}

	/**
	 * create a transformer of the given XSL.
	 * @param xslSystemID is the XSL system ID.
	 *    if it is null, then return a copy transformer.
	 *    if it doesn't start with a valid URL protocol(such as http:, file:, etc), then it is treated as a resource reachable in classpath.
	 * @return a transformer
	 * @exception Exception
	 */
	private static javax.xml.transform.Transformer createTransformer(String xslSystemID) throws Exception {
		// JAXP, Apache xalan 2.0.1 and xerces 1.3 (note: xalan 2.0.0 has some bugs)
		if (xslSystemID == null) {	// get a transformer to copy source to result (DOM, stream, or for serialization)
			return(transformerFactory.newTransformer());
		}
		javax.xml.transform.Templates templates = (javax.xml.transform.Templates)transformerTemplates.get(xslSystemID);
		if (templates == null) {
			if (logger.isDebugEnabled()) logger.debug("getting XSL source for " + xslSystemID + " ...");
			javax.xml.transform.stream.StreamSource xslSrc = null;
			InputStream xslIS = null;
			if (isValidUrl(xslSystemID)) {	// it is a URL
				xslSrc = new javax.xml.transform.stream.StreamSource(xslSystemID);
			} else {	// it is a resource reachable in classpath
				ClassLoader clsLoader = XmlUtility.class.getClassLoader();
				if (clsLoader == null) xslIS = ClassLoader.getSystemResourceAsStream(xslSystemID);
				else xslIS = clsLoader.getResourceAsStream(xslSystemID);
				xslSrc = new javax.xml.transform.stream.StreamSource(xslIS);
			}
			if (logger.isDebugEnabled()) logger.debug("getting XSL template for " + xslSystemID + " ...");
			synchronized(transformerFactory) {
				templates = transformerFactory.newTemplates(xslSrc);
			}
			if (isTemplatesCacheOn) {	// put templates into cache
				synchronized(transformerTemplates) {
					Object tmp = transformerTemplates.get(xslSystemID);
					if (tmp == null) {
						transformerTemplates.put(xslSystemID, templates);
						if (logger.isDebugEnabled()) logger.debug("XSL template " + xslSystemID + " is added into cache");
					} else {
						templates = (javax.xml.transform.Templates)tmp;
					}
				}
			}
			if (xslIS != null) try { xslIS.close(); } catch (Exception e) {}
		}
		return(templates.newTransformer());
	}

	/**
	 * check if a string is a valid whole URL that starts with valid protocol.
	 */
	protected static boolean isValidUrl(String str) {
		if (str == null) return(false);
		for (int i = 0; i < urlProtocols.length; ++i)
			if (str.startsWith(urlProtocols[i])) return(true);
		return(false);
	}
	//---------------------------------------------------------------------------------------

	//---------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------
	/**
	 * parse an xml string in format <br>
	 * <pre>
	 * &lt;params&gt;
	 *   &lt;param name="p1Name" isPassword="true"&gt;{xor}p1ValueEncoded&lt;/param&gt;
	 *   &lt;param name="p2Name" type="java.lang.Double"&gt;p2Value&lt;/param&gt;
	 *   &lt;param name="p3Name" type="java.util.Date" format="yyyy-MM-dd"&gt;p3Value&lt;/param&gt;
	 * &lt;/params&gt;
	 * </pre> <br>
	 * It returns a HashMap containing the parameter name/value pairs. <br>
	 * It doesn't allow multiple values for same name.
	 *
	 * @param strParams
	 * @return HashMap the parameter name/values. null if there are no any parameters.
	 * @throws Exception
	 */
	public static HashMap<String, Object> parseParams(String strParams) throws Exception {
		return(parseProperties(strParams, "param"));
	}

	/**
	 * parse an xml string in format <br>
	 * <pre>
	 * &lt;parameters&gt;
	 *   &lt;parameter name="p1Name" type="java.lang.Long"&gt;p1Value&lt;/parameter&gt;
	 *   &lt;parameter name="p2Name" type="java.util.Date" format="yyyy-MM-dd"&gt;p2Value&lt;/parameter&gt;
	 *   &lt;parameter name="p3Name" isPassword="true"&gt;{xor}p3ValueEncoded&lt;/parameter&gt;
	 * &lt;/parameters&gt;
	 * </pre> <br>
	 * It returns a HashMap containing the parameter name/value pairs. <br>
	 * It doesn't allow multiple values for same name.
	 *
	 * @param strParameters
	 * @return HashMap the parameter name/values. null if there are no any parameters.
	 * @throws Exception
	 */
	public static HashMap<String, Object> parseParameters(String strParameters) throws Exception {
		return(parseProperties(strParameters, "parameter"));
	}

	/**
	 * parse an xml string in format <br>
	 * <pre>
	 * &lt;props&gt;
	 *   &lt;prop name="p1Name" type="java.lang.Double"&gt;p1Value&lt;/prop&gt;
	 *   &lt;prop name="p2Name" type="java.util.Date" format="yyyy-MM-dd"&gt;p2Value&lt;/prop&gt;
	 *   &lt;prop name="p3Name" isPassword="true"&gt;{xor}p3ValueEncoded&lt;/prop&gt;
	 * &lt;/props&gt;
	 * </pre> <br>
	 * It returns a HashMap containing the property name/value pairs. <br>
	 * It doesn't allow multiple values for same name.
	 *
	 * @param strProps
	 * @return HashMap the property name/values. null if there are no any properties.
	 * @throws Exception
	 */
	public static HashMap<String, Object> parseProps(String strProps) throws Exception {
		return(parseProperties(strProps, "prop"));
	}

	/**
	 * parse an xml string in format <br>
	 * <pre>
	 * &lt;properties&gt;
	 *   &lt;property name="p1Name" type="java.lang.Long"&gt;p1Value&lt;/property&gt;
	 *   &lt;property name="p2Name" type="java.util.Date" format="yyyy-MM-dd"&gt;p2Value&lt;/property&gt;
	 *   &lt;property name="p3Name" isPassword="true"&gt;{xor}p3ValueEncoded&lt;/property&gt;
	 * &lt;/properties&gt;
	 * </pre> <br>
	 * It returns a HashMap containing the property name/value pairs. <br>
	 * It doesn't allow multiple values for same name.
	 *
	 * @param strProperties
	 * @return HashMap the property name/values. null if there are no any properties.
	 * @throws Exception
	 */
	public static HashMap<String, Object> parseProperties(String strProperties) throws Exception {
		return(parseProperties(strProperties, "property"));
	}

	/**
	 * parse an xml string in format <br>
	 * <pre>
	 * &lt;tagNames&gt;
	 *   &lt;tagName name="p1Name" type="java.lang.Long"&gt;p1Value&lt;/tagName&gt;
	 *   &lt;tagName name="p2Name" type="java.util.Date" format="yyyy-MM-dd"&gt;p2Value&lt;/tagName&gt;
	 *   &lt;tagName name="p3Name" isPassword="true"&gt;{xor}p3ValueEncoded&lt;/tagName&gt;
	 * &lt;/tagNames&gt;
	 * </pre> <br>
	 * It returns a HashMap containing the properties name/value pairs.
	 * The "tagNames" and "tagName" could be any valid xml tag name. "tagName" could be "prop", "property", "param", "parameter", "attribute", etc. <br>
	 * It doesn't allow multiple values for same name.
	 *
	 * @param strProps
	 * @param tagName the tag name
	 * @return HashMap the property name/values. null if there are no any properties.
	 * @throws Exception
	 */
	public static HashMap<String, Object> parseProperties(String strProps, String tagName) throws Exception {
		if (strProps == null || tagName == null || strProps.length() == 0) return(null);
		Document doc = XmlUtility.stringToDocument(strProps);
		if (doc == null) return(null);
		Element docElm = doc.getDocumentElement();
		return(parseProperties(docElm, tagName));
	}

	/**
	 * parse an xml element in format <br>
	 * <pre>
	 * &lt;tagNames type="java.util.Date" format="yyyy-MM-dd"&gt;
	 *   &lt;tagName name="p1Name" isPassword="true"&gt;{xor}p1ValueEncoded&lt;/tagName&gt;
	 *   &lt;tagName name="p2Name" type="java.lang.Double"&gt;p2Value&lt;/tagName&gt;
	 *   &lt;tagName name="p3Name" format="yyyy-MM-dd HH:mm:ss"&gt;p3Value&lt;/tagName&gt;
	 * &lt;/tagNames&gt;
	 * </pre> <br>
	 * type is full java class name of the value type. see method parseStringToObject() for the list of supported types<br>
	 * <br>
	 * format could be SimpleDateFormat allowed standard formats. <br>
	 * <br>
	 * type and format in tagNames is the default type and format for all items.
	 * <br>
	 * isPassword=true, then the value is encoded using PwdEncoder. <br>
	 * <br>
	 * It returns a HashMap containing the properties name/value pairs.
	 * The "tagNames" and "tagName" could be any valid xml tag name. "tagName" could be "prop", "property", "param", "parameter", "attribute", etc.  <br>
	 * It doesn't allow multiple values for same name.
	 *
	 * @param ctx the Element containing all tagName.
	 * @param tagName the tag name
	 * @return HashMap the property name/values. null if there are no any properties.
	 * @throws Exception
	 */
	public static HashMap<String, Object> parseProperties(Element ctx, String tagName) throws Exception {
		if (ctx == null || tagName == null) return(null);
		NodeList nl = XmlUtility.selectNodeList(ctx, tagName);
		if (nl == null || nl.getLength() == 0) return(null);

		String typeClass = ctx.getAttribute("type");
		if (typeClass != null && typeClass.length() == 0) typeClass = null;
		String format = ctx.getAttribute("format");
		if (format != null && format.length() == 0) format = null;
		
		HashMap<String, Object> props = new HashMap<String, Object>();
		for (int i = 0; i < nl.getLength(); ++i) {
			Element propElm = (Element) nl.item(i);
			String propName = propElm.getAttribute("name");
			if (propName != null && propName.length() > 0) {
				Object propValue = XmlUtility.getSingleValue(propElm, ".");
				if (propValue == null) propValue = "";
				// if isPassword=true, then try to decode the encoded value
				if (Boolean.valueOf(propElm.getAttribute("isPassword")).booleanValue()) {
					if (PwdEncoder.isEncoded((String) propValue))
						propValue = new String(PwdEncoder.decode((String) propValue));
				}

				String propTypeClass = propElm.getAttribute("type");
				if (propTypeClass == null || propTypeClass.length() == 0) propTypeClass = typeClass;
				String propFormat = propElm.getAttribute("format");
				if (propFormat == null || propFormat.length() == 0) propFormat = format;
				// parse value to given Object
				if (propTypeClass != null) propValue = parseStringToObject((String) propValue, propTypeClass, propFormat);
				
				props.put(propName, propValue);
			}
		}
		if (props.size() == 0) props = null;

		return(props);
	}

	/**
	 * parse a string to an Object of given data type. <br>
	 * @param strValue the string value
	 * @param valueType the full java class name which could be <br>
	 * java.util.Date   (default format is yyyy-MM-dd) <br>
	 * java.sql.Date   (default format is yyyy-MM-dd) <br>
	 * java.sql.Timestamp   (default format is yyyy-MM-dd HH:mm:ss) <br>
	 * java.sql.Time   (default format is HH:mm:ss) <br>
	 * java.lang.String   <br>
	 * java.lang.Boolean   <br>
	 * java.lang.Double   <br>
	 * java.lang.Long   <br>
	 * java.lang.Short   <br>
	 * java.lang.Integer   <br>
	 * java.lang.Byte   <br>
	 * java.lang.Float   <br>
	 * java.math.BigInteger   <br>
	 * java.math.BigDecimal   <br>
	 * 
	 * @param format SimpleDateFormat allowed standard formats.
	 * @return Object
	 * @throws Exception
	 */
	public static Object parseStringToObject(String strValue, String valueType, String format) throws Exception {
		if ("java.util.Date".equals(valueType)) {
			// default format yyyy-MM-dd
			DateFormat fmt = new SimpleDateFormat(format != null ? format : "yyyy-MM-dd");
			return(fmt.parse(strValue));
		} else if ("java.sql.Date".equals(valueType)) {
			// default format yyyy-MM-dd
			DateFormat fmt = new SimpleDateFormat(format != null ? format : "yyyy-MM-dd");
			return(new java.sql.Date(fmt.parse(strValue).getTime()));
		} else if ("java.sql.Timestamp".equals(valueType)) {
			// default format yyyy-MM-dd HH:mm:ss
			DateFormat fmt = new SimpleDateFormat(format != null ? format : "yyyy-MM-dd HH:mm:ss");
			return(new java.sql.Timestamp(fmt.parse(strValue).getTime()));
		} else if ("java.sql.Time".equals(valueType)) {
			// default format HH:mm:ss
			DateFormat fmt = new SimpleDateFormat(format != null ? format : "HH:mm:ss");
			return(new java.sql.Time(fmt.parse(strValue).getTime()));
		} else if ("java.lang.Boolean".equals(valueType)) {
			return(Boolean.valueOf(strValue));
		} else if ("java.lang.Double".equals(valueType)) {
			return(Double.valueOf(strValue));
		} else if ("java.lang.Long".equals(valueType)) {
			return(Long.valueOf(strValue));
		} else if ("java.lang.Short".equals(valueType)) {
			return(Short.valueOf(strValue));
		} else if ("java.lang.Integer".equals(valueType)) {
			return(Integer.valueOf(strValue));
		} else if ("java.lang.Byte".equals(valueType)) {
			return(Byte.valueOf(strValue));
		} else if ("java.lang.Float".equals(valueType)) {
			return(Float.valueOf(strValue));
		} else if ("java.math.BigInteger".equals(valueType)) {
			return(new java.math.BigInteger(strValue));
		} else if ("java.math.BigDecimal".equals(valueType)) {
			return(new java.math.BigDecimal(strValue));
		} else {
			return(strValue);
		}
	}

	/**
	 * parse deep properties with nested properties <br>
	 * <pre>
	 * &lt;ctx&gt;
	 *   &lt;tagName name="p1Name" type="java.lang.Long"&gt;p1Value&lt;/tagName&gt;
	 *   &lt;tagName name="p2Name" isPassword="true"&gt;{xor}p2ValueEncoded&lt;/tagName&gt;
	 *   &lt;tagName name="p3Name" type="java.util.Date" format="yyyy-MM-dd"&gt;
	 *     &lt;tagName name="p31Name"&gt;p31Value&lt;/tagName&gt;
	 *   &lt;/tagName&gt;
	 * &lt;/ctx&gt;
	 * </pre>
	 * type is full java class name of the value type. see method parseStringToObject() for the list of supported types<br>
	 * <br>
	 * format could be SimpleDateFormat allowed standard formats. <br>
	 * <br>
	 * type and format in an element is also the default type and format for all its child elements.
	 * <br>
	 * isPassword=true, then the value is encoded using PwdEncoder. <br>
	 * <br>
	 * It allows multiple values for same property name.
	 *
	 * @param ctx is the Node containing the tagName tags
	 * @return HashMap of properties. it is (name, value) pair, where value could be String, ArrayList or HashMap.
	 */
	public static HashMap<String, Object> parseDeepProperties(String strProps, String tagName) throws Exception {
		if (strProps == null || tagName == null || strProps.length() == 0) return(null);
		Document doc = XmlUtility.stringToDocument(strProps);
		if (doc == null) return(null);
		Element docElm = doc.getDocumentElement();
		return(parseDeepProperties(docElm, tagName));
	}

	/**
	 * parse deep properties with nested properties <br>
	 * <pre>
	 * &lt;ctx&gt;
	 *   &lt;tagName name="p1Name" type="java.lang.Long"&gt;p1Value&lt;/tagName&gt;
	 *   &lt;tagName name="p2Name" isPassword="true"&gt;{xor}p2ValueEncoded&lt;/tagName&gt;
	 *   &lt;tagName name="p3Name" type="java.util.Date" format="yyyy-MM-dd"&gt;
	 *     &lt;tagName name="p31Name"&gt;p31Value&lt;/tagName&gt;
	 *   &lt;/tagName&gt;
	 * &lt;/ctx&gt;
	 * </pre>
	 * type is full java class name of the value type. see method parseStringToObject() for the list of supported types<br>
	 * <br>
	 * format could be SimpleDateFormat allowed standard formats. <br>
	 * <br>
	 * type and format in an element is also the default type and format for all its child elements.
	 * <br>
	 * isPassword=true, then the value is encoded using PwdEncoder. <br>
	 * <br>
	 * It allows multiple values for same property name.
	 *
	 * @param ctx is the Node containing the tagName tags
	 * @return HashMap of properties. it is (name, value) pair, where value could be String, ArrayList or HashMap.
	 */
	public static HashMap<String, Object> parseDeepProperties(Element ctx, String tagName) throws Exception {
		if (ctx == null || tagName == null) return(null);
		NodeList nl = XmlUtility.selectNodeList(ctx, tagName);
		if (nl == null || nl.getLength() == 0) return(null);
		
		String typeClass = ctx.getAttribute("type");
		if (typeClass != null && typeClass.length() == 0) typeClass = null;
		String format = ctx.getAttribute("format");
		if (format != null && format.length() == 0) format = null;
		
		HashMap<String, Object> props = new HashMap<String, Object>();
		for (int i = 0; i < nl.getLength(); ++i) {
			Element propElm = (Element) nl.item(i);
			String propName = propElm.getAttribute("name");
			if (propName != null && propName.length() > 0) {
				Object val = parseDeepProperties(propElm, tagName);	// HashMap
				if (val == null) {	// leaf value (there are no child properties)
					val = XmlUtility.getSingleValue(propElm, ".");	// string value
					if (val == null) val = "";
					// if isPassword=true, then try to decode the encoded value
					if (Boolean.valueOf(propElm.getAttribute("isPassword")).booleanValue()) {
						if (PwdEncoder.isEncoded((String) val))
							val = new String(PwdEncoder.decode((String) val));
					}
					
					String propTypeClass = propElm.getAttribute("type");
					if (propTypeClass == null || propTypeClass.length() == 0) propTypeClass = typeClass;
					String propFormat = propElm.getAttribute("format");
					if (propFormat == null || propFormat.length() == 0) propFormat = format;
					// parse value to given Object
					if (propTypeClass != null) val = parseStringToObject((String) val, propTypeClass, propFormat);
				}
				Object oldVal = props.get(propName);
				if (oldVal == null) {	// this is the first value for this name
					props.put(propName, val);
				} else {	// already has value (multiple values)
					if (oldVal instanceof ArrayList<?>) {	// add to existing list
						((ArrayList<Object>)oldVal).add(val);
					} else {	// create list
						ArrayList<Object> lv = new ArrayList<Object>();
						lv.add(oldVal);
						lv.add(val);
						props.put(propName, lv);
					}
				}
			}
		}
		return(props.size() > 0 ? props : null);
	}

}
