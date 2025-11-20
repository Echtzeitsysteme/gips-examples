package teachingassistant.uni.batch.runner;

import teachingassistant.uni.metamodel.export.JsonToModelImporter;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaBatchPipelineRunner {

	public static String SCENARIO_FILE_NAME = "kcl_tiny_dataset";
//	public static String SCENARIO_FILE_NAME = "kcl_semester_dataset";

	public static void main(final String[] args) {
//		SimpleTaUniGenerator.main(null);
		final String projectFolder = System.getProperty("user.dir");
		final String resourceFolder = projectFolder + "/../teachingassistant.uni.metamodel/resources/";
		final String instanceFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances/";
		final String jsonFilePath = resourceFolder + SCENARIO_FILE_NAME + ".json";
		final String xmiFilePath = instanceFolder + SCENARIO_FILE_NAME + ".xmi";

		JsonToModelImporter.main(new String[] { jsonFilePath, xmiFilePath });
		TaBatchRunner.scenarioFileName = SCENARIO_FILE_NAME + ".xmi";
		TaBatchRunner.main(null);
		TeachingAssistantUniValidator.SCENARIO_FILE_NAME = "solved.xmi";
		TeachingAssistantUniValidator.main(null);
		System.exit(0);
	}

}
