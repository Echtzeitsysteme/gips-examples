package org.emoflon.gips.ihtc.runner;

import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.ihtc.runner.utils.FileUtils;
import org.emoflon.gips.ihtc.runner.utils.XmiSetupUtil;

import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.ModelToJsonExporter;

public class IhtcSoftCnstrTuningGipsRunner extends AbstractIhtcGipsRunner {

	/**
	 * If true, the runner will print more detailed information.
	 */
	private boolean verbose = true;

	/**
	 * Random seed for the (M)ILP solver.
	 */
	private int randomSeed = 0;

	/**
	 * Time limit for the (M)ILP solver.
	 */
	private int timeLimit = -1;

	/**
	 * Number of threads for the (M)ILP solver.
	 */
	private int threads = 0;

	/**
	 * Gurobi callback path.
	 */
	private String callbackPath = projectFolder + "/../ihtcvirtualrunner/scripts/callback.json";

	/**
	 * Gurobi parameter path.
	 */
	private String parameterPath = projectFolder + "/../ihtcvirtualrunner/scripts/parameter.json";

	/**
	 * Create a new instance of this class.
	 */
	public IhtcSoftCnstrTuningGipsRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);

		final IhtcSoftCnstrTuningGipsRunner runner = new IhtcSoftCnstrTuningGipsRunner();
		runner.setupDefaultPaths();
		runner.run();
	}

	/**
	 * Sets the default paths up.
	 */
	void setupDefaultPaths() {
		// Update output JSON file path
		this.datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/runner/";
		this.outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";
	}

	@Override
	public void run() {
		checkIfFileExists(inputPath);
		final long startTime = System.nanoTime();

		//
		// Convert JSON input file to XMI file
		//

		if (verbose) {
			logger.info("=> Start JSON model loader.");
		}

		transformJsonToModel(inputPath, instancePath);
		final long modelLoadedTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime model load: " + tickTockToElapsedSeconds(startTime, modelLoadedTime) + "s.");
		}

		//
		// Initialize GIPS API
		//

		if (verbose) {
			logger.info("=> Start GIPS init.");
		}

		Observer.getInstance().setCurrentSeries("Eval");
		final SoftcnstrtuningGipsAPI gipsApi = new SoftcnstrtuningGipsAPI();
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, instancePath);
		final long gipsInitDoneTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime GIPS init: " + tickTockToElapsedSeconds(startTime, gipsInitDoneTime) + "s.");
		}

		// Set GIPS configuration parameters from this object
		setGipsConfig(gipsApi);

		//
		// Run GIPS solution
		//

		buildAndSolve(gipsApi, verbose);
		final long gipsSolvingDoneTime = System.nanoTime();

		applySolution(gipsApi, verbose);

		final long solutionApplicationDoneTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime solution application: "
					+ tickTockToElapsedSeconds(gipsSolvingDoneTime, solutionApplicationDoneTime) + "s.");
		}
		gipsSave(gipsApi, gipsOutputPath);
		final long gipsSaveDoneTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime GIPS save: " + tickTockToElapsedSeconds(solutionApplicationDoneTime, gipsSaveDoneTime)
					+ "s.");
		}

		//
		// Export
		//

		if (verbose) {
			logger.info("=> Start JSON export.");
		}
		exportToJson(gipsOutputPath, outputPath);
		final long exportDoneTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime JSON export: " + tickTockToElapsedSeconds(gipsSaveDoneTime, exportDoneTime) + "s.");
		}

		//
		// The end
		//

		gipsApi.terminate();

		if (verbose) {
			logger.info("Runtime total: " + tickTockToElapsedSeconds(startTime, System.nanoTime()) + "s.");
		}
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
		Objects.requireNonNull(xmiOutputPath);
		Objects.requireNonNull(jsonOutputPath);

		final Resource loadedResource = FileUtils.loadModel(xmiOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		logger.info("Writing output JSON file to: " + outputPath);
		exporter.modelToJson(jsonOutputPath, verbose);
	}

	/**
	 * Sets the verbose flag to the given value.
	 * 
	 * @param verbose Verbose flag value.
	 */
	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets the random seed to the given value.
	 * 
	 * @param seed Random seed to set.
	 */
	public void setRandomSeed(final int seed) {
		this.randomSeed = seed;
	}

	/**
	 * Sets the (M)ILP solver time limit to the given value.
	 * 
	 * @param timeLimit Time limit to set.
	 */
	public void setTimeLimit(final int timeLimit) {
		this.timeLimit = timeLimit;
	}

	/**
	 * Sets the number of threads to be used by the (M)ILP solver.
	 * 
	 * @param threads Number of threads to set.
	 */
	public void setThreads(final int threads) {
		this.threads = threads;
	}

	/**
	 * Sets the Gurobi callback path to the given value.
	 * 
	 * @param callbackPath Gurobi callback path to set.
	 */
	public void setCallbackPath(final String callbackPath) {
		Objects.requireNonNull(callbackPath);
		this.callbackPath = callbackPath;
	}

	/**
	 * Sets the Gurobi parameter path to the given value.
	 * 
	 * @param parameterPath Gurobi parameter path to set.
	 */
	public void setParameterPath(final String parameterPath) {
		Objects.requireNonNull(parameterPath);
		this.parameterPath = parameterPath;
	}

	/**
	 * Sets the private GIPS API configuration parameters from this object to the
	 * actual GIPS API.
	 * 
	 * @param gipsApi GIPS API to set the configuration parameters for.
	 */
	private void setGipsConfig(final SoftcnstrtuningGipsAPI gipsApi) {
		Objects.requireNonNull(gipsApi);

		gipsApi.getSolverConfig().setRandomSeed(randomSeed);
		if (timeLimit != -1) {
			gipsApi.getSolverConfig().setTimeLimit(timeLimit);
		}
		gipsApi.getSolverConfig().setThreadCount(threads);
		if (callbackPath != null) {
			gipsApi.getSolverConfig().setEnableCallbackPath(true);
			gipsApi.getSolverConfig().setCallbackPath(callbackPath);
		}
		if (parameterPath != null) {
			gipsApi.getSolverConfig().setParameterPath(parameterPath);
		}
	}

}
