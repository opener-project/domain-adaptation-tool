package org.vicomtech.opener.utils;

import java.io.*;

/**
 * This class implements a Stream Gobbler to read the input or error
 * stream of a process asynchronously
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class StreamGobbler extends Thread {
	
	/**
	 * The stream from the text will be read
	 */
    private InputStream is;
    /**
     * Readed text
     */
    private String text;
    
    /**
     * Public constructor
     * @param is : the input stream
     */
    public StreamGobbler(InputStream is) {
        this.is = is;
        this.text = new String();
    }
    
    /**
     * Reads the text from the stream asynchronously
     */
    public void run() {
    	try {
    		InputStreamReader isr = new InputStreamReader(is);
    		BufferedReader br = new BufferedReader(isr);
    		
    		String line=null;
			while ( (line = br.readLine()) != null) {
				text += line+"\n";
			}
			if (text.length() > 0)
				text = text.substring(0, text.length()-1);
        } catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
    }
    
    /**
     * Get the readed text
     * @return
     */
    public String output() {
    	return text;
    }
}