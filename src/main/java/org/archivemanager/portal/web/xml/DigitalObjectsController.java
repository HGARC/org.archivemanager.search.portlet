package org.archivemanager.portal.web.xml;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.content.ContentModel;
import org.heed.openapps.QName;
import org.heed.openapps.SystemModel;
import org.heed.openapps.content.Datasource;
import org.heed.openapps.content.DirectoryNode;
import org.heed.openapps.content.DirectorySettings;
import org.heed.openapps.content.DirectorySettingsUtility;
import org.heed.openapps.content.FileNode;
import org.heed.openapps.content.FileSettings;
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
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.ValidationResult;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.util.FileUtility;
import org.heed.openapps.util.IOUtility;
import org.heed.openapps.util.NumberUtility;
import org.heed.openapps.util.XMLUtility;
import org.archivemanager.portal.web.WebserviceSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping("/service/archivemanager")
public class DigitalObjectsController extends WebserviceSupport {
	private final static Logger log = Logger.getLogger(DigitalObjectsController.class.getName());
	//private Map<Long,Datasource> datasources = new HashMap<Long,Datasource>();
	//private List<String> uids = new ArrayList<String>();
	//private List<FileNode> files = new ArrayList<FileNode>();
	//private List<Long> sources = new ArrayList<Long>();
	private static final List<String> exclusions = new ArrayList<String>();
	private Format dateFormatter = new SimpleDateFormat("MMMM d yyyy HH:mm a");
	
	static {
		exclusions.add("System Volume Information");
		exclusions.add("AI_RecycleBin");
	}
	
