package org.vicomtech.opener.svm;

import org.vicomtech.libsvm.utils.svm_parameter;

/**
 * This class has needed arguments to build a C-SVC using libSVM
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class C_SVC {
	
	protected static final String LINEAR_KERNEL = "0";
	protected static final int DEF_COST = 1;
	protected static final int DEF_SHRINKING = 0;
	protected static final int DEF_PROB_ESTIMATES = 0;
	protected static final double DEF_CACHE_SIZE = 100.0;
	
	public static svm_parameter createParameters(int numFeatures) {
		
		svm_parameter param = new svm_parameter();
		
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.degree = 3;
		param.gamma = 1/numFeatures;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = DEF_CACHE_SIZE;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = DEF_SHRINKING;
		param.probability = 1;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		return param;
	}
	
	public static svm_parameter createParametersWeights(int numFeatures, double negativeLabel, double positiveLabel) {
		
		svm_parameter param = new svm_parameter();
		
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.degree = 3;
		param.gamma = 1/numFeatures;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = DEF_CACHE_SIZE;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = DEF_SHRINKING;
		param.probability = DEF_PROB_ESTIMATES;
		
		param.nr_weight = 2;
		param.weight_label = new int[]{(int) negativeLabel,(int) positiveLabel};
		param.weight = new double[]{0.7,1};

		return param;
	}
	
	public static svm_parameter createProbParameters(int numFeatures) {
		
		svm_parameter param = new svm_parameter();
		
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.degree = 3;
		param.gamma = 1/numFeatures;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = DEF_CACHE_SIZE;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = DEF_SHRINKING;
		param.probability = 1;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		return param;
	}

}
