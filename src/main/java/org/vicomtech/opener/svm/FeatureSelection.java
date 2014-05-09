package org.vicomtech.opener.svm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vicomtech.libsvm.utils.svm_node;

/**
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class FeatureSelection {

	private Map<Double,List<Integer>> featuresMap;
	
	public FeatureSelection() {
		this.featuresMap = new HashMap<Double,List<Integer>>();
		this.initFeatures();
	}
	
	public svm_node[] filterFeatures(svm_node[] instance, double label) {
		
		// get features list with given category
		List<Integer> features = this.featuresMap.get(label);
		svm_node[] nodes = new svm_node[features.size()];
		int i=0;
		// for each node, if is in feature list, add
		for (svm_node node : instance) {
			int index = node.index;
			
			boolean found = false;
			for (int ind : features) {
				if (ind == index) {
					found = true;
					break;
				}
			}
			
			if (found) {
				nodes[i] = node;
				i++;
			}
		}
		
		return nodes;
	}
	
	private void initFeatures() {
		// location
		List<Integer> features = new ArrayList<Integer>();
		features.add(123);
		features.add(102);
		features.add(110);
		features.add(107);
		features.add(115);
		features.add(109);
		features.add(17);
		features.add(6);
		features.add(60);
		features.add(120);
		features.add(99);
		features.add(121);
		features.add(108);
		features.add(28);
		features.add(114);
		features.add(39);
		features.add(100);
		features.add(104);
		features.add(71);
		features.add(117);
		features.add(46);
		this.featuresMap.put(1.0, features);
		// organization
		features = new ArrayList<Integer>();
		features.add(121);
		features.add(123);
		features.add(116);
		features.add(110);
		features.add(115);
		features.add(101);
		features.add(113);
		features.add(50);
		features.add(114);
		features.add(104);
		features.add(102);
		features.add(118);
		features.add(51);
		features.add(74);
		features.add(48);
		features.add(6);
		features.add(47);
		features.add(19);
		features.add(103);
		features.add(73);
		features.add(120);
		this.featuresMap.put(2.0, features);
		// person
		features = new ArrayList<Integer>();
		features.add(123);
		features.add(121);
		features.add(115);
		features.add(102);
		features.add(104);
		features.add(109);
		features.add(120);
		features.add(107);
		features.add(28);
		features.add(60);
		features.add(100);
		features.add(101);
		features.add(103);
		features.add(17);
		features.add(39);
		features.add(108);
		features.add(117);
		features.add(99);
		features.add(116);
		features.add(71);
		features.add(105);
		features.add(6);
		features.add(46);
		features.add(40);
		features.add(93);
		features.add(106);
		features.add(119);
		features.add(111);
		features.add(122);
		features.add(29);
		features.add(114);
		features.add(2);
		features.add(82);
		features.add(45);
		features.add(1);
		features.add(23);
		features.add(66);
		features.add(118);
		features.add(34);
		features.add(50);
		features.add(24);
		features.add(35);
		features.add(112);
		features.add(56);
		this.featuresMap.put(3.0, features);
	}

}
