package org.archivemanager.portal.web.json;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.QName;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationImpl;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.ValidationResult;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.NumberUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/archivemanager")
public class JsonLocationController extends WebserviceSupport {
	
	
	@ResponseBody
	@RequestMapping(value="/locations.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchLocationTreeData(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=false) String ticket) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		/*
		User user = ticket == null ? securityService.currentUser() : securityService.currentUser(ticket);
		if(user.isGuest()) {
			data.getResponse().addMessage("security exception : must be authenticated.");
			return data;
		}
		*/
		QName qname = RepositoryModel.LOCATION;
		String parent = request.getParameter("parent");
		if(parent == null || parent.equals("null") || parent.equals("0")) {
			String query = request.getParameter("query");
			EntityQuery eQuery = new EntityQuery(qname, null, null, true);
			if(query != null && !query.equals("null")) {
				eQuery.setQueryString("collection:"+query+" name:"+query);
			} 
			int startRow = (request.getParameter("_startRow") != null) ? Integer.valueOf(request.getParameter("_startRow")) : 0;
			int endRow = (request.getParameter("_endRow") != null) ? Integer.valueOf(request.getParameter("_endRow")) : 0;
			eQuery.setStartRow(startRow);
			eQuery.setEndRow(endRow);
			eQuery.setDefaultOperator("OR");
			EntityResultSet<Entity> results = getEntityService().search(eQuery);
			ExportProcessor processor = getEntityService().getExportProcessor("default");
			FormatInstructions instructions = new FormatInstructions(false, false, false);
			instructions.setFormat(FormatInstructions.FORMAT_JSON);
			for(int i=0; i < results.getResults().size(); i++) {
				data.getResponse().getData().add(processor.export(instructions, results.getResults().get(i)));
			}
			data.getResponse().setTotalRows(results.getResults().size());
			data.getResponse().setStartRow(startRow);
			data.getResponse().setEndRow(endRow);
			//return response(results.getResultSize()+" "+qname.getLocalName()+" fetched", results.getResultSize(), results.getStartRow(), results.getEndRow(), buff.toString(), response.getWriter());
		} else {
			Entity collection = getEntityService().getEntity(RepositoryModel.COLLECTION, Long.valueOf(parent));
			List<Entity> list = new ArrayList<Entity>();
			for(Association assoc : collection.getSourceAssociations(RepositoryModel.LOCATIONS)) {
				Entity accession = getEntityService().getEntity(RepositoryModel.LOCATION, assoc.getTarget());
				//accession.setParent(collection);
				list.add(accession);
			}
			//Collections.sort(list, sorter);
			//Collections.reverse(list);
			ExportProcessor processor = getEntityService().getExportProcessor("default");
			for(Entity component : list) {	
				data.getResponse().getData().add(processor.export(new FormatInstructions(true), component));
			}
			//return response("", buff.toString(), response.getWriter());			
		}
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value="/location/add.json", method = RequestMethod.POST)
	public RestResponse<Object> entityAssociationAdd(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=false) String ticket) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		/*
		User user = ticket == null ? securityService.currentUser() : securityService.currentUser(ticket);
		if(user.isGuest()) {
			data.getResponse().addMessage("security exception : must be authenticated.");
			return data;
		}
		*/
		QName aQname = RepositoryModel.LOCATIONS;
		QName eQname = RepositoryModel.LOCATION;
		String sourceStr = request.getParameter("source");
		Entity entity = getEntityService().getEntity(request, eQname);
		ValidationResult entityResult = getEntityService().validate(entity);
		if(entityResult.isValid()) {
			getEntityService().addEntity(entity);
			if(sourceStr != null && NumberUtility.isLong(sourceStr)) {
				Long source = Long.valueOf(sourceStr);
				//entity.setParent(getEntityService().getEntity(source));
				Association assoc = new AssociationImpl(aQname, source, entity.getId());
				ValidationResult assocResult = getEntityService().validate(assoc);
				if(assocResult.isValid()) {
					getEntityService().addAssociation(assoc);
					entity.getTargetAssociations().add(assoc);
				}
			}
			ExportProcessor processor = getEntityService().getExportProcessor("default");
			FormatInstructions instructions = new FormatInstructions(false, false, false);
			instructions.setFormat(FormatInstructions.FORMAT_JSON);
			data.getResponse().getData().add(processor.export(instructions, entity));
			data.getResponse().setTotalRows(1);
			data.getResponse().setStartRow(0);
			data.getResponse().setEndRow(1);
		} else {
			
		}
		return data;
	}
	
}
