package org.vicomtech.opener.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * org.vicomtech.opener.entities is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Feature {

	private enum Type { WORD , NP };
	
	private Type type;
	private List<String> leftModifiers;
	private List<String> rightModifiers;
	private String head;
	private List<String> others;
	private int modSize;
	
	public Feature(int modSize) {
		
		this.type = Type.NP;
		this.leftModifiers = new ArrayList<String>();
		this.rightModifiers = new ArrayList<String>();
		this.head = new String();
		this.others = new ArrayList<String>();
		this.modSize = modSize;
	}
	
	public Feature() {
		
		this.type = Type.WORD;
		this.leftModifiers = new ArrayList<String>();
		this.rightModifiers = new ArrayList<String>();
		this.head = new String();
		this.others = new ArrayList<String>();
	}
	
	public Type getType() {
		return this.type;
	}
	
	public void setHead(String head) {
		this.head = head;
	}
	
	public String getHead() {
		return this.head;
	}
	
	public void addLeftModifier(String modifier) {
		
		if (this.leftModifiers.size() < this.modSize) {
			this.leftModifiers.add(modifier);
		}
		else {
			this.leftModifiers.remove(0);
			this.leftModifiers.add(modifier);
		}
	}
	
	public void addRightModifier(String modifier) {
		
		if (this.rightModifiers.size() < this.modSize) {
			this.rightModifiers.add(modifier);
		}
	}
	
	public void addOther(String other) {

		this.others.add(other);
	}
	
	public String getLeftModifier(int index) {
		return this.leftModifiers.get(index);
	}
	
	public String getRightModifier(int index) {
		return this.rightModifiers.get(index);
	}
	
	public List<String> getLeftModifiers() {
		return this.leftModifiers;
	}
	
	public List<String> getRightModifiers() {
		return this.rightModifiers;
	}
	
	public int getLeftModifiersSize() {
		return this.leftModifiers.size();
	}
	
	public int getRightModifiersSize() {
		return this.rightModifiers.size();
	}
	
	public int getOthersSize() {
		return this.others.size();
	}
	
	public String toString() {
		
		String s = new String();
		if (this.type.equals(Type.NP)) {
			s += "Left Modifiers: ";
			for (String modifier : this.leftModifiers) {
				s += modifier+" , ";
			}
			s += "\n";
		}
		s += "Head: "+this.head;
		if (this.type.equals(Type.NP)) {
			s += "\nRight Modifiers: ";
			for (String modifier : this.rightModifiers) {
				s += modifier+" , ";
			}
		}
		if (this.type.equals(Type.NP)) {
			s += "\nOthers: ";
			for (String other : this.others) {
				s += other+" , ";
			}
		}
		
		return s;
	}
	
	/**
	 * 
	 * @param token
	 * @param featureType : '0' if feature has no right features, else '1'
	 * @return
	 */
	public boolean hasHead(String token, int featureType) {
		if (featureType == 0) return hasHead0(token);
		else return hasHead1(token);
	}
	
	public boolean hasHead0(String token) {
		
		if (this.head.contentEquals(token)) {
			return true;
		}
		else {
			if (this.type.equals(Type.NP)) {
				// lets see if is a modifiers
				for (String s : this.leftModifiers) {
					if (s.contentEquals(token)) {
						// shift old head to others list
						this.others.add(0, this.head);
						// put token as head
						this.head = s;
						// remove modifiers from token index to last
						// and add to the others list
						int start = leftModifiers.size()-1;
						int end = leftModifiers.indexOf(s);
						for (int i=start; i>=end; i--) {
							String m = leftModifiers.get(i);
							this.leftModifiers.remove(m);
							if (!m.contentEquals(s))
								this.others.add(0, m);
						}
						return true;
					}
				}
				// lets see if is at others list
				for (String s : this.others) {
					if (s.contentEquals(token)) {
						// set token as head, and put old head as
						// modifier
						this.leftModifiers.add(this.head);
						this.head = s;
						return true;
					}
				}
				return false;
			}
			else {
				return false;
			}
		}
	}
	
	public boolean hasHead1(String token) {
//		System.out.println("head: "+this.head);
//		System.out.println("token: "+token);
		if (this.head.contentEquals(token)) {
			return true;
		}
		else {
			if (this.type.equals(Type.NP)) {
				String[] array = token.split(" ");
				if (array.length > 1)
					return hasHead(array);
				else {
					// lets see if is a modifiers
					for (String s : this.leftModifiers) {
						if (s.contentEquals(token)) {
							// shift old head to others list
							this.rightModifiers.add(0, this.head);
							// put token as head
							this.head = s;
							// remove modifiers from token index to last
							// and add to the others list
							int start = leftModifiers.size()-1;
							int end = leftModifiers.indexOf(s);
							for (int i=start; i>=end; i--) {
								String m = leftModifiers.get(i);
								this.leftModifiers.remove(m);
								if (!m.contentEquals(s))
									this.rightModifiers.add(0, m);
							}
							return true;
						}
					}
					// lets see if is at others list
					for (String s : this.rightModifiers) {
						if (s.contentEquals(token)) {
							// set token as head, and put old head as
							// modifier
							this.leftModifiers.add(this.head);
							this.head = s;
							// move right modifiers from 0 to s_index-1 to left modifiers
							int start = 0;
							int end = rightModifiers.indexOf(s);
							for (int i=start; i<end; i++) {
								String m = rightModifiers.remove(0);
								this.leftModifiers.add(this.leftModifiers.size(), m);
							}
							// remove new head from right modifiers
							this.rightModifiers.remove(0);
							return true;
						}
					}
					return false;
				}
			}
			else {
				return false;
			}
		}
	}
	
	public boolean hasHead(String[] tokens) {
		
		// if there is no token as head return false
		int index = -1;
		for (int i=0; i<tokens.length; i++) {
			String tok = tokens[i];
			if (tok.contentEquals(this.head)) {
				index = i;
				break;
			}
			for (String m : this.leftModifiers) {
				if (m.contentEquals(tok)) {
					index = -10;
				}
			}
			for (String m : this.rightModifiers) {
				if (m.contentEquals(tok)) {
					index = -20;
				}
			}
			for (String m : this.others) {
				if (m.contentEquals(tok)) {
					index = -30;
				}
			}
		}
		if (index == -1) return false;
		
		// lets see if all tokens exist in Feature
		boolean found = false;
		for (int i=0; i<tokens.length; i++) {
			if (i != index) {
				String tok = tokens[i];
				found = false;
				// find at left tokens
				for (String m : this.leftModifiers) {
					if (m.contentEquals(tok)) {
						found = true;
					}
				}
				if (!found) {
					for (String m : this.rightModifiers) {
						if (m.contentEquals(tok)) {
							found = true;
						}
					}
				}
				if (!found) {
					for (String m : this.others) {
						if (m.contentEquals(tok)) {
							found = true;
						}
					}
				}
				if (!found) break;
			}
		}
		if (!found) return false;
		
		// all tokens exist, update head and remove tokens from modifiers
		found = false;
		for (int i=0; i<tokens.length; i++) {
			if (i != index) {
				String tok = tokens[i];
				found = false;
				// find at left tokens
				for (int j=0; j<this.getLeftModifiersSize(); j++) {
					String m = this.getLeftModifier(j);
					if (m.contentEquals(tok)) {
						this.leftModifiers.remove(j);
						found = true;
						break;
					}
				}
				if (!found) {
					for (int j=0; j<this.getRightModifiersSize(); j++) {
						String m = this.getRightModifier(j);
						if (m.contentEquals(tok)) {
							this.rightModifiers.remove(j);
							found = true;
							break;
						}
					}
				}
				if (!found) {
					for (int j=0; j<this.getOthersSize(); j++) {
						String m = this.others.get(j);
						if (m.contentEquals(tok)) {
							this.others.remove(j);
							found = true;
							break;
						}
					}
				}
			}
		}
		// tokens was at left modifiers => move old tokens to right modifiers
		if (index == -10)
			this.addRightModifier(this.head);
		// tokens was at right modifiers => move old tokens to left modifiers
		if (index == -20)
			this.addLeftModifier(this.head);
		this.setHead(StringUtils.join(tokens, " "));
		return true;
	}
	
	public boolean hasToken(String token) {
		
		if (this.head.contentEquals(token)) {
			return true;
		}
		else {
			if (this.type.equals(Type.NP)) {
				for (String s : this.leftModifiers) {
					if (s.contentEquals(token)) {
						return true;
					}
				}
				for (String s : this.rightModifiers) {
					if (s.contentEquals(token)) {
						return true;
					}
				}
				for (String s : this.others) {
					if (s.contentEquals(token)) {
						return true;
					}
				}
				return false;
			}
			else {
				return false;
			}
		}
	}
	
	public boolean isHead(String token) {
		return this.head.contentEquals(token);
	}
	
	public static List<Feature> mergeFeatures(
			List<Feature> features, int start, int end, String newEntity) {
		
		// merge features from start to end
		Feature mergedFeature = features.get(start);
		for (int i=start+1; i<=end; i++) {
			Feature feature = features.get(i);
			mergedFeature.leftModifiers.addAll(feature.leftModifiers);
			mergedFeature.rightModifiers.addAll(feature.rightModifiers);
			mergedFeature.others.addAll(feature.others);
		}
		
		for (String entity : newEntity.split(" ")) {
			mergedFeature.leftModifiers.remove(entity);
			mergedFeature.rightModifiers.remove(entity);
			mergedFeature.others.remove(entity);
		}
		
		// set new head
		mergedFeature.setHead(newEntity);
		
		// remove features from start to end
		int c = end - start;
		int i = 0;
		while (i <= c) {
			features.remove(start);
			i++;
		}
		
		// insert merged feature
		features.add(start, mergedFeature);
		return features;
		
	}

}
