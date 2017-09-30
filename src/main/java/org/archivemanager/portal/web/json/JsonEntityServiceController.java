package org.archivemanager.portal.web.json;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.QName;
import org.heed.openapps.data.RestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/entity")
public class JsonEntityServiceController extends WebserviceSupport {
	private final static Logger log = Logger.getLogger(JsonEntityServiceController.class.getName());
			
	
	@ResponseBody
	@RequestMapping(value="/search.json", method = RequestMethod.GET)
	public RestResponse<Object> search(HttpServletRequest request, HttpServletResponse response, @RequestParam("qname") String entityQname) throws Exception {
		QName qname = QName.createQualifiedName(entityQname);
		String query = request.getParameter("query");
		String field = request.getParameter("field");
		String sort = request.getParameter("sort");
		String sourcesStr = request.getParameter("sources");
		String targetsStr = request.getParameter("targets");
		String tokenizeStr = request.getParameter("tokenize");
		int startRow = (request.getParameter("_startRow") != null) ? Integer.valueOf(request.getParameter("_startRow")) : 0;
		int endRow = (request.getParameter("_endRow") != null) ? Integer.valueOf(request.getParameter("_endRow")) : 0;
		
		boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
		boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;
		boolean tokenize = (tokenizeStr != null && tokenizeStr.equals("true")) ? true : false;
		
		prepareResponse(response);
		
		return getEntityServiceSupport().search(new QName[]{qname}, query, field, sort, startRow, endRow, sources, targets, tokenize);
	}
	@ResponseBody
	@RequestMapping(value="/get.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchEntity(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=false) long id) throws Exception {
		String sourcesStr = request.getParameter("sources");
		String targetsStr = request.getParameter("targets");
		String view = request.getParameter("view");
		String uid = request.getParameter("uid");
		
		boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
		boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;
		
		prepareResponse(response);
		
		return getEntityServiceSupport().getEntity(id, uid, view, sources, targets);
	}
	@ResponseBody
	@RequestMapping(value="/get/{id}.json", method = RequestMethod.GET)
	public RestResponse<Object> getEntity(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) throws Exception {
		String sourcesStr = request.getParameter("sources");
		String targetsStr = request.getParameter("targets");
		String view = request.getParameter("view");
		
		boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
		boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;
		
		prepareResponse(response);
		
		return getEntityServiceSupport().getEntity(id, null, view, sources, targets);
	}
	@ResponseBody
	@RequestMapping(value="/add.json", method = RequestMethod.POST)
	public RestResponse<Object> addEntity(HttpServletRequest request, HttpServletResponse response,	@RequestParam("qname") String entityQname) throws Exception {
		QName qname = QName.createQualifiedName(entityQname);
		
		prepareResponse(response);	
		
		return getEntityServiceSupport().addEntity(qname, request);
	}
	@ResponseBody
	@RequestMapping(value="/update.json", method = RequestMethod.POST)
	public RestResponse<Object> updateEntity(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Long id) throws Exception {
		
		prepareResponse(response);
		
		return getEntityServiceSupport().updateEntity(id, request);
	}
	@ResponseBody
	@RequestMapping(value="/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeEntity(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
				
		prepareResponse(response);
		
		return getEntityServiceSupport().removeEntityById(id);
	}
	@ResponseBody
	@RequestMapping(value="/index.json", method = RequestMethod.GET)
	public RestResponse<Object> indexQName(HttpServletRequest request, HttpServletResponse response, @RequestParam("qname") String qname) throws Exception {
		QName q = QName.createQualifiedName(qname);
		
		prepareResponse(response);
		
		return getEntityServiceSupport().indexEntities(q);
	}
	
	@ResponseBody
	@RequestMapping(value="/get/children.json", method = RequestMethod.GET)
	public RestResponse<Object> getEntities(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
		String printTargets = request.getParameter("targets");
		String printSources = request.getParameter("sources");
		boolean sources = printSources != null ? Boolean.valueOf(printSources) : true;
		boolean targets = printTargets != null ? Boolean.valueOf(printTargets) : false;
				
		prepareResponse(response);
		
		return getEntityServiceSupport().getChildren(id, sources, targets);
	}
	
	@ResponseBody
	@RequestMapping(value="/association/add.json", method = RequestMethod.POST)
	public RestResponse<Object> associationAdd(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("assoc_qname") String assoc_qname,
			@RequestParam(required=false) String entity_qname,
			@RequestParam("source") String source, 
			@RequestParam(required=false) String target,
			@RequestParam(required=false) boolean targets) throws Exception {
		QName assocQname = QName.createQualifiedName(assoc_qname);
		QName entityQname = QName.createQualifiedName(entity_qname);
		prepareResponse(response);
		
		return getEntityServiceSupport().addAssociation(assocQname, entityQname, source, target, request, targets);
	}
	@ResponseBody
	@RequestMapping(value="/association/switch.json", method = RequestMethod.POST)
	public RestResponse<Object> switchAssociation(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("id") Long id, @RequestParam("source") Long sourceEntityId, @RequestParam("target") Long targetEntityId) throws Exception {
		
		return getEntityServiceSupport().switchAssociation(id, targetEntityId, sourceEntityId);
	}
	@ResponseBody
	@RequestMapping(value="/association/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeAssociation(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id,
			@RequestParam(required=false) String ticket) throws Exception {
		
		return getEntityServiceSupport().removeAssociation(id);
	}
	@ResponseBody
	@RequestMapping(value="/association/remove/target.json", method = RequestMethod.POST)
	public RestResponse<Object> removeAssociationTarget(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id, @RequestParam("target_id") Long target) throws Exception {
		
		return getEntityServiceSupport().removeEntityById(target);
	}
	
	protected void response(RestResponse<Object> data, int status, String message) {
		data.getResponse().setStatus(status);
		if(status == -1) {
			data.getResponse().addError(message);
			log.info(message);
		} else if(message != null && message.length() > 0){
			data.getResponse().addMessage(message);
			log.info(message);
		}
	}
	
}
