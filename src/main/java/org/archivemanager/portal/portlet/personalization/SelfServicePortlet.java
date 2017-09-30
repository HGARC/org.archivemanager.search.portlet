package org.archivemanager.portal.portlet.personalization;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.model.Result;
import org.archivemanager.portal.portlet.search.PortletSupport;
import org.archivemanager.util.EntityRepositoryModelUtil;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class SelfServicePortlet extends PortletSupport {
	private static Log log = LogFactoryUtil.getLog(SelfServicePortlet.class);
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		String baseUrl = themeDisplay.getURLCurrent();
		renderRequest.setAttribute("baseUrl", baseUrl);
		
		List<Result> entities = subjects(renderRequest);
		renderRequest.setAttribute("entities", entities);
		
		include("/jsp/personalization/self_service.jsp", renderRequest, renderResponse);
	}
	
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(resourceRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		HttpServletResponse httpRes = PortalUtil.getHttpServletResponse(resourceResponse);
			
		try {
			httpRes.getWriter().append("booyah");
		} catch(Exception e) {
			throw new IOException(e);
		}
	}
	protected List<Result> subjects(RenderRequest renderRequest) {
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		org.heed.openapps.User user = getSecurityService().getCurrentUser(httpReq2);
		List<Result> subjects = new ArrayList<Result>();
		
		SearchRequest query = new SearchRequest(null,ClassificationModel.SUBJECT);
		query.setUser(user);
		query.setPublic(false);
		query.setStartRow(0);
		query.setEndRow(100);
		SearchResponse searchResponse = getSearchService().search(query);
		if(searchResponse != null) {
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			for(SearchResult searchResult :searchResponse.getResults()) {
				subjects.add(modelUtility.getResult(searchResult.getEntity()));
			}
		}
		log.info(searchResponse.getResultSize()+" results for "+query+" parsed to "+searchResponse.getQueryParse()+" ----> "+searchResponse.getQueryExplanation());
		return subjects;
	}
}
