package teachingassistant.uni.batch.runner;

import teachingassistant.uni.metamodel.export.JsonToModelImporter;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaBatchPipelineRunner {

	public static void main(final String[] args) {
//		SimpleTaUniGenerator.main(null);
		final String projectFolder = System.getProperty("user.dir");
		final String resourceFolder = projectFolder + "/../teachingassistant.uni.metamodel/resources/";
		final String instanceFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances/";
		final String jsonFilePath = resourceFolder + "kcl_tiny_dataset.json";
		final String xmiFilePath = instanceFolder + "kcl_tiny_dataset.xmi";

		JsonToModelImporter.main(new String[] { jsonFilePath, xmiFilePath });
		TaBatchRunner.scenarioFileName = "kcl_tiny_dataset.xmi";
		TaBatchRunner.main(null);
		TeachingAssistantUniValidator.SCENARIO_FILE_NAME = "solved.xmi";
		TeachingAssistantUniValidator.main(null);
		System.exit(0);
	}

}
