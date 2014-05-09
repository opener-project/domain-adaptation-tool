package org.vicomtech.opener.svm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vicomtech.libsvm.Instances;
import org.vicomtech.libsvm.utils.svm;
import org.vicomtech.libsvm.utils.svm_model;
import org.vicomtech.libsvm.utils.svm_node;
import org.vicomtech.libsvm.utils.svm_parameter;
import org.vicomtech.opener.dictionary.TF_IDF;
import org.vicomtech.opener.dictionary.TF_IDF.TF_Type;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.utils.Utils;

/**
 * This class runs the bootstrapping process
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class SVM {

//	private FeatureSelection featureFilter = null;
	
	private List<Instances> labelledInstances;
	private Map<String,List<String>> tf_idf_map;
	private Map<String,Double> tf_idf_dictionary;
	private Instances unlabelledInstances;
	private Instances testInstances = null;
	private int numFeatures = 0;
	
	private List<Model> classifiers;
	private Set<Double> categoryLabels;
	
	private static int     TF_IDF_PERCENTAGE = -1;
	private static double  TF_IDF_THRESHOLD  = 0.55;
	private static TF_Type TF_TYPE           = TF_Type.LOG;
	
	private static double THRESHOLD = 0.95;
	private static double MAX_THRESHOLD = 0.95;
	private static final double MIN_THRESHOLD = 0.1;
	private static final double THRESHOLD_STEP = 0.05;
	private static final int MIN_INSTANCES = 100;
	
	private static final double EVALUATION_THRESHOLD = 0.4;
	
	/**
	 * Desired ratio for the train instances: 'ratio':1 (negative:positive)
	 */
	private static int RATIO = 2;
	
	public static final int NEGATIVE_CLASS = -1;
	
	/**
	 * The Message Handlers
	 */
	private MessageHandler message;
	
