package org.emoflon.gips.ihtc.runner;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import ihtcgipssolution.api.gips.IhtcgipssolutionGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.utils.FileUtils;

/**
 * This example runner can be used to load an IHTC 2024 JSON-based problem file,
 * convert it to an XMI file, solve the problem using our GIPS(L)
 * implementation, and writing the solution to a JSON file as required by the
 * contest.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsRunner extends AbstractIhtcGipsRunner {

	/**
	 * The scenario (JSON) file to load.
	 */
	protected String scenarioFileName = "test01.json";

	/**
	 * Project folder location.
	 */
	protected String projectFolder = System.getProperty("user.dir");

	/**
	 * Data set folder location.
	 */
	protected String datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_test_dataset/";

	/**
	 * Default input path.
	 */
	protected String inputPath = datasetFolder + scenarioFileName;

	/**
	 * Default instance folder path.
	 */
	protected String instanceFolder = projectFolder + "/../ihtcmetamodel/instances/";

	/**
	 * Default instance XMI path.
	 */
	protected String instancePath = instanceFolder + scenarioFileName.replace(".json", ".xmi");

	/**
	 * Default instance solved XMI path.
	 */
	protected String gipsOutputPath = instanceFolder
			+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_solved.xmi";

	/**
	 * Default JSON output folder path.
	 */
	protected String datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/";

	/**
	 * Default JSON output file path.
	 */
	protected String outputPath = datasetSolutionFolder + "sol_"
			+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new IhtcGipsRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	public void run() {
		tick();

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
		// Initialize GIPS API
		//

		final IhtcgipssolutionGipsAPI gipsApi = new IhtcgipssolutionGipsAPI();
		gipsApi.init(URI.createFileURI(instancePath));

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi, true);

		//
		// Apply the solution
		//

		applySolution(gipsApi, true);

		// TODO: remove me
//		printVariableValues(gipsApi);

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, gipsOutputPath);

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
		gipsApi.terminate();
	}

}
