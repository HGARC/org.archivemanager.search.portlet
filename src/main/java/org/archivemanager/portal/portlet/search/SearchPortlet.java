package org.archivemanager.portal.portlet.search;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.archivemanager.model.Attribute;
import org.archivemanager.model.AttributeValue;
import org.archivemanager.model.Breadcrumb;
import org.archivemanager.model.Category;
import org.archivemanager.model.Collection;
import org.archivemanager.model.Item;
import org.archivemanager.model.Paging;
import org.archivemanager.model.Result;
import org.archivemanager.model.ResultSet;
import org.archivemanager.model.Subject;
import org.archivemanager.portal.portlet.search.data.EntitySorter;
import org.archivemanager.util.EntityRepositoryModelUtil;
import org.heed.openapps.SystemModel;
import org.heed.openapps.User;
import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.Property;
import org.heed.openapps.search.Clause;
import org.heed.openapps.search.Parameter;
import org.heed.openapps.search.SearchAttribute;
import org.heed.openapps.search.SearchAttributeValue;
import org.heed.openapps.search.SearchNode;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;


public class SearchPortlet extends PortletSupport {
	private static Log log = LogFactoryUtil.getLog(SearchPortlet.class);
	
	
	@SuppressWarnings("unchecked")
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		String mediaUrl = PropsUtil.get("openapps.media.url");
		String view = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "view", "default.jsp");
		
		String code = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "code", "");		
		String contentType = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "contentType", "false");
		String dateExpression = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "dateExpression", "false");
		String displayCollection = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "collection", "false");
		String displayDescription = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "description", "false");
		String displaySummary = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "summary", "false");
		String displayLanguage = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "language", "false");
		String sort = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "sort", "name_e");
		String size = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "size", "10");
		String detailPage = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "detailPage", "detail");
		int maxFieldSize = PrefsParamUtil.getInteger(renderRequest.getPreferences(), renderRequest, "maxFieldSize", 100);
		Boolean displayAllResults = PrefsParamUtil.getBoolean(renderRequest.getPreferences(), renderRequest, "all-results", true);
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);
		try {
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(themeDisplay.getUser());
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		} catch(Exception e) {
			e.printStackTrace();
		}
		//String currentUrl = themeDisplay.getURLCurrent();
		String query = httpReq2.getParameter("query") != null ? httpReq2.getParameter("query") : "";
		String page = httpReq2.getParameter("page") != null ? httpReq2.getParameter("page") : "1";
		if(httpReq2.getParameter("sort") != null) sort = httpReq2.getParameter("sort");
		if(httpReq2.getParameter("size") != null) size = httpReq2.getParameter("size");
		boolean sources = httpReq2.getParameter("sources") != null ? Boolean.valueOf(httpReq2.getParameter("sources")) : false;
		boolean targets = httpReq2.getParameter("targets") != null ? Boolean.valueOf(httpReq2.getParameter("targets")) : false;
		String id = httpReq2.getParameter("id");
		
		String baseUrl = themeDisplay.getURLCurrent();
		if(baseUrl.contains("?")) baseUrl = baseUrl.substring(0, baseUrl.indexOf("?"));
		if(baseUrl.contains(";jsessionid=")) baseUrl.substring(0, baseUrl.indexOf(";jsessionid="));
		//if(!baseUrl.contains("/search-collection")) baseUrl = baseUrl += "/search-collection";
		
		renderRequest.setAttribute("baseUrl", baseUrl);
		renderRequest.setAttribute("mediaUrl", mediaUrl);
		renderRequest.setAttribute("query", query);
		renderRequest.setAttribute("contentType", contentType);
		renderRequest.setAttribute("dateExpression", dateExpression);
		renderRequest.setAttribute("collection", displayCollection);
		renderRequest.setAttribute("description", displayDescription);
		renderRequest.setAttribute("summary", displaySummary);
		renderRequest.setAttribute("language", displayLanguage);
		renderRequest.setAttribute("detailPage", detailPage);
		renderRequest.setAttribute("maxFieldSize", maxFieldSize+"px");
		
		ResultSet results = null;
		
		if(view != null) {
			if(view.equals("gallery1.jsp")) {			
				renderRequest.setAttribute("query", query);
				if(query == null || query.length() == 0) query = "all results";
				results = searchLocal(renderRequest, "item", code, query, Integer.valueOf(page), null, 100, targets, sources);
			} else if(view.equals("gallery2.jsp")) {			
				renderRequest.setAttribute("query", query);
				if(query == null || query.length() == 0) query = "all results";
				try {
					List<Collection> collections = collectionNav(renderRequest);
					renderRequest.setAttribute("collections", collections);			
					results = searchLocal(renderRequest, "item", code, query, Integer.valueOf(page), null, 100, targets, sources);
				} catch(Exception e) {
					e.printStackTrace();
				}		
			} else if(view.equals("videos.jsp")) {			
				if(query == null || query.length() == 0) query = "all results";
				results = searchLocal(renderRequest, "item", code, query, Integer.valueOf(page), new String[]{"date_expression_"}, 9, targets, true);
			
			} else if(view.equals("collections.jsp")) {
				if(query == null || query.length() == 0) query = "all results";
				results = searchLocal(renderRequest, "collection", code, query.toLowerCase(), Integer.valueOf(page), new String[]{sort}, Integer.valueOf(size), targets, sources);	
				
			} else if(view.equals("archive_results.jsp")) {
				results = searchLocal(renderRequest, "archive", null, query, Integer.valueOf(page), new String[]{"sort_","relevance","name_e"}, Integer.valueOf(size), targets, sources);
			
			} else if(view.equals("notable_figures_entries.jsp")) {
				if(query == null || query.length() == 0) query = "all results";
				results = searchLocal(renderRequest, "entry", null, query.toLowerCase(), Integer.valueOf(page), new String[]{"name_e"}, Integer.valueOf(size), true, sources);	
			} else if(view.equals("series.jsp")) {
				try {
					List<Collection> collections = collections(renderRequest);
					renderRequest.setAttribute("collections", collections);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else if(view.equals("subject_guides.jsp")) {
				try {
					EntityQuery subjectQuery = null;
					if(id == null) {
						subjectQuery = new EntityQuery(ClassificationModel.SUBJECT);
						subjectQuery.setType(EntityQuery.TYPE_LUCENE_TEXT);
						subjectQuery.getProperties().add(new Property(ClassificationModel.SOURCE, "Subject"));
						subjectQuery.getProperties().add(new Property(ClassificationModel.SOURCE, "Guide"));
						EntityResultSet<Entity> resultSet = getEntityService().search(subjectQuery);
						renderRequest.setAttribute("resultset", resultSet);
					} else {
						List<Result> collections = subject_guides(renderRequest, id);
						EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
						Entity subject = getEntityService().getEntity(Long.valueOf(id));
						renderRequest.setAttribute("collections", collections);
						renderRequest.setAttribute("subject", modelUtility.getSubject(subject));
					}
				} catch(Exception e) {
					e.printStackTrace();
				} 
			} else {
				if(id != null && !query.contains("source_assoc:"+id)) {
					query += " source_assoc:"+id;
				}
				if(id != null && id.length() > 0) {				
					renderRequest.setAttribute("baseUrl", "?id="+id+"&");
				} else {
					renderRequest.setAttribute("baseUrl", "");
				}			
				if(!query.equals("") || displayAllResults) {
					renderRequest.getPortletSession().setAttribute("LIFERAY_SHARED_QUERY", query, PortletSession.APPLICATION_SCOPE);
					results = searchLocal(renderRequest, "item", code, query.trim(), Integer.valueOf(page), new String[]{sort}, Integer.valueOf(size), targets, sources);
					//results.setBaseUrl(themeDisplay.getURLCurrent().substring(0, themeDisplay.getURLCurrent().indexOf("?")));					
					//populateWebContent(renderRequest, code);
				}
			}
		} else {
			view = "results.jsp";		
		}
		if(results != null) {
			//log.info(results.getResultCount()+" result returned for query:"+query);
			renderRequest.setAttribute("resultset", results);
			renderRequest.setAttribute("currentPage", String.valueOf(results.getPage()));
			PortletSession session = renderRequest.getPortletSession();
			session.setAttribute("am_results", results, PortletSession.APPLICATION_SCOPE);
		}
		include("/jsp/search/"+view, renderRequest, renderResponse);
		
	}
		    
	protected ResultSet allItems(String query) {
		ResultSet results = new ResultSet();
		
		return results;
	}
	@SuppressWarnings("unchecked")
	protected ResultSet searchLocal(RenderRequest renderRequest, String type, String code, String query, int page, String[] sorts, int size, boolean targets, boolean sources) throws IOException {
		HttpServletRequest httpReq = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest httpReq2 = PortalUtil.getOriginalServletRequest(httpReq);		
		User user = getSecurityService().getCurrentUser(httpReq2);
		
		String parms = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "parms", null);
		
		SearchRequest searchRequest = null;
		ResultSet results = new ResultSet();
		int end = size * page;
		int start = end - size;
		String paramterString = "";		
		if(sorts == null) sorts = new String[0];
		
		if(type != null && type.equals("archive")) {
			searchRequest = getSearchRequest(query, start, end, sorts, false,ClassificationModel.CORPORATION,ClassificationModel.PERSON,RepositoryModel.COLLECTION);
		} else if(type != null && type.equals("collection")) {
			searchRequest = getSearchRequest(RepositoryModel.COLLECTION, query, start, end, sorts, false);
			searchRequest.addParameter(RepositoryModel.CODE.getLocalName(), code);
		} else if(type != null && type.equals("subjects")) {
			searchRequest = getSearchRequest(query, start, end, sorts, false,ClassificationModel.SUBJECT);
		} else {
			 if(type != null && type.equals("entry")) {
				searchRequest = getSearchRequest(ClassificationModel.ENTRY, query, start, end, sorts, false);
			} else {
				searchRequest = getSearchRequest(RepositoryModel.ITEM, query, start, end, sorts, false);
			}
			if(code != null && code.length() > 0) {
				EntityQuery collectionQuery = new EntityQuery(RepositoryModel.COLLECTION);
				collectionQuery.setType(EntityQuery.TYPE_LUCENE_TEXT);
				collectionQuery.getProperties().add(new Property(RepositoryModel.CODE, code));
				EntityResultSet<Entity> collectionResults = getEntityService().search(collectionQuery);
				if(collectionResults.getResults().size() == 1) {
					Entity collection = collectionResults.getResults().get(0);
					searchRequest.addParameter("path", String.valueOf(collection.getId()));
				} else if(collectionResults.getResults().size() > 1) {
					Clause clause = new Clause();
					clause.setOperator(Clause.OPERATOR_OR);
					for(Entity collection : collectionResults.getResults()) {
						clause.addParamater(new Parameter("path", String.valueOf(collection.getId())));
						//searchRequest.setCollection(collection.getId());
					}
					searchRequest.addClause(clause);
				}
			}
		}
		searchRequest.setUser(user);
		
		
		if(parms != null && parms.length() > 0) {
			String[] parmVals = parms.split(",");
			for(String p : parmVals) {
				if(httpReq2.getParameter(p) != null && httpReq2.getParameter(p).length() > 0) {
					paramterString += "&" + p + "=" + URLEncoder.encode(httpReq2.getParameter(p), "UTF-8");
					searchRequest.getRequestParameters().put(p, new String[]{httpReq2.getParameter(p)});
				}
			}
		}
		
		SearchResponse searchResponse = getSearchService().search(searchRequest);
		if(searchResponse != null) {			
			results.setQuery(query.trim());
			try {
				int maxFieldSize = PrefsParamUtil.getInteger(renderRequest.getPreferences(), renderRequest, "maxFieldSize", 0);
				results.setStart(searchResponse.getStartRow());
				results.setEnd(searchResponse.getEndRow());
				results.setTime(searchResponse.getTime());
				results.setResultCount(searchResponse.getResultSize());
				results.setQuery(query);
				results.setPageSize(size);
				
				EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
				for(SearchResult searchResult :searchResponse.getResults()) {
					Result result = null;
					String localName = searchResult.getQName().getLocalName();
					if(localName.equals("entry")) {
						result = modelUtility.getEntry(searchResult.getEntity());
					} else if(localName.equals("collection")) {
						result = modelUtility.getCollection(searchResult.getEntity(), true);
					} else if(localName.equals("video")) {
						result = modelUtility.getVideo(searchResult.getEntity());						
					} else {
						result = modelUtility.getItem(searchResult.getEntity());									
					}
					if(result != null) {
						if(result.getDescription() != null && maxFieldSize > 0) {
							if(result.getDescription().length() > 250) {
								result.setDescription(result.getDescription().substring(0, 250)+"...");
							}
						}
						if(result instanceof Item && maxFieldSize > 0) {
							Item item = (Item)result;
							if(item.getSummary() != null) {
								if(item.getSummary().length() > maxFieldSize) {
									item.setSummary(item.getSummary().substring(0, maxFieldSize)+"...");
								}
							}
						}
						results.getResults().add(result);
					}
				}
				if(!searchResponse.getAttributes().isEmpty()) {
					for(SearchAttribute att : searchResponse.getAttributes()) {
						Attribute attribute = new Attribute(att.getName());
						for(SearchAttributeValue valueNode : att.getValues()) {
							String name = valueNode.getName();
							String pageQuery = valueNode.getQuery().replace("//",  "/");
							if(results.getPageSize() != 10) pageQuery += "&size="+results.getPageSize();
							if(results.getSort() != null) pageQuery += "&sort="+results.getSort();
							AttributeValue value = new AttributeValue(name, pageQuery, String.valueOf(valueNode.getCount()));
							attribute.getValues().add(value);
						}
						attribute.setCount(String.valueOf(attribute.getValues().size()));
						if(attribute.getName().equals("Collections") && attribute.getValues().size() <= 1) attribute.setDisplay(false);
						else if(attribute.getValues().size() == 0) attribute.setDisplay(false);
						else attribute.setDisplay(true);
						results.getAttributes().add(attribute);
					}
				}
				if(!searchResponse.getBreadcrumb().isEmpty()) {
					for(SearchNode crumbNode : searchResponse.getBreadcrumb()) {
						String pageQuery = crumbNode.getQuery().replace("//",  "/").trim();
						if(crumbNode.getLabel() != null) {
							Breadcrumb crumb = new Breadcrumb(crumbNode.getLabel().trim(), pageQuery);
							results.getBreadcrumbs().add(crumb);
							results.setQuery(crumb.getQuery());
							results.setLabel(crumb.getName());
						}
					}
				}
				doPaging(results, query, page, paramterString);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		log.info(searchResponse.getResultSize()+" results for "+query+" parsed to "+searchResponse.getQueryParse()+" ----> "+searchResponse.getQueryExplanation());
		return results;
	}
	
	protected void doPaging(ResultSet results, String query, int currentPage, String parameters) {
		int pageCount = 0;		
		if(results.getResultCount() > results.getPageSize()) {
			double ratio = (double)results.getResultCount() / results.getPageSize();
			pageCount = (int)(Math.ceil(ratio));
		}
		results.setPageCount(pageCount);
		results.setPage(currentPage);
		
		if(query != null && !query.equals("all results")) {
			results.setQuery("query="+query);
		} else {
			results.setQuery("query="+parameters);
		}
		
		if(results.getPageSize() != 10) {
			for(Breadcrumb page : results.getBreadcrumbs()) {
				page.setQuery(page.getQuery() + "&size="+results.getPageSize());
			}
		}
		if(results.getSort() != null) {
			for(Breadcrumb page : results.getBreadcrumbs()) {
				page.setQuery(page.getQuery() + "&sort="+results.getSort());
			}
		}	
		
		int startPage = currentPage < 5 ? 1 : currentPage - 4;
		int endPage = (pageCount >= 10) ? 10 : pageCount;
		/*
		if(currentPage == 1) {
			String pageQuery = results.getQuery()+"&page=1&size="+results.getPageSize();
			if(results.getSort() != null) pageQuery += "&sort="+results.getSort();
			results.getPaging().add(new Paging("1", pageQuery));
		}
		*/
		if(results.getPage() > 1 && pageCount > 1) {
			String pageQuery = results.getQuery()+"&page="+(currentPage - 1);
			pageQuery += "&size="+results.getPageSize();
			if(results.getSort() != null) pageQuery += "&sort="+results.getSort();
			results.getPaging().add(new Paging("Previous", pageQuery));
		}
		for(int i = startPage; i <= endPage; i++) {
			String pageQuery = results.getQuery()+"&page="+i;
			pageQuery += "&size="+results.getPageSize();
			if(results.getSort() != null) pageQuery += "&sort="+results.getSort();
			results.getPaging().add(new Paging(String.valueOf(i), pageQuery));
		}
		if(pageCount > currentPage) {
			String pageQuery = results.getQuery()+"&page="+(currentPage + 1);
			pageQuery += "&size="+results.getPageSize();
			if(results.getSort() != null) pageQuery += "&sort="+results.getSort();
			results.getPaging().add(new Paging("Next", pageQuery));
		}
	}
	@SuppressWarnings("unchecked")
	protected List<Collection> collectionNav(RenderRequest renderRequest) throws Exception {
		String code = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "code", "");
		List<Collection> collections = new ArrayList<Collection>();
		
		EntitySorter entitySorter = new EntitySorter(new Sort(Sort.STRING, SystemModel.NAME.toString(), false));
		
		EntityQuery collectionQuery = new EntityQuery(RepositoryModel.COLLECTION);
		collectionQuery.setType(EntityQuery.TYPE_LUCENE_TEXT);
		collectionQuery.getProperties().add(new Property(RepositoryModel.CODE, code));
		EntityResultSet<Entity> collectionResults = getEntityService().search(collectionQuery);
		if(collectionResults != null && collectionResults.getResultSize() > 0) {
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			for(Entity collectionEntity : collectionResults.getResults()) {
				Collection collection = modelUtility.getCollection(collectionEntity, false);
				collection.setDescription("");
				List<Association> seriesAssociations = collectionEntity.getSourceAssociations(RepositoryModel.CATEGORIES);
				List<Entity> series = new ArrayList<Entity>();
				for(Association seriesAssoc : seriesAssociations) {
					Entity target = getEntityService().getEntity(null, seriesAssoc.getTarget());
					series.add(target);
				}
				Collections.sort(series, entitySorter);
				for(Entity seriesEntity : series) {
					Category category = new Category();
					category.setId(String.valueOf(seriesEntity.getId()));
					category.setTitle(seriesEntity.getName());
					collection.getSeries().add(category);
					/*
					List<Association> subseriesAssociations = seriesEntity.getSourceAssociations(RepositoryModel.CATEGORIES);
					for(Association subseriesAssoc : subseriesAssociations) {
						Entity target2 = getEntityService().getEntity(subseriesAssoc.getTarget());
						Category series2 = new Category();
						series2.setId(String.valueOf(target2.getId()));
						series2.setTitle(target2.getName());
						series.getCategories().add(series2);
					}
					*/
				}
				collections.add(collection);
			}			
		}
		return collections;
	}
	protected List<Result> subject_guides(RenderRequest renderRequest, String id) throws Exception {
		List<Result> results = new ArrayList<Result>();
		SearchRequest searchRequest = getSearchRequest("subj:"+id, 0, 1000, new String[]{"name_e"}, false, RepositoryModel.ITEM,RepositoryModel.COLLECTION);
		
		SearchResponse searchResponse = getSearchService().search(searchRequest);
		if(searchResponse != null) {			
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			for(SearchResult searchResult :searchResponse.getResults()) {
				String localName = searchResult.getQName().getLocalName();
				if(localName.equals("collection")) {
					Collection collection = modelUtility.getCollection(searchResult.getEntity(), true);
					List<Subject> subjectGuides = new ArrayList<Subject>();
					List<Subject> subjects = collection.getSubjects();
					for(Subject subject : subjects) {
						if(subject.getSource() != null && subject.getSource().equals("Subject Guide"))
							subjectGuides.add(subject);
					}
					collection.setSubjects(subjectGuides);
					results.add(collection);
				} else {
					Item item = modelUtility.getItem(searchResult.getEntity());
					results.add(item);
				}
			}
		}
		log.info(searchResponse.getResultSize()+" results for subject guide query parsed to "+searchResponse.getQueryParse()+" ----> "+searchResponse.getQueryExplanation());
		return results;
	}
	@SuppressWarnings("unchecked")
	protected List<Collection> collections(RenderRequest renderRequest) throws Exception {
		String code = PrefsParamUtil.getString(renderRequest.getPreferences(), renderRequest, "code", "");
		List<Collection> collections = new ArrayList<Collection>();
		EntitySorter entitySorter = new EntitySorter(new Sort(Sort.STRING, SystemModel.NAME.toString(), false));
		
		EntityQuery collectionQuery = new EntityQuery(RepositoryModel.COLLECTION);
		collectionQuery.setType(EntityQuery.TYPE_LUCENE_TEXT);
		collectionQuery.getProperties().add(new Property(RepositoryModel.CODE, code));
		EntityResultSet<Entity> collectionResults = getEntityService().search(collectionQuery);
		if(collectionResults != null && collectionResults.getResultSize() > 0) {
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			for(Entity collectionEntity : collectionResults.getResults()) {
				Collection collection = modelUtility.getCollection(collectionEntity, false);
				collection.setDescription("");
				List<Association> seriesAssociations = collectionEntity.getSourceAssociations(RepositoryModel.CATEGORIES);
				List<Entity> series = new ArrayList<Entity>();
				for(Association seriesAssoc : seriesAssociations) {
					Entity target = getEntityService().getEntity(null, seriesAssoc.getTarget());
					series.add(target);
				}
				Collections.sort(series, entitySorter);
				for(Entity seriesEntity : series) {
					Category category = new Category();
					category.setId(String.valueOf(seriesEntity.getId()));
					category.setTitle(seriesEntity.getName());
					collection.getSeries().add(category);
				}
				collections.add(collection);
			}			
		}
		return collections;
	}
	
	@SuppressWarnings("unchecked")
	protected void populateWebContent(RenderRequest renderRequest, String code) {
		try {
			DynamicQuery query = DynamicQueryFactoryUtil.forClass(JournalArticle.class)
					.add(RestrictionsFactoryUtil.like("title", "%"+code+"%"));
			List<JournalArticle> articles = new ArrayList<JournalArticle>();
			List<String> ids = new ArrayList<String>();
			List<JournalArticle> list = (List<JournalArticle>)JournalArticleLocalServiceUtil.dynamicQuery(query, 0, 100);
			for(JournalArticle article : list) {
				JournalArticle latest = JournalArticleLocalServiceUtil.getLatestArticle(article.getGroupId(), article.getArticleId());
				if(!ids.contains(latest.getArticleId())) {
					ids.add(latest.getArticleId());
					articles.add(latest);
				}
			}
			for(JournalArticle article : articles) {
				String title = article.getTitleCurrentValue();
				if(title.startsWith(code)) {
					String varName = title.substring(code.length()+1);
					String contentStr = article.getContent();
					com.liferay.portal.kernel.xml.Document document = SAXReaderUtil.read(contentStr);
					
					com.liferay.portal.kernel.xml.Node node = null;
					node = document.selectSingleNode("/root/static-content");
					if(node != null) {
						String content = node.getText(); 
						renderRequest.setAttribute(varName, content);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
}
