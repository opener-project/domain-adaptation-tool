package org.vicomtech.opener.nlp;

/**
 * NLP tools enumeration used for feature extraction
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class NLPtools {

	public enum Type {
	    TOKENIZER, POSTAGGER, LEMMATIZER,
	    CHUNKER, LEMMATIZER_CHUNKER, POSTAGGER_CHUNKER,
	    CONSTITUENT_PARSER, LEMMATIZER_POSTAGGER, ALL,
	    ALL_NO_CHUNK, CONCAT_POS
	}
	
	@SuppressWarnings("serial")
	public static class NLPtoolsException extends Exception {

		public NLPtoolsException(String s) {
			super(s);
		}
	}

}