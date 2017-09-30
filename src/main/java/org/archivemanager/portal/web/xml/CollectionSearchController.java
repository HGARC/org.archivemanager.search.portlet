package org.archivemanager.portal.web.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.model.Category;
import org.archivemanager.model.Collection;
import org.archivemanager.util.EntityRepositoryModelUtil;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationSorter;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.NumberUtility;
import org.heed.openapps.search.SearchRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping("/service/archivemanager")
public class CollectionSearchController extends SearchControllerSupport {
	
	
	//private QName[] archiveQNames = new QName[]{RepositoryModel.COLLECTION,ClassificationModel.SUBJECT,ClassificationModel.PERSON};
	
	@RequestMapping(value="/search", method = RequestMethod.GET)
	public ModelAndView search(HttpServletRequest req, HttpServletResponse res, @RequestParam("code") String code, @RequestParam("query") String query) throws Exception {
		Map<String, Object> parms = new HashMap<String, Object>();
		/*
		parms.put("code", code);
		//String fmt = req.getRequestURL().substring(req.getRequestURL().lastIndexOf("."));
		if(query == null || query.equals("")) query = "all results";
		if(query != null && !query.equals("")) {
			//analyticsService.search(req.getQueryString());
			String sizeStr = req.getParameter("size");  //page size for result set (default 10)
			String pageStr = req.getParameter("page");
			String startStr = req.getParameter("start");
			String sort = req.getParameter("sort");
			
			if(sort == null || sort.length() == 0) sort = "localName";
			int page = (NumberUtility.isInteger(pageStr)) ? Integer.valueOf(pageStr) : 1;
			int size = (NumberUtility.isInteger(sizeStr)) ? Integer.valueOf(sizeStr) : 10;
			long start = (startStr != null && NumberUtility.isLong(startStr)) ? Long.valueOf(startStr) : 0;
			
			SearchRequest searchRequest = null;			
			if(code.equals("archive")) {
				searchRequest = new SearchRequest(RepositoryModel.ITEM, query);
				searchRequest.setAttributes(true);
				//searchRequest.setPage(page);
				//searchRequest.setPageSize(size);
				
				searchRequest.getSorts().add(new Sort(Sort.STRING, sort, false));				
				SearchResponse result = getSearchService().search(searchRequest);
				
				String output = toXml(searchRequest, result, false, false);
				return response("", output, res.getWriter());
			} else {
				searchRequest = getSearchRequest(RepositoryModel.ITEM, start, query, page, size, sort, true);
				
				EntityQuery collectionQuery = new EntityQuery(RepositoryModel.COLLECTION, "code", code, "name", true);
				EntityResultSet collectionResults = getEntityService().search(collectionQuery);
				if(collectionResults.getResults().size() == 1) {
					Entity collection = collectionResults.getResults().get(0);
					searchRequest.addParameter("path", String.valueOf(collection.getId()));
				} else {
					Clause clause = new Clause();
					clause.setOperator(Clause.OPERATOR_OR);
					for(Entity collection : collectionResults.getResults()) {
						clause.addParamater(new Parameter("path", String.valueOf(collection.getId())));
						//searchRequest.setCollection(collection.getId());
					}
					searchRequest.addClause(clause);
				}
				searchRequest.setPrintSources(true);				
				SearchResponse result = getSearchService().search(searchRequest);
				
				String output = toXml(searchRequest, result, true, false);
				return response("", output, res.getWriter());
			}						
		}
		parms.put("status", "0");
		*/
		return new ModelAndView("results_xml", parms);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/search/series", method = RequestMethod.GET)
	public ModelAndView collection(HttpServletRequest req, HttpServletResponse res, @RequestParam("code") String code) throws Exception {
		Map<String, Object> parms = new HashMap<String, Object>();
		parms.put("code", code);
		EntityQuery query = new EntityQuery(RepositoryModel.COLLECTION, "code", code, "name_e", true);
		EntityResultSet<Entity> collectionResults = getEntityService().search(query);
		parms.put("code", code);
		StringBuffer buff = new StringBuffer();
		if(collectionResults != null && collectionResults.getResultSize() > 0) {
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			parms.put("title", collectionResults.getResult(0).getValue(SystemModel.NAME));
			List<Collection> collections = new ArrayList<Collection>();
			AssociationSorter associationSorter = new AssociationSorter(new Sort(Sort.STRING, SystemModel.NAME.getLocalName(), true));
			for(Entity collectionEntity : collectionResults.getResults()) {
				//getEntityService().hydrate(collectionEntity);
				Collection collection = modelUtility.getCollection(collectionEntity, false);
				List<Association> seriesAssociations = collectionEntity.getSourceAssociations(RepositoryModel.CATEGORIES);
				Collections.sort(seriesAssociations, associationSorter);
				for(Association seriesAssoc : seriesAssociations) {
					Entity target = getEntityService().getEntity(null, seriesAssoc.getTarget());
					Category series = new Category();
					String description = null;
					for(Association assoc : target.getSourceAssociations(SystemModel.NOTES)) {
						Entity note = getEntityService().getEntity(null, assoc.getTarget());
						if(note.getPropertyValue(SystemModel.NOTE_TYPE).equals("Scope and Contents note")) {
							description = note.getPropertyValue(SystemModel.NOTE_CONTENT);
						}
					}
					series.setDescription(description);
					collection.getSeries().add(series);
				}
				collections.add(collection);
			}
			
			for(Collection collection : collections) {
				buff.append("<node id='"+collection.getId()+"' type='collection' parent='null'>");
				buff.append("<name>"+collection.getTitle()+"</name>");
				for(Category series : collection.getSeries()) {
					buff.append("<node id='"+series.getId()+"' type='series' parent='"+collection.getId()+"' isFolder='false'>");
					buff.append("<name>"+series.getTitle()+"</name>");
					buff.append("<description>"+series.getDescription()+"</description>");
					buff.append("</node>");
				}
				buff.append("</node>");
			}
		}
				
		res.setContentType("text/xml");
		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("pragma","no-cache");
		return response("", buff.toString().trim(), res.getWriter());
	}
	
	@RequestMapping(value="/search/collections", method = RequestMethod.GET)
	public ModelAndView searchCollections(HttpServletRequest req, HttpServletResponse res, @RequestParam("code") String code, @RequestParam("query") String query) throws Exception {
		Map<String, Object> parms = new HashMap<String, Object>();
		
		parms.put("code", code);
		
		String sizeStr = req.getParameter("size");  //page size for result set (default 10)
		String pageStr = req.getParameter("page");
		String startStr = req.getParameter("start");
		String sort = req.getParameter("sort");
		int page = (NumberUtility.isInteger(pageStr)) ? Integer.valueOf(pageStr) : 1;
		int size = (NumberUtility.isInteger(sizeStr)) ? Integer.valueOf(sizeStr) : 10;
		long start = (startStr != null && NumberUtility.isLong(startStr)) ? Long.valueOf(startStr) : 0;
			
		SearchRequest searchRequest = getSearchRequest(RepositoryModel.COLLECTION, start, query, page, size, sort, false);
		if(query != null && query.equals("all results"))
			searchRequest.setQuery("code:"+code);
		else if(query != null && query.length() > 0) 
			searchRequest.setQuery("code:"+code+" and name_e:"+query);
		else searchRequest.setQuery("code:"+code);
		//SearchResponse result = getSearchService().search(searchRequest);
		
		//String output = toXml(searchRequest, result, false, false);
			
		parms.put("status", "0");
		
		return response("", ""/*output*/, res.getWriter());
	}
	@RequestMapping(value="/search/detail/{id}.{mime}", method = RequestMethod.GET)
	public ModelAndView component(HttpServletRequest req, HttpServletResponse res, @PathVariable("id") long id, @PathVariable("mime") String mime,
			@RequestParam(required=false,defaultValue="true") boolean sources, @RequestParam(required=false,defaultValue="true") boolean targets) throws Exception {
		StringBuffer buff = new StringBuffer();
		
		Entity entity = getEntityService().getEntity(null, Long.valueOf(id));
		if(mime.equals("xml")) {
			ExportProcessor processor = getEntityService().getExportProcessor(RepositoryModel.ITEM.toString());
			if(processor == null) processor = getEntityService().getExportProcessor("default");
			try {
				buff.append(processor.export(new FormatInstructions(true, false, sources, targets), entity));
				//String path = getComponentPath(entity);
			
			} catch(Exception e) {
				e.printStackTrace();
			}			
		}	
		res.setContentType("text/xml");
		res.setHeader("Cache-Control", "no-cache");
		res.setHeader("pragma","no-cache");
		res.setDateHeader ("Expires", -1);
		return response("", buff.toString().trim(), res.getWriter());
	}
	@RequestMapping(value="/collections/{repositoryId}", method = RequestMethod.GET)
	public ModelAndView collectionsByRepository(HttpServletRequest req, HttpServletResponse res,@PathVariable("repositoryId") Long repositoryId) throws Exception {
		//Map<String, Object> parms = new HashMap<String, Object>();
		StringBuffer buff = new StringBuffer("<data>");
		if(repositoryId != null && !repositoryId.equals(0L)) {
			Entity repository = getEntityService().getEntity(null, repositoryId);
			for(Association assoc : repository.getSourceAssociations(new QName("openapps_org_repository_1_0","collections"))) {
				Entity collection = getEntityService().getEntity(null, assoc.getTarget());
				Property name = collection.getProperty(new QName("openapps.org_system_1.0","name"));
				buff.append("<node id='"+collection.getId()+"'><title><![CDATA["+name.toString()+"]]></title></node>");
			}
		}
		res.getWriter().append(buff.toString()+"</data>");
		return null;
	}
	
	protected String getComponentPath(Entity comp) throws Exception {
		StringBuffer buff = new StringBuffer("<path>");
		List<Entity> path = new ArrayList<Entity>();
		Association parent = comp.getTargetAssociation(RepositoryModel.CATEGORIES);
		while(parent != null) {
			Entity p = getEntityService().getEntity(null, parent.getSource());
			path.add(p);
			parent = p.getTargetAssociation(RepositoryModel.CATEGORIES);
		}
		Collections.reverse(path);
		if(path.size() > 0) {
			Long parentId = null;
			for(int i=0; i < path.size(); i++) {
				Entity node = path.get(i);
				String title = node.getPropertyValue(SystemModel.NAME);
				String dateExpression = node.getPropertyValue(RepositoryModel.DATE_EXPRESSION);
				if(!node.getQName().equals(RepositoryModel.REPOSITORY)) {
					if(title != null  && !title.equals("")) buff.append("<node id='"+node.getId()+"' type='"+node.getQName().getLocalName()+"' parent='"+parentId+"'><title><![CDATA["+title+"]]></title></node>");
					else if(dateExpression != null) buff.append("<node id='"+node.getId()+"' type='"+node.getQName().getLocalName()+"' parent='"+parentId+"'><title>"+dateExpression+"</title></node>");
					parentId = node.getId();
				}
			}		
		}
		return buff.append("</path>").toString();
	}
	protected SearchRequest getSearchRequest(QName qname, long start, String query, int page, int size, String sort, boolean attributes) {
		SearchRequest sQuery = (qname == null) ? new SearchRequest(qname, query) : new SearchRequest(qname, query);
		sQuery.setAttributes(attributes);
		//sQuery.setPage(page);
		//sQuery.setPageSize(size);
		if(sort != null) {
			Sort lSort = sort.endsWith("_") ? new Sort(Sort.LONG, sort, true) : new Sort(Sort.STRING, sort, false);
			sQuery.addSort(lSort);
		} else {
			Sort lSort = new Sort(Sort.STRING, "name_e", true);
			sQuery.addSort(lSort);
		}
		if(start != 0) {
			sQuery.addParameter("path", String.valueOf(start));
		}		
		return sQuery;
	}
	
}
