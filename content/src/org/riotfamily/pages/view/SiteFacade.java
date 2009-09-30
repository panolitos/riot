/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.pages.view;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.cache.CacheTagUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.pages.model.Site;

public class SiteFacade {
	
	private Site site;

	private HttpServletRequest request;
	
	public SiteFacade(Site site, HttpServletRequest request) {
		this.site = site;
		this.request = request;
		CacheTagUtils.tag(site);
	}
		
	public String getAbsoluteUrl() {
		return makeAbsolute("/");
	}
	
	public String makeAbsolute(String path) {
		return site.makeAbsolute(request.isSecure(),
				ServletUtils.getServerNameAndPort(request), 
				request.getContextPath(), path);
	}
		
	/**
	 * @see http://freemarker.org/docs/api/freemarker/ext/beans/BeanModel.html#get(java.lang.String)
	 */
	public Object get(String key) {
		return site.getProperty(key);
	}

	@Override
	public String toString() {
		return site.toString();
	}
	
}
