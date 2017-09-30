package org.archivemanager.portal.web.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.data.RestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/scheduling")
public class SchedulingController extends WebserviceSupport {

	
	@ResponseBody
	@RequestMapping(value="/status.json", method = RequestMethod.GET)
	public RestResponse<Object> fetchEntity(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required=true) String uid) throws Exception {
		
		prepareResponse(response);
		
		return getSchedulingServiceSupport().getStatus(uid);
	}
}
