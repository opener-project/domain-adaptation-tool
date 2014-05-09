package org.vicomtech.opener.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.parser.Parse;

import org.vicomtech.opener.constituentparser.AnnotateConstituents;
import org.vicomtech.opener.entities.Feature;
import org.vicomtech.opener.utils.Language;

/**
 * This class uses org.vicomtech.opener.constituentparser to detect constituents
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class ConstituentParser {

	private AnnotateConstituents annotate;
	private int modSize;
	
	public ConstituentParser(Language language, int modSize) throws IOException {	
		// create annotator
    	this.annotate = new AnnotateConstituents(language);
    	this.modSize = modSize;
	}
	
	public List<Feature> parse(
			String text, boolean show) {
    	
    	// parse text
    	Parse[] parsedSentence = this.annotate.parser.parse(text,1);
    	
    	if (show)
    		for (Parse a : parsedSentence)
    			a.show();
    	
    	return this.parse(parsedSentence);
    }
	
	private List<Feature> parse(Parse[] parsedSentence) {
    	
		// for each child...
		List<Feature> features = new ArrayList<Feature>();
    	for (Parse parsedSent : parsedSentence) {
    		// add NP to features
    		if (parsedSent.getType().contentEquals("NP")
    				&& !hasGrandchildren(parsedSent)) {
    			features.add(this.parseNP(parsedSent));
    		}
    		// add parse recursively
    		else if (parsedSent.getChildCount() > 0) {
    			features.addAll(this.parse(parsedSent.getChildren()));
    		}
    		// add word to features
    		else {
    			Feature feature = new Feature();
    			feature.setHead(parsedSent.getCoveredText());
    			features.add(feature);
    		}
    	}
    	return features;
    }
    
    private boolean hasGrandchildren(Parse parsedSentence) {
    	
    	// for each child, if the child has children return true 
	    for (Parse child : parsedSentence.getChildren())
	    	if (this.getDescendantCount(child) > 1)
	    		return true;
	   	return false;
    }
    
    private Feature parseNP(Parse NP) {
    	
    	Feature feature = new Feature(modSize);
    	
    	// for each child
    	boolean found = false;
    	for (Parse child : NP.getChildren()) {
    		if (found) {
    			feature.addOther(child.getCoveredText());
    		}
    		// if is head, break
    		else if (this.isHead(child)) {
    			feature.setHead(child.getCoveredText());
    			found = true;
    		}
    		// if is modifier add
    		else if (!child.getType().contentEquals("D")) {
    			feature.addLeftModifier(child.getCoveredText());
    		}
    		else {
    			feature.addOther(child.getCoveredText());
    		}
    	}
    	return feature;
    }
    
    private int getDescendantCount(Parse node) {
    	
    	String coveredText = node.getCoveredText();
    	return coveredText.split(" ").length;
    }
    
    private boolean isHead(Parse parse) {
    	return parse.getType().endsWith("=H");
    }

}
