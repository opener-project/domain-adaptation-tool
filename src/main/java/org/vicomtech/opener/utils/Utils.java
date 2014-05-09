package org.vicomtech.opener.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.entities.Feature;

/**
 * This class provides general methods for the boostrapping application
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Utils {
	
	/**
	 * Output file extensions
	 */
	private static final String TRAIN_EXT = ".train";
	private static final String DEV_EXT   = ".dev";
	private static final String TEST_EXT  = ".test";
	private static final String MODEL_EXT         = ".bin";
	private static final String RESULTS_EXT       = ".out";
	private static final String DICTIONARY_EXT    = ".dic";
	public  static final String BINARIZED_DIC_EXT = ".dbin";
	private static final String TFIDF_EXT           = ".tfidf.dic";
	public  static final String BINARIZED_TFIDF_EXT = ".tfidf.dbin";
	
	public static final int FAILURE = -1;
	
	/**
	 * Get feature index where the token is
	 * @param token : token to search
	 * @param features : features list
	 * @param leftTok : token left token
	 * @param rightTok : token right token
	 * @param featureType : '0' if feature has no right features, else '1'
	 * @return
	 */
	public static int getIndex(String token, List<Feature> features,
			String leftTok, String rightTok, int featureType) {
		
		int index = -1;
		for (int i=0; i<features.size(); i++) {
			Feature feature = features.get(i);
			if (feature.hasHead(token, featureType)) {
				// token found
				if (leftTok.length() > 0) {
					if (feature.hasToken(leftTok)) {
						// left token is at the same feature object
						if (rightTok.length() > 0) {
							if (feature.hasToken(rightTok)) {
								// right token is at the same feature object
								index = i;
								break;
							}
							else if (i < features.size()-1 &&
									features.get(i+1).hasToken(rightTok)) {
								// right token is at the next feature object
								index = i;
								break;
							}
						}
						else {
							// right token is empty
							index = i;
							break;
						}
					}
					else if (i > 0 && features.get(i-1).hasToken(leftTok)) {
						// left token is at previous feature object
						if (rightTok.length() > 0) {
							if (feature.hasToken(rightTok)) {
								// right token is at the same feature object
								index = i;
								break;
							}
							else if (i < features.size()-1 &&
									features.get(i+1).hasToken(rightTok)) {
								// right token is at the next feature object
								index = i;
								break;
							}
						}
						else {
							// right token is empty
							index = i;
							break;
						}
					}
				}
				else {
					// left token is empty
					if (rightTok.length() > 0) {
						if (feature.hasToken(rightTok)) {
							// right token is at the same feature object
							index = i;
							break;
						}
						else if (i < features.size()-1 &&
								features.get(i+1).hasToken(rightTok)) {
							// right token is at the next feature object
							index = i;
							break;
						}
					}
					else {
						// right token is empty
						index = i;
						break;
					}
				}
			}
		}
		return index;
	}
	
	/**
	 * Get feature index where the token is
	 * @param token : token to search
	 * @param features : features list
	 * @param leftTok : token left token
	 * @param rightTok : token right token
	 * @param featureType : '0' if feature has no right features, else '1'
	 * @return
	 */
	private static int getIndex2(String token, List<Feature> features,
			String leftTok, String rightTok, int featureType) {
		
		int start = -1;
		int end = -1;
		
		String[] entities = token.split(" ");
		// for each entity
		for (int i=0; i<entities.length; i++) {
			String entity = entities[i];
			// find at features
			boolean found = false;
			for (int j=0; j<features.size(); j++) {
				Feature feature = features.get(j);
				
				// if found, the other entities must be at features sequentially
				if (feature.hasToken(entity)) {
					int q = j;
					found = true;
					for (int z=i+1; z<entities.length; z++) {
						String otherEntity = entities[z];
						Feature otherFeature = features.get(q);
						if (!otherFeature.hasToken(otherEntity)) {
							q++;
							if (q < features.size()) {
								otherFeature = features.get(q);
								if (!otherFeature.hasToken(otherEntity)) {
									found = false;
									break;
								}
							}
							else {
								found = false;
								break;
							}
						}
					}
					if (found) {
						start = j;
						end = q;
						break;
					}
				}
			}
			if (found) break;
		}
		
		if (start > -1 && end > -1) {
			features = Feature.mergeFeatures(features, start, end, token);
		}
		return start;
	}
	
	/**
	 * Creates an entity object
	 * @param entity
	 * @param category
	 * @param features
	 * @param leftTok
	 * @param rightTok
	 * @param window
	 * @param modSize
	 * @param featureType : '0' if feature has no right features, else '1'
	 * @return
	 */
	public static Entity createEntity(String entity, String category,
			List<Feature> features, String leftTok,
			String rightTok, int window, int modSize, int featureType) {
		
		List<String> leftFeatures = new ArrayList<String>();
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new ArrayList<String>();
		
		// get indexes
		int index = Utils.getIndex(entity, features, leftTok, rightTok, featureType);
		if (index == -1) {
			// entity could be distributed between 2 feature objects
			index = Utils.getIndex2(entity, features, leftTok, rightTok, featureType);
		}
		
		int start = 0;
		int end = features.size()-1;
		if (index-window >= 0)
			start = index-window;
		if (index+window <= end)
			end = index+window;
		
		leftFeatures.addAll(getTextFromLeftFeatures(
				features, start, index-1, window, modSize, featureType));
		middleFeatures.addAll(getTextFromFeatures(
				features, entity, index, modSize, featureType));
		rightFeatures.addAll(getTextFromRightFeatures(
				features, index+1, end, window, modSize, featureType));
		
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		middleFeatures = Utils.removeUnderscore(middleFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		Entity e = new Entity(entity.replace("_", " "),
				category, leftFeatures, middleFeatures, rightFeatures);
		
		return e;
	}
	
	/**
	 * Creates an entity object with lemmas
	 * @param entity
	 * @param category
	 * @param features
	 * @param leftTok
	 * @param rightTok
	 * @param window
	 * @param modSize
	 * @param featureType : '0' if feature has no right features, else '1'
	 * @return
	 */
	public static Entity createEntity(String entityWord, String entityLemmas,
			String category, List<Feature> features, String leftTok,
			String rightTok, int window, int modSize, int featureType) {
		
		List<String> leftFeatures = new ArrayList<String>();
		List<String> middleFeatures = new ArrayList<String>();
		List<String> rightFeatures = new ArrayList<String>();
		
		// get indexes
		int index = Utils.getIndex(entityLemmas, features, leftTok, rightTok, featureType);
		if (index == -1) {
			// entity could be distributed between 2 feature objects
			index = Utils.getIndex2(entityLemmas, features, leftTok, rightTok, featureType);
		}
		
		int start = 0;
		int end = features.size()-1;
		if (index-window >= 0)
			start = index-window;
		if (index+window <= end)
			end = index+window;
		
		leftFeatures.addAll(getTextFromLeftFeatures(
				features, start, index-1, window, modSize, featureType));
		middleFeatures.addAll(getTextFromFeatures(
				features, entityLemmas, index, modSize, featureType));
		rightFeatures.addAll(getTextFromRightFeatures(
				features, index+1, end, window, modSize, featureType));
		
		leftFeatures = Utils.removeUnderscore(leftFeatures);
		middleFeatures = Utils.removeUnderscore(middleFeatures);
		rightFeatures = Utils.removeUnderscore(rightFeatures);
		
		Entity e = new Entity(entityLemmas.replace("_", " "),
				category, leftFeatures, middleFeatures, rightFeatures);
		
		return e;
	}
	
	/**
	 * 
	 * @param features
	 * @param start
	 * @param end
	 * @param window
	 * @param modSize
	 * @param featureType : '0' if feature has no right features, else '1'
	 * @return
	 */
	public static List<String> getTextFromLeftFeatures(
			List<Feature> features, int start, int end,
			int window, int modSize, int featureType) {
		
		List<String> text = new ArrayList<String>();
		
		// add missing features
		for (int i=end-start+1; i<window; i++) {
			// left features
			for (int j=0; j<modSize; j++) {
				text.add(new String());
			}
			text.add(new String());
			if (featureType != 0) {
				// right features
				for (int j=0; j<modSize; j++) {
					text.add(new String());
				}
			}
		}
		
		for (int i=start; i<=end; i++) {
			Feature feature = features.get(i);
			// add modifiers
			text.addAll(feature.getLeftModifiers());
			for (int j=feature.getLeftModifiersSize(); j<modSize; j++)
				text.add(new String());
			// add head
			text.add(feature.getHead());
			if (featureType != 0) {
				// add modifiers
				text.addAll(feature.getRightModifiers());
				for (int j=feature.getRightModifiersSize(); j<modSize; j++)
					text.add(new String());
			}
		}
		
		return text;
	}
	
	/**
	 * 
	 * @param features
	 * @param start
	 * @param end
	 * @param window
	 * @param modSize
	 * @param featureType : '0' if feature has no right features, else '1'
	 * @return
	 */
	public static List<String> getTextFromRightFeatures(
			List<Feature> features, int start, int end,
			int window, int modSize, int featureType) {
		
		List<String> text = new ArrayList<String>();
		for (int i=start; i<=end; i++) {
			Feature feature = features.get(i);
			// add modifiers
			text.addAll(feature.getLeftModifiers());
			for (int j=feature.getLeftModifiersSize(); j<modSize; j++)
				text.add(new String());
			// add head
			text.add(feature.getHead());
			if (featureType != 0) {
				// add modifiers
				text.addAll(feature.getRightModifiers());
				for (int j=feature.getRightModifiersSize(); j<modSize; j++)
					text.add(new String());
			}
		}
		// add missing features
		for (int i=end-start+1; i<window; i++) {
			// left features
			for (int j=0; j<modSize; j++) {
				text.add(new String());
			}
			text.add(new String());
			if (featureType != 0) {
				// right features
				for (int j=0; j<modSize; j++) {
					text.add(new String());
				}
			}
		}
		return text;
	}
	
	public static List<String> getTextFromFeatures(
			List<Feature> features, String entity, int index,
			int modSize, int featureType) {
		
		List<String> text = new ArrayList<String>();
		
		Feature feature = features.get(index);
		
		// add left modifiers
		for (int i=0; i<feature.getLeftModifiersSize(); i++) {
			String modifier = feature.getLeftModifier(i);
			// if modifier is equal entity break, else add
			if (modifier.contentEquals(entity))
				break;
			else
				text.add(modifier);
		}
		while (text.size() < modSize)
			text.add(new String());
		
	//	text.add(entity);
		
		if (featureType != 0) {
			// add right modifiers
			for (int i=0; i<feature.getRightModifiersSize(); i++) {
				String modifier = feature.getRightModifier(i);
				// if modifier is equal entity break, else add
				text.add(modifier);
			}
			for (int j=feature.getRightModifiersSize(); j<modSize; j++)
				text.add(new String());
		}
		
		return text;
	}
	
	/**
	 * Fits input list to the window size removing and adding if necessary
	 * @param list : input list
	 * @param window : window size
	 * @param direction : list direction, -1 is left direction and 1 right direction
	 * @return
	 */
	public static List<String> fitWindow(List<String> list, int window, int direction) {
		
		// add values
		while (list.size() < window) {
			if (direction == -1)
				list.add(0, new String());
			else if (direction == 1)
				list.add(new String());
		}
		// remove values
		while (list.size() > window) {
			if (direction == -1)
				list.remove(0);
			else if (direction == 1)
				list.remove(list.size()-1);
		}
		
		return list;
	}
	
	/**
	 * Removes underscore character form list
	 * @param list : input list
	 * @return
	 */
	public static List<String> removeUnderscore(List<String> list) {
		List<String> l = new ArrayList<String>();
		for (String s : list) {
			l.add(s.replaceAll("_", " "));
		}
		return l;
	}
	
	/**
	 * Fits list to window size and removes underscore chars
	 * @param list : input list
	 * @param window : window size
	 * @param direction : list direction, -1 is left direction and 1 right direction
	 * @return
	 */
	public static List<String> cleanList(List<String> list, int window, int direction) {
		list = Utils.fitWindow(list, window, direction);
		return Utils.removeUnderscore(list);
	}
	
	/**
	 * Returns a List<String> with the input text split with 
	 * space char.
	 * It takes the last 'window' tokens.
	 * @param text : the input text
	 * @param window : the list size
	 * @return
	 */
/*	public static List<String> getLeftList(
			List<Feature> features, String token, int window) {
		
		List<String> list = new ArrayList<String>();
		int index = Utils.getIndex(features, token);
		
		for (int i=index-3)
		
		if (features.size() <= window)
			return features;
		else
			return features.subList(
					features.size()-window, features.size());
	}
	
	/**
	 * Returns a List<String> with the input text split with 
	 * space char.
	 * It takes the first 'window' tokens.
	 * @param text : the input text
	 * @param window : the list size
	 * @return
	 */
/*	public static List<String> getRightList(
			List<Feature> features, String token, int window) {
		
		List<String> list = new ArrayList<String>();
		int index = Utils.getIndex(features, token);
		
		if (features.size() <= window)
			return features;
		else
			return features.subList(0, window);
	}
	
	/**
	 * Adds training file extension
	 * @param name
	 * @return
	 */
	public static String addTrainExtension(String name) {
		return name+TRAIN_EXT;
	}
	
	/**
	 * Adds developing file extension
	 * @param name
	 * @return
	 */
	public static String addDevExtension(String name) {
		return name+DEV_EXT;
	}
	
	/**
	 * Adds testing file extension
	 * @param name
	 * @return
	 */
	public static String addTestExtension(String name) {
		return name+TEST_EXT;
	}
	
	/**
	 * Adds SVM model extension
	 * @param name
	 * @return
	 */
	public static String addModelExtension(String name) {
		return name+MODEL_EXT;
	}
	
	/**
	 * Adds model classification file extension
	 * @param name
	 * @return
	 */
	public static String addResultsExtension(String name) {
		return name+RESULTS_EXT;
	}
	
	/**
	 * Add dictionary extension
	 * @param name : corpus name
	 * @return
	 */
	public static String addDictionaryExtension(String name) {
		return name+DICTIONARY_EXT;
	}
	
	/**
	 * Add binarized extension
	 * @param name : corpus name
	 * @return
	 */
	public static String addBinarizedDictionaryExtension(String name) {
		return name+BINARIZED_DIC_EXT;
	}
	
	public static String addTF_IDF_Extension(String name) {
		return name+TFIDF_EXT;
	}
	
	public static String addBinarizedTF_IDF_Extension(String name) {
		return name+BINARIZED_TFIDF_EXT;
	}
	
	/**
	 * Get the input file without extension
	 * @param file
	 * @throws IOException 
	 */
	public static String getFileWhithoutExtension(File file) {
		String filename = file.getName();
		if (filename.indexOf(".") > 0) {
		    filename = filename.substring(0, filename.lastIndexOf("."));
		}
		return file.getAbsoluteFile().getParent()+File.separator+filename;
	}
	
	public static List<String> readStream(InputStream inStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		return read(br);
	}
	
	/**
	 * Reads a file and returns a list with the lines within the file.
	 * @param file : input file
	 * @return
	 * @throws IOException
	 */
	public static List<String> readFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		return read(br);
	}
	
	/**
	 * 
	 * Reads an input file and returns an array.
	 * The array contains each line of the file.
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static List<String> readFile(String file) throws IOException {
		FileReader fileread = new FileReader(file);
		BufferedReader br = new BufferedReader(fileread);
		return read(br);
	}
	
	public static List<String> read(BufferedReader br) throws IOException {
		
		List<String> list = new ArrayList<String>();
				
		// read lines and insert at list
		String line;
		while ((line = br.readLine()) != null) {
			if (line.length() > 0)
				list.add(line);
		}
		br.close();
		return list;
	}
	
	/**
	 * Writes the text at the buffered writer and inserts a new line.
	 * @param writer : the buffered writer
	 * @param text : input text
	 * @throws IOException
	 */
	public static void writeln(BufferedWriter writer, String text) throws IOException {
		writer.write(text);
		writer.newLine();
	}
	
	public static int atoi(String s) {
		return Integer.parseInt(s);
	}
	
	public static double atof(String s)
	{
		double d = Double.valueOf(s).doubleValue();
		if (Double.isNaN(d) || Double.isInfinite(d)) {
			throw new NumberFormatException("String '"+s+"' not a number or infinity");
		}
		return(d);
	}
	
	public static boolean atob(String s) {
		return Boolean.parseBoolean(s);
	}
	
	public static boolean isMultiple(double x, double y) {
		return (x % y == 0);
	}
	
	public static double round(double d, int decimals) {
		return Math.rint(d*(Math.pow(10, decimals)))/(Math.pow(10, decimals));
	}
	
	public static double precision(double TP, double FP) {
		
		double precision = 0.0;
		if ((TP + FP) != 0)
			precision = TP / (double)(TP + FP);
		precision = precision*100;
		precision = Math.rint(precision*100)/100;
		return precision;
	}
	
	public static double recall(double TP, double TOTAL) {
		
		double recall = 0.0;
		if (TOTAL != 0)
			recall = TP / (double)TOTAL;
		recall = recall*100;
		recall = Math.rint(recall*100)/100;
		return recall;
	}
	
	public static double fscore(double TP, double FP, double TOTAL) {
		
		double precision = 0.0;
		if ((TP + FP) != 0)
			precision = TP / (double)(TP + FP);
		
		double recall = 0.0;
		if (TOTAL != 0)
			recall = TP / (double)TOTAL;
		
		double fscore = 0.0;
		if ((precision + recall) != 0)
			fscore = 2 * ( (precision*recall) / (precision+recall) );
		fscore = fscore*100;
		fscore = Math.rint(fscore*100)/100;
		
		return fscore;
	}
	
	public static void removePunctuation(List<String> list) {
		
		for (int i=0; i<list.size(); i++) {
			if ( Utils.isPunctuation(list.get(i)) ) {
				list.remove(i);
				i--;
			}
		}
	}
	
	public static boolean isPunctuation(String s) {
		if (s.length() > 1 || s.length() == 0)
			return false;
		else {
			char c = s.charAt(0);
			return !Character.isSpaceChar(c) && !Character.isLetterOrDigit(c);
			/*return c == ',' || c == '.' || c == ':' || c == ';' || c == '!' 
        	|| c == '¡' || c == '?' || c == '¿' || c == '«' || c == '»' 
        	|| c == '“' || c == '”' || c == '‘' || c == '’' || c == '"' 
        	|| c == '(' || c == ')' || c == '[' || c == ']' || c == '{'
        	|| c == '}' || c == '…' || c == '|' || c == '\\'|| c == '/'
        	/* || c == '+' || c == '*' */  
         //   ;
		}
    }
	
	public static void removeBeforePoint(List<String> list) {
		
		for (int i=0; i<list.size(); i++) {
			String text = list.get(i);
			if (text.equalsIgnoreCase(".") ) {
				while (i > 0) {
					list.remove(i-1);
					i--;
				}
				break;
			}
		}
	}

	public static void removeAfterPoint(List<String> list) {
		
		for (int i=0; i<list.size(); i++) {
			String text = list.get(i);
			if (text.equalsIgnoreCase(".") ) {
				while (i+1 < list.size())
					list.remove(i+1);
				break;
			}
		}
	}
	
	/**
	 * Returns true if the 2 lists contain the same elements at
	 * the same position
	 * @param list : input list
	 * @param other : input other list
	 * @return
	 */
	public static boolean contains(List<String> list, List<String> other) {
		
		if (list.size() != other.size()) return false;
		
		boolean equal = true;
		for (int i=0; i<list.size(); i++) {
			if (!list.get(i).contentEquals(other.get(i))) {
				equal = false;
				break;
			}
		}
		return equal;
	}
	
	public static String getCoveredText(Span span, String[] text) {
		
		int start = span.getStart();
		int end = span.getEnd();
		
		String coveredText = new String();
		for (int i=start; i<end; i++) {
			coveredText += text[i]+" ";
		}
		coveredText = coveredText.trim();
		
		return coveredText;
	}
	
	public static boolean isBinarizedDictionary(File dictionary) {
		return dictionary.getAbsolutePath().endsWith(BINARIZED_DIC_EXT);
	}
	
	public static boolean isBinarizedTF_IDF(File dictionary) {
		return dictionary.getAbsolutePath().endsWith(BINARIZED_TFIDF_EXT);
	}
}
