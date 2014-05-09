package org.vicomtech.opener.esterparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the categories of an entity
 * and the tags that represent each subcategory of the 
 * the categories
 * 
 * org.vicomtech.opener.esterparser is a module of Domain Adaptation Tool for OpeNER
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
		tags.add("pers");
		tags.add("pers.hum");
		tags.add("pers.anim");
		tags.add("pers.imag");
		tags.add("pers.spir");
		tags.add("gsp.pers");
		//tags.add("prod.award/pers.hum");
		//tags.add("prod.printing/pers.hum");
		String category = Categories.PERSON.toString();
		
		this.categories.put(category, tags);
		
		tags = new ArrayList<String>();
		tags.add("org");
		tags.add("org.pol");
		tags.add("org.edu");
		tags.add("org.non-profit");
		tags.add("org.com");
		tags.add("org.synd");
		tags.add("gsp.org");
		//tags.add("fac/org.edu");
		//tags.add("fac/org.pol");
		//tags.add("fac/org.com");
		//tags.add("fac/org");
		//tags.add("fac/org.non-profit");
		//tags.add("org.pol/fac");
		//tags.add("gsp.org/fac");
		//tags.add("gsp.loc/org.com");
		//tags.add("gsp.loc/org.pol");
		//tags.add("org.edu/fac");
		tags.add("org.mil");
		category = Categories.ORGANIZATION.toString();
		
		this.categories.put(category, tags);
		
		tags = new ArrayList<String>();
		tags.add("loc");
		tags.add("loc.geo");
		tags.add("loc.geo.line");
		tags.add("loc.line");
		//tags.add("loc.addr.post");
		//tags.add("loc.addr.tel");
		//tags.add("loc.addr.elec");
		tags.add("gsp.loc");
		//tags.add("gsp.loc/fac");
		//tags.add("gsp.loc/org.com");
		//tags.add("gsp.loc/org.pol");
		//tags.add("fac/gsp.loc");
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
