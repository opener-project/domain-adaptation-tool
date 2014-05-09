package org.vicomtech.opener.chunker;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chunk.
 * 
 * org.vicomtech.opener.chunker is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Chunk {

	public static class Constituent {
		
		private String token;
		private String pos;
		
		private Constituent(String token, String pos) {
			this.token = token;
			this.pos = pos;
		}
		
		public String getToken() {
			return this.token;
		}
		public String getPoS() {
			return this.pos;
		}
	}
	
	private static final String OUT = "O";
	private static final String IN = "I-";
	private static final String BEGIN = "B-";
	
	private List<Constituent> constituents;
	private int headIndex;
	private String tag;
	
	public Chunk(String token, String pos, String tag) {
		constituents = new ArrayList<Constituent>();
		Constituent c = new Constituent(token, pos);
		this.constituents.add(c);
		this.headIndex = 0;
		if (isOut(tag))
			this.tag = tag;
		else {
			this.tag = tag.substring(BEGIN.length(), tag.length());
		}
	}
	
	public void addConstituent(String token, String pos) {
		Constituent c = new Constituent(token, pos);
		this.constituents.add(c);
	}
	
	public List<Constituent> getConstituents() {
		return this.constituents;
	}
	
	public boolean isOut() {
		return isOut(this.tag);
	}
	
	public int size() {
		return this.constituents.size();
	}
	
	public String getToken(int i) {
		return this.constituents.get(i).token;
	}
	
	public String getPoS(int i) {
		return this.constituents.get(i).pos;
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public String getHead() {
		return this.constituents.get(headIndex).token;
	}
	
	public int getHeadIndex() {
		return this.headIndex;
	}
	
	public void setHead(int index) {
		this.headIndex = index;
	}
	
	public static boolean isBegin(String tag) {
		return tag.startsWith(BEGIN);
	}
	
	public static boolean isIn(String tag) {
		return tag.startsWith(IN);
	}
	
	public static boolean isOut(String tag) {
		return tag.contentEquals(OUT);
	}
	
	public String toString() {
		
		String s = new String();
		if (this.tag.equals(OUT)) {
			s += this.getToken(0)+"_"+this.getPoS(0);
		}
		else {
			s += "[" + this.getTag()+" ";
			for (int i=0; i<this.constituents.size(); i++) {
				Constituent c = this.constituents.get(i);
				s += c.token + "_";
				if (i == this.headIndex)
					s += "H=" + c.pos;
				else
					s += c.pos;
				s += " ";
			}
			s += "]";
		}
		return s;
	}

}
