package org.vicomtech.opener.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import org.vicomtech.opener.utils.ResourceLoader;
import org.vicomtech.opener.utils.Language;

/**
 * This class uses OpenNLP NERC models to detect named entities.
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Aitor Garcia Pablos (agarciap@vicomtech.org),
 *  Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class NERC extends ResourceLoader {

	private static final String CONFIG_FILE_PATH = "./config/ner.conf";
	private static Map<String,String> modelMap = null;
	
	private NameFinderME nerc;
	
	public NERC(Language language) throws InvalidFormatException, IOException {
		
		if (modelMap == null) {
			modelMap = super.loadMap(CONFIG_FILE_PATH, true);
		}
		
		String path = modelMap.get(language.getValue());
		InputStream modelIn = NERC.class.getResourceAsStream(path);
		if (modelIn == null) {
			modelIn = new FileInputStream(path);
		}
		this.nerc = new NameFinderME(new TokenNameFinderModel(modelIn));
		modelIn.close();
	}
	
	public Span[] find(String[] tokens) {
		// find entities
		Span[] locationSpans = this.nerc.find(tokens);
		return locationSpans;
	}
	
	public double[] probs() {
		return this.nerc.probs();
	}

}
