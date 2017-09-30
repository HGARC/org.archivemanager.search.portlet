package org.archivemanager.portal.portlet.search;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


import com.liferay.portal.kernel.util.PrefsParamUtil;


public class AssociationsPortlet extends PortletSupport {

	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		String view = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "view", "");
		String links = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "links", "");
		
		PortletSession ps = renderRequest.getPortletSession();
		Object result = ps.getAttribute("am_result", PortletSession.APPLICATION_SCOPE);
		
		renderRequest.setAttribute("links", links);
		renderRequest.setAttribute("result", result);
		
		include("/jsp/associations/"+view, renderRequest, renderResponse);
	}
}
