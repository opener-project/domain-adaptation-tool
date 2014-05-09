package org.vicomtech.opener.entities;

import java.util.List;
import java.util.Map;

import org.vicomtech.opener.utils.Utils;

/**
 * This class stores the information of an entity and its
 * features.
 * 
 * org.vicomtech.opener.entities is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Entity {

	/**
	 * The entity string
	 */
	private String entity;
	
	/**
	 * Entity category
	 */
	private String category;
	
	/**
	 * Token window
	 */
	private List<String> leftFeatures;
	private List<String> middleFeatures;
	private List<String> rightFeatures;
	
	/**
	 * Public constructor
	 * @param entity : entity string
	 * @param category : entity category
	 * @param leftText : left tokens
	 * @param rightText : right tokens
	 */
	public Entity(String entity, String category,
			List<String> leftFeatures, List<String> middleFeatures,
			List<String> rightFeatures) {
		
		this.entity = entity;
		this.category = category;
		this.leftFeatures = leftFeatures;
		this.middleFeatures = middleFeatures;
		this.rightFeatures = rightFeatures;
		
	}
	
	/**
	 * Get entity string
	 * @return
	 */
	public String getEntity() {
		return this.entity;
	}
	
	/**
	 * Get entity category
	 * @return
	 */
	public String getCategory() {
		return this.category;
	}
	
	/**
	 * Get left tokens
	 * @return
	 */
	public List<String> getLeftFeatures() {
		return this.leftFeatures;
	}
	
	/**
	 * Get middle tokens
	 * @return
	 */
	public List<String> getMiddleFeatures() {
		return this.middleFeatures;
	}
	
	/**
	 * Get right tokens
	 * @return
	 */
	public List<String> getRightFeatures() {
		return this.rightFeatures;
	}
	
	/**
	 * Add feature to the right features list
	 * @param feature : input feature
	 */
	public void addRightFeature(String feature) {
		this.rightFeatures.add(feature);
	}
	
	/**
	 * Add features to the right features list
	 * @param feature : input feature
	 */
	public void addRightFeatures(List<String> features) {
		this.rightFeatures.addAll(features);
	}
	
	/**
	 * True if the entity has features
	 * @return
	 */
	public boolean hasFeatures() {
		return this.leftFeatures.size()
				+this.middleFeatures.size()
				+this.rightFeatures.size() > 0;
	}
	
	/**
	 * Adds entity maps to an existing entity map.
	 * An entity map is: <Category,List<Entity>>
	 * @param newEntities : new entities maps
	 * @param entities : existing entities maps
	 * @return
	 */
	public static int addEntities(Map<String,List<Entity>> newEntities,
			Map<String,List<Entity>> entities) {
		
		int count = 0;
		for (String category : newEntities.keySet()) {
			count += newEntities.get(category).size();
			if (entities.containsKey(category)) {
				
				entities.get(category).addAll(
						newEntities.get(category));
			}
			else {
				entities.put(category, newEntities.get(category));
			}
		}
		return count;
	}
	
	/**
	 * Return true if entity is at entity list
	 * @param entityList : input entity list
	 * @param entity : input entity
	 * @return
	 */
	public static boolean contains(List<Entity> entityList, Entity entity) {
		
		for (Entity e : entityList) {
			String ent = e.getEntity();
			List<String> leftFeatures = e.getLeftFeatures();
			List<String> rightFeatures = e.getRightFeatures();
			List<String> middleFeatures = e.getMiddleFeatures();
			
			// check entity
			boolean equal = ent.contentEquals(entity.getEntity());
				
			// check left features
			if (equal)
				equal = Utils.contains(leftFeatures, entity.getLeftFeatures());
			// check right features
			if (equal)
				equal = Utils.contains(rightFeatures, entity.getRightFeatures());
			// check middle features
			if (equal)
				equal = Utils.contains(middleFeatures, entity.getMiddleFeatures());
			
			if (equal) return true;
		}
		return false;
	}

}
