package ihtcgips.patterns.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import ihtcmetamodel.Hospital;
import ihtcmetamodel.IhtcmetamodelPackage;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.utils.FileUtils;

public class Utilities {

	private Utilities() {
	}

	/**
	 * Transforms a given JSON file to an XMI file.
	 * 
	 * @param inputJsonPath Input JSON file.
	 * @param outputXmiPath Output XMI file.
	 */
	public static void transformJsonToModel(final String inputJsonPath, final String outputXmiPath) {
		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputJsonPath);
		final Hospital model = loader.getModel();
		try {
			// Prepare folder if necessary
			if (inputJsonPath.contains("/")) {
				FileUtils.prepareFolder(inputJsonPath.substring(0, inputJsonPath.lastIndexOf("/")));
			}
			FileUtils.save(model, outputXmiPath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}
	}

	public static double tickTockToSeconds(final long tick, final long tock) {
		return 1.0 * (tock - tick) / 1_000_000_000;
	}

	public static Hospital loadHospitalFromFile(final URI absPath) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		resourceSet.getPackageRegistry().put(IhtcmetamodelPackage.eINSTANCE.getNsURI(), IhtcmetamodelPackage.eINSTANCE);
		resourceSet.getResource(absPath, true);
		return (Hospital) resourceSet.getResources().get(0).getContents().get(0);
	}

	public static void deleteFile(final String path) {
		if (new File(path).isDirectory()) {
			throw new IllegalArgumentException("Given path is not a file but a directory.");
		}
		try {
			Files.delete(Path.of(path));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Path createDir(final String path) {
		try {
			return Files.createTempDirectory("eMoflonTmp");
		} catch (final IOException e) {
			throw new RuntimeException("Unable to create temporary directory for eMoflon", e);
		}
	}

}
