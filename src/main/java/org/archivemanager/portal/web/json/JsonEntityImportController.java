package org.archivemanager.portal.web.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.util.HttpUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/entity")
public class JsonEntityImportController extends WebserviceSupport {
	
	
	@ResponseBody
	@RequestMapping(value="/import/add.json", method = RequestMethod.POST)
	public RestResponse<Object> add(HttpServletRequest req, HttpServletResponse res, @RequestParam("session") String session) throws Exception {
		return getEntityServiceSupport().importAdd(session);
	}
	@ResponseBody
	@RequestMapping(value="/import/fetch.json", method = RequestMethod.GET)
	public RestResponse<Object> fetch(HttpServletRequest req, HttpServletResponse res, @RequestParam("session") String session) throws Exception {
		int start = HttpUtility.getParmInt(req, "_startRow");
		int end = HttpUtility.getParmInt(req, "_endRow");
		
		return getEntityServiceSupport().importFetch(session, start, end);
	}
	@ResponseBody
	@RequestMapping(value="/import/select.json", method = RequestMethod.GET)
	public RestResponse<Object> select(HttpServletRequest req, HttpServletResponse res, @RequestParam("session") String session, @RequestParam("id") String id) throws Exception {
		return getEntityServiceSupport().importSelect(session, id);
	}
	@ResponseBody
	@RequestMapping(value="/import/entity.json", method = RequestMethod.POST)
	public RestResponse<Entity> loadEntity(HttpServletRequest req, HttpServletResponse res) throws Exception {
		RestResponse<Entity> response = new RestResponse<Entity>();
		long collectionId = HttpUtility.getParmLong(req, "collectionId");
		if(collectionId > 0) {
			
		}
		return response;
	}
	@RequestMapping(value="/import/upload.json", method = RequestMethod.POST)
	public void upload(HttpServletRequest req, HttpServletResponse res) throws Exception {
		getEntityServiceSupport().importUpload(req, res);
	}
	@ResponseBody
	@RequestMapping(value="/import/processors.json", method = RequestMethod.GET)
	public RestResponse<Object> processors(HttpServletRequest req, HttpServletResponse res, @RequestParam("namespace") String namespace, @RequestParam("localname") String localname) throws Exception {
		return getEntityServiceSupport().importProcessors(namespace, localname);
	}
	
}