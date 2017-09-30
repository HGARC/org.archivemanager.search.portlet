package org.archivemanager.portal.portlet.search.configuration;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

public class AssociationsConfigurationAction extends DefaultConfigurationAction {

	
	public String render(PortletConfig config, RenderRequest renderRequest, RenderResponse renderResponse) throws Exception { 

		return "/jsp/associations/configuration.jsp";
		
	}
}
