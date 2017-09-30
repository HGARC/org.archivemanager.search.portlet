package org.archivemanager.portal.web.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.heed.openapps.data.RestResponse;
import org.archivemanager.portal.web.WebserviceSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/entity")
public class JsonNodeServiceController extends WebserviceSupport {
				
	
	
	
	@ResponseBody
	@RequestMapping(value="/node/browse.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String parent = request.getParameter("parent");
		String startStr = request.getParameter("_startRow");
		String endStr = request.getParameter("_endRow");
		String mode = request.getParameter("mode");
		
		int start = Integer.valueOf(startStr);
		int end = Integer.valueOf(endStr);
		
		return getNodeServiceSupport().browse(parent,  mode, start, end);
	}
	
	@ResponseBody
	@RequestMapping(value="/node/get.json", method = RequestMethod.GET)
	public RestResponse<Object> getEntityJSON(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
		return getNodeServiceSupport().getNode(id);
	}
	@ResponseBody
	@RequestMapping(value="/node/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeEntity(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Long id) throws Exception {
		return getNodeServiceSupport().removeNode(id);
	}
		
}
