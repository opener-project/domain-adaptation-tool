package org.vicomtech.opener.bootstrapping;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import opennlp.tools.util.InvalidFormatException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language;
import org.xml.sax.SAXException;

/**
 * This class process corpora to extract entities
 * 
 * org.vicomtech.opener.bootstrapping is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class CorpusProcessor {

//////////////////////////// CORPUS ENUM /////////////////////////////
	
	public static class CorpusProcessorException extends Exception {
		
		private static final long serialVersionUID = 1L;

		public CorpusProcessorException(String s) {
			super(s);
		}
	}

	/**
	 * Supported corpus enumeration
	 */
	public enum CorpusEnum {
	    ESTER, ANCORA, CONLL
	}
	
	/**
	 * Returns true if exists a processor to parse input corpus
	 * @param corpus : input corpus
	 * @return
	 */
	public static boolean exists(String corpus) {
		for (CorpusEnum c : CorpusEnum.values())
			if (c.toString().equalsIgnoreCase(corpus))
				return true;
		return false;
	}
	
	private static CorpusEnum getCorpusEnum(String corpus) throws CorpusProcessorException {
		for (CorpusEnum c : CorpusEnum.values())
			if (c.toString().equalsIgnoreCase(corpus))
				return c;
		throw new CorpusProcessorException(String.format("corpus '%s' not supported", corpus));
	}
	
/////////////////////////////////////////////////////////////////////
	
	/**
	 * Corpus parser
	 */
	private IParser parser;
	
	/**
	 * Corpus parsing classes
	 */
	private static final String CONLL  = "org.vicomtech.opener.conllparser.ConllParser";
	private static final String ESTER  = "org.vicomtech.opener.esterparser.ESTERParser";
	private static final String ANCORA = "org.vicomtech.opener.ancoraparser.AnCoraParser";
	
	/**
	 * Corpus parser map (<CorpusEnum,CorpusParsingClass>)
	 */
	private Map<CorpusEnum,String> parserMap;
	
	/**
	 * Public constructor
	 * @param corpus : the corpus to process
	 * @param language : corpus language
	 * @param window : features window
	 * @param modSize : NP modifier sum for each head noun
	 * @param lemmatizer : lemmatizer (can be null if not needed)
	 * @param nlpTool : nlp tools used to extract features
	 * @param message : message handler
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws CorpusProcessorException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * @throws ExecException 
	 */
	public CorpusProcessor(String corpus, Set<String> categories, Language language, int window, int modSize,
			NLPtools.Type nlpTool, MessageHandler message) throws InvalidFormatException, IOException, CorpusProcessorException, InstantiationException, IllegalAccessException, ClassNotFoundException, ParseException, InterruptedException, ExecException {
		
		this.createProcessorMapping();
		
		CorpusEnum ce = getCorpusEnum(corpus);
		
		// get the parser for the input corpus
		String s = this.parserMap.get(ce);
		this.parser = (IParser) Class.forName(s).newInstance();
		this.parser.addParameters(categories, language, window, modSize, nlpTool, message);
	}
	
	/**
	 * Creates a map with corpus and the classes
	 *  that are needed to process them
	 */
	public void createProcessorMapping() {
		this.parserMap = new HashMap<CorpusEnum,String>();
		this.parserMap.put(CorpusEnum.CONLL, CONLL);
		this.parserMap.put(CorpusEnum.ESTER, ESTER);
		this.parserMap.put(CorpusEnum.ANCORA, ANCORA);
	}
	
	/**
	 * Get the entity categories
	 * @return
	 */
	public Set<String> getCategories() {
		return this.parser.getCategories();
	}
	
	/**
	 * Get all the tokens of the corpus file
	 * @param file : input corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String[] getTokens(File file) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		return this.parser.getTokens(file);
	}
	
	/**
	 * Get all the lemmas of the corpus file
	 * @param file : input corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String[] getLemmas(File file) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		return this.parser.getLemmas(file);
	}
	
	/**
	 * Get all the entities and its features of the corpus file
	 * @param file : input corpus file
	 * @return
	 * @throws Exception 
	 */
	public Map<String,List<Entity>> getEntities(File file) throws Exception {
		return this.parser.getEntities(file);
	}

}
