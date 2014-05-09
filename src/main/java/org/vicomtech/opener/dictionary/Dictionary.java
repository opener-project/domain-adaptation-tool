package org.vicomtech.opener.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.vicomtech.opener.utils.Utils;

/**
 * This class implements a Dictionary that imlements
 * a word vector
 * 
 * org.vicomtech.opener.dictionary is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Dictionary implements Serializable {

	/**
	 * The dictionary
	 */
	private SortedMap<String,Double> dictionary;
	
	/**
	 * Map<category,label>
	 */
	private Map<String,String> categoryLabelMap;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Public constructor
	 */
	public Dictionary() {
		this.dictionary = new TreeMap<String,Double>();
		this.categoryLabelMap = new HashMap<String,String>();
	}
	
	/**
	 * Public constructor, reads the dictionary object
	 * from a binarize file
	 * @param file : the binarized file
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public Dictionary(File file) throws ClassNotFoundException, IOException {
		this.readSerializedObject(file);
	}
	
	/**
	 * Adds the token string to the dictionary
	 * @param tokens
	 */
	public void addDictionary(String token) {
		
		if (!this.dictionary.containsKey(token))
			this.dictionary.put(token, null);
	}
	
	/**
	 * Adds the token array to the dictionary
	 * @param tokens
	 */
	public void addDictionary(String[] tokens) {
		
		for (String tok : tokens)
			if (!this.dictionary.containsKey(tok))
				this.dictionary.put(tok, null);
	}
	
	/**
	 * Adds the token list to the dictionary
	 * @param tokens
	 */
	public void addDictionary(List<String> tokens) {
		
		for (String tok : tokens)
			if (!this.dictionary.containsKey(tok))
				this.dictionary.put(tok, null);
	}
	
	/**
	 * Adds a category to the category->label mapping.
	 * The label is 'map.size()+1'
	 * @param entityCategoy
	 */
	public void addCategoryLabel(String category) {
		
		if (!this.categoryLabelMap.containsKey(category)) {
			this.categoryLabelMap.put(category,
				new Integer(this.categoryLabelMap.size()+1).toString());
		}
	}
	
	/**
	 * Gives values to the tokens at the dictionary
	 */
	public void addDictionaryValues() {
		
		int size = this.dictionary.keySet().size();
		double i=0.0;
		for (String tok : this.dictionary.keySet()) {
			Double value = new Double(i/size);
			this.dictionary.put(tok, new Double(value));
			i++;
		}
	}
	
	/**
	 * Adds stop words to the dictionary
	 */
	public void addStopWords() {
		this.dictionary.put("ex-", null);
	}
	
	/**
	 * Get the value of the token
	 * @param token : input token
	 * @return value
	 */
	public double getValue(String token) {
		try {
			return this.dictionary.get(token);
		}
		catch (NullPointerException ex) {
			throw new NullPointerException("token '"+token+"' not found");
		}
	}
	
	/**
	 * Get the label of the category
	 * @param category : input category
	 * @return
	 */
	public String getLabel(String category) {
		return this.categoryLabelMap.get(category);
	}
	
	public String getCategory(double label) {
		int l = (int) label;
		for (String category : this.categoryLabelMap.keySet()) {
			int l2 = Integer.parseInt(this.categoryLabelMap.get(category));
			if (l == l2) {
				return category;
			}
		}
		return null;
	}
	
	/**
	 * Writes the dictionary to a file
	 * @param name : the corpus name
	 * @return written filename
	 * @throws IOException
	 */
	public String write(String name) throws IOException {
		
		// get file
		name = Utils.addDictionaryExtension(name);
		File file = new File(name);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));

		// write category label mapping
		bw.write("Category -> Label mapping");
		bw.newLine();
		bw.newLine();
		for (String category : this.categoryLabelMap.keySet()) {
			bw.write(category+"\t"+this.getLabel(category));
			bw.newLine();
		}
		
		bw.newLine();
		bw.write("----------------------------------");
		bw.newLine();
		bw.newLine();
		
		// write token dictionary
		bw.write("Token dictionary");
		bw.newLine();
		bw.newLine();
		for (String tok : this.dictionary.keySet()) {
			Double value = this.dictionary.get(tok);
			bw.write(tok+"\t"+value);
			bw.newLine();
		}
		bw.close();
		
		return file.getAbsolutePath();
	}
	
	/**
	 * Binarizes the dictionary to a file
	 * @param name : the corpus name
	 * @return binary filename
	 * @throws IOException
	 */
	public String binarize(String name) throws IOException {
		
		// get file
		name = Utils.addBinarizedDictionaryExtension(name);
		File file = new File(name);
		
		// write object
		this.writeSerializedObject(file);
		
		return file.getAbsolutePath();
	}
	
	/**
	 * Serialize the dictionary to the file
	 * @param file : file
	 * @throws IOException
	 */
	private void writeSerializedObject(File file) throws IOException {

		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(file));
		oos.writeObject(this.categoryLabelMap);
		oos.writeObject(this.dictionary);
		oos.close();
	}
	
	
	/**
	 * Reads the dictionary from binarized file
	 * @param file : birarized file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public void readSerializedObject(File file) throws IOException, ClassNotFoundException {
		
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(file));
		this.categoryLabelMap = (Map<String, String>) ois.readObject();
		this.dictionary = (SortedMap<String, Double>) ois.readObject();
		ois.close();
	}

}
