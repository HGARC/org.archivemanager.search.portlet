package org.archivemanager.portal.web.xml;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.heed.openapps.dictionary.ClassificationModel;
import org.heed.openapps.QName;
import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.SystemModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.AssociationImpl;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.ExportProcessor;
import org.heed.openapps.entity.InvalidEntityException;
import org.heed.openapps.entity.Property;
import org.heed.openapps.entity.ValidationResult;
import org.heed.openapps.entity.data.FileImportProcessor;
import org.heed.openapps.entity.data.FormatInstructions;
import org.heed.openapps.entity.ImportProcessor;
import org.heed.openapps.util.XMLUtility;
import org.archivemanager.portal.web.WebserviceSupport;
import org.heed.openapps.cache.TimedCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/service/archivemanager")
public class CollectionImportController extends WebserviceSupport {
	private TimedCache<String,Entity> entityCache = new TimedCache<String,Entity>(60);
	private TimedCache<String,FileImportProcessor> parserCache = new TimedCache<String,FileImportProcessor>(60);
	
	
	@RequestMapping(value="/collection/import/add.xml", method = RequestMethod.POST)
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String collectionId = req.getParameter("source");
		String sessionKey = req.getParameter("sessionKey");
		String format = req.getParameter("format");
		if(collectionId != null) {
			if(sessionKey != null && sessionKey.length() > 0) {
				Entity collection = getEntityService().getEntity(null, Long.valueOf(collectionId));
				Entity root = entityCache.get(sessionKey);
				getEntityService().addEntity(root);
				ImportProcessor parser = parserCache.get(sessionKey);
				Collection<Entity> entities = parser.getEntities().values();
				getEntityService().addEntities(entities);
				Association assoc = new AssociationImpl(RepositoryModel.CATEGORIES, collection.getId(), root.getId());
				getEntityService().addAssociation(assoc);
				root.addProperty(SystemModel.NAME, root.getPropertyValue(SystemModel.NAME) + "(import)");
				getEntityService().updateEntity(root);
				ExportProcessor processor = getEntityService().getExportProcessor(root.getQName().toString());
				if(format != null && format.equals("tree")) response("", (String)processor.export(new FormatInstructions(true), assoc), res.getWriter());
				else response("", (String)processor.export(new FormatInstructions(false, true, false), root), res.getWriter());
			} else {				
				String assocQname = req.getParameter("assoc_qname");
				String entityQname = req.getParameter("entity_qname");
				String source = req.getParameter("source");
				QName aQname = QName.createQualifiedName(assocQname);
				QName eQname = QName.createQualifiedName(entityQname);
				Entity entity = getEntityService().getEntity(req, eQname);
				ValidationResult entityResult = getEntityService().validate(entity);
				if(entityResult.isValid()) {
					ExportProcessor processor = getEntityService().getExportProcessor(entity.getQName().toString());
					if(processor == null) processor = getEntityService().getExportProcessor("default");
					if(entity.getId() != null) {
						getEntityService().updateEntity(entity);
						if(format != null && format.equals("tree")) response("", (String)processor.export(new FormatInstructions(true), entity), res.getWriter());
						else response("", (String)processor.export(new FormatInstructions(false, true, false), entity), res.getWriter());
					} else {
						Long assocId = getEntityService().addEntity(Long.valueOf(source), null, aQname, null, entity);
						Association assoc = getEntityService().getAssociation(assocId);	
						if(format != null && format.equals("tree")) response("", (String)processor.export(new FormatInstructions(true), assoc), res.getWriter());
						else response("", (String)processor.export(new FormatInstructions(false, true, false), entity), res.getWriter());
					} //else response("", "", response.getWriter());
				}
			}
		}
		return null;
	}
	@RequestMapping(value="/collection/import/fetch.xml", method = RequestMethod.GET)
	public ModelAndView fetch(HttpServletRequest req, HttpServletResponse res, @RequestParam("session") String sessionKey) throws Exception {
		String source = req.getParameter("source");
		StringBuffer buff = new StringBuffer();		
		ImportProcessor parser = parserCache.get(sessionKey);
		if(parser != null) {
			if(source == null || source.equals("nodes")) {
				Entity root = entityCache.get(sessionKey);
				if(root != null) {
					List<Association> associations = root.getSourceAssociations();
					if(associations.size() > 0) buff.append("<record id='"+root.getUid()+"' title='"+root.getProperty(SystemModel.NAME)+"' parent='null'/>");
					else buff.append("<record id='0' title='Collection' parent='null'><children/></record>");
					printNodeTaxonomy(buff, root, parser); 
				}
				//System.out.println(buff.toString());
				return response(buff.toString(), res.getWriter());
			} else if(source.equals("node")) {
				String id = req.getParameter("id");
				if(id != null) {				
					Entity node = parser.getEntityById(id);
					if(node != null) {
						buff.append("<node id='"+id+"' localName='"+node.getQName().getLocalName()+"'>");
						buff.append("<properties>");
						for(Property prop : node.getProperties()) {
							buff.append("<property name='"+prop.getQName().getLocalName()+"'><value><![CDATA["+prop.getValue()+"]]></value></property>");
						}
						buff.append("</properties>");
						/*
						buff.append("<containers>");
						for(Association prop : node.getTargetAssociations(SystemModel.CATEGORIES,SystemModel.ITEMS)) {
							buff.append("<node name='"+prop.getSourceNode().getProperty(RepositoryModel.CONTAINER_TYPE)+"'>");
							buff.append("<value><![CDATA["+toXmlCdata(prop.getSourceNode().getProperty(RepositoryModel.CONTAINER_VALUE))+"]]></value>");
							buff.append("</node>");
						}
						buff.append("</containers>");
						 */
						buff.append("<notes>");
						for(Association propAssoc : node.getSourceAssociations(SystemModel.NOTES)) {
							Entity prop = parser.getEntityById(String.valueOf(propAssoc.getTarget()));
							buff.append("<node name='"+prop.getProperty(SystemModel.NOTE_TYPE)+"'>");
							buff.append("<value><![CDATA["+prop.getProperty(SystemModel.NOTE_CONTENT)+"]]></value>");
							buff.append("</node>");
						}
						buff.append("</notes>");
						buff.append("<names>");
						for(Association propAssoc : node.getSourceAssociations(ClassificationModel.NAMED_ENTITIES)) {
							Entity prop = parser.getEntityById(String.valueOf(propAssoc.getTarget()));
							buff.append("<node name='"+prop.getQName().getLocalName()+"' value='"+prop.getProperty(SystemModel.NAME)+"' />");
						}
						buff.append("</names>");
						buff.append("<subjects>");
						for(Association propAssoc : node.getSourceAssociations(ClassificationModel.SUBJECTS)) {
							Entity prop = parser.getEntityById(String.valueOf(propAssoc.getTarget()));
							buff.append("<node name='"+prop.getQName().getLocalName()+"' value='"+prop.getProperty(SystemModel.NAME)+"' />");
						}
						buff.append("</subjects>");
						buff.append("</node>");
						return response(buff.toString(), res.getWriter());
					}
				}
			}
		}
		return error("problem fetching resource", res.getWriter());
	}
	@RequestMapping(value="/collection/import/upload.xml", method = RequestMethod.POST)
	public ModelAndView upload(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String mode = null;
		String sessionKey = "";
		try {
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload();
			// Parse the request
			FileItemIterator iter = upload.getItemIterator(req);
			while(iter.hasNext()) {
			    FileItemStream item = iter.next();
			    String name = item.getFieldName();
			    InputStream stream = item.openStream();
			    if(item.isFormField()) {
			    	if(name.equals("mode")) mode = Streams.asString(stream);
			    	//System.out.println("Form field " + name + " with value " + Streams.asString(tmpStream) + " detected.");
			    } else {
			    	FileImportProcessor parser = (FileImportProcessor)getEntityService().getImportProcessors(mode).get(0);
					if(parser != null) {
						parser.process(stream, null);
						sessionKey = java.util.UUID.randomUUID().toString();
						entityCache.put(sessionKey, parser.getRoot());
						parserCache.put(sessionKey, parser);
					}
			    }
			}
		} catch(FileUploadException e) {
			e.printStackTrace();
		}		
		res.setContentType("text/html");
		res.getWriter().print("<script language='javascript' type='text/javascript'>window.top.window.uploadComplete('"+sessionKey+"');</script>");
		return null;
	}
	
	protected void printNodeTaxonomy(StringBuffer buff, Entity node, ImportProcessor parser) throws InvalidEntityException {
		for(Association assoc : node.getChildren()) {
			Entity child = parser.getEntityById(assoc.getTargetUid());
			Property titleProp = child.getProperty(SystemModel.NAME);
			String title = titleProp != null ? titleProp.toString() : child.getQName().getLocalName();
			if(child.getChildren().size() > 0) {
				buff.append("<record id='"+child.getUid()+"' title='"+toXmlData(title)+"' parent='"+node.getUid()+"'/>");
				printNodeTaxonomy(buff,child, parser);
			} else buff.append("<record id='"+child.getUid()+"' title='"+toXmlData(title)+"' parent='"+node.getUid()+"' isFolder='false'/>");
		}
	}
	protected ModelAndView response(String data, PrintWriter out) {
		out.write("<response><status>0</status><data>"+data+"</data></response>");
		return null;
	}
	protected ModelAndView response(String message, String data, PrintWriter out) {
		out.write("<response><status>0</status><message>"+message+"</message><data>"+data+"</data></response>");
		return null;
	}
	protected ModelAndView error(String msg, PrintWriter out) {
		out.write("<response><status>-1</status><message>"+msg+"</message></response>");
		return null;
	}
	public static String toXmlData(Object o) {
		if(o == null) return "";
		else return XMLUtility.escape(o.toString());
	}
}
