package org.emoflon.gips.ihtc.virtual.runner;

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
import org.emoflon.gips.ihtc.virtual.runner.utils.FileUtils;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.importexport.JsonToModelLoader;
import ihtcvirtualmetamodel.importexport.ModelToJsonExporter;
import ihtcvirtualpostprocessing.PostprocessingGtApp;
import ihtcvirtualpreprocessing.PreprocessingGtApp;
import ihtcvirtualpreprocessing.PreprocessingNoGtApp;

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
	public String scenarioFileName = "i01.json";

	/**
	 * Project folder location.
	 */
	public String projectFolder = System.getProperty("user.dir");

	/**
	 * Data set folder location.
	 */
	public String datasetFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/ihtc2024_competition_instances/";

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
	 * Default pre-processing output XMI path.
	 */
	public String preprocessingPath = instancePath.substring(0, instancePath.lastIndexOf(".xmi")) + "_pre-proc.xmi";

	/**
	 * Default instance solved XMI path.
	 */
	public String gipsOutputPath = instanceFolder + scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json"))
			+ "_solved.xmi";

	/**
	 * Default post-processing output XMI path.
	 */
	public String postProcOutputPath = gipsOutputPath.substring(0, gipsOutputPath.lastIndexOf(".xmi"))
			+ "_post-proc.xmi";

	/**
	 * Default JSON output folder path.
	 */
	public String datasetSolutionFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/";

	/**
	 * Default JSON output file path.
	 */
	public String outputPath = datasetSolutionFolder + "sol_"
			+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

	/**
	 * Default Output FDolder for Debug-files
	 */
	public String debugFolder = projectFolder + "/../ihtcvirtualmetamodel/instances/debug/";

	/**
	 * Default Output Path for Debug-file of current model instance
	 */
	public String debugOutputPath = debugFolder + scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json"))
			+ "_debug.txt";

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
		Objects.requireNonNull(verbose);

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
	protected void applySolution(final IhtcvirtualgipssolutionGipsAPI gipsApi, final boolean verbose) {
		Objects.requireNonNull(gipsApi);

		// Apply found solution
		// Do not update the pattern matcher on purpose
		gipsApi.getSelectedOperationDay().applyNonZeroMappings(false);
		gipsApi.getSelectedShiftToRoster().applyNonZeroMappings(false);
		gipsApi.getSelectedShiftToFirstWorkload().applyNonZeroMappings(false);
		gipsApi.getSelectedExtendingShiftToFirstWorkload().applyNonZeroMappings(false);
		gipsApi.getSelectedOccupantNodes().applyNonZeroMappings(false);
		// Alternative:
//		gipsApi.applyAllNonZeroMappings();

		// Update the pattern matcher after all rule applications once
		gipsApi.update();
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object. This method does not utilize the GT engine
	 * built-in to GIPS but rather manipulates the model directly.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolutionNoGt(final IhtcvirtualgipssolutionGipsAPI gipsApi, final boolean verbose) {
		Objects.requireNonNull(gipsApi);

		// Apply found solution
		final long tick = System.nanoTime();

		gipsApi.getSelectedOperationDay().getNonZeroVariableMappings().forEach(m -> {
			m.getMatch().getVopc().setIsSelected(true);
			m.getMatch().getVwc().setIsSelected(true);
		});
		gipsApi.getSelectedShiftToRoster().getNonZeroVariableMappings().forEach(m -> {
			m.getMatch().getVsr().setIsSelected(true);
		});
		gipsApi.getSelectedShiftToFirstWorkload().getNonZeroVariableMappings().forEach(m -> {
			m.getMatch().getVsw().setIsSelected(true);
		});
		gipsApi.getSelectedExtendingShiftToFirstWorkload().getNonZeroVariableMappings().forEach(m -> {
			m.getMatch().getNextvsw().setIsSelected(true);
		});
		gipsApi.getSelectedOccupantNodes().getNonZeroVariableMappings().forEach(m -> {
			m.getMatch().getVsw().setIsSelected(true);
		});

		final long tock = System.nanoTime();
		if (verbose) {
			logger.info("=> Solution application (no GT) duration: " + (tock - tick) / 1_000_000_000 + "s.");
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
		logger.info("Writing output JSON file to: " + outputPath);
		exporter.modelToJson(outputPath);
	}

	/**
	 * Pre-processing method that runs the separated GT rule set. The given
	 * `instancePath` will be used to load the XMI model. The produced (altered)
	 * model file will also be written to `instancePath`.
	 * 
	 * @param instancePath Model (XMI) to load and overwrite.
	 */
	@Deprecated
	protected void preprocess(final String instancePath) {
		preprocess(instancePath, instancePath);
	}

	/**
	 * Pre-processing method that runs the separated GT rule set. The given
	 * `instancePath` will be used to load the XMI model. The produced (altered)
	 * model file will be written to `outputPath`.
	 * 
	 * @param instancePath Model (XMI) to load.
	 * @param outputPath   Model (XMI) to save the result to.
	 */
	protected void preprocess(final String instancePath, final String outputPath) {
		Objects.requireNonNull(instancePath);

		final PreprocessingGtApp app = new PreprocessingGtApp(instancePath, outputPath);
		app.run();
		// The app will terminate itself
	}

	/**
	 * Pre-processing method that runs the separated Java-based pre-processing
	 * implementation. The given `instancePath` will be used to load the XMI model.
	 * The produced (altered) model file will be written to `outputPath`.
	 * 
	 * @param instancePath Model (XMI) to load.
	 * @param outputPath   Model (XMI) to save the result to.
	 */
	protected void preprocessNoGt(final String instancePath, final String outputPath) {
		Objects.requireNonNull(instancePath);

		final PreprocessingNoGtApp app = new PreprocessingNoGtApp(instancePath, outputPath);
		app.run();
	}

	/**
	 * Post-processing method that runs the separated GT rule set. The given
	 * `instancePath` will be used to load the XMI model. The produced (altered)
	 * model file will also be written to `instancePath`.
	 * 
	 * @param instancePath Model (XMI) to load and overwrite.
	 */
	@Deprecated
	protected void postprocess(final String instancePath) {
		postprocess(instancePath, instancePath);
	}

	/**
	 * Post-processing method that runs the separated GT rule set. The given
	 * `instancePath` will be used to load the XMI model. The produced (altered)
	 * model file be written to `outputPath`.
	 * 
	 * @param instancePath Model (XMI) to load.
	 * @param outputPath   Model (XMI) output path.
	 */
	protected void postprocess(final String instancePath, final String outputPath) {
		Objects.requireNonNull(instancePath);
		Objects.requireNonNull(outputPath);

		final PostprocessingGtApp app = new PostprocessingGtApp(instancePath, outputPath);
		app.run();
		// The app will terminate itself
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
	 * Runs the execution of the configured scenario.
	 */
	protected abstract void run();

}
