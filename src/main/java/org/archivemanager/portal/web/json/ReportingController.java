package org.archivemanager.portal.web.json;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.job.FindingAidGenerationJob;
import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.util.FileUtility;
import org.heed.openapps.util.HttpUtility;
import org.heed.openapps.util.IOUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.liferay.portal.kernel.util.MimeTypesUtil;


@Controller
@RequestMapping("/service/archivemanager")
public class ReportingController extends WebserviceSupport {
	
	
	@ResponseBody
	@RequestMapping(value="/reporting/finding_aid/generate.json", method = RequestMethod.GET)
	public Map<String, Object> exportCollection(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("id") Long id, @RequestParam("format") String format, @RequestParam("title") String title,
			@RequestParam("schedule") boolean schedule) throws Exception {
		StringWriter buff = new StringWriter();
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		FindingAidGenerationJob job = new FindingAidGenerationJob(id, format, title);
		job.setEntityService(getEntityService());
		job.setReportingService(getReportingService());
		if(schedule) getSchedulingService().run(job);
		else job.execute();
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );
		
		if(job != null && job.getId() != null) {
			buff.append("<node localName='job' id='"+job.getId()+"' uid='"+job.getUid()+"' />");
			map.put("localName", "job");
			map.put("id", job.getId());
			map.put("uid", job.getUid());
			map.put("status", "0");
			map.put("message", "collection report generation job running");
		}
		else {
			map.put("status", "-1");
			map.put("message", "problem running collection report generation job");
		}
		return map;
	}
	@RequestMapping(value="/reporting/stream/{name}", method = RequestMethod.GET)
	public void exportCollection(HttpServletRequest request, HttpServletResponse response, 
			@PathVariable("name") String name) throws IOException { 		
		String lastPath = HttpUtility.getLastPathName(request);
		String id = FileUtility.getExtension(lastPath).length() > 0 ? lastPath.substring(0, lastPath.lastIndexOf(".")) : lastPath;
		String mimetype = FileUtility.getMimetype(lastPath);
		String mime = FileUtility.getExtension(lastPath);
		try {
			response.addHeader("pragma", "no-store,no-cache");
			response.addHeader("cache-control", "no-cache, no-store,must-revalidate");
			response.addHeader("expires", "-1");
			response.addHeader("Content-Type", MimeTypesUtil.getContentType(lastPath));
			FileInputStream in = new FileInputStream(getPropertyService().getPropertyValue("home.dir") + "/data/reports/"+id+"."+mime);
			if(in != null) {
				response.setContentType(FileUtility.getMimetype(mimetype));
				IOUtility.pipe(in, response.getOutputStream());					
			}
		} catch(Exception e) {
			throw new IOException(e);
		}
	}
}
