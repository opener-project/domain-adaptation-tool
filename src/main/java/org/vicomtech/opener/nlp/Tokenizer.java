package org.vicomtech.opener.nlp;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vicomtech.opener.utils.Exec;
import org.vicomtech.opener.utils.Language;

/**
 * This class uses the moses tokenizer in order to tokenize text
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Tokenizer {

	private Language lang;
	
	private String tokenized;
	private String entity;
	private String leftText;
	private String rightText;
	private String leftToken;
	private String rightToken;
	
	/**
	 * Commands to exec the tokenizer
	 */
	private static final String CMD_TOK = "perl Tokenizer/tokenizer.perl -l ";
	private static final String CMD_DEESCAPE = "perl Tokenizer/deescape-special-chars.perl";
	
	public Tokenizer(Language language) {
		this.lang = language;
		
		this.tokenized = new String();
		this.entity = new String();
		this.leftText = new String();
		this.rightText = new String();
		this.leftToken = new String();
		this.rightToken = new String();
	}
	
	/**
	 * Tokenizes the input text
	 * @param text : input text
	 * @return tokenized text
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String tokenize(String text) throws IOException, InterruptedException {
		
		text = text.replaceAll("(\\w') ", "$1");
		
		String command = CMD_TOK+this.lang.toString();
		text = Exec.executeCommandInputOutput(CMD_DEESCAPE, text);
		text = Exec.executeCommandInputOutput(command, text);
		text = Exec.executeCommandInputOutput(CMD_DEESCAPE, text);
		
		return text;
	
	}
	
	/**
	 * Tokenizes the input text and gets the left and right token of
	 * the entity.
	 * @param entity : the entity
	 * @param leftText : left text of the entity
	 * @param rightText : right text of the entity
	 * @return if the right and left tokens are found
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean tokenize(
			String entity, String leftText, String rightText) throws IOException, InterruptedException {
		
		String text = leftText+entity+rightText;
		
		text = text.replaceAll("(\\w') ", "$1");
		
		String command = CMD_TOK+this.lang;
		text = Exec.executeCommandInputOutput(CMD_DEESCAPE, text);
		text = Exec.executeCommandInputOutput(command, text);
		text = Exec.executeCommandInputOutput(CMD_DEESCAPE, text);
		
		// identify the tokenized entity and left/write text
		Pattern pat = Pattern.compile(Tokenizer.getPattern(entity));
				
		Matcher mt = pat.matcher(text);
				
		while (mt.find()) {
			
			if (Tokenizer.areSimilar(leftText, text.substring(0, mt.start()))) {
				
				// get text
				this.entity = mt.group().replace("\n", " ").trim().replace(" ", "_");
				this.leftText = text.substring(0, mt.start());
				this.rightText = text.substring(mt.end(), text.length());
				
				// 'join' paragraphs
				this.leftText = this.leftText.replace("\n", " ")
						.replaceAll(" +", " ").trim();
				this.rightText = this.rightText.replace("\n", " ")
						.replaceAll(" +", " ").trim();
				this.tokenized = this.leftText+" "
						+this.entity+" "+this.rightText;
				
				// get left and right tokens
				String[] leftToks = this.leftText.split(" ");
				this.leftToken = leftToks[leftToks.length-1];
				this.rightToken = this.rightText.split(" ")[0];
				
				return true;
			}
		}
		
		return false;
	}
	
	public String getTokenized() {
		return this.tokenized;
	}
	
	public String getEntity() {
		return this.entity;
	}

	public String getLeftText() {
		return this.leftText;
	}
	
	public String getRightText() {
		return this.rightText;
	}
	
	public String getLeftToken() {
		return this.leftToken;
	}
	
	public String getRightToken() {
		return this.rightToken;
	}
	
	/**
	 * Analyzes the input string and return a pattern to match
	 * with tokenized text
	 * @param s : input text
	 * @return pattern
	 */
	public static String getPattern(String s) {
		
		String trace = s;
		
		trace = trace.replace("\\", "\\\\");
		trace = trace.replace("/", "\\/");
		trace = trace.replace("^", "\\^");
		trace = trace.replace(".", "\\.");
		trace = trace.replace("?", "\\?");
		trace = trace.replace("!", "\\!");
		trace = trace.replace("+", "\\+");
		trace = trace.replace("*", "\\*");
		trace = trace.replace("-", "\\-");
		trace = trace.replace("[", "\\[");
		trace = trace.replace("]", "\\]");
		trace = trace.replace("(", "\\(");
		trace = trace.replace(")", "\\)");
		trace = trace.replace("{", "\\{");
		trace = trace.replace("}", "\\}");
		trace = trace.replace("$", "\\$");
		trace = trace.replace("|", "\\|");
		trace = trace.replace(" ", " ?");
		
		trace = trace.replace("0", " ?0 ?");
		trace = trace.replace("1", " ?1 ?");
		trace = trace.replace("2", " ?2 ?");
		trace = trace.replace("3", " ?3 ?");
		trace = trace.replace("4", " ?4 ?");
		trace = trace.replace("5", " ?5 ?");
		trace = trace.replace("6", " ?6 ?");
		trace = trace.replace("7", " ?7 ?");
		trace = trace.replace("8", " ?8 ?");
		trace = trace.replace("9", " ?9 ?");
		trace = trace.replace("@", " ?@ ?");
		trace = trace.replace("'", " ?' ?");
		trace = trace.replace("_", " ?_ ?");
		trace = trace.replace(",", " ?, ?");
		trace = trace.replace(";", " ?; ?");
		trace = trace.replace(":", " ?: ?");
		trace = trace.replace("\"", " ?\" ?");
		trace = trace.replace("\\?", " ?\\? ?");
		trace = trace.replace("\\*", " ?\\* ?");
		trace = trace.replace("\\+", " ?\\+ ?");
		trace = trace.replace("\\-", " ?\\- ?");
		trace = trace.replace("\\^", " ?\\^ ?");
		trace = trace.replace("\\!", " ?\\! ?");
		trace = trace.replace("\\(", " ?\\( ?");
		trace = trace.replace("\\)", " ?\\) ?");
		trace = trace.replace("\\[", " ?\\[ ?");
		trace = trace.replace("\\]", " ?\\] ?");
		trace = trace.replace("\\/", " ?\\/ ?");
		trace = trace.replace("\\.", " ?\\. ?");
		
		return trace;
	}
	
	/**
	 * True if 'text1' is a detokenized version of 'text2'
	 * @param text1 : detokenized text
	 * @param text2 : tokenized text
	 * @return
	 */
	public static boolean areSimilar(String text1, String text2) {
		
		Pattern pat = Pattern.compile(Tokenizer.getPattern(text1));
		Matcher mt = pat.matcher(text2);
		
		return mt.find();
	}

}
