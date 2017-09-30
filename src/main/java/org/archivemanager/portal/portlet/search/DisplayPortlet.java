package org.archivemanager.portal.portlet.search;
import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.archivemanager.model.Audio;
import org.archivemanager.model.Collection;
import org.archivemanager.model.NamedEntity;
import org.archivemanager.model.Result;
import org.archivemanager.model.Video;
import org.archivemanager.util.EntityRepositoryModelUtil;
import org.heed.openapps.entity.Entity;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.util.PortalUtil;


public class DisplayPortlet extends PortletSupport {
	private static Log log = LogFactoryUtil.getLog(DisplayPortlet.class);
	
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		String view = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "view", "item.jsp");
		String displayName = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displayName", null);
		String displayDescription = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displayDescription", "false");
		String displaySummary = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displaySummary", "false");
		String displayDateExpression = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displayDateExpression", "false");
		String displayContainer = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displayContainer", "false");
		String displayCollectionName = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displayCollectionName", "false");
		String displayContentType = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displayContentType", "false");
		String displayLanguage = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "displayLanguage", "false");
		String missingImage = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "missingImage", null);
		String id = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "id", null);
		if(missingImage != null && missingImage.length() == 0) missingImage = null;
		
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		
		if(httpReq2.getParameter("id") != null) id = httpReq2.getParameter("id");
		
		Result result = null;
		if(id != null) {		
			result = searchLocal(id);
			if(result != null) {
				if(result instanceof Video) view = "video.jsp";
				else if(result instanceof Audio) view = "audio.jsp";
				else if(result instanceof Collection) view = "collection.jsp";
				else if(result instanceof NamedEntity && result.getContentType().equals("person")) view = "person.jsp";
				else if(result instanceof NamedEntity && result.getContentType().equals("corporation")) view = "corporation.jsp";
			}
		}
		
		renderRequest.setAttribute("missingImage", missingImage);
		renderRequest.setAttribute("displayName", displayName);
		renderRequest.setAttribute("displayDescription", displayDescription);
		renderRequest.setAttribute("displaySummary", displaySummary);
		renderRequest.setAttribute("displayDateExpression", displayDateExpression);
		renderRequest.setAttribute("displayContainer", displayContainer);
		renderRequest.setAttribute("displayCollectionName", displayCollectionName);
		renderRequest.setAttribute("displayContentType", displayContentType);
		renderRequest.setAttribute("displayLanguage", displayLanguage);
		renderRequest.setAttribute("result", result);
		PortletSession session = renderRequest.getPortletSession(true);
		session.setAttribute("am_result", result, PortletSession.APPLICATION_SCOPE);
		
		if(view.endsWith(".jsp")) include("/jsp/display/"+view, renderRequest, renderResponse);
		else include("/jsp/display/"+view+".jsp", renderRequest, renderResponse);
	}
	
	protected Result searchLocal(String id) {
		Result result = null;
		try {
			Entity entity = getEntityService().getEntity(null, Long.valueOf(id));
			
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			String localName = entity.getQName().getLocalName();
			if(localName.equals("subject")) {
				result = modelUtility.getSubject(entity);
			} else if(localName.equals("person")) {
				result = modelUtility.getNamedEntity(entity);
			} else if(localName.equals("corporation")) {
				result = modelUtility.getNamedEntity(entity);
			} else if(localName.equals("collection")) {
				result = modelUtility.getCollection(entity, true);
			} else if(localName.equals("video")) {
				result = modelUtility.getVideo(entity);
			} else if(localName.equals("audio")) {
				result = modelUtility.getAudio(entity);
			} else {
				result = modelUtility.getItem(entity);
			}
		} catch(Exception e) {
			log.error(e);
		}
		return result;
	}
	
}
