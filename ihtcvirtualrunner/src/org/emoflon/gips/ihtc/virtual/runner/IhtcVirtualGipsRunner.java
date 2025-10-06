package org.emoflon.gips.ihtc.virtual.runner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.ihtc.virtual.runner.utils.FileUtils;
import org.emoflon.gips.ihtc.virtual.runner.utils.XmiSetupUtil;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.importexport.ModelToJsonExporter;
import ihtcvirtualmetamodel.importexport.ModelToJsonNoPostProcExporter;
import ihtcvirtualmetamodel.importexport.SolvedModelValidator;

public class IhtcVirtualGipsRunner extends AbstractIhtcVirtualGipsRunner {

	/**
	 * If true, the runner will print more detailed information.
	 */
	private boolean verbose = true;

	/**
	 * If true, the post processing will be skipped and the JSON output will be
	 * directly derived from the GIPS solution model (i.e., it will search for
	 * `isSelected == true` virtual objects.
	 */
	private boolean postProc = false;

	/**
	 * If true, the pre-processing will be executed with the Java-only (i.e., no GT)
	 * implementation.
	 */
	private boolean preProcNoGt = true;

	/**
	 * If true, the application of the GT rules of the GIPSL specification will only
	 * be simulated by manually written Java code instead of actually applying GT
	 * rule matches with eMoflon::IBeX-GT.
	 */
	private boolean applicationNoGt = false;

	/**
	 * If true a timestamp will be added to the filename.
	 */
	private boolean saveAllDebugFiles = true;

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
	public IhtcVirtualGipsRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);

		final IhtcVirtualGipsRunner runner = new IhtcVirtualGipsRunner();
		runner.setupDefaultPaths();
		runner.run();
	}

	/**
	 * Sets the default paths up.
	 */
	void setupDefaultPaths() {
		// Update output JSON file path
		this.datasetSolutionFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/runner/";
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
		// Pre-processing via a separated GT rule set
		//

		if (verbose) {
			logger.info("=> Start pre-processing.");
		}

		if (preProcNoGt) {
			preprocessNoGt(instancePath, preprocessingPath);
		} else {
			preprocess(instancePath, preprocessingPath);
		}
		final long preProcDoneTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime pre-processing: " + tickTockToElapsedSeconds(modelLoadedTime, preProcDoneTime) + "s.");
		}

		//
		// Initialize GIPS API
		//

		if (verbose) {
			logger.info("=> Start GIPS init.");
		}

		Observer.getInstance().setCurrentSeries("Eval");
		final IhtcvirtualgipssolutionGipsAPI gipsApi = new IhtcvirtualgipssolutionGipsAPI();
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, preprocessingPath);
		final long gipsInitDoneTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime GIPS init: " + tickTockToElapsedSeconds(preProcDoneTime, gipsInitDoneTime) + "s.");
		}

		// Set GIPS configuration parameters from this object
		setGipsConfig(gipsApi);

		//
		// Run GIPS solution
		//

		buildAndSolve(gipsApi, verbose);
		final long gipsSolvingDoneTime = System.nanoTime();

		if (applicationNoGt) {
			applySolutionNoGt(gipsApi, verbose);
		} else {
			applySolution(gipsApi, verbose);
		}

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
		// Model Validation
		//

		if (verbose) {
			logger.info("=> Start Model Validation");
		}
		validateModel(gipsOutputPath);
		final long validateDoneTime = System.nanoTime();
		if (verbose) {
			logger.info(
					"Runtime validate Model: " + tickTockToElapsedSeconds(gipsSaveDoneTime, validateDoneTime) + "s.");
		}

		//
		// Export
		//

		if (verbose) {
			logger.info("=> Start JSON export.");
		}

		if (postProc) {
			if (verbose) {
				logger.info("=> Start post-processing GT.");
			}
			postprocess(gipsOutputPath, postProcOutputPath);
			final long postProcessingDoneTime = System.nanoTime();
			if (verbose) {
				logger.info("Runtime post-processing: "
						+ tickTockToElapsedSeconds(validateDoneTime, postProcessingDoneTime) + "s.");
			}
			exportToJson(postProcOutputPath, outputPath);
			final long exportDoneTime = System.nanoTime();
			if (verbose) {
				logger.info("Runtime JSON export (with post-processing): "
						+ tickTockToElapsedSeconds(postProcessingDoneTime, exportDoneTime) + "s.");
			}
		} else {
			if (verbose) {
				logger.info("=> Skipped post-processing GT.");
			}
			exportToJsonNoPostProc(gipsOutputPath, outputPath);
			final long exportDoneTime = System.nanoTime();
			if (verbose) {
				logger.info("Runtime JSON export (no post-processing): "
						+ tickTockToElapsedSeconds(validateDoneTime, exportDoneTime) + "s.");
			}
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
	 * Takes a XMI output path (of a GIPS-generated solution model) and validates
	 * the model
	 * 
	 * @param gipsOutputPath
	 */
	private void validateModel(final String gipsOutputPath) {
		Objects.requireNonNull(gipsOutputPath);
		Objects.requireNonNull(debugOutputPath);
		final Resource loadedResource = FileUtils.loadModel(gipsOutputPath);
		final Root solvedHospital = (Root) loadedResource.getContents().get(0);
		final SolvedModelValidator validator = new SolvedModelValidator(solvedHospital, verbose);
		if (saveAllDebugFiles) {
			LocalDateTime now = LocalDateTime.now();
			String formatted = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String debugOutputPathTimeStamp = debugOutputPath.substring(0, debugOutputPath.lastIndexOf(".txt")) + "_"
					+ formatted + ".txt";
			validator.validate(debugOutputPathTimeStamp);
		} else {
			validator.validate(debugOutputPath);
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
		final Root solvedHospital = (Root) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		logger.info("Writing output JSON file to: " + outputPath);
		exporter.modelToJson(jsonOutputPath, verbose);
	}

	/**
	 * Takes an XMI output path (of a GIPS-generated solution model) and writes the
	 * corresponding JSON output to `jsonOutputPath`. This method relies on the
	 * non-post-processed model.
	 * 
	 * @param xmiOutputPath  GIPS-generated solution model to convert.
	 * @param jsonOutputPath JSON output file location to write the JSON output file
	 *                       to.
	 */
	private void exportToJsonNoPostProc(final String xmiOutputPath, final String jsonOutputPath) {
		Objects.requireNonNull(xmiOutputPath);
		Objects.requireNonNull(jsonOutputPath);

		final Resource loadedResource = FileUtils.loadModel(xmiOutputPath);
		final Root solvedHospital = (Root) loadedResource.getContents().get(0);
		final ModelToJsonNoPostProcExporter exporter = new ModelToJsonNoPostProcExporter(solvedHospital);
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
	 * Sets the pre-processing approach to the given value.
	 * 
	 * @param noGt If true, the Java-based pre-processing will be used.
	 */
	public void setPreProcessingApproach(final boolean noGt) {
		this.preProcNoGt = noGt;
	}

	/**
	 * Sets the private GIPS API configuration parameters from this object to the
	 * actual GIPS API.
	 * 
	 * @param gipsApi GIPS API to set the configuration parameters for.
	 */
	private void setGipsConfig(final IhtcvirtualgipssolutionGipsAPI gipsApi) {
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
