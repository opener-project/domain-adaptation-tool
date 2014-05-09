package org.vicomtech.opener.chunker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.vicomtech.opener.nlp.HeadRule;
import org.vicomtech.opener.utils.Language;

/**
 * This class reads and processes head rules for chunking.
 * 
 * org.vicomtech.opener.chunker is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class HeadRulesReaderC {
	
	private static List<HeadRule> headRules;
	
	public HeadRulesReaderC(Language language) throws IOException{
		
		InputStream is = null;
		if (language.toString().equalsIgnoreCase("fr")) {
			is =HeadRulesReaderC.class.getResourceAsStream("/fr-head-rules");
		}
		headRules = HeadRule.readRules(is);
		is.close();
	}
	
	public HeadRulesReaderC(InputStream is) throws IOException {
		headRules = HeadRule.readRules(is);
		is.close();
	}
	
	/**
	 * Modifies the input Parse tree annotating the type of heads with '=H' according to headrules
	 * @param parse
	 */
	public void annotateHeads(List<Chunk> chunks) {
			
		for(Chunk chunk : chunks) {
			this.applyHeadRules(chunk);
		}
	}
	
	private void applyHeadRules(Chunk chunk) {
		
		boolean found=false;
		for(HeadRule headRule:headRules) {
			if (headRule.nonterminal.equalsIgnoreCase(chunk.getTag())) {
				if(headRule.direction==1) {
					for(int i=chunk.size()-1;i>=0 && !found;i--) {
						String pos = chunk.getPoS(i);
						for(String possibleHead : headRule.possibleHeads) {
							if(pos.equalsIgnoreCase(possibleHead)){
								chunk.setHead(i);
								found=true;
								break;
							}
						}
					}
				} else if (headRule.direction==0) {
					for(int i=0;i<chunk.size() && !found;i++) {
						String pos = chunk.getPoS(i);
						for(String possibleHead : headRule.possibleHeads) {
							if(pos.equalsIgnoreCase(possibleHead)){
								chunk.setHead(i);
								found=true;
								break;
							}
						}
					}
				}
			}
		}
	}
	
}