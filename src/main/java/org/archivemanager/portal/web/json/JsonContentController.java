package org.archivemanager.portal.web.json;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.crawling.Crawler;
import org.heed.openapps.crawling.CrawlerImpl;
import org.heed.openapps.crawling.CrawlingEngine;
import org.heed.openapps.crawling.CrawlingModel;
import org.heed.openapps.crawling.DocumentImpl;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.util.FileUtility;
import org.heed.openapps.util.IOUtility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/service/archivemanager/content")
public class JsonContentController extends WebserviceSupport {
	
	
	@RequestMapping(value="/upload.json", method = RequestMethod.POST)
	public void upload(HttpServletRequest req, HttpServletResponse res) throws Exception {
		getContentServiceSupport().importUpload(req, res);
	}
	@ResponseBody
	@RequestMapping(value="/remove.json", method = RequestMethod.POST)
	public RestResponse<Object> removeAssociation(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("id") Long id) throws Exception {		
		return getContentServiceSupport().removeDigitalObject(id);
	}
	@RequestMapping(value="/stream/{id}")
	public ModelAndView stream(HttpServletRequest req, HttpServletResponse res, @PathVariable("id") long id) throws Exception {
		try {
			File root = getRootDirectory(id);
			File file = new File(root, id + ".bin");
			if(file.exists()) {
				res.setContentType("image/png");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".png\"");
				IOUtility.pipe(new FileInputStream(file), res.getOutputStream());			
			} else {
				URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
				IOUtility.pipe(url.openStream(), res.getOutputStream());
			}
		} catch(Exception e) {
			URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
			IOUtility.pipe(url.openStream(), res.getOutputStream());
			e.printStackTrace();
		}		
		return null;
	}
	@RequestMapping(value="/stream/original/{id}")
	public ModelAndView streamOriginal(HttpServletRequest req, HttpServletResponse res, @PathVariable("id") long id) throws Exception {
		try {
			Crawler crawler =null;
			DocumentImpl document = new DocumentImpl(getEntityService().getEntity(CrawlingModel.DOCUMENT, id));			
			List<Association> seeds = document.getTargetAssociations(CrawlingModel.DOCUMENTS);
			System.out.println(seeds.size() + " seeds found");
			for(Association seedAssoc : seeds) {
				Entity seed = getEntityService().getEntity(seedAssoc.getSourceName(), seedAssoc.getSource());
				List<Association> crawlers = seed.getTargetAssociations(CrawlingModel.SEEDS);
				System.out.println(crawlers.size() + " crawlers found");
				if(crawlers.size() > 0) {
					for(Association crawlerAssoc : crawlers) {
						crawler = new CrawlerImpl(getEntityService().getEntity(crawlerAssoc.getSourceName(), crawlerAssoc.getSource()));
					}
				} else {
					EntityQuery query = new EntityQuery(CrawlingModel.CRAWLER);
					@SuppressWarnings("unchecked")
					EntityResultSet<Entity> results = getEntityService().search(query);
					System.out.println(results.getResults().size() + " crawlers found");
					for(Entity crawlerEntity : results.getResults()) {
						Crawler c = new CrawlerImpl(crawlerEntity);
						if(document.getPath().startsWith(c.getPath())) {
							crawler = c;
						}
					}
				}
			}
			if(crawler != null) {
				CrawlingEngine engine = getCrawlingService().getEngine(crawler.getProtocol());					
				byte[] data = engine.load(crawler, document);
				String mimeType = FileUtility.getMimetype(document.getFile());
				res.setContentType(mimeType);
				res.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFile() + "\"");
				IOUtility.pipe(new ByteArrayInputStream(data), res.getOutputStream());
			} else {
				res.setContentType("image/png");
				res.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".png\"");
				URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
				IOUtility.pipe(url.openStream(), res.getOutputStream());
			}
		} catch(Exception e) {
			res.setContentType("image/png");
			res.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".png\"");
			URL url = new URL(getHostUrl(req)+"/theme/images/logo/ArchiveManager-viewer.png");
			IOUtility.pipe(url.openStream(), res.getOutputStream());
			e.printStackTrace();
		}		
		return null;
	}
	private File getRootDirectory(long id) {
		String idStr = String.valueOf(id);
		String homeDir = getPropertyService().getPropertyValue("home.dir") != null ? getPropertyService().getPropertyValue("home.dir") : "";
		File root = new File(homeDir + "/data/content");
		if(!root.exists()) root.mkdir();
		for(int i=0; i < idStr.length(); i++) {
			root = new File(root, Character.toString(idStr.charAt(i)));
			if(!root.exists()) root.mkdir();
		}
		return root;
	}
}