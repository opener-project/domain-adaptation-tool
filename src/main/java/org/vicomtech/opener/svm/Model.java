package org.vicomtech.opener.svm;

import java.io.File;

import org.vicomtech.libsvm.utils.svm_model;

/**
 * This class uses libSVM models
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Model {

	private svm_model classifier;
	private int positiveCategoryIndex;
	private int negativeCategoryIndex;
	private double label;
	private File classifierFile;
	
	public Model(svm_model classifier,
			File classifierFile) {
		
		this.classifier = classifier;
		this.getCategoryIndexes(classifier);
		this.classifierFile = classifierFile;
		this.label = this.classifier.label[this.positiveCategoryIndex];
	}
	
	private void getCategoryIndexes(svm_model classifier) {
		
		if (classifier.label[0] == SVM.NEGATIVE_CLASS) {
			this.negativeCategoryIndex = 0;
			this.positiveCategoryIndex = 1;
		}
		else {
			this.negativeCategoryIndex = 1;
			this.positiveCategoryIndex = 0;
		}
	}
	
	public svm_model getClassifier() {
		return this.classifier;
	}
	
	public int getPositiveCategoryIndex() {
		return this.positiveCategoryIndex;
	}
	
	public int getNegativeCategoryIndex() {
		return this.negativeCategoryIndex;
	}
	
	public double getLabel() {
		return this.label;
	}
	
	public int[] getLabels() {
		return this.classifier.label;
	}
	
	public File getClassifierFile() {
		return this.classifierFile;
	}
	
	public int getLabelIndex(double label) {
		
		for (int i=0; i<this.classifier.label.length; i++) {
			int l = this.classifier.label[i];
			if (l == (int) label) {
				return i;
			}
		}
		return -1;
	}
}
