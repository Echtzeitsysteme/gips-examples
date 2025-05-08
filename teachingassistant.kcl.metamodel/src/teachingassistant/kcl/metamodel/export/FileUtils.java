package teachingassistant.kcl.metamodel.export;

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

import metamodel.Department;
import metamodel.MetamodelPackage;

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
	public static void writeFileFromJson(final String path, final JsonObject json) {
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
	public static JsonObject readFileToJson(final String path) {
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
	 * Creates a new folder at the given path if it is non-existent beforehand.
	 * 
	 * @param folderPath Path to create a new folder at.
	 */
	public static void prepareFolder(final String folderPath) {
		final File f = new File(folderPath);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * Checks if the given object is null and, if it is null, throws an exception
	 * with a type-specific error message.
	 * 
	 * @param o    Object to check null for.
	 * @param type Type-specific value for the error message.
	 */
	private static void checkNotNull(final Object o, final String type) {
		if (o == null) {
			throw new IllegalArgumentException(type + " must not be null.");
		}
	}

	/**
	 * Saves the given model as XMI to the given path.
	 * 
	 * @param model Model to be saved.
	 * @param path  Output (XMI) path for the model to be saved at.
	 * @throws IOException Throws an IOException if the file could not be written.
	 */
	public static void save(final Department model, final String path) throws IOException {
		final Resource r = saveAndReturn(model, path);
		r.unload();
	}

	/**
	 * Saves the given model as XMI to the given path and returns the respective
	 * resource.
	 * 
	 * @param model Model to be saved.
	 * @param path  Output (XMI) path for the model to be saved at.
	 * @return Respective resource of the model.
	 * @throws IOException Throws an IOException if the file could not be written.
	 */
	public static Resource saveAndReturn(final Department model, final String path) throws IOException {
		checkNotNull(model, "Model");
		checkNotNull(path, "Path");

		final URI uri = URI.createFileURI(path);
		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		final Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}

	/**
	 * Loads an EMF resource from a given (XMI) file path.
	 * 
	 * @param path (XMI) file path to load resource from.
	 * @return Loaded resource.
	 */
	public static Resource loadModel(final String path) {
		checkNotNull(path, "Path");
		final URI pathUri = URI.createFileURI(path);
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		resourceSet.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		resourceSet.getResource(pathUri, true);
		return resourceSet.getResources().getFirst();
	}

	/**
	 * Checks if a file exists at the given path.
	 * 
	 * @param path Path to check for file existence.
	 * @return True if file exists, false otherwise.
	 */
	public static boolean checkIfFileExists(final String path) {
		checkNotNull(path, "Path");
		final File f = new File(path);
		return f.exists() && !f.isDirectory();
	}

	/**
	 * Deletes the file with the given file path if it is not a directory.
	 * 
	 * @param path File path to delete file on.
	 */
	public static void deleteFile(final String path) {
		checkNotNull(path, "Path");
		final File toDelete = new File(path);
		if (toDelete.isDirectory()) {
			throw new IllegalArgumentException("Given path is not a file but a directory.");
		}
		toDelete.delete();
	}

}
