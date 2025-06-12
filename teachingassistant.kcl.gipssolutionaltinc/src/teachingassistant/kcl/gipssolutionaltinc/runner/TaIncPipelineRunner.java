package teachingassistant.kcl.gipssolutionaltinc.runner;

import metamodel.TAAllocation;
import teachingassistant.kcl.gipssolutioninc.preprocessing.PreprocessingGtApp;
import teachingassistant.kcl.metamodelalt.comparator.SolutionComparator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant incremental pipeline (scenario generator, GIPSL
 * optimization, manipulator, incremental solution, and validator).
 */
public class TaIncPipelineRunner extends AbstractGipsTeachingAssistantIncrementalPipelineRunner {

	/**
	 * No instantiations of this class.
	 */
	private TaIncPipelineRunner() {
	}

	/**
	 * Entry point for the execution of this runner. All arguments will be ignored.
	 * 
	 * @param args All arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new TaIncPipelineRunner().run();
	}

	/**
	 * Runs the pipeline.
	 */
	private void run() {
		// Chose whether to generate a scenario or use a scenario that can only be
		// solved by a complete re-plan procedure.
		final TAAllocation firstSolution = prepareScenarioGen();
//		final TAAllocation firstSolution = prepareScenarioReplan();

		//
		// Second stage optimization/repair
		//

		// Run pre-processing for the second optimization stage
		final PreprocessingGtApp pre = new PreprocessingGtApp(filePath);
		pre.run();

		// Optimize/solve the changed model
		TaIncRunner.scenarioFileName = TeachingAssistantKclValidator.SCENARIO_FILE_NAME;
		TaIncRunner.main(null);

		// Validate the solution
		validate();

		// Save second solution
		final TAAllocation secondSolution = loadModelFromFile(filePath);

		// Compare both solutions
		SolutionComparator.compareSolutions(firstSolution, secondSolution);

		// End
		System.exit(0);
	}

}
