package teachingassistant.gipssolution.runner;

import teachingassistant.metamodel.generator.TeachingAssistantScenarioQu;
import teachingassistant.metamodel.validator.TeachingAssistantValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TeachingAssistantPipelineRunner {

	public static void main(final String[] args) {
		TeachingAssistantScenarioQu.main(null);
		TeachingAssistantRunner.main(null);
		TeachingAssistantValidator.main(null);
		System.exit(0);
	}

}
