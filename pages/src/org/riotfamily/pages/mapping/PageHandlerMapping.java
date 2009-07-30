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
package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.Site;
import org.riotfamily.website.controller.HttpErrorController;
import org.riotfamily.website.controller.RedirectController;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.util.WebUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageHandlerMapping extends AbstractHandlerMapping {

	private PageResolver pageResolver;
	
	public PageHandlerMapping(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}

	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		if (WebUtils.isIncludeRequest(request)) {
			return null;
		}
		Page page = pageResolver.getPage(request);
		String path = pageResolver.getPathWithinSite(request);
		if (page == null) {
			Site site = pageResolver.getSite(request);
			if (site == null) {
				return null;
			}
			return getPageNotFoundHandler(site, path);
		}
		
		exposePathWithinMapping(path, request);
		return page.getHandler();
	}
		
	/**
	 * Checks if an alias is registered for the given site and path and returns 
	 * a RedirectController, or <code>null</code> in case no alias can be found.
	 */
	protected Object getPageNotFoundHandler(Site site, String path) {
		try {
			PageAlias alias = PageAlias.loadBySiteAndPath(site, path);
			if (alias != null) {
				Page page = alias.getPage();
				if (page != null) {
					String url = page.getUrl();
					return new RedirectController(url, true, false);
				}
				else {
					return new HttpErrorController(HttpServletResponse.SC_GONE);
				}
			}
			return null;
		}
		catch (HibernateSystemException e) {
			// No Hibernate session bound to thread
		}
		return null;
	}
	
	/**
	 * <strong>Copied from AbstractUrlHandlerMapping</strong>
	 */
	protected void exposePathWithinMapping(String pathWithinMapping, HttpServletRequest request) {
		request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping);
	}
	
}
