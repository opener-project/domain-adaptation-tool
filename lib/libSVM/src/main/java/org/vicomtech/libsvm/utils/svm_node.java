package org.vicomtech.libsvm.utils;

@SuppressWarnings("serial")
public class svm_node implements java.io.Serializable {
	
	public int index;
	public double value;
	
	private final static String NODES_SEPARATOR = "\\t|\\s";
	private final static String NODE_SEPARATOR  = ":";
	
	public svm_node() {}
	
	public svm_node(int index, double value) {
		this.index = index;
		this.value = value;
	}
	
	public static svm_node[] parseLine(String line) {
		String[] nodesString = line.trim().split(NODES_SEPARATOR);
		svm_node[] nodes = new svm_node[nodesString.length];
		for (int i=0; i<nodesString.length; i++) {
			String[] nodeString = nodesString[i].split(NODE_SEPARATOR);
			svm_node node = new svm_node(
					Integer.parseInt(nodeString[0]),
					Double.parseDouble(nodeString[1]));
			nodes[i] = node;
		}
		return nodes;
	}
}
