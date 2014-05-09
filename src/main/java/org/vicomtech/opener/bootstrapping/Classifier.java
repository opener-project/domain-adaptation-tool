package org.vicomtech.opener.bootstrapping;

import ixa.kaflib.Term;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.vicomtech.libsvm.utils.svm;
import org.vicomtech.libsvm.utils.svm_model;
import org.vicomtech.libsvm.utils.svm_node;
import org.vicomtech.opener.dictionary.Dictionary;
import org.vicomtech.opener.kafparser.KAFParser;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.nlp.TagsetMappings;
import org.vicomtech.opener.nlp.TagsetMappings.KafTag;
import org.vicomtech.opener.svm.Model;
import org.vicomtech.opener.svm.SVM;
import org.vicomtech.opener.utils.ResourceLoader;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language.LanguageException;

/**
 * This class detects and classifies entities within a KAF file.
 * 
 * org.vicomtech.opener.bootstrapping is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Classifier extends ResourceLoader {
	
	private File inputFile;
	private File outputFile;
	private KAFParser kafParser;
	private List<Model> models;
	private File dictionaryFile;
	private File tf_idfFile;
	private Dictionary dictionary;
	private NLPtools.Type nlpTool;
	private int modSize;
	private int window;
	
	private List<List<KafTag>> entityRules;
	
	/**
	 * The Message Handlers
	 */
	private MessageHandler message;
	
	private static final String ENTITY_RULES_PATH = "/entity.rules";
	
	/**
	 * Public constructor
	 * @param inData : input data
	 * @param outData : output data
	 * @param modelPaths : model path list to classify sample data
	 * @param dictionary : dictionary file to parse words to vectors
	 * @param tf_idf_dictionary : dictionary to extract tf-idf feature
	 * @param language : sample data language
	 * @param window : window size for context features
	 * @param modSize : modifier size for NPs
	 * @param window : token window size
	 * @param modSize : NP modifier sum for each head noun
	 * @param nlpTool : NLP tools needed to extract features
	 * @param message : message handler
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws ParseException 
	 * @throws LanguageException 
	 * @throws InterruptedException 
	 * @throws ExecException 
	 */
	public Classifier(File inputFile, File outputFile, List<File> modelPaths,
					  File dictionary, File tf_idf_dictionary,
					  int window, int modSize,
					  NLPtools.Type nlpTool, MessageHandler message) throws IOException, ClassNotFoundException, ParseException, LanguageException, InterruptedException, ExecException {
		this.entityRules = super.loadRules(ENTITY_RULES_PATH);
		this.dictionaryFile = dictionary;
		this.dictionary = new Dictionary(dictionary);
		this.tf_idfFile = tf_idf_dictionary;
		this.nlpTool = nlpTool;
		this.kafParser = new KAFParser(inputFile, this.dictionary,
									   tf_idf_dictionary, window,
									   modSize, nlpTool, message);
		this.window = window;
		this.modSize = modSize;
		this.message = message;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		
		this.loadModels(modelPaths);
	}
	
	private void displayParameters() {
		this.message.displayHeader("PARAMETERS");
		this.message.displayTextln(String.format("Input file:  %s", this.inputFile.getAbsolutePath()));
		this.message.displayTextln(String.format("Output file: %s", this.outputFile.getAbsolutePath()));
		this.message.displayTextln("");
		this.message.displayTextln(String.format("Language:           %s", this.kafParser.getLanguage()));
		this.message.displayTextln(String.format("Features:           %s", this.nlpTool));
		this.message.displayTextln(String.format("Window size:        %s", this.window));
		if (nlpTool.equals(NLPtools.Type.ALL) || nlpTool.equals(NLPtools.Type.CHUNKER)) {
			this.message.displayTextln(String.format("NP modifiers size:  %s", this.modSize));
		}
		this.message.displayTextln(String.format("Dictionary file:    %s", this.dictionaryFile));
		if (this.tf_idfFile != null) {
			this.message.displayTextln(String.format("TF-IDF:             %s", this.tf_idfFile.getAbsolutePath()));
		}
	}
	
	/**
	 * Processes sample data and classifies the heads within using the input model list
	 * @param inFile : input data
	 * @param outFile : output data
	 * @param modelPaths : model path list to classify sample data
	 * @param dictionary : dictionary file to parse words to vectors
	 * @param tf_idf_dictionary : dictionary to extract tf-idf feature (can be null)
	 * @param window : window size for context features
	 * @param modSize : modifier size for NPs
	 * @param window : token window size
	 * @param modSize : NP modifier sum for each head noun
	 * @param nlpTool : NLP tools needed to extract features
	 * @param message : message handler
	 * @throws Exception 
	 */
	public static void classify(File inFile, File outFile, List<File> modelPaths,
								File dictionary, File tf_idf_dictionary,
								int window, int modSize,
								NLPtools.Type nlpTool, MessageHandler message) throws Exception {
		
		Classifier classifier = new Classifier(inFile, outFile, modelPaths,
											   dictionary, tf_idf_dictionary,
											   window, modSize,
											   nlpTool, message);
		classifier.displayParameters();
		classifier.run();
	}
	
	/**
	 * Load svm models from file list
	 * @param modelPaths
	 * @throws IOException
	 */
	private void loadModels(List<File> modelPaths) throws IOException {
		
		this.models = new ArrayList<Model>();
		for (File file : modelPaths) {
			svm_model model = svm.svm_load_model(file.getAbsolutePath());
			Model classifier = new Model(model, file);
			this.models.add(classifier);
		}
	}
	
	private void run() throws Exception {
		this.message.displayHeader("DETECTING ENTITIES");
		this.message.displayText("detecting");
		List<Term> terms = this.kafParser.getTerms();
		if (terms.size() > 0) {
			int cont = 0;
			// detect entities
			for (int i=0; i<terms.size(); i++) {
				if (i % 5 == 0) {
					this.message.displayText(".");
				}
				// detect entities with more than 1 term
				for (List<KafTag> rule : this.entityRules) {
					List<Integer> ids = this.getRuleMatchingIds(terms, i, rule);
					if (ids.size() > 0) {
						// get instance
						svm_node[] instance = this.kafParser.parse2Vector(ids);
						// classify instance
						double cat = SVM.classifyByProbability(this.models, instance, false);
						if (cat != SVM.NEGATIVE_CLASS) {
							String category = this.dictionary.getCategory(cat);
							if (category == null) {
								throw new IOException(
										String.format("category '%' not found at dictionary '%s'",
												cat, this.dictionaryFile));
							}
							else {
								cont++;
								this.kafParser.addEntity(ids, category);
							}
						}
					}
				}
				// detect entities with only 1 term
				Term term = terms.get(i);
				String pos = term.getPos();
				if (TagsetMappings.isProperNoun(pos)) {
					// get instance
					svm_node[] instance = this.kafParser.parse2Vector(i);
					// classify instance
					double cat = SVM.classifyByProbability(this.models, instance, false);
					if (cat != SVM.NEGATIVE_CLASS) {
						String category = this.dictionary.getCategory(cat);
						if (category == null) {
							throw new IOException(
									String.format("category '%' not found at dictionary '%s'",
											cat, this.dictionaryFile));
						}
						else {
							cont++;
							List<Integer> tIndexes = new ArrayList<Integer>();
							tIndexes.add(i);
							this.kafParser.addEntity(tIndexes, category);
						}
					}
				}
			}
			
			this.message.displayTextln("");
			this.message.displayTextln(String.format("%s entites detected!", cont));
			this.message.displayTextln("");
			this.kafParser.write(this.outputFile);
			this.message.displayTextln(String.format("Output kaf file '%s' wrote.", this.outputFile.getAbsolutePath()));
		}
		else {
			this.message.displayTextln(String.format("Input KAF file '%s' has no term", this.inputFile));
		}
		
	}
	
	/**
	 * Return a list of indexes that match the input rule, if there is
	 * no matching, returns an empty list
	 * @param terms : list of terms
	 * @param index : input index, matching starts from this index
	 * @param rule : input rule
	 * @return
	 */
	private List<Integer> getRuleMatchingIds(List<Term> terms,
											 int index,
											 List<KafTag> rule) {
		List<Integer> ids = new ArrayList<Integer>();
		int sentence = terms.get(index).getSent();
		int j=index;
		for (KafTag tag : rule) {
			// get pos and sentence index
			KafTag pos = TagsetMappings.convertFromStringToKaf(terms.get(j).getPos());
			int sent = terms.get(j).getSent();
			// if sentence and pos are correct add to list
			if (sentence == sent && tag.equals(pos)) {
				ids.add(j);
				if (j+1<terms.size()) {
					j++;
				}
			}
			// no rule matching, empty list
			else {
				ids = new ArrayList<Integer>();
				break;
			}
		}
		return ids;
	}

}
