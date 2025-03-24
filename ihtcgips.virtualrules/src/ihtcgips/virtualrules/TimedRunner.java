package ihtcgips.virtualrules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import ihtcgips.virtualrules.api.VirtualrulesAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.IhtcmetamodelPackage;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.utils.FileUtils;

public class TimedRunner {

	private static String INPUT_FOLDER_PATH = "/home/mkratz/git/gips-examples/ihtcmetamodel/resources/ihtc2024_competition_instances/";
	private static String INPUT_PATH = null;
	private static String MODEL_PATH = null;

	public static void main(final String[] args) {
		new TimedRunner().run();
	}

	private void run() {
		for (int i = 1; i <= 30; i++) {
			System.out.println("==> Running scenario " + i);
			runSingleFile(i);
			System.out.println("---");
		}
		System.exit(0);
	}

	private void runSingleFile(final int scenarioNumber) {
		INPUT_PATH = INPUT_FOLDER_PATH + "i" + (scenarioNumber <= 9 ? "0" : "") + scenarioNumber + ".json";
		MODEL_PATH = INPUT_PATH.replace(".json", ".xmi");

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(INPUT_PATH, MODEL_PATH);
		final Hospital model = loadHospitalFromFile(URI.createFileURI(MODEL_PATH));
		runGtRules(model);
	}

	private void runGtRules(final Hospital model) {
		final long tick = System.nanoTime();

		final VirtualrulesAPI api = new GtApp(model).initAPI();
		api.assignNurseToRoomShift().findMatches().forEach(m -> {
			api.assignNurseToRoomShift().apply(m);
		});
		api.assignAdmissionDayToPatient().findMatches().forEach(m -> {
			api.assignAdmissionDayToPatient().apply(m);
		});
		api.assignRoomToPatient().findMatches().forEach(m -> {
			api.assignRoomToPatient().apply(m);
		});
		api.assignSurgeryToPatient().findMatches().forEach(m -> {
			api.assignSurgeryToPatient().apply(m);
		});
		deleteFile(MODEL_PATH);

		final long tock = System.nanoTime();
		final double time = tickTockToSeconds(tick, tock);
		System.out.println("GT application runtime: " + time + "s.");
		api.terminate();
	}

	private double tickTockToSeconds(final long tick, final long tock) {
		return 1.0 * (tock - tick) / 1_000_000_000;
	}

	/**
	 * Transforms a given JSON file to an XMI file.
	 * 
	 * @param inputJsonPath Input JSON file.
	 * @param outputXmiPath Output XMI file.
	 */
	protected void transformJsonToModel(final String inputJsonPath, final String outputXmiPath) {
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

	private static Hospital loadHospitalFromFile(final URI absPath) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		resourceSet.getPackageRegistry().put(IhtcmetamodelPackage.eINSTANCE.getNsURI(), IhtcmetamodelPackage.eINSTANCE);
		resourceSet.getResource(absPath, true);
		return (Hospital) resourceSet.getResources().get(0).getContents().get(0);
	}

	private void deleteFile(final String path) {
		if (new File(path).isDirectory()) {
			throw new IllegalArgumentException("Given path is not a file but a directory.");
		}
		try {
			Files.delete(Path.of(path));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
