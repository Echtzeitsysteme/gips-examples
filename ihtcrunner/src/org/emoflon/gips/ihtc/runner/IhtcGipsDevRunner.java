package org.emoflon.gips.ihtc.runner;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.ihtc.runner.utils.XmiSetupUtil;

import ihtcgipssolution.api.gips.IhtcgipssolutionGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.utils.FileUtils;

/**
 * TODO.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsDevRunner extends AbstractIhtcGipsRunner {

	/**
	 * If true, the runner will print more detailed information.
	 */
	private boolean verbose = true;

	/**
	 * Create a new instance of this class.
	 */
	public IhtcGipsDevRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcGipsDevRunner runner = new IhtcGipsDevRunner();
		runner.setupDefaultPaths();
		runner.run();
	}

	/**
	 * Sets the default paths up.
	 */
	void setupDefaultPaths() {
		// Update output JSON file path
		this.datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/dev_runner/";
		this.outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";
	}

	/**
	 * Sets the JSON input path.
	 * 
	 * @param jsonInputPath JSON input path.
	 */
	public void setJsonInputPath(final String jsonInputPath) {
		this.inputPath = jsonInputPath;
	}

	/**
	 * Sets the JSON output path.
	 * 
	 * @param jsonOutputPath JSON output path.
	 */
	public void setJsonOutputPath(final String jsonOutputPath) {
		this.outputPath = jsonOutputPath;
	}

	/**
	 * Sets the XMI input model path.
	 * 
	 * @param xmiInputModelPath XMI input model path.
	 */
	public void setXmiInputModelPath(final String xmiInputModelPath) {
		this.instancePath = xmiInputModelPath;
	}

	/**
	 * Sets the XMI output model path.
	 * 
	 * @param xmiOutputModelPath XMI output model path.
	 */
	public void setXmiOutputModelPath(final String xmiOutputModelPath) {
		this.gipsOutputPath = xmiOutputModelPath;
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();
		final long tickStageOne = System.nanoTime();

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(inputPath, instancePath);

		//
		// Initialize GIPS API
		//

		if (verbose) {
			System.out.println("=> Start GIPS API.");
		}

		final IhtcgipssolutionGipsAPI gipsApi = new IhtcgipssolutionGipsAPI();
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, instancePath);
		// Set presolve to "auto"
//		GurobiTuningUtil.updatePresolve(gipsApi, -1);
//		if (randomSeed != -1) {
//			GurobiTuningUtil.updateRandomSeed(gipsApi, randomSeed);
//		}
//
//		if (verbose) {
//			GurobiTuningUtil.setDebugOutput(gipsApi);
//		}

		//
		// Run GIPS solution
		//

		buildAndSolve(gipsApi, verbose);
		applySolution(gipsApi, verbose);
		gipsSave(gipsApi, gipsOutputPath);
		exportToJson(gipsOutputPath, outputPath);
		gipsApi.terminate();
		if (verbose) {
			System.out.println("=> GIPS found a solution.");
		}
		final long tockStageOne = System.nanoTime();
		final double stageOneRuntime = 1.0 * (tockStageOne - tickStageOne) / 1_000_000_000;
		if (verbose) {
			System.out.println("=> GIPS run time: " + stageOneRuntime + "s.");
		}
		
//		{
//			gipsApi.getNurseShiftRoomLoad().getMappings().forEach((n,m) -> {
//				System.out.println(n + ": " + m.getValueOfLoad());
//			});
//		}

		//
		// The end
		//

		tock();
		printWallClockRuntime();
	}

	/**
	 * Takes an XMI output path (of a GIPS-generated solution model) and writes the
	 * corresponding JSON output to `jsonOutputPath`.
	 * 
	 * @param xmiOutputPath  GIPS-generated solution model to convert.
	 * @param jsonOutputPath JSON output file location to write the JSON output file
	 *                       to.
	 */
	private void exportToJson(final String xmiOutputPath, final String jsonOutputPath) {
		final Resource loadedResource = FileUtils.loadModel(xmiOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(jsonOutputPath);
	}

	/**
	 * Sets the verbose flag to the given value.
	 * 
	 * @param verbose Verbose flag.
	 */
	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

}
