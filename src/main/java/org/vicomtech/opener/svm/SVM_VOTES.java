package org.vicomtech.opener.svm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.utils.Utils;

/**
 * This class run the bootstrapping process with classifier votes
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class SVM_VOTES {

	private FeatureSelection featureFilter = null;
	
	private List<Instances> labeledInstances;
	private Instances unlabeledInstances;
	private Instances testInstances = null;
	private int numFeatures = 0;
	
	private List<Model> classifiers;
	private Model probClassifier;
	private Set<Double> categoryLabels;
	
	private static double VOTE_DIFF = 1.8;
	private static final double MAX_VOTE_DIFF = 1.8;
	private static final double MIN_VOTE_DIFF = 1.5;
	private static final double VOTE_DIFF_STEP = 0.05;
	private static final int MIN_INSTANCES = 100;
	
	
	private static double THRESHOLD = 0.95;
	private static final double MAX_THRESHOLD = 0.95;
	private static final double MIN_THRESHOLD = 0.5;
	private static final double THRESHOLD_STEP = 0.05;
	
	/**
	 * Desired ratio for the train instances: 'ratio':1 (negative:positive)
	 */
	private static final int RATIO = 1;
	
	protected static final int NEGATIVE_CLASS = -1;
	
	/**
	 * The Message Handlers
	 */
	private MessageHandler message;
	
