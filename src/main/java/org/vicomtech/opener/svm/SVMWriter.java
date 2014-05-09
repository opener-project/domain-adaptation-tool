package org.vicomtech.opener.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vicomtech.libsvm.Instances;
import org.vicomtech.opener.bootstrapping.Parser;
import org.vicomtech.opener.bootstrapping.SeedUtils;
import org.vicomtech.opener.dictionary.Dictionary;
import org.vicomtech.opener.entities.Entity;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.utils.Utils;

/**
 * This class parses entities to vectors
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class SVMWriter {

	/**
	 * Seed mappings: Map<Category,List<Entity>
	 */
	private Map<String, List<Entity>> entities = null;
	private Map<String, List<Entity>> seedEntities = null;
	
	/**
	 * Mapping for seed statistics: Map<Category,SumOfSeeds>
	 */
	private Map<String, Integer> seedStats = null;
	
	/**
	 * The seed list
	 */
	//TODO
//	private List<String> seedList = null;
	private Map<String,List<String>> seedList = null;
	
	/**
	 * Output training file mapping: Map<Category,File>
	 */
	private Map<String,BufferedWriter> trainFileWriterMap = null;
	
	/**
	 * Unannotated training file
	 */
	private File unannotatedFile;
	
	
	private int window = 3;
	
	/**
	 * Balanced | unbalanced seed train list boolean
	 */
	private boolean balanced;
	private boolean tfidf;
	private Dictionary dictionary;
	
	/**
	 * The Message Handlers
	 */
	private MessageHandler message;
	
	//TODO
//	public SVMWriter(Map<String, List<Entity>> entities, List<String> seedList,
//			int window, boolean balanced, Dictionary dictionary, 
//			Map<String,File> fileMap, File unannotatedFile, boolean tfidf) throws IOException {
//		this.tfidf = tfidf;
//		this.entities = entities;
//		this.seedList = seedList;
//		this.window = window;
//		this.balanced = balanced;
//		this.dictionary = dictionary;
//		this.unannotatedFile = unannotatedFile;
//		
//		this.seedEntities = new HashMap<String,List<Entity>>();
//		
//		this.createTrainFileWriterMap(fileMap);
//		
//		this.seedStats = new HashMap<String,Integer>();
//		for (String category : entities.keySet())
//			this.seedStats.put(category, new Integer(0));
//	}
	
	public SVMWriter(Map<String,List<Entity>> entities, Map<String,List<String>> seedList,
			int window, boolean balanced, Dictionary dictionary, 
			Map<String,File> fileMap, File unannotatedFile, boolean tfidf,
			MessageHandler message) throws IOException {
		this.message = message;
		this.tfidf = tfidf;
		this.entities = entities;
		this.seedList = seedList;
		this.window = window;
		this.balanced = balanced;
		this.dictionary = dictionary;
		this.unannotatedFile = unannotatedFile;
		
		this.seedEntities = new HashMap<String,List<Entity>>();
		
		this.createTrainFileWriterMap(fileMap);
		
		this.seedStats = new HashMap<String,Integer>();
		
		//TODO
		for (String category : seedList.keySet())
			this.seedStats.put(category, new Integer(0));
//		for (String category : entities.keySet())
//			this.seedStats.put(category, new Integer(0));
	}
	
	private void createTrainFileWriterMap(Map<String,File> fileMap) throws IOException {
		
		this.trainFileWriterMap = new HashMap<String,BufferedWriter>();
		for (String category : fileMap.keySet()) {
			File file = fileMap.get(category);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			this.trainFileWriterMap.put(category, bw);
		}
	}
	
	private void closeTrainFileWriterMap() throws IOException {
		
		for (String category : this.trainFileWriterMap.keySet()) {
			BufferedWriter bw = this.trainFileWriterMap.get(category);
			bw.close();
		}
	}
	//TODO
