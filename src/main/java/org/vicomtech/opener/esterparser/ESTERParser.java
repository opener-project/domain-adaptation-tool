package org.vicomtech.opener.esterparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import opennlp.tools.util.InvalidFormatException;

import org.vicomtech.opener.bootstrapping.IParser;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.entities.Feature;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.Chunker;
import org.vicomtech.opener.nlp.ConstituentParser;
import org.vicomtech.opener.nlp.Lemmatizer;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.nlp.PoSTagger;
import org.vicomtech.opener.nlp.SentenceDetector;
import org.vicomtech.opener.nlp.TagsetMappings;
import org.vicomtech.opener.nlp.Tokenizer;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements the corpus parsing interface to parse
 * the information of the ESTER corpus
 * (http://catalog.elra.info/product_info.php?products_id=999)
 * 
 * org.vicomtech.opener.esterparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class ESTERParser implements IParser {
	
	/**
	 * Token window size
	 */
	private int window;
	private int modSize;
	
	/**
	 * Entity categories and the ESTER categories
	 */
	private EntityCategories categories;
	
	/**
	 * Utilities for ESTER corpus XML tags
	 */
	private TagUtils tagUtils;
	
	/**
	 * The message handler
	 */
	private MessageHandler message;
	
	/**
	 * The amount of nodes that are extracted at the left and right
	 * side of a entity
	 */
	private final static int WINDOW_SIZE = 20;
	
	/**
	 * Analyzed last file's tokens
	 */
	private String[] tokens = null;
	
	private NLPtools.Type nlpTool;
	private Tokenizer tokenizer;
	private SentenceDetector sentenceDetector;
	private PoSTagger posTagger;
	private Lemmatizer lemmatizer;
	private Chunker chunker;
	private ConstituentParser constituentParser;
	
	/**
	 * Variable to identify entity index within tokenized text
	 */
	private int start = -1;
	private int end = -1;
	
	/**
	 * Public constructor
	 */
	public ESTERParser() {
	}
	
	/**
	 * Adds parameters to the ESTER parser
	 * @param language : corpus language
	 * @param window : features window
	 * @param modSize : NP modifier sum for each head noun
	 * @param tokenizer : tokenizer
	 * @param posTagger : PoS tagger (can be null if not needed)
	 * @param lemmatizer : lemmatizer (can be null if not needed)
	 * @param nlpTool : nlp tools used to extract features
	 * @param message : message handler
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
/*	public ESTERParser(EntityCategories categories,
			int window, int modSize, Tokenizer tokenizer, 
			SentenceDetector sentenceDetector, 
			PoSTagger posTagger, Lemmatizer lemmatizer,
			Chunker chunker,
			ConstituentParser constituentParser,
			MessageHandler message) {
		
		this.categories = categories;
		this.tagUtils = new TagUtils(categories);
		this.window = window;
		this.modSize = modSize;
		this.tokenizer = tokenizer;
		this.sentenceDetector = sentenceDetector;
		this.posTagger = posTagger;
		this.lemmatizer = lemmatizer;
		this.chunker = chunker;
		this.constituentParser = constituentParser;
		this.message = message;
	}
	*/
	@Override
	public void addParameters(Set<String> categories, Language language, int window, int modSize,
			NLPtools.Type nlpTool, MessageHandler message) throws InvalidFormatException, IOException {
		
		this.window = window;
		this.modSize = modSize;
		this.nlpTool = nlpTool;
		
		// read tokenizer model
		this.tokenizer = new Tokenizer(language);
		
		if (!nlpTool.equals(NLPtools.Type.TOKENIZER))
			this.sentenceDetector = new SentenceDetector(language);
		if (nlpTool.equals(NLPtools.Type.POSTAGGER)) {
			this.posTagger = new PoSTagger(language);
		} else if (nlpTool.equals(NLPtools.Type.LEMMATIZER)) {
			this.posTagger = new PoSTagger(language);
			this.lemmatizer = new Lemmatizer(language);
		} else if (nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)) {
			this.posTagger = new PoSTagger(language);
			this.lemmatizer = new Lemmatizer(language);
			this.window = window*2; // each token has 2 features
		} else if (nlpTool.equals(NLPtools.Type.CHUNKER)) {
			this.chunker = new Chunker(language, modSize);
		} else if (nlpTool.equals(NLPtools.Type.CONSTITUENT_PARSER)) {
			this.constituentParser = new ConstituentParser(language, modSize);
		}
		
		this.categories = new EntityCategories();
		this.tagUtils = new TagUtils(this.categories);
		
		this.message = message;
	}
	
	/**
	 * Get the entity categories
	 * @return
	 */
	@Override
	public Set<String> getCategories() {
		return this.categories.getCategories();
	}
	
	/**
	 * Get all the tokens of the ESTER corpus file
	 * @param file : ESTER corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public String[] getTokens(File file) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		
		Element rootElement = this.tagUtils.getRootElement(file);
		
		// vars with file text
		String text = new String();
		String textTok = new String();
		
		// iterate turns
		NodeList turns = rootElement.getElementsByTagName("Turn");
		for (int i=0; i<turns.getLength();i++) {
			Node turn = turns.item(i);
			
			// iterate turn childs
			NodeList turnChilds = turn.getChildNodes();
			for (int j=0; j<turnChilds.getLength();j++) {
				Node turnChild = turnChilds.item(j);
				
				if (turnChild.getNodeType() == Node.TEXT_NODE
						&& turnChild.getNodeValue().trim().length() > 0) {
					// is text node
					text += this.tagUtils.getText(turnChild)+" ";
				}
			}
			text += "\n";
			// end of turn
		}
		
		// tokenize text
		textTok = this.tokenizer.tokenize(text);
		// 'join' all the paragraphs
		textTok = textTok.replace("\n", " ").replaceAll(" +", " ").trim();

		this.tokens = textTok.split(" ");
		
		return this.tokens;
	
	}
	
	/**
	 * Get all the lemmas of the ESTER corpus file
	 * @param file : ESTER corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public String[] getLemmas(File file) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		
		// if file has not been tokenized before, tokenize
		if (this.tokens == null)
			this.getTokens(file);
		
		// get pos
		String[] pos = this.posTagger.postag(this.tokens);
		// get lemmas
		String[] lemmas = new String[this.tokens.length];
		for (int i=0; i<this.tokens.length; i++) {
			lemmas[i] = this.lemmatizer.getLemma(this.tokens[i], pos[i]);
		}
		
		// empty tokenization for next files
		this.tokens = null;
		
		return lemmas;
	
	}
	
	/**
	 * Get all the entities and its features of the ESTER corpus file
	 * @param file : input ESTER file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws InterruptedException
	 */
	@Override
	public Map<String,List<Entity>> getEntities(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, InterruptedException {
		
		// entity map, <Category,<List of Entities>
		Map<String,List<Entity>> entityMap = new HashMap<String,List<Entity>>();
		
		Element rootElement = this.tagUtils.getRootElement(file);
		
		// for each category
		for (String category : this.categories.getCategories()) {
			
			List<Entity> entityList = new ArrayList<Entity>();
			
			// get entity start tags
			NodeList entityStartNodes = this.tagUtils.getEntityNodes(
					category, TagUtils.OPEN_VAL, rootElement);
			
			// for each entity start tag...
			for (int i=0; i<entityStartNodes.getLength();i++) {
				
				// get entity
				Entity entity = this.getEntity(
						entityStartNodes.item(i), category);
				
				// add entity to the list
				entityList.add(entity);
				
				this.message.displayText(".");
			}
			// add entities to the map
			entityMap.put(category, entityList);
		}
		return entityMap;
	}
	
	/**
	 * Get a Entity and its features from a Entity starting tag node
	 * @param entityStartNode : entity starting tag node
	 * @param category : Entity category
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private Entity getEntity(Node entityStartNode, String category) throws IOException, InterruptedException {
		
		Element entityStart = (Element) entityStartNode;
		
		// get subcategory: desc="org.com"
		String subcategory = entityStart.getAttribute(TagUtils.CATEGORY_ATTR);
		
		// while not entity close tag...
		String entity = new String();
		Node nextNode = entityStart;
		while ((nextNode = nextNode.getNextSibling()) != null) {

			// if is text node, get text;
			// else if is entity close node, break
			if (nextNode.getNodeType() == Node.TEXT_NODE
					&& nextNode.getNodeValue().trim().length() > 0) {
				
				entity += this.tagUtils.getText(nextNode)+" ";
			}
			else if (this.tagUtils.isEntityCloseNode(nextNode, subcategory)) {
				break;
			}
		}
		
		// get left text
		String leftText = this.getLeftText(entityStart);
		// get right text
		String rightText = this.getRightText(nextNode);
		
		// tokenize full text
		boolean b = this.tokenizer.tokenize(entity, leftText, rightText);
		if (b) {
			
			entity = this.tokenizer.getEntity();
			leftText = this.tokenizer.getLeftText();
			rightText = this.tokenizer.getRightText();
			String leftTok = this.tokenizer.getLeftToken();
			String rightTok = this.tokenizer.getRightToken();
			String tokenized = this.tokenizer.getTokenized();
			
			//if (this.sentenceDetector == null) {
			if (this.nlpTool.equals(NLPtools.Type.TOKENIZER)) {
				return this.getEntityNoParsing(entity, category, leftText, rightText);
			}
			//else if (this.chunker != null || this.constituentParser != null) {
			else if (this.nlpTool.equals(NLPtools.Type.CHUNKER)
					|| this.nlpTool.equals(NLPtools.Type.CONSTITUENT_PARSER)) {
				return this.getEntityParsing(entity, category, leftText,
						rightText, leftTok, rightTok, tokenized);
			}
			else {
				return this.getEntityPoSTagging(entity, category, leftText,
						rightText, leftTok, rightTok, tokenized);
			}
		}
		else
			throw new IOException("match with entity '"+entity
				+"' at text '"+leftText+entity+rightText+"' failed");
	}
	
	private Entity getEntityParsing(String entity, String category, 
			String leftText, String rightText, String leftTok,
			String rightTok, String tokenized) {
		
		// detect sentences and get left and right tokens
		String[] sentences = this.sentenceDetector
				.detect(tokenized);
		this.sentenceDetector.getSentenceAndLeftRightTokens(sentences, entity, leftTok, rightTok);
		tokenized = this.sentenceDetector.getSentence();
		leftTok = this.sentenceDetector.getLeftToken();
		rightTok = this.sentenceDetector.getRightToken();
		
		//System.out.println("\nENTITY: "+entity);
		//System.out.println("LEFT: "+leftTok);
		//System.out.println("RIGHT: "+rightTok);
		//System.out.println("TOKENIZED: "+tokenized);
		
		// parse sentence
		List<Feature> features = null;
		if (this.nlpTool.equals(NLPtools.Type.CONSTITUENT_PARSER))
			features = this.constituentParser.parse(tokenized, false);
		else if (this.nlpTool.equals(NLPtools.Type.CHUNKER))
			features = this.chunker.chunk(tokenized, false);
		
		Entity s = Utils.createEntity(entity, category, 
				features, leftTok, rightTok, window, modSize, 0);
		//	// get arrays
		//	List<String> leftList = Utils.getLeftList(features, entity, this.window);
		//	List<String> rightList = Utils.getRightList(features, entity, this.window);
							
		//	Entity s = new Entity(entity, category, null, null);
		return s;
	}
	
	private Entity getEntityPoSTagging(String entity, String category, 
			String leftText, String rightText, String leftTok,
			String rightTok, String tokenized) throws IOException {
		
		// detect sentences and get left and right tokens
		String[] sentences = this.sentenceDetector.detect(tokenized);
		this.sentenceDetector.getSentenceAndLeftRightTokens(sentences, entity, leftTok, rightTok);
		tokenized = this.sentenceDetector.getSentence();
		leftTok = this.sentenceDetector.getLeftToken();
		rightTok = this.sentenceDetector.getRightToken();
		
		// get tokens and PoS
		String[] tokens = tokenized.split(" ");
		String[] pos = this.posTagger.postag(tokens);
		
		// get entity index
		this.getEntityIndexes(entity, tokenized, tokens, leftTok, rightTok);
		
		// initialize lists
		List<String> leftFeatures = new ArrayList<String>();
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new ArrayList<String>();
		
		// get lemmas or PoS tags
		if (this.start > -1)
			for (int i=0; i<this.start; i++) {
				//if (this.lemmatizer == null)
				if (this.nlpTool.equals(NLPtools.Type.LEMMATIZER)
						|| this.nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)) {
					String lemma = this.lemmatizer.getLemma(tokens[i], pos[i]);
					leftFeatures.add(lemma);
				}
				if (this.nlpTool.equals(NLPtools.Type.POSTAGGER)
						|| this.nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)) {
					leftFeatures.add(TagsetMappings.convertFromFtbToKaf(pos[i]));
				}
			}
		else throw new IOException("entity '"+entity+"' not found at sentence '"+tokenized+"'**"+leftTok+"**");
		
		if (this.end > -1)
			for (int i=this.end+1; i<tokens.length; i++) {
				//if (this.lemmatizer == null)
				if (this.nlpTool.equals(NLPtools.Type.LEMMATIZER)
						|| this.nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)) {
					String lemma = this.lemmatizer.getLemma(tokens[i], pos[i]);
					rightFeatures.add(lemma);
				}
				if (this.nlpTool.equals(NLPtools.Type.POSTAGGER)
						|| this.nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)) {
					rightFeatures.add(TagsetMappings.convertFromFtbToKaf(pos[i]));
				}
			}
		else throw new IOException("entity '"+entity+"' not found at sentence '"+tokenized+"'"+"**"+rightTok+"**");
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
	/*	
		System.out.println("\nENTITY: "+entity);
		System.out.println("Sentence: "+tokenized);
		System.out.println("Left: "+StringUtils.join(leftFeatures," "));
		System.out.println("Right: "+StringUtils.join(rightFeatures," "));
	*/	
		entity = entity.replace("_", " ");
		
		Entity e = new Entity(entity, category, leftFeatures,middleFeatures, rightFeatures);
		
		return e;
	}
	
	/**
	 * Find entity starting and ending indexes within the tokenized array
	 * @param entity : the input entity
	 * @param tokenized : input tokenized string (only for error catching message)
	 * @param tokens : input tokenized array
	 * @param leftTok : entity left token
	 * @param rightTok : entity right token
	 * @throws IOException
	 */
	private void getEntityIndexes(String entity, String tokenized,
			String[] tokens, String leftTok, String rightTok) throws IOException {
		
		this.start = -1;
		this.end = -1;
		
		int entityIndex = 0;
		String[] entityArray = entity.trim().split(" ");
		
		for (int i=0; i<tokens.length; i++) {
			String token = tokens[i];
			// if start index not found
			if (this.start < 0) {
				if (token.equals(entityArray[entityIndex])) {
					// check left token
					if (leftTok.length() > 0 && i > 0) {
						try {
							if (leftTok.contentEquals(tokens[i-1])) {
								this.start = i;
								entityIndex++;
							}
						}
						catch(ArrayIndexOutOfBoundsException ex){
							System.out.println("ENTITY: "+entity);
							System.out.println("SENTENCE: "+tokenized);
							System.out.println("LEFTTOK: "+leftTok);
							System.out.println("RIGHTTOK: "+rightTok);
							System.out.println("i: "+i);
							throw new ArrayIndexOutOfBoundsException();
						}
					}
					else if (leftTok.length() == 0) {
						this.start = i;
						entityIndex++;
					}
				}
			}
			// find end index
			else if (entityIndex < entityArray.length) {
				if (token.equals(entityArray[entityIndex]))
					entityIndex++;
				else throw new IOException("Entity missmatch: "+entityArray[entityIndex]+" != "+token);
			}
			// check right token
			else {
				if (rightTok.length() > 0) {
					if (rightTok.contentEquals(tokens[i])) {
						this.end = i-1;
						break;
					}
					// right token incorrect, initialize start index
					else {
						this.start = -1;
						entityIndex = 0;
					}
				}
				else {
					this.end = i-1;
					break;
				}
			}
		}
		if (this.end < 0 && rightTok.length() == 0)
			this.end = tokens.length-1;
	}
	
	private Entity getEntityNoParsing(String entity, String category,
			String leftText, String rightText) {
		
		List<String> leftFeatures = new LinkedList<String>(Arrays.asList(leftText.split(" ")));
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new LinkedList<String>(Arrays.asList(rightText.split(" ")));

		//Utils.removePunctuation(leftFeatures);
		//Utils.removePunctuation(middleFeatures);
		//Utils.removePunctuation(rightFeatures);
		//Utils.removeBeforePoint(leftFeatures);
		//Utils.removeAfterPoint(rightFeatures);
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		
		entity = entity.replace("_", " ");
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		return e;
	}
	
	/**
	 * Get left test giving an Entity start tag node
	 * @param node : Entity start tag node
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String getLeftText(Node node) throws IOException, InterruptedException {
		
		int i=0;
		String leftText = new String();
		
		// get left nodes
		Node prevNode = node;
		while ((prevNode = prevNode.getPreviousSibling()) != null && i < WINDOW_SIZE) {
			
			// get text
			if (prevNode.getNodeType() == Node.TEXT_NODE
					&& prevNode.getNodeValue().trim().length() > 0) {
				
				leftText = this.tagUtils.getText(prevNode)+" "+leftText;
			}
			i++;
		}
		
		return leftText;
		
	}
	
	/**
	 * Get right test giving an Entity end tag node
	 * @param node : Entity end tag node
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String getRightText(Node node) throws IOException, InterruptedException {
		
		int i=0;
		String rightText = " ";
		
		// get right nodes
		Node nextNode = node;
		while ((nextNode = nextNode.getNextSibling()) != null && i < WINDOW_SIZE) {
			
			// get text
			if (nextNode.getNodeType() == Node.TEXT_NODE
					&& nextNode.getNodeValue().trim().length() > 0) {
				
				rightText += this.tagUtils.getText(nextNode)+" ";
			}
			i++;
		}
		
		return rightText;
		
	}

}
