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

/**
 * XPathAPI utility. because there are different versions from Apache, it just delegates
 * to different base class. Here it uses org.apache.xpath.XPathAPI which is included in xalan jar file.
 * <br>
 * Please use this utility to do XPath operations in your application. 
 * this will make it easier to upgrade the XML parser and other tools (XSLT, etc) in the future. 
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class XPathAPI extends org.apache.xpath.XPathAPI {
}
