package org.vicomtech.opener.ancoraparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the categories of an entity
 * and the tags that represent each subcategory of the the categories
 * 
 * org.vicomtech.opener.ancoraparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class EntityCategories {

	/**
	 * <Category,List<Subcategory>>
	 */
    private Map<String,List<String>> categories;
    
    /**
     * Enumeration with all the categories
     */
    public static enum Categories {
    	PERSON, ORGANIZATION, LOCATION
    }
	
    /**
     * Public constructor, inits the tags info
     */
	public EntityCategories() {
		
		categories = new HashMap<String,List<String>>();
		
		List<String> tags = new ArrayList<String>();
		tags.add("person");
		String category = Categories.PERSON.toString();
		
		this.categories.put(category, tags);
		
		tags = new ArrayList<String>();
		tags.add("organization");
		category = Categories.ORGANIZATION.toString();
		
		this.categories.put(category, tags);
		
		tags = new ArrayList<String>();
		tags.add("location");
		category = Categories.LOCATION.toString();
		
		this.categories.put(category, tags);
	}
	
	/**
	 * Get the categories
	 * @return
	 */
	public Set<String> getCategories() {
		return this.categories.keySet();
	}

	/**
	 * Get the tags of a category
	 * @param category : input category
	 * @return
	 */
	public List<String> getTags(String category) {
		return this.categories.get(category);
	}
	
}
