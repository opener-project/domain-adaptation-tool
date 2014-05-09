package org.vicomtech.opener.bootstrapping;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import opennlp.tools.util.InvalidFormatException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language;
import org.xml.sax.SAXException;

/**
 * This interface provides methods to parse a corpus
 *  and extract the tokens and the entities within.
 * 
 * org.vicomtech.opener.bootstrapping is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public interface IParser {

	/**
	 * Adds parameters to the parser
	 * @param language : corpus language
	 * @param window : features window
	 * @param modSize : NP modifier sum for each head noun
	 * @param nlpTool : nlp tools used to extract features
	 * @param message : message handler
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * @throws ExecException 
	 */
	public void addParameters(Set<String> categories, Language language, int window, int modSize,
			NLPtools.Type nlpTool, MessageHandler message) throws InvalidFormatException, IOException, ParseException, InterruptedException, ExecException;
	
	/**
	 * Get the entity categories
	 * @return
	 */
	public Set<String> getCategories();
	
	/**
	 * Get all the tokens of the corpus file
	 * @param file : input corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String[] getTokens(File file) throws ParserConfigurationException, SAXException, IOException, InterruptedException;
	
	/**
	 * Get all the lemmas of the corpus file
	 * @param file : input corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String[] getLemmas(File file) throws ParserConfigurationException, SAXException, IOException, InterruptedException;
	
	/**
	 * Get all the entities and its features of the corpus file
	 * @param file : input corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws InterruptedException
	 * @throws Exception 
	 */
	public Map<String,List<Entity>> getEntities(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, InterruptedException, Exception;

}
