package org.vicomtech.opener.nlp;

import java.io.IOException;
import java.io.InputStream;

import org.vicomtech.opener.utils.Language;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

/**
 * This class postags a sentence using OpenNLP postagger models
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class PoSTagger {

	private static final String FR_MODEL_PATH = "/fr-pos-ftb-morpho.bin";
	private POSTaggerME postagger;

	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME. The
	 * model is only loaded once, when the first instance is created, and is
	 * shared among all instances.
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public PoSTagger(Language language) throws InvalidFormatException, IOException {
		
		InputStream modelIn = null;
		if (language.toString().equalsIgnoreCase("fr")) {
			modelIn = PoSTagger.class.getResourceAsStream(
					FR_MODEL_PATH);
		}
		postagger = new POSTaggerME(new POSModel(modelIn));
		modelIn.close();
	}

	public String[] postag(String[] tokens) {
		String[] tags = postagger.tag(tokens);
		return tags;
	}

}
