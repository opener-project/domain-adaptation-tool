package org.vicomtech.libsvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.vicomtech.libsvm.utils.svm_node;
import org.vicomtech.libsvm.utils.svm_problem;

/**
 * This class reads instances information from input file
 * @author aazpeitia
 *
 */
public class Instances {

	public static final String ENTITY_DELIMITER = "#ENTITY#";
	
	private File sourceFile;
	private svm_problem instances;
	private int numFeatures;
	private double positiveLabel = 999;
	private double negativeLabel = -999;
	private int positiveInstances = 0;
	private int negativeInstances = 0;
	private Map<Double,Integer> labelSizes;
	private List<String> entities;
	private List<String> positiveEntities;
	
	/**
	 * Public constructor, reads instances info from file
	 * @param file
	 * @throws IOException
	 */
	public Instances(File file) throws IOException {
		this.entities = new ArrayList<String>();
		this.positiveEntities = new ArrayList<String>();
		this.labelSizes = new HashMap<Double,Integer>();
		this.sourceFile = file;
		this.instances = read_problem();
	}
	
	/**
	 * Public constructor, initializes all variables
	 * @param numFeatures : max number of features
	 */
	public Instances(int numFeatures) {
		this.entities = new ArrayList<String>();
		this.positiveEntities = new ArrayList<String>();
		this.numFeatures = numFeatures;
		this.labelSizes = new HashMap<Double,Integer>();
		this.sourceFile = null;
		this.instances = new svm_problem();
		this.instances.l = 0;
		this.instances.y = new double[0];
		this.instances.x = new svm_node[0][];
	}
	
	private svm_problem read_problem() throws IOException {
		BufferedReader fp = new BufferedReader(new FileReader(this.sourceFile));
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		int max_index = 0;

		while(true)
		{
			String line = fp.readLine();
			if(line == null) break;
			
			List<String> list = Arrays.asList(line.split(" "));
			String entity = list.get(list.size()-1);
			if (entity.startsWith(ENTITY_DELIMITER)) {
				this.entities.add(entity.substring(ENTITY_DELIMITER.length(),
												   entity.length())
								.replaceAll("_", " "));
				line = StringUtils.join(list.subList(0, list.size()-1), " ");
			}
			
			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
			double d = svm_train.atof(st.nextToken());
			if (d < 0) {
				this.negativeInstances++;
				if (this.negativeLabel == -999)
					this.negativeLabel = d;
			}
			if (d > 0) {
				if (this.entities.size() > 0) {
					this.positiveEntities.add(this.entities.get(this.entities.size()-1));
				}
				this.positiveInstances++;
				if (this.positiveLabel == 999)
					this.positiveLabel = d;
			}
			try {
				this.labelSizes.put(d, this.labelSizes.get(d)+1);
			}
			catch (NullPointerException ex) {
				this.labelSizes.put(d, 1);
			}
				
			vy.addElement(d);
			
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = svm_train.atoi(st.nextToken());
				x[j].value = svm_train.atof(st.nextToken());
			}
			if(m>0) max_index = Math.max(max_index, x[m-1].index);
			vx.addElement(x);
		}

		svm_problem prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for(int i=0;i<prob.l;i++)
			prob.x[i] = vx.elementAt(i);
		prob.y = new double[prob.l];
		for(int i=0;i<prob.l;i++)
			prob.y[i] = vy.elementAt(i);
		
		this.numFeatures = max_index;

		fp.close();
		
