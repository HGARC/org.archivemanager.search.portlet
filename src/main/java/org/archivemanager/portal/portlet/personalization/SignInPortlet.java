package org.archivemanager.portal.portlet.personalization;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.archivemanager.portal.portlet.search.PortletSupport;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

public class SignInPortlet extends PortletSupport {

	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
				
		if(themeDisplay.isSignedIn())
			renderRequest.setAttribute("signedin", true);
		else
			renderRequest.setAttribute("signedin", false);
				
		String pageName="/research/digital-reserve";
		long plid = 0L;
		try {
			plid = LayoutLocalServiceUtil.getFriendlyURLLayout(themeDisplay.getScopeGroupId(), false, pageName).getPlid();
			renderRequest.setAttribute("plid", plid);
			//System.out.println("Pliid==>"+plid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		include("/jsp/signin/default.jsp", renderRequest, renderResponse);
	}
}
