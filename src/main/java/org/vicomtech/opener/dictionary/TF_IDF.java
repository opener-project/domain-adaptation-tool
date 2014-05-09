package org.vicomtech.opener.dictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.vicomtech.opener.utils.Utils;

/**
 * This class processes TF-IDF features
 * 
 * org.vicomtech.opener.dictionary is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class TF_IDF {

	private Map<String,List<String>> tf_idf_map;
	private Map<String,Double> tf_idf_dictionary;
	
	public static enum TF_Type {
		LOG, AUGMENTED, NULL
	}
	
	public static class TF_IDF_Comparator implements Comparator<String> {
		private String category;
		private Map<String,Map<String,Integer>> frequencyMap;
		private Map<String,Integer> maxFrequency;
		private TF_Type tfType;
		
		public TF_IDF_Comparator(String category,
								 Map<String,Map<String,Integer>> frequencyMap,
								 Map<String,Integer> maxFrequency,
								 TF_Type tfType) {
			this.category = category;
			this.frequencyMap = frequencyMap;
			this.maxFrequency = maxFrequency;
			this.tfType = tfType;
		}
		
		@Override
		public int compare(String arg0, String arg1) {
			double tf_idf0 = -1;
			double tf_idf1 = -1;
			try {
				tf_idf0 = getTF_IDF(arg0, category, frequencyMap, maxFrequency, tfType);
				tf_idf1 = getTF_IDF(arg1, category, frequencyMap, maxFrequency, tfType);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (tf_idf0 > tf_idf1) {
				return -1;
			}
			else if (tf_idf0 < tf_idf1) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
	
	public TF_IDF(Map<String,List<String>> tf_idf_map,
			Map<String,Double> tf_idf_dictionary) {
		this.tf_idf_map = tf_idf_map;
		this.tf_idf_dictionary = tf_idf_dictionary;
	}
	
	/**
	 * Public constructor, reads the dictionary object
	 * from a binarize file
	 * @param file : the binarized file
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public TF_IDF(File file) throws ClassNotFoundException, IOException {
		this.readSerializedObject(file);
	}
	
	public Map<String,List<String>> getTF_IDF_Map() {
		return this.tf_idf_map;
	}
	
	public Map<String,Double> getDictionary() {
		return this.tf_idf_dictionary;
	}
	
	/**
	 * Writes the dictionary to a file
	 * @param name : the corpus name
	 * @return written filename
	 * @throws IOException
	 */
	public String write(String name) throws IOException {
		// get file
		name = Utils.addTF_IDF_Extension(name);
		File file = new File(name);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		// write category label mapping
		bw.write("Category -> TF-IDF mapping");
		bw.newLine();
		bw.newLine();
		for (String category : this.tf_idf_map.keySet()) {
			for (String word : this.tf_idf_map.get(category)) {
				bw.write(category+"\t"+word);
				bw.newLine();
			}
		}
		
		bw.newLine();
		bw.write("----------------------------------");
		bw.newLine();
		bw.newLine();
		
		// write tf-idf dictionary
		bw.write("TF-IDF dictionary");
		bw.newLine();
		bw.newLine();
		for (String tok : this.tf_idf_dictionary.keySet()) {
			Double value = this.tf_idf_dictionary.get(tok);
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
		name = Utils.addBinarizedTF_IDF_Extension(name);
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
		oos.writeObject(this.tf_idf_map);
		oos.writeObject(this.tf_idf_dictionary);
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
		this.tf_idf_map = (Map<String,List<String>>) ois.readObject();
		this.tf_idf_dictionary = (HashMap<String,Double>) ois.readObject();
		ois.close();
	}
	
//-----------------------STATIC FUNCTIONS----------------------------------
	
	/**
	 * Return tf value of input token
	 * @param token : input token
	 * @param document : input document
	 * @param frequencyMap : frequency mapping Map<Document,Map<Token,Frequency>>
	 * @param maxFrequency : max frequency mapping Map<Document,MaxFrequency>
	 * @param tfType : TF type
	 * @return
	 * @throws IOException 
	 */
	public static double getTF(String token,
							   String document,
							   Map<String,Map<String,Integer>> frequencyMap,
							   Map<String,Integer> maxFrequency,
							   TF_Type tfType) throws IOException {
		if (tfType.equals(TF_Type.LOG)) {
			return Math.log10( frequencyMap.get(document).get(token)+1 );
		}
		else if (tfType.equals(TF_Type.AUGMENTED)) {
			return 0.5+((0.5*frequencyMap.get(document).get(token))
					  / (maxFrequency.get(document)));
		}
		else throw new IOException(String.format("TF type '%s' not valid", tfType.toString()));
	}
	
	/**
	 * Return itf value of input token
	 * @param token : input token
	 * @param document : input document
	 * @param frequencyMap : frequency mapping Map<Document,Map<Token,Frequency>>
	 * @return
	 */
	public static double getIDF(String token,
								Map<String,Map<String,Integer>> frequencyMap) {
		int appearances = 0;
		for (String document : frequencyMap.keySet()) {
			if (frequencyMap.get(document).containsKey(token)) {
				appearances++;
			}
		}
		return Math.log10(frequencyMap.size() / appearances);
	}
	
	/**
	 * Return tf-itf value of input token
	 * @param token : input token
	 * @param document : input document
	 * @param frequencyMap : frequency mapping Map<Document,Map<Token,Frequency>>
	 * @param maxFrequency : max frequency mapping Map<Document,MaxFrequency>
	 * @param tfType : TF type
	 * @return
	 * @throws IOException 
	 */
	public static double getTF_IDF(String token,
								   String document,
								   Map<String,Map<String,Integer>> frequencyMap,
								   Map<String,Integer> maxFrequency,
								   TF_Type tfType) throws IOException {
		return getTF(token, document, frequencyMap, maxFrequency, tfType)
				* getIDF(token, frequencyMap);
	}
	
	/**
	 * Sort tokens map by tf-idf in ascending order and filter by percentage
	 * @param tokensMap : tokens mapping Map<Document,List<tokens>
	 * @param frequencyMap : frequency mapping Map<Document,Map<Token,Frequency>>
	 * @param maxFrequency : max frequency mapping Map<Document,MaxFrequency>
	 * @param percentage : percentage to filter
	 * @param tfType : TF type
	 */
	public static void sortTF_IDF(Map<String,List<String>> tokensMap,
	   		  					  Map<String,Map<String,Integer>> frequencyMap,
	   		  					  Map<String,Integer> maxFrequency,
	   		  					  int percentage,
	   		  					  TF_Type tfType) {
		double pct = percentage / 100;
		for (String category : tokensMap.keySet()) {
			List<String> tokenList = tokensMap.get(category);
			// sort and put
			String[] tokenArray = tokenList.toArray(new String[tokenList.size()]);
			Arrays.sort(tokenArray, new TF_IDF_Comparator(category, frequencyMap, maxFrequency, tfType));
			tokenList =  Arrays.asList(tokenArray).subList(0, (int) (tokenList.size()*pct));
			tokensMap.put(category, tokenList);
		}
	}
	
	/**
	 * Sort tokens map by tf-idf in ascending order and filter by threshold
	 * @param tokensMap : tokens mapping Map<Document,List<tokens>
	 * @param frequencyMap : frequency mapping Map<Document,Map<Token,Frequency>>
	 * @param maxFrequency : max frequency mapping Map<Document,MaxFrequency>
	 * @param threshold : threshold to filter
	 * @param tfType : TF type
	 * @throws IOException 
	 */
	public static void sortTF_IDF(Map<String,List<String>> tokensMap,
				  				  Map<String,Map<String,Integer>> frequencyMap,
				  				  Map<String,Integer> maxFrequency,
				  				  double threshold,
				  				  TF_Type tfType) throws IOException {
		for (String category : tokensMap.keySet()) {
			List<String> tokenList = tokensMap.get(category);
			// filter by threshold
			for (int i=0; i<tokenList.size(); i++) {
				double tf_idf = getTF_IDF(tokenList.get(i), category, frequencyMap, maxFrequency, tfType);
				if (tf_idf < threshold) {
					tokenList.remove(i);
					i--;
				}
			}
			// sort and put
			String[] tokenArray = tokenList.toArray(new String[tokenList.size()]);
			Arrays.sort(tokenArray, new TF_IDF_Comparator(category, frequencyMap, maxFrequency, tfType));
			tokenList = Arrays.asList(tokenArray);
			tokensMap.put(category, tokenList);
		}
	}
	
	/**
	 * Get frequency mapping Map<Document,Map<Token,Frequency>>
	 * @param tokensMap : tokens map Map<Document,List<tokens>
	 * @return
	 */
	private static Map<String,Map<String,Integer>> getFrequencies(Map<String,List<String>> tokensMap) {
		Map<String,Map<String,Integer>> frequencyMap = new HashMap<String,Map<String,Integer>>();
		for (String category : tokensMap.keySet()) {
			frequencyMap.put(category, new HashMap<String,Integer>());
			for (String token : tokensMap.get(category)) {
				if (frequencyMap.get(category).containsKey(token)) {
					frequencyMap.get(category).put(token, frequencyMap.get(category).get(token)+1);
				}
				else {
					frequencyMap.get(category).put(token, 1);
				}
			}
		}
		return frequencyMap;
	}
	
	/**
	 * Get max frequency mapping Map<Document,MaxFrequency>
	 * @param frequencyMap : frequency mapping Map<Document,Map<Token,Frequency>>
	 * @return
	 */
	private static Map<String,Integer> getMaxFrequencies(Map<String,Map<String,Integer>> frequencyMap) {
		Map<String,Integer> maxFrequencies = new HashMap<String,Integer>();
		for (String category : frequencyMap.keySet()) {
			maxFrequencies.put(category, 0);
			int maxFreq = 0;
			for (String token : frequencyMap.get(category).keySet()) {
				int freq = frequencyMap.get(category).get(token);
				if (freq > maxFreq) {
					maxFreq = freq;
				}
			}
			maxFrequencies.put(category, maxFreq);
		}
		return maxFrequencies;
	}
	
	/**
	 * Return unique tokens mapping
	 * @param tokensMap : tokens mapping Map<Document,List<tokens>
	 * @return
	 */
	private static Map<String,List<String>> getTokensUniqMap(Map<String,List<String>> tokensMap) {
		Map<String,List<String>> tokensUniqMap = new HashMap<String,List<String>>();
		for (String category : tokensMap.keySet()) {
			List<String> seedTokensUniq = new ArrayList<String>();
			for (String token : tokensMap.get(category)) {
				if (!seedTokensUniq.contains(token)) {
					seedTokensUniq.add(token);
				}
			}
			tokensUniqMap.put(category, seedTokensUniq);
		}
		return tokensUniqMap;
	}
	
	/**
	 * Get tf-idf mapping in ascending order and filtered by percentage
	 * @param tokensMap : tokens mapping Map<Document,List<tokens>
	 * @param percentage : percentage to filter
	 * @param tfType : TF type
	 */
	public static Map<String,List<String>> getTF_IDF_Map(Map<String,List<String>> tokensMap,
														 int percentage,
														 TF_Type tfType) {
		// get frequencies
		Map<String,Map<String,Integer>> frequencyMap = getFrequencies(tokensMap);
		Map<String,Integer> maxFrequency = getMaxFrequencies(frequencyMap);
		// uniq
		Map<String,List<String>> tokensUniqMap = getTokensUniqMap(tokensMap);	
		// sort
		sortTF_IDF(tokensUniqMap, frequencyMap, maxFrequency, percentage, tfType);
		return tokensUniqMap;
	}
	
	/**
	 * Get tf-idf mapping in ascending order and filtered by threshold
	 * @param tokensMap : tokens mapping Map<Document,List<tokens>
	 * @param threshold : threshold to filter
	 * @param tfType : TF type
	 * @throws IOException 
	 */
	public static Map<String,List<String>> getTF_IDF_Map(Map<String,List<String>> tokensMap,
			 											 double threshold,
			 											 TF_Type tfType) throws IOException {
		// get frequencies
		Map<String,Map<String,Integer>> frequencyMap = getFrequencies(tokensMap);
		Map<String,Integer> maxFrequency = getMaxFrequencies(frequencyMap);
		// uniq
		Map<String,List<String>> tokensUniqMap = getTokensUniqMap(tokensMap);	
		// sort
		sortTF_IDF(tokensUniqMap, frequencyMap, maxFrequency, threshold, tfType);
		return tokensUniqMap;
	}
	
}