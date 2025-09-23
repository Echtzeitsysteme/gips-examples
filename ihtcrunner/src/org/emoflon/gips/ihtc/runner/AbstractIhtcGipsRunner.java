package org.emoflon.gips.ihtc.runner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
//import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.utils.FileUtils;

/**
 * This abstract runner contains utility methods to wrap a given GIPS API object
 * in the context of the IHTC 2024 example.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public abstract class AbstractIhtcGipsRunner {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(AbstractIhtcGipsRunner.class.getName());

	/**
	 * The scenario (JSON) file to load.
	 */
	public String scenarioFileName = "i01.json";

	/**
	 * Project folder location.
	 */
	public String projectFolder = System.getProperty("user.dir");

	/**
	 * Data set folder location.
	 */
	public String datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";

	/**
	 * Default input path.
	 */
	public String inputPath = datasetFolder + scenarioFileName;

	/**
	 * Default instance folder path.
	 */
	public String instanceFolder = projectFolder + "/../ihtcmetamodel/instances/";

	/**
	 * Default instance XMI path.
	 */
	public String instancePath = instanceFolder + scenarioFileName.replace(".json", ".xmi");

	/**
	 * Default instance solved XMI path.
	 */
	public String gipsOutputPath = instanceFolder + scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json"))
			+ "_solved.xmi";

	/**
	 * Default JSON output folder path.
	 */
	public String datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/";

	/**
	 * Default JSON output file path.
	 */
	public String outputPath = datasetSolutionFolder + "sol_"
			+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

	/**
	 * If true, the runner will print more detailed information.
	 */
	protected boolean verbose = true;

	/**
	 * Random seed for the (M)ILP solver.
	 */
	protected int randomSeed = 0;

	/**
	 * Time limit for the (M)ILP solver.
	 */
	protected int timeLimit = -1;

	/**
	 * Number of threads for the (M)ILP solver.
	 */
	protected int threads = 0;

	/**
	 * Gurobi callback path.
	 */
	protected String callbackPath = projectFolder + "/../ihtcrunner/scripts/callback.json";

	/**
	 * Gurobi parameter path.
	 */
	protected String parameterPath = projectFolder + "/../ihtcrunner/scripts/parameter.json";

	/**
	 * Constructor for creating a new object. Initializes the logging.
	 */
	public AbstractIhtcGipsRunner() {
		// Configure logging
		logger.setUseParentHandlers(false);
		final ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {
			@Override
			public String format(final LogRecord record) {
				Objects.requireNonNull(record, "Given log entry was null.");
				return record.getMessage() + System.lineSeparator();
			}
		});
		logger.addHandler(handler);
	}

	/**
	 * Saves the result of a run of a given GIPS API to a given path as XMI file.
	 * 
	 * @param gipsApi GIPS API to save results from.
	 * @param path    (XMI) path to save the results to.
	 */
	protected void gipsSave(final GipsEngineAPI<?, ?> gipsApi, final String path) {
		Objects.requireNonNull(gipsApi);
		Objects.requireNonNull(path);
		logger.info("Saving GIPS output XMI file to: " + path);
		try {
			gipsApi.saveResult(path);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the given ResourceSet to an XMI file at the given file path.
	 * 
	 * @param path File path to save the ResourceSet's contents to.
	 * @param rs   ResourceSet which should be saved to file.
	 */
	protected void writeXmiToFile(final String path, final ResourceSet rs) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(rs);

		logger.info("Saving resource set <" + rs + "> to path: " + path);

		// Workaround: Always use absolute path
		final URI absPath = URI.createFileURI(path);

		// Create new model for saving
		final ResourceSet rs2 = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl(null));
		// ^null is okay if all paths are absolute
		final Resource r = rs2.createResource(absPath);
		// Fetch model contents from eMoflon
		r.getContents().add(rs.getResources().get(0).getContents().get(0));
		try {
			r.save(null);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds and solves the ILP problem for the given GIPS API. Also prints the
	 * objective value to the console and throws an error if no solution could be
	 * found.
	 * 
	 * @param gipsApi GIPS API to build and solve the ILP problem for.
	 * @param verbose If true, the method will print some more information about the
	 *                objective value.
	 * @return Returns the objective value.
	 */
	protected double buildAndSolve(final GipsEngineAPI<?, ?> gipsApi, final boolean verbose) {
		Objects.requireNonNull(gipsApi);

		gipsApi.buildProblemTimed(true, true); // Second Parameter: sequential = false/default, parallel = true
		final SolverOutput output = gipsApi.solveProblemTimed();
		if (output.solutionCount() == 0) {
			gipsApi.terminate();
			logger.warning("No solution found. Aborting.");
			throw new InternalError("No solution found!");
		}
		if (verbose) {
			logger.info("=> Objective value: " + output.objectiveValue());
			final Map<String, IMeasurement> measurements = new LinkedHashMap<>(
					Observer.getInstance().getMeasurements("Eval"));
			Observer.getInstance().getMeasurements("Eval").clear();
			logger.info("PM: " + measurements.get("PM").maxDurationSeconds() + "s.");
			logger.info("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds() + "s.");
			logger.info("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds() + "s.");
			logger.info("BUILD: " + measurements.get("BUILD").maxDurationSeconds() + "s.");
			logger.info("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds() + "s.");
		}
		return output.objectiveValue();
	}

	/**
	 * Checks if a file for the given path exists and throws an exception otherwise.
	 * 
	 * @param path Path to check the file existence for.
	 */
	protected void checkIfFileExists(final String path) {
		Objects.requireNonNull(path);

		final File xmiInputFile = new File(path);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("File <" + path + "> could not be found.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final HardonlyGipsAPI gipsApi, final boolean verbose) {
		Objects.requireNonNull(gipsApi);

		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			logger.info("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final SoftcnstrtuningGipsAPI gipsApi, final boolean verbose) {
		Objects.requireNonNull(gipsApi);

		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			logger.info("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Transforms a given JSON file to an XMI file.
	 * 
	 * @param inputJsonPath Input JSON file.
	 * @param outputXmiPath Output XMI file.
	 */
	protected void transformJsonToModel(final String inputJsonPath, final String outputXmiPath) {
		Objects.requireNonNull(inputJsonPath);
		Objects.requireNonNull(outputXmiPath);

		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputJsonPath);
		final Hospital model = loader.getModel();
		try {
			// Prepare folder if necessary
			if (inputJsonPath.contains("/")) {
				FileUtils.prepareFolder(outputXmiPath.substring(0, outputXmiPath.lastIndexOf("/")));
			}
			FileUtils.save(model, outputXmiPath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Transforms the model to JSON.
	 */
	protected void transformModelToJson() {
		final Resource loadedResource = FileUtils.loadModel(gipsOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(outputPath);
	}

	/**
	 * Converts the two given time stamps (tick and tock) from nano seconds to
	 * elapsed time in seconds.
	 * 
	 * @param tick First time stamp.
	 * @param tock Second time stamp.
	 * @return Elapsed time between tick and tock in seconds.
	 */
	protected double tickTockToElapsedSeconds(final long tick, final long tock) {
		if (tick < 0 || tock < 0) {
			throw new IllegalArgumentException("Given tick or tock was below zero.");
		}
		return 1.0 * (tock - tick) / 1_000_000_000;
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

	/**
	 * Runs the execution of the configured scenario.
	 */
	public abstract void run();

	/**
	 * Takes an XMI output path (of a GIPS-generated solution model) and writes the
	 * corresponding JSON output to `jsonOutputPath`.
	 * 
	 * @param xmiOutputPath  GIPS-generated solution model to convert.
	 * @param jsonOutputPath JSON output file location to write the JSON output file
	 *                       to.
	 */
	protected void exportToJson(final String xmiOutputPath, final String jsonOutputPath) {
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
	protected void setGipsConfig(final GipsEngineAPI<?, ?> gipsApi) {
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
		gipsApi.getSolverConfig().setEnableOutput(verbose);
	}

}
