package org.archivemanager.portal.portlet.personalization;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.archivemanager.portal.portlet.search.PortletSupport;
import org.archivemanager.util.EntityRepositoryModelUtil;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.dictionary.RepositoryModel;
import org.archivemanager.model.Result;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class UserSpacePortlet extends PortletSupport {
	private static Log log = LogFactoryUtil.getLog(UserSpacePortlet.class);
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		User user = themeDisplay.getUser();
		String view = "default";
		if(isArchivist(user) || isAdministrator(user)) {
			
			view = "archivist";
		} else if(isTeacher(user)) {
		
			view = "educator";
		} else if(isStudent(user)) {
			
			view = "student";
		}
		List<Result> entities = entities(renderRequest);
		renderRequest.setAttribute("entities", entities);
		
		include("/jsp/personalization/"+view+".jsp", renderRequest, renderResponse);
	}
	
	protected List<Result> entities(RenderRequest renderRequest) {
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		org.heed.openapps.User user = getSecurityService().getCurrentUser(httpReq2);
		List<Result> collections = new ArrayList<Result>();
		
		SearchRequest query = new SearchRequest(null,RepositoryModel.COLLECTION,ClassificationModel.SUBJECT,ClassificationModel.NAMED_ENTITY);
		query.setUser(user);
		query.setPublic(false);
		query.setStartRow(0);
		query.setEndRow(100);
		SearchResponse searchResponse = getSearchService().search(query);
		if(searchResponse != null) {
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			for(SearchResult searchResult :searchResponse.getResults()) {
				collections.add(modelUtility.getResult(searchResult.getEntity()));
			}
		}
		log.info(searchResponse.getResultSize()+" results for "+query+" parsed to "+searchResponse.getQueryParse()+" ----> "+searchResponse.getQueryExplanation());
		return collections;
	}
}
