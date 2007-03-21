/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages;

import java.io.Serializable;
import java.util.Locale;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PathAndLocale implements Serializable {

	private String path;
	
	private Locale locale;

	public PathAndLocale() {
	}

	public PathAndLocale(String path, Locale locale) {
		this.path = path;
		this.locale = locale;
	}
	
	public PathAndLocale(Page page) {
		this.path = page.getPath();
		this.locale = page.getLocale();
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String toString() {
		return locale + ":" + path;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof PathAndLocale) {
			return toString().equals(obj.toString());
		}
		return false;
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
}
