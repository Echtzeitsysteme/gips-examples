package ihtcmetamodel.loader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ihtcmetamodel.Hospital;
import ihtcmetamodel.IhtcmetamodelPackage;

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

	/**
	 * TODO.
	 * 
	 * @param path
	 */
	public static void prepareFolder(final String path) {
		final File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * TODO.
	 * 
	 * @param o
	 * @param type
	 */
	private static void checkNotNull(final Object o, final String type) {
		if (o == null) {
			throw new IllegalArgumentException(type + " must not be null.");
		}
	}

	/**
	 * TODO.
	 * 
	 * @param model
	 * @param path
	 * @throws IOException
	 */
	public static void save(final Hospital model, final String path) throws IOException {
		final Resource r = saveAndReturn(model, path);
		r.unload();
	}

	/**
	 * TODO.
	 */
	public static Resource saveAndReturn(final Hospital model, final String path) throws IOException {
		checkNotNull(model, "Model");
		checkNotNull(path, "Path");

		final URI uri = URI.createFileURI(path);
		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(IhtcmetamodelPackage.eNS_URI, IhtcmetamodelPackage.eINSTANCE);
		final Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}

}
