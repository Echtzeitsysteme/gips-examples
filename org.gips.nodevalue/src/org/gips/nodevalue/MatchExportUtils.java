package org.gips.nodevalue;

import java.io.File;

/**
 * Utilities for the match export functionality.
 */
public class MatchExportUtils {

	/**
	 * File name template, i.e., the start name of the file to save.
	 */
	private static final String filenameTemplate = "match-export";

	/**
	 * File ending, in our case `.json`.
	 */
	private static final String fileEnding = ".json";

	/**
	 * Private constructor to prevent instantiation of new objects of this class.
	 */
	private MatchExportUtils() {
	}

	/**
	 * Returns the next available time name dynamically. This method probes for
	 * existing file names over and over again. Caution, this may be slow if there
	 * are a lot of files available.
	 * 
	 * @return Next free file name, e.g., `$filenameTemplate0$fileEnding =
	 *         match-export0.json`.
	 */
	public static String getNextFreeFilename() {
		String ret = filenameTemplate;
		int i = 0;
		while (checkIfFileExists(ret + i + fileEnding)) {
			i++;
		}
		ret = filenameTemplate + i + fileEnding;
		return ret;
	}

	/**
	 * Returns true if a file with the given filename exists.
	 * 
	 * @param filename File name to probe file existence for.
	 * @return True if a file with the given filename exists.
	 */
	public static boolean checkIfFileExists(final String filename) {
		final File f = new File(filename);
		return f.exists() && !f.isDirectory();
	}

}
