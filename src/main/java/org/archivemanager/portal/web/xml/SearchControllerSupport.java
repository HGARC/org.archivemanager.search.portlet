package org.archivemanager.portal.web.xml;

import java.io.PrintWriter;

import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.XMLUtility;
import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.search.SearchAttribute;
import org.heed.openapps.search.SearchAttributeValue;
import org.heed.openapps.search.SearchNode;
import org.heed.openapps.search.SearchRequest;
import org.heed.openapps.search.SearchResponse;
import org.heed.openapps.search.SearchResult;
import org.springframework.web.servlet.ModelAndView;


public abstract class SearchControllerSupport extends WebserviceSupport {
	
	
	protected String toXml(EntityQuery query, EntityResultSet<Entity> results, int page, boolean printSources, boolean printTargets) {
		StringBuffer buff = new StringBuffer();
		ExportProcessor processor = getEntityService().getExportProcessor("default");
		buff.append("<request page='"+page+"'>");
		buff.append("<query><![CDATA["+query.getQueryString()+"]]></query>");
		buff.append("</request>");
		
		int pageCount = 0;
		if(query.getSize() == 0 || results.getResultSize() < query.getSize()) pageCount = 1;
		else {
			double ratio = (double)results.getResultSize() / query.getSize();
			pageCount = (int)(Math.ceil(ratio));
		}
		
		buff.append("<results page='"+page+"' pageSize='"+query.getSize()+"' pageCount='"+pageCount+"' resultCount='"+results.getResultSize()+"' time='0'>");
		for(int i=0; i < results.getResults().size(); i++) {
			buff.append("<result>");
			try {
				Entity entity = results.getResults().get(i);
				buff.append(processor.export(new FormatInstructions(true, false, printSources, printTargets), entity));
			} catch(Exception e) {
				e.printStackTrace();
			}
			buff.append("</result>");
		}
		buff.append("</results>");
		
		return buff.toString();
	}
	protected String toXml(SearchRequest request, SearchResponse response, boolean printSources, boolean printTargets) {
		StringBuffer buff = new StringBuffer();
		ExportProcessor processor = getEntityService().getExportProcessor(RepositoryModel.ITEM.toString());
		if(processor == null) processor = getEntityService().getExportProcessor("default");
		
		int size = response.getEndRow() - response.getStartRow();
		int page = response.getEndRow() / size;
		//int currentPage = response.getEndRow() / size;
		int pageCount = 0;
		
		if(size == 0 || response.getResultSize() < size) pageCount = 1;
		else {
			double ratio = (double)response.getResultSize() / size;
			pageCount = (int)(Math.ceil(ratio));
		}
		
		buff.append("<request page='"+page+"'>");
		buff.append("<query><![CDATA["+request.getQuery()+"]]></query>");
		buff.append("</request>");
		buff.append("<breadcrumb>");
		if(response.getBreadcrumb() != null) {
			for(SearchNode crumb : response.getBreadcrumb()) {
				buff.append("<crumb>");
				buff.append("<query><![CDATA["+XMLUtility.toXmlCdata(crumb.getQuery())+"]]></query>");
				buff.append("<label><![CDATA["+XMLUtility.toXmlCdata(crumb.getLabel())+"]]></label>");
				buff.append("</crumb>");
			}
		}
		buff.append("</breadcrumb>");
		buff.append("<results page='"+page+"' pageSize='"+size+"' pageCount='"+pageCount+"' resultCount='"+response.getResultSize()+"' time='"+response.getTime()+"'>");
		for(int i=0; i < response.getResults().size(); i++) {
			SearchResult result = response.getResults().get(i);
			buff.append("<result score='"+result.getNormalizedScore()+"' rawScore='"+result.getRawScore()+"'>");
			if(result.getData() != null && result.getData().size() > 0) {
				buff.append("<data>");
				for(String key : result.getData().keySet()) {
					buff.append("<"+key+"><![CDATA["+result.getData().get(key)+"]]></"+key+">");
				}
				buff.append("</data>");
			}
			try {
				buff.append(processor.export(new FormatInstructions(true, false, printSources, printTargets), result.getEntity()));
			} catch(Exception e) {
				e.printStackTrace();
			}
			buff.append("</result>");
		}
		buff.append("</results>");
		buff.append("<attributes>");
		for(SearchAttribute attribute : response.getAttributes()) {
			buff.append("<attribute>");
			buff.append("<name>"+XMLUtility.toXmlData(attribute.getName())+"</name>");
			for(SearchAttributeValue value :attribute.getValues()) {
				buff.append("<value>");
				buff.append("<count>"+value.getCount()+"</count>");
				buff.append("<query><![CDATA["+XMLUtility.toXmlCdata(value.getQuery())+"]]></query>");
				buff.append("<name><![CDATA["+XMLUtility.toXmlCdata(value.getName())+"]]></name>");
				buff.append("</value>");
			}
			buff.append("</attribute>");
		}
		buff.append("</attributes>");
		return buff.toString();
	}
	protected ModelAndView response(String message, String data, PrintWriter out) {
		out.write("<response><status>0</status><message>"+message+"</message>");
		out.write("<data>"+data+"</data>");
		out.write("</response>");
		return null;
	}
	
}
