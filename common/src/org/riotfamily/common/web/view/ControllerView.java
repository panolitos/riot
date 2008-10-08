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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

/**
 * @deprecated Because we don't really need it. 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ControllerView implements View {

	private Controller controller;
	
	private ViewResolver viewResolver;

	
	public ControllerView(Controller controller, ViewResolver viewResolver) {
		this.controller = controller;
		this.viewResolver = viewResolver;
	}

	public String getContentType() {
		return null;
	}

	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (model != null) {
			WebUtils.exposeRequestAttributes(request, model);
		}
		
		ModelAndView mv = controller.handleRequest(request, response);
		if (mv != null) {
			View view;
			if (mv.isReference()) {
				Locale locale = RequestContextUtils.getLocale(request);
				view = viewResolver.resolveViewName(mv.getViewName(), locale);
			}
			else {
				view = mv.getView();
			}
			view.render(mv.getModel(), request, response);
		}
	}

}