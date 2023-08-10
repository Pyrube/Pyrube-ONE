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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu item information
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class MenuItem implements Serializable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 5477810421675115151L;
	
	/**
	 * the ROOT menu item
	 */
	public static MenuItem ROOT = new MenuItem(MenuItem.class.getName(), "menu.ROOT");

	/**
	 * menu id
	 */
	private String id = null;

	/**
	 * menu label to display
	 */
	private String label = null;
	
	/**
	 * action (event) for this item
	 */
	private String action = null;
	
	/**
	 * action mode for this item
	 */
	private String mode = "forward";
	
	/**
	 * additional query string of the URL associated with this item
	 */
	private String queryString = null;
	
	/**
	 * menu icon
	 */
	private String icon = null;
	
	/**
	 * menu item access control expression
	 */
	private String access = null;
	
	/**
	 * isSeparator, this item is just separator
	 */
	private boolean separator = false;
	
	/**
	 * whether menu item is a group item containing sub-menus
	 */
	private boolean group = false;

	/**
	 * the parent menu item
	 */
	private MenuItem parent = null;
	
	/**
	 * sub-menu items, it is null for leaf menu item
	 */
	private List<MenuItem> subs = null;
	
	/**
	 * Constructor for MenuItem.
	 */
	public MenuItem() {
	}
	
	/**
	 * Constructor for MenuItem.
	 * @param id the string to be identified
	 * @param label the string to be displayed
	 */
	public MenuItem(String id, String label) {
		this.setId(id);
		this.setLabel(label);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label == null ? null : label.trim();
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the access
	 */
	public String getAccess() {
		return access;
	}

	/**
	 * @param access the access to set
	 */
	public void setAccess(String access) {
		this.access = access;
	}

	/**
	 * @return the separator
	 */
	public boolean isSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(boolean separator) {
		this.separator = separator;
	}

	/**
	 * @return the group
	 */
	public boolean isGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(boolean group) {
		this.group = group;
	}

	/**
	 * @return the parent
	 */
	public MenuItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(MenuItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the subs
	 */
	public List<MenuItem> getSubs() {
		return subs;
	}

	/**
	 * @param subs the subs to set
	 */
	public void setSubs(List<MenuItem> subs) {
		this.subs = subs;
	}

	/**
	 * Add a sub-menu item
	 * @param sub the sub-menu item
	 */
	public void addSub(MenuItem sub) {
		if (subs == null) {
			if (sub.isSeparator()) {
				// the first item couldn't be separator
				return;
			} else {
				subs = new ArrayList<MenuItem>();
			}
		}
		subs.add(sub);
	}
	
	/**
	 * Find the MenuItem with a given id.
	 * @param id
	 * @return
	 */
	public MenuItem find(String id) {
		if (id.equals(this.getId())) return this;
		if (subs != null) {
			MenuItem found = null;
			for (MenuItem sub : subs) {
				found = sub.find(id);
				if (found != null) return found;
			}
		}
		return null;
	}
	
	/**
	 * return a clone
	 * @param deep boolean. if true, clone deeply
	 * @return
	 */
	public MenuItem clone(boolean deep) {
		MenuItem clone = new MenuItem();
		clone.setId(this.getId());
		clone.setLabel(this.getLabel());
		clone.setAction(this.getAction());
		clone.setMode(this.getMode());
		clone.setQueryString(this.getQueryString());
		clone.setIcon(this.getIcon());
		clone.setSeparator(this.isSeparator());
		clone.setGroup(this.isGroup());
		if (deep) {
			if (subs != null) {
				ArrayList<MenuItem> cloneSubs = new ArrayList<MenuItem>();
				for (MenuItem sub : subs) {
					cloneSubs.add(sub.clone(deep));
				}
				clone.setSubs(cloneSubs);
			}
		}
		return clone;
	}

}
