package org.vicomtech.opener.bootstrapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.vicomtech.opener.bootstrapping.CorpusProcessor.CorpusProcessorException;
import org.vicomtech.opener.bootstrapping.SeedUtils.SeedFileMalformedException;
import org.vicomtech.opener.dictionary.Dictionary;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.svm.SVMWriter;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.Utils;
import org.xml.sax.SAXException;

/**
 * This class parses the corpus and extracts all the features in
 * order to train a SVMs that are able to identify Domain Entities.
 * 
 * This class implements the method described at:
 * http://www.cs.utah.edu/~riloff/pdfs/acl10-semtag.pdf
 * by Ruihong Huang and Ellen Riloff.
 * 
 * org.vicomtech.opener.bootstrapping is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Parser {

	/**
	 * The Message Handlers
	 */
	private MessageHandler message;
	
	/**
	 * Corpus name
	 */
	private String name;
	//private Language language;
	
	/**
	 * The corpus, can be a directory or a file
	 */
	private File trainCorpus = null;
	private File devCorpus = null;
	private File testCorpus = null;
	
	/**
	 * The seed-list
	 */
	//private List<String> seedList = null;
	private Map<String,List<String>> seedList = null;
	
	/**
	 * Token window size for each entity
	 */
	private int window;
	//private int modSize;
	
	/**
	 * Balanced | unbalanced seed train list boolean
	 */
	private boolean balanced;
	
	private boolean tfidf;

	/**
	 * Dictionary that maps tokens to word vector
	 */
	private Dictionary dictionary;
	
	/**
	 * Entity maps <Category,List<Entity>>
	 */
	private Map<String,List<Entity>> trainEntityMap;
	private Map<String,List<Entity>> devEntityMap;
	private Map<String,List<Entity>> testEntityMap;
	
	/**
	 * NLP processors
	 */
	private NLPtools.Type nlpTool;
