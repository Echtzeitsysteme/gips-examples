package ihtcgipssolution.runner;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;

import ihtcgipssolution.api.gips.IhtcgipssolutionGipsAPI;
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
public class IhtcGipsRunner extends AbstractIhtcGipsRunner {

	private final String scenarioFileName = "test01.json";

	public static void main(final String[] args) {
		new IhtcGipsRunner().run();
	}

	public void run() {
		//
		// Folder and file definitions
		//

		final String projectFolder = System.getProperty("user.dir");

		// Input JSON file
		final String datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_test_dataset/";
		final String inputPath = datasetFolder + scenarioFileName;

		// Input XMI file
		final String instanceFolder = projectFolder + "/../ihtcmetamodel/instances/";
		final String instancePath = instanceFolder + scenarioFileName.replace(".json", ".xmi");

		// Output XMI file
		final String gipsOutputPath = instanceFolder
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_solved.xmi";

		// Output JSON file
		final String datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_test_solutions/";
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

		final long tick = System.nanoTime();
		buildAndSolve(gipsApi);
		final long tock = System.nanoTime();

		//
		// Apply the solution
		//

		applySolution(gipsApi);

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, gipsOutputPath);

		//
		// Convert solution XMI model to JSON output file
		//

		final ModelToJsonExporter exporter = new ModelToJsonExporter(model);
		exporter.modelToJson(outputPath);

		//
		// The end
		//

		System.out.println("Building + solving took " + 1.0 * (tock - tick) / 1_000_000_000 + "s.");
		gipsApi.terminate();
	}

}
