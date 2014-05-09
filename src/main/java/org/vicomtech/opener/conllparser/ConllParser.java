package org.vicomtech.opener.conllparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import opennlp.tools.util.InvalidFormatException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.vicomtech.opener.bootstrapping.IParser;
import org.vicomtech.opener.bootstrapping.Parser;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.entities.Feature;
import org.vicomtech.opener.entities.FeatureGenerator;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.nlp.NPDetector;
import org.vicomtech.opener.nlp.NLPtools.Type;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.Utils;
import org.xml.sax.SAXException;

/**
 * This class processes a file in Conll format to extract tokens and entities
 * 
 * org.vicomtech.opener.conllparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class ConllParser implements IParser {

	private static enum Parameter {
		INDEX, WORD, LEMMA, POS, HEAD, MORE;
		private static int getIndex(Parameter param) {
			switch (param) {
				case INDEX : return 0;
				case WORD  : return 1;
				case LEMMA : return 2;
				case POS   : return 3;
				case HEAD  : return 4;
				case MORE  : return 5;
				default    : return -1;
			}
		}
	}
	
	/**
	 * Token window size
	 */
	private int window;
	private int modSize;
	
	/**
	 * Entity categories and the ESTER categories
	 */
	private Set<String> categories;
	
	/**
	 * The message handler
	 */
	private MessageHandler message;
	
	private FeatureGenerator f_generator;
	private NLPtools.Type nlpTool;
	
	private Language language;
	
	private static final String COMMENT    = "#";
	private static final String SEPARATOR  = "\\t|\\s";
	private static final int    FIRST_WORD = 1;
	
	/**
	 * Public constructor
	 */
	public ConllParser() {
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
			Type nlpTool, MessageHandler message)
			throws InvalidFormatException, IOException, ParseException, InterruptedException, ExecException {
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
		
		this.categories = categories;
		
		this.message = message;
		
	}

	@Override
	public Set<String> getCategories() {
		return this.categories;
	}

	@Override
	public String[] getTokens(File file) throws ParserConfigurationException,
			SAXException, IOException, InterruptedException {
		List<String> tokens = new ArrayList<String>();
		List<String> lines = Utils.readFile(file);
		for (String line : lines) {
			String token = this.getParameter(line, Parameter.WORD);
			if (token != null) {
				tokens.add(token);
			}
		}
		return tokens.toArray(new String[tokens.size()]);
	}

	@Override
	public String[] getLemmas(File file) throws ParserConfigurationException,
			SAXException, IOException, InterruptedException {
		List<String> lemmas = new ArrayList<String>();
		List<String> lines = Utils.readFile(file);
		for (String line : lines) {
			String lemma = this.getParameter(line, Parameter.LEMMA);
			if (lemma != null) {
				lemmas.add(lemma);
			}
		}
		return lemmas.toArray(new String[lemmas.size()]);
	}

	@Override
	public Map<String, List<Entity>> getEntities(File file)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, InterruptedException, Exception {
		Map<String,List<Entity>> entities = new HashMap<String,List<Entity>>();
		List<String> lines = Utils.readFile(file);
		for (int i=0; i<lines.size(); i++) {
			ConllLine cline = this.getConllLine(lines.get(i));
			if (cline != null && cline.getIndex()==FIRST_WORD) {
				// get all lines of the sentence
				List<ConllLine> clines = new ArrayList<ConllLine>();
				// not add ellipsis
				if (!cline.isEllipsis()) {
					clines.add(cline);
				}
				while (i+1 < lines.size()) {
					i++;
					cline = this.getConllLine(lines.get(i));
					if (cline != null && cline.getIndex()>FIRST_WORD) {
						// not add ellipsis
						if (!cline.isEllipsis()) {
							clines.add(cline);
						}
					}
					else break;
				}
				
				if (i+1 < lines.size()) {
					i--;
				}
				// get entities within sentence
				for (int j=0;j<clines.size(); j++) {
					cline = clines.get(j);
					if (cline.isEntity()) {
						boolean found = false;
						for (String cat : this.categories) {
							String category = cline.getCategory();
							if (cat.equalsIgnoreCase(category)
									|| category.equalsIgnoreCase(Parser.EMPTY_CATEGORY)) {
								found = true;
								break;
							}
						}
						if (!found) {
							cline.setCategory(Parser.EMPTY_CATEGORY);
						}
						
						Entity entity = null;
						if (this.nlpTool.equals(NLPtools.Type.TOKENIZER)) {
							entity = this.getEntityTokenizer(clines, j);
						}
						else if (this.nlpTool.equals(NLPtools.Type.ALL)) {
							entity =  this.getEntityAll(clines, j);
						}
						else if (this.nlpTool.equals(NLPtools.Type.ALL_NO_CHUNK)) {
							entity =  this.getEntityAllNoChunk(clines, j);
						}
						
						List<Entity> list = entities.get(cline.getCategory());
						if (list == null) {
							list = new ArrayList<Entity>();
						}
						list.add(entity);
						entities.put(cline.getCategory(), list);
					}
				}
				this.message.displayText(".");
			}
		}
		return entities;
	}
	
	private Entity getEntityTokenizer(List<ConllLine> clines, int index) {
		List<String> leftFeatures = new ArrayList<String>();
		for (int i=0; i<index; i++) {
			leftFeatures.add(clines.get(i).getWord());
		}
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new ArrayList<String>();
		for (int i=index+1; i<clines.size(); i++) {
			rightFeatures.add(clines.get(i).getWord());
		}
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		String entity = clines.get(index).getWord().replace("_", " ");
		String category = clines.get(index).getCategory();
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		
		return e;
	}
	
	private Entity getEntityChunking(List<ConllLine> clines, int index) throws IOException {
		String[] text = this.getAttributes(clines, index, Parameter.WORD);
		String[] pos = this.getAttributes(clines, index, Parameter.POS);
		String leftTok = new String();
		if (index > 0) {
			leftTok = text[index-1];
		}
		String rightTok = new String();
		if (index+1 < text.length) {
			rightTok = text[index+1];
		}
		
		NPDetector chunker = new NPDetector(this.language, this.modSize);
		List<Feature> features = chunker.chunk(text, pos);
		
		Entity e = Utils.createEntity(
				clines.get(index).getWord(),
				clines.get(index).getCategory(),
				features, leftTok, rightTok, this.window, this.modSize, 1);
		
		return e;
	}
	
	private Entity getEntityAll(List<ConllLine> clines, int index) throws Exception {
		Entity e = this.getEntityChunking(clines, index);
		return getAllFeatures(e, clines, index);
	}
	
	private Entity getEntityAllNoChunk(List<ConllLine> clines, int index) throws Exception {
		Entity e = this.getEntityTokenizer(clines, index);
		return getAllFeatures(e, clines, index);
	}
	
	private Entity getAllFeatures(Entity entity, List<ConllLine> clines, int index) throws Exception {
		if (entity != null) {
			this.f_generator.setEntity(entity);
			
			// words
			String[] leftWords = this.getLeftAttributes(clines, index, Parameter.WORD);
			String[] rightWords = this.getRightAttributes(clines, index, Parameter.WORD);
			
			// lemmas
			String[] leftLemmas = this.getLeftAttributes(clines, index, Parameter.LEMMA);
			String[] rightLemmas = this.getRightAttributes(clines, index, Parameter.LEMMA);
			
			this.f_generator.addLemmas(leftLemmas, rightLemmas);
			
			// part of speech
			String[] leftPos = this.getLeftAttributes(clines, index, Parameter.POS);
			String[] rightPos = this.getRightAttributes(clines, index, Parameter.POS);
			
			this.f_generator.addPoS(leftPos, rightPos);
			
//			// add first word feature
//			if (leftWords.length > 0) {
//				f_generator.addFirstWord(leftLemmas[leftLemmas.length-1]);
//			}
//			else {
//				f_generator.addFirstWord(new String());
//			}
//			
//			// add length feature
//			f_generator.addLength();
			
			// add previous prefix feature
			if (leftWords.length > 0) {
				this.f_generator.addPrefix(leftWords[leftWords.length-1]);
			}
			else {
				this.f_generator.addPrefix(new String());
			}
			
			// add prefix feature
			this.f_generator.addPrefix();
			
			// add next prefix feature
			if (rightWords.length > 0) {
				this.f_generator.addPrefix(rightWords[0]);
			}
			else {
				this.f_generator.addPrefix(new String());
			}
			
			// add previous suffix feature
			if (leftWords.length > 0) {
				this.f_generator.addSuffix(leftWords[leftWords.length-1]);
			}
			else {
				this.f_generator.addSuffix(new String());
			}
			
			// add next suffix feature
			this.f_generator.addSuffix();
			
			// add suffix feature
			if (rightWords.length > 0) {
				this.f_generator.addSuffix(rightWords[0]);
			}
			else {
				this.f_generator.addSuffix(new String());
			}
			
			// add gazetteer feature
			this.f_generator.addGazetteer(leftWords, rightWords);
			
			entity = this.f_generator.getEntity();
		}
		
		return entity;
	}
	
	private String[] getAttributes(List<ConllLine> clines, int index, Parameter param) {
		String[] leftAttributes = this.getLeftAttributes(clines, index, param);
		String middleAttribute = this.getAttribute(clines.get(index), param);
		String[] rightAttributes = this.getRightAttributes(clines, index, param);
		
		List<String> attributes = new ArrayList<String>();
		attributes.addAll(Arrays.asList(leftAttributes));
		attributes.add(middleAttribute);
		attributes.addAll(Arrays.asList(rightAttributes));
		
		return attributes.toArray(new String[attributes.size()]);
	}
	
	private String[] getLeftAttributes(List<ConllLine> clines, int index, Parameter param) {
		List<String> attributes = new ArrayList<String>();
		for (int i=0; i<index; i++) {
			attributes.add(this.getAttribute(clines.get(i), param));
		}
		return attributes.toArray(new String[attributes.size()]);
	}
	
	private String[] getRightAttributes(List<ConllLine> clines, int index, Parameter param) {
		List<String> attributes = new ArrayList<String>();
		for (int i=index+1; i<clines.size(); i++) {
			attributes.add(this.getAttribute(clines.get(i), param));
		}
		return attributes.toArray(new String[attributes.size()]);
	}
	
	private String getAttribute(ConllLine cline, Parameter param) {
		switch (param) {
			case INDEX : return Integer.toString(cline.getIndex());
			case WORD  : return cline.getWord();
			case LEMMA : return cline.getLemma();
			case POS   : return cline.getPoS();
			case HEAD  : return Integer.toString(cline.getHead());
			case MORE  : return cline.getMore();
			default : return null;
		}
	}

	private String getParameter(String line, Parameter param) {
		if (line.length() > 0 && !line.startsWith(COMMENT)) {
			String[] columns = line.split(SEPARATOR);
			int index = Parameter.getIndex(param);
			if (param.equals(Parameter.MORE) && columns.length < index+1) {
				return null;
			}
			else {
				return columns[index];
			}
		}
		else {
			return null;
		}
	}
	
	private ConllLine getConllLine(String line) {
		String ind = this.getParameter(line, Parameter.INDEX);
		if (ind != null) {
			int index = Integer.parseInt(ind);
			String word = this.getParameter(line, Parameter.WORD);
			String lemma = this.getParameter(line, Parameter.LEMMA);
			String pos = this.getParameter(line, Parameter.POS);
			int head = Integer.parseInt(this.getParameter(line, Parameter.HEAD));
			String more = this.getParameter(line, Parameter.MORE);
			if (more != null) {
				return new ConllLine(index, word, lemma, pos, head, more);
			}
			else {
				return new ConllLine(index, word, lemma, pos, head);
			}
		}
		else {
			return null;
		}
	}
	
}
