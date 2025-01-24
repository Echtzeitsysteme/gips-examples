package teachingassistant.kcl.gipssolution.runner;

import teachingassistant.kcl.metamodel.generator.SimpleTaKclGenerator;
import teachingassistant.metamodel.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TeachingAssistantPipelineRunner {

	public static void main(final String[] args) {
		SimpleTaKclGenerator.main(null);
		TeachingAssistantRunner.main(null);
		TeachingAssistantKclValidator.main(null);
		System.exit(0);
	}

}
