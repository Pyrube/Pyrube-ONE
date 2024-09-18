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
 *     <menu>
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
 *     </menu>
 *     <navMenu>
 *       <appNav>
 *         <item action="/nav1" icon="icon1">nav.item1</item>
 *         <item action="/nav2" icon="icon2">nav.item2</item>
 *       </appNav>
 *       <mainNav>
 *         <item action="/nav3" icon="icon3">nav.item3</item>
 *         <item action="/nav4" icon="icon4">nav.item4</item>
 *       </mainNav>
 *       <funcNav>
 *         <item name="funcname">
 *           <item action="/funcname/nav5" icon="icon5">nav.item5</item>
 *           <item action="/funcname/nav6" icon="icon6">nav.item6</item>
 *         </item>
 *       </funcNav>
 *     </navMenu>
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
		NodeList itemNodes = ConfigManager.getNodeList(cfgNode, "menu/item");
		if (itemNodes != null) {
			List<MenuItem> items = new ArrayList<MenuItem>();
			for (int i = 0; i < itemNodes.getLength(); ++i) {
				MenuItem mi = obtainItem("mi" + String.valueOf(i + 1), MenuItem.MENU_ROOT, itemNodes.item(i));
				if (mi != null) items.add(mi);
			}
			MenuItem.MENU_ROOT.setSubs(items);
		}
		// navigation menus
		itemNodes = ConfigManager.getNodeList(cfgNode, "navMenu/appNav/item");
		MenuItem APP_NAV = new MenuItem("nav_APP", "nav.APP");
		if (itemNodes != null) {
			List<MenuItem> navItems = new ArrayList<MenuItem>();
			for (int i = 0; i < itemNodes.getLength(); ++i) {
				MenuItem ni = obtainItem("ni_app_" + String.valueOf(i + 1), APP_NAV, itemNodes.item(i));
				if (ni != null) navItems.add(ni);
			}
			APP_NAV.setSubs(navItems);
		}
		MenuItem.NAV_ROOT.addSub(APP_NAV);
		itemNodes = ConfigManager.getNodeList(cfgNode, "navMenu/mainNav/item");
		MenuItem MAIN_NAV = new MenuItem("nav_MAIN", "nav.MAIN");
		if (itemNodes != null) {
			List<MenuItem> navItems = new ArrayList<MenuItem>();
			for (int i = 0; i < itemNodes.getLength(); ++i) {
				MenuItem ni = obtainItem("ni_main_" + String.valueOf(i + 1), MAIN_NAV, itemNodes.item(i));
				if (ni != null) navItems.add(ni);
			}
			MAIN_NAV.setSubs(navItems);
		}
		MenuItem.NAV_ROOT.addSub(MAIN_NAV);
		itemNodes = ConfigManager.getNodeList(cfgNode, "navMenu/funcNav/item");
		MenuItem FUNC_NAV = new MenuItem("nav_FUNC", "nav.FUNC");
		if (itemNodes != null) {
			List<MenuItem> navItems = new ArrayList<MenuItem>();
			for (int i = 0; i < itemNodes.getLength(); ++i) {
				Node itemNode = itemNodes.item(i);
				String funcname = ConfigManager.getSingleValue(itemNode, ".");
				MenuItem ni = obtainItem("ni_func_" + funcname, FUNC_NAV, itemNode);
				if (ni != null) navItems.add(ni);
			}
			FUNC_NAV.setSubs(navItems);
		}
		MenuItem.NAV_ROOT.addSub(FUNC_NAV);
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
