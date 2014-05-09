package org.vicomtech.opener.svm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.vicomtech.opener.message.MessageHandler;
import org.vicomtech.opener.utils.Utils;

/**
 * This class evaluates predicted values and computes statistics
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Evaluator {

	protected class Eval {
		
		private int TP=0;
		private int FP=0;
		private int TOTAL=0;
		
		protected int truePositives() {
			return TP;
		}
		
		protected int falsePositives() {
			return FP;
		}
		
		protected int total() {
			return TOTAL;
		}
		
		protected void addTruePositive() {
			this.TP++;
		}
		
		protected void addFalsePositive() {
			this.FP++;
		}
		
		protected void addTotal() {
			this.TOTAL++;
		}
		
		protected double precision() {
			return Utils.precision(TP, FP);
		}
		
		protected double recall() {
			return Utils.recall(TP, TOTAL);
		}
		
		protected double fscore() {
			return Utils.fscore(TP, FP, TOTAL);
		}
		
	}
	
	private Map<Double,Eval> categoryMap;
	
	private static final String AVG = "AVERAGE";
	
	public Evaluator(Set<Double> categoryLabels) {
		
		this.categoryMap = new HashMap<Double,Eval>();
		for (Double d : categoryLabels)
			this.categoryMap.put(d, new Eval());
	}
	
	public void evaluate(double category, double predictedCategory) {
		
		if (predictedCategory == category)
			this.categoryMap.get(category).addTruePositive();
		else if (/*predictedCategory != SVM.NEGATIVE_CLASS
				&& */predictedCategory != category) {
			try {
				this.categoryMap.get(predictedCategory).addFalsePositive();
			}
			catch (Exception ex) {
				this.categoryMap.put(predictedCategory, new Eval());
				this.categoryMap.get(predictedCategory).addFalsePositive();
			}
		}
		try {
			this.categoryMap.get(category).addTotal();
		}
		catch (Exception ex) {
			this.categoryMap.put(category, new Eval());
			this.categoryMap.get(category).addTotal();
		}
	}
	
	public void printStatistics(MessageHandler message) {
		
		double size = this.categoryMap.keySet().size();
		int sum_tp = 0;
		int sum_fp = 0;
		int sum_total = 0;
		double sum_precision = 0;
		double sum_recall = 0;
		double sum_fscore = 0;
		Object[] keys = this.categoryMap.keySet().toArray();
		
		message.displayTextln(this.header());
		
		for (int i=0; i<this.categoryMap.keySet().size(); i++) {
			
			Double d = (Double) keys[i];
			Eval e = this.categoryMap.get(d);
			
			if ( (d == SVM.NEGATIVE_CLASS && e.TP == 0) ) {
				size--;
			}
			else {
				int tp = e.truePositives();
				int fp = e.falsePositives();
				int total = e.total();
				double precision = e.precision();
				double recall = e.recall();
				double fscore = e.fscore();
				
				sum_tp += tp;
				sum_fp += fp;
				sum_total += total;
				sum_precision += precision;
				sum_recall += recall;
				sum_fscore += fscore;
				
				message.displayTextln(this.line(d.toString(), precision,
						recall, fscore, tp, fp, total));
			}
		}
		
		double avg_tp = Utils.round(sum_tp/size, 2);
		double avg_fp = Utils.round(sum_fp/size, 2);
		double avg_precision = Utils.round(sum_precision/size, 2);
		double avg_recall = Utils.round(sum_recall/size, 2);
		double avg_fscore = Utils.round(sum_fscore/size, 2);
		
		message.displayTextln(this.line(AVG, avg_precision,
				avg_recall, avg_fscore, avg_tp,
				avg_fp, sum_total));
	}
	
	private String header() {
		return "| Category | Precision | Recall | F-Score |    TP    |    FP    |  TOTAL  |";
	}
	
	private String line(String category, double precision,
			double recall, double fscore, double tp,
			double fp, int total) {
		
		String line;
		if (category.equalsIgnoreCase(AVG))
			line = "|  "+category+" |  ";
		else if (category.equalsIgnoreCase(Double.toString(SVM.NEGATIVE_CLASS)))
			line = "|     "+category+" |  ";
		else
			line = "|      "+category+" |  ";
		
		// precision-----------------------------
		String s = Double.toString(precision);
		String iPart = s.substring(0, s.indexOf("."));
		String fPart = s.substring(s.indexOf(".")+1, s.length());
		
		int l = 3-iPart.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
		line += precision;
		
		if (fPart.length() == 1)
			line += " ";
		
		line += "   | ";
		
		// recall-------------------------------
		s = Double.toString(recall);
		iPart = s.substring(0, s.indexOf("."));
		fPart = s.substring(s.indexOf(".")+1, s.length());
		
		l = 3-iPart.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
		
		line += recall;
		
		if (fPart.length() == 1)
			line += " ";
		
		line += " |  ";
		
		// f-score--------------------------------
		s = Double.toString(fscore);
		iPart = s.substring(0, s.indexOf("."));
		fPart = s.substring(s.indexOf(".")+1, s.length());
		
		l = 3-iPart.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
		
		line += fscore;
		
		if (fPart.length() == 1)
			line += " ";
		
		line += " | ";
		
		// tp---------------------------------------
		s = Double.toString(tp);
		
		l = 8-s.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
		
		line += tp;
		
		line += " | ";
		
		// fp---------------------------------------
		s = Double.toString(fp);
				
		l = 8-s.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
				
		line += fp;
				
		line += " | ";
		
		// total--------------------------------------
		s = Integer.toString(total);
		
		l = 7-s.length();
		for (int i=0; i<l; i++) {
			line += " ";
		}
				
		line += total;
				
		line += " | ";
		
		return line;
	}

}
