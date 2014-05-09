package org.vicomtech.opener.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.vicomtech.opener.chunker.AnnotateChunks;
import org.vicomtech.opener.chunker.Chunk;
import org.vicomtech.opener.chunker.Chunk.Constituent;
import org.vicomtech.opener.entities.Feature;
import org.vicomtech.opener.utils.Language;

import opennlp.tools.util.InvalidFormatException;

/**
 * This class uses org.vicomtech.opener.chunker to chunk sentences
 * 
 * org.vicomtech.opener.nlp is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Chunker {

	private AnnotateChunks annotate;
	private PoSTagger postagger;
	private int modSize;

	/**
	 * Instantiates a new OpenNlpChunker class. Every instance shares the same
	 * ChunkerModel (not the internal chunker class itself, which is not thread
	 * safe). The model is read only one, when the first instance is created. It
	 * is safe, and much more memory and time efficient than reading the model
	 * once and again.
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public Chunker(Language language, int modSize) throws InvalidFormatException, IOException {
		
		// create annotator
    	this.annotate = new AnnotateChunks(language);
    	this.postagger = new PoSTagger(language);
    	this.modSize = modSize;
	}

	/**
	 * Receives a ChunkingData object, which internally contains the wordforms and postags to perform the chunking.
	 * The output format is an array of chunk tags, one per token as described in OpenNLP documentation (CoNLL format)
	 * @param chunkingData
	 * @return
	 */
	public List<Feature> chunk(String text, boolean show) {
		
		String[] tokens = text.split(" ");
		String[] pos = this.postagger.postag(tokens);
		
		List<Chunk> chunks=this.annotate.chunk(tokens, pos);
		
		if (show) {
			String s = new String();
    		for (Chunk c : chunks)
    			s += c.toString()+" ";
    		System.out.println(s);
		}
		return this.parse(chunks);
	}
	
	private List<Feature> parse(List<Chunk> chunks) {
		
		List<Feature> features = new ArrayList<Feature>();
		for (Chunk c : chunks) {
			if (c.getTag().contentEquals("NP")) {
				features.add(this.parseNP(c));
			}
			else {
				features.addAll(this.parseWords(c));
			}
		}
		return features;
	}
	
	private Feature parseNP(Chunk c) {
		
		Feature feature = new Feature(modSize);
		boolean found = false;
		for (int i=0; i<c.size(); i++) {
			String token = c.getToken(i);
			String pos = c.getPoS(i);
			if (found) {
    			feature.addOther(token);
    		}
    		// if is head, break
    		else if (i == c.getHeadIndex()) {
    			feature.setHead(token);
    			found = true;
    		}
    		// if is modifier add
    		else if (pos.contentEquals("D")) {
    			feature.addLeftModifier(token);
    		}
    		else {
    			feature.addOther(token);
    		}
		}
		return feature;
	}
	
	private List<Feature> parseWords(Chunk c) {
		
		List<Feature> features = new ArrayList<Feature>();
		for (Constituent con : c.getConstituents()) {
			Feature feature = new Feature();
			feature.setHead(con.getToken());
			features.add(feature);
		}
		return features;
	}

}
