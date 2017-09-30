package org.archivemanager.portal.web.html;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/service/archivemanager/content")
public class WebContentController {

	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ModelAndView upload(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String, Object> parms = new HashMap<String, Object>();
		
		
		return new ModelAndView("html/logo.jsp", parms);
	}
}
