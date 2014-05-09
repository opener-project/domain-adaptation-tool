package org.vicomtech.opener.entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.util.Span;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.NoSuchDirectoryException;
import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.nlp.NERC;
import org.vicomtech.opener.utils.Exec;
import org.vicomtech.opener.utils.Exec.ExecException;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.Utils;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.GeoParserFactory;
import com.bericotech.clavin.index.IndexDirectoryBuilder;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * This class generates features for the input entity
 * 
 * org.vicomtech.opener.entities is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class FeatureGenerator {

	private Entity entity;
	private int window;
	private static final int PREFIX_SUFFIX_LENGTH = 2;
	private GeoParser geoparser;
	private NERC nerc;
	private MessageHandler message;
	
	private final static String LUCENE_INDEX = "./IndexDirectory";
	private final static String GEO_NAMES_PATH = "./allCountries.txt";
	private final static String GEO_NAMES_ZIP_PATH = "./allCountries.zip";
	private final static String GEO_NAMES_COMMAND = "curl -O http://download.geonames.org/export/dump/allCountries.zip";
	private final static String REMOVE_INDEX_COMMAND = Exec.REMOVE_CMD+LUCENE_INDEX;
	private final static String UNZIP_GEO_NAMES_COMMAND = "unzip ./allCountries.zip";
	
	public FeatureGenerator(int window, Language language, MessageHandler message) throws IOException, ParseException, InterruptedException, ExecException {
		this.message = message;
		this.window = window;
		try {
			this.geoparser = GeoParserFactory.getDefault(LUCENE_INDEX);
		}
		catch (NoSuchDirectoryException ex) {
			this.createLuceneIndex();
			this.geoparser = GeoParserFactory.getDefault(LUCENE_INDEX);
		}
		this.nerc = new NERC(language);
	}

	/**
	 * Creates lucene index using CLAVIN (https://github.com/Berico-Technologies/CLAVIN)
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecException
	 */
	private void createLuceneIndex() throws IOException, InterruptedException, ExecException {
		this.message.displayTextln(String.format("Lucene index for gazetteer not found at '%s'", LUCENE_INDEX));
		this.message.displayTextln("Creating new index............");
		File geonames = new File(GEO_NAMES_PATH);
		if (!geonames.exists()) {
			this.message.displayTextln("Downloading the latest version of allCountries.zip gazetteer file from GeoNames.org....");
			if (Exec.executeCommand(GEO_NAMES_COMMAND) != 0) {
				throw new ExecException(String.format("could not execute command: %s", GEO_NAMES_COMMAND));
			}
			this.message.displayTextln("Unziping allCountries.zip file............");
			if (Exec.executeCommand(UNZIP_GEO_NAMES_COMMAND) != 0) {
				throw new ExecException(String.format("could not execute command: %s", UNZIP_GEO_NAMES_COMMAND));
			}
		}
		
		try {
			this.message.displayTextln("Building lucene index............");
			IndexDirectoryBuilder.main(new String[0]);
			File geonamesZip = new File(GEO_NAMES_ZIP_PATH);
			this.message.displayTextln("Lucene index created!");
			geonames.delete();
			geonamesZip.delete();
		}
		catch (FileNotFoundException e) {
			Exec.executeCommand(REMOVE_INDEX_COMMAND);
			throw new FileNotFoundException(e.getMessage());
		}
	}
	
	public void setEntity(Entity e) {
		this.entity = e;
	}
	
	public Entity getEntity() {
		return this.entity;
	}
	
	public void addLemmas(String[] leftLemmas, String[] rightLemmas) {
		
		// convert to list
		List<String> leftFeatures = new LinkedList<String>(Arrays.asList(leftLemmas));
		List<String> rightFeatures = new LinkedList<String>(Arrays.asList(rightLemmas));
		
		// clean lists
		leftFeatures = Utils.cleanList(leftFeatures, this.window, -1);
		rightFeatures = Utils.cleanList(rightFeatures, this.window, 1);
		
		// add features
		this.entity.addRightFeatures(leftFeatures);
		this.entity.addRightFeatures(rightFeatures);
	}

	public void addPoS(String[] leftPoS, String[] rightPoS) {
		
		// convert to list
		List<String> leftFeatures = new LinkedList<String>(Arrays.asList(leftPoS));
		List<String> rightFeatures = new LinkedList<String>(Arrays.asList(rightPoS));
		
		// clean lists
		leftFeatures = Utils.cleanList(leftFeatures, this.window, -1);
		rightFeatures = Utils.cleanList(rightFeatures, this.window, 1);
		
		// add features
		this.entity.addRightFeatures(leftFeatures);
		this.entity.addRightFeatures(rightFeatures);
	}
	
	public void addFirstWord(String leftWord) {
		
		String firstWord = "0";
		if (leftWord.length() > 0) {
			firstWord = "1";
		}
		this.entity.addRightFeature(firstWord);
	}
	
	public void addLength() {
		/*
		String entity = this.entity.getEntity();
		String length = "1";
		if (entity.length() < 3) {
			length = "0";
		}
		this.entity.addRightFeature(length);
		*/
		String[] entity = this.entity.getEntity().split(" ");
		String length = "1";
		if (entity.length == 1) {
			length = "0";
		}
		this.entity.addRightFeature(length);
	}
	
	public void addPrefix() {
		String entity = this.entity.getEntity();
		String prefix = new String();
		if (entity.length() > PREFIX_SUFFIX_LENGTH) {
			prefix = entity.substring(0, PREFIX_SUFFIX_LENGTH);
		}
		this.entity.addRightFeature(prefix);
	}
	
	public void addPrefix(String word) {
		String prefix = new String();
		if (word.length() > PREFIX_SUFFIX_LENGTH) {
			prefix = word.substring(0, PREFIX_SUFFIX_LENGTH);
		}
		this.entity.addRightFeature(prefix);
	}
	
	public void addPrefixes(String[] leftWords, String[] rightWords) {
		
		// convert to list
		List<String> leftFeatures = new LinkedList<String>(Arrays.asList(leftWords));
		List<String> rightFeatures = new LinkedList<String>(Arrays.asList(rightWords));
				
		// clean lists
		leftFeatures = Utils.cleanList(leftFeatures, this.window, -1);
		rightFeatures = Utils.cleanList(rightFeatures, this.window, 1);
		
		for (String word : leftFeatures)
			this.addPrefix(word);
		for (String word : rightFeatures)
			this.addPrefix(word);
	}
	
	public void addSuffix() {
		String entity = this.entity.getEntity();
		String suffix = new String();
		if (entity.length() > PREFIX_SUFFIX_LENGTH) {
			suffix = entity.substring(entity.length()-PREFIX_SUFFIX_LENGTH, entity.length());
		}
		this.entity.addRightFeature(suffix);
	}
	
	public void addSuffix(String word) {
		String suffix = new String();
		if (word.length() > PREFIX_SUFFIX_LENGTH) {
			suffix = word.substring(word.length()-PREFIX_SUFFIX_LENGTH, word.length());
		}
		this.entity.addRightFeature(suffix);
	}
	
	public void addSuffixes(String[] leftWords, String[] rightWords) {
		
		// convert to list
		List<String> leftFeatures = new LinkedList<String>(Arrays.asList(leftWords));
		List<String> rightFeatures = new LinkedList<String>(Arrays.asList(rightWords));
						
		// clean lists
		leftFeatures = Utils.cleanList(leftFeatures, this.window, -1);
		rightFeatures = Utils.cleanList(rightFeatures, this.window, 1);
				
		for (String word : leftFeatures)
			this.addSuffix(word);
		for (String word : rightFeatures)
			this.addSuffix(word);
	}
	
	public void addGazetteer(String[] leftWords, String[] rightWords) throws Exception {
		
		// get text
		String[] textArray = join(leftWords, this.entity.getEntity(), rightWords);
		
		// find entities
		Span[] spans = this.nerc.find(textArray);
		
		// get start and end indexes of entity
		int start = leftWords.length;
		int end = leftWords.length+this.entity.getEntity().split(" ").length;
		
		// add entity gazetteer info
		if (rightWords.length == 0) {
			System.in.toString();
		}
		this.addGazetteer(textArray, spans, start, end);
	}
	
	private void addGazetteer(String[] text, Span[] spans, int start, int end) throws Exception {
				
		String coveredText = new String();
		boolean found = false;
		// for each span ...
		for (int i=0; i<spans.length; i++) {
			Span span = spans[i];
			int startSpan = span.getStart();
			int endSpan = span.getEnd();
			
			// if entity found inside out entity
			if (startSpan >= start && endSpan <= end) {
				// if is location get covered text
				if (span.getType().equals("location")) {
					coveredText = Utils.getCoveredText(span, text);
					found = true;
				}
				break;
			}
		}
				
		// if entity is location find out if is geoname
		if (found) {
			if (this.isGeoName(coveredText))
				this.entity.addRightFeature("1");
			else
				this.entity.addRightFeature("0");
		}
		else {
			this.entity.addRightFeature("0");
		}
	}
	
	public boolean isGeoName(String inputText) throws Exception {
		List<ResolvedLocation> resolvedLocations = this.geoparser.parse(inputText);
        return resolvedLocations.size() > 0;
    }
	
	private static String[] join(String[] leftArray, String middle, String[] rightArray) {
		
		String text = StringUtils.join(leftArray, " ") + " ";
		text += middle + " ";
		text += StringUtils.join(rightArray, " ");
		String[] textArray = text.split(" ");
		
		return textArray;
	}

}
