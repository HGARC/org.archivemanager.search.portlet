package org.archivemanager.portal.portlet.apps;
import java.io.IOException;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.archivemanager.portal.portlet.PortletSupport;
import org.heed.openapps.dictionary.DataDictionary;
import org.heed.openapps.dictionary.Model;
import org.heed.openapps.dictionary.ModelField;
import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Entity;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;


public class ToolbarPortlet extends PortletSupport {
		
	
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);		
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		org.heed.openapps.User user = getSecurityService().getCurrentUser(httpReq2);
		
		renderRequest.setAttribute("isSignedIn", themeDisplay.isSignedIn());
		String id = httpReq2.getParameter("id");
		try {
			Entity entity = getEntityService().getEntity(null, Long.valueOf(id));
			if(entity.getQName().equals(RepositoryModel.COLLECTION) && entity.getSourceAssociations().size() > 0) {
				renderRequest.setAttribute("searchable", true);				
			}
			renderRequest.setAttribute("entityId", id);
			if(themeDisplay.isSignedIn() && (isArchivist(user) || isAdministrator(user)) && id != null && id.length() > 0) {				
				include("/jsp/apps/toolbar.jsp", renderRequest, renderResponse);		
			} else {
				include("/jsp/apps/toolbar_anon.jsp", renderRequest, renderResponse);
			}
		} catch(Exception e) {
			throw new PortletException("", e);
		} 
	}
	@Override
	public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		super.render(request, response);
		String id = request.getParameter("id");
		String view = request.getParameter("view");
		if(view != null) {
			if(view.equals("editor") && id != null) {
				try {
					Entity entity = getEntityService().getEntity(null, Long.valueOf(id));
					DataDictionary dictionary = getDictionaryService().getSystemDictionary();
					List<Model> models = dictionary.getChildModels(RepositoryModel.ITEM);
					models.add(dictionary.getModel(RepositoryModel.COLLECTION));
					for(Model model : models) {
						if(entity.getQName().equals(model.getQName())) {
							List<ModelField> fields = model.getFields();
							request.setAttribute("fields", fields);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
 	}
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws IOException, PortletException {
		String serviceUrl = PropsUtil.get("openapps.service.url");
		
		resourceRequest.setAttribute("serviceUrl", serviceUrl);
		
		//includeResource("/jsp/collections.jsp", resourceRequest, resourceResponse);
	}
}
