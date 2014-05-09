package org.vicomtech.opener.ancoraparser;

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

import opennlp.tools.util.InvalidFormatException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.vicomtech.opener.bootstrapping.IParser;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.entities.Feature;
import org.vicomtech.opener.entities.FeatureGenerator;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools.Type;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.nlp.NPDetector;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements the corpus parsing interface to parse
 * the information of the AnCora corpus (http://clic.ub.edu/corpus/ancora)
 * 
 * org.vicomtech.opener.ancoraparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class AnCoraParser implements IParser {

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
	 * Utilities for AnCora corpus XML tags
	 */
	private TagUtils tagUtils;
	
	/**
	 * The message handler
	 */
	private MessageHandler message;
	
	private FeatureGenerator f_generator;
	private NLPtools.Type nlpTool;
	
	private Language language;
	
	/**
	 * Public constructor
	 */
	public AnCoraParser() {
	}

	/**
	 * Adds parameters to the AnCora parser
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
	@Override
	public void addParameters(Set<String> categories, Language language, int window, int modSize,
			Type nlpTool, MessageHandler message) throws InvalidFormatException, IOException, ParseException, InterruptedException, ExecException {
		
		this.window = window;
		this.modSize = modSize;
		this.nlpTool = nlpTool;
		this.language = language;
		
		if (nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER))
			this.window = window+2; // each token has 2 features
		else if (nlpTool.equals(NLPtools.Type.ALL))
			this.f_generator = new FeatureGenerator(this.window, this.language, message);
		else if (nlpTool.equals(NLPtools.Type.ALL_NO_CHUNK))
			this.f_generator = new FeatureGenerator(this.window, this.language, message);
		
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
	 * Get all the tokens of the AnCora corpus file
	 * @param file : AnCora corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public String[] getTokens(File file) throws ParserConfigurationException,
			SAXException, IOException, InterruptedException {
		
		String[] text = this.getAttributes(file, TagUtils.Attribute.WORD);
		return text;
	}
	
	/**
	 * Get all the lemmas of the AnCora corpus file
	 * @param file : AnCora corpus file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public String[] getLemmas(File file) throws ParserConfigurationException,
			SAXException, IOException, InterruptedException {
		
		String[] lemmas = this.getAttributes(file, TagUtils.Attribute.LEMMA);
		return lemmas;
	}
	
	/**
	 * Get all the attribute variables of the AnCora corpus file
	 * @param file : AnCora corpus file
     * @param attribute : attribute value that will be extracted
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private String[] getAttributes(File file, TagUtils.Attribute attribute) throws ParserConfigurationException, SAXException, IOException {
		
		Element rootElement = TagUtils.getRootElement(file);
		
		String attributes = new String();
		// iterate sentences
		NodeList sentences = rootElement.getElementsByTagName("sentence");
		for (int i=0; i<sentences.getLength();i++) {
			Node sentence = sentences.item(i);
			attributes += TagUtils.getAttributes(sentence, attribute)+"\n";
		}
		
		// 'join' all the paragraphs
		attributes = attributes.replace("\n", " ").replaceAll(" +", " ").trim();
		//System.out.println(text);
		return attributes.split(" ");
	}
	
	/**
	 * Get all the entities and its features of the AnCora corpus file
	 * @param file : input AnCora file
	 * @return
	 * @throws Exception 
	 */
	@Override
	public Map<String, List<Entity>> getEntities(File file)
			throws Exception {
		
		// entity map, <Category,<List of Entities>
		Map<String,List<Entity>> entityMap = new HashMap<String,List<Entity>>();
				
		Element rootElement = TagUtils.getRootElement(file);
		
		// for each category
		for (String category : this.categories.getCategories()) {
					
			List<Entity> entityList = new ArrayList<Entity>();
					
			// get entity start tags
			NodeList entityNodes = this.tagUtils.getEntityNodes(
					category, rootElement);
			
			// for each entity start tag...
			for (int i=0; i<entityNodes.getLength();i++) {
				
				// get entity
				Entity entity = this.getEntity(
						entityNodes.item(i), category);
				
				if (entity != null) {
					// add entity to the list
					if (!Entity.contains(entityList, entity))
						entityList.add(entity);
				}
				
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
	 * @throws Exception 
	 */
	private Entity getEntity(Node entityNode, String category) throws Exception {
		
		if (this.nlpTool.equals(NLPtools.Type.TOKENIZER)) {
			return this.getEntityTokenizer(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.POSTAGGER)) {
			return this.getEntityPoS(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.LEMMATIZER)) {
			return this.getEntityLemmatizer(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER)) {
			return this.getEntityLemmatizerPoS(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.CHUNKER)) {
			return this.getEntityChunker(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.LEMMATIZER_CHUNKER)) {
			return this.getEntityLemmatizerChunker(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.POSTAGGER_CHUNKER)) {
			return this.getEntityPostaggerChunker(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.ALL)) {
			return this.getEntityAll(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.ALL_NO_CHUNK)) {
			return this.getEntityAllNoChunk(entityNode, category);
		}
		else if (this.nlpTool.equals(NLPtools.Type.CONCAT_POS)) {
			return this.getEntityConcatPoS(entityNode, category);
		}
		
		return null;
	}
	
	private Entity getEntityTokenizer(Node entityNode, String category) throws IOException, InterruptedException {
		
		String entity = TagUtils.getAttributes(entityNode, TagUtils.Attribute.WORD);
		// get left text
		String leftText = this.getLeftAttributes(entityNode, TagUtils.Attribute.WORD);
		// get right text
		String rightText = this.getRightAttributes(entityNode, TagUtils.Attribute.WORD);
	/*	
		System.out.println("entity: "+entity);
		System.out.println("left: "+leftText);
		System.out.println("right: "+rightText);
	*/	
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
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		entity = entity.replace("_", " ");
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		return e;
	}
	
	private Entity getEntityPoS(Node entityNode, String category) throws IOException, InterruptedException {
		
		String entity = TagUtils.getAttributes(entityNode, TagUtils.Attribute.WORD);
		// get left pos
		String leftPoS = this.getLeftAttributes(entityNode, TagUtils.Attribute.POS);
		// get right pos
		String rightPoS = this.getRightAttributes(entityNode, TagUtils.Attribute.POS);
	/*	
		System.out.println("entity: "+entity);
		System.out.println("left: "+leftPoS);
		System.out.println("right: "+rightPoS);
	*/	
		List<String> leftFeatures = new LinkedList<String>(Arrays.asList(leftPoS.split(" ")));
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new LinkedList<String>(Arrays.asList(rightPoS.split(" ")));
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		entity = entity.replace("_", " ");
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		return e;
	}
	
	private Entity getEntityLemmatizer(Node entityNode, String category) throws IOException, InterruptedException {
		
		String entity = TagUtils.getAttributes(entityNode, TagUtils.Attribute.WORD);
		// get left lemmas
		String leftLemmas = this.getLeftAttributes(entityNode, TagUtils.Attribute.LEMMA);
		// get right lemma
		String rightLemmas = this.getRightAttributes(entityNode, TagUtils.Attribute.LEMMA);
	/*	
		System.out.println("entity: "+entity);
		System.out.println("left: "+leftPoS);
		System.out.println("right: "+rightPoS);
	*/	
		List<String> leftFeatures = new LinkedList<String>(Arrays.asList(leftLemmas.split(" ")));
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new LinkedList<String>(Arrays.asList(rightLemmas.split(" ")));
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		entity = entity.replace("_", " ");
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		return e;
	}
	
	private Entity getEntityLemmatizerPoS(Node entityNode, String category) throws IOException, InterruptedException {
		
		String entity = TagUtils.getAttributes(entityNode, TagUtils.Attribute.WORD);
		// get left pos
		String[] leftPoS = this.getLeftAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
		// get right pos
		String[] rightPoS = this.getRightAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
		// get left lemmas
		String[] leftLemmas = this.getLeftAttributes(entityNode, TagUtils.Attribute.LEMMA).split(" ");
		// get right lemma
		String[] rightLemmas = this.getRightAttributes(entityNode, TagUtils.Attribute.LEMMA).split(" ");
		
		List<String> leftFeatures = new ArrayList<String>();
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new ArrayList<String>();
		
		for (int i=0; i<leftPoS.length; i++) {
			//leftFeatures.add(leftPoS[i]);
			leftFeatures.add(leftLemmas[i]);
		}
		leftFeatures.add(leftPoS[leftPoS.length-1]);
		rightFeatures.add(rightPoS[0]);
		for (int i=0; i<rightPoS.length; i++) {
			//rightFeatures.add(rightPoS[i]);
			rightFeatures.add(rightLemmas[i]);
		}
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		entity = entity.replace("_", " ");
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		return e;
	}
	
	private Entity getEntityChunker(Node entityNode, String category) throws IOException, InterruptedException {
		
		String entity = TagUtils.getAttributes(entityNode, TagUtils.Attribute.WORD);
		
		if (!this.isValidEntity(entity)) {
			return null;
		}
		
		String[] leftText = this.getLeftAttributes(entityNode, TagUtils.Attribute.WORD).split(" ");
		String[] rightText = this.getRightAttributes(entityNode, TagUtils.Attribute.WORD).split(" ");
		String leftTok = leftText[leftText.length-1];
		String rightTok = rightText[0];
		
		// fix for wrong entities
		if (entity.startsWith(",")) {
			entity = entity.substring(1, entity.length()).trim();
			leftTok = ",";
		}
		if (entity.endsWith(",") && entity.indexOf(",") == entity.length()-1) {
			entity = entity.substring(0, entity.length()-1).trim();
			rightTok = ",";
		}
		
		String[] text = this.getSentenceAttributes(entityNode, TagUtils.Attribute.WORD);
		String[] pos = this.getSentenceAttributes(entityNode, TagUtils.Attribute.POS);
	/*	
		System.out.println("Entity: "+entity);
		System.out.println("Text: "+StringUtils.join(text, " "));
		System.out.println("Pos: "+StringUtils.join(pos, " "));
		System.out.println("Prev: "+leftTok);
		System.out.println("Next: "+rightTok);
		System.out.println();
		*/
		NPDetector chunker = new NPDetector(this.language, this.modSize);
		List<Feature> features = chunker.chunk(text, pos);
	/*	for (Feature f : features) {
			System.out.println(f.toString());
			System.out.println();
		}*/
		Entity e = Utils.createEntity(entity, category,
				features, leftTok, rightTok, this.window, this.modSize, 1);
	
		/*System.out.println(e.getLeftFeatures());
		System.out.println(e.getMiddleFeatures());
		System.out.println(e.getRightFeatures());*/
		
		return e;
	}
	
	private Entity getEntityLemmatizerChunker(Node entityNode, String category) throws IOException, InterruptedException {
		
		String entityWord = TagUtils.getAttributes(entityNode, TagUtils.Attribute.LEMMA);
		String entityLemma = TagUtils.getAttributes(entityNode, TagUtils.Attribute.LEMMA);
		
		if (!this.isValidEntity(entityWord)) {
			return null;
		}
		
		String[] leftText = this.getLeftAttributes(entityNode, TagUtils.Attribute.LEMMA).split(" ");
		String[] rightText = this.getRightAttributes(entityNode, TagUtils.Attribute.LEMMA).split(" ");
		String leftTok = leftText[leftText.length-1];
		String rightTok = rightText[0];
		
		// fix for wrong entities
		if (entityWord.startsWith(",")) {
			entityWord = entityWord.substring(1, entityWord.length()).trim();
			entityLemma = entityLemma.substring(1, entityLemma.length()).trim();
			leftTok = ",";
		}
		if (entityWord.endsWith(",") && entityWord.indexOf(",") == entityWord.length()-1) {
			entityWord = entityWord.substring(0, entityWord.length()-1).trim();
			entityLemma = entityLemma.substring(0, entityLemma.length()-1).trim();
			rightTok = ",";
		}
		
		String[] lemmas = this.getSentenceAttributes(entityNode, TagUtils.Attribute.LEMMA);
		String[] pos = this.getSentenceAttributes(entityNode, TagUtils.Attribute.POS);
		
		NPDetector chunker = new NPDetector(this.language, this.modSize);
		List<Feature> features = chunker.chunk(lemmas, pos);
		
		Entity e = Utils.createEntity(entityWord, entityLemma, category,
				features, leftTok, rightTok, this.window, this.modSize, 1);
		
		return e;
	}
	
	private Entity getEntityPostaggerChunker(Node entityNode, String category) throws IOException, InterruptedException {
		
		Entity e = this.getEntityChunker(entityNode, category);
	
		if (e != null) {
			String[] leftPos = this.getLeftAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
			String[] rightPos = this.getRightAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
			String leftP = leftPos[leftPos.length-1];
			String rightP = rightPos[0];
			
			e.addRightFeature(leftP);
			e.addRightFeature(rightP);
		}
		
		return e;
	}
	
	private Entity getEntityAll(Node entityNode, String category) throws Exception {
		Entity e = this.getEntityChunker(entityNode, category);
		return getAllFeatures(e, entityNode);
	}
	
	private Entity getEntityAllNoChunk(Node entityNode, String category) throws Exception {
		Entity e = this.getEntityTokenizer(entityNode, category);
		return getAllFeatures(e, entityNode);
	}
	
	private Entity getEntityConcatPoS(Node entityNode, String category) throws IOException, InterruptedException {
		
		String entity = TagUtils.getAttributes(entityNode, TagUtils.Attribute.WORD);
		// get left pos
		String[] leftPoS = this.getLeftAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
		// get right pos
		String[] rightPoS = this.getRightAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
		
		List<String> leftFeatures = new ArrayList<String>();
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new ArrayList<String>();
		
		for (int i=leftPoS.length-1; i>=0; i--) {
			String pos1 = leftPoS[i];
			String pos2 = new String();
			i--;
			if (i > -1) {
				pos2 = leftPoS[i];
				leftFeatures.add(0, pos2+"_"+pos1);
			}
			else leftFeatures.add(0, pos1);
		}
		
		for (int i=0; i<rightPoS.length; i++) {
			String pos1 = rightPoS[i];
			String pos2 = new String();
			i++;
			if (i < rightPoS.length) {
				pos2 = rightPoS[i];
				rightFeatures.add(pos1+"_"+pos2);
			}
			else rightFeatures.add(pos1);
		}
		middleFeatures.add(entity);
	/*	// get left text
		String leftText = this.getLeftAttributes(entityNode, TagUtils.Attribute.WORD);
		// get right text
		String rightText = this.getRightAttributes(entityNode, TagUtils.Attribute.WORD);
				
		System.out.println("entity: "+entity);
		System.out.println("left: "+leftText);
		System.out.println("right: "+rightText);
		System.out.println("left pos: "+StringUtils.join(leftPoS, " "));
		System.out.println("right pos: "+StringUtils.join(rightPoS, " "));
		
		System.out.println("left pos: "+leftFeatures);
		System.out.println("right pos: "+rightFeatures);
		*/
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		
		entity = entity.replace("_", " ");
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		return e;
	}
	
	private Entity getAllFeatures(Entity entity, Node entityNode) throws Exception {
		if (entity != null) {
			this.f_generator.setEntity(entity);
			
			// words
			String[] leftWords = this.getLeftAttributes(entityNode, TagUtils.Attribute.WORD).split(" ");
			String[] rightWords = this.getRightAttributes(entityNode, TagUtils.Attribute.WORD).split(" ");
			
			// lemmas
			String[] leftLemmas = this.getLeftAttributes(entityNode, TagUtils.Attribute.LEMMA).split(" ");
			String[] rightLemmas = this.getRightAttributes(entityNode, TagUtils.Attribute.LEMMA).split(" ");
			
			this.f_generator.addLemmas(leftLemmas, rightLemmas);
			
			// part of speech
			String[] leftPos = this.getLeftAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
			String[] rightPos = this.getRightAttributes(entityNode, TagUtils.Attribute.POS).split(" ");
			
			this.f_generator.addPoS(leftPos, rightPos);
			
//			// add first word feature
//			f_generator.addFirstWord(leftLemmas[leftLemmas.length-1]);
//			
//			// add length feature
//			f_generator.addLength();
			
			// add previous prefix feature
			this.f_generator.addPrefix(leftWords[leftWords.length-1]);
			
			// add prefix feature
			this.f_generator.addPrefix();
			
			// add next prefix feature
			this.f_generator.addPrefix(rightWords[0]);
			
			// add previous suffix feature
			this.f_generator.addSuffix(leftWords[leftWords.length-1]);
			
			// add next suffix feature
			this.f_generator.addSuffix();
			
			// add suffix feature
			this.f_generator.addSuffix(rightWords[0]);
			
			// add gazetteer feature
			this.f_generator.addGazetteer(leftWords, rightWords);
			
			entity = this.f_generator.getEntity();
		}
		
		return entity;
	}
	
	/**
	 * Get left attributes giving an Entity start tag node
	 * @param node : entity tag node
	 * @param attribute : attribute value that will be extracted
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String getLeftAttributes(Node node, TagUtils.Attribute attribute) throws IOException, InterruptedException {
		
		String leftText = new String();
		
		// get top parent node and then, we will get text within the parent until
		// we reach the entity node
		Node prevNode = node;
		Node lastNode = node;
		while ((prevNode = TagUtils.getPrevNode(prevNode)) != null) {
			lastNode = prevNode;
		}

		// if top parent node equals node, it is because there is no parent
		if (lastNode.equals(node))
			return leftText;
		else {
			leftText = TagUtils.getAttributesUntil(lastNode, node, attribute);
		}
		
		return leftText.trim();
	}
	
	/**
	 * Get right attributes giving an Entity end tag node
	 * @param node : entity tag node
	 * @param attribute : attribute value that will be extracted
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String getRightAttributes(Node node, TagUtils.Attribute attribute) throws IOException, InterruptedException {
		
		String rightText = " ";
		
		// get right nodes
		Node nextNode = node;
		while ((nextNode = TagUtils.getNextNode(nextNode)) != null) {
			if (nextNode.getNodeType() == Node.ELEMENT_NODE) {
				// get text
				rightText += TagUtils.getAttributes(nextNode, attribute)+" ";
			}
		}
		return rightText.trim();
	}
	
	/**
	 * Get attributes giving an entity tag node
	 * @param node : entity tag node
	 * @param attribute : attribute value that will be extracted
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String[] getSentenceAttributes(Node node, TagUtils.Attribute attribute) throws IOException, InterruptedException {
		
		// get top parent node and then, we will get text within the parent until
		// we reach the entity node
		Node prevNode = node;
		Node lastNode = node;
		while ((prevNode = TagUtils.getPrevNode(prevNode)) != null) {
			lastNode = prevNode;
		}
		String[] features = TagUtils.getAttributes(lastNode, attribute).split(" ");
		return features;
	}
	
	/**
	 * True if the entity is valid
	 * @param entity
	 * @return
	 */
	private boolean isValidEntity(String entity) {
		return (entity.split(" ").length < 5) && (!entity.startsWith("también")) && (entity.length() > 0);
	}
}
