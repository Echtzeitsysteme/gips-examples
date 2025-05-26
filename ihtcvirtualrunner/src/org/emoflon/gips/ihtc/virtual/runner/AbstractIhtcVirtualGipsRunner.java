package org.emoflon.gips.ihtc.virtual.runner;

import java.io.File;
import java.io.IOException;
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
import org.emoflon.gips.ihtc.virtual.runner.utils.FileUtils;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.importexport.JsonToModelLoader;
import ihtcvirtualmetamodel.importexport.ModelToJsonExporter;
import ihtcvirtualpreprocessing.PreprocessingGtApp;

/**
 * This abstract runner contains utility methods to wrap a given GIPS API object
 * in the context of the IHTC 2024 example using virtual edges/nodes.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public abstract class AbstractIhtcVirtualGipsRunner {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(AbstractIhtcVirtualGipsRunner.class.getName());

	/**
	 * The scenario (JSON) file to load.
	 */
	public String scenarioFileName = "test01.json";

	/**
	 * Project folder location.
	 */
	public String projectFolder = System.getProperty("user.dir");

	/**
	 * Data set folder location.
	 */
	public String datasetFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/ihtc2024_test_dataset/";

	/**
	 * Default input path.
	 */
	public String inputPath = datasetFolder + scenarioFileName;

	/**
	 * Default instance folder path.
	 */
	public String instanceFolder = projectFolder + "/../ihtcvirtualmetamodel/instances/";

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
	public String datasetSolutionFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/";

	/**
	 * Default JSON output file path.
	 */
	public String outputPath = datasetSolutionFolder + "sol_"
			+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

	public AbstractIhtcVirtualGipsRunner() {
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
		Objects.requireNonNull(verbose);

		gipsApi.buildProblemTimed(true);
		final SolverOutput output = gipsApi.solveProblemTimed();
		if (output.solutionCount() == 0) {
			gipsApi.terminate();
			logger.warning("No solution found. Aborting.");
			throw new InternalError("No solution found!");
		}
		if (verbose) {
			logger.info("=> Objective value: " + output.objectiveValue());
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
	protected void applySolution(final IhtcvirtualgipssolutionGipsAPI gipsApi, final boolean verbose) {
		Objects.requireNonNull(gipsApi);

		// Apply found solution
		final long tick = System.nanoTime();
//		gipsApi.getAssignSurgeonMapping().applyNonZeroMappings(false);
		gipsApi.getSelectedOperationDay().applyNonZeroMappings();
		gipsApi.getSelectedShiftToRoster().applyNonZeroMappings();
		gipsApi.getSelectedShiftToFirstWorkload().applyNonZeroMappings();
		gipsApi.getSelectedExtendingShiftToFirstWorkload().applyNonZeroMappings();

		// TODO: Add all other mappings and their application here
		if (gipsApi.getMappers().size() > 1) {
			throw new InternalError("Implementation is missing other mapping applications.");
		}

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
		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputJsonPath);
		final Root model = loader.getModel();
		try {
			// Prepare folder if necessary
			if (outputXmiPath.contains("/")) {
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
		final Root solvedHospital = (Root) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(outputPath);
	}

	/**
	 * Pre-processing method that runs the separated GT rule set. The given
	 * `instancePath` will be used to load the XMI model. The produced (altered)
	 * model file will also be written to `instancePath`.
	 * 
	 * @param instancePath Model (XMI) to load and overwrite.
	 */
	protected void preprocess(final String instancePath) {
		Objects.requireNonNull(instancePath);

		final PreprocessingGtApp app = new PreprocessingGtApp(instancePath);
		app.run();
		// The app will terminate itself
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	protected abstract void run();

}
