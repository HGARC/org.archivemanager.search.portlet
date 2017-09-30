package org.archivemanager.portal.web.json;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.data.FormatInstructions;
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
@RequestMapping("/service/entity")
public class JsonEntityExportController extends WebserviceSupport {
	
	
	@ResponseBody
	@RequestMapping(value="/export.json", method = RequestMethod.GET)
	public Map<String, Object> exportCollection(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("id") Long id, @RequestParam("title") String title, @RequestParam("format") String format) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
				
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );
		
		Entity entity = getEntityService().getEntity(null, id);
		
		FormatInstructions formatInstr = new FormatInstructions(false,true,true);
		formatInstr.setFormat(format);
		String xml = (String)getEntityService().export(formatInstr, entity);
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
		FileOutputStream out = new FileOutputStream(getPropertyService().getPropertyValue("home.dir") + "/tomcat-7.0.42/webapps/exports/"+title+"."+format);
		IOUtility.pipe(in, out);
		
		map.put("localName", entity.getQName().getLocalName());
		map.put("id", entity.getId());
		map.put("uid", entity.getUid());
		map.put("status", "0");
		map.put("message", "oaxml generation job running");
		
		return map;
	}
	@RequestMapping(value="/data/export/stream/{name}", method = RequestMethod.GET)
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
			FileInputStream in = new FileInputStream(getPropertyService().getPropertyValue("home.dir") + "/data/exports/"+id+"."+mime);
			if(in != null) {
				response.setContentType(FileUtility.getMimetype(mimetype));
				IOUtility.pipe(in, response.getOutputStream());					
			}
		} catch(Exception e) {
			throw new IOException(e);
		}
	}
}