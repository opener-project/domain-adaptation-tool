package org.vicomtech.opener.kafparser;

import ixa.kaflib.KAFDocument;
import ixa.kaflib.Span;
import ixa.kaflib.Term;
import ixa.kaflib.WF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import opennlp.maxent.Main;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.vicomtech.libsvm.utils.svm_node;
import org.vicomtech.opener.dictionary.Dictionary;
import org.vicomtech.opener.dictionary.TF_IDF;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.entities.Feature;
import org.vicomtech.opener.entities.FeatureGenerator;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.nlp.NPDetector;
import org.vicomtech.opener.svm.SVMWriter;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.Utils;
import org.vicomtech.opener.utils.Language.LanguageException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * This class uses IXA kaflib to parse KAF files
 * 
 * org.vicomtech.opener.kafparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class KAFParser {

	private static enum Parameter {
		WF, POS, LEMMA;
	}
	
	private int window;
	private int modSize;
	private Language language;
	
	private KAFDocument inKaf;
	private Dictionary dictionary;
	private TF_IDF tfidf = null;
	private NLPtools.Type nlpTool;
	private FeatureGenerator f_generator;
	
	/**
	 * The Message Handlers
	 */
	private MessageHandler message;
	
	private static final String ENTITY_ID_PREFIX = "e";
	private static final String LINGUISTIC_PROCESSOR = "vicom-domain-adaptator";
	
	public KAFParser(File inputFile,
					 Dictionary dictionary,
					 File tf_idf_dictionary,
					 int window,
					 int modSize,
					 NLPtools.Type nlpTool,
					 MessageHandler message) throws IOException, LanguageException, ClassNotFoundException, ParseException, InterruptedException, ExecException {
		this.window = window;
		this.modSize = modSize;
		this.nlpTool = nlpTool;
		this.message = message;
		
		this.inKaf = KAFDocument.createFromFile(inputFile);
		this.language = new Language(this.inKaf.getLang());
		
		this.dictionary = dictionary;
		if (tf_idf_dictionary != null) {
			this.tfidf = new TF_IDF(tf_idf_dictionary);
		}
		
		if (nlpTool.equals(NLPtools.Type.LEMMATIZER_POSTAGGER))
			this.window = window+2; // each token has 2 features
		else if (nlpTool.equals(NLPtools.Type.ALL))
			this.f_generator = new FeatureGenerator(this.window, this.language, this.message);
		else if (nlpTool.equals(NLPtools.Type.ALL_NO_CHUNK))
			this.f_generator = new FeatureGenerator(this.window, this.language, this.message);
		
		this.addLinguisticProcessor();
	}
	
	private void addLinguisticProcessor() throws IOException {
		Properties p = new Properties();
		p.load(Main.class.getClass().getResourceAsStream("/version.prop"));
		String version = p.getProperty("version");
		//String v = KAFParser.class.getClass().getPackage().getImplementationVersion();
		this.inKaf.addLinguisticProcessor("entities", LINGUISTIC_PROCESSOR, this.getTimestamp(), version);
	}
	
	public String getTimestamp() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		String data = fmt.format(ts);
		fmt = new SimpleDateFormat("HH:mm:ss");
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		String time = fmt.format(ts);
		String timestamp = data + "T" + time + "Z";
		return timestamp;
	}
	
	public String getLanguage() {
		return this.language.getValue();
	}
	
	/**
	 * Returns terms within kaf document
	 * @return
	 */
	public List<Term> getTerms() {
		return this.inKaf.getTerms();
	}
	
	public List<ixa.kaflib.Entity> getEntities() {
		return this.inKaf.getEntities();
	}
	
	public void addEntity(List<Integer> tIndexes, String category) {
		// create span
		Span<Term> span = KAFDocument.newTermSpan();
		for (int i : tIndexes) {
			span.addTarget(this.inKaf.getTerms().get(i));
		}
		
		// create list of spans
		List<Span<Term>> spans = new ArrayList<Span<Term>>();
		spans.add(span);
		
		// create entity id
		int lastEId = 0;
		if (this.inKaf.getEntities().size() > 0) {
			lastEId = this.getEntityNumber(this.inKaf.getEntities().size()-1);
		}
		String eId = this.getCreateEntityId(++lastEId);
		
		// create entity
		ixa.kaflib.Entity entity = this.inKaf.newEntity(eId, spans);
		entity.setType(category);
	}
	
	private int getEntityNumber(int index) {
		List<ixa.kaflib.Entity> entities = this.inKaf.getEntities();
		String eId = entities.get(index).getId();
		return Integer.parseInt(eId.substring(ENTITY_ID_PREFIX.length(), eId.length()));
	}
	
	private String getCreateEntityId(int number) {
		return ENTITY_ID_PREFIX+number;
	}
	
	public svm_node[] parse2Vector(int index) throws Exception {
		List<Term> terms = this.getSentence(index);
		// get entity object
		Entity entity = this.getEntity(terms, index);
		// get vector
		svm_node[] vector = this.getVector(entity);
		
		return vector;
	}
	
	public svm_node[] parse2Vector(List<Integer> indexes) throws Exception {
		List<Term> terms = this.getSentence(indexes.get(0));
		// get entity object
		Entity entity = this.getEntity(terms, indexes);
		// get vector
		svm_node[] vector = this.getVector(entity);
		
		return vector;
	}
	
	private List<Term> getSentence(int index) {
		List<Term> sentence = new ArrayList<Term>();
		// get terms
		List<Term> terms = this.inKaf.getTerms();
		// get sentence id
		int sentenceId = terms.get(index).getSent();
		// add terms from the left
		for (int i=index-1; i>=0; i--) {
			Term t = terms.get(i);
			int sId = t.getSent();
			if (sId == sentenceId) {
				sentence.add(0, t);
			}
			else {
				break;
			}
		}
		sentence.add(terms.get(index));
		// add terms from the right
		for (int i=index+1; i<terms.size(); i++) {
			Term t = terms.get(i);
			int sId = t.getSent();
			if (sId == sentenceId) {
				sentence.add(t);
			}
			else {
				break;
			}
		}
		
		return sentence;
	}
	
	private Entity getEntity(List<Term> terms, int index) throws Exception {
		Entity entity = null;
		if (this.nlpTool.equals(NLPtools.Type.TOKENIZER)) {
			entity = this.getEntityTokenizer(terms, index);
		}
		else if (this.nlpTool.equals(NLPtools.Type.ALL)) {
			entity =  this.getEntityAll(terms, index);
		}
		else if (this.nlpTool.equals(NLPtools.Type.ALL_NO_CHUNK)) {
			entity =  this.getEntityAllNoChunk(terms, index);
		}
		return entity;
	}
	
	private Entity getEntity(List<Term> terms, List<Integer> indexes) throws Exception {
		Entity entity = null;
		if (this.nlpTool.equals(NLPtools.Type.TOKENIZER)) {
			entity = this.getEntityTokenizer(terms, indexes);
		}
		else if (this.nlpTool.equals(NLPtools.Type.ALL)) {
			entity =  this.getEntityAll(terms, indexes);
		}
		else if (this.nlpTool.equals(NLPtools.Type.ALL_NO_CHUNK)) {
			entity =  this.getEntityAllNoChunk(terms, indexes);
		}
		return entity;
	}
	
	private Entity getEntityTokenizer(List<Term> terms, int index) {
		List<String> leftFeatures = this.getLeftAttributes(terms, index, Parameter.WF);
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = this.getRightAttributes(terms, index, Parameter.WF);
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		String entity = this.getAttribute(terms.get(index), Parameter.WF).replace("_", " ");
		String category = null;
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		
		return e;
	}
	
	private Entity getEntityTokenizer(List<Term> terms, List<Integer> indexes) {
		List<String> leftFeatures = this.getLeftAttributes(terms, indexes.get(0), Parameter.WF);
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = this.getRightAttributes(terms, indexes.get(indexes.size()-1), Parameter.WF);
		
		leftFeatures = Utils.fitWindow(leftFeatures, this.window, -1);
		rightFeatures = Utils.fitWindow(rightFeatures, this.window, 1);
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		String entity = StringUtils.join(this.getMiddleAttributes(terms, indexes, Parameter.WF)," ").replace("_", " ");
		String category = null;
		
		Entity e = new Entity(entity, category, leftFeatures, middleFeatures, rightFeatures);
		
		return e;
	}
	
	private Entity getEntityChunking(List<Term> terms, int index) throws IOException {
		List<String> text = this.getAttributes(terms, index, Parameter.WF);
		List<String> pos = this.getAttributes(terms, index, Parameter.POS);
		String leftTok = new String();
		if (index > 0) {
			leftTok = text.get(index-1);
		}
		String rightTok = new String();
		if (index+1 < text.size()) {
			rightTok = text.get(index+1);
		}
		
		NPDetector chunker = new NPDetector(this.language, this.modSize);
		List<Feature> features = chunker.chunk(
				text.toArray(new String[text.size()]),
				pos.toArray(new String[pos.size()]));
		
		String entity = this.getAttribute(terms.get(index), Parameter.WF);
		String category = null;
		
		Entity e = Utils.createEntity(entity, category,
				features, leftTok, rightTok, this.window, this.modSize, 1);
		
		return e;
	}
	
	private Entity getEntityChunking(List<Term> terms, List<Integer> indexes) throws IOException {
		List<String> text = this.getAttributes(terms, indexes.get(0), Parameter.WF);
		List<String> pos = this.getAttributes(terms, indexes.get(0), Parameter.POS);
		String leftTok = new String();
		if (indexes.get(0) > 0) {
			leftTok = text.get(indexes.get(0)-1);
		}
		String rightTok = new String();
		if (indexes.get(indexes.size()-1)+1 < text.size()) {
			rightTok = text.get(indexes.get(indexes.size()-1)+1);
		}
		
		NPDetector chunker = new NPDetector(this.language, this.modSize);
		List<Feature> features = chunker.chunk(
				text.toArray(new String[text.size()]),
				pos.toArray(new String[pos.size()]));
		
		String entity = StringUtils.join(this.getMiddleAttributes(terms, indexes, Parameter.WF), " ");
		String category = null;
		
		Entity e = Utils.createEntity(entity, category,
				features, leftTok, rightTok, this.window, this.modSize, 1);
		
		return e;
	}
	
	private Entity getEntityAll(List<Term> terms, int index) throws Exception {
		Entity e = this.getEntityChunking(terms, index);
		return getAllFeatures(e, terms, index);
	}
	
	private Entity getEntityAll(List<Term> terms, List<Integer> indexes) throws Exception {
		Entity e = this.getEntityChunking(terms, indexes);
		return getAllFeatures(e, terms, indexes);
	}
	
	private Entity getEntityAllNoChunk(List<Term> terms, int index) throws Exception {
		Entity e = this.getEntityTokenizer(terms, index);
		return getAllFeatures(e, terms, index);
	}
	
	private Entity getEntityAllNoChunk(List<Term> terms, List<Integer> indexes) throws Exception {
		Entity e = this.getEntityTokenizer(terms, indexes);
		return getAllFeatures(e, terms, indexes);
	}
	
	private Entity getAllFeatures(Entity entity, List<Term> terms, int index) throws Exception {
		if (entity != null) {
			this.f_generator.setEntity(entity);
			List<String> lag;
			
			// words
			lag = this.getLeftAttributes(terms, index, Parameter.WF);
			String[] leftWords = lag.toArray(new String[lag.size()]);
			lag = this.getRightAttributes(terms, index, Parameter.WF);
			String[] rightWords = lag.toArray(new String[lag.size()]);
			
			// lemmas
			lag = this.getLeftAttributes(terms, index, Parameter.LEMMA);
			String[] leftLemmas = lag.toArray(new String[lag.size()]);
			lag = this.getRightAttributes(terms, index, Parameter.LEMMA);
			String[] rightLemmas = lag.toArray(new String[lag.size()]);
			
			this.f_generator.addLemmas(leftLemmas, rightLemmas);
			
			// part of speech
			lag = this.getLeftAttributes(terms, index, Parameter.POS);
			String[] leftPos = lag.toArray(new String[lag.size()]);
			lag = this.getRightAttributes(terms, index, Parameter.POS);
			String[] rightPos = lag.toArray(new String[lag.size()]);
			
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
	
	private Entity getAllFeatures(Entity entity, List<Term> terms, List<Integer> indexes) throws Exception {
		return null;
	}
	
	private svm_node[] getVector(Entity entity) {
		String features = SVMWriter.getLine(entity, this.dictionary, false, false).trim();
		if (this.tfidf != null) {
			// get token with max tf-idf value
			String max_tf_idf_token = this.getMaxTF_IDF_Token(entity);
			if (max_tf_idf_token != null) {
				// add tf-idf feature
				String[] nodes = features.split(" ");
				String[] lastNode = nodes[nodes.length-1].split(":");
				int lastIndex = Integer.parseInt(lastNode[0]);
				lastIndex++;
				features += " "+lastIndex+":"+this.tfidf.getDictionary().get(max_tf_idf_token);
			}
		}
		svm_node[] nodes = svm_node.parseLine(features);
		return nodes;
	}
	
	private String getMaxTF_IDF_Token(Entity entity) {
		String max_tf_idf_token = null;
		int min_index = Integer.MAX_VALUE;
		String ent = entity.getEntity();
		for (String token : ent.split(" ")) {
			for (String cat : this.tfidf.getTF_IDF_Map().keySet()) {
				List<String> tdf_idf_list = this.tfidf.getTF_IDF_Map().get(cat);
				if (tdf_idf_list.contains(token)) {
					int ind = tdf_idf_list.indexOf(token);
					if (ind < min_index) {
						min_index = ind;
						max_tf_idf_token = token;
						break;
					}
				}
			}
		}
		return max_tf_idf_token;
	}
	
	private List<String> getLeftAttributes(List<Term> terms, int index, Parameter param) {
		List<String> attributes = new ArrayList<String>();
		for (int i=0; i<index; i++) {
			attributes.add(this.getAttribute(terms.get(i), param));
		}
		return attributes;
	}
	
	private List<String> getRightAttributes(List<Term> terms, int index, Parameter param) {
		List<String> attributes = new ArrayList<String>();
		for (int i=index+1; i<terms.size(); i++) {
			attributes.add(this.getAttribute(terms.get(i), param));
		}
		return attributes;
	}
	
	private List<String> getMiddleAttributes(List<Term> terms, List<Integer> indexes, Parameter param) {
		List<String> attributes = new ArrayList<String>();
		for (int i=indexes.get(0); i<=indexes.get(indexes.size()-1); i++) {
			attributes.add(this.getAttribute(terms.get(i), param));
		}
		return attributes;
	}
	
	private String getAttribute(Term term, Parameter param) {
		switch (param) {
			case WF   :
				List<String> wfs = new ArrayList<String>();
				for (WF wf : term.getWFs()) {
					wfs.add(wf.getForm());
				}
				return StringUtils.join(wfs, "_");
			case POS  : return term.getPos();
			case LEMMA: return term.getLemma(); 
			default   : return null;
		}
	}
	
	private List<String> getAttributes(List<Term> terms, int index, Parameter param) {
		List<String> leftAttributes = this.getLeftAttributes(terms, index, param);
		String middleAttribute = this.getAttribute(terms.get(index), param);
		List<String> rightAttributes = this.getRightAttributes(terms, index, param);
		
		List<String> attributes = new ArrayList<String>();
		attributes.addAll(leftAttributes);
		attributes.add(middleAttribute);
		attributes.addAll(rightAttributes);
		
		return attributes;
	}
	
	public void write(File outputFile) {
		this.inKaf.save(outputFile.getAbsolutePath());
	}
	
}
