package org.archivemanager.portal.web;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.heed.openapps.cache.CacheService;
import org.heed.openapps.content.DigitalObjectService;
import org.heed.openapps.crawling.CrawlingService;
import org.heed.openapps.data.TreeNode;
import org.heed.openapps.dictionary.DataDictionaryService;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.node.NodeService;
import org.heed.openapps.property.PropertyService;
import org.heed.openapps.reporting.ReportingService;
import org.heed.openapps.scheduling.SchedulingService;
import org.heed.openapps.security.SecurityService;
import org.heed.openapps.util.XMLUtility;
import org.heed.openapps.web.DictionaryServiceSupport;
import org.heed.openapps.web.EntityServiceSupport;
import org.heed.openapps.web.NodeServiceSupport;
import org.heed.openapps.web.ContentServiceSupport;
import org.heed.openapps.web.SchedulingServiceSupport;
import org.heed.openapps.web.SecurityServiceSupport;
import org.heed.openapps.search.SearchService;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;

public abstract class WebserviceSupport {
	private NodeService nodeService;
	private EntityService entityService;
	private SearchService searchService;
	private DataDictionaryService dictionaryService;
	private CacheService cacheService;
	private SchedulingService schedulingService;
	private ReportingService reportingService;
	private SecurityService securityService;
	private PropertyService propertyService;
	private DigitalObjectService digitalObjectService;
	private CrawlingService crawlingService;
	
	protected EntityServiceSupport entityServiceSupport;
	protected NodeServiceSupport nodeServiceSupport;
	protected DictionaryServiceSupport dictionaryServiceSupport;
	protected ContentServiceSupport contentServiceSupport;
	protected SecurityServiceSupport securityServiceSupport;
	protected SchedulingServiceSupport schedulingServiceSupport;
	
	
	protected TreeNode getTreeNode(String id, String uid, String parent, String type, String namespace, String title, Boolean isFolder) {
		TreeNode node = new TreeNode();
		node.setId(uid);
		node.setUid(uid);
		node.setParent(parent);
		node.setType(type);
		node.setNamespace(namespace);
		node.setTitle(title);
		node.setIsFolder(isFolder.toString());
		if(type != null) node.setIcon("/theme/images/tree_icons/"+type+".png");
		return node;
	}
	protected ModelAndView response(String data, PrintWriter out) {
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><status>0</status><message></message><data>"+data+"</data></response>");
		return null;
	}
	protected ModelAndView response(String message, String data, PrintWriter out) {
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><status>0</status><message>"+message+"</message><data>"+data+"</data></response>");
		return null;
	}
	protected ModelAndView response(String message, int total, int start, int end, String data, PrintWriter out) {
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><status>0</status><totalRows>"+total+"</totalRows><startRow>"+start+"</startRow><endRow>"+end+"</endRow><message>"+message+"</message><data>"+data+"</data></response>");
		return null;
	}
	public static String getHostUrl(HttpServletRequest req) {
		return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
	}
	
	public CrawlingService getCrawlingService() {
		if(crawlingService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				crawlingService = (CrawlingService)locator.locate("crawlingService");
		}
		return crawlingService;
	}
	public SearchService getSearchService() {
		if(searchService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				searchService = (SearchService)locator.locate("searchService");
		}
		return searchService;
	}
	public EntityService getEntityService() {
		if(entityService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				entityService = (EntityService)locator.locate("entityService");
		}
		return entityService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
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
	public CacheService getCacheService() {
		if(cacheService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				cacheService = (CacheService)locator.locate("cacheService");
		}
		return cacheService;
	}
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	public SchedulingService getSchedulingService() {
		if(schedulingService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				schedulingService = (SchedulingService)locator.locate("schedulingService");
		}
		return schedulingService;
	}
	public void setSchedulingService(SchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}
	public ReportingService getReportingService() {
		if(reportingService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				reportingService = (ReportingService)locator.locate("reportingService");
		}
		return reportingService;
	}
	public void setReportingService(ReportingService reportingService) {
		this.reportingService = reportingService;
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
	public PropertyService getPropertyService() {
		if(propertyService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				propertyService = (PropertyService)locator.locate("propertyService");
		}
		return propertyService;
	}
	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}
	public DigitalObjectService getDigitalObjectService() {
		if(digitalObjectService == null) {
			BeanLocator locator = PortletBeanLocatorUtil.getBeanLocator("archivemanager-portlet");
			if(locator != null)
				digitalObjectService = (DigitalObjectService)locator.locate("digitalObjectService");
		}
		return digitalObjectService;
	}
	public void setDigitalObjectService(DigitalObjectService digitalObjectService) {
		this.digitalObjectService = digitalObjectService;
	}
	
	public EntityServiceSupport getEntityServiceSupport() {
		if(entityServiceSupport == null) {
			entityServiceSupport = new EntityServiceSupport(getEntityService());
		}
		return entityServiceSupport;
	}
	public NodeServiceSupport getNodeServiceSupport() {
		if(nodeServiceSupport == null) {
			nodeServiceSupport = new NodeServiceSupport(getNodeService());
		}
		return nodeServiceSupport;
	}
	public DictionaryServiceSupport getDictionaryServiceSupport() {
		if(dictionaryServiceSupport == null) {
			dictionaryServiceSupport = new DictionaryServiceSupport(getDictionaryService());
		}
		return dictionaryServiceSupport;
	}
	public ContentServiceSupport getContentServiceSupport() {
		if(contentServiceSupport == null) {
			contentServiceSupport = new ContentServiceSupport();
			contentServiceSupport.setEntityService(getEntityService());
			contentServiceSupport.setSecurityService(getSecurityService());
			contentServiceSupport.setDigitalObjectService(getDigitalObjectService());
		}
		return contentServiceSupport;
	}
	public SecurityServiceSupport getSecurityServiceSupport() {
		if(nodeServiceSupport == null) {
			securityServiceSupport = new SecurityServiceSupport(getSecurityService());
		}
		return securityServiceSupport;
	}
	public SchedulingServiceSupport getSchedulingServiceSupport() {
		if(entityServiceSupport == null) {
			schedulingServiceSupport = new SchedulingServiceSupport(getSchedulingService());
		}
		return schedulingServiceSupport;
	}
	
	public static String toXmlData(Object o) {
		if(o == null) return "";
		else return XMLUtility.escape(o.toString());
	}
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );		
	}
}
