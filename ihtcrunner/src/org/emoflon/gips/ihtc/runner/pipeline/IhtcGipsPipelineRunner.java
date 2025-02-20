package org.emoflon.gips.ihtc.runner.pipeline;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.ihtc.runner.IhtcGipsRunner;

import ihtcgipssolution.nursesrooms.api.gips.NursesroomsGipsAPI;
import ihtcgipssolution.patientssurgeonsrooms.api.gips.PatientssurgeonsroomsGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.loader.FileUtils;
import ihtcmetamodel.loader.JsonToModelLoader;
import ihtcmetamodel.loader.ModelToJsonExporter;

/**
 * This example runner can be used to load an IHTC 2024 JSON-based problem file,
 * convert it to an XMI file, solve the problem using our GIPS(L)
 * implementation, and writing the solution to a JSON file as required by the
 * contest.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsPipelineRunner extends IhtcGipsRunner {

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsPipelineRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new IhtcGipsPipelineRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();

		//
		// Folder and file definitions
		//
		final String inputPath = datasetFolder + scenarioFileName;

		// Input XMI file
		final String instanceFolder = projectFolder + "/../ihtcmetamodel/instances/";
		final String instancePath = instanceFolder + scenarioFileName.replace(".json", ".xmi");

		// Output XMI file
		final String gipsOutputPath = instanceFolder
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_solved.xmi";

		// Output JSON file
		final String datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/";
		final String outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputPath);
		final Hospital model = loader.getModel();
		try {
			FileUtils.prepareFolder(instanceFolder);
			FileUtils.save(model, instancePath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}

		//
		// Pipeline stage (1): assign patients to days, rooms, and operating theaters
		//

		final long tick = System.nanoTime();
		final PatientssurgeonsroomsGipsAPI gipsApiA = new PatientssurgeonsroomsGipsAPI();
		gipsApiA.init(URI.createFileURI(instancePath));
		buildAndSolve(gipsApiA);
		applySolution(gipsApiA);
		gipsSave(gipsApiA, instancePath);
		final long tock = System.nanoTime();
		final double stageATimeConsumed = 1.0 * (tock - tick) / 1_000_000_000;

		//
		// Pipeline stage (2): assign nurses to rooms
		//

		final NursesroomsGipsAPI gipsApiB = new NursesroomsGipsAPI();
		gipsApiB.init(URI.createFileURI(instancePath));
		gipsApiB.setTimeLimit(600 - stageATimeConsumed);
		buildAndSolve(gipsApiB);
		applySolution(gipsApiB);
		gipsSave(gipsApiB, gipsOutputPath);

		//
		// Convert solution XMI model to JSON output file
		//

		final Resource loadedResource = FileUtils.loadModel(gipsOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(outputPath);

		//
		// The end
		//

		tock();
		printWallClockRuntime();
		gipsApiA.terminate();
		gipsApiB.terminate();
	}

}
