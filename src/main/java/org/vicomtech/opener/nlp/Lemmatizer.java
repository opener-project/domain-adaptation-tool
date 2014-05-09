package org.vicomtech.opener.nlp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;

import org.vicomtech.opener.utils.Language;

/**
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Lemmatizer {

	private static final String FR_SERIALIZED_LEMMATIZATION_MAP = "/FrenchLemmatizationMap.ser";

	private static Map<String, String> lemmatizationMap;

	public Lemmatizer(Language language) {
		if (lemmatizationMap == null || lemmatizationMap.isEmpty()) {
			readSerializedMap(language);
		}
	}

	@SuppressWarnings("unchecked")
	private void readSerializedMap(Language language) {
		try {
			InputStream is = null;
			if (language.toString().equalsIgnoreCase("fr")) {
				is = Lemmatizer.class
					.getResourceAsStream(FR_SERIALIZED_LEMMATIZATION_MAP);
			// is = new FileInputStream(dir + "FrenchLemmatizationMap.ser");
			}
			InputStream buffer = new BufferedInputStream(is);
			ObjectInput input = new ObjectInputStream(buffer);
			lemmatizationMap = (Map<String, String>) input.readObject();
			input.close();
			is.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLemma(String surfaceForm, String postag) {
		
		postag = TagsetMappings.convertFromFtbToKaf(postag);
		String lemmasAndTags = lemmatizationMap.get(surfaceForm.toLowerCase());
		
		if (lemmasAndTags == null) {
			return surfaceForm;
		}
		
		String[] splittedLemmasAndTags = lemmasAndTags.split(" ");
		for (int i = 0; i < splittedLemmasAndTags.length - 1; i += 2) {
			String currentLemma = splittedLemmasAndTags[i];
		//	String currentTag = splittedLemmasAndTags[i + 1];
			String currentTag = TagsetMappings
					.convertFromLemmatagsToKaf(splittedLemmasAndTags[i + 1]);
		/*	System.out.println(postag);
			System.out.println(currentTag);
			System.out.println();
		*/	if (currentTag.equalsIgnoreCase(postag)) {
				return currentLemma;
			}
		}
		return surfaceForm;
	}
	
	public String[] getLemmas(String surfaceForm) {

		String[] array = new String[1];
		array[0] = surfaceForm;
		
		String lemmasAndTags = lemmatizationMap.get(surfaceForm.toLowerCase());
		if (lemmasAndTags == null) {
			return array;
		}
		
		String[] splittedLemmasAndTags = lemmasAndTags.split(" ");
		array = new String[splittedLemmasAndTags.length / 2];
		
		int j=0;
		for (int i = 0; i < splittedLemmasAndTags.length - 1; i += 2) {
			String currentLemma = splittedLemmasAndTags[i];
			array[j]=currentLemma;
			j++;
		}
		return array;
	}
}
