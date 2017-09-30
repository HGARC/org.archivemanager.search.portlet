package org.archivemanager.portal.portlet.search.data;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import org.heed.openapps.SystemModel;
import org.heed.openapps.data.Sort;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.Property;


public class EntitySorter implements Comparator<Entity> {
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private Sort sort;
	
	
	public EntitySorter(Sort sort) {
		this.sort = sort;
	}
	
	@SuppressWarnings("deprecation")
	public int compare(Entity e1, Entity e2) {
		if(e1 == null || e1 == null) return 0;
		Property property1 = null;
		Property property2 = null;
		if(sort.getField().equals(SystemModel.NAME.toString())) {
			property1 = sort.isReverse() ? new Property(SystemModel.NAME, e2.getName()) : new Property(SystemModel.NAME, e1.getName());
			property2 = sort.isReverse() ? new Property(SystemModel.NAME, e1.getName()) : new Property(SystemModel.NAME, e2.getName());
		} else {
			property1 = sort.isReverse() ? e2.getProperty(sort.getField()) : e1.getProperty(sort.getField());
			property2 = sort.isReverse() ? e1.getProperty(sort.getField()) : e2.getProperty(sort.getField());
		}
		if(property1 != null && property2 != null) {
			if(sort.getType() == 0) sort.setType(property1.getType());
			String field1 = removeTags(String.valueOf(property1));
			String field2 = removeTags(String.valueOf(property2));
			if(sort.getType() == Property.DATE) {
				try {
					return dateFormatter.parse(field1).compareTo(dateFormatter.parse(field2));
				} catch(Exception e) {}
			} else if(sort.getType() == Sort.INTEGER || sort.getType() == Sort.DOUBLE || sort.getType() == Sort.LONG) {
				return Integer.valueOf(field1).compareTo(Integer.valueOf(field2));
			} else {
				for(int i=0; i < field1.length(); i++) {
					if(field2.length() > i && field1.charAt(i) != field2.charAt(i)) {
						if(Character.isLetter(field1.charAt(i)) && Character.isLetter(field2.charAt(i)))
							return Character.valueOf(field1.charAt(i)).compareTo(Character.valueOf(field2.charAt(i)));
						else if(Character.isSpace(field1.charAt(i)) && !Character.isSpace(field2.charAt(i)))
							return -1;
						else if(Character.isSpace(field2.charAt(i)) && !Character.isSpace(field1.charAt(i)))
							return 1;
						else if(Character.isDigit(field1.charAt(i)) && Character.isDigit(field2.charAt(i))) {
							StringBuffer digit1 = new StringBuffer();
							StringBuffer digit2 = new StringBuffer();
							int index = i;
							while(field1.length() > index && Character.isDigit(field1.charAt(index))) {
								digit1.append(field1.charAt(index));
								index++;
							}
							index = i;
							while(field2.length() > index && Character.isDigit(field2.charAt(index))) {
								digit2.append(field2.charAt(index));
								index++;
							}
							if(Integer.valueOf(digit1.toString()) > Integer.valueOf(digit2.toString()))
								return 1;
							else return -1;
						}
					}
				}
				return field1.compareTo(field2);
			}
		}
		return 0;
	}
	protected String removeTags(String in) {
		if(in == null) return "";
    	return in.replaceAll("\\<.*?>","");
	}
}