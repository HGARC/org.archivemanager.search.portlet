package org.archivemanager.portal.web.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.QName;
import org.heed.openapps.data.Sort;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.NumberUtility;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.data.SearchRestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/archivemanager")
public class NotableFigureEntrySearchController extends WebserviceSupport {
	
	
	public NotableFigureEntrySearchController() {
		
	}
	
	@ResponseBody
	@RequestMapping(value="/{code}/search/entries", method = RequestMethod.GET)
	public SearchRestResponse<Object> searchNotableFigureEntries(HttpServletRequest req, HttpServletResponse res, @PathVariable("code") String code,
			@RequestParam(required=false, defaultValue="") String query, @RequestParam(required=false, defaultValue="") String name,
			@RequestParam(required=false, defaultValue="") String description, @RequestParam(required=false, defaultValue="") String collection) throws Exception {
		SearchRestResponse<Object> data = new SearchRestResponse<Object>();
				
		String sizeStr = req.getParameter("size");  //page size for result set (default 10)
		String pageStr = req.getParameter("page");
		String startStr = req.getParameter("start");
		String sort = req.getParameter("sort");
		int page = (NumberUtility.isInteger(pageStr)) ? Integer.valueOf(pageStr) : 1;
		int size = (NumberUtility.isInteger(sizeStr)) ? Integer.valueOf(sizeStr) : 10;
		long start = (startStr != null && NumberUtility.isLong(startStr)) ? Long.valueOf(startStr) : 0;
		
		/*
		String searchQuery = "";
		if(query.length() > 0) {
			searchQuery += query;
		} else if(name.length() > 0) {
			searchQuery += " "+name;
		} else if(description.length() > 0) {
			searchQuery += " "+description;
		} else if(collection.length() > 0) {
			searchQuery += " "+collection;
		} else {
			searchQuery += "all results";
		}	
		CollectionSearchRequest searchRequest = getSearchRequest(ClassificationModel.ENTRY, 0, searchQuery.trim(), page, size, "name", false);
		
		
		SearchResponse result = getSearchService().search(searchRequest);
		
		String output = toXml(searchRequest, result, false, true);
		*/
		
		//String output = "";
		
		int startRow = (page*size)-size;
		int endRow = (page*size);
		EntityQuery entryQuery = null;		
		if(query.length() > 0) {
			SearchRequest searchRequest = getSearchRequest(ClassificationModel.ENTRY, start, query, page, size, sort, false);
			if(query != null && !query.equals("all results")) {
				searchRequest.setQuery("name_e:"+query+"*");
			}
			//SearchResponse result = getSearchService().search(searchRequest);			
			//output = toXml(searchRequest, result, false, true);
			
		} else if(name.length() > 0) {
			entryQuery = new EntityQuery(ClassificationModel.ENTRY, "name", name, "name", true);
		} else if(description.length() > 0) {
			entryQuery = new EntityQuery(ClassificationModel.ENTRY, "items", description, "name", true);
		} else if(collection.length() > 0) {
			entryQuery = new EntityQuery(ClassificationModel.ENTRY, "collection_name", collection, "name", true);
		} else {
			entryQuery = new EntityQuery(ClassificationModel.ENTRY);
		}		
		
		if(entryQuery != null) {
			entryQuery.setSize(size);
			entryQuery.setStartRow(startRow);
			entryQuery.setEndRow(endRow);
					
			EntityResultSet<Entity> entryResults = getEntityService().search(entryQuery);
			
			FormatInstructions instr = new FormatInstructions();
			instr.setFormat(FormatInstructions.FORMAT_JSON);
			instr.setPrintSources(true);
			instr.setPrintTargets(true);
			
			for(Entity result : entryResults.getResults()) {
				data.getResponse().addData(getEntityService().export(instr, result));
			}
			//data.getResponse().setTime(entryResults.getTime());
			data.getResponse().setTotalRows(entryResults.getResultSize());
			data.getResponse().setStartRow(entryResults.getStartRow());
			data.getResponse().setEndRow(entryResults.getEndRow());
			data.getResponse().setQuery(query);
			
			//getLoggingService().info("search for '"+query+"' returned "+entryResults.getResultSize()+" results parsed to:"+result.getQueryExplanation());
		}
		return data;
	}
	
	protected SearchRequest getSearchRequest(QName qname, long start, String query, int page, int size, String sort, boolean attributes) {
		SearchRequest sQuery = new SearchRequest(qname, query);
		sQuery.setAttributes(attributes);
		//sQuery.setPage(page);
		//sQuery.setPageSize(size);
		if(sort != null) {
			Sort lSort = null;
			String[] s = sort.split("_");
			if(s.length == 2) {
				boolean reverse = s[1].equals("a") ? true : false;
				lSort = new Sort(Sort.STRING, s[0], reverse);						
			} else if(s.length == 1) {
				lSort = new Sort(Sort.STRING, sort, false);
			}
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
