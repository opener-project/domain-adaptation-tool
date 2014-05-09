package org.vicomtech.opener.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vicomtech.opener.entities.Feature;
import org.vicomtech.opener.utils.Language;
import org.vicomtech.opener.utils.ResourceLoader;

/**
 * This class detect NPs.
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class NPDetector extends ResourceLoader {

	private List<List<TagsetMappings.KafTag>> rules;
	private List<Integer> headIndexes;
	private int modsize;
	
	private static Map<String,String> ruleFilesMap = null;
	private static final String CONFIG_FILE_PATH = "./config/np_rules.conf";
	
	public NPDetector(Language lang, int modsize) throws IOException {
		this.modsize = modsize;
		
		if (ruleFilesMap == null) {
			ruleFilesMap = super.loadMap(CONFIG_FILE_PATH, true);
		}
		
		this.rules = super.loadRules(ruleFilesMap.get(lang.getValue()));
		this.headIndexes = super.loadHeads(ruleFilesMap.get(lang.getValue()));
	}
	
	public List<Feature> chunk(String[] text, String[] pos) {
		
		List<Feature> features = new ArrayList<Feature>();
		for (int i=0; i<pos.length; i++) {
			int r = this.getRuleIndex(pos, i);
			// rule found, build features
			if (r != -1) {
				int ri = 0;
				int headIndex = this.headIndexes.get(r);
				Feature feature = new Feature(this.modsize);
				for (TagsetMappings.KafTag tag : this.rules.get(r)) {
					String t = text[i];
					if (!TagsetMappings.isDeterminer(tag.getTagText())) {
						if (ri<headIndex)
							feature.addLeftModifier(t);
						else if (ri > headIndex)
							feature.addRightModifier(t);
						else
							feature.setHead(t);
					}
					else feature.addOther(t);
					i++;
					ri++;
				}
				features.add(feature);
				i--;
			}
			else {
				Feature feature = new Feature();
				feature.setHead(text[i]);
				features.add(feature);
			}
		}
		
		return features;
	}
	
	/**
	 * Returns the rule index that suits to the pos array starting from index.
	 * If there isn't any suiting index returns -1
	 * @param pos : pos array
	 * @param index : pos array starting index
	 * @return
	 */
	public int getRuleIndex(String[] pos, int index) {
		
		// for each rule
		for (int i=0; i<this.rules.size(); i++) {
			List<TagsetMappings.KafTag> rule = rules.get(i);
			boolean found = true;
			int j=index;
			// for each tag of rule
			for (TagsetMappings.KafTag tag : rule) {
				// ensure that will not have index out of bounds exception
				if (j<pos.length) {
					String p = pos[j];
					// rule is not suiting
					if (!p.equalsIgnoreCase(tag.getTagText())) {
						found = false;
						break;
					}
					else {
					}
					j++;
				}
				// rule is not suiting
				else {
					found = false;
					break;
				}
			}
			if (found) return i;
		}
		// there is no rule
		return -1;
	}
	
}
