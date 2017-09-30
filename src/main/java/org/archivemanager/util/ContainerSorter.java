package org.archivemanager.util;
import java.util.Comparator;

import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;
import org.heed.openapps.util.NumberUtility;


public class ContainerSorter implements Comparator<Association> {
	private EntityService entityService;
	
	public ContainerSorter(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public int compare(Association assoc1, Association assoc2) {
		int response = 0;
		String field1 = null;
		String field2 = null;
		try {
			Entity doc1 = entityService.getEntity(null, assoc1.getTarget());
			Entity doc2 = entityService.getEntity(null, assoc2.getTarget());
			field1 = doc1.getPropertyValue(RepositoryModel.CONTAINER);
			field2 = doc2.getPropertyValue(RepositoryModel.CONTAINER);
			if(field1 != null && field2 != null) {
				if(field1.equals(field2)) response = 0;
				if(!field1.equals("") && !field2.equals("")) {
					String[] parts1 = field1.trim().replace("  ", " ").split(" ");
					String[] parts2 = field2.trim().replace("  ", " ").split(" ");
					if(parts1.length > 1 && parts2.length > 1) {						
						int vote1 = compareContainer(parts1[0].toLowerCase().trim(), parts1[1].replace(",","").trim(), parts2[0].toLowerCase().trim(), parts2[1].replace(",","").trim());
						if(vote1 != 0) response = vote1;
						if(parts1.length > 3 && parts2.length > 3) {
							int vote2 = compareContainer(parts1[2].toLowerCase(), parts1[3].replace(",",""), parts2[2].toLowerCase(), parts2[3].replace(",",""));
							if(vote2 != 0) response = vote2;
							if(parts1.length > 5 && parts2.length > 5) {
								int vote3 = compareContainer(parts1[4].toLowerCase(), parts1[5].replace(",",""), parts2[4].toLowerCase(), parts2[5].replace(",",""));
								if(vote3 != 0) response = vote3;
							}
						}
					}
				} else if((field1 != null && !field1.equals(""))) response = -1;
				else if((field2 != null && !field2.equals(""))) response = 1;
		} else if(field1 != null && field2 == null) {
			response = 1;
		} else if(field2 != null && field1 == null) 
			response = -1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		//System.out.println(field1 + " compared to " + field2 + " evaluated to " + response);
		return response;		
	}
	
	protected int compareContainer(String type1, String value1, String type2, String value2) {
		if(type1.equals(type2) && value1.equals(value2)) return 0;
		if(type1.equals(type2)) return compareNumericStrings(value1, value2);
		if(type1.equals("boxes")) type1 = "box";
		if(type2.equals("boxes")) type2 = "box";		
		if(type1.startsWith("box")) return 1;
		if(type1.startsWith("reels")) {
			if(type2.startsWith("box")) return -1;
			else return 1;
		} 
		if(type1.startsWith("package")) {
			if(type2.startsWith("box") || type2.startsWith("reels")) return -1;
			else return 1;
		} 
		if(type1.startsWith("folder")) {
			if(type2.startsWith("box") || type2.startsWith("reels") || type2.startsWith("package")) return -1;
			else return 1;
		} 
		if(type1.equals("film") && value1.equals("Vault")) return 1; 
		return compareNumericStrings(value1, value2);
	}
	protected int compareNumericStrings(String field1, String field2) {
		if(field1.equals(field2)) return 0;
		String[] parts1 = field1.split("-");
		String[] parts2 = field2.split("-");
		if(parts1.length > 0 && parts2.length > 0) {
			for(int i=0; i < parts1.length; i++) {
				if(parts1[i].length() > parts2[i].length()) return 1;
				if(parts2[i].length() > parts1[i].length()) return -1;
				if(parts1[i] != parts2[i] && NumberUtility.isInteger(parts1[i]) && NumberUtility.isInteger(parts2[i]))
					return Integer.valueOf(parts1[i]).compareTo(Integer.valueOf(parts2[i]));
				for(int j=0; j < parts1[i].length(); j++) {
					if(parts1[i].charAt(j) != parts2[i].charAt(j)) {
						if(parts1[i].charAt(j) > parts2[i].charAt(j)) return 1;
						else if(parts1[i].charAt(j) < parts2[i].charAt(j)) return -1;
					}
				}
			}
		}
		return field1.compareTo(field2);
	}
}