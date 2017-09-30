package org.archivemanager.util;
import java.util.Comparator;

import org.archivemanager.model.Category;


public class SeriesSorter implements Comparator<Category> {
	
	
	public int compare(Category e1, Category e2) {
		int response = 0;
		String field1 = null;
		String field2 = null;
		try {
			field1 = removeTags(e1.getTitle());
			field2 = removeTags(e2.getTitle());
			for(int i=0; i < field1.length(); i++) {
				if(field2.length() > i && field1.charAt(i) != field2.charAt(i)) {
					if(Character.isLetter(field1.charAt(i)) && Character.isLetter(field2.charAt(i)))
						response = Character.valueOf(field1.charAt(i)).compareTo(Character.valueOf(field2.charAt(i)));
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
							response = 1;
						else response = -1;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	protected String removeTags(String in) {
		if(in == null) return "";
    	return in.replaceAll("\\<.*?>","");
	}
}