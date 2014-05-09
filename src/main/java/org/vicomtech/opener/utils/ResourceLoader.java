package org.vicomtech.opener.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.maxent.Main;

import org.vicomtech.opener.nlp.TagsetMappings;
import org.vicomtech.opener.nlp.TagsetMappings.KafTag;

/**
 * This class read resources and returns maps
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class ResourceLoader {

	private static final String COMMENT            = "#";
	private static final String CONFIG_SEPARATOR   = "=";
	private static final String TAG_SEPARATOR      = "\\s+|\\t+";
	private static final String MULTITAG_SEPARATOR = "/";
	private static final String HEAD_CHARACTER     = "*";

	public Map<String,String> loadMap(String confFile, boolean keyAtLeft) throws IOException {
		InputStream inStream = Main.class.getResourceAsStream(confFile);
		if (inStream == null) {
			inStream = new FileInputStream(confFile);
		}
		List<String> lines = Utils.readStream(inStream);
		
		Map<String,String> langMap = new HashMap<String,String>();
		for (String line : lines) {
			if (line.length() > 0 && !line.startsWith(COMMENT)) {
				String[] columns = line.trim().split(CONFIG_SEPARATOR);
				if (columns.length != 2) {
					throw new IOException(String.format("Line '%s' with wrong config format", line));
				}
				else {
					if (keyAtLeft) {
						langMap.put(columns[0].trim(), columns[1].trim());
					}
					else {
						langMap.put(columns[1].trim(), columns[0].trim());
					}
					
				}
			}
		}
		if (langMap.size() == 0) {
			throw new IOException(String.format("Config file '%s' is empty", confFile));
		}
		else {
			return langMap;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<List<KafTag>> loadRules(String ruleFile) throws IOException {
		InputStream inStream = Main.class.getResourceAsStream(ruleFile);
		if (inStream == null) {
			inStream = new FileInputStream(ruleFile);
		}
		List<String> lines = Utils.readStream(inStream);
		
		List<List<KafTag>> rules = new ArrayList<List<KafTag>>();
		for (String line : lines) {
			if (line.length() > 0 && !line.startsWith(COMMENT)) {
				String[] columns = line.trim().split(TAG_SEPARATOR);
				if (columns.length == 0) {
					throw new IOException(String.format("Line '%s' with wrong rule format", line));
				}
				else {
					// create new rules list of lists (for multitags)
					List<List<KafTag>> newRules = new ArrayList<List<KafTag>>();
					newRules.add(new ArrayList<KafTag>());
					for (String s : columns) {
						if (s.length() > 0) {
							if (s.endsWith(HEAD_CHARACTER)) {
								s = s.substring(0, s.indexOf(HEAD_CHARACTER));
							}
							
							if (s.contains(MULTITAG_SEPARATOR)) {
								// get all tags
								String[] tags = s.split(MULTITAG_SEPARATOR);
								String tag = tags[0];
								// add first tag to existing rules
								for (List<KafTag> list : newRules) {
									list.add(TagsetMappings.convertFromStringToKaf(tag));
								}
								// for each other tags, clone existing rules and
								// put the new tag
								int size = newRules.size();
								for (int i=1; i< tags.length; i++) {
									tag = tags[i];
									for (int j=0; j<size; j++) {
										List<KafTag> list = (List<KafTag>) ((ArrayList<KafTag>) newRules.get(j)).clone();
										list.remove(list.size()-1);
										list.add(TagsetMappings.convertFromStringToKaf(tag));
										newRules.add(list);
									}
								}
							}
							else {
								for (List<KafTag> list : newRules) {
									list.add(TagsetMappings.convertFromStringToKaf(s));
								}
							}
						}
					}
					rules.addAll(newRules);
					//rules.add(rule);
				}
			}
		}
		if (rules.size() == 0) {
			throw new IOException(String.format("Rule file '%s' is empty", ruleFile));
		}
		else {
			return rules;
		}
	}
	
	public List<Integer> loadHeads(String ruleFile) throws IOException {
		InputStream inStream = Main.class.getResourceAsStream(ruleFile);
		if (inStream == null) {
			inStream = new FileInputStream(ruleFile);
		}
		List<String> lines = Utils.readStream(inStream);
		
		List<Integer> heads = new ArrayList<Integer>();
		for (String line : lines) {
			if (line.length() > 0 && !line.startsWith(COMMENT)) {
				String[] columns = line.trim().split(TAG_SEPARATOR);
				if (columns.length == 0) {
					throw new IOException(String.format("Line '%s' with wrong head format", line));
				}
				else {
					// create new head lists
					List<Integer> newHeads = new ArrayList<Integer>();
					newHeads.add(new Integer(0));
					int head = -1;
					for (int i=0; i<columns.length; i++) {
						String s = columns[i];
						if (s.length() > 0) {
							// get head index
							if (s.endsWith(HEAD_CHARACTER)) {
								head = i;
							}
							// add new Integers for multitags...
							if (s.contains(MULTITAG_SEPARATOR)) {
								// get all tags
								String[] tags = s.split(MULTITAG_SEPARATOR);
								// for each other tags put new Integer
								int size = newHeads.size();
								for (int j=1; j< tags.length; j++) {
									for (int z=0; z<size; z++) {
										newHeads.add(new Integer(0));
									}
								}
							}
						}
					}
					
					if (head < 0) {
						throw new IOException(String.format("Line '%s' has no head", line));
					}
					else {
						// put head index at new heads
						for (int i =0; i<newHeads.size(); i++) {
							newHeads.set(i, head);
						}
					}
					heads.addAll(newHeads);
				}
			}
		}
		if (heads.size() == 0) {
			throw new IOException(String.format("Head rule file '%s' is empty", ruleFile));
		}
		else {
			return heads;
		}
	}
}