//	private SentenceDetector sentenceDetector = null;
//	private PoSTagger posTagger = null;
//	private ConstituentParser constituentParser = null;
//	private Chunker chunker = null;
//	private Lemmatizer lemmatizer = null;
	
	/**
	 * Corpus parser
	 */
	private CorpusProcessor corpusProcessor;
	
	public static final String EMPTY_CATEGORY = "NONE";
	
	/**
	 * Public constructor
	 * @param corpus : input corpus to process
	 * @param language : corpus language
	 * @param trainCorpus : can be a directory or a file, must exist
	 * @param devCorpus : can be a directory or a file, can be null
	 * @param testCorpus : can be a directory or a file, can be null
	 * @param seedFile : the file with seed-list
	 * @param window : token window size
	 * @param modSize : NP modifier sum for each head noun
	 * @param balanced : to output balanced | unbalanced seed train file
	 * @param tfidf : to create tf-idf features or not
	 * @param name : output corpus name
	 * @param nlpTool : nlp tools to extract features
	 * @param message : message handler
	 * @throws IOException 
	 * @throws CorpusProcessorException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ParseException 
	 * @throws SeedFileMalformedException 
	 * @throws InterruptedException 
	 * @throws ExecException 
	 */
	public Parser(String corpus, Language language, File trainCorpus, File devCorpus,
			File testCorpus, File seedFile, int window, int modSize, 
			boolean balanced, boolean tfidf, String name, NLPtools.Type nlpTool,
			MessageHandler message) throws IOException, CorpusProcessorException, InstantiationException, IllegalAccessException, ClassNotFoundException, ParseException, SeedFileMalformedException, InterruptedException, ExecException {
		
		this.trainCorpus = trainCorpus;
		this.devCorpus = devCorpus;
		this.testCorpus = testCorpus;
		this.message = message;
		this.window = window;
		this.balanced = balanced;
		this.tfidf = tfidf;
		this.name = name;
		
		this.trainEntityMap = new HashMap<String,List<Entity>>();
		this.devEntityMap = new HashMap<String,List<Entity>>();
		this.testEntityMap = new HashMap<String,List<Entity>>();
		
		// get the seed-list
		this.seedList = SeedUtils.parseSeedFile(seedFile);
		//TODO
		//this.seedList = Utils.readFile(seedFile);
		
		this.nlpTool = nlpTool;
	
		// Corpus parser
		this.corpusProcessor = new CorpusProcessor(corpus,
				this.seedList.keySet(), language, window, modSize, nlpTool, message);
	}
	
	private void displayParameters(Language lang, File seedFile, int modSize) {
		this.message.displayHeader("PARAMETERS");
		this.message.displayTextln(String.format("Input train corpus dir: %s", trainCorpus.getAbsolutePath()));
		if (devCorpus != null) {
			this.message.displayTextln(String.format("Input dev corpus dir:   %s", devCorpus.getAbsolutePath()));
		}
		if (testCorpus != null) {
			this.message.displayTextln(String.format("Input test corpus dir:  %s", testCorpus.getAbsolutePath()));
		}
		this.message.displayTextln(String.format("Language:           %s", lang.getValue()));
		this.message.displayTextln(String.format("Seed-list file:     %s", seedFile.getAbsolutePath()));
		this.message.displayTextln(String.format("Features:           %s", nlpTool));
		this.message.displayTextln(String.format("Window size:        %s", window));
		if (nlpTool.equals(NLPtools.Type.ALL) || nlpTool.equals(NLPtools.Type.CHUNKER)) {
			this.message.displayTextln(String.format("NP modifiers size:  %s", modSize));
		}
		this.message.displayTextln(String.format("Balanced:           %s", balanced));
		this.message.displayTextln(String.format("TF-IDF:             %s", tfidf));
		this.message.displayTextln(String.format("Output corpus name: %s", name));
	}
	
	/**
	 * Static method to parse a corpus to SVM
	 * @param corpus : input corpus to process
	 * @param language : corpus language
	 * @param trainCorpus : can be a directory or a file, must exist
	 * @param devCorpus : can be a directory or a file, can be null
	 * @param testCorpus : can be a directory or a file, can be null
	 * @param seedFile : the seed file list
	 * @param window : token window size
	 * @param modSize : NP modifier sum for each head noun
	 * @param balanced : to output balanced | unbalanced seed train file
	 * @param corpus : corpus name
	 * @param nlpTool : nlp tools to extract features
	 * @param message : message handler
	 * @throws Exception 
	 */
	public static void parseCorpus(String corpus, Language language, File trainCorpus,
			File devCorpus, File testCorpus, File seedFile,
			int window, int modSize,  boolean balanced, boolean tfidf,
			String name, NLPtools.Type nlpTool,
			MessageHandler message) throws Exception {
		
		Parser parser = new Parser(corpus, language, trainCorpus, devCorpus,
				testCorpus, seedFile, window, modSize, balanced, tfidf,
				name, nlpTool, message);
		
		parser.displayParameters(language, seedFile, modSize);
		
		parser.message.displayHeader("PARSING CORPUS");
		
		// parse corpus
		parser.parseCorpus();
		
		// add stop-words to the dictionary
		parser.dictionary.addStopWords();
		
		// write text within entities to dictionary
		//parser.entities2dictionary();
		
		// set values at dictionary
		parser.addPoS2Dictionary();
		parser.dictionary.addDictionaryValues();
		
		// add category label mapping
		parser.addCategoryLabelMapping();
		
		// write SVM files
		parser.writeParsedCorpus();
		
		// binarize to file
		parser.message.displayTextln("Binarizing dictionary.......");
		String dictionary = parser.dictionary.binarize(parser.name);
		parser.message.displayTextln("Dictionary '"+dictionary+"' binarized!");
		parser.message.displayDivider();
				
		// write dictionary
		parser.message.displayTextln("Writing dictionary.......");
		dictionary = parser.dictionary.write(parser.name);
		parser.message.displayTextln("Dictionary '"+dictionary+"' written!");
		parser.message.displayDivider();

	}
	
	/**
	 * Parses the corpus to SVM
	 * @throws Exception 
	 */
	private void parseCorpus() throws Exception {
		
		// initialize vars
		this.dictionary = new Dictionary();
		
		// parse train corpus
		this.message.displayTextln("Parsing train corpus.......");
		if (this.trainCorpus.isDirectory()) {
			for (File file : this.trainCorpus.listFiles()) {
				this.parseFile(file, this.trainEntityMap);
			}
		}
		else {
			this.parseFile(this.trainCorpus, this.trainEntityMap);
		}
		
		// parse dev corpus
		if (this.devCorpus != null && this.devCorpus.exists()) {
			this.message.displayTextln("Parsing dev corpus.......");
			if (this.devCorpus.isDirectory()) {
				for (File file : this.devCorpus.listFiles()) {
					this.parseFile(file, this.devEntityMap);
				}
			}
			else {
				this.parseFile(this.devCorpus, this.devEntityMap);
			}
		}
		
		// parse test corpus
		if (this.testCorpus != null && this.testCorpus.exists()) {
			this.message.displayTextln("Parsing test corpus.......");
			if (this.testCorpus.isDirectory()) {
				for (File file : this.testCorpus.listFiles()) {
					this.parseFile(file, this.testEntityMap);
				}
			}
			else {
				this.parseFile(this.testCorpus, this.testEntityMap);
			}
		}
	}
	
	/**
	 * Parses a single file and add the info to the token dictionary
	 * and the entities
	 * @param file : input file
	 * @throws Exception 
	 */
	private void parseFile(File file, 
			Map<String,List<Entity>> entityMap) throws Exception {
		
		if (!file.toString().endsWith("~")) {
			
			this.message.displayTextln("File: "+file);
			
			// if 'posTagger' option don't add to dictionary
		//	if (this.posTagger == null || this.lemmatizer != null) {
			if (!this.nlpTool.equals(NLPtools.Type.POSTAGGER)
					&& !this.nlpTool.equals(NLPtools.Type.CONCAT_POS)) {
				// get dictionary
				this.message.displayTextln("Getting tokens.......");
				// add features within file to dictionary
				this.addFeatures2Dictionary(file);
				
				this.message.displayTextln("DONE!");
			}
			
			// get entities
			this.message.displayText("Getting entities");
			Map<String,List<Entity>> tmp = this.corpusProcessor.getEntities(file);
			this.message.displayTextln("");
			
			// add entities
			int count = Entity.addEntities(tmp, entityMap);
			// add features within entity map to dictionary
			this.addFeatures2Dictionary(tmp);
					
			this.message.displayTextln(count+" entities added!");
			
			this.message.displayDivider();
		
		}
	}
	
	/**
	 * Find tokens within input file, and adds features to the dictionary
	 * @param file : input file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void addFeatures2Dictionary(File file)
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		
		String[] tokens = this.corpusProcessor.getTokens(file);
		this.dictionary.addDictionary(tokens);
		
		// get lemmas
		//if (this.lemmatizer != null) {
		if (this.nlpTool.equals(NLPtools.Type.LEMMATIZER)
				|| this.nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)
				|| this.nlpTool.equals(NLPtools.Type.LEMMATIZER_CHUNKER)
				|| this.nlpTool.equals(NLPtools.Type.ALL)) {
			String[] lemmas = this.corpusProcessor.getLemmas(file);
			this.dictionary.addDictionary(lemmas);
		}
	}
	
	/**
	 * Find tokens within input entity mapping, and adds features to the dictionary
	 * @param entityMap : input entity mapping
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void addFeatures2Dictionary(Map<String,List<Entity>> entityMap)
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		
		for (String key : entityMap.keySet()) {
			List<Entity> entityList = entityMap.get(key);
			for (Entity entity : entityList) {
				this.dictionary.addDictionary(entity.getLeftFeatures());
				this.dictionary.addDictionary(entity.getMiddleFeatures());
				this.dictionary.addDictionary(entity.getRightFeatures());
			}
		}
	}
	
	/**
	 * Extracts text within the entities and writes them to dictionary
	 */
	private void entities2dictionary() {
		
		List<String> tokens = new ArrayList<String>();
		
		// extract text from train entities
		for (String key : this.trainEntityMap.keySet()) {
			List<Entity> entityList = this.trainEntityMap.get(key);
			for (Entity entity : entityList) {
				tokens.addAll(entity.getLeftFeatures());
				tokens.addAll(entity.getMiddleFeatures());
				tokens.addAll(entity.getRightFeatures());
			}
		}
		
		// extract text from dev entities
		if (this.devCorpus != null && this.devCorpus.exists()) {
			for (String key : this.devEntityMap.keySet()) {
				List<Entity> entityList = this.devEntityMap.get(key);
				for (Entity entity : entityList) {
					tokens.addAll(entity.getLeftFeatures());
					tokens.addAll(entity.getMiddleFeatures());
					tokens.addAll(entity.getRightFeatures());
				}
			}
		}
		
		// extract text from test entities
		if (this.testCorpus != null && this.testCorpus.exists()) {
			for (String key : this.testEntityMap.keySet()) {
				List<Entity> entityList = this.testEntityMap.get(key);
				for (Entity entity : entityList) {
					tokens.addAll(entity.getLeftFeatures());
					tokens.addAll(entity.getMiddleFeatures());
					tokens.addAll(entity.getRightFeatures());
				}
			}
		}
		
		// add tokens to dictionary
		this.dictionary.addDictionary(tokens);
	}
	
	/**
	 * Adds category label mapping to the dictionary
	 */
	private void addCategoryLabelMapping() {
		
		for (String category : this.corpusProcessor.getCategories())
			this.dictionary.addCategoryLabel(category);
	}
	
	/**
	 * Write parsed corpus in train, dev and test files
	 * @throws IOException
	 */
	private void writeParsedCorpus() throws IOException {
		
		this.message.displayTextln("Writing parsed files.......");
		
		// write train corpus
		this.message.displayTextln("Writing train corpus.......");
		
		File unannotatedFile = new File(Utils.addTrainExtension(this.name));
		
		// create <category,categoryTrainWriter> mapping
		Map<String,File> fileMap = this.createFileMap();
		
		// write train files
		SVMWriter.writeTrainFiles(this.trainEntityMap, this.seedList,
				this.window, this.balanced, this.dictionary, fileMap,
				unannotatedFile, this.tfidf, this.message);
		
		// show messages
		this.message.displayTextln("Unannoted file '"+
				unannotatedFile.getAbsolutePath()+"' written!");
		for (String category : fileMap.keySet()) {
			File train = fileMap.get(category);
			this.message.displayTextln("Train file '"+train.getAbsolutePath()+"' written!");
		}
		this.message.displayDivider();
		
		// write dev corpus
		if (this.devEntityMap.size() > 0) {
			this.message.displayTextln("Writing dev corpus.......");
			
			// write dev files
			File devFile = new File(Utils.addDevExtension(this.name));
				
			// write dev file
			SVMWriter.writeTestFile(this.devEntityMap,
					this.window, this.dictionary, devFile, this.tfidf, this.message);
				
			this.message.displayTextln("Dev file '"+
					devFile.getAbsolutePath()+"' written!");
			this.message.displayDivider();
		}
		
		// write test corpus
		if (this.testEntityMap.size() > 0) {
			this.message.displayTextln("Writing test corpus.......");
			
			// write dev file
			File testFile = new File(Utils.addTestExtension(this.name));
			
			// write test file
			SVMWriter.writeTestFile(this.testEntityMap,
					this.window, this.dictionary, testFile, this.tfidf, this.message);
			
			this.message.displayTextln("Test file '"+testFile.getAbsolutePath()+"' written!");
			this.message.displayDivider();
		}
	}

	/**
	 * Creates a Map<category,File> where each file will be used
	 * to train a SVM.
	 * @return
	 * @throws IOException
	 */
	private Map<String,File> createFileMap() throws IOException {
		
		Map<String,File> fileMap = new HashMap<String,File>();
		
		//TODO
		for (String category : this.seedList.keySet()) {
			
			File train = new File(Utils.addTrainExtension(this.name+"."+category));
			fileMap.put(category, train);
		}

//		for (String category : this.trainEntityMap.keySet()) {
//			
//			File train = new File(Utils.addTrainExtension(this.name+"."+category));
//			fileMap.put(category, train);
//		}
		
		return fileMap;
	}
	
	/**
	 * Adds PoS tags to dictionary if needed
	 */
	private void addPoS2Dictionary() {
		
		if (this.nlpTool.equals(NLPtools.Type.POSTAGGER) 
				|| this.nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)
				|| this.nlpTool.equals(NLPtools.Type.POSTAGGER_CHUNKER)
				|| this.nlpTool.equals(NLPtools.Type.ALL)) {
			this.dictionary.addDictionary("V");
			this.dictionary.addDictionary("N");
			this.dictionary.addDictionary("R");
			this.dictionary.addDictionary("G");
			this.dictionary.addDictionary("A");
			this.dictionary.addDictionary("D");
			this.dictionary.addDictionary("P");
			this.dictionary.addDictionary("Q");
			this.dictionary.addDictionary("C");
			this.dictionary.addDictionary("O");
		}
	}

}
