package org.archivemanager.portal.portlet.search;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.archivemanager.model.Breadcrumb;
import org.archivemanager.model.Item;
import org.archivemanager.model.Result;
import org.archivemanager.model.ResultSet;
import org.heed.openapps.util.HTMLUtility;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;


public class BreadcrumbPortlet extends PortletSupport {
	private static Log log = LogFactoryUtil.getLog(BreadcrumbPortlet.class);
	
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		PortletSession ps = renderRequest.getPortletSession();
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		String view = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "view", "standard.jsp");
		
		List<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
		try{
			Layout current = themeDisplay.getLayout();
			String rootUrl = "/web"+themeDisplay.getScopeGroup().getFriendlyURL()+current.getFriendlyURL();
			while(current != null) {
				crumbs.add(new Breadcrumb(current.getName(themeDisplay.getLocale()), "/web"+themeDisplay.getScopeGroup().getFriendlyURL()+current.getFriendlyURL()));
				long parentId = current.getParentPlid();
				if(parentId > 0) current = LayoutLocalServiceUtil.getLayout(parentId);
				else current = null;
			}
			crumbs.add(new Breadcrumb(themeDisplay.getSiteGroupName(), "/web"+themeDisplay.getScopeGroup().getFriendlyURL()));
			Collections.reverse(crumbs);
			
			String id = httpReq2.getParameter("id") != null ? httpReq2.getParameter("id") : null;
			String query = httpReq2.getParameter("query") != null ? httpReq2.getParameter("query") : null;
			if(query != null) {	
				ResultSet result = (ResultSet)ps.getAttribute("am_results", PortletSession.APPLICATION_SCOPE);
				if(result != null) {				
					for(Breadcrumb c : result.getBreadcrumbs()) {
						c.setQuery(rootUrl+"?query="+c.getQuery());
						crumbs.add(c);			
					}					
				} 
			} else if(id != null && crumbs.size() >= 0) {
				Result result = (Result)ps.getAttribute("am_result", PortletSession.APPLICATION_SCOPE);
				//crumbs.remove(crumbs.size()-1);
				crumbs.get(crumbs.size()-2).setQuery("javascript:history.back()");				
				if(result != null) {
					if(result.getContentType().equals("collection")) {
						renderRequest.setAttribute("collection_name", result.getTitle());
					} else {
						renderRequest.setAttribute("collection_name", ((Item)result).getCollectionName());
					}
				}
			}
		} catch(Exception e) {
			log.error("", e);
		}
		for(Breadcrumb crumb : crumbs) {
			crumb.setName(HTMLUtility.removeTags(crumb.getName()));
		}
		renderRequest.setAttribute("breadcrumbs", crumbs);
		
		include("/jsp/breadcrumb/"+view, renderRequest, renderResponse);
	}
	
	
}
