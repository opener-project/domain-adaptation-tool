package org.vicomtech.opener.utils;

import java.io.IOException;

/**
 * This class runs external commands.
 * 
 * org.vicomtech.opener.svm is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class Exec {
	
	@SuppressWarnings("serial")
	public static class ExecException extends Exception {
		public ExecException(String message) {
			super(message);
		}
	}
	
	public static final String REMOVE_CMD = "rm -r ";
	
	public static int executeCommand(String command) throws IOException, InterruptedException {
		// execute process
		Process process = Runtime.getRuntime().exec(command);
		
		StreamGobbler errorDataReader = new StreamGobbler(process
				.getErrorStream());
		StreamGobbler outputDataReader = new StreamGobbler(process
				.getInputStream());
		
		// start processes
		errorDataReader.start();
		outputDataReader.start();
		
		return process.waitFor();
	}
	
	/**
	 * Executes an external command and redirects stdin and stdout
	 * @param command : the command to execute
	 * @param inString : input String
	 * @param printErrorStream : prints error stream or not
	 * @return command output string
	 * @throws IOException, InterruptedException
	 */
	public static String executeCommandInputOutput(String command,
			String inString) throws IOException, InterruptedException {
		
		// execute process
		Process process = Runtime.getRuntime().exec(command);
		
		// create threads to read from stdout and stderr asynchronously
		StreamPipe fileDataReader = new StreamPipe(inString,
				process.getOutputStream());
		StreamGobbler errorDataReader = new StreamGobbler(process
				.getErrorStream());
		StreamGobbler outputDataReader = new StreamGobbler(process
				.getInputStream());
		
		// start processes
		fileDataReader.start();
		errorDataReader.start();
		outputDataReader.start();

		int exitVal = process.waitFor();
		
		fileDataReader.join();
		errorDataReader.join();
		outputDataReader.join();
		
		// get output
		String txt = outputDataReader.output();
		
		// check exit value
		if (exitVal != 0)
			throw new IOException("command exited badly: '"+command+"'");
							
		return txt;
	}

}
