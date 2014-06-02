package org.vicomtech.opener.conllparser;

import org.vicomtech.opener.bootstrapping.Parser;
import org.vicomtech.opener.conllparser.ConllParser.BIO;
import org.vicomtech.opener.nlp.TagsetMappings;

/**
 * This class represents a line in Conll format.
 * 
 * org.vicomtech.opener.conllparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class ConllLine {

	private int index;
	private String word;
	private String lemma;
	private String pos;
	private int head;
	private String bioString;
	private String category;
	private BIO bio = null;
	
	private static final String SEPARATOR = "\\|";
	private static final String ELLIPSIS  = "_";
	private static final String NE        = "ne=";
	
	public ConllLine(int index, String word, String lemma,
					 String pos, int head, String bioString) {
		this.index     = index;
		this.word      = word;
		this.lemma     = lemma;
		this.pos       = pos;
		this.head      = head;
		this.bioString = bioString;
		this.processLine();
	}
	
	public ConllLine(int index, String word, String lemma,
			 	 	 String pos, int head) {
		this.index     = index;
		this.word      = word;
		this.lemma     = lemma;
		this.pos       = pos;
		this.head      = head;
		this.bioString = null;
		this.processLine();
	}
	
	private void processLine() {
		this.category = null;
		if (this.bioString == null && TagsetMappings.isProperNoun(this.pos))
			this.category = Parser.EMPTY_CATEGORY;
		else if (this.bioString != null) {
			this.bio = BIO.getBIO(bioString);
			if (this.bio.equals(BIO.OUT)) {
				if (TagsetMappings.isProperNoun(this.pos))
					this.category = Parser.EMPTY_CATEGORY;
				else
					this.category = null;
			}
			else if (this.bio.equals(BIO.BEGIN) || this.bio.equals(BIO.IN)) {
				this.category = BIO.getEntityType(bioString);
			}
			else
				this.category = Parser.EMPTY_CATEGORY;
		}
	}
	
	public boolean isEntity() {
		return this.category != null;
	}
	
	public boolean isEllipsis() {
		return this.word.equalsIgnoreCase(ELLIPSIS)
				&& this.lemma.equalsIgnoreCase(ELLIPSIS)
				&& TagsetMappings.isOther(this.pos);
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public String getWord() {
		return this.word;
	}
	
	public String getLemma() {
		return this.lemma;
	}
	
	public String getPoS() {
		return this.pos;
	}
	
	public int getHead() {
		return this.head;
	}
	
	public String getBioString() {
		return this.bioString;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public BIO getBIO() {
		return this.bio;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
}
