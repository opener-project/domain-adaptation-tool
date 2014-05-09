package org.vicomtech.opener.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * This class implements a pipe between a text and a output stream
 * asynchronously.
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class StreamPipe extends Thread {

	/**
	 * The output stream
	 */
    private OutputStream os;
    
    /**
     * The text
     */
    private String text;
    
    /**
     * Public constructor
     * @param text : the text
     * @param os : the output stream
     */
    public StreamPipe(String text, OutputStream os) {
        this.text = text;
        this.os = os;
    }
    
    /**
     * Writes text at output stream asynchronously
     */
    public void run() {
    	try {
    		OutputStreamWriter osw = new OutputStreamWriter(this.os);
    		BufferedWriter bw = new BufferedWriter(osw);
    		
    		bw.write(this.text);

    		bw.close();
    		
        } catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
    }

}