//	private File classifier;
	
	public SVM(List<File> labelledData, File unlabelledData,
			   File testData, int ratio, double threshold,
			   double tfidf_threshold, boolean featureSelection,
			   MessageHandler message) throws IOException {
		
		RATIO = ratio;
		THRESHOLD = threshold;
		MAX_THRESHOLD = threshold;
		TF_IDF_THRESHOLD = tfidf_threshold;
		
		// add message handler
		this.message = message;
		
		// initialize variables
		this.labelledInstances = new ArrayList<Instances>();
		this.classifiers = new ArrayList<Model>();
		
		// reads labeled instances from files
		this.message.displayHeader("LABELLED DATA");
		for (File data : labelledData) {
			this.message.displayTextln(data.getAbsolutePath());
			// add instances
			this.labelledInstances.add(this.getInstances(data));
		}
		this.message.displayTextln("");
		
		// get tf-idf tokens
		this.getTF_IDF_Map();
		// get tf-idf dictionary
		this.getTF_IDF_Dictionary();
		
		// add tf-idf features at labelled instances
		for (Instances instances : this.labelledInstances) {
			instances.addTF_IDF_Feature(this.tf_idf_map, this.tf_idf_dictionary);
		}
		
		// add unlabelled instances from file
		this.message.displayHeader("UNLABELLED DATA");
		this.message.displayTextln(unlabelledData.getAbsolutePath());
		this.message.displayTextln("");
		this.unlabelledInstances = new Instances(unlabelledData);
		this.unlabelledInstances.addTF_IDF_Feature(this.tf_idf_map, this.tf_idf_dictionary);
		
		// add test data
		if (testData != null) {
			this.message.displayHeader("TEST DATA");
			this.message.displayTextln(testData.getAbsolutePath());
			this.message.displayTextln("");
			this.testInstances = new Instances(testData);
			this.testInstances.addTF_IDF_Feature(this.tf_idf_map, this.tf_idf_dictionary);
		}
//		// add feature filter
//		if (featureSelection)
//			this.featureFilter = new FeatureSelection();
		
		// add threshold
	//	if (threshold > 0)
	//		THRESHOLD = threshold;
		
	}
	
	/**
	 * Public constructor, minimum arguments to classify instances
	 * @param classifiers : classifiers to classify instances
	 */
	public SVM(List<Model> classifiers) {
		this.classifiers = classifiers;
	}
	
	private void getTF_IDF_Map() throws IOException {
		Map<String,List<String>> tokensMap = new HashMap<String,List<String>>();
		for (Instances instances : this.labelledInstances) {
			List<String> tokens = new ArrayList<String>();
			for (String entity : instances.getPositiveEntities()) {
				tokens.addAll(Arrays.asList(entity.split(" ")));
			}
			tokensMap.put(Double.toString(instances.getPositiveLabel()), tokens);
		}
		
		if (TF_IDF_PERCENTAGE > 0) {
			this.tf_idf_map = TF_IDF.getTF_IDF_Map(tokensMap, TF_IDF_PERCENTAGE, TF_TYPE);
		}
		else {
			this.tf_idf_map = TF_IDF.getTF_IDF_Map(tokensMap, TF_IDF_THRESHOLD, TF_TYPE);
		}
	}
	
	private void getTF_IDF_Dictionary() {
		List<String> tokens = new ArrayList<String>();
		for (String category : this.tf_idf_map.keySet()) {
			for (String token : this.tf_idf_map.get(category)) {
				if (!tokens.contains(token)) {
					tokens.add(token);
				}
			}
		}
		
		this.tf_idf_dictionary = new HashMap<String,Double>();
		int size = tokens.size();
		double i=0.0;
		for (String tok : tokens) {
			Double value = new Double(i/size);
			this.tf_idf_dictionary.put(tok, new Double(value));
			i++;
		}
	}
	
	private Instances getInstances(File data) throws IOException {
		
		// get instances
		Instances inst = new Instances(data);
		
		// update max number of features
		if (this.numFeatures < inst.getNumFeatures())
			this.numFeatures = inst.getNumFeatures();
		
		return inst;
	}
	
	private void displayParameters() {
		this.message.displayHeader("PARAMETERS");
		this.message.displayTextln("THRESHOLD:         "+SVM.MAX_THRESHOLD);
		this.message.displayTextln("THRESHOLD_MIN:     "+SVM.MIN_THRESHOLD);
		this.message.displayTextln("THRESHOLD_STEP:    "+SVM.THRESHOLD_STEP);
		this.message.displayTextln("MIN INSTANCES:     "+SVM.MIN_INSTANCES);
		this.message.displayTextln("RATIO:             "+SVM.RATIO+":1");
		if (this.tf_idf_dictionary.size() > 0) {
			if (TF_IDF_PERCENTAGE > 0) {
//				this.message.displayTextln("TF TYPE:           "+TF_TYPE.toString());
				this.message.displayTextln("TF-IDF PERCENTAGE: "+TF_IDF_PERCENTAGE);
			}
			else if (TF_IDF_THRESHOLD > 0) {
//				this.message.displayTextln("TF TYPE:           "+TF_TYPE.toString());
				this.message.displayTextln("TF-IDF THRESHOLD:  "+TF_IDF_THRESHOLD);
			}
		}
		this.message.displayTextln("");
	}
	
	public static void train(List<File> labelledData, File unlabelledData,
							 File testData, int ratio, double threshold,
							 double tf_idf_threshold,  boolean featureSelection,
							 MessageHandler message) throws IOException {
		
		SVM svm = new SVM(labelledData, unlabelledData,
						  testData, ratio, threshold,
						  tf_idf_threshold, featureSelection, message);
		
		svm.displayParameters();
		
		// train classifiers
		svm.message.displayHeader("FIRST CLASSIFIERS");
		svm.train();
		
	/*	svm_node[] instance = new svm_node[16];
		
		instance[0] = new svm_node();
		instance[0].index = 1;
		instance[0].value = 0.4166146158650843;
		
		instance[1] = new svm_node();
		instance[1].index = 2;
		instance[1].value = 0.2146470955652717;
		
		instance[2] = new svm_node();
		instance[2].index = 3;
		instance[2].value = 0.9465021861336664;
		
		instance[3] = new svm_node();
		instance[3].index = 4;
		instance[3].value = 0.07092442223610243;
		
		instance[4] = new svm_node();
		instance[4].index = 5;
		instance[4].value = 0.24088069956277328;
		
		instance[5] = new svm_node();
		instance[5].index = 6;
		instance[5].value = 0.15474703310430982;
		
		instance[6] = new svm_node();
		instance[6].index = 7;
		instance[6].value = 0.41014990630855713;
		
		instance[7] = new svm_node();
		instance[7].index = 8;
		instance[7].value = 0.1664896939412867;
		
		instance[8] = new svm_node();
		instance[8].index = 9;
		instance[8].value = 0.309181761399126;
		
		instance[9] = new svm_node();
		instance[9].index = 10;
		instance[9].value = 0.16158650843222985;
		
		instance[10] = new svm_node();
 		instance[10].index = 11;
		instance[10].value = 0.4166146158650843;
		
		instance[11] = new svm_node();
		instance[11].index = 12;
		instance[11].value = 0.4166146158650843;
		
		instance[12] = new svm_node();
		instance[12].index = 13;
		instance[12].value = 0.4166146158650843;
		
		instance[13] = new svm_node();
		instance[13].index = 14;
		instance[13].value = 0.4166146158650843;
		
		instance[14] = new svm_node();
		instance[14].index = 15;
		instance[14].value = 0.4166146158650843;
		
		instance[15] = new svm_node();
		instance[15].index = 16;
		instance[15].value = 0.4166146158650843;
		
		
		double category = svm.classify(instance, false);
		System.out.println(category);
		System.out.println(category);
		System.out.println(category);
		System.out.println(category);
		*/
		
		// boostrapping
		svm.boostrapping();
		
		// save models
		svm.saveModels();
	}
	
	private void train() throws IOException {
		
		this.message.displayText("Training classifiers..........");
		
		// remove classifiers
		this.removeClassifiers();
		this.categoryLabels = new HashSet<Double>();
		
		// for each labeled instance list train a C-SVC classifier
		for (Instances inst : this.labelledInstances) {
			
			// get output classifier file
			File classifierFile = new File(Utils.addModelExtension(
					Utils.getFileWhithoutExtension(inst.getSourceFile())));
			
			// train classifier
			svm_model model = this.train(inst);
			Model classifier = new Model(model, classifierFile);
			
			// add classifier
			this.classifiers.add(classifier);
			
			// add label to list (for Evaluator class)
			this.categoryLabels.add(classifier.getLabel());
			
		}
		
		this.message.displayTextln("");
		
		// evaluate classifiers
		this.evaluate();
	}
	
	private svm_model train(Instances inst) throws IOException {
		
		// get parameters to train a C-SVC
		svm_parameter params = C_SVC.createParameters(this.numFeatures);
		
		// train the C-SVC classifier
		svm_model classifier = svm.svm_train(inst.getInstances(),
				params, false);
		
		// return model
		return classifier;
		
	}
	
	private void boostrapping() throws IOException {
		
		this.message.displayHeader("BOOTSTRAPPING");
		
		int iteration=1;
		while (true) {
			
			this.message.displayText("ITERATION: "+iteration);
			
			// mapping to count added instance count by category
			Map<Double,Integer> contHash = new HashMap<Double,Integer>();
			// mapping to count how many added instances are predicted correctly
			Map<Double,Integer> correctContHash = new HashMap<Double,Integer>();
			int cont = 0;
			int correctCont = 0;
			int total = this.unlabelledInstances.size();
			// for each unlabeled instance
			for (int j=0; j<this.unlabelledInstances.size(); j++) {
				
				// initialize variables
				svm_node[] instance = this.unlabelledInstances.getInstance(j);
				
				// predict category
				double category = this.unlabelledInstances.getCategory(j);
				double predictedCategory = classifyByProbability(this.classifiers, instance, true);
				
				if (predictedCategory != SVM.NEGATIVE_CLASS) {
					// add instance and count
					this.addUnlabelled(j, predictedCategory);
					j--;
					try {
						contHash.put(predictedCategory, contHash.get(predictedCategory)+1);
					}
					catch (NullPointerException ex) {
						contHash.put(predictedCategory, 0);
						contHash.put(predictedCategory, contHash.get(predictedCategory)+1);
					}
					cont++;
					
					// count correctly added category
					if (predictedCategory == category) {
						try {
							correctContHash.put(predictedCategory, correctContHash.get(predictedCategory)+1);
						}
						catch (NullPointerException ex) {
							correctContHash.put(predictedCategory, 0);
							correctContHash.put(predictedCategory, correctContHash.get(predictedCategory)+1);
						}
						correctCont++;
					}
				}
				
				if (Utils.isMultiple(j, 300))
					this.message.displayText(".");
			}
			
			// update tf-idf tokens
			this.getTF_IDF_Map();
			// update tf-idf dictionary
			this.getTF_IDF_Dictionary();
			
			// update tf-idf features at labelled instances
			for (Instances instances : this.labelledInstances) {
				instances.updateTF_IDF_Feature(this.tf_idf_map, this.tf_idf_dictionary);
			}
			
			this.unlabelledInstances.updateTF_IDF_Feature(this.tf_idf_map, this.tf_idf_dictionary);
			
			// add test data
			if (this.testInstances != null) {
				this.testInstances.updateTF_IDF_Feature(this.tf_idf_map, this.tf_idf_dictionary);
			}
			
			// force 3:1 (negative:positive) instance ratio at labelled instances
			this.forceRatio();
			
			this.printStatistics(contHash, correctContHash, cont, correctCont, total);
			
			// if threshold is minimum and there are not new instances break
			if (cont == 0 && SVM.THRESHOLD <= SVM.MIN_THRESHOLD) {
				break;
			}
			
			// slide threshold
			if (cont < SVM.MIN_INSTANCES && SVM.THRESHOLD > SVM.MIN_THRESHOLD)
				this.downThreshold();
			else if (cont > SVM.MIN_INSTANCES)
				this.upThreshold();
			
			this.message.displayTextln("New threshold: "+SVM.THRESHOLD);
			this.message.displayTextln("");
			
			// train new classifiers
			if (cont > 0) this.train();
			
			// increase iteration
			iteration++;
		}
		
	}
	
	private void evaluate() {
		
		// if test data exists
		if (this.testInstances != null) {
			
			this.message.displayText("Evaluation");
			
			Evaluator evaluator = new Evaluator(this.categoryLabels);
			
			// initialize scores
		/*	int TP = 0;
			int FP = 0;
			int TN = 0;
			int FN = 0;
			int TOTAL = this.testInstances.size();*/
			
			// for each test instance
			for (int i=0; i<this.testInstances.size(); i++) {
				
				// initialize variables
				svm_node[] instance = this.testInstances.getInstance(i);
				
				// predict category
				double category = this.testInstances.getCategory(i);
				if (category == SVM.NEGATIVE_CLASS) {
					System.in.toString();
				}
				double predictedCategory = classifyByProbability(this.classifiers, instance, false);
				
				
				//System.out.println("predicted: "+predictedCategory);
				//System.out.println("category: "+category);
				
				//this.message.displayDivider();
				//this.message.displayDivider();
				//this.message.displayDivider();
				
				if (Utils.isMultiple(i, 300))
					this.message.displayText(".");
				
				// compute statistics
		/*		if (predictedCategory == SVM.NEGATIVE_CLASS)
					FN++;
				else if (predictedCategory == category)
					TP++;
				else
					FP++;
				TN = 0;*/
				evaluator.evaluate(category, predictedCategory);
			}
			this.message.displayTextln("");
			this.message.displayTextln("");
			
		/*	double precision = 0.0;
			if ((TP + FP) != 0)
				precision = TP / (double)(TP + FP);
			double recall = 0.0;
			if ((TP + FN) != 0)
				recall = TP / (double)(TP + FN);
			double f1 = 0.0;
			if ((precision + recall) != 0)
				f1 = 2 * ( (precision*recall) / (precision+recall) );
			*/
		/*	this.message.displayTextln("True positives: "+TP);
			this.message.displayTextln("False positives: "+FP);
			this.message.displayTextln("False negative: "+FN);
			this.message.displayTextln("True negative: "+TN);
			this.message.displayTextln("TOTAL: "+TOTAL);
			this.message.displayTextln("");
			this.message.displayTextln("Precision: "+precision);
			this.message.displayTextln("Recall: "+recall);
			this.message.displayTextln("F score: "+f1);
			*/
			
			
			evaluator.printStatistics(message);
			this.message.displayTextln(" ");
			this.message.displayDivider();
			
		}
	}
	
	private static double classifyByDistance(List<Model> classifiers,
											 svm_node[] instance,
											 boolean threshold) {
		double[] distances = new double[classifiers.size()];
		double[] categories = new double[classifiers.size()];
		double[] dec_values = new double[1];
		
		// for each classifier
		for (int j=0; j<classifiers.size(); j++) {
			
			// get classifier
			Model classifier = classifiers.get(j);
			
//			// feature filtering
//			if (this.featureFilter != null) {
//				instance = this.featureFilter.filterFeatures(instance, classifier.getLabel());
//			}
			
			categories[j] = svm.svm_predict_values(
					classifier.getClassifier(),
					instance, dec_values);
			
			// get distance
			if (categories[j] == SVM.NEGATIVE_CLASS)
				distances[j] = 0;
			else
				distances[j] = svm.getDistance(classifier.getClassifier(), dec_values);
		}
		
		// get category
		double predictedCategory = 
				getCategory(categories, distances, threshold);
		return predictedCategory;

	}
	
	public static double classifyByProbability(List<Model> classifiers,
											   svm_node[] instance,
											   boolean threshold) {
		double[] categories = new double[classifiers.size()];
		double[] prob_estimates = new double[classifiers.size()];
		
		// for each classifier
		for (int j=0; j<classifiers.size(); j++) {
			
			// get classifier
			Model classifier = classifiers.get(j);
			
//			// feature filtering
//			if (this.featureFilter != null) {
//				instance = this.featureFilter.filterFeatures(instance, classifier.getLabel());
//			}
			
			double[] probs = new double[2];
			categories[j] = svm.svm_predict_probability(
					classifier.getClassifier(), instance, probs);
			
			// get probability
			if (categories[j] == SVM.NEGATIVE_CLASS)
				prob_estimates[j] = 0;
			else
				prob_estimates[j] = probs[classifier.getPositiveCategoryIndex()];
		}
		
		// get category
		double predictedCategory = 
				getCategory(categories, prob_estimates, threshold);
		return predictedCategory;

	}
	
	private void addUnlabelled(int index, double category) {
		
		// update unlabelled data
		svm_node[] instance =
				this.unlabelledInstances.removeInstance(index);
		String entity = this.unlabelledInstances.removeEntity(index);
		
		// update labelled data
		for (int i=0; i<this.classifiers.size(); i++) {
			
			// get classifier and labelled instances
			Model classifier = this.classifiers.get(i);
			Instances instances = this.labelledInstances.get(i);
			
			// add instance at target labelled instances,
			//  and add at other labelled instances 
			//  with negative category
			if (classifier.getLabel() == category) {
				instances.addInstance(instance, category);
				if (entity != null) {
					instances.addEntity(entity);
					instances.addPositiveEntity(entity);
				}
			}
			else {
				instances.addInstance(instance, SVM.NEGATIVE_CLASS);
				if (entity != null) {
					instances.addEntity(entity);
				}
			}
		}
	}
	
	/**
	 * Forces train instances to the 'ratio':1 (negative:positive) ratio
	 * The ratio is at global variable 'ratio'
	 */
	private void forceRatio() {
		for (Instances instances : this.labelledInstances) {
			// while ratio is not correct
//			System.out.println("category: "+instances.getPositiveLabel());
//			System.out.println("positive: "+instances.positiveInstances());
//			System.out.println("negative: "+instances.negativeInstances());
			while (instances.positiveInstances()*RATIO < instances.negativeInstances()) {
				// remove last negative instance
				for (int i=instances.size()-1; i>=0; i--) {
					if (instances.getCategory(i) < 0) {
						instances.removeInstance(i);
						break;
					}
				}
			}
//			System.out.println("positive: "+instances.positiveInstances());
//			System.out.println("negative: "+instances.negativeInstances());
//			System.out.println();
		}
	}
	
	private static double getCategory(double[] categories, 
									  double[] distances,
									  boolean threshold) {
		// get max score index
		int maxIndex = getMaxScoreIndex(distances);
		if (maxIndex > -1)
			
			// check threshold
			if (threshold)
				return passThreshold(
						categories, distances, maxIndex, THRESHOLD);
			else
				//TODO optimize evaluation threshold
				return passThreshold(
						categories, distances, maxIndex, EVALUATION_THRESHOLD);
//				return categories[maxIndex];
		else
			return SVM.NEGATIVE_CLASS;
	}
	
	private static double passThreshold(double[] categories, 
			double[] distances, int maxIndex, double threshold) {
		
		// get score
		double score = distances[maxIndex];
		
		// get max score of other classifiers
		double maxOtherScore = getMaxOtherScore(
				distances, maxIndex);
		
		// get confidence
		double confidence = score - maxOtherScore;
		
		// check threshold
		if (confidence >= threshold)
			return categories[maxIndex];
		else
			return SVM.NEGATIVE_CLASS;
	}
	
	private void upThreshold() {
		SVM.THRESHOLD = SVM.MAX_THRESHOLD;
	}
	
	private void downThreshold() {
		SVM.THRESHOLD -= SVM.THRESHOLD_STEP;
	/*	String s = Double.toString(SVM.MAX_THRESHOLD);
		String fPart = s.substring(s.indexOf(".")+1, s.length());
		int decimals = fPart.length();*/
		SVM.THRESHOLD = Utils.round(SVM.THRESHOLD, 2);
	}
	
	private static int getMaxScoreIndex(double[] classifierProbs) {
		
		int maxIndex = -1;
		double maxScore = -1;
		for (int i=0; i<classifierProbs.length; i++) {
			double prob = classifierProbs[i];
			if (prob > maxScore) {
				maxScore = prob;
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	private static double getMaxOtherScore(
			double[] classifierProbs, int scoreIndex) {
		
		double maxOtherScore = -1;
		for (int i=0; i<classifierProbs.length; i++) {
			double prob = classifierProbs[i];
			if (i != scoreIndex && prob > maxOtherScore)
				maxOtherScore = prob;
		}
		return maxOtherScore;
	}
	
	private void removeClassifiers() {
		
		if (this.classifiers.size() != 0) {
			this.classifiers.clear();
			this.classifiers = new ArrayList<Model>();
		}
	}
	
	/**
	 * Write classifiers and tf-idf dictionary
	 * @throws IOException
	 */
	public void saveModels() throws IOException {
		this.message.displayHeader("SAVING CLASSIFIERS");
		for (Model classifier : this.classifiers) {
			String filePath = classifier.getClassifierFile().getAbsolutePath();
			svm_model model = classifier.getClassifier();
			svm.svm_save_model(filePath, model);
			this.message.displayTextln(String.format("Classifier '%s' wrote", filePath));
		}
		
		if (this.tf_idf_dictionary.size() > 0) {
			this.message.displayHeader("SAVING TF-IDF DICTIONARY");
			File file = this.classifiers.get(0).getClassifierFile();
			
			String filePath = file.getAbsolutePath();
			filePath = filePath.substring(0, filePath.lastIndexOf("."));
			filePath = filePath.substring(0, filePath.lastIndexOf("."));
			
			TF_IDF tfidf = new TF_IDF(this.tf_idf_map, this.tf_idf_dictionary);
			this.message.displayTextln(String.format("TF-IDF dictionary '%s' wrote",
					tfidf.write(filePath)));
			this.message.displayTextln(String.format("TF-IDF binarized dictionary '%s' wrote",
					tfidf.binarize(filePath)));
		}
		
		
	}

	/**
	 * Prints statistics about added instances from unlabelled dataset
	 * @param contHash : added instances by category
	 * @param correctContHash : correctly added instances by category
	 * @param totAdded : total added instances
	 * @param totCorrectAdded : total correctly added instances
	 * @param total : total instances
	 */
	public void printStatistics(Map<Double,Integer> contHash,
			Map<Double,Integer> correctContHash, int totAdded, int totCorrectAdded, int total) {
		
		this.message.displayTextln("");
		this.message.displayTextln("");
		this.message.displayTextln(this.header());
		
		for (Double label : this.categoryLabels) {
			
			int added = 0;
			if (contHash.get(label) != null)
				added = contHash.get(label);
			
			int correctAdded = 0;
			if (correctContHash.get(label) != null)
				correctAdded = correctContHash.get(label);
			
			int size = 0;
			try {
				this.unlabelledInstances.labelSize(label);
			}
			catch (NullPointerException ex) {
				size = 0;
			}
			
			this.message.displayTextln(
					this.line(label.toString(), added, correctAdded, size));
		}
		
		this.message.displayTextln(this.line("TOTAL", totAdded, totCorrectAdded, total));
	}
	
	private String header() {
		if (this.testInstances != null) {
			return "| Category | Added | Correct |  Total |";
		}
		else {
			return "| Category | Added |  Total |";
		}
	}
	
	private String line(String label, int added, int correctAdded, int total) {
		
		String line;
		if (label.equalsIgnoreCase("TOTAL"))
			line = "|    "+label+" |  ";
		else
			line = "|      "+label+" |  ";
		
		// added-----------------------------
		String s = Integer.toString(added);
		
		int l = 4-s.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
		line += added+" | ";
		
		// correct-----------------------------
		if (this.testInstances != null) {
			s = Integer.toString(correctAdded);
			
			l = 7-s.length();
			for (int i=0; i<l; i++) {
				line += " ";
			}
			line += correctAdded+" | ";
		}
		
		// total-------------------------------
		s = Integer.toString(total);
		
		l = 6-s.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
		
		line += total+" |";
		
		return line;
	}
}
