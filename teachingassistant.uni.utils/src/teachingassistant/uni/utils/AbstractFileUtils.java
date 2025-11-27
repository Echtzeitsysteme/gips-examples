package teachingassistant.uni.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * (Abstract) File utilities for loading and saving files.
 */
public abstract class AbstractFileUtils {

	/**
	 * Takes a given path and a JSON object and writes it to a file.
	 *
	 * @param path Path for the file to write.
	 * @param json JSON object to write to file.
	 */
	public static void writeFileFromJson(final String path, final JsonObject json) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(json);

		if (path.isBlank()) {
			throw new IllegalArgumentException("Given path was blank.");
		}

		// pretty print
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		writeFile(path, gson.toJson(json).toString());
	}

	/**
	 * Reads a file from a given path to a JSON object.
	 *
	 * @param path Path for the file to read.
	 * @return JSON object read from file.
	 */
	public static JsonObject readFileToJson(final String path) {
		Objects.requireNonNull(path);
		if (path.isBlank()) {
			throw new IllegalArgumentException("Given path was blank.");
		}
		return new Gson().fromJson(readFile(path), JsonObject.class);
	}

	/**
	 * Writes given string content to a file at given path.
	 *
	 * @param path   Path to write file to.
	 * @param string Content to write in file.
	 */
	public static void writeFile(final String path, final String string) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(string);

		if (path.isBlank()) {
			throw new IllegalArgumentException("Given path was blank.");
		}

		FileWriter file = null;
		try {
			file = new FileWriter(path);
			Objects.requireNonNull(file);
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
		Objects.requireNonNull(path);

		if (path.isBlank()) {
			throw new IllegalArgumentException("Given path was blank.");
		}

		// Check if file exists
		final File f = new File(path);
		if (!f.exists() || f.isDirectory()) {
			throw new UnsupportedOperationException("File <" + path + "> does not exist.");
		}

		String read = "";
		try {
			read = Files.readString(Path.of(path));
			Objects.requireNonNull(read);
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
		Objects.requireNonNull(toConvert);
		return toConvert.replace("\r\n", System.lineSeparator()).replace("\n", System.lineSeparator());
	}

	/**
	 * Creates a new folder at the given path if it is non-existent beforehand.
	 * 
	 * @param folderPath Path to create a new folder at.
	 */
	public static void prepareFolder(final String folderPath) {
		Objects.requireNonNull(folderPath);
		final File f = new File(folderPath);
		Objects.requireNonNull(f);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * Checks if a file exists at the given path.
	 * 
	 * @param path Path to check for file existence.
	 * @return True if file exists, false otherwise.
	 */
	public static boolean checkIfFileExists(final String path) {
		Objects.requireNonNull(path);
		final File f = new File(path);
		return f.exists() && !f.isDirectory();
	}

	/**
	 * Deletes the file with the given file path if it is not a directory.
	 * 
	 * @param path File path to delete file on.
	 */
	public static void deleteFile(final String path) {
		Objects.requireNonNull(path);
		final File toDelete = new File(path);
		if (toDelete.isDirectory()) {
			throw new IllegalArgumentException("Given path is not a file but a directory.");
		}
		toDelete.delete();
	}

}
