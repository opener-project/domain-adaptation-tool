package org.vicomtech.opener.constituentparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.vicomtech.opener.nlp.HeadRule;
import org.vicomtech.opener.utils.Language;

import opennlp.tools.parser.Parse;

/**
 * Custom class to read a HeadRules in the same format OpenNLP uses.
 * 
 * org.vicomtech.opener.constituentparser is a module of Domain Adaptation Tool for OpeNER
 * @author Aitor Garcia Pablos (agarciap@vicomtech.org),
 * Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class HeadRulesReaderCP {
	
	private static List<HeadRule> headRules;
	
	public HeadRulesReaderCP(Language language) throws IOException {
		InputStream is = null;
		if (language.toString().equalsIgnoreCase("fr")) {
			is =HeadRulesReaderCP.class.getResourceAsStream("/fr-head-rules");
		}
		headRules = HeadRule.readRules(is);
		is.close();
		
	}

	public HeadRulesReaderCP(InputStream is) throws IOException {
		headRules = HeadRule.readRules(is);
		is.close();
	}
	
	/**
	 * Modifies the input Parse tree annotating the type of heads with '=H' according to headrules
	 * @param parse
	 */
	public void annotateHeads(Parse parse){
		LinkedList<Parse> nodes=new LinkedList<Parse>();
		nodes.add(parse);
		while(!nodes.isEmpty()){
			Parse currentNode=nodes.removeFirst();			
			applyHeadRules(currentNode, currentNode.getChildren());
			for(Parse child:currentNode.getChildren()){
				nodes.addLast(child);
			}
		}
	}
	
	private void applyHeadRules(Parse father, Parse[]children){
		String fatherType=father.getType();
		boolean found=false;
		for(HeadRule headRule:headRules){
			if(headRule.nonterminal.equalsIgnoreCase(fatherType)){
				if(headRule.direction==1){
					for(int i=children.length-1;i>=0 && !found;i--){
						String childType=children[i].getType().replace("=H","");
						for(String possibleHead:headRule.possibleHeads){
							if(childType.equalsIgnoreCase(possibleHead)){
								children[i].setType(childType+"=H");
								found=true;
								break;
							}
						}
					}
				}else if (headRule.direction==0){
					for(int i=0;i<children.length && !found;i++){
						String childType=children[i].getType().replace("=H","");;
						for(String possibleHead:headRule.possibleHeads){
							if(childType.equalsIgnoreCase(possibleHead)){
								children[i].setType(childType+"=H");
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
