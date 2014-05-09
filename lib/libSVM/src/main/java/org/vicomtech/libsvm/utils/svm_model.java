//
// svm_model
//
package org.vicomtech.libsvm.utils;

public class svm_model implements java.io.Serializable
{
	public svm_parameter param;	// parameter
	public int nr_class;		// number of classes, = 2 in regression/one class svm
	public int l;			// total #SV
	public svm_node[][] SV;	// SVs (SV[l])
	public double[][] sv_coef;	// coefficients for SVs in decision functions (sv_coef[k-1][l])
	public double[] rho;		// constants in decision functions (rho[k*(k-1)/2])
	public double[] probA;         // pariwise probability information
	public double[] probB;
	public int[] sv_indices;       // sv_indices[0,...,nSV-1] are values in [1,...,num_traning_data] to indicate SVs in the training set

	// for classification only

	public int[] label;		// label of each class (label[k])
	public int[] nSV;		// number of SVs for each class (nSV[k])
				// nSV[0] + nSV[1] + ... + nSV[k-1] = l

	protected double[] getW() {
		
		//	w = model.SVs' * model.sv_coef;
		int numFeatures = this.getNumFeatures();
		double[] w = new double[numFeatures];
			
		for (int i=0; i<numFeatures; i++) {
			double sum = 0.0;
			for (int j=0; j<SV.length; j++) {
				if (i < SV[j].length) {
					sum += sv_coef[0][j] * SV[j][i].value;
				}
			}
			w[i] = sum;
		}
		
		if (label[1] == -1) {
			for (int i=0; i<w.length; i++)
				w[i] = -w[i];
		}
		return w;
	}
	
	private int getNumFeatures() {
		
		int maxNumFeatures = -1;
		for (int i=0; i<SV.length; i++) {
			int numFeatures = SV[i].length;
			if (numFeatures > maxNumFeatures)
				maxNumFeatures = numFeatures;
		}
		return maxNumFeatures;
	}

};
