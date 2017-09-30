package org.archivemanager.portal.portlet.search;

import java.io.IOException;
import java.io.StringReader;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.heed.openapps.QName;
import org.heed.openapps.data.Sort;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.security.SecurityService;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchService;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;


public class PortletSupport extends GenericPortlet {
	private static Log log = LogFactoryUtil.getLog(PortletSupport.class);
	private SecurityService securityService;
	private EntityService entityService;
	private SearchService searchService;
	private static DocumentBuilderFactory factory;
	
	
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
	protected SearchRequest getSearchRequest(String query, int startRow, int endRow, String[] sorts, boolean attributes,QName... qnames) {
		SearchRequest sQuery = new SearchRequest(query, qnames);
		sQuery.setAttributes(attributes);
		sQuery.setStartRow(startRow);
		sQuery.setEndRow(endRow);
		if(sorts != null) {
			for(String sort : sorts) {
				Sort lSort = null;
				String[] s = sort.split(",");
				if(s.length == 2) {
					boolean reverse = s[1].equals("asc") ? true : false;
					if(s[0].endsWith("_")) lSort = new Sort(Sort.LONG, sort, reverse);
					else lSort = new Sort(Sort.STRING, s[0], reverse);						
				} else if(s.length == 1) {
					if(s[0].endsWith("_")) lSort = new Sort(Sort.LONG, sort, true);
					else lSort = new Sort(Sort.STRING, sort, false);
				}
				sQuery.addSort(lSort);
			}
		}				
		return sQuery;
	}
	protected SearchRequest getSearchRequest(QName qname, String query, int startRow, int endRow, String[] sorts, boolean attributes) {
		SearchRequest sQuery = new SearchRequest(qname, query);
		sQuery.setAttributes(attributes);
		sQuery.setStartRow(startRow);
		sQuery.setEndRow(endRow);
		if(sorts != null) {
			for(String sort : sorts) {
				Sort lSort = null;
				String[] sortStrings = sort.split(",");
				for(String sortStr : sortStrings) {
					String[] s = sortStr.split(" ");
					if(s.length == 2) {
						boolean reverse = s[1].equals("asc") ? true : false;
						if(s[0].endsWith("_")) lSort = new Sort(Sort.LONG, s[0], reverse);
						else lSort = new Sort(Sort.STRING, s[0], reverse);						
					} else if(s.length == 1) {
						if(s[0].endsWith("_")) lSort = new Sort(Sort.LONG, s[0], true);
						else lSort = new Sort(Sort.STRING, s[0], false);
					}
					sQuery.addSort(lSort);
				}
			}
		}				
		return sQuery;
	}
	public static JSONObject getJSONObjectFromString(String content) {
		try {
			return new JSONObject(content);
		} catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	public static org.w3c.dom.Document getDOMDocumentFromString(String content) {
		try {
			if(factory == null) factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
			return doc;
		} catch (SAXException e) { e.printStackTrace();
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		return null;
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
	public SearchService getSearchService() {
		if(searchService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			searchService = (SearchService)locator.locate("searchService");
		}
		return searchService;
	}
	public EntityService getEntityService() {
		if(entityService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			entityService = (EntityService)locator.locate("entityService");
		}
		return entityService;
	}
	public SecurityService getSecurityService() {
		if(securityService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			securityService = (SecurityService)locator.locate("securityService");
		}
		return securityService;
	}
}
