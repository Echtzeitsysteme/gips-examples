package org.emoflon.gips.ihtc.runner.utils;

/**
 * String utilities for the IHTC contest.
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class StringUtils {

	/**
	 * No public instances of this class allowed.
	 */
	private StringUtils() {
	}

	/**
	 * Replaces the last occurrence of `toReplace` with `replacement` in the given
	 * string `string`.
	 * 
	 * @param string      String to be altered.
	 * @param toReplace   Last occurrence of this string should be replaced.
	 * @param replacement Replacement string.
	 * @return
	 */
	public static String replaceLast(final String string, final String toReplace, final String replacement) {
		if (string == null) {
			throw new IllegalArgumentException("Given string was null.");
		}

		if (toReplace == null) {
			throw new IllegalArgumentException("Given string to be replaced was null.");
		}

		if (replacement == null) {
			throw new IllegalArgumentException("Given string replacement was null.");
		}

		final int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length());
		} else {
			return string;
		}
	}

}
