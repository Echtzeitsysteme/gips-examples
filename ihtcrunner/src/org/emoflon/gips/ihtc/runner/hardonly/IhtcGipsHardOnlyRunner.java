package org.emoflon.gips.ihtc.runner.hardonly;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.ihtc.runner.IhtcGipsRunner;

import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
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
public class IhtcGipsHardOnlyRunner extends IhtcGipsRunner {

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsHardOnlyRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new IhtcGipsHardOnlyRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();

		// Output JSON file
		this.datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/hard_only/";
		this.outputPath = datasetSolutionFolder + "sol_"
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
		// Initialize GIPS API
		//

		final HardonlyGipsAPI gipsApi = new HardonlyGipsAPI();
		gipsApi.init(URI.createFileURI(instancePath));

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi, true);

		//
		// Apply the solution
		//

		applySolution(gipsApi, true);

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
