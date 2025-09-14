package teachingassistant.uni.plaster.set.runner;

import metamodel.TaAllocation;
import teachingassistant.uni.recomp.runner.AbstractGipsTeachingAssistantRecompPipelineRunner;
import teachingassistant.uni.metamodel.comparator.SolutionComparator;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;

/**
 * Runs the teaching assistant incremental pipeline (scenario generator, GIPSL
 * optimization, manipulator, incremental solution, and validator).
 */
public class TaPlasterSetPipelineRunner extends AbstractGipsTeachingAssistantRecompPipelineRunner {

	/**
	 * No instantiations of this class.
	 */
	private TaPlasterSetPipelineRunner() {
	}

	/**
	 * Entry point for the execution of this runner. All arguments will be ignored.
	 * 
	 * @param args All arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new TaPlasterSetPipelineRunner().run();
	}

	/**
	 * Runs the pipeline.
	 */
	protected void run() {
		// Generate conflicting scenario.
		final TaAllocation firstSolution = prepareScenarioTimelimitGen();
//		final TaAllocation firstSolution = prepareScenarioBlockedGen();

		//
		// Second stage optimization/repair
		//

		TaPlasterSetRunner.scenarioFileName = TeachingAssistantUniValidator.SCENARIO_FILE_NAME;
		TaPlasterSetRunner.main(null);

		// Validate the solution
		validate();

		// Save second solution
		final TaAllocation secondSolution = loadModelFromFile(filePath);

		// Compare
		SolutionComparator.compareSolutions(firstSolution, secondSolution);

		// End
		System.exit(0);
	}

}
