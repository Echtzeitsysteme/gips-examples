package teachingassistant.uni.recomp.runner;

import metamodel.TaAllocation;
import teachingassistant.uni.recomp.preprocessing.PreprocessingGtApp;
import teachingassistant.uni.metamodel.comparator.SolutionComparator;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;

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
	protected void run() {
		// Chose whether to generate a scenario or use a scenario that can only be
		// solved by a complete re-plan procedure.
		final TaAllocation firstSolution = prepareScenarioBlockedGen();
//		final TaAllocation firstSolution = prepareScenarioBlockedReplan();
//		final TaAllocation firstSolution = prepareScenarioTimelimitGen();

		//
		// Second stage optimization/repair
		//

		// Run pre-processing for the second optimization stage
		final PreprocessingGtApp pre = new PreprocessingGtApp(filePath);
		pre.run();

		// Optimize/solve the changed model
		TaIncRunner.scenarioFileName = TeachingAssistantUniValidator.SCENARIO_FILE_NAME;
		TaIncRunner.main(null);

		// Validate the solution
		validate();

		// Save second solution
		final TaAllocation secondSolution = loadModelFromFile(filePath);

		// Compare both solutions
		SolutionComparator.compareSolutions(firstSolution, secondSolution);

		// End
		System.exit(0);
	}

}
