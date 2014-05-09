package org.vicomtech.opener.utils;

import java.io.IOException;
import java.util.Map;

/**
 * This class implements languages supported
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Language extends ResourceLoader {

	public static class LanguageException extends Exception {
		
		private static final long serialVersionUID = 1L;

		public LanguageException(String s) {
			super(s);
		}
	}
	
	/**
	 * Current language
	 */
	private String lang;
	
	private static final String CONFIG_FILE_PATH = "./config/language.conf";
	private static Map<String,String> langMap = null; 
	
	/**
	 * Public constructor, if language is not supported
	 * raises IOException
	 * @param lang
	 * @throws LanguageException 
	 * @throws IOException
	 */
	public Language (String lang) throws LanguageException, IOException {
		if (langMap == null) {
			langMap = super.loadMap(CONFIG_FILE_PATH, false);
		}
		this.lang = getLanguage(lang);
	}
	
	/**
	 * Gets the enumeration of the language string value,
	 * if language is not supported raises IOException
	 * @param lang
	 * @return
	 * @throws LanguageException 
	 * @throws IOException
	 */
	private static String getLanguage(String lang) throws LanguageException {
		if (langMap.containsKey(lang))
			return lang;
		else
			throw new LanguageException(String.format("language '%s' not supported", lang));
	}
	
	/**
	 * Returns if language is supported
	 * @param lang
	 * @return
	 * @throws LanguageException 
	 */
	public static boolean isLanguage(String lang) throws LanguageException {
		try {
			getLanguage(lang);
			return true;
		}
		catch (LanguageException ex) {
			return false;
		}
	}
	
	/**
	 * Prints language
	 */
	public String toString() {
		return this.lang;
	}
	
	public String getValue() {
		return langMap.get(this.lang);
	}
	
	public boolean equals(Language lang) {
		return this.lang.compareTo(lang.lang) == 0;
	}

}
