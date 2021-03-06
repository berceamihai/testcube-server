/**
 * TestCube is an enterprise Test management tool.
 * Copyright (C) 2011 JatakaSource Ltd.
 *
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TestCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TestCube.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jatakasource.testcube.web.controller.security;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jatakasource.common.model.security.IUser;
import org.jatakasource.common.svc.CRUDService;
import org.jatakasource.testcube.model.security.User;
import org.jatakasource.testcube.svc.security.UserService;
import org.jatakasource.testcube.web.controller.ApplicationMessagesController;
import org.jatakasource.testcube.web.xml.users.UserRendered;
import org.jatakasource.testcube.web.xml.users.UserRenderedList;
import org.jatakasource.web.viewer.LogoutViewer;
import org.jatakasource.web.xml.rendered.KeywordParameterRendered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController extends ApplicationMessagesController<IUser, Long> {
	private static final Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = PROTECTED_SERVICE + "/user/get")
	protected ModelAndView getCurrentUser() {
		logger.trace("Request for current login User !!!");

		// Get Current user from session
		IUser user = userService.getCurrentUser();

		return getXMLViewer(getAsRenderer(user, UserRendered.class));
	}

	@RequestMapping(value = PROTECTED_SERVICE + "/user/search")
	protected ModelAndView search(@RequestParam(value = GRID_PARAMETERS, required = true) String xmlGridParameters) {
		logger.trace("Request for search users with parameters " + xmlGridParameters + "!!!");

		KeywordParameterRendered gridParameters = null;
		String keyword = null;

		if (StringUtils.isNotEmpty(xmlGridParameters)) {
			gridParameters = getGridParameters(xmlGridParameters, KeywordParameterRendered.class);
			keyword = gridParameters.getKeyword();
		}

		PagingAndSorting pagingAndSorting = getPagingAndSorting(gridParameters);

		// Return all users according to keyword search
		List<IUser> users = userService.getAll(pagingAndSorting.getPaging(), pagingAndSorting.getSorting(), keyword);

		// Convert all users to UsersRendered
		UserRenderedList userRendererList = getAsRenderer(users, UserRendered.class, IUser.class, UserRenderedList.class, gridParameters);

		return getXMLViewer(userRendererList);
	}

	@RequestMapping(value = PROTECTED_SERVICE + "/user/logout")
	public ModelAndView logout() {
		return getViewer(LogoutViewer.class, null);
	}

	@Override
	@RequestMapping(value = PROTECTED_SERVICE + "/user/delete")
	protected ModelAndView delete(@RequestParam(value = CRUD_PARAMETERS, required = true) Long id) {
		return super.delete(id);
	}

	@RequestMapping(value = PROTECTED_SERVICE + "/user/update")
	protected ModelAndView update(@RequestParam(value = CRUD_PARAMETERS, required = true) String xmlCrudParameters) {
		return super.update(xmlCrudParameters, UserRendered.class, User.class);	
	}

	@RequestMapping(value = PROTECTED_SERVICE + "/user/create")
	protected ModelAndView create(@RequestParam(value = CRUD_PARAMETERS, required = true) String xmlCrudParameters) {
		return super.create(xmlCrudParameters,  UserRendered.class, User.class);
	}

	@Override
	protected CRUDService<IUser, Long> getCrudService() {
		return userService;
	}

	@Override
	protected String getModelName() {
		return getMessages().getMessage(UserProperties.class.getName() + "." + UserProperties.MODEL_NAME.name());
	}

}
