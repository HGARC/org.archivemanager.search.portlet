package org.archivemanager.portal.web.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.content.Datasource;
import org.heed.openapps.content.DirectoryNode;
import org.heed.openapps.content.DirectorySettings;
import org.heed.openapps.content.DirectorySettingsUtility;
import org.heed.openapps.content.FileNode;
import org.heed.openapps.content.FileSettings;
import org.heed.openapps.data.RestResponse;
import org.heed.openapps.content.ContentModel;
import org.heed.openapps.content.data.DirectoryNodeXmlImportHandler;
import org.heed.openapps.content.data.FileNodeXmlImportHandler;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityImpl;
import org.heed.openapps.entity.EntityQuery;
import org.heed.openapps.entity.EntityResultSet;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.InvalidAssociationException;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.IOUtility;
import org.heed.openapps.util.NumberUtility;
import org.heed.openapps.util.XMLUtility;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


//@Controller
//@RequestMapping("/service/archivemanager")
public class ContentServiceController extends WebserviceSupport {
	private final static Logger log = Logger.getLogger(ContentServiceController.class.getName());
	private static final List<String> exclusions = new ArrayList<String>();
	//private Format dateFormatter = new SimpleDateFormat("MMMM d yyyy HH:mm a");
	
	static {
		exclusions.add("System Volume Information");
		exclusions.add("AI_RecycleBin");
	}
	