//	public static void writeTrainFiles(Map<String,List<Entity>> entities,
//			List<String> seedList, int window, boolean balanced, Dictionary dictionary, 
//			Map<String,File> fileMap, File unannotatedFile, boolean tfidf) throws IOException {
//		
//		SVMWriter svm = new SVMWriter(entities, seedList, window,
//				balanced, dictionary, fileMap, unannotatedFile, tfidf);
//		
//		svm.writeTrainFiles();
//		
//		svm.printTrainStatistics();
//	}
	
	public static void writeTrainFiles(Map<String,List<Entity>> entities,
			Map<String,List<String>> seedList, int window, boolean balanced, Dictionary dictionary, 
			Map<String,File> fileMap, File unannotatedFile, boolean tfidf,
			MessageHandler message) throws IOException {
		
		SVMWriter svm = new SVMWriter(entities, seedList, window,
				balanced, dictionary, fileMap, unannotatedFile, tfidf, message);
		
		svm.writeTrainFiles();
		
		svm.printTrainStatistics();
	}
	
	public void writeTrainFiles() throws IOException {
		
		BufferedWriter unannotatedBW = new BufferedWriter(
				new FileWriter(this.unannotatedFile));
		
		// for each category
		for (String category : this.entities.keySet()) {
			List<Entity> entityList = this.entities.get(category);
			
			// for each entity
			for (Entity entity : entityList) {
				
				if (entity.hasFeatures()) {
					
					// get category label
					String categoryLabel = Double.toString((SVM.NEGATIVE_CLASS));
					if (!category.equalsIgnoreCase(Parser.EMPTY_CATEGORY)) {
						categoryLabel = this.dictionary.getLabel(category);
					}
					
					// get entity parsed to SVM
					String line = getLine(entity, this.dictionary, this.tfidf, true);
					
					if (SeedUtils.isAtSeedList(entity.getEntity(), this.seedList)) {
						//TODO
						String cat = SeedUtils.getCategory(entity.getEntity(), this.seedList);
						categoryLabel = this.dictionary.getLabel(cat);
					/*	if (entity.getEntity().contentEquals("Ejecutivo")) {
							
							System.out.println("left: "+entity.getLeftFeatures());
							System.out.println("entity: "+entity.getEntity());
							System.out.println("right: "+entity.getRightFeatures());
							System.out.println();
						}*/
						
						// increment statistics
						this.seedStats.put(cat, this.seedStats.get(cat)+1);
						
						// add seed to the seed mapping
						if (this.seedEntities.containsKey(cat)) {
							this.seedEntities.get(cat).add(entity);
						}
						else {
							this.seedEntities.put(cat, new ArrayList<Entity>());
							this.seedEntities.get(cat).add(entity);
						}
						
						// if output seed train files are unbalanced,
						// write each seed at all classifiers
						if (!this.balanced) {
							
							// write at category train file
							this.writeAtCategory(cat, categoryLabel+line);
							// write at other category train files
							this.writeAtOtherCategories(cat, SVM.NEGATIVE_CLASS+line);
						}
					}
					else {
						
						// write at unannoted file
						Utils.writeln(unannotatedBW, categoryLabel+line);
					}
				}
			}
		}
		
		if (this.balanced)
			// write seeds balanced
			this.writeSeedsBalanced();
		
		// close writers
		this.closeTrainFileWriterMap();
		unannotatedBW.close();
	}
	
	private void writeSeedsBalanced() throws IOException {
		
		// for each category
		for (String category : this.seedEntities.keySet()) {
			List<Entity> entityList = this.seedEntities.get(category);
			
			// get how many seeds will be written in each other categories:
			//           category_total_seeds / (categories-1)
			int seedCount = (int)  (( this.seedEntities.get(category).size()
						/ (this.seedEntities.keySet().size()-1) ) )  ;
			
			// counter to control how much seed we are writing and set
			// to control in what categories we want to write
			int cont = 0;
			Set<String> notWritingCategories = new HashSet<String>();
			notWritingCategories.add(category);
			
			// for each entity
			for (Entity entity : entityList) {
				
				if (entity.hasFeatures()) {
					// get category label
					String categoryLabel = this.dictionary.getLabel(category);
					// get entity parsed to SVM
					String line = getLine(entity, this.dictionary, this.tfidf, true);
					
					// write at category train file
					this.writeAtCategory(category, 
							categoryLabel+line);
					
					// write at other category (we write only at one other category)
					for (String otherCategory : this.seedEntities.keySet()) {
							
						if (!notWritingCategories.contains(otherCategory)) {
							this.writeAtCategory(otherCategory, SVM.NEGATIVE_CLASS+line);
							cont++;
							// if at other category we have written the enough seeds,
							// reinitialize counter and put other category at not writing set
							if (cont >= seedCount) {
								notWritingCategories.add(otherCategory);
								cont=0;
							}
							break;
						}
					}
				}
			}
		}
	}
	
	private void writeAtCategory(String category, String text) throws IOException {
		
		Utils.writeln(this.trainFileWriterMap.get(category), text);
	}
	
	private void writeAtOtherCategories(String category, String text) throws IOException {
		
		// for each category...
		for (String otherCategory : this.trainFileWriterMap.keySet()) {
			// if is other category write
			if (!otherCategory.equalsIgnoreCase(category)) {
				Utils.writeln(this.trainFileWriterMap.get(
						otherCategory), text);
			}
		}
	}
	
	private void writeAtOtherCategoriesBalanced(String category, String text) throws IOException {
		
		// for each category...
		for (String otherCategory : this.trainFileWriterMap.keySet()) {
			// if is other category write
			if (!otherCategory.equalsIgnoreCase(category)) {
				Utils.writeln(this.trainFileWriterMap.get(
						otherCategory), text);
			}
		}
	}
	
	private void printTrainStatistics() {
		this.message.displayTextln("");
		this.message.displayTextln("TRAIN FILE STATISTICS:");
		this.message.displayTextln("  Seed files:");
		for (String category : this.seedStats.keySet())
			this.message.displayTextln("    "+category+": "+this.seedStats.get(category));
		this.message.displayTextln("  Total statistics:");
		for (String category : this.entities.keySet())
			this.message.displayTextln("    "+category+": "+this.entities.get(category).size());
		this.message.displayTextln("");
	}
	
	public static void writeTestFile(Map<String,List<Entity>> entities,
			int window, Dictionary dictionary, 
			File file, boolean tfidf, MessageHandler message) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		// for each category...
		for (String category : entities.keySet()) {
			// write entites
			writeTestFile(entities.get(category), window, dictionary, bw, tfidf);
		}
		bw.close();
		
		printTestStatistics(entities, message);
	}
	
	private static void writeTestFile(List<Entity> entities,
			int window, Dictionary dictionary, 
			BufferedWriter bw, boolean tfidf) throws IOException {
		
		// for each entity...
		for (Entity entity : entities) {
			// get category label
			String label = Double.toString(SVM.NEGATIVE_CLASS);
			if (!entity.getCategory().equalsIgnoreCase(Parser.EMPTY_CATEGORY)) {
				label = dictionary.getLabel(entity.getCategory());
			}
			// get entity parsed to SVM
			String line = getLine(entity, dictionary, tfidf, true);
			if (line.length() > 1) {
				// write
				Utils.writeln(bw, label+line);
			}
		}
	}
	
	public static String getLine(Entity entity,
								 Dictionary dictionary,
								 boolean tfidf,
								 boolean raiseError) {
		String line = " ";
		int feature = 1;
		
		// add left features
		for (String tok : entity.getLeftFeatures()) {
			line += parseFeature(feature, tok, dictionary, raiseError);
			feature++;
		}
		
		// add middle features
		for (String tok : entity.getMiddleFeatures()) {
			line += parseFeature(feature, tok, dictionary, raiseError);
			feature++;
		}
		
		// add right features
		for (String tok : entity.getRightFeatures()) {
			line += parseFeature(feature, tok, dictionary, raiseError);
			feature++;
		}
		
		line = line.substring(0, line.length()-1);
		if (tfidf)
			line += " "+Instances.ENTITY_DELIMITER+entity.getEntity().replaceAll(" ", "_");
		return line;
	}
	
	private static String parseFeature(int index, String token, Dictionary dictionary, boolean raiseError) {
		String line = new String();
		if (token.length() > 0) {
			if (raiseError) {
				line += index+":"+dictionary.getValue(token)+" ";
			}
			else {
				try {
					line += index+":"+dictionary.getValue(token)+" ";
				}
				catch (NullPointerException npe) {
				}
			}
		}
		return line;
	}
	
	private static void printTestStatistics(Map<String,List<Entity>> entities,
										    MessageHandler message) {
		message.displayTextln("");
		message.displayTextln("TEST FILE STATISTICS:");
		for (String category : entities.keySet())
			message.displayTextln("  "+category+": "+entities.get(category).size());
		message.displayTextln("");
	}
	
	/*private static String getLine(Entity entity, int window,
			Dictionary dictionary) {
		
		String line = " ";
		
		// get correct feature index in case of leftText size
		// has no enough features
		int feature = 1;
		while (window > entity.getLeftFeatures().size()) {
			feature++;
			window--;
		}
		
		// add leftText features
		if (entity.getLeftFeatures().size() > 0) {
			for (String tok : entity.getLeftFeatures()) {
				line += feature+":"+dictionary.getValue(tok)+" ";
				feature++;
			}
		}
		
		// add righText features
		if (entity.getRightFeatures().size() > 0) {
			for (String tok : entity.getRightFeatures()) {
				line += feature+":"+dictionary.getValue(tok)+" ";
				feature++;
			}
		}
		
		line = line.substring(0, line.length()-1);
		return line;
	}*/

}
