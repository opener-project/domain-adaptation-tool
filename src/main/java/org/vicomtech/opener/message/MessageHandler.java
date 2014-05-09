package org.vicomtech.opener.message;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This class handles the Domain Adaptation Tool info
 * 
 * org.vicomtech.opener.message is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class MessageHandler extends Logger {

	public class BriefFormatter extends Formatter 
	{   
	    public BriefFormatter() {
	    	super();
	    }

	    @Override 
	    public String format(LogRecord record) {
	        return record.getMessage();
	    }
	}
	
	private static final String DIVIDER  = "-------------------------------------------------------------------------------";
	private static final String NEW_LINE = "\n";
	
	public static final int FAILURE = -1;
	public static final int GOOD = 0;
	
	private FileHandler fh;
    private static final String LOG_DIR_PATH = "./log";
    private static final String LOGSUFFIX    = ".log";
	
	public MessageHandler(String task) throws SecurityException, IOException {
		super("org.vicomtech.opener.bootstrapping", null);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm");
		
		File logDir = new File(LOG_DIR_PATH);
		logDir.mkdir();
		String logFile = LOG_DIR_PATH+File.separator
				+task+"."+dateFormat.format(Calendar.getInstance().getTime())+LOGSUFFIX;
		
        fh = new FileHandler(logFile);  
        addHandler(fh);
        fh.setFormatter(new BriefFormatter());  
	}
	
	/**
	 * Prints a message
	 * @param msg
	 */
	public void displayTextln(String msg) {
		super.log(Level.INFO, msg+NEW_LINE);
		System.out.println(msg);
	}
	
	/**
	 * Prints a message
	 * @param msg
	 */
	public void displayText(String msg) {
		super.log(Level.INFO, msg);
		System.out.print(msg);
	}
	
	/**
	 * Prints a message inside dividers
	 * @param msg
	 */
	public void displayHeader(String msg) {
		displayDivider();
		displayTextln(msg);
		displayDivider();
	}
	
	/**
	 * Prints a message and ends process
	 * @param msg
	 */
	public void displayError(String msg) {
		super.log(Level.SEVERE, "ERROR: "+msg+NEW_LINE);
		System.err.println("ERROR: "+msg);
	}
	
	/**
	 * Prints a message and ends process
	 * @param msg
	 */
	public void raiseCriticalError(String msg) {
		if (msg != null) {
			super.log(Level.SEVERE, "ERROR: "+msg+NEW_LINE);
			System.err.println("ERROR: "+msg);
		}
		System.exit(FAILURE);
	}

	/**
	 * Prints divider
	 */
	public void displayDivider() {
		super.log(Level.INFO, DIVIDER+NEW_LINE);
		System.out.println(DIVIDER);
	}
	
	public void close() {
		this.fh.close();
	}
}
