package teachingassistant.kcl.gipssolutionaltincswap.runner;

import teachingassistant.kcl.metamodelalt.generator.SimpleTaKclGenerator;
import teachingassistant.kcl.metamodelalt.generator.TeachingAssistantKclManipulator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaIncSwapPipelineRunner {

	public static void main(final String[] args) {
		//
		// Generate the initial model
		//

		SimpleTaKclGenerator.main(null);

		//
		// Optimize/solve the initial model/problem
		//

		teachingassistant.kcl.gipssolutionalt.runner.TaBatchRunner.main(null);

		// Validate the solution
		TeachingAssistantKclValidator.main(null);

		// Save initial solution
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodelalt/instances/";
		final String filePath = instanceFolder + TeachingAssistantKclValidator.SCENARIO_FILE_NAME;

		//
		// Alter the solution, i.e., violate a constraint by changing the model
		//

		final TeachingAssistantKclManipulator manipulator = new TeachingAssistantKclManipulator(filePath);
		manipulator.executeBlocking();

		// Model should now be invalid
		TeachingAssistantKclValidator.main(null);

		//
		// Second stage optimization/repair
		//

		TaIncSwapRunner.scenarioFileName = TeachingAssistantKclValidator.SCENARIO_FILE_NAME;
		TaIncSwapRunner.main(null);

		// Validate the solution
		TeachingAssistantKclValidator.main(null);

		System.exit(0);
	}

}
