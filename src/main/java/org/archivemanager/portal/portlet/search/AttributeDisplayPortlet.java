package org.archivemanager.portal.portlet.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.archivemanager.model.Attribute;
import org.archivemanager.model.AttributeValue;
import org.archivemanager.model.Result;
import org.archivemanager.model.ResultSet;
import org.archivemanager.util.EntityRepositoryModelUtil;
import org.heed.openapps.User;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.util.PortalUtil;


public class AttributeDisplayPortlet extends PortletSupport {
	private static Log log = LogFactoryUtil.getLog(AttributeDisplayPortlet.class);
	
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		
		PortletSession ps = renderRequest.getPortletSession();
		String view = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "view", "");
		
		Boolean people = PrefsParamUtil.getBoolean(renderRequest.getPreferences(), renderRequest, "people", true);
		Boolean corporations = PrefsParamUtil.getBoolean(renderRequest.getPreferences(), renderRequest, "corporations", true);
		Boolean subjects = PrefsParamUtil.getBoolean(renderRequest.getPreferences(), renderRequest, "subjects", true);
		Boolean collections = PrefsParamUtil.getBoolean(renderRequest.getPreferences(), renderRequest, "collections", true);
		Boolean contentTypes = PrefsParamUtil.getBoolean(renderRequest.getPreferences(), renderRequest, "contentTypes", true);
		Boolean languages = PrefsParamUtil.getBoolean(renderRequest.getPreferences(), renderRequest, "languages", true);
		Integer width = PrefsParamUtil.getInteger(renderRequest.getPreferences(), renderRequest, "width", 0);
		
		String id = httpReq2.getParameter("id");
		
		if(view.equals("user.jsp")) {
			List<Attribute> attributes = new ArrayList<Attribute>();
			User user = getSecurityService().getCurrentUser(httpReq2);			
			SearchRequest query = new SearchRequest(null,RepositoryModel.COLLECTION,ClassificationModel.SUBJECT,ClassificationModel.NAMED_ENTITY);
			query.setUser(user);
			query.setPublic(false);
			query.setStartRow(0);
			query.setEndRow(100);
			SearchResponse searchResponse = getSearchService().search(query);
			if(searchResponse != null) {
				EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
				Attribute collectionAttribute = new Attribute("Collections");
				Attribute peopleAttribute = new Attribute("Personal Entities");
				Attribute corporateAttribute = new Attribute("Corporate Entities");
				Attribute subjectAttribute = new Attribute("Subjects");
				for(SearchResult searchResult :searchResponse.getResults()) {
					Result result = modelUtility.getResult(searchResult.getEntity());
					if(result.getContentType().equals("collection") && collections) {
						collectionAttribute.getValues().add(new AttributeValue(result.getTitle(),"path:"+result.getId(),""));
					} else if(result.getContentType().equals("person") && people) {
						peopleAttribute.getValues().add(new AttributeValue(result.getTitle(),"name:"+result.getId(),""));
					} else if(result.getContentType().equals("corporation") && corporations) {
						corporateAttribute.getValues().add(new AttributeValue(result.getTitle(),"name:"+result.getId(),""));
					} else if(result.getContentType().equals("subject") && subjects) {
						subjectAttribute.getValues().add(new AttributeValue(result.getTitle(),"subj:"+result.getId(),""));
					}						
				}
				if(collectionAttribute.getValues().size() > 0)
					attributes.add(collectionAttribute);
				if(subjectAttribute.getValues().size() > 0)
					attributes.add(subjectAttribute);
				if(peopleAttribute.getValues().size() > 0)
					attributes.add(peopleAttribute);				
				if(corporateAttribute.getValues().size() > 0)
					attributes.add(corporateAttribute);
			}
			renderRequest.setAttribute("attributes", attributes);
			log.info(searchResponse.getResultSize()+" results for "+query+" parsed to "+searchResponse.getQueryParse()+" ----> "+searchResponse.getQueryExplanation());
		} else {
			ResultSet result = (ResultSet)ps.getAttribute("am_results", PortletSession.APPLICATION_SCOPE);
			if(result != null) {
				for(Attribute attribute : result.getAttributes()) {
					if(attribute.getName().equals("Collections") && !collections)
						attribute.setDisplay(false);
					if(attribute.getName().equals("Personal Entities") && !people)
						attribute.setDisplay(false);
					if(attribute.getName().equals("Corporate Entities") && !corporations)
						attribute.setDisplay(false);
					if(attribute.getName().equals("Subjects") && !subjects)
						attribute.setDisplay(false);
					if(attribute.getName().equals("Content Type") && !contentTypes)
						attribute.setDisplay(false);
					if(attribute.getName().equals("Language") && !languages)
						attribute.setDisplay(false);
					
					for(AttributeValue value : attribute.getValues()) {
						String name = value.getName();
						if(width > 0 && name.length() > width) 
							name = name.substring(0, width)+"...";
						if(name.contains("<br>")) 
							name = name.replace("<br>", "");
						value.setName(name);
					}
				}				
				renderRequest.setAttribute("resultset", result);				
			}
		}
		if(id != null && id.length() > 0) {				
			renderRequest.setAttribute("baseUrl", "?id="+id+"&");
		} else {
			renderRequest.setAttribute("baseUrl", "?");
		}
		include("/jsp/attributes/"+view, renderRequest, renderResponse);
	}
	
}
