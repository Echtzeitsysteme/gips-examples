package teachingassistant.uni.batch.runner;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;

import metamodel.TaAllocation;
import teachingassistant.uni.batch.api.gips.BatchGipsAPI;
import teachingassistant.uni.metamodel.export.FileUtils;
import teachingassistant.uni.metamodel.export.JsonToModelImporter;
import teachingassistant.uni.metamodel.export.ModelToJsonExporter;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;
import teachingassistant.uni.utils.AbstractGipsTeachingAssistantRunner;

public class TaBatchRunner extends AbstractGipsTeachingAssistantRunner {

	public static void main(final String[] args) {
		new TaBatchRunner().run();
	}

	public TaBatchRunner() {
		super();
	}

	private void log(final String message) {
		Objects.requireNonNull(message);
		if (verbose) {
			logger.info(message);
		}
	}

	public void run() {
		FileUtils.checkIfFileExists(inputPath);
		final long start = System.nanoTime();

		//
		// Load an XMI model
		//

		log("=> Start JSON model loader.");

		JsonToModelImporter.main(new String[] { inputPath, instancePath });

		final long modelLoadedTime = System.nanoTime();
		log("Runtime model load: " + tickTockToSeconds(start, modelLoadedTime) + "s.");

		//
		// Initialize GIPS API
		//

		log("=> Start GIPS.");
		Observer.getInstance().setCurrentSeries("Eval");
		final long gipsStart = System.nanoTime();
		final BatchGipsAPI gipsApi = new BatchGipsAPI();
		log("GIPS init.");
		gipsApi.init(URI.createFileURI(instancePath));
		// enableTracing(gipsApi);
		final long gipsInitDone = System.nanoTime();
		log("Runtime GIPS init: " + tickTockToSeconds(gipsStart, gipsInitDone) + "s.");

		log("Start GIPS update.");
		gipsApi.update();
		final long gipsUpdateDone = System.nanoTime();
		log("Runtime GIPS update: " + tickTockToSeconds(gipsInitDone, gipsUpdateDone) + "s.");

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi);
		final long gipsSolvingDone = System.nanoTime();
		log("Runtime GIPS build + solve: " + tickTockToSeconds(gipsUpdateDone, gipsSolvingDone) + "s.");

		//
		// Apply the solution
		//

		applySolution(gipsApi);
		final long gipsApplyDone = System.nanoTime();
		log("Runtime GIPS apply: " + tickTockToSeconds(gipsSolvingDone, gipsApplyDone) + "s.");

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, gipsOutputPath);
		final long gipsSaveDone = System.nanoTime();
		log("Runtime GIPS save: " + tickTockToSeconds(gipsApplyDone, gipsSaveDone) + "s.");

		//
		// Model Validation
		//

		log("=> Start TA university validator.");
		TeachingAssistantUniValidator.FILE_PATH = gipsOutputPath;
		TeachingAssistantUniValidator.main(null);
		final long afterValidator = System.nanoTime();
		log("Validator runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(gipsSaveDone, afterValidator)
				+ "s.");

		//
		// Export
		//

		if (outputPath != null && !outputPath.isBlank()) {
			log("=> Start JSON export.");
			final Resource model = FileUtils.loadModel(gipsOutputPath);
			final ModelToJsonExporter exporter = new ModelToJsonExporter((TaAllocation) model.getContents().get(0));
			exporter.modelToJson(outputPath);
			final long exportDone = System.nanoTime();
			log("Export runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(afterValidator, exportDone)
					+ "s.");
		}

		//
		// The end
		//

		gipsApi.terminate();

		final Map<String, IMeasurement> measurements = new LinkedHashMap<>(
				Observer.getInstance().getMeasurements("Eval"));
		Observer.getInstance().getMeasurements("Eval").clear();
		log("=> GIPS observer measurements:");
		log("\tPM: " + measurements.get("PM").maxDurationSeconds() + "s.");
		log("\tBUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds() + "s.");
		log("\tBUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds() + "s.");
		log("\tBUILD_TOTAL: " + measurements.get("BUILD").maxDurationSeconds() + "s.");
		log("\tSOLVE_MILP: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds() + "s.");

		final long end = System.nanoTime();
		log("Total runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(start, end) + "s.");
		log("=> Finished.");
		System.exit(0);
	}

}
