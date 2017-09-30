package org.archivemanager.portal.web.json;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.model.Category;
import org.archivemanager.model.Collection;
import org.archivemanager.portal.web.WebserviceSupport;
import org.archivemanager.util.EntityRepositoryModelUtil;
import org.archivemanager.util.SeriesSorter;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.User;
import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationSorter;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.JSONUtility;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.heed.openapps.search.data.SearchRestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/archivemanager")
public class JsonSearchServiceController extends WebserviceSupport {
	private final static Logger log = Logger.getLogger(JsonSearchServiceController.class.getName());
	private SeriesSorter seriesSorter = new SeriesSorter();
	
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value="/search.json", method = RequestMethod.GET)
	public SearchRestResponse<Object> search(HttpServletRequest req, HttpServletResponse res) throws Exception {
		SearchRestResponse<Object> data = new SearchRestResponse<Object>();
		String qname = req.getParameter("qname") != null ? req.getParameter("qname") : RepositoryModel.ITEM.toString();
		QName qName = QName.createQualifiedName(qname);
		String query = req.getParameter("query");
		User user = getSecurityService().getCurrentUser(req);
		
		//analyticsService.search(req.getQueryString());
		String startStr = req.getParameter("start");
		String endStr = req.getParameter("end");
		String sort = req.getParameter("sort");
		String ctx = req.getParameter("ctx");
		String attributes = req.getParameter("attributes");
		boolean sources = req.getParameter("sources") != null ? Boolean.valueOf(req.getParameter("sources")) : false;
		boolean targets = req.getParameter("targets") != null ? Boolean.valueOf(req.getParameter("targets")) : false;
		
		if(startStr == null) startStr = "0";
		if(endStr == null) endStr = "10";
		if(sort == null) sort = "name_e";
		
		SearchRequest searchRequest = getSearchRequest(qName, query, Integer.valueOf(startStr), Integer.valueOf(endStr), sort, true);
		searchRequest.setContext(ctx);
		searchRequest.setRequestParameters(req.getParameterMap());
		
		if(attributes != null && attributes.equals("true"))
			searchRequest.setAttributes(true);
		searchRequest.setUser(user);
		
		SearchResponse result = getSearchService().search(searchRequest);
			
		FormatInstructions instr = new FormatInstructions();
		instr.setFormat(FormatInstructions.FORMAT_JSON);
		instr.setPrintSources(sources);
		instr.setPrintTargets(targets);
			
		for(SearchResult searchResult : result.getResults()) {
			data.getResponse().addData(getEntityService().export(instr, searchResult.getEntity()));
		}
		data.getResponse().setTime(result.getTime());
		data.getResponse().setTotalRows(result.getResultSize());
		data.getResponse().setStartRow(result.getStartRow());
		data.getResponse().setEndRow(result.getEndRow());
		data.getResponse().setQuery(query);
		data.getResponse().setExplanation(result.getQueryExplanation());
		
		data.getResponse().setAttributes(result.getAttributes());
		data.getResponse().setBreadcrumb(result.getBreadcrumb());
		
		log.info("search for '"+query+"' returned "+result.getResultSize()+" results parsed to:"+result.getQueryExplanation());
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/search/series.json", method = RequestMethod.GET)
	public SearchRestResponse<Object> collection(HttpServletRequest req, HttpServletResponse res, @RequestParam("code") String code) throws Exception {
		SearchRestResponse<Object> data = new SearchRestResponse<Object>();
		EntityQuery query = new EntityQuery(RepositoryModel.COLLECTION, "code", code, "name_e", true);
		EntityResultSet<Entity> collectionResults = getEntityService().search(query);
		if(collectionResults != null && collectionResults.getResultSize() > 0) {
			EntityRepositoryModelUtil modelUtility = new EntityRepositoryModelUtil(getEntityService());
			List<Collection> collections = new ArrayList<Collection>();
			AssociationSorter associationSorter = new AssociationSorter(new Sort(Sort.STRING, SystemModel.NAME.getLocalName(), true));
			for(Entity collectionEntity : collectionResults.getResults()) {
				Collection collection = modelUtility.getCollection(collectionEntity, false);
				collection.setDescription("");
				List<Association> seriesAssociations = collectionEntity.getSourceAssociations(RepositoryModel.CATEGORIES);
				Collections.sort(seriesAssociations, associationSorter);
				for(Association seriesAssoc : seriesAssociations) {
					Entity target = getEntityService().getEntity(seriesAssoc.getTargetName(), seriesAssoc.getTarget());
					Category series = new Category();
					series.setId(String.valueOf(target.getId()));
					series.setTitle(target.getPropertyValue(SystemModel.NAME));
					String description = null;
					for(Association assoc : target.getSourceAssociations(SystemModel.NOTES)) {
						Entity note = getEntityService().getEntity(assoc.getTargetName(), assoc.getTarget());
						if(note.getPropertyValue(SystemModel.NOTE_TYPE).equals("Scope and Contents note")) {
							description = note.getPropertyValue(SystemModel.NOTE_CONTENT);
						}
					}
					if(description != null) series.setDescription(description);
					else series.setDescription("");
					collection.getSeries().add(series);
				}
				Collections.sort(collection.getSeries(), seriesSorter);
				collections.add(collection);
			}			
			for(Collection collection : collections) {
				data.getResponse().addData(collection);
			}
			data.getResponse().setTotalRows(collectionResults.getResultSize());
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/queries.json", method = RequestMethod.GET)
	public Map<String, Object> queries(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String query = req.getParameter("query");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("query", query);
		List<Map<String, String>> suggestions = new ArrayList<Map<String, String>>();
		suggestions.add(getSuggestion("1", "King", "General"));
		suggestions.add(getSuggestion("2", "Test Query #2", "General"));
		suggestions.add(getSuggestion("3", "Test Query #3", "General"));
		suggestions.add(getSuggestion("4", "Test Query #4", "General"));
		suggestions.add(getSuggestion("5", "Test Query #5", "General"));
		map.put("suggestions", suggestions);
		return map;
	}
	
	protected Map<String,String> getSuggestion(String id, String value, String type) {
		Map<String,String> entryMap = new HashMap<String, String>();
		entryMap.put("data", id);
		entryMap.put("value", value);
		entryMap.put("type", type);
		return entryMap;
	}
	protected SearchRequest getSearchRequest(QName qname, String query, int startRow, int endRow, String sort, boolean attributes) {
		SearchRequest sQuery = new SearchRequest(qname, query);
		sQuery.setAttributes(attributes);
		sQuery.setStartRow(startRow);
		sQuery.setEndRow(endRow);
		if(sort != null) {
			Sort lSort = null;
			String[] s = sort.split(" ");
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
		return sQuery;
	}
	
	public String toJson(Object o) {
		if(o == null) return "";
		else return JSONUtility.escape(o.toString());
	}
	
}
