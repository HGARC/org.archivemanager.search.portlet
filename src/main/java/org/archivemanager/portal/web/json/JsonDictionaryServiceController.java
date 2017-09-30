package org.archivemanager.portal.web.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.dictionary.ModelField;
import org.heed.openapps.dictionary.ModelRelation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/dictionary")
public class JsonDictionaryServiceController extends WebserviceSupport {
		
	@ResponseBody
	@RequestMapping(value="/models/fetch.json", method = RequestMethod.GET)
	public RestResponse<Object> fetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String parent = request.getParameter("parent");
		return getDictionaryServiceSupport().getModels(parent);
	}
	@ResponseBody
	@RequestMapping(value="/model/fetch.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchModel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("qname") String qname) throws Exception {
		return getDictionaryServiceSupport().getModel(qname);
	}
	@ResponseBody
	@RequestMapping(value="/model/add.json", method = RequestMethod.POST)
	public RestResponse<Object> add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String description = request.getParameter("description");
		
		return getDictionaryServiceSupport().addModel(namespace, localName, description);
	}
	@ResponseBody
	@RequestMapping(value="/model/update", method = RequestMethod.POST)
	public RestResponse<Object> updateModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String parent = request.getParameter("parent");
		String namespace = request.getParameter("namespace");
		String localName = request.getParameter("localName");
		String description = request.getParameter("description");
		
		return getDictionaryServiceSupport().updateModel(id, namespace, localName, description, parent);
	}
	@ResponseBody
	@RequestMapping(value="/model/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		
		return getDictionaryServiceSupport().removeModel(id);
	}
	@ResponseBody
	@RequestMapping(value="/field/add.json", method = RequestMethod.POST)
	public RestResponse<Object> addField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getDictionaryServiceSupport().addField(request);
	}
	@ResponseBody
	@RequestMapping(value="/relation/add.json", method = RequestMethod.POST)
	public RestResponse<Object> addRelation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getDictionaryServiceSupport().addRelation(request);
	}
	@ResponseBody
	@RequestMapping(value="/field/remove.json", method = RequestMethod.POST)
	public RestResponse<ModelField> removeModelField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RestResponse<ModelField> data = new RestResponse<ModelField>();
		String id = request.getParameter("id");
		if(id != null && id.length() > 0) {
			/*
			ModelField node = getDictionaryService().getSystemDictionary().getModelField(Long.valueOf(id));			
			ListNode listNode = new ListNode();
			listNode.setId(String.valueOf(node.getId()));
			listNode.setUid(node.getUid());
			getDictionaryService().remove(node.getId());
			data.getResponse().addData(node);
			*/
		} 
		
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/relation/update.json", method = RequestMethod.POST)
	public RestResponse<Object> updateRelation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getDictionaryServiceSupport().updateRelation(request);
	}
	@ResponseBody
	@RequestMapping(value="/field/update.json", method = RequestMethod.POST)
	public RestResponse<Object> updateField(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getDictionaryServiceSupport().updateField(request);
	}
	@ResponseBody
	@RequestMapping(value="/relation/remove.json", method = RequestMethod.POST)
	public RestResponse<ModelRelation> removeModelRelation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RestResponse<ModelRelation> data = new RestResponse<ModelRelation>();
		String id = request.getParameter("id");
		if(id != null && id.length() > 0) {
			/*
			ModelRelation node = getDictionaryService().getModelRelation(Long.valueOf(id));			
			ListNode listNode = new ListNode();
			listNode.setId(String.valueOf(node.getId()));
			listNode.setUid(node.getUid());
			getDictionaryService().remove(node);
			data.getResponse().addData(node);
			*/
		} 
		return data;
	}
	
	
}