		return prob;
	}
	
	private String getMaxTF_IDF_Token(int index,
									  Map<String,List<String>> tf_idf_map) {
		String max_tf_idf_token = null;
		String[] entity = this.entities.get(index).split(" ");
		int min_index = Integer.MAX_VALUE;
		for (String token : entity) {
			// is positive instance
			if (this.instances.y[index] == this.positiveLabel) {
				List<String> tdf_idf_list = tf_idf_map.get(Double.toString(this.positiveLabel));
				if (tdf_idf_list.contains(token)) {
					int ind = tdf_idf_list.indexOf(token);
					if (ind < min_index) {
						min_index = ind;
						max_tf_idf_token = token;
					}
				}
			}
			// is negative instance
			else {
				for (String cat : tf_idf_map.keySet()) {
					if (!cat.contentEquals(Double.toString(this.positiveLabel))) {
						List<String> tdf_idf_list = tf_idf_map.get(cat);
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
			}
		}
		return max_tf_idf_token;
	}
	
	public void addTF_IDF_Feature(Map<String,List<String>> tf_idf_map,
								  Map<String,Double> tf_idf_dictionary) {
		if (this.entities.size() > 0) {
			this.numFeatures++;
			for (int i=0; i<this.instances.x.length; i++) {
				// get token with max tf-idf value
				String max_tf_idf_token = this.getMaxTF_IDF_Token(i, tf_idf_map);
				
				// add tf-idf feature
				if (max_tf_idf_token != null) {
					// create feature
					double value = tf_idf_dictionary.get(max_tf_idf_token);
					svm_node feature = new svm_node();
					feature.index = this.numFeatures;
					feature.value = value;
					// copy old features
					svm_node[] features = new svm_node[this.instances.x[i].length+1];
					for (int n=0; n<this.instances.x[i].length; n++) {
						features[n] = this.instances.x[i][n];
					}
					// add created feature
					features[features.length-1] = feature;
					// update new features
					
					this.instances.x[i] = features;
				}
			}
		}
	}
	
	public void updateTF_IDF_Feature(Map<String,List<String>> tf_idf_map,
									 Map<String,Double> tf_idf_dictionary) {
		if (this.entities.size() > 0) {
			for (int i=0; i<this.instances.x.length; i++) {
				// get token with max tf-idf value
				String max_tf_idf_token = this.getMaxTF_IDF_Token(i, tf_idf_map);
				
				// add tf-idf feature
				if (max_tf_idf_token != null) {
					// create feature
					double value = tf_idf_dictionary.get(max_tf_idf_token);
					int lastIndex = this.instances.x[i][this.instances.x[i].length-1].index;
					// instance has tdf-idf feature
					if (lastIndex == this.numFeatures) {
						this.instances.x[i][this.instances.x[i].length-1].value = value;
					}
					// instance has not tdf-idf feature
					else {
						svm_node feature = new svm_node();
						feature.index = this.numFeatures;
						feature.value = value;
						// copy old features
						svm_node[] features = new svm_node[this.instances.x[i].length+1];
						for (int n=0; n<this.instances.x[i].length; n++) {
							features[n] = this.instances.x[i][n];
						}
						// add created feature
						features[features.length-1] = feature;
						// update new features
						this.instances.x[i] = features;
					}
				}
				// remove tf-idf feature if needed
				else {
					int lastIndex = this.instances.x[i][this.instances.x[i].length-1].index;
					if (lastIndex == this.numFeatures) {
						// copy features except last
						svm_node[] features = new svm_node[this.instances.x[i].length-1];
						for (int n=0; n<this.instances.x[i].length-1; n++) {
							features[n] = this.instances.x[i][n];
						}
						// update new features
						this.instances.x[i] = features;
					}
				}
			}
		}
	}
	
	/**
	 * Return number of features
	 * @return
	 */
	public int getNumFeatures() {
		return this.numFeatures;
	}
	
	/**
	 * Returns previously readed file
	 * @return
	 */
	public File getSourceFile() {
		return this.sourceFile;
	}
	
	/**
	 * Get instances
	 * @return
	 */
	public svm_problem getInstances() {
		return this.instances;
	}
	
	/**
	 * Gets instance at input index
	 * @param index : input index
	 * @return
	 */
	public svm_node[] getInstance(int index) {
		return this.instances.x[index];
	}
	
	/**
	 * Gets category of instance at input index
	 * @param index : input index
	 * @return
	 */
	public double getCategory(int index) {
		return this.instances.y[index];
	}
	
	/**
	 * Get positive label for binary classification
	 * @return
	 */
	public double getPositiveLabel() {
		return this.positiveLabel;
	}
	
	/**
	 * Get negative label for binary classification
	 * @return
	 */
	public double getNegativeLabel() {
		return this.negativeLabel;
	}
	
	/**
	 * Get positive instance for binary classification
	 * @return
	 */
	public int positiveInstances() {
		return this.positiveInstances;
	}
	
	/**
	 * Get negative instances for binary classification
	 * @return
	 */
	public int negativeInstances() {
		return this.negativeInstances;
	}
	
	/**
	 * Returns number of instances
	 * @return
	 */
	public int size() {
		return this.instances.l;
	}
	
	/**
	 * Returns number of categoies or labels
	 * @param label
	 * @return
	 */
	public int labelSize(double label) {
	/*	int total = 0;
		for (double d : this.instances.y) {
			if (d == label) {
				total++;
			}
		}
		return total;*/
		return this.labelSizes.get(label);
	}
	
	/**
	 * Removes instance at input indes
	 * @param index : input index
	 * @return
	 */
	public svm_node[] removeInstance(int index) {
		
		if (index < this.instances.l) {

			// remove instance
			svm_node[] instance = this.removeVector(index);
			
			// remove category
			double removed = this.removeCategory(index);
			
			// update positive or negative instances
			if (removed > 0)
				this.positiveInstances--;
			else if (removed < 0)
				this.negativeInstances--;
			this.labelSizes.put(removed, this.labelSizes.get(removed)-1);
			
			// update length
			this.instances.l = this.instances.y.length;
			
			return instance;
		}
		else {
			System.err.println("instance not exists");
			return null;
		}
	}
	
	/**
	 * Adds instance
	 * @param instance : input instance
	 * @param label : input instance label
	 */
	public void addInstance(svm_node[] instance, double label) {
		
		// add instance
		this.addVector(instance);
					
		// add category
		this.addCategory(label);
		
		// update positive or negative instances
		if (label > 0)
			this.positiveInstances++;
		else if (label < 0)
			this.negativeInstances++;
		
		try {
			this.labelSizes.put(label, this.labelSizes.get(label)+1);
		}
		catch (NullPointerException ex) {
			this.labelSizes.put(label, 1);
		}
					
		// update length
		this.instances.l = this.instances.y.length;
	}
	
	private svm_node[] removeVector(int index) {
		
		// initialize variables
		svm_node[][] newArray = new svm_node[this.instances.x.length-1][];
		svm_node[] removed = null;
		int pos = 0;
		
		// iterate array
		for (int i=0; i<this.instances.x.length; i++) {
			svm_node[] instance = this.instances.x[i];
			if (i != index) {
				newArray[pos] = instance;
				pos++;
			}
			else
				removed = instance;
		}
		
		// update array
		this.instances.x = newArray;

		return removed;
	}
	
	private double removeCategory(int index) {
		
		// initialize variables
		double[] newArray = new double[this.instances.y.length-1];
		double removed = -1;
		int pos = 0;
		
		// iterate array
		for (int i=0; i<this.instances.y.length; i++) {
			double d = this.instances.y[i];
			if (i != index) {
				newArray[pos] = d;
				pos++;
			}
			else
				removed = d;
		}
		
		// update array
		this.instances.y = newArray;
		
		return removed;
	}
	
	public String removeEntity(int index) {
		if (this.entities.size() > 0) {
			return this.entities.remove(index);
		}
		else {
			return null;
		}
	}
	
	private void addVector(svm_node[] instance) {
		
		// initialize variables
		svm_node[][] newArray = new svm_node[this.instances.x.length+1][];
		
		// iterate array
		for (int i=0; i<this.instances.x.length; i++)
			newArray[i] = this.instances.x[i];
		
		// add 'instance' at last position
		newArray[newArray.length-1] = instance;
		
		// update array
		this.instances.x = newArray;
	}
	
	private void addCategory(double d) {
		
		// initialize variables
		double[] newArray = new double[this.instances.y.length+1];
		
		// iterate array
		for (int i=0; i<this.instances.y.length; i++)
			newArray[i] = this.instances.y[i];
		
		// add 'd' at last position
		newArray[newArray.length-1] = d;
		
		// update array
		this.instances.y = newArray;
	}
	
	public void addEntity(String entity) {
		this.entities.add(entity);
	}
	
	public void addPositiveEntity(String entity) {
		this.positiveEntities.add(entity);
	}
	
	/**
	 * Gets all the distinct categories or labels
	 * @return
	 */
	public double[] getLabels() {
		double[] labels = new double[this.labelSizes.keySet().size()];
		int i=0;
		for (double label : this.labelSizes.keySet()) {
			labels[i] = label;
			i++;
		}
		return labels;
	}
	
	public List<String> getPositiveEntities() {
		return this.positiveEntities;
	}

}
