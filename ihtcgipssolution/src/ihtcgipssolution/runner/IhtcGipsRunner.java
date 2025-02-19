package ihtcgipssolution.runner;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

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

	/**
	 * Runtime tick.
	 */
	private long tick = 0;

	/**
	 * Runtime tock.
	 */
	private long tock = 0;

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
		// Initialize GIPS API
		//

		final IhtcgipssolutionGipsAPI gipsApi = new IhtcgipssolutionGipsAPI();
		gipsApi.init(URI.createFileURI(instancePath));

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi);

		//
		// Apply the solution
		//

		applySolution(gipsApi);

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

	/**
	 * Sets the current system time as tick value. The tock value gets re-set to 0.
	 */
	protected void tick() {
		this.tick = System.nanoTime();
		this.tock = 0;
	}

	/**
	 * Sets the current system time as tock value.
	 */
	protected void tock() {
		this.tock = System.nanoTime();
	}

	/**
	 * Prints the measured wall clock runtime value to System.out if its value is
	 * smaller than 10 minutes and to System.err otherwise.
	 */
	protected void printWallClockRuntime() {
		final double runtime = 1.0 * (tock - tick) / 1_000_000_000;

		if (runtime < 0) {
			throw new IllegalArgumentException("Runtime value was negative.");
		}

		if (runtime > 600) {
			System.err.println("=> Time limit of 10 minutes violated.");
			System.err.println("=> Wall clock run time: " + runtime + "s.");
		} else {
			System.out.println("=> Time limit of 10 minutes respected.");
			System.out.println("=> Wall clock run time: " + runtime + "s.");
		}
	}

}
