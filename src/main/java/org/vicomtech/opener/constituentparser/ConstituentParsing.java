package org.vicomtech.opener.constituentparser;

import java.io.IOException;
import java.io.InputStream;

import org.vicomtech.opener.utils.Language;


import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

/**
 * Simple Parse module based on Apache OpenNLP by Rodrigo Agerri and modified by Andoni Azpeitia.
 * 
 * org.vicomtech.opener.constituentparser is a module of Domain Adaptation Tool for OpeNER
 * @author Rodrigo Agerri 2012/11/30 UPV-EHU,
 * Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 * 
 */

public class ConstituentParsing {

	private ParserModel parserModel;
	private Parser parser;
	private HeadRulesReaderCP headRulesReader;

	/**
	 * It constructs an object Parser from the Parser class. First it loads a model,
	 * then it initializes the nercModel and finally it creates a nercDetector
	 * using such model.
	 * @throws IOException 
	 */
	public ConstituentParsing(Language language) throws IOException {

		InputStream trainedModel = null;
    
		if (language.toString().equalsIgnoreCase("fr")) {
			trainedModel = getClass().getResourceAsStream(
					"/fr-parsing-fun.bin");
		}
		headRulesReader=new HeadRulesReaderCP(language);

		parserModel = new ParserModel(trainedModel);
		trainedModel.close();

		parser = ParserFactory.create(parserModel);
	}

	/**
	 * This method receives as an input a tokenized sentence and stores the 
	 * parses in a Parse object array. The parses can then be visualized in the usual 
	 * treebank format by using the Parse.show() function. 
	 * 
	 *  
	 * @param tokenized sentence
	 * @param int number of parsers 
	 * @return an array Parse objects (as many as numParsers parameter) 
	 */
	public Parse[] parse(String sentence, int numParsers) {
		Parse parsedSentence[] = ParserTool.parseLine(sentence, parser, numParsers);
		for(Parse parse:parsedSentence){
			this.headRulesReader.annotateHeads(parse);
		}
		return parsedSentence;
	}

}
