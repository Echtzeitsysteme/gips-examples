package model.converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * File utilities for loading and saving files.
 *
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class FileUtils {

	/**
	 * Private constructor to forbid instantiation of objects.
	 */
	private FileUtils() {
	}

	/**
	 * Takes a given path and a JSON object and writes it to a file.
	 *
	 * @param path Path for the file to write.
	 * @param json JSON object to write to file.
	 */
	protected static void writeFileFromJson(final String path, final JsonObject json) {
		if (path == null || path.isBlank()) {
			throw new IllegalArgumentException("Given path was null or blank.");
		}

		if (json == null) {
			throw new IllegalArgumentException("Given json object was null.");
		}

		FileUtils.writeFile(path, json.toString());
	}

	/**
	 * Reads a file from a given path to a JSON object.
	 *
	 * @param path Path for the file to read.
	 * @return JSON object read from file.
	 */
	protected static JsonObject readFileToJson(final String path) {
		if (path == null || path.isBlank()) {
			throw new IllegalArgumentException("Given path was null or blank.");
		}
		return new Gson().fromJson(FileUtils.readFile(path), JsonObject.class);
	}

	/**
	 * Writes given string content to a file at given path.
	 *
	 * @param path   Path to write file to.
	 * @param string Content to write in file.
	 */
	public static void writeFile(final String path, final String string) {
		if (path == null || path.isBlank()) {
			throw new IllegalArgumentException("Given path was null or blank.");
		}

		if (string == null) {
			throw new IllegalArgumentException("Given String was null.");
		}

		FileWriter file = null;
		try {
			file = new FileWriter(path);
			file.write(string);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				file.flush();
				file.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads a file from a given path and returns its content as string.
	 *
	 * @param path Path to read file from.
	 * @return File content as string.
	 */
	public static String readFile(final String path) {
		if (path == null || path.isBlank()) {
			throw new IllegalArgumentException("Given path was null or blank.");
		}

		// Check if file exists
		final File f = new File(path);
		if (!f.exists() || f.isDirectory()) {
			throw new UnsupportedOperationException("File <" + path + "> does not exist.");
		}

		String read = "";
		try {
			read = Files.readString(Path.of(path));
		} catch (final IOException e) {
			throw new IllegalArgumentException();
		}
		return read;
	}

	/**
	 * Converts the linebreaks of a given string to the one used by the current
	 * system.
	 *
	 * @param toConvert Input string to convert linebreaks for.
	 * @return String with converted linebreaks.
	 */
	public static String replaceLinebreaks(final String toConvert) {
		if (toConvert == null) {
			throw new IllegalArgumentException("Given String was null.");
		}
		return toConvert.replace("\r\n", System.lineSeparator()).replace("\n", System.lineSeparator());
	}

}
