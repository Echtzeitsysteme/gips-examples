package teachingassistant.uni.batch.runner;

import teachingassistant.uni.metamodel.generator.SimpleTaUniGenerator;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaBatchPipelineRunner {

	public static void main(final String[] args) {
		SimpleTaUniGenerator.main(null);
		TaBatchRunner.main(null);
		TeachingAssistantUniValidator.main(null);
		System.exit(0);
	}

}
