package org.vicomtech.opener.nlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.vicomtech.opener.utils.Language;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * This class detects sentences using OpenNLP models.
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class SentenceDetector {
	
	private static final String FR_MODEL_PATH = "/fr-sent.bin";
	private SentenceDetectorME detector; 
	
	private String sentence;
	private String token;
	private String leftToken;
	private String rightToken;
	
	public SentenceDetector(Language language) throws InvalidFormatException, IOException {
		
		InputStream modelIn = null;
		if (language.toString().equalsIgnoreCase("fr")) {
			modelIn = PoSTagger.class.getResourceAsStream(
					FR_MODEL_PATH);
		}
		detector = new SentenceDetectorME(new SentenceModel(modelIn));
		modelIn.close();
	}
	
	public String[] detect(String text) {
		String sentences[] = this.detector.sentDetect(text);
		return sentences;
	}
	
	public int getSentenceAndLeftRightTokens(
			String[] sentences, String token, String left,
			String right) {
		
		//System.out.println("\nSENTTOKEN: "+token);
		//System.out.println("SENTLEFT: "+left);
		//System.out.println("SENTRIGHT: "+right);
		
		int index = -1;
		String leftTok = new String();
		String rightTok = new String();
		
		// for each sentence....
		for (int i=0; i<sentences.length; i++) {
			String sentence = sentences[i];
			//System.out.println("SENT: "+sentence);
			// get token index
			index = sentence.indexOf(token);
			// if token exists at sentence...
			while (index > -1) {
				// get left and right token
				String[] leftToks = sentence.substring(0, index)
						.trim().split(" ");
				String[] rightToks = sentence.substring(index+token.length(), sentence.length())
						.trim().split(" ");
				leftTok = leftToks[leftToks.length-1];
				rightTok = rightToks[0];
				// if left text is not empty
				if (left.length() > 0) {
					// if left token is not empty
					if (leftTok.length() > 0) {
						// if left token is equal left text
						// search for right token
						if (leftTok.contentEquals(left)) {
							// if right text is not empty
							if (right.length() > 0) {
								// if right token is not empty
								if (rightTok.length() > 0) {
									// if right token is equal right
									// text, finish
									if (rightTok.contentEquals(right)) {
										index = i;
										break;
									}
								}
								// get next sentence token first token
								else if (i < sentences.length-1) {
									rightToks = sentences[i+1].trim().split(" ");
									rightTok = rightToks[0];
									// if right token is equal 
									// right text finish
									if (rightTok.contentEquals(right)) {
										rightTok = new String();
										index = i;
										break;
									}
								}
							}
							// finish
							else {
								index = i;
								break;
							}
						}
					}
					// get previous sentence last token
					else if (i > 0) {
						leftToks = sentences[i-1].trim().split(" ");
						leftTok = leftToks[leftToks.length-1];
						// if left token is equal left text 
						// get right token
						if (leftTok.contentEquals(left)) {
							leftTok = new String();
							if (right.length() > 0) {
								// if right token is not empty
								if (rightTok.length() > 0) {
									// if right token is equal right
									// text, finish
									if (rightTok.contentEquals(right)) {
										index = i;
										break;
									}
								}
								// get next sentence token first token
								else if (i < sentences.length-1) {
									rightToks = sentences[i+1].trim().split(" ");
									rightTok = rightToks[0];
									// if right token is equal 
									// right text finish
									if (rightTok.contentEquals(right)) {
										rightTok = new String();
										index = i;
										break;
									}
								}
							}
							// finish
							else {
								index = i;
								break;
							}	
						}
					}
				}
				else {
					if (right.length() > 0) {
						// if right token is not empty
						if (rightTok.length() > 0) {
							// if right token is equal right
							// text, finish
							if (rightTok.contentEquals(right)) {
								index = i;
								break;
							}
						}
						// get next sentence token first token
						else if (i < sentences.length-1) {
							rightToks = sentences[i+1].trim().split(" ");
							rightTok = rightToks[0];
							// if right token is equal 
							// right text finish
							if (rightTok.contentEquals(right)) {
								rightTok = new String();
								index = i;
								break;
							}
						}
					}
					// finish
					else {
						index = i;
						break;
					}
				}
				String sentLag = sentence;
				// get end index of the token
				int tokEndIndex = index+token.length();
				// get sub-sentence from token end index
				sentLag = sentLag.substring(
						tokEndIndex, sentLag.length());
				// get index of token at sub-sentence
				index = sentLag.indexOf(token);
				// if found, get index at the origin sentence
				if (index > -1)
					index = tokEndIndex+index;
			}
			// if token found exit
			if (index > -1)
				break;
		}
		this.sentence = sentences[index].trim();
		this.token = token;
		this.leftToken = leftTok;
		this.rightToken = rightTok;
		//System.out.println();
		return index;
	}
	
	public String getSentence() {
		return this.sentence;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String getLeftToken() {
		return this.leftToken;
	}
	
	public String getRightToken() {
		return this.rightToken;
	}
	
	public List<String> getLeftTokens() {
		
		List<String> list = new ArrayList<String>();
		int index = sentence.indexOf(token);
		if (index > -1) {
			
			String[] array = sentence.substring(0, index)
					.trim().split(" ");
			return Arrays.asList(array);
		}
		return list;
	}
	
	public List<String> getRightTokens() {
		
		List<String> list = new ArrayList<String>();
		int index = sentence.indexOf(this.token);
		if (index > -1) {
			
			String[] array = sentence.substring(
					index+this.token.length(), sentence.length())
					.trim().split(" ");
			return Arrays.asList(array);
		}
		return list;
	}

}
