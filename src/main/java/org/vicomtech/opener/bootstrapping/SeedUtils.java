package org.vicomtech.opener.bootstrapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vicomtech.opener.utils.Utils;

/**
 * This class processes seed files.
 * 
 * org.vicomtech.opener.bootstrapping is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class SeedUtils {

	private static final String SEED_FILE_SEPARATOR = "\\t";
	private static final String SEED_FILE_COMMENT   = "#";
	
	@SuppressWarnings("serial")
	protected static class SeedFileMalformedException extends Exception {
		private static final String ERROR_MESSAGE = "seed file '%s' needs" +
				" 2 tab separated columns: the seed and its category";
		public SeedFileMalformedException(File seedFile) {
			super(String.format(ERROR_MESSAGE, seedFile.getAbsolutePath()));
			
		}
	}

	protected static Map<String,List<String>> parseSeedFile(File seedFile) throws IOException, SeedFileMalformedException {
		Map<String,List<String>> seedList = new HashMap<String,List<String>>();
		List<String> lines = Utils.readFile(seedFile);
		for (String line : lines) {
			if (!line.startsWith(SEED_FILE_COMMENT)) {
				String[] columns = line.split(SEED_FILE_SEPARATOR);
				if (columns.length != 2) {
					throw new SeedFileMalformedException(seedFile);
				}
				else {
					String seed = columns[0];
					String category = columns[1];
					if (seedList.containsKey(category)) {
						List<String> list = seedList.get(category);
						list.add(seed);
						seedList.put(category, list);
					}
					else {
						List<String> list = new ArrayList<String>();
						list.add(seed);
						seedList.put(category, list);
					}
				}
			}
		}
		return seedList;
	}
	
	public static String getCategory(String entity, Map<String,List<String>> seedList) {
		for (String category : seedList.keySet()) {
			List<String> list = seedList.get(category);
			if (list.contains(entity)) {
				return category;
			}
		}
		return null;
	}

	/**
	 * True if the entity is at the seed-list
	 * @param seedList : the seed-list
	 * @return
	 */
	public static boolean isAtSeedList(String entity, Map<String,List<String>> seedList) {
		
		for (String category : seedList.keySet()) {
			for (String seed : seedList.get(category)) {
				if (entity.contentEquals(seed)) {
					return true;
				}
			}
		}
		return false;
	}
	
//TODO
	
	/**
	 * True if the entity is at the seed-list
	 * @param seedList : the seed-list
	 * @return
	 */
	public static boolean isAtSeedList(String entity, List<String> seedList) {
		
		for (String seed : seedList) {
			if (entity.contentEquals(seed)) {
				return true;
			}
		}
		return false;
	}
	
}
