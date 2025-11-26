package teachingassistant.uni.utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

public abstract class AbstractGipsTeachingAssistantRunner {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * The scenario (JSON) file to load.
	 */
	public String scenarioFileName = "kcl_tiny_dataset.json";

	/**
	 * Project folder location.
	 */
	public String projectFolder = System.getProperty("user.dir");

	/**
	 * Data set folder location.
	 */
	public String resourceFolder = projectFolder + "/../teachingassistant.uni.metamodel/resources/";

	/**
	 * Default input path.
	 */
	public String inputPath = resourceFolder + scenarioFileName;

	/**
	 * Default instance folder path.
	 */
	public String instanceFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances/";

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
	 * Default output path.
	 */
	public String outputPath = resourceFolder + scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json"))
			+ "_solved.json";

	/**
	 * If true, the runner will print more detailed information.
	 */
	protected boolean verbose = true;

	/**
	 * Gurobi callback path.
	 */
	protected String callbackPath = projectFolder + "/../teachingassistant.uni.batch/scripts/callback.json";

	/**
	 * Gurobi parameter path.
	 */
	protected String parameterPath = projectFolder + "/../teachingassistant.uni.batch/scripts/parameter.json";

	/**
	 * Constructor to setup the logging.
	 */
	public AbstractGipsTeachingAssistantRunner() {
		LoggingUtils.configureLogging(logger);
	}

	/**
	 * Checks if a file for the given path exists and throws an exception otherwise.
	 * 
	 * @param path Path to check the file existence for.
	 */
	public void checkIfFileExists(final String path) {
		Objects.requireNonNull(path);
		final File xmiInputFile = new File(path);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("Input XMI file <" + path + "> could not be found.");
		}
	}

	/**
	 * Saves the result of a run of a given GIPS API to a given path as XMI file.
	 * 
	 * @param gipsApi GIPS API to save results from.
	 * @param path    (XMI) path to save the results to.
	 */
	public void gipsSave(final GipsEngineAPI<?, ?> gipsApi, final String path) {
		Objects.requireNonNull(gipsApi);
		Objects.requireNonNull(path);
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
	public void writeXmiToFile(final String path, final ResourceSet rs) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(rs);

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
	 * @return Returns the objective value.
	 */
	protected double buildAndSolve(final GipsEngineAPI<?, ?> gipsApi) {
		Objects.requireNonNull(gipsApi);
		log("Build GIPS problem.");
		// Build problem in parallel
		gipsApi.buildProblemTimed(true, true);
		log("Solve MILP problem.");
		final SolverOutput output = gipsApi.solveProblemTimed();
		if (output.solutionCount() == 0) {
			logger.warning("No solution found!");
		}
		log("Objective value: " + output.objectiveValue());
		return output.objectiveValue();
	}

	/**
	 * Applies the best found solution (aka all non-zero mappings) with a given GIPS
	 * API object.
	 * 
	 * @param gipsApi Teaching Assistant GIPS API object to get all mapping
	 *                information from.
	 */
	protected void applySolution(final GipsEngineAPI<?, ?> gipsApi) {
		Objects.requireNonNull(gipsApi);
		log("Apply GIPS solution.");
		// Apply found solution
		gipsApi.applyAllNonZeroMappings(false);
	}

	/**
	 * Enables the tracing feature on the given GIPS API objects.
	 * 
	 * @param gipsApi GIPS API object to enable the tracing feature on.
	 */
	protected void enableTracing(final GipsEngineAPI<?, ?> gipsApi) {
		Objects.requireNonNull(gipsApi);
		gipsApi.getTracer().enableTracing(true);
		gipsApi.getEclipseIntegrationConfig().setSolutionValuesSynchronizationEnabled(true);
		gipsApi.getSolverConfig().setEnableIIS(true);
	}

	/**
	 * Converts the given tick and tock value to a runtime in seconds.
	 * 
	 * @param tick Start epoch.
	 * @param tock End epoch.
	 * @return Elapsed time between tick and tock in seconds.
	 */
	public static double tickTockToSeconds(final long tick, final long tock) {
		if (tick < 0 || tock < 0) {
			throw new IllegalArgumentException("tick or tock was smaller than zero.");
		}
		return 1.0 * (tock - tick) / 1_000_000_000;
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
	 * Sets the Gurobi parameter path to the given value.
	 * 
	 * @param parameterPath Gurobi parameter path to set.
	 */
	public void setParameterPath(final String parameterPath) {
		Objects.requireNonNull(parameterPath);
		this.parameterPath = parameterPath;
	}

	protected void log(final String message) {
		Objects.requireNonNull(message);
		if (verbose) {
			logger.info(message);
		}
	}

}
