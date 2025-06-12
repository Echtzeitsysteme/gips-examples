package teachingassistant.kcl.gipssolutionaltincswap.runner;

import metamodel.TAAllocation;
import teachingassistant.kcl.gipssolutionaltinc.runner.AbstractGipsTeachingAssistantIncrementalPipelineRunner;
import teachingassistant.kcl.metamodelalt.comparator.SolutionComparator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant incremental pipeline (scenario generator, GIPSL
 * optimization, manipulator, incremental solution, and validator).
 */
public class TaIncSwapPipelineRunner extends AbstractGipsTeachingAssistantIncrementalPipelineRunner {

	/**
	 * No instantiations of this class.
	 */
	private TaIncSwapPipelineRunner() {
	}

	/**
	 * Entry point for the execution of this runner. All arguments will be ignored.
	 * 
	 * @param args All arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new TaIncSwapPipelineRunner().run();
	}

	/**
	 * Runs the pipeline.
	 */
	protected void run() {
		// Generate conflicting scenario.
		final TAAllocation firstSolution = prepareScenarioBlockedGen();

		//
		// Second stage optimization/repair
		//

		TaIncSwapRunner.scenarioFileName = TeachingAssistantKclValidator.SCENARIO_FILE_NAME;
		TaIncSwapRunner.main(null);

		// Validate the solution
		validate();

		// Save second solution
		final TAAllocation secondSolution = loadModelFromFile(filePath);

		// Compare
		SolutionComparator.compareSolutions(firstSolution, secondSolution);

		// End
		System.exit(0);
	}

}
