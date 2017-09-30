package org.archivemanager.portal.portlet.search;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.archivemanager.model.Result;

import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.util.PortalUtil;


public class DigitalObjectDisplayPortlet extends PortletSupport {
	
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		
		PortletSession ps = renderRequest.getPortletSession();
		String view = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "view", "");
		
		String id = httpReq2.getParameter("id");
		
		Result result = (Result)ps.getAttribute("am_result", PortletSession.APPLICATION_SCOPE);
		if(result != null) {
			renderRequest.setAttribute("result", result);
			if(result.getContentType().equals("Photographic Material"))
				include("/jsp/digital-object/photographs.jsp", renderRequest, renderResponse);
			else
				include("/jsp/digital-object/default.jsp", renderRequest, renderResponse);
		}
		
	}
	
}
