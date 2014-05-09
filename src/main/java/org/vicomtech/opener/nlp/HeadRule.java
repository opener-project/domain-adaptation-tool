package org.vicomtech.opener.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class HeadRule {
		
	public String nonterminal;
	public int direction;
	public List<String> possibleHeads=new ArrayList<String>();
	
	public HeadRule(String nonterminal, int direction,
			List<String> possibleHeads) {
		
		this.nonterminal = nonterminal;
		this.direction = direction;
		this.possibleHeads = possibleHeads;
	}
	
	/**
	 * Constructor which takes an InputStream to read a valid head rules file
	 * The file is read only the first time an instance is loaded.
	 * Every instance shares the same internal rules representation
	 * @param is
	 * @throws IOException 
	 */
	public static List<HeadRule> readRules(InputStream is) throws IOException{
		
		List<HeadRule> headRules=new ArrayList<HeadRule>();
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String line="";
		while((line=br.readLine())!=null){
			String[] ruleParts=line.split(" ");
			String nonterminal=ruleParts[1];
			int direction=Integer.parseInt(ruleParts[2]);
			List<String>possibleHeads=new ArrayList<String>();
			for(int i=3;i<ruleParts.length;i++){
				possibleHeads.add(ruleParts[i]);
			}
			HeadRule headRule=new HeadRule(nonterminal,direction,possibleHeads);
			headRules.add(headRule);
		}
		br.close();
		return headRules;
	}

}
