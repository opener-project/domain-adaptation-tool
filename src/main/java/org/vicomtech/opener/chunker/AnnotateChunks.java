package org.vicomtech.opener.chunker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.InvalidFormatException;

import org.vicomtech.opener.nlp.Chunker;
import org.vicomtech.opener.utils.Language;

/**
 * This class uses the OpenNlpChunker class for chunking.
 * 
 * org.vicomtech.opener.chunker is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class AnnotateChunks {

	private static final String FR_MODEL_PATH = "/fr-chunk-corrected.bin";
	
	private ChunkerME chunker;
	private HeadRulesReaderC headRules;

	/**
	 * Instantiates a new OpenNlpChunker class. Every instance shares the same
	 * ChunkerModel (not the internal chunker class itself, which is not thread
	 * safe). The model is read only one, when the first instance is created. It
	 * is safe, and much more memory and time efficient than reading the model
	 * once and again.
	 */
	public AnnotateChunks(Language language) throws InvalidFormatException, IOException {
		
		InputStream modelIn = null;
		if (language.toString().equalsIgnoreCase("fr")) {
			modelIn = Chunker.class.getResourceAsStream(
					FR_MODEL_PATH);
		}
		chunker = new ChunkerME(new ChunkerModel(modelIn));
		modelIn.close();
		
		headRules = new HeadRulesReaderC(language);
		
	}
	
	private List<Chunk> parseChunks(String[] tokens, String[] pos,
			String[] tags) {
		
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		for (int i=0; i<tokens.length; i++) {
			
			String token = tokens[i];
			String po = pos[i];
			String tag = tags[i];
			
			if (Chunk.isOut(tag)) {
				Chunk chunk = new Chunk(token, po, tag);
				chunks.add(chunk);
			}
			else if (Chunk.isBegin(tag)) {
				Chunk chunk = new Chunk(token, po, tag);
				chunks.add(chunk);
			}
			else {
				chunks.get(chunks.size()-1)
					.addConstituent(token, po);
			}
		}
		return chunks;
		
	}

	/**
	 * Receives a ChunkingData object, which internally contains the wordforms and postags to perform the chunking.
	 * The output format is an array of chunk tags, one per token as described in OpenNLP documentation (CoNLL format)
	 * @param chunkingData
	 * @return
	 */
	public List<Chunk> chunk(String[] tokens, String[] pos){
		
		String[] tags=chunker.chunk(tokens, pos);
		List<Chunk> chunks = this.parseChunks(tokens, pos, tags);
		this.headRules.annotateHeads(chunks);
		return chunks;
	}

}
