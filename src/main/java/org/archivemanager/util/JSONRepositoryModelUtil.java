package org.archivemanager.util;

import org.archivemanager.model.Collection;
import org.archivemanager.model.DigitalObject;
import org.archivemanager.model.Entry;
import org.archivemanager.model.NamedEntity;
import org.archivemanager.model.Note;
import org.archivemanager.model.PathNode;
import org.archivemanager.model.Subject;
import org.archivemanager.model.WebLink;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONRepositoryModelUtil extends RepositoryModelUtilSupport {

	/*
	public static Collection getCollection(JSONObject entity) throws JSONException {
		String id = String.valueOf(entity.getLong("id"));
		String name = entity.has("name") ? entity.getString("name") : "";
		String description = entity.has("description") ? entity.getString("description") : "";
		String contentType = entity.has("localName") ? cleanContentType(entity.getString("localName") : "";
		Collection collection = new Collection(id, name, description, contentType);
		/*
		if(entity.has("url")) collection.setUrl(entity.getString("url"));
		if(entity.has("bio_note")) this.bioNote = entity.getString("bio_note");
		if(entity.has("scope_note")) this.scopeNote = entity.getString("scope_note");
		if(entity.has("date_expression")) {
			if(getTitle() == null) setTitle(entity.getString("date_expression"));
			this.dateExpression = entity.getString("date_expression");
		}
		if(entity.has("begin")) this.begin = entity.getString("begin");
		if(entity.has("end")) this.end = entity.getString("end");
		
		if(entity.has("container") ) this.container = entity.getString("container");
		
		if(entity.has("series")) {
			JSONArray seriesList = entity.getJSONArray("series");
			for(int i=0; i < seriesList.length(); i++) {
				Series seriesObj = new Series(seriesList.getJSONObject(i));
				series.add(seriesObj);
			}
		}
		
		if(entity.has("source_associations")) {
			JSONArray source_associations = entity.getJSONArray("source_associations");		
			for(int i=0; i < source_associations.length(); i++) {
				JSONObject association = source_associations.getJSONObject(i);
				if(association.has("notes")) {
					JSONArray sources = association.getJSONArray("notes");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						if(source.has("type")) {
							String type = source.getString("type");
							if(type.equals("General note") || type.equals("General Physical Description note") ||
									type.equals("Table of Contents")) {
								notes.add(new Note("", type, source.getString("content")));
							} 
						}
					}
				}
				if(association.has("named_entities")) {
					JSONArray sources = association.getJSONArray("named_entities");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						String localName = source.getString("localName");
						NamedEntity name = new NamedEntity(source);
						if(source.has("target_id")) name.setId(String.valueOf(source.getLong("target_id")));						
						if(localName.equals("person")) people.add(name);
						if(localName.equals("corporation")) corporations.add(name);
					}
				}
				if(association.has("subjects")) {
					JSONArray sources = association.getJSONArray("subjects");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						Subject subject = new Subject(source);
						if(source.has("target_id")) subject.setId(String.valueOf(source.getLong("target_id")));
						subjects.add(subject);
					}
				}
				if(association.has("files")) {
					JSONArray sources = association.getJSONArray("files");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						DigitalObject file = new DigitalObject();
						if(source.has("target_id")) file.setId(String.valueOf(source.getLong("target_id")));
						if(source.has("uuid")) file.setUuid(source.getString("uuid"));
						if(source.has("title")) file.setTitle(source.getString("title"));
						if(source.has("mimetype")) file.setMimetype(source.getString("mimetype"));
						if(source.has("type")) file.setType(source.getString("type"));
						if(source.has("group")) file.setGroup(source.getString("group"));
						if(source.has("order")) file.setOrder(source.getInt("order"));
						digitalObjects.add(file);
					}
				}
				if(association.has("web_links")) {
					JSONArray sources = association.getJSONArray("web_links");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						WebLink file = new WebLink();
						if(source.has("id")) file.setId(String.valueOf(source.getLong("target_id")));
						if(source.has("url")) {
							file.setUrl(source.getString("url"));
							int endIdx = file.getUrl().indexOf("/", 7);
							if(endIdx > -1) file.setDomain(file.getUrl().substring(0, endIdx));
						}
						if(source.has("title")) file.setTitle(source.getString("title"));
						if(source.has("type")) file.setType(source.getString("type"));
						weblinks.add(file);
						
					}
				}
			}
		}
	}
	public static Entry getEntry(JSONObject entity) {
		if(entity.has("collection_name")) this.collection = entity.getString("collection_name");
		if(entity.has("items")) {
			String description = entity.getString("items");
			description = description.replace("\n", "<br>");
			setDescription(description);
		}
		
		if(entity.has("target_associations")) {
			JSONArray target_associations = entity.getJSONArray("target_associations");
			for(int i=0; i < target_associations.length(); i++) {
				JSONObject association = target_associations.getJSONObject(i);
				if(association.has("entries")) {
					JSONArray sources = association.getJSONArray("entries");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						namedEntity = String.valueOf(source.getLong("source_id"));
						setTitle(source.getString("name"));
					}
				}
			}
		}
	}
	public Item(JSONObject entity) throws JSONException {
		super(entity);
		if(entity.has("container")) this.container = entity.getString("container");
		if(entity.has("summary") && entity.getString("summary").length() > 0) this.summary = entity.getString("summary");
		if(entity.has("collection_id")) this.collectionId = String.valueOf(entity.getLong("collection_id"));
		if(entity.has("collection_name")) this.collectionName = entity.getString("collection_name");
		if(entity.has("collection_url")) this.collectionUrl = entity.getString("collection_url");
		if(entity.has("date_expression")) {
			if(getTitle() == null) setTitle(entity.getString("date_expression"));
			this.dateExpression = entity.getString("date_expression");
		}
		if(entity.has("path")) {
			JSONArray nodesList = entity.getJSONArray("path");
			for(int i=0; i < nodesList.length(); i++) {
				JSONObject source = nodesList.getJSONObject(i);
				PathNode pathNode = new PathNode(source);
				path.add(pathNode);
			}
		}
		if(entity.has("source_associations")) {
			JSONArray source_associations = entity.getJSONArray("source_associations");		
			for(int i=0; i < source_associations.length(); i++) {
				JSONObject association = source_associations.getJSONObject(i);
				if(association.has("notes")) {
					JSONArray sources = association.getJSONArray("notes");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						if(source.has("type")) {
							String type = source.getString("type");
							if(type.equals("General note") || type.equals("General Physical Description note") ||
									type.equals("Table of Contents")) {
								notes.add(new Note("", type, source.getString("content")));
							} 
						}
					}
				}
				if(association.has("named_entities")) {
					JSONArray sources = association.getJSONArray("named_entities");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						String localName = source.getString("localName");
						NamedEntity name = new NamedEntity(source);
						if(source.has("target_id")) name.setId(String.valueOf(source.getLong("target_id")));						
						if(localName.equals("person")) people.add(name);
						if(localName.equals("corporation")) corporations.add(name);
					}
				}
				if(association.has("subjects")) {
					JSONArray sources = association.getJSONArray("subjects");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						Subject subject = new Subject(source);
						if(source.has("target_id")) subject.setId(String.valueOf(source.getLong("target_id")));
						subjects.add(subject);
					}
				}
				if(association.has("files")) {
					JSONArray sources = association.getJSONArray("files");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						DigitalObject file = new DigitalObject();
						if(source.has("target_id")) file.setId(String.valueOf(source.getLong("target_id")));
						if(source.has("uuid")) file.setUuid(source.getString("uuid"));
						if(source.has("title")) file.setTitle(source.getString("title"));
						if(source.has("mimetype")) file.setMimetype(source.getString("mimetype"));
						if(source.has("type")) file.setType(source.getString("type"));
						if(source.has("group")) file.setGroup(source.getString("group"));
						if(source.has("order")) file.setOrder(source.getInt("order"));
						digitalObjects.add(file);
					}
				}
				if(association.has("web_links")) {
					JSONArray sources = association.getJSONArray("web_links");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						WebLink file = new WebLink();
						if(source.has("id")) file.setId(String.valueOf(source.getLong("target_id")));
						if(source.has("url")) {
							file.setUrl(source.getString("url"));
							int endIdx = file.getUrl().indexOf("/", 7);
							if(endIdx > -1) file.setDomain(file.getUrl().substring(0, endIdx));
						}
						if(source.has("title")) file.setTitle(source.getString("title"));
						if(source.has("type")) file.setType(source.getString("type"));
						weblinks.add(file);
						
					}
				}
			}
		}
	}
	public NamedEntity(JSONObject entity) throws JSONException {
		super(entity);
		if(entity.has("function")) setFunction(entity.getString("function"));
		if(entity.has("rule")) setRole(entity.getString("rule"));
		if(entity.has("note")) setNote(entity.getString("note"));
		if(entity.has("dates")) setDates(entity.getString("dates"));
		if(entity.has("source")) setSource(entity.getString("source"));
		
		if(entity.has("source_associations")) {
			JSONArray source_associations = entity.getJSONArray("source_associations");
			for(int i=0; i < source_associations.length(); i++) {
				JSONObject association = source_associations.getJSONObject(i);
				if(association.has("entries")) {
					JSONArray sources = association.getJSONArray("entries");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						if(source.get("localName").equals("entry")) {
							Entry entry = new Entry(source);
							entries.add(entry);
						}
					}
				}
			}
		}
		
		if(entity.has("target_associations")) {
			JSONArray target_associations = entity.getJSONArray("target_associations");
			for(int i=0; i < target_associations.length(); i++) {
				JSONObject association = target_associations.getJSONObject(i);
				if(association.has("named_entities")) {
					JSONArray sources = association.getJSONArray("named_entities");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						if(source.get("localName").equals("collection")) {
							Collection collection = new Collection(source);
							collection.setId(String.valueOf(source.getLong("source_id")));
							collections.add(collection);
						}
					}
				}
			}
		}
	}
	public Audio(JSONObject entity) throws JSONException {
		super(entity);		
		for(WebLink link : getWeblinks()) {
			if(link.getType().equals("avatar"))
				avatar = link.getUrl();
			else if(link.getType().equals("rendition"))
				rendition = link.getUrl();
		}
	}
	public Subject(JSONObject entity) throws JSONException {
		super(entity);
		if(entity.has("type")) this.type = entity.getString("type");
		if(entity.has("source")) this.type = entity.getString("source");
		
		if(entity.has("target_associations")) {
			JSONArray target_associations = entity.getJSONArray("target_associations");		
			for(int i=0; i < target_associations.length(); i++) {
				JSONObject association = target_associations.getJSONObject(i);
				if(association.has("subjects")) {
					JSONArray sources = association.getJSONArray("subjects");
					for(int j=0; j < sources.length(); j++) {
						JSONObject source = sources.getJSONObject(j);
						if(source.get("localName").equals("collection")) {
							Collection collection = new Collection(source);
							collection.setId(String.valueOf(source.getLong("source_id")));
							collections.add(collection);
						}
					}
				}
			}
		}
	}
	public Video(JSONObject object) throws JSONException {
		super(object);		
		for(WebLink link : getWeblinks()) {
			if(link.getType().equals("avatar"))
				avatar = link.getUrl();
			else if(link.getType().equals("rendition"))
				rendition = link.getUrl();
		}
	}
	*/
}
