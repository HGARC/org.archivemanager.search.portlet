package org.archivemanager.util;
import java.util.Comparator;

import org.heed.openapps.dictionary.RepositoryModel;
import org.heed.openapps.entity.Association;
import org.heed.openapps.entity.Entity;
import org.heed.openapps.entity.EntityService;


public class OrderSorter implements Comparator<Association> {
	private EntityService entityService;
	
	public OrderSorter(EntityService entityService) {
		this.entityService = entityService;
	}
	
	public int compare(Association assoc1, Association assoc2) {
		int response = 0;
		String field1 = null;
		String field2 = null;
		try {
			Entity doc1 = entityService.getEntity(null, assoc1.getTarget());
			Entity doc2 = entityService.getEntity(null, assoc2.getTarget());
			field1 = doc1.getPropertyValue(RepositoryModel.ORDER);
			field2 = doc2.getPropertyValue(RepositoryModel.ORDER);
			if(field1 != null && (field2 != null)) {
				if(field1.equals(field2)) response = 0;
				if(!field1.equals("") && !field2.equals(""))
					response = Integer.valueOf(field1).compareTo(Integer.valueOf(field2));
				else 
					if((field1 != null && !field1.equals(""))) response = -1;
				else 
					if((field2 != null && !field2.equals(""))) response = 1;
			} else if(field1 != null && field2 == null) {
				response = 1;
			} else if(field2 != null && field1 == null) {
				response = -1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
}