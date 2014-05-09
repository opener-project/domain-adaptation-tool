package org.vicomtech.opener.bootstrapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.vicomtech.opener.bootstrapping.CorpusProcessor.CorpusProcessorException;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NLPtools;
import org.vicomtech.opener.svm.SVM;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.Language.LanguageException;
import org.vicomtech.opener.utils.Utils;
import org.xml.sax.SAXException;

/**
 * This is the main class of the Domain Adaptation Tool.
 * It uses libSVM to create SVM instances in order to train
 * classifiers.
 * 
 * org.vicomtech.opener.bootstrapping is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Main {
	
	/**
	 * A thread class to close MessaseHandler
	 */
	public class AppFinisher extends Thread {
		private MessageHandler message;
		public AppFinisher(MessageHandler message) {
			this.message = message;
		}
		public void run() {
			this.message.close();
		}
	}
	
	/**
	 * Command numbers
	 */
	private final static int CMD_PARSE_CORPUS = 0;
	private final static int CMD_TRAIN = 1;
	private final static int CMD_CLASSIFY = 2;
	
	private final static Map<Integer,String> commandMap;
	static {
		commandMap = new HashMap<Integer,String>();
		commandMap.put(CMD_PARSE_CORPUS, "parse");
		commandMap.put(CMD_TRAIN, "train");
		commandMap.put(CMD_CLASSIFY, "classify");
	}
	
	/**
	 * Command selected
	 */
	private int command = -1;
	
	/**
	 * The Message Handler
	 */
	private MessageHandler message;
	
	/**
	 * Corpus language
	 */
	private Language language = null;
	
	/**
	 * Corpus name
	 */
	private String name   = null;
	private String corpus = "conll";
	
	/**
	 * Corpus files
	 */
	private File trainCorpus = null;
	private File devCorpus   = null;
	private File testCorpus  = null;
	
	/**
	 * The seed-list file
	 */
	private File seedFile = null;
	
	/**
	 * The classifier list
	 */
	private List<File> classifiers = null;
	
	/**
	 * Token window size
	 */
	private int window  = 3;
	private int modSize = 5;
	
	/**
	 * Balanced | unbalanced seed train list boolean
	 */
	private boolean balanced = false;
	
	private boolean tfidf = false;
	
	/**
	 * NLP processors
	 */
	private NLPtools.Type nlpTools = NLPtools.Type.ALL;
	
	/**
	 * List of data files for training
	 */
	private List<File> labelledData = null;
	private File unlabelledData     = null;
	
	/**
	 * Default values
	 */
	private int    ratio            = 2;
	private double threshold        = 0.95;
	private double tf_idf_threshold = 0.55;
	
	private boolean featureSelection = false;
	
	/**
	 * Data files for classification
	 */
	private File inData  = null;
	private File outData = null;
	
	/**
	 * Binarized dictionary path
	 */
	private File dictionary = null;
	private File tf_idf_dictionary = null;
	
	/**
	 * Get arguments for the application
	 * @param args
	 * @throws LanguageException 
	 * @throws IOException 
	 */
	private void getArguments(String[] originalArgs) throws LanguageException, IOException {
		
		ArrayList<String> args = new ArrayList<String>();
		for ( String tmp : originalArgs )
			if ( tmp.length() > 0 ) args.add(tmp);
		
		if ( args.contains("-h") || args.contains("--help") ) {
			this.showHelp();
			System.exit(MessageHandler.GOOD);
		}
		
		// get arguments
		for ( int i=0; i<args.size(); i++ ) {
			String arg = args.get(i);
			if (arg.equalsIgnoreCase("-parse"))
				this.command = CMD_PARSE_CORPUS;
			else if ( arg.equalsIgnoreCase("-train") )
				this.command = CMD_TRAIN;
			else if ( arg.equalsIgnoreCase("-classify") )
				this.command = CMD_CLASSIFY;
			else if ( arg.equalsIgnoreCase("-l") )
				this.language = new Language(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-trainC") )
				this.trainCorpus = new File(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-seeds") )
				this.seedFile = new File(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-devC") )
				this.devCorpus = new File(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-testC") )
				this.testCorpus = new File(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-corpus") )
				this.corpus = this.getArgument(args, ++i);
			else if ( arg.equalsIgnoreCase("-window") )
				this.window = Utils.atoi(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-modSize") )
				this.modSize = Utils.atoi(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-balanced") )
				this.balanced = true;
			else if ( arg.equalsIgnoreCase("-tfidf") ) {
				this.tfidf = true;
				if (i+1<args.size() && !args.get(i+1).startsWith("-")) {
					if (this.command == CMD_TRAIN) {
						this.tf_idf_threshold = Double.parseDouble(this.getArgument(args, ++i));
					}
					else {
						this.tf_idf_dictionary = new File(this.getArgument(args, ++i));
					}
				}
			}
			else if ( arg.equalsIgnoreCase("-tokenizer") )
				this.nlpTools = NLPtools.Type.TOKENIZER;
			else if ( arg.equalsIgnoreCase("-constituentParser") )
				this.nlpTools = NLPtools.Type.CONSTITUENT_PARSER;
			else if ( arg.equalsIgnoreCase("-chunker") )
				this.nlpTools = NLPtools.Type.CHUNKER;
			else if ( arg.equalsIgnoreCase("-lemchunker") )
				this.nlpTools = NLPtools.Type.LEMMATIZER_CHUNKER;
			else if ( arg.equalsIgnoreCase("-poschunker") )
				this.nlpTools = NLPtools.Type.POSTAGGER_CHUNKER;
			else if ( arg.equalsIgnoreCase("-lemmatizer") )
				this.nlpTools = NLPtools.Type.LEMMATIZER;
			else if ( arg.equalsIgnoreCase("-postagger") )
				this.nlpTools = NLPtools.Type.POSTAGGER;
			else if ( arg.equalsIgnoreCase("-lempos") )
				this.nlpTools = NLPtools.Type.LEMMATIZER_POSTAGGER;
			else if ( arg.equalsIgnoreCase("-all") )
				this.nlpTools = NLPtools.Type.ALL;
			else if ( arg.equalsIgnoreCase("-all_no_chunk") )
				this.nlpTools = NLPtools.Type.ALL_NO_CHUNK;
			else if ( arg.equalsIgnoreCase("-concat_pos") )
				this.nlpTools = NLPtools.Type.CONCAT_POS;
			else if ( arg.equalsIgnoreCase("-name") )
				this.name = this.getArgument(args, ++i);
			else if ( arg.equalsIgnoreCase("-labelled") ) {
				this.labelledData = new ArrayList<File>();
				for (; ++i<args.size(); ) {
					arg = args.get(i);
					if ( !arg.startsWith("-"))
						this.labelledData.add(new File(arg));
					else { i--;break; }
				}
			}
			else if ( arg.equalsIgnoreCase("-unlabelled") )
				this.unlabelledData = new File(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-ratio") )
				this.ratio = Utils.atoi(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-th") )
				this.threshold = Utils.atof(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-f") )
				this.featureSelection = Utils.atob(arg);
			else if ( arg.equalsIgnoreCase("-in") )
				this.inData = new File(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-out") )
				this.outData = new File(this.getArgument(args, ++i));
			else if ( arg.equalsIgnoreCase("-models") ) {
				this.classifiers = new ArrayList<File>();
				for (; ++i<args.size(); ) {
					arg = args.get(i);
					if ( !arg.startsWith("-"))
						this.classifiers.add(new File(arg));
					else { i--;break; }
				}
			}
			else if ( arg.equalsIgnoreCase("-dic") )
				this.dictionary = new File(this.getArgument(args, ++i));
			else {
				System.err.println(String.format("ERROR: option '%s' unknown", arg));
				this.showHelp();
				System.exit(MessageHandler.FAILURE);
			}
		}
		
		// check arguments
		if (!this.checkArguments()) {
			this.showHelp();
			System.exit(MessageHandler.FAILURE);
		}
	}
	
	/**
	 * Get the argument at the index position
	 * @param args
	 * @param index
	 * @return
	 */
	private String getArgument (List<String> args, int index) {
		if (index >= args.size()) {
			this.showHelp();
			System.err.println(String.format(
				"ERROR: missing parameter after '%s'", args.get(index-1)));
			System.exit(MessageHandler.FAILURE);
		}
		return args.get(index);
	}
	
	/**
	 * Check that the arguments are correct
	 * @return
	 */
	private boolean checkArguments() {
		
		// check arguments for translation command
		if (this.command == CMD_PARSE_CORPUS)
			return this.checkParseCorpusArguments();
		else if (this.command == CMD_TRAIN)
			return this.checkTrainArguments();
		else if (this.command == CMD_CLASSIFY)
			return this.checkClassifyArguments();
		else return true;
	}
	
	/**
	 * Check arguments for corpus parsing
	 * @return
	 */
	private boolean checkParseCorpusArguments() {
		
		// check language
		if (this.language == null) {
			System.err.println("ERROR: language not specified");
			return false;
		}
		else if (this.corpus == null) {
			System.err.println("ERROR: corpus not specified");
			return false;
		}
		else if (this.name == null) {
			System.err.println("ERROR: corpus name not specified");
			return false;
		}
		else
			// check corpus
			if (this.trainCorpus == null) {
				System.err.println("ERROR: corpus not specified");
				return false;
			}
			else if (this.trainCorpus.exists())
				// check seed-list
				if (this.seedFile == null) {
					System.err.println("ERROR: seed-list not specified");
					return false;
				}
				else if (this.trainCorpus.exists()) {
					// check corpus processor
					if (CorpusProcessor.exists(this.corpus)) {
						return true;
					}
					else {
						System.err.println("ERROR: specified corpus not supported");
						return false;
					}
				}
				else {
					System.err.println("ERROR: seed-list does not exist");
					return false;
				}
			else {
				System.err.println("ERROR: corpus does not exist");
				return false;
			}
	}
	
	/**
	 * Check arguments for training a classifier
	 * @return
	 */
	private boolean checkTrainArguments() {
		
		// check sample datas
		if (this.labelledData.size() > 0) {
			for (File file : this.labelledData)
				if (!file.exists()) {
					System.err.println(String.format("ERROR: labelled data '%s' does not exist",
							file.getAbsolutePath()));
					return false;
				}
			// check unlabeled data
			if (this.unlabelledData != null)
				if (this.unlabelledData.exists())
					// check test data
					if (this.testCorpus != null)
						if (this.testCorpus.exists())
							return true;
						else {
							System.err.println(String.format("ERROR: test data '%s' does not exist",
									this.testCorpus.getAbsolutePath()));
							return false;
						}
					else
						return true;
				else {
					System.err.println(String.format("ERROR: unlabelled data '%s' does not exist",
							this.unlabelledData.getAbsolutePath()));
					return false;
				}
			else {
				System.err.println("ERROR: unlabelled data not specified");
				return false;
			}
		}
		else {
			System.err.println("ERROR: labelled data not specified");
			return false;
		}
	}
	
	/**
	 * Check arguments for classifying instances
	 * @return
	 */
	private boolean checkClassifyArguments() {
		
		// check input data
		if (this.inData == null)
			System.err.println("ERROR: input kaf file not specified");
		else if (!this.inData.exists()) {
			System.err.println(String.format("ERROR: input kaf file '%s' does not exist",
					inData.getAbsolutePath()));
			return false;
		}
		
		// check output data
		if (this.outData == null) {
			System.err.println("ERROR: output kaf file not specified");
			return false;
		}
		
		// check classifiers
		if (this.classifiers.size() > 0) {
			for (File file : this.classifiers)
				if (!file.exists()) {
					System.err.println(String.format("ERROR: classifier '%s' does not exist",
							file.getAbsolutePath()));
					return false;
				}
		}
		else {
			System.err.println("classifier not specified");
			return false;
		}
		
		// check dictionary
		if (this.dictionary == null)
			System.err.println("ERROR: dictionary not specified");
		else if (!this.dictionary.exists()) {
			System.err.println(String.format("ERROR: dictionary '%s' does not exist",
					dictionary.getAbsolutePath()));
			return false;
		}
		else if (!Utils.isBinarizedDictionary(this.dictionary)) {
			System.err.println(String.format("ERROR: dictionary '%s' has not '%s' extension",
					dictionary.getAbsolutePath(),
					Utils.BINARIZED_DIC_EXT));
			return false;
		}
		
		// check tf-idf dictionary
		if (this.tf_idf_dictionary != null) {
			if (!this.tf_idf_dictionary.exists()) {
				System.err.println(String.format("ERROR: tf-idf dictionary '%s' does not exist",
						this.tf_idf_dictionary.getAbsolutePath()));
				return false;
			}
			else if (!Utils.isBinarizedTF_IDF(this.tf_idf_dictionary)) {
				System.err.println(String.format("ERROR: tf-idf dictionary '%s' has not '%s' extension",
						this.tf_idf_dictionary.getAbsolutePath(),
						Utils.BINARIZED_TFIDF_EXT));
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets runtime and adds a shutdownhook to close MessaseHandler
	 */
	private void addAppFinisher() {
		Runtime.getRuntime().addShutdownHook(new AppFinisher(this.message));
	}
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		
		Main prog = new Main();
		
		try {
			prog.getArguments(args);
			prog.message = new MessageHandler(commandMap.get(prog.command));
			prog.addAppFinisher();
			
			if (prog.command == CMD_PARSE_CORPUS)
				Parser.parseCorpus(prog.corpus, prog.language, prog.trainCorpus,
						prog.devCorpus, prog.testCorpus, prog.seedFile,
						prog.window, prog.modSize, prog.balanced, prog.tfidf,
						prog.name, prog.nlpTools, prog.message);
			else if (prog.command == CMD_TRAIN)
				SVM.train(prog.labelledData, prog.unlabelledData,
						prog.testCorpus, prog.ratio, prog.threshold, prog.tf_idf_threshold,
						prog.featureSelection, prog.message);
			else if (prog.command == CMD_CLASSIFY) {
				Classifier.classify(prog.inData, prog.outData, prog.classifiers, prog.dictionary,
						prog.tf_idf_dictionary, prog.window, prog.modSize, prog.nlpTools, prog.message);
			}
		} catch (IOException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (LanguageException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (CorpusProcessorException e) {
			prog.message.displayTextln(e.getMessage());
			e.printStackTrace();
		} catch (InstantiationException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			prog.message.displayError(e.getMessage());
			e.printStackTrace();
		}
		finally {
			prog.message.close();
		}
	}
	
	/**
     * Prints the application usage
     */
    private void showHelp() {
    	System.err.println("Shows this screen: -h or --help");
    	System.err.println("");
    	System.err.println("Parse corpus in order to use SVMs");
//    	System.err.println("    -parse -l language -name corpusName -trainC trainCorpus");
//    	System.err.println("           -corpus corpus -seeds seedFile [OPTIONS...]");
    	System.err.println("    -parse -l language -name corpusName -trainC trainCorpus");
    	System.err.println("           -seeds seedFile [OPTIONS...]");
    	System.err.println("  ARGUMENTS:");
    	System.err.println("    -name      corpusName,       Corpus output name.");
    	System.err.println("    -trainC    trainCorpus,      Train corpus, can be a file or a directory.");
    	System.err.println("    -seeds     seedFile,         File with the seed-list.");
//    	System.err.println("    -corpus [conll | ester | ancora],  Corpus that will be parsed. There are 3 type of corpus");
//    	System.err.println("                                       supported: conll, ester and ancora.");
    	System.err.println("  OPTIONS:");
    	System.err.println("    -devC      devCorpus,        Dev corpus, can be a file or a directory.");
    	System.err.println("    -testC     testCorpus,       Test corpus, can be a file or a directory.");
    	System.err.println("    -window    windowSize,       Token window size for each entity,");
    	System.err.println("                                 default value is '3'.");
    	System.err.println("    -modSize   modifierSize,     How much modifiers will be used for NPs");
    	System.err.println("                                 default value is '5'.");
    	System.err.println("    -balanced                    Uses balanced classifier training files.");
    	System.err.println("                                 Without this flag each instance of each classifier");
    	System.err.println("                                 is added as negative instance to the other");
    	System.err.println("                                 classifiers, but with the '-balanced' flag,");
    	System.err.println("                                 each instance is added only to one other classifier.");
    	System.err.println("    -tfidf                       Creates tf-idf features.");
//    	System.err.println("    [NLP tool]                         Nlp tool to parse sentences:");
//    	System.err.println("     -tokenizer,                       there is only tokenization.");
//    	System.err.println("     -postagger,                       it is used postagging.");
//    	System.err.println("     -lemmatizer,                      it is used a lemmatizer.");
//    	System.err.println("     -chunker,                         it is used chunking.");
//    	System.err.println("     -constituentParser,               it is used constituent parsing.");
//    	System.err.println("     -lempos,                          both postagging and lemmatizer are used.");
//    	System.err.println("     -lemchunker,                      both lemmatizing and chunking are used.");
//    	System.err.println("     -poschunker,                      both postagging and chunking are used.");
//    	System.err.println("     -all,                             all nlp tools are used.");
//    	System.err.println("     -all_no_chunk,                    all nlp tools are used except chunking.");
//    	System.err.println("     -concat_pos,                      concatenated PoS forms: Det_Noun.");
//    	System.err.println("                                       Default option is '-all' .");
    	System.err.println("    [-all | -all_no_chunk]       -all:          all features are extracted,");
    	System.err.println("                                 -all_no_chunk: all features except chunking are extracted");
    	System.err.println("                                 Default option is '-all' .");
    	System.err.println("");
    	System.err.println("Train SVM classifiers");
    	System.err.println("    -train -labelled labelledData -unlabelled unlabelledData [OPTIONS...]");
    	System.err.println("  ARGUMENTS:");
    	System.err.println("    -labelled    labelledData,      A list of data files used to train the classifier.");
    	System.err.println("                                    A classifier will be learned for each labelled");
    	System.err.println("                                    data file.");
    	System.err.println("    -unlabelled  unlabelledData,    The data file used to boostrapping.");
    	System.err.println("  OPTIONS:");
    	System.err.println("    -testC       testCorpus,        Test corpus to output Precision, Recall and");
    	System.err.println("                                    F1-score during the boostrapping.");
    	System.err.println("    -ratio       negativeInstances, Positive:Negative instance ratio for labelled data,");
    	System.err.println("                                    the ratio is 1:'negativeInstances'.");
    	System.err.println("                                    Default value is '"+this.ratio+"'.");
    	System.err.println("    -th          threshold,         Beginning threshold to add instances from the");
    	System.err.println("                                    unlabelled data. Default value is '"+this.threshold+"'.");
//    	System.err.println("    -f                              Do feature selection, for default it is not used.");
    	System.err.println("    -tfidf       threshold,         Changes tf-idf threshold, default value is '"+this.tf_idf_threshold+"'.");
    	System.err.println("Classify SVM instances");
    	System.err.println("    -classify -in input.kaf -out output.kaf -models [model...] [OPTIONS...] -dic dictionary");
    	System.err.println("  ARGUMENTS:");
    	System.err.println("    -in         input.kaf,         Input KAF file with terms");
    	System.err.println("    -out        output.kaf,        Output KAF file");
    	System.err.println("    -models     [model...],        Model list to classify instances.");
    	System.err.println("    -dic        dictionary,        Binarized dictionary file ("+Utils.BINARIZED_DIC_EXT+" extension).");
    	System.err.println("  OPTIONS:");
    	System.err.println("    -window     windowSize,        Token window size for each entity used when training,");
    	System.err.println("                                   default value is '3'.");
    	System.err.println("    -modSize    modifierSize,      How much modifiers had for NPs when training.");
    	System.err.println("                                   default value is '5'.");
    	System.err.println("    -tfidf      dictionary,        Binarized tf-idf dictionary file ("+Utils.BINARIZED_TFIDF_EXT+" extension).");
//    	System.err.println("    [NLP tool]                         Nlp tool to parse sentences:");
//    	System.err.println("     -tokenizer,                       there is only tokenization.");
//    	System.err.println("     -postagger,                       it is used postagging.");
//    	System.err.println("     -lemmatizer,                      it is used a lemmatizer.");
//    	System.err.println("     -chunker,                         it is used chunking.");
//    	System.err.println("     -constituentParser,               it is used constituent parsing.");
//    	System.err.println("     -lempos,                          both postagging and lemmatizer are used.");
//    	System.err.println("     -lemchunker,                      both lemmatizing and chunking are used.");
//    	System.err.println("     -poschunker,                      both postagging and chunking are used.");
//    	System.err.println("     -all,                             all nlp tools are used.");
//    	System.err.println("     -all_no_chunk,                    all nlp tools are used except chunking.");
//    	System.err.println("     -concat_pos,                      concatenated PoS forms: Det_Noun.");
//    	System.err.println("                                       Default option is '-all' .");
    	System.err.println("    [-all | -all_no_chunk]         -all:          all features are extracted,");
    	System.err.println("                                   -all_no_chunk: all features except chunking are extracted");
    	System.err.println("                                   Default option is '-all' .");
	}

}