	@ResponseBody
	@RequestMapping(value="/datasource/folders/browse.json", method = RequestMethod.GET)
	public RestResponse<Object> browseFolders(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		RestResponse<Object> data = browse(req, res, false);
		
		return data;
		//return response(0, "", browse(req, res, false), res.getWriter());
	}
	@ResponseBody
	@RequestMapping(value="/datasource/files/browse.json", method = RequestMethod.GET)
	public RestResponse<Object> browseFiles(HttpServletRequest req, HttpServletResponse res) throws Exception {
		RestResponse<Object> data = browse(req, res, true);
		
		return data;
	}
	@SuppressWarnings("unchecked")
	protected RestResponse<Object> browse(HttpServletRequest req, HttpServletResponse res, boolean printFiles) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		FileNodeXmlImportHandler handler = new FileNodeXmlImportHandler();
		String parent = req.getParameter("parent");
		//String query = req.getParameter("query");
		if(parent == null || parent.equals("null") || parent.equals("")) {			
			EntityQuery eQuery = new EntityQuery(ContentModel.DATASOURCE);
			eQuery.getFields().add("name");
			EntityResultSet<Entity> results = getEntityService().search(eQuery);
			for(Entity dsEntity : results.getResults()) {
				//detatchFoldersFix(dsEntity);
				printDatasource(data, dsEntity);
			}
			data.getResponse().setStartRow(results.getStartRow());
			data.getResponse().setEndRow(results.getEndRow());
			data.getResponse().setTotalRows(results.getResults().size());
		} else {
			Object obj = getCacheService().get("objectCache", parent);
			if(obj != null) {
				Datasource source = null;
				FileNode node = null;
				if(obj instanceof Datasource) {
					source = (Datasource)obj;
				} else if(obj instanceof FileNode) {
					node = (FileNode)obj;
					source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
				}
				if(source.getType().equals("loc")) {
					if(node == null) node = new FileNode(parent, parent, source.getUrl());
					if(node != null) {
						File file = new File(node.getPath());
						if(file != null && file.exists()) {
							for(File dir : file.listFiles(DirectorySettingsUtility.directoryFilter)) {
								File settingsFile = DirectorySettingsUtility.getSettingsFile(dir);
								String uuid = DirectorySettingsUtility.getUUID(settingsFile);
								FileNode n = (FileNode)getCacheService().get("objectCache", uuid);
								if(n == null) n = new FileNode(source.getUid(), uuid, dir.getPath());
								n.setName(dir.getName());
								File[] dirs = dir.listFiles(DirectorySettingsUtility.directoryFilter);
								if(dirs.length > 0) n.setChildren(true);
								else n.setChildren(false);
								printNode(data, source, n);
								getCacheService().put("objectCache", uuid, n);
							}
						}
					}						
				} else {						
					String url = parent.equals(source.getUid()) ?
							source.getUrl() + "/datasource/folders/browse.xml" :
								source.getUrl() + "/datasource/folders/browse.xml?uid="+parent;
					if(!url.startsWith("http://")) url = "http://"+url;
					String responseData = get(url);
					if(data != null && responseData.length() > 0) {
						XMLUtility.SAXParse(false, responseData, handler);
						List<FileNode> nodes = handler.getNodes();
						for(FileNode n : nodes) {
							n.setDatasource(source.getUid());
							printNode(data, source, n);
							getCacheService().put("objectCache", n.getUid(), n);
						}
						data.getResponse().setStartRow(0);
						data.getResponse().setEndRow(nodes.size());
						data.getResponse().setTotalRows(nodes.size());
					}
				}
			}
		}
		return data;
	}
	@ResponseBody
	@RequestMapping(value="/content/get.json", method = RequestMethod.GET)
	public RestResponse<Object> get(HttpServletRequest req, HttpServletResponse response, @RequestParam("uid") String uid) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();			
		Object obj = getCacheService().get("objectCache", uid);
		if(obj != null) {
			Datasource source = null;
			FileNode node = null;
			Entity entity = null;			
			if(obj instanceof Datasource) {
				Datasource ds = (Datasource)obj;
				entity = getEntityService().getEntity(null, ds.getId());
				getCacheService().put("objectCache", ds.getUid(), ds);
				FormatInstructions fmt = new FormatInstructions(false, true, true);
				data.getResponse().addData(getEntityService().export(fmt, entity));
			} else if(obj instanceof FileNode) {
				node = (FileNode)obj;
				source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
				entity = getEntityService().getEntity(ContentModel.FOLDER, uid);
				if(entity != null) {
					FormatInstructions fmt = new FormatInstructions(false, true, true);
					data.getResponse().addData(getEntityService().export(fmt, entity));
				}
				else if(node != null) {
					if(source.getType().equals("loc")) {
						printDirectory(data, source, node);
					} else {
						printRemoteDirectory(data, source, uid);
					}
				}
			}			
		}
		
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );
		return data;
	}
	@RequestMapping(value="/content/stream/{uid}.{extension}")
	public ModelAndView stream(HttpServletRequest req, HttpServletResponse res, @PathVariable("uid") String uid, @PathVariable("extension") String extension) throws Exception {
		return stream(req, res, uid);
	}
	@RequestMapping(value="/content/stream/{uid}")
	public ModelAndView stream(HttpServletRequest req, HttpServletResponse res, @PathVariable("uid") String uid) throws Exception {
		Entity obj = getEntityService().getEntity(null, uid);
		//Object obj = getCacheService().get("objectCache", uid);
		if(obj != null) {
			//FileNode node = (FileNode)obj;
			//Datasource source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
			Entity source = getEntityService().getEntity(null, obj.getPropertyValue(ContentModel.DATASOURCE));
			if(source != null) {
				String protocol = source.getPropertyValue(ContentModel.DATASOURCE_PROTOCOL);
				String dsUrl = source.getPropertyValue(ContentModel.DATASOURCE_URL);
				if(protocol.equals("loc")) {
					String path = obj.getPropertyValue(ContentModel.PATH);
					IOUtility.pipe(new FileInputStream(new File(path)), res.getOutputStream());
				} else if(protocol.equals("rem")) { 
					String url = dsUrl + "/datasource/folders/stream.xml?uid="+uid;
					if(!url.startsWith("http://")) url = "http://"+url;
					pipe(url, res.getOutputStream());
				} else {
					//log.info("stream request received too large:"+node.getSize());
					URL url = new URL(getHostUrl(req)+"/theme/images/logo/Logo500_550.png");
					IOUtility.pipe(url.openStream(), res.getOutputStream());
				}
				
			}
		}		
		return null;
	}
	@ResponseBody
	@RequestMapping(value="/content/file/associate.json", method = RequestMethod.POST)
	public RestResponse<Object> addFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("source") String source, @RequestParam("target") String target) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();	
		Entity sourceEntity = NumberUtility.isLong(source) ? getEntityService().getEntity(null, Long.valueOf(source)) : getEntityService().getEntity(null, source);
		Entity targetEntity = null;
		try {
			targetEntity = getEntityService().getEntity(null, target);
		} catch(Exception e) {
			
		}
		if(targetEntity == null) {
			Object obj = getCacheService().get("objectCache", target);
			if(obj != null) {
				FileNode node = (FileNode)obj;
				targetEntity = new EntityImpl(ContentModel.FILE);
				targetEntity.setUid(target);
				if(node.getName() != null) targetEntity.addProperty(SystemModel.NAME, node.getName());
				targetEntity.addProperty(ContentModel.PATH, node.getPath());
				targetEntity.addProperty(ContentModel.CONTENT_SIZE, node.getSize());
				targetEntity.addProperty(ContentModel.DATASOURCE, node.getDatasource());
				getEntityService().addEntity(targetEntity);
			}
		}
		if(sourceEntity != null && targetEntity != null) {
			Association assoc = getEntityService().getAssociation(request, QName.createQualifiedName("{openapps.org_content_1.0}files"));
			assoc.setSourceEntity(sourceEntity);
			assoc.setTargetEntity(targetEntity);
			getEntityService().addAssociation(assoc);
			ExportProcessor processor = getEntityService().getExportProcessor(sourceEntity.getQName().toString());
			if(processor == null) processor = getEntityService().getExportProcessor("default");
			//return response(0, "", (String)processor.export(new FormatInstructions(false, true, false), assoc), response.getWriter());
		}
		return data;
	}
	/*
	 @ResponseBody
	@RequestMapping(value="/content/file/associate.json", method = RequestMethod.POST)
	public RestResponse<Object> addFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("source") String source, @RequestParam("target") String target) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		Entity sourceEntity = NumberUtility.isLong(source) ? getEntityService().getEntity(Long.valueOf(source)) : getEntityService().getEntity(source);
		Entity targetEntity = null;
		try {
			targetEntity = getEntityService().getEntity(target);
		} catch(Exception e) {
			
		}
		if(targetEntity == null) {
			Object obj = getCacheService().get("objectCache", target);
			if(obj != null) {
				FileNode node = (FileNode)obj;
				targetEntity = new Entity(ContentModel.FILE);
				targetEntity.setUid(target);
				if(node.getName() != null) targetEntity.addProperty(SystemModel.NAME, node.getName());
				targetEntity.addProperty(ContentModel.PATH, node.getPath());
				targetEntity.addProperty(ContentModel.CONTENT_SIZE, node.getSize());
				targetEntity.addProperty(ContentModel.DATASOURCE, node.getDatasource());
				getEntityService().addEntity(targetEntity);
			}
		}
		if(sourceEntity != null && targetEntity != null) {
			Association assoc = getEntityService().getAssociation(request, QName.createQualifiedName("openapps_org_content_1_0_files"));
			assoc.setSourceEntity(sourceEntity);
			assoc.setTargetEntity(targetEntity);
			getEntityService().addAssociation(assoc);
			ExportProcessor processor = getEntityService().getExportProcessor(sourceEntity.getQName().toString());
			if(processor == null) processor = getEntityService().getExportProcessor("default");
			FormatInstructions instructions = new FormatInstructions(false, true, false);
			data.getResponse().getData().add(processor.export(instructions, sourceEntity));
		}
		data.getResponse().addMessage("problem adding association between source:"+source+" target:"+target);
		return data;
	}
	 
	@ResponseBody
	@RequestMapping(value="/content/update", method = RequestMethod.POST)
	public RestResponse<Object> update(HttpServletRequest request, HttpServletResponse response, @RequestParam("uid") String uid, @RequestParam("qname") String qnameStr) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		Entity entity = null;
		try {				
			QName qname = QName.createQualifiedName(qnameStr);
			entity = getEntityService().getEntity(qname, uid);
			Entity newEntity = getEntityService().getEntity(request, qname);
			ValidationResult result = getEntityService().validate(newEntity);
			if(result.isValid()) {
				if(entity == null) {
					entity = new Entity(qname);
					entity.setUid(uid);
				}
				for(Property prop : newEntity.getProperties()) {
					entity.addProperty(prop.getQName(), prop.getValue());
				}	
				if(entity.getId() == null) getEntityService().addEntity(entity);
				else {
					getEntityService().updateEntity(entity);
				}
			}			
			if(qname.equals(ContentModel.DATASOURCE)) {
				printDatasource(data, entity);				
			} else {
				FileNode node = (FileNode)getCacheService().get("objectCache", uid);
				if(node != null) {
					Datasource source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
					printDirectory(data, source, node);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Datasource source = null;
		FileNode node = null;
		Object obj = getCacheService().get("objectCache", uid);
		if(obj != null) {
			if(obj instanceof Datasource) {
				source = (Datasource)obj;	
			} else if(obj instanceof FileNode) {
				node = (FileNode)obj;
				source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
			}
		} else {
			String localName = entity.getQName().getLocalName();
			if(localName.equals("datasource")) source = (Datasource)getCacheService().get("objectCache", uid);
			else {
				node = (FileNode)getCacheService().get("objectCache", uid);
				source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
			}
		}
		//printEntity(data, source, node, entity);
		data.getResponse().addData(entity);
		
		return data;
	}
	*/
	@ResponseBody
	@RequestMapping(value="/content/data")
	public RestResponse<Object> contentData(HttpServletRequest req, HttpServletResponse res, @RequestParam("id") Long id) throws Exception {
		RestResponse<Object> data = new RestResponse<Object>();
		
		
		
		return data;
	}
	protected void printDatasource(RestResponse<Object> data, Entity entity) {
		SortedMap<String,Object> entityMap = new TreeMap<String,Object>();
		String sourceName = entity.getPropertyValue(SystemModel.NAME);
		String sourceType = entity.getPropertyValue(ContentModel.DATASOURCE_PROTOCOL);
		String sourceUrl = entity.getPropertyValue(ContentModel.DATASOURCE_URL);
		String domain = entity.getPropertyValue(ContentModel.DATASOURCE_DOMAIN);
		String username = entity.getPropertyValue(ContentModel.DATASOURCE_USERNAME);
		//String password = entity.getPropertyValue(ContentModel.DATASOURCE_PASSWORD);
		String description = entity.getPropertyValue(SystemModel.DESCRIPTION);
		
		Datasource datasource = new Datasource(entity.getId(), entity.getUid(), sourceType, sourceName, sourceUrl);
		getCacheService().put("objectCache", datasource.getUid(), datasource);				
		
		entityMap.put("id", entity.getId());
		entityMap.put("uid", entity.getUid());
		entityMap.put("qname", entity.getQName().toString());
		entityMap.put("localName", entity.getQName().getLocalName());
		entityMap.put("name", entity.getName());
		if(entity.getUser() > 0) entityMap.put("user", entity.getUser());
		if(entity.getCreated() > 0) entityMap.put("created", entity.getCreated());
		if(entity.getCreator() > 0) entityMap.put("creator", entity.getCreator());
		if(entity.getModified() > 0) entityMap.put("modified", entity.getModified());
		if(entity.getModifier() > 0) entityMap.put("modifier", entity.getModifier());
		entityMap.put("deleted", entity.getDeleted());
		
		entityMap.put("url", sourceUrl);
		entityMap.put("description", description);
		entityMap.put("protocol", sourceType);
		entityMap.put("domain", domain);
		entityMap.put("username", username);
		entityMap.put("children", "true");
		
		data.getResponse().addData(entityMap);
	}
	protected void printNode(RestResponse<Object> data, Datasource datasource, FileNode directory) throws InvalidEntityException {
		String name = directory.getName();
		if(!name.startsWith(".") && !name.startsWith("$") && !exclusions.contains(name)) {
			Entity entity = getEntityService().getEntity(ContentModel.FOLDER, directory.getUid());
			if(entity != null) {
				data.getResponse().addData(entity);
			} else {
				data.getResponse().addData(directory);
			}
		}
	}
	protected void printDirectory(RestResponse<Object> data, Datasource datasource, FileNode directory) {
		DirectorySettings settings = DirectorySettingsUtility.getDirectorySettings(new File(directory.getPath()));
		directory.setUid(settings.getUid());
		if(settings.getFiles().size() > 0) {
			for(FileSettings fileSettings : settings.getFiles()) {								
				if(!fileSettings.isDeleted()) {
					Object node = getCacheService().get("objectCache", fileSettings.getUid());
					if(node == null) node = new FileNode(datasource.getUid(), fileSettings.getUid(), fileSettings.getPath());						
					if(node != null) {
						getCacheService().put("objectCache", fileSettings.getUid(), node);
						directory.getFiles().add((FileNode)node);
					}
				}
			}
		}
		data.getResponse().addData(directory);
	}
	protected void printRemoteDirectory(RestResponse<Object> data, Datasource datasource, String uid) throws IOException {
		DirectoryNodeXmlImportHandler handler = new DirectoryNodeXmlImportHandler();
		String url = datasource.getUrl() + "/endpoint/service/folders/get.xml?uid="+uid;
		if(!url.startsWith("http://")) url = "http://"+url;
		String responseData = get(url);
		if(data != null && responseData.length() > 0) {
			XMLUtility.SAXParse(false, responseData, handler);
			DirectoryNode directory = handler.getDirectoryNode();
			if(directory != null) {
				directory.setUid(uid);
				if(directory.getFiles().size() > 0) {
					for(FileNode file : directory.getFiles()) {
						FileNode node = (FileNode)getCacheService().get("objectCache", file.getUid());
						if(node == null) {
							node = new FileNode(datasource.getUid(), file.getUid(), file.getPath());
							node.setSize(file.getSize());
							getCacheService().put("objectCache", file.getUid(), node);
							data.getResponse().addData(node);
						}						
					}
				}
			}			
		}
	}
	/*
	protected void printEntity(RestResponse<Object> data, Datasource datasource, FileNode file, Entity entity) throws IOException {
		//get rid of the persisted files, replace with ones parsed from crawl file
		//List<Association> files = dir.getSourceAssociations(ContentModel.FILES);
		//dir.getSourceAssociations().removeAll(files);	

		buff.append("<node id='"+entity.getId()+"' uid='"+entity.getUid()+"' qname='"+entity.getQName().toString()+"' localName='"+entity.getQName().getLocalName()+"' created='"+dateFormatter.format(entity.getCreated())+"'>");
		if(file != null) buff.append("<name><![CDATA["+file.getName()+"]]></name>");
		else buff.append("<name><![CDATA["+datasource.getName()+"]]></name>");
		for(Property property : entity.getProperties()) {
			if(!property.getQName().equals(SystemModel.NAME))
				buff.append("<"+property.getQName().getLocalName()+"><![CDATA["+property.getValue()+"]]></"+property.getQName().getLocalName()+">");
		}		
		buff.append("<source_associations>");			
		List<Association> source_associations = entity.getSourceAssociations(ClassificationModel.NAMED_ENTITIES);
		if(source_associations.size() > 0) {							
			buff.append("<"+ClassificationModel.NAMED_ENTITIES.getLocalName()+" source='"+ContentModel.FOLDER.toString()+"' target='"+ClassificationModel.NAMED_ENTITY.toString()+"'>");
			for(Association assoc : source_associations) {	
				buff.append(toXml(assoc, false, false));			
			}
			buff.append("</"+ClassificationModel.NAMED_ENTITIES.getLocalName()+">");
		}
		source_associations = entity.getSourceAssociations(ClassificationModel.SUBJECTS);
		if(source_associations.size() > 0) {							
			buff.append("<"+ClassificationModel.SUBJECTS.getLocalName()+" source='"+ContentModel.FOLDER.toString()+"' target='"+ClassificationModel.SUBJECT.toString()+"'>");
			for(Association assoc : source_associations) {	
				buff.append(toXml(assoc, false, false));			
			}
			buff.append("</"+ClassificationModel.SUBJECTS.getLocalName()+">");
		}
		buff.append("</source_associations>");
		if(datasource != null) {
			if(datasource.getType().equals("loc")) {
				DirectorySettings settings = DirectorySettingsUtility.getDirectorySettings(new File(file.getPath()));
				if(settings.getFiles().size() > 0) {
					buff.append("<files>");
					for(FileSettings fileSettings : settings.getFiles()) {								
						if(!fileSettings.isDeleted()) {
							buff.write("<file uid='"+fileSettings.getUid()+"' size='"+DirectorySettingsUtility.getFileSize(fileSettings.getSize())+"'>");
							buff.write("<path><![CDATA["+fileSettings.getPath()+"]]></path>");
							buff.write("<name><![CDATA["+fileSettings.getName()+"]]></name>");
							buff.write("</file>");
						}
					}
					buff.append("</files>");
				}
			} else {
				DirectoryNodeXmlImportHandler handler = new DirectoryNodeXmlImportHandler();
				String url = datasource.getUrl() + "/endpoint/service/folders/get.xml?uid="+entity.getUid();
				if(!url.startsWith("http://")) url = "http://"+url;
				String data = get(url);
				if(data != null && data.length() > 0) {
					XMLUtility.SAXParse(false, data, handler);
					DirectoryNode directory = handler.getDirectoryNode();
					if(directory != null) {
						if(directory.getFiles().size() > 0) {
							buff.append("<files>");
							for(FileNode childFile : directory.getFiles()) {
								if(childFile.getDatasource() == null) childFile.setDatasource(datasource.getUid());
								getCacheService().put("objectCache", childFile.getUid(), childFile);
								buff.write("<file uid='"+childFile.getUid()+"' size='"+FileUtility.getFileSize(childFile.getSize())+"'>");
								buff.write("<path><![CDATA["+childFile.getPath()+"]]></path>");
								buff.write("<name><![CDATA["+childFile.getName()+"]]></name>");
								buff.write("</file>");
							}
							buff.append("</files>");
						}
					}
				}
			}
		}
		buff.append("</node>");
	}
	*/
	protected void detatchFoldersFix(Entity datasource) {
		for(Association assoc : datasource.getSourceAssociations(ContentModel.FOLDERS)) {
			//System.out.println("assoc "+assoc.getId());
			try {
				getEntityService().removeAssociation(assoc.getId());
				datasource.getSourceAssociations().remove(assoc);
			} catch(InvalidAssociationException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	public String toXml(Association association, boolean printSource, boolean isTree) {
		StringBuffer buff = new StringBuffer("");
		try {
			Entity target = association.getTargetEntity();
			if(target == null) target = printSource ? getEntityService().getEntity(association.getSource()) : getEntityService().getEntity(association.getTarget());
			if(isTree) buff.append("<node localName='"+target.getQName().getLocalName()+"' id='"+association.getTarget()+"' parentLocalName='"+association.getSourceName().getLocalName()+"' parent='"+association.getSource()+"'>");
			else buff.append("<node localName='"+target.getQName().getLocalName()+"' id='"+association.getId()+"' source_id='"+association.getSource()+"' target_id='"+association.getTarget()+"'>");	
			for(Property entityProperty : target.getProperties()) {
				if(!entityProperty.getValue().equals("")) {
					buff.append("<"+entityProperty.getQName().getLocalName()+"><![CDATA["+entityProperty.getValue()+"]]></"+entityProperty.getQName().getLocalName()+">");
				}
			}
			for(Property assocProperty : association.getProperties()) {
				buff.append("<"+assocProperty.getQName().getLocalName()+"><![CDATA["+assocProperty.getValue()+"]]></"+assocProperty.getQName().getLocalName()+">");
			}
			buff.append("</node>");	
		} catch(Exception e) {
			e.printStackTrace();
		}
		return buff.toString();
	}
	*/
	protected void pipe(String url, OutputStream out) throws IOException {
		log.info("contacting:"+url);
		URL psNav = new URL(url);
		HttpURLConnection conn = (HttpURLConnection)psNav.openConnection();
		// set the connection timeout to 5 seconds and the read timeout to 10 seconds
	    conn.setConnectTimeout(30000);
	    conn.setReadTimeout(60000);
	    try {
			IOUtility.pipe(conn.getInputStream(), out);
		} catch(Exception e) {
			throw new IOException(e);
		} finally{
			conn.disconnect();
		}
	}
	protected String get(String url) throws IOException {
		log.info("contacting:"+url);
		URL psNav = new URL(url);
		HttpURLConnection conn = (HttpURLConnection)psNav.openConnection();
		// set the connection timeout to 5 seconds and the read timeout to 10 seconds
	    conn.setConnectTimeout(0);
	    conn.setReadTimeout(0);
		StringWriter writer = new StringWriter();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;			
			while((line = in.readLine()) != null) {
				writer.append(line);
			}
		} catch(Exception e) {
			throw new IOException(e);
		} finally{
			conn.disconnect();
		}
		return writer.toString();
	}
	
}
