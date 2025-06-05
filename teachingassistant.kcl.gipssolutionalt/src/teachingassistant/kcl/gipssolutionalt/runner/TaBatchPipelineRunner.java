package teachingassistant.kcl.gipssolutionalt.runner;

import teachingassistant.kcl.metamodelalt.generator.SimpleTaKclGenerator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaBatchPipelineRunner {

	public static void main(final String[] args) {
		SimpleTaKclGenerator.main(null);
		TaBatchRunner.main(null);
		TeachingAssistantKclValidator.main(null);
		System.exit(0);
	}

}
