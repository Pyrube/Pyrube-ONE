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

package com.pyrube.one.app.menu;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.config.ConfigManager;
import com.pyrube.one.app.config.Configurator;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * Application Menu structure configurator.
 * <pre>
 * <![CDATA[
 *   <MenuConfig>
 *     <items>
 *       <item>menu.item1
 *         <item action="/action11" mode="forward" queryString="param11=param11Value" icon="icon11" access="hasAuthority('right11')">menu.item11</item>
 *         <item action="/action12" mode="redirect" icon="icon12" access="hasAnyAuthority('right121', 'right122', 'right123')">menu.item12</item>
 *         <item separator="true"/>
 *         <item action="/action13" mode="openup" queryString="param13=param13Value" icon="icon13" access="hasAuthority('right13')">menu.item13</item>
 *         <item href="javascript:myJsFunc()" icon="icon14" >menu.item14</item>
 *         <item separator="true"/>
 *         <item>menu.item16
 *           <item action="/action161" icon="icon161" access="hasAuthority('right161')">menu.item161</item>
 *           <item separator="true"/>
 *           <item action="/action162" mode="popup" icon="icon162" access="hasAuthority('right162')">menu.item162</item>
 *         </item>
 *       </item>
 *       <item>menu.item2
 *         <item action="/action21" queryString="param21=param21Value" access="hasAuthority('right21')">menu.item21</item>
 *         <item action="/action22" access="hasAuthority('right22')">menu.item12</item>
 *       </item>
 *       <item action="/action31" access="hasAuthority('right31')">menu.item31</item>
 *     </items>
 *   </MenuConfig>
 * ]]>
 * </pre>
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class MenuConfig extends Configurator {

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(MenuConfig.class.getName());

	/**
	 * Menu structure, a list of level-1 menu items
	 */
	private List<MenuItem> items = null;
	
	/**
	 * configurator of Menu
	 */
	private static MenuConfig menuConfig = null;
	
	/**
	 * return the Menu configurator
	 */
	public static MenuConfig getMenuConfig() {
		if (menuConfig == null) {
			synchronized(MenuConfig.class) {
				if (menuConfig == null) {
					menuConfig = (MenuConfig) getInstance("MenuConfig");
					if (menuConfig == null) logger.warn("Configurator named MenuConfig is not found. Please check configuration file.");
				}
			}
		}
		return (menuConfig);
	}
	
	/**
	 * constructor
	 */
	public MenuConfig() {
	}

	/**
	 * load configuration, which will be called by ConfigManager
	 * @param cfgName is the name for the current config path
	 * @param cfgNode is the configuration Node
	 * @exception AppException
	 * @see com.pyrube.one.app.config.Configurator#loadConfig(String, Node)
	 */
	public final void loadConfig(String cfgName, Node cfgNode) throws AppException {
		items = new ArrayList<MenuItem>();
		NodeList itemNodes = ConfigManager.getNodeList(cfgNode, "items/item");
		if (itemNodes == null) return;
		for (int i = 0; i < itemNodes.getLength(); ++i) {
			MenuItem mi = obtainItem("mi" + String.valueOf(i + 1), MenuItem.ROOT, itemNodes.item(i));
			if (mi != null) items.add(mi);
		}
		MenuItem.ROOT.setSubs(items);
	}

	/**
	 * obtain a level-1 menu item
	 */
	private MenuItem obtainItem(String id, MenuItem parent, Node itemNode) throws AppException {
		if (itemNode == null || itemNode.getNodeType() != Node.ELEMENT_NODE) return(null);
		String label = ConfigManager.getSingleValue(itemNode, ".");
		String action = ConfigManager.getAttributeValue((Element)itemNode, "action");
		String mode = ConfigManager.getAttributeValue((Element)itemNode, "mode");
		String queryString = ConfigManager.getAttributeValue((Element)itemNode, "queryString");
		String icon = ConfigManager.getAttributeValue((Element)itemNode, "icon");
		String access = ConfigManager.getAttributeValue((Element)itemNode, "access");
		boolean isSeparator = Boolean.valueOf(ConfigManager.getAttributeValue((Element)itemNode, "separator")).booleanValue();
		MenuItem mi = new MenuItem(id, label);
		if (!Strings.isEmpty(action)) mi.setAction(action);
		if (!Strings.isEmpty(mode)) mi.setMode(mode);
		if (!Strings.isEmpty(queryString)) mi.setQueryString(queryString);
		if (!Strings.isEmpty(icon)) mi.setIcon(icon);
		if (!Strings.isEmpty(access)) mi.setAccess(access);
		mi.setSeparator(isSeparator);
		mi.setParent(parent);
		
		// handle sub-menu items
		List<MenuItem> subs = null;
		NodeList subitemNodes = ConfigManager.getNodeList(itemNode, "item");
		if (subitemNodes != null && subitemNodes.getLength() > 0) { 
			subs = new ArrayList<MenuItem>();
			for (int i = 0; i < subitemNodes.getLength(); ++i) {
				MenuItem subItem = obtainItem(id + "_" + String.valueOf(i + 1), mi, subitemNodes.item(i));
				if (subItem != null) subs.add(subItem);
			}
		}
		if (subs != null && subs.size() > 0) {
			mi.setSubs(subs);
			mi.setGroup(true);
		}
		return(mi);
	}

}
