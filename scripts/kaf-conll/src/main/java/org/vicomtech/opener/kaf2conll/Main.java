package org.vicomtech.opener.kaf2conll;

import ixa.kaflib.Entity;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Span;
import ixa.kaflib.Term;
import ixa.kaflib.WF;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.List;


/**
 * This class translates KAF format to CONLL format
 * It has been designed for Bootstrapping NER in OpeNER, but
 * could be used in other tasks.
 * @author mcuadros
 *
 */
public class Main {
	
	private static enum ExitStatus {
		SUCCESS(0), ERROR(-1);
		int value;
		ExitStatus(int i) {
			value = i;
		}
	}
	
	/**
	 * Static main method,
	 * Input KAF file as stream, Output CONLL file.
	 * @param args		input arguments
	 */
	public static void main(String[] args) {
		Main prog = new Main();
		prog.execute(args, System.in, System.out);	
	}
	
	/**
	 * Input KAF file as stream, Output CONLL file.
	 * @param args		input arguments
	 */
	public void execute(String[] args, InputStream inStream, OutputStream outStream) {
		
		checkArguments(args);
		
		/**
		 * Creation of buffers for input/output
		 */
		BufferedWriter bwriter = null;

		try {
			/**
			 * Inicialization of buffers
			 */
			bwriter = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
			
			// Inicialization of kaf variable as KAFDocument

			KAFDocument kaf = KAFDocument.createFromStream(new InputStreamReader(inStream,
					"UTF-8"));
			
			//Create list with named entities

			List<Entity> entities = kaf.getEntities();

			//Creation of two hashes for quicky access entities
			
			Hashtable<String, Integer> entities_number = new Hashtable<String, Integer>();
			Hashtable<String, String> entities_type = new Hashtable<String, String>();

			for (Entity entity_var : entities) {
				
				List<Span<Term>> entitySpans = entity_var.getSpans();
				for (Span<Term> sterm : entitySpans) {
					Term t = sterm.getFirstTarget();				
					entities_number.put(t.getId(), sterm.size());
					entities_type.put(t.getId(), entity_var.getType());
					
				}

			}

			List<List<WF>> sentences = kaf.getSentences();
			
			for (List<WF> sentence : sentences) {
				int index = 1;
				
				int sentenceNumber = sentence.get(0).getSent();

				List<Term> sentenceTerms = kaf.getSentenceTerms(sentenceNumber);

				// Go through sentences and terms, to print out number of token, term, lemma, nerc, id (not-used), type of entity
				for (int i = 0; i < sentenceTerms.size(); i++) {
					
					bwriter.write(Integer.toString(index));
					bwriter.write("\t");
					Term t = sentenceTerms.get(i);
					String new_term_form = t.getForm();
					String new_term_lemma = t.getLemma();

					if (entities_number.get(t.getId()) != null) {						
						int number = entities_number.get(t.getId());
						String type = entities_type.get(t.getId());
						
						if (number > 1) {
							for (int j = 1; j < number; j++) {
								t = sentenceTerms.get(i + j);
								new_term_form = new_term_form.concat("_")
										.concat(t.getForm());
								new_term_lemma = new_term_lemma.concat("_")
										.concat(t.getLemma());

							}
						}
						i = i + number;
						
						bwriter.write(new_term_form);
						bwriter.write("\t");
						bwriter.write(new_term_lemma);
						bwriter.write("\t");

						bwriter.write(t.getPos());
						bwriter.write("\t");
						bwriter.write(Integer.toString(1));
						bwriter.write("\t");

						bwriter.write("ne=");
						bwriter.write(type);
					} else {
						bwriter.write(new_term_form);
						bwriter.write("\t");
						bwriter.write(new_term_lemma);
						bwriter.write("\t");
						bwriter.write(t.getPos());
						bwriter.write("\t");
						bwriter.write(Integer.toString(1));
						bwriter.write("\t");
					}

					bwriter.newLine();
					index++;

				}

				bwriter.newLine();
			}

			bwriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks application arguments.
	 * @param args		input arguments
	 */
	private void checkArguments(String[] args) {
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help")) {
				printUsage();
				System.exit(ExitStatus.SUCCESS.value);
			}
			else {
				System.err.println(String.format("unknown argument: %s", arg));
				printUsage();
				System.exit(ExitStatus.ERROR.value);
			}
		}
	}
	
	/**
	 * Prints application help message.
	 */
	private void printUsage() {
		System.err.println("Translates KAF format to CONLL format.");
		System.err.println();
		System.err.println("USAGE:   java -jar kaf-conll-0.0.1-SNAPSHOT.jar [OPTIONS...]");
		System.err.println("Reads text from standard input and writes result at standard output.");
		System.err.println();
		System.err.println("  OPTIONS:");
		System.err.println("    -h, --help       shows this help");
	}
}
