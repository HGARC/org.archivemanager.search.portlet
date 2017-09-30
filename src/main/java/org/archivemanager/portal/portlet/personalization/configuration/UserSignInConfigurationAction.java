package org.archivemanager.portal.portlet.personalization.configuration;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

public class UserSignInConfigurationAction extends DefaultConfigurationAction {

	public String render(PortletConfig config, RenderRequest renderRequest, RenderResponse renderResponse) throws Exception { 

		return "/jsp/signin/configuration.jsp";
		
	}
	
}
