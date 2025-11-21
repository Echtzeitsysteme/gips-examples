package teachingassistant.uni.batch.runner;

import java.util.logging.Logger;

import teachingassistant.uni.metamodel.export.JsonToModelImporter;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;
import teachingassistant.uni.utils.AbstractGipsTeachingAssistantRunner;
import teachingassistant.uni.utils.LoggingUtils;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaBatchPipelineRunner {

	/**
	 * Logger for system outputs.
	 */
	protected final static Logger logger = Logger.getLogger(TaBatchPipelineRunner.class.getName());

//	public static String SCENARIO_FILE_NAME = "kcl_tiny_dataset";
	public static String SCENARIO_FILE_NAME = "kcl_semester_dataset";

	public static void main(final String[] args) {
//		try {
//			Thread.sleep(20_000);
//		} catch (final InterruptedException e) {
//			e.printStackTrace();
//		}

		LoggingUtils.configureLogging(logger);
		final long start = System.nanoTime();

//		SimpleTaUniGenerator.main(null);
		final String projectFolder = System.getProperty("user.dir");
		final String resourceFolder = projectFolder + "/../teachingassistant.uni.metamodel/resources/";
		final String instanceFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances/";
		final String jsonFilePath = resourceFolder + SCENARIO_FILE_NAME + ".json";
		final String xmiFilePath = instanceFolder + SCENARIO_FILE_NAME + ".xmi";

		logger.info("=> Start JSON importer.");
		JsonToModelImporter.main(new String[] { jsonFilePath, xmiFilePath });
		final long afterImport = System.nanoTime();
		logger.info("Importer runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(start, afterImport)
				+ "s.");

		logger.info("=> Start GIPS batch runner.");
		TaBatchRunner.scenarioFileName = SCENARIO_FILE_NAME + ".xmi";
		TaBatchRunner.main(null);
		final long afterGips = System.nanoTime();

		logger.info("=> Start TA university validator.");
		TeachingAssistantUniValidator.SCENARIO_FILE_NAME = "solved.xmi";
		TeachingAssistantUniValidator.main(null);
		final long afterValidator = System.nanoTime();
		logger.info("Validator runtime: "
				+ AbstractGipsTeachingAssistantRunner.tickTockToSeconds(afterGips, afterValidator) + "s.");

		final long end = System.nanoTime();
		logger.info("Total runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(start, end) + "s.");
		logger.info("=> Finished.");
		System.exit(0);
	}

}