	public void foo() {
		
	}
	@RequestMapping(value="/datasource/folders/browse", method = RequestMethod.GET)
	public ModelAndView browseFolders(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		return response(0, "", browse(req, res, false), res.getWriter());
	}
	@RequestMapping(value="/datasource/files/browse", method = RequestMethod.GET)
	public ModelAndView browseFiles(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return response(0, "", browse(req, res, true), res.getWriter());
	}
	protected String browse(HttpServletRequest req, HttpServletResponse res, boolean printFiles) throws Exception {
		FileNodeXmlImportHandler handler = new FileNodeXmlImportHandler();
		StringWriter buff = new StringWriter();
		String parent = req.getParameter("parent");
		//String query = req.getParameter("query");
		if(parent == null || parent.equals("null") || parent.equals("")) {			
			EntityQuery eQuery = new EntityQuery(ContentModel.DATASOURCE);
			eQuery.getFields().add("name");
			EntityResultSet<Entity> results = getEntityService().search(eQuery);
			for(Entity dsEntity : results.getResults()) {
				//detatchFoldersFix(dsEntity);
				printDatasource(buff, dsEntity);
			}
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
								printNode(buff, source, n);
								getCacheService().put("objectCache", uuid, n);
							}
						}
					}
						
				} else {
						
					String url = parent.equals(source.getUid()) ?
							source.getUrl() + "/datasource/folders/browse.xml" :
								source.getUrl() + "/datasource/folders/browse.xml?uid="+parent;
					if(!url.startsWith("http://")) url = "http://"+url;
					String data = get(url);
					if(data != null && data.length() > 0) {
						XMLUtility.SAXParse(false, data, handler);
						List<FileNode> nodes = handler.getNodes();
						for(FileNode n : nodes) {
							n.setDatasource(source.getUid());
							printNode(buff, source, n);
							getCacheService().put("objectCache", n.getUid(), n);
						}
					}
				}
			}
		}
		return buff.toString();
	}
	@RequestMapping(value="/content/get", method = RequestMethod.GET)
	public ModelAndView get(HttpServletRequest req, HttpServletResponse response, @RequestParam("uid") String uid) throws Exception {
		StringWriter buff = new StringWriter();				
		Object obj = getCacheService().get("objectCache", uid);
		if(obj != null) {
			Datasource source = null;
			FileNode node = null;
			Entity entity = null;
			if(obj instanceof Datasource) {
				source = (Datasource)obj;	
			} else if(obj instanceof FileNode) {
				node = (FileNode)obj;
				source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
				entity = getEntityService().getEntity(ContentModel.FOLDER, uid);
				if(entity != null) printEntity(buff, source, node, entity);
			}
			if(entity == null && node != null) {
				if(source.getType().equals("loc")) {
					printDirectory(buff, source, node);
				} else {
					printRemoteDirectory(buff, source, uid);
				}
			}
		}
		
		response.setHeader( "Pragma", "no-cache" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setDateHeader( "Expires", 0 );
		return response(0, "", buff.toString(), response.getWriter());
	}
	@RequestMapping(value="/content/stream/{uid}.{extension}")
	public ModelAndView stream(HttpServletRequest req, HttpServletResponse res, @PathVariable("uid") String uid, @PathVariable("extension") String extension) throws Exception {
		return stream(req, res, uid);
	}
	@RequestMapping(value="/content/stream/{uid}")
	public ModelAndView stream(HttpServletRequest req, HttpServletResponse res, @PathVariable("uid") String uid) throws Exception {
		Entity obj = getEntityService().getEntity(ContentModel.DIGITAL_OBJECT, uid);
		//Object obj = getCacheService().get("objectCache", uid);
		if(obj != null) {
			//FileNode node = (FileNode)obj;
			//Datasource source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
			Entity source = getEntityService().getEntity(ContentModel.DATASOURCE, obj.getPropertyValue(ContentModel.DATASOURCE));
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
	@RequestMapping(value="/content/file/associate.xml", method = RequestMethod.POST)
	public ModelAndView addFile(HttpServletRequest request, HttpServletResponse response, @RequestParam("source") String source, @RequestParam("target") String target) throws Exception {
		StringWriter buff = new StringWriter();
		Entity sourceEntity = NumberUtility.isLong(source) ? getEntityService().getEntity(ContentModel.CONTENT, Long.valueOf(source)) : getEntityService().getEntity(ContentModel.CONTENT, source);
		Entity targetEntity = null;
		try {
			targetEntity = getEntityService().getEntity(ContentModel.FILE, target);
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
			return response(0, "", (String)processor.export(new FormatInstructions(false, true, false), assoc), response.getWriter());
		}
		return response(-1, "problem adding association between source:"+source+" target:"+target, buff.toString(), response.getWriter());
	}
	@RequestMapping(value="/content/update", method = RequestMethod.POST)
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response, @RequestParam("uid") String uid, @RequestParam("qname") String qnameStr) throws Exception {
		//FileNode node = getFileNode(uid);
		StringWriter buff = new StringWriter();
		//String idStr = request.getParameter("id");
		Entity entity = null;
		try {				
			QName qname = QName.createQualifiedName(qnameStr);
			entity = getEntityService().getEntity(qname, uid);
			Entity newEntity = getEntityService().getEntity(request, qname);
			ValidationResult result = getEntityService().validate(newEntity);
			if(result.isValid()) {
				if(entity == null) {
					entity = new EntityImpl(qname);
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
				printDatasource(buff, entity);
				
				return response(0, "", buff.toString(), response.getWriter());
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
			if(node != null) {
			
			}
		} else {
			String localName = entity.getQName().getLocalName();
			if(localName.equals("datasource")) source = (Datasource)getCacheService().get("objectCache", uid);
			else {
				node = (FileNode)getCacheService().get("objectCache", uid);
				source = (Datasource)getCacheService().get("objectCache", node.getDatasource());
			}
		}
		printEntity(buff, source, node, entity);
				
		return response(0, "", buff.toString(), response.getWriter());
	}
	@RequestMapping(value="/content/data")
	public ModelAndView contentData(HttpServletRequest req, HttpServletResponse res, @RequestParam("id") Long id) throws Exception {
		StringWriter buff = new StringWriter();
		
		
		
		return response(0, "", buff.toString(), res.getWriter());
	}
	protected void printDatasource(StringWriter buff, Entity dsEntity) {
		String sourceName = dsEntity.getPropertyValue(SystemModel.NAME);
		String sourceType = dsEntity.getPropertyValue(ContentModel.DATASOURCE_PROTOCOL);
		String sourceUrl = dsEntity.getPropertyValue(ContentModel.DATASOURCE_URL);
		String domain = dsEntity.getPropertyValue(ContentModel.DATASOURCE_DOMAIN);
		String username = dsEntity.getPropertyValue(ContentModel.DATASOURCE_USERNAME);
		String password = dsEntity.getPropertyValue(ContentModel.DATASOURCE_PASSWORD);
		String description = dsEntity.getPropertyValue(SystemModel.DESCRIPTION);
		Datasource datasource = new Datasource(dsEntity.getId(), dsEntity.getUid(), sourceType, sourceName, sourceUrl);
		getCacheService().put("objectCache", datasource.getUid(), datasource);				
		//File file = new File(sourceUrl);
		buff.append("<node id='"+datasource.getId()+"' uid='"+datasource.getUid()+"' localName='datasource'>");
		buff.write("<url><![CDATA["+sourceUrl+"]]></url>");
		buff.append("<name><![CDATA["+sourceName+"]]></name>");
		buff.append("<protocol>"+sourceType+"</protocol>");
		buff.append("<domain>"+domain+"</domain>");
		buff.append("<username>"+username+"</username>");
		buff.append("<password>"+password+"</password>");
		buff.append("<description>"+description+"</description>");
		buff.append("<children>true</children>");
		buff.append("</node>");
	}
	protected void printNode(StringWriter buff, Datasource datasource, FileNode directory) throws InvalidEntityException {
		String name = directory.getName();
		if(!name.startsWith(".") && !name.startsWith("$") && !exclusions.contains(name)) {
			Entity entity = getEntityService().getEntity(ContentModel.FOLDER, directory.getUid());
			if(entity != null) {
				buff.append("<node id='"+entity.getId()+"' uid='"+directory.getUid()+"' localName='folder'>");
			} else {
				buff.append("<node uid='"+directory.getUid()+"' localName='folder'>");
			}
			buff.append("<path><![CDATA["+directory.getPath()+"]]></path>");
			buff.append("<name><![CDATA["+directory.getName()+"]]></name>");
			buff.append("<children>"+directory.hasChildren()+"</children>");
			buff.append("</node>");
		}
	}
	protected void printDirectory(StringWriter buff, Datasource datasource, FileNode directory) {
		DirectorySettings settings = DirectorySettingsUtility.getDirectorySettings(new File(directory.getPath()));
		buff.append("<node uid='"+settings.getUid()+"' qname='{openapps.org_content_1.0}folder' localName='folder'>");
		buff.append("<name><![CDATA["+directory.getName()+"]]></name>");
		buff.write("<path><![CDATA["+directory.getPath()+"]]></path>");
		if(settings.getFiles().size() > 0) {
			buff.append("<files>");
			for(FileSettings fileSettings : settings.getFiles()) {								
				if(!fileSettings.isDeleted()) {
					Object node = getCacheService().get("objectCache", fileSettings.getUid());
					if(node == null) getCacheService().put("objectCache", fileSettings.getUid(), new FileNode(datasource.getUid(), fileSettings.getUid(), fileSettings.getPath()));
					buff.write("<file uid='"+fileSettings.getUid()+"' size='"+DirectorySettingsUtility.getFileSize(fileSettings.getSize())+"'>");
					buff.write("<path><![CDATA["+fileSettings.getPath()+"]]></path>");
					buff.write("<name><![CDATA["+fileSettings.getName()+"]]></name>");
					buff.write("</file>");
				}
			}
			buff.append("</files>");
		}
		buff.append("</node>");
	}
	protected void printRemoteDirectory(StringWriter buff, Datasource datasource, String uid) throws IOException {
		DirectoryNodeXmlImportHandler handler = new DirectoryNodeXmlImportHandler();
		String url = datasource.getUrl() + "/endpoint/service/folders/get.xml?uid="+uid;
		if(!url.startsWith("http://")) url = "http://"+url;
		String data = get(url);
		if(data != null && data.length() > 0) {
			XMLUtility.SAXParse(false, data, handler);
			DirectoryNode directory = handler.getDirectoryNode();
			if(directory != null) {
				buff.append("<node uid='"+uid+"' qname='"+ContentModel.FOLDER.toString()+"' localName='"+ContentModel.FOLDER.getLocalName()+"'>");
				buff.append("<name><![CDATA["+directory.getName()+"]]></name>");
				if(directory.getFiles().size() > 0) {
					buff.append("<files>");
					for(FileNode file : directory.getFiles()) {
						Object node = getCacheService().get("objectCache", file.getUid());
						if(node == null) {
							FileNode fileNode = new FileNode(datasource.getUid(), file.getUid(), file.getPath());
							fileNode.setSize(file.getSize());
							getCacheService().put("objectCache", file.getUid(), fileNode);						
						}
						buff.write("<file uid='"+file.getUid()+"' size='"+FileUtility.getFileSize(file.getSize())+"'>");
						buff.write("<path><![CDATA["+file.getPath()+"]]></path>");
						buff.write("<name><![CDATA["+file.getName()+"]]></name>");
						buff.write("</file>");
					}
					buff.append("</files>");
				}
				buff.append("</node>");
			}			
		}
	}
	protected void printEntity(StringWriter buff, Datasource datasource, FileNode file, Entity entity) throws IOException {
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
	protected ModelAndView response(int status, String message, String data, PrintWriter out) {
		out.write("<response><status>"+status+"</status><message><![CDATA["+message+"]]></message><data>"+data+"</data></response>");
		return null;
	}
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

	public String toXml(Association association, boolean printSource, boolean isTree) {
		StringBuffer buff = new StringBuffer("");
		try {
			Entity target = association.getTargetEntity();
			if(target == null) target = printSource ? getEntityService().getEntity(association.getSourceName(), association.getSource()) : getEntityService().getEntity(association.getTargetName(), association.getTarget());
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
	public static String getHostUrl(HttpServletRequest req) {
		return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
	}
}
