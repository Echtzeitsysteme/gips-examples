package teachingassistant.kcl.gipssolutionaltinc.runner;

import teachingassistant.kcl.gipssolutioninc.preprocessing.PreprocessingGtApp;
import teachingassistant.kcl.metamodelalt.generator.SimpleTaKclGenerator;
import teachingassistant.kcl.metamodelalt.generator.TeachingAssistantKclManipulator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TeachingAssistantPipelineRunner {

	public static void main(final String[] args) {
		// Generate the initial model
		SimpleTaKclGenerator.main(null);

		// Optimize/solve the initial model/problem
		TeachingAssistantRunner.main(null);

		// Validate the solution
		TeachingAssistantKclValidator.main(null);

		// Alter the solution, i.e., violate a constraint by changing the model
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodelalt/instances/";
		final String filePath = instanceFolder + TeachingAssistantKclValidator.SCENARIO_FILE_NAME;
		final TeachingAssistantKclManipulator manipulator = new TeachingAssistantKclManipulator(filePath);
		manipulator.execute();

		// Model should now be invalid
		TeachingAssistantKclValidator.main(null);

		// Run pre-processing for the second optimization stage
		final PreprocessingGtApp pre = new PreprocessingGtApp(filePath);
		pre.run();

		// Optimize/solve the changed model
		TeachingAssistantRunner.scenarioFileName = TeachingAssistantKclValidator.SCENARIO_FILE_NAME;
		TeachingAssistantRunner.main(null);

		// Validate the solution
		TeachingAssistantKclValidator.main(null);
		System.exit(0);
	}

}
