package org.archivemanager.portal.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.heed.openapps.data.RestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/service/home")
public class HomeController {

	
	@RequestMapping(value = "/test.json", method = RequestMethod.GET)
	@ResponseBody
    public RestResponse<Object> getHome(HttpServletRequest request, HttpServletResponse resp) {
		RestResponse<Object> response = new RestResponse<Object>();
		response.getResponse().getData().add("Test Result #1");
		return response;
	}
}