//	private File classifier;
	
	public SVM_VOTES(List<File> labeledData, File unlabeledData,
			File testData, double threshold, boolean featureSelection, MessageHandler message) throws IOException {
		
		// initialize variables
		this.labeledInstances = new ArrayList<Instances>();
		this.classifiers = new ArrayList<Model>();
		
		// reads labeled instances from files
		for (File data : labeledData)
			// add instances
			this.labeledInstances.add(this.getInstances(data));
		
		// add unlabeled instances from file
		this.unlabeledInstances = new Instances(unlabeledData);
		
		// add test data
		if (testData != null)
			this.testInstances = new Instances(testData);
		
		// add feature filter
		if (featureSelection)
			this.featureFilter = new FeatureSelection();
		
		// add threshold
	//	if (threshold > 0)
	//		THRESHOLD = threshold;
		
		// add message handler
		this.message = message;
	}
	
	private Instances getInstances(File data) throws IOException {
		
		// get instances
		Instances inst = new Instances(data);
		
		// update max number of features
		if (this.numFeatures < inst.getNumFeatures())
			this.numFeatures = inst.getNumFeatures();
		
		return inst;
	}
	
	public static void train(List<File> labeledData, File unlabeledData,
			File testData, double threshold, boolean featureSelection, MessageHandler message) throws IOException {
		
		SVM_VOTES svm = new SVM_VOTES(labeledData, unlabeledData, testData,
				threshold, featureSelection, message);
		
		System.out.println("VOTE_DIFF: "+SVM_VOTES.MAX_VOTE_DIFF);
		System.out.println("VOTE_DIFF_MIN: "+SVM_VOTES.MIN_VOTE_DIFF);
		System.out.println("VOTE_DIFF_STEP: "+SVM_VOTES.VOTE_DIFF_STEP);
		System.out.println("MIN INSTANCES: "+SVM_VOTES.MIN_INSTANCES);
//		System.out.println("THRESHOLD: "+SVM2.MAX_THRESHOLD);
//		System.out.println("THRESHOLD_MIN: "+SVM2.MIN_THRESHOLD);
//		System.out.println("THRESHOLD_STEP: "+SVM2.THRESHOLD_STEP);
//		System.out.println("MIN INSTANCES: "+SVM2.MIN_INSTANCES);
		System.out.println("RATIO: "+SVM_VOTES.RATIO+":1");
		System.out.println();
		
		// train classifiers
		svm.train();
		
		// boostrapping
		svm.boostrapping();
		
		// save models
		svm.saveModels();
	}
	
	private void train() throws IOException {
		
		this.message.displayText("Training classifiers");
		
		// remove classifiers
		this.removeClassifiers();
		this.categoryLabels = new HashSet<Double>();
		
		// for each labeled instance list train a C-SVC classifier
		for (Instances inst : this.labeledInstances) {
			
			// get output classifier file
			File classifierFile = new File(Utils.addModelExtension(
					Utils.getFileWhithoutExtension(inst.getSourceFile())));
			
			// train classifier
			svm_model model = this.train(inst);
			Model classifier = new Model(model, classifierFile);
			
			// add classifier
			this.classifiers.add(classifier);
			
			// add label to list (for Evaluator class)
			for (int l : classifier.getLabels()) {
				if (l != (int) SVM.NEGATIVE_CLASS
						&& !this.categoryLabels.contains(l))
					this.categoryLabels.add((double) l);
			}
			
		}
		
	//	this.trainProbClassifier();
		
		this.message.displayTextln("");
		
		// evaluate classifiers
		this.evaluate();
	}
	
	private void trainProbClassifier() throws IOException {
		
		Instances instances = this.createProbInstances();
	/*	for (int i=0; i<instances.size(); i++) {
			double category = instances.getCategory(i);
			svm_node[] instance = instances.getInstance(i);
			System.out.print(category+" ");
			for (svm_node node : instance) {
				System.out.print(node.index+":"+node.value+" ");
			}
			System.out.println();
		}*/
		
		svm_model classifier = this.trainProbClassifier(instances);
		this.probClassifier = new Model(classifier, null);
	}
	
	private Instances createProbInstances() {
		
		Instances probInstances = new Instances(this.classifiers.size()*2);
		
		Set<Instances> classified = new HashSet<Instances>();
		// get instances
		for (Instances instances : this.labeledInstances) {
			if (!classified.contains(instances)) {
				for (int i=0; i<instances.size(); i++) {
					
					double category = instances.getCategory(i);
					svm_node[] instance = instances.getInstance(i);
					svm_node[] probNodes = this.createProbNodes(instance);
					
					// add probability instance
					probInstances.addInstance(probNodes, category);
				}
				classified.add(instances);
			}
		}
		return probInstances;
	}
	
	private svm_node[] createProbNodes(svm_node[] instance) {
		
		int index = 1;
		svm_node[] probNodes = new svm_node[this.classifiers.size()*2];
		
		// for each classifier
		for (int j=0; j<this.classifiers.size(); j++) {
			
			// get classifier
			Model classifier = this.classifiers.get(j);
			
			// get probabilities
			double[] probs = new double[2];
			svm.svm_predict_probability(classifier.getClassifier(), instance, probs);
		
			// add probability nodes
			for (double prob : probs) {
				svm_node node = new svm_node();
				node.index = index;
				node.value = prob;
				probNodes[index-1] = node;
				index++;
			}
		}
		return probNodes;
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
	
	private svm_model trainProbClassifier(Instances inst) throws IOException {
		
		// get parameters to train a C-SVC
		svm_parameter params = C_SVC.createProbParameters(this.classifiers.size()*2);
		
		// train the C-SVC classifier
		svm_model classifier = svm.svm_train(inst.getInstances(),
				params, false);
		
		// return model
		return classifier;
		
	}
	
	private void boostrapping() throws IOException {
		
		this.message.displayTextln("BOOSTRAPPING");
		this.message.displayDivider();
		
		int iteration=1;
		while (true) {
			
			this.message.displayText("ITERATION: "+iteration);
			
			// mapping to count added instance count by category
			Map<Double,Integer> contHash = new HashMap<Double,Integer>();
			// mapping to count how many added instances are predicted correctly
			Map<Double,Integer> correctContHash = new HashMap<Double,Integer>();
			// mapping to store removed instances from unlabeled instances
			Map<Double,List<svm_node[]>> removedInstances = new HashMap<Double,List<svm_node[]>>();
			int cont = 0;
			int correctCont = 0;
			int total = this.unlabeledInstances.size();
			// for each unlabeled instance
			for (int j=0; j<this.unlabeledInstances.size(); j++) {
				
				// initialize variables
				svm_node[] instance = this.unlabeledInstances.getInstance(j);
				
				// predict category
				double predictedCategory = this.classifyByProbability(instance, true);
				double category = this.unlabeledInstances.getCategory(j);
				
				if (predictedCategory != SVM_VOTES.NEGATIVE_CLASS) {
					// add instance and count
					this.unlabeledInstances.removeInstance(j);
					try {
						removedInstances.get(predictedCategory).add(instance);
					}
					catch (NullPointerException ex) {
						List<svm_node[]> list = new ArrayList<svm_node[]>();
						removedInstances.put(predictedCategory, list);
						removedInstances.get(predictedCategory).add(instance);
					}
					//this.addUnlabeled(j, predictedCategory);
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
			
			this.printStatistics(contHash, correctContHash, cont, correctCont, total);
			
			// if threshold is minimum and there are not new instances break
			if (cont == 0 && SVM_VOTES.VOTE_DIFF <= SVM_VOTES.MIN_VOTE_DIFF) {
	//		if (cont == 0 && SVM2.THRESHOLD <= SVM2.MIN_THRESHOLD) {
				break;
			}
			
			// slide threshold
			if (cont < SVM_VOTES.MIN_INSTANCES && SVM_VOTES.VOTE_DIFF > SVM_VOTES.MIN_VOTE_DIFF)
	//		if (cont < SVM2.MIN_INSTANCES && SVM2.THRESHOLD > SVM2.MIN_THRESHOLD)
				this.downThreshold();
			else if (cont > SVM_VOTES.MIN_INSTANCES)
				this.upThreshold();
			
			this.message.displayTextln("New threshold: "+SVM_VOTES.VOTE_DIFF);
	//		this.message.displayTextln("New threshold: "+SVM2.THRESHOLD);
			this.message.displayTextln("");
			
			// force 3:1 (negative:positive) instance ratio at labeled instances
			this.addUnlabeled(removedInstances);
			
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
				double predictedCategory = this.classifyByProbability(instance, false);
				double category = this.testInstances.getCategory(i);
				
	//			System.out.println("predicted: "+predictedCategory);
	//			System.out.println("category: "+category);
				
	//			this.message.displayDivider();
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
	//		System.exit(0);
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
	
	private double classifyByDistance(svm_node[] instance, boolean threshold) {
		
		double[] distances = new double[this.classifiers.size()];
		double[] categories = new double[this.classifiers.size()];
		double[] dec_values = new double[1];
		
		// for each classifier
		for (int j=0; j<this.classifiers.size(); j++) {
			
			// get classifier
			Model classifier = this.classifiers.get(j);
			
			// feature filtering
			if (this.featureFilter != null) {
				instance = this.featureFilter.filterFeatures(instance, classifier.getLabel());
			}
			
			categories[j] = svm.svm_predict_values(
					classifier.getClassifier(),
					instance, dec_values);
			
			// get distance
			if (categories[j] == SVM.NEGATIVE_CLASS)
				distances[j] = 0;
			else
				distances[j] = svm.getDistance(classifier.getClassifier(), dec_values);
			
			//System.out.println("distance: "+distances[j]);
			//System.out.println("category: "+categories[j]);
			//this.message.displayDivider();
		}
		
		//this.message.displayDivider();
		//this.message.displayDivider();
		
		// get category
		double predictedCategory = 
				this.getCategory(categories, distances, threshold);
		return predictedCategory;

	}
	
	private double classifyByProbability(svm_node[] instance, boolean threshold) {
		
		double[] categories = new double[this.classifiers.size()];
		double[] prob_estimates = new double[this.classifiers.size()];
		
		// for each classifier
		for (int j=0; j<this.classifiers.size(); j++) {
			
			// get classifier
			Model classifier = this.classifiers.get(j);
			
			// feature filtering
			if (this.featureFilter != null) {
				instance = this.featureFilter.filterFeatures(instance, classifier.getLabel());
			}
			
			double[] probs = new double[2];
			categories[j] = svm.svm_predict_probability(
					classifier.getClassifier(), instance, probs);
			
			// get probability
			if (categories[j] == SVM.NEGATIVE_CLASS)
				prob_estimates[j] = 0;
			else
				prob_estimates[j] = probs[classifier.getLabelIndex(categories[j])];
				//prob_estimates[j] = probs[classifier.getPositiveCategoryIndex()];
			
	//		for (int i=0; i<classifier.getLabels().length; i++){
	//			System.out.println("prob: "+probs[i]);
	//			System.out.println("category: "+classifier.getLabels()[i]);
	//		}
	//		System.out.println("classifier prediction: "+categories[j]);
	//		System.out.println();
			//this.message.displayDivider();
			
		}
		//this.message.displayDivider();
		//this.message.displayDivider();
		
		// get category
		double predictedCategory = 
				this.getCategory(categories, prob_estimates, threshold);
		return predictedCategory;

	}
	
	private double classifyByProbClassifier(svm_node[] instance, boolean threshold) {
		
		// create probability instance
		svm_node[] probInstance = this.createProbNodes(instance);
		
		double[] probs = new double[3];
		double predictedCategory = svm.svm_predict_probability(
				this.probClassifier.getClassifier(), probInstance, probs);
		double probability = probs[this.probClassifier.getLabelIndex(predictedCategory)];
//		System.out.println(probability);
		if (threshold && probability < SVM_VOTES.THRESHOLD)
			return SVM_VOTES.NEGATIVE_CLASS;
		else
			return predictedCategory;
	}
	
	/*private void addUnlabeled(int index, double category) {
		
		// update unlabeled data
		svm_node[] instance =
				this.unlabeledInstances.removeInstance(index);
		
		// update labeled data
		for (int i=0; i<this.classifiers.size(); i++) {
			
			// get classifier and labeled instances
			Model classifier = this.classifiers.get(i);
			Instances instances = this.labeledInstances.get(i);
			
			// add instance at target labeled instances
			for (int label : classifier.getLabels()) {
				if (label == (int) category) {
					instances.addInstance(instance, category);
					break;
				}
			}
		}
	}*/
	
	/**
	 * Adds removed instances to the labeled data
	 * @param removedInstances : removed instances from unlabeled data
	 */
	private void addUnlabeled(Map<Double,List<svm_node[]>> removedInstances) {
		
	//	removedInstances = this.forceRatio(removedInstances);
		
		// update labeled data
		for (int i=0; i<this.classifiers.size(); i++) {
					
			// get classifier and labeled instances
			Model classifier = this.classifiers.get(i);
			Instances instances = this.labeledInstances.get(i);
					
			// for each label of the classifier, add instances from unlabeled dataset
			//   with the same label
			for (int label : classifier.getLabels()) {
				if (removedInstances.containsKey((double) label)) {
					for (svm_node[] instance : removedInstances.get((double) label)) {
						instances.addInstance(instance, label);
					}
				}
			}
		}
		/*
		for (Instances instances : this.labeledInstances) {
		//	int minSize = 
			
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
		}*/
	}
	
	/**
	 * Forces removed instances from unlabeled data to the 'ratio':1 (negative:positive) ratio
	 * The ratio is at global variable 'RATIO'. Instances discarded because of the ratio are
	 * reinserted at unlabeled data
	 * @param removedInstances : removed instances from unlabeled data
	 * @return
	 */
	private Map<Double,List<svm_node[]>> forceRatio(Map<Double,List<svm_node[]>> removedInstances) {
		
		// get min length
		int minLength = Integer.MAX_VALUE;
		for (Double d : removedInstances.keySet()) {
			int length = removedInstances.get(d).size();
			if (minLength > length) {
				minLength = length;
			}
		}
		
		// get max length for every label
		int maxLength = minLength * RATIO;
		
		// force ratio and add to unlabeled data discarded instances
		for (Double d : removedInstances.keySet()) {
			while (removedInstances.get(d).size() > maxLength) {
				svm_node[] instance = removedInstances.get(d).remove(0);
				this.unlabeledInstances.addInstance(instance, d);
			}
		}
		
		return removedInstances;
	}
	
	private double getCategory(double[] categories, 
			double[] probabilities, boolean threshold) {
		
//		// get max score index
//		int maxIndex = this.getMaxScoreIndex(probabilities);
//		if (maxIndex > -1)
//			
//			// check threshold
//			if (threshold)
//				return this.passThreshold(
//						categories, probabilities, maxIndex);
//			else
//				return categories[maxIndex];
//		else
//			return SVM.NEGATIVE_CLASS;
			
		Map<Double,Double> votes = new HashMap<Double,Double>();
		for (int i=0; i<categories.length; i++) {
			double vote = categories[i];
			double prob = probabilities[i];
			try {
				votes.put(vote, votes.get(vote)+prob);
			}
			catch (Exception ex) {
				votes.put(vote, prob);
			}
		}
		double maxCategory = SVM_VOTES.NEGATIVE_CLASS;
		double maxVotes = -1;
		for (Double category : votes.keySet()) {
			double value = votes.get(category);
			if (value > maxVotes) {
				maxVotes = value;
				maxCategory = category;
			}
		}
	//	System.out.println("maxProb: "+maxVotes);
		if (threshold)
			if (maxVotes > SVM_VOTES.VOTE_DIFF)
				return maxCategory;
			else
				return SVM_VOTES.NEGATIVE_CLASS;
		else
			if (maxVotes > SVM_VOTES.MIN_VOTE_DIFF)
				return maxCategory;
			else
				return SVM_VOTES.NEGATIVE_CLASS;
	}
	
	private double passThreshold(double[] categories, 
			double[] distances, int maxIndex) {
		
		// get score
		double score = distances[maxIndex];
		
		// get max score of other classifiers
		double maxOtherScore = this.getMaxOtherScore(
				distances, maxIndex);
		
		// get confidence
		double confidence = score - maxOtherScore;
		
		// check threshold
		if (confidence >= VOTE_DIFF)
			return categories[maxIndex];
		else
			return SVM.NEGATIVE_CLASS;
	}
	
	private void upThreshold() {
		SVM_VOTES.VOTE_DIFF = SVM_VOTES.MAX_VOTE_DIFF;
	//	SVM2.THRESHOLD = SVM2.MAX_THRESHOLD;
	}
	
	private void downThreshold() {
		SVM_VOTES.VOTE_DIFF -= SVM_VOTES.VOTE_DIFF_STEP;
	//	SVM2.THRESHOLD -= SVM2.THRESHOLD_STEP;
		SVM_VOTES.VOTE_DIFF = Utils.round(SVM_VOTES.VOTE_DIFF, 2);
	//	SVM2.THRESHOLD = Utils.round(SVM2.THRESHOLD, 2);
	}
	
	private int getMaxScoreIndex(double[] classifierProbs) {
		
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

	private double getMaxOtherScore(
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
	 * Write classifiers
	 * @throws IOException
	 */
	public void saveModels() throws IOException {
		for (Model classifier : this.classifiers) {
			String filePath = classifier.getClassifierFile().getAbsolutePath();
			svm_model model = classifier.getClassifier();
			svm.svm_save_model(filePath, model);
		}
	}

	/**
	 * Prints statistics about added instances from unlabeled dataset
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
			
			this.message.displayTextln(
					this.line(label.toString(), added, correctAdded, this.unlabeledInstances.labelSize(label)));
		}
		
		this.message.displayTextln(this.line("TOTAL", totAdded, totCorrectAdded, total));
	}
	
	private String header() {
		return "| Category | Added | Correct |  Total |";
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
		
		// added-----------------------------
		s = Integer.toString(correctAdded);
				
		l = 7-s.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
		line += correctAdded+" | ";
		
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
