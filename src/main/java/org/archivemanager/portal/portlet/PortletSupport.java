package org.archivemanager.portal.portlet;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.heed.openapps.Group;
import org.heed.openapps.Role;
import org.heed.openapps.User;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.logging.LoggingService;
import org.heed.openapps.node.NodeService;
import org.heed.openapps.reporting.ReportingService;
import org.heed.openapps.security.SecurityService;
import org.heed.openapps.search.SearchService;

import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


public class PortletSupport extends GenericPortlet {
	private static Log log = LogFactoryUtil.getLog(PortletSupport.class);
	private LoggingService loggingService;
	private NodeService nodeService;
	private DataDictionaryService dictionaryService;
	private EntityService entityService;
	private SearchService searchService;
	private SecurityService securityService;
	private ReportingService reportingService;
	
	private User user;
	
	
	public void init() throws PortletException {
	}
	
	public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		String jspPage = renderRequest.getParameter("jspPage");
		if (jspPage != null) {
			include(jspPage, renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	public void doEdit(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		if (renderRequest.getPreferences() == null) {
			super.doEdit(renderRequest, renderResponse);
		}
		else {
			include("", renderRequest, renderResponse);
		}
	}

	public void doHelp(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		include("", renderRequest, renderResponse);
	}
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException, PortletException {}
	
	protected String getRolesString(List<Role> roles) {
		StringWriter writer = new StringWriter();
		writer.append("[");
		for(int i=0; i < roles.size(); i++) {
			writer.append("'"+roles.get(i).getName()+"'");
			if(i < roles.size() - 1) writer.append(",");
		}
		writer.append("]");
		return writer.toString().trim();
	}
	protected String getRolesString(String[] roles) {
		StringWriter writer = new StringWriter();
		writer.append("[");
		for(int i=0; i < roles.length; i++) {
			writer.append("'"+roles[i]+"'");
			if(i < roles.length - 1) writer.append(",");
		}
		writer.append("]");
		return writer.toString().trim();
	}
	public boolean isStudent(User user) {
		try {
			for(Role role : user.getRoles()) {
				if(role.getName().toLowerCase().equals("student")) {
					return true;
				}
			}
		} catch(Exception e) {
			log.error("", e);
		}
		return false;
	}
	public boolean isTeacher(User user) {
		try {
			for(Role role : user.getRoles()) {
				if(role.getName().toLowerCase().equals("teacher")) {
					return true;
				}
			}
		} catch(Exception e) {
			log.error("", e);
		}
		return false;
	}
	public boolean isArchivist(User user) {
		try {
			for(Role role : user.getRoles()) {
				if(role.getName().toLowerCase().equals("archivist")) {
					return true;
				}
			}
			for(Group group : user.getGroups()) {
				if(group.getName().toLowerCase().equals("archivists")) {
					return true;
				}
			}
		} catch(Exception e) {
			log.error("", e);
		}
		return false;
	}
	public boolean isAdministrator(User user) {
		try {
			for(Role role : user.getRoles()) {
				if(role.getName().toLowerCase().equals("administrator")) {
					return true;
				}
			}
		} catch(Exception e) {
			log.error("", e);
		}
		return false;
	}
	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse)	throws IOException, PortletException {
		PortletRequestDispatcher portletRequestDispatcher =	getPortletContext().getRequestDispatcher(path);
		if (portletRequestDispatcher == null) {
			log.error(path + " is not a valid include");
		} else {
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}
	protected void includeResource(String path, ResourceRequest request, ResourceResponse response)	throws IOException, PortletException {
		PortletRequestDispatcher portletRequestDispatcher =	getPortletContext().getRequestDispatcher(path);
		if (portletRequestDispatcher == null) {
			log.error(path + " is not a valid include");
		} else {
			portletRequestDispatcher.include(request, response);
		}
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public NodeService getNodeService() {
		if(nodeService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				nodeService = (NodeService)locator.locate("nodeService");
		}
		return nodeService;
	}
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public DataDictionaryService getDictionaryService() {
		if(dictionaryService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				dictionaryService = (DataDictionaryService)locator.locate("dictionaryService");
		}
		return dictionaryService;
	}
	public void setDictionaryService(DataDictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	public EntityService getEntityService() {
		if(entityService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				entityService = (EntityService)locator.locate("entityService");
		}
		return entityService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}
	public SearchService getSearchService() {
		if(searchService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				searchService = (SearchService)locator.locate("searchService");
		}
		return searchService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public SecurityService getSecurityService() {
		if(securityService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				securityService = (SecurityService)locator.locate("securityService");
		}
		return securityService;
	}
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
	public LoggingService getLoggingService() {
		if(loggingService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				loggingService = (LoggingService)locator.locate("loggingService");
		}
		return loggingService;
	}
	public void setLoggingService(LoggingService loggingService) {
		this.loggingService = loggingService;
	}
	public ReportingService getReportingService() {
		if(reportingService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			reportingService = (ReportingService)locator.locate("reportingService");
		}
		return reportingService;
	}
	public void setReportingService(ReportingService reportingService) {
		this.reportingService = reportingService;
	}
}
