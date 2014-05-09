package org.vicomtech.opener.constituentparser;

import java.io.IOException;

import org.vicomtech.opener.utils.Language;

import opennlp.tools.parser.Parse;

/**
 * This class is a modification of a constituent parser developed by Rodrigo Agerri
 * 
 * org.vicomtech.opener.constituentparser is a module of Domain Adaptation Tool for OpeNER
 * @author Rodrigo Agerri 2012/11/30 UPV-EHU,
 * Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 * 
 */
public class AnnotateConstituents {

  public ConstituentParsing parser;

  public AnnotateConstituents(Language lang) throws IOException {
    parser = new ConstituentParsing(lang);
  }

  /**
   * This method uses the Apache OpenNLP to perform POS tagging.
   * 
   * It gets a Map<SentenceId, tokens> from the input KAF document and iterates
   * over the tokens of each sentence to annotated POS tags.
   * 
   * It also reads <wf>, elements from the input KAF document and fills
   * the KAF object with those elements plus the annotated POS tags in the <term>
   * elements.
   * 
   * @param tokenSent
   *          tokenized sentence
   * 
   * @return JDOM KAF document containing <wf>, and <terms> elements.
   */

  public String getConstituentParse(String tokenSent) throws IOException {

    StringBuffer parsingDoc = new StringBuffer();
      
    // Constituent Parsing

    Parse parsedSentence[] = parser.parse(tokenSent,1);
    for (Parse parsedSent : parsedSentence) {
    	  parsedSent.show(parsingDoc);
    	  parsingDoc.append("\n");
    }
	return parsingDoc.toString();

    }
  

}
