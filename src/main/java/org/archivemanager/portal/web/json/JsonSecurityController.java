package org.archivemanager.portal.web.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.data.IDTypeName;
import org.heed.openapps.data.RestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/service/security")
public class JsonSecurityController extends WebserviceSupport {
	//private final static Logger log = Logger.getLogger(JsonSecurityController.class.getName());
	
	
	@ResponseBody
	@RequestMapping(value="/search.json", method = RequestMethod.GET)
	public RestResponse<IDTypeName> search(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String query = request.getParameter("query");
		String sourcesStr = request.getParameter("sources");
		String targetsStr = request.getParameter("targets");		
		int startRow = (request.getParameter("_startRow") != null) ? Integer.valueOf(request.getParameter("_startRow")) : 0;
		int endRow = (request.getParameter("_endRow") != null) ? Integer.valueOf(request.getParameter("_endRow")) : 0;
		
		boolean sources = (sourcesStr != null && sourcesStr.equals("true")) ? true : false;
		boolean targets = (targetsStr != null && targetsStr.equals("true")) ? true : false;
		
		prepareResponse(response);
		
		return getSecurityServiceSupport().search(query, startRow, endRow, sources, targets);
	}
}
