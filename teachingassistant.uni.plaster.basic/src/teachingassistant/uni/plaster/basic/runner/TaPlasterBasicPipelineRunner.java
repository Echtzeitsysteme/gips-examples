package teachingassistant.uni.plaster.basic.runner;

import metamodel.TaAllocation;
import teachingassistant.uni.recomp.runner.AbstractGipsTeachingAssistantRecompPipelineRunner;
import teachingassistant.uni.metamodel.comparator.SolutionComparator;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;

/**
 * Runs the teaching assistant incremental pipeline (scenario generator, GIPSL
 * optimization, manipulator, incremental solution, and validator).
 */
public class TaPlasterBasicPipelineRunner extends AbstractGipsTeachingAssistantRecompPipelineRunner {

	/**
	 * No instantiations of this class.
	 */
	private TaPlasterBasicPipelineRunner() {
	}

	/**
	 * Entry point for the execution of this runner. All arguments will be ignored.
	 * 
	 * @param args All arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new TaPlasterBasicPipelineRunner().run();
	}

	/**
	 * Runs the pipeline.
	 */
	protected void run() {
		// Generate conflicting scenario.
		final TaAllocation firstSolution = prepareScenarioBlockedGen();

		//
		// Second stage optimization/repair
		//

		TaPlasterBasicRunner.scenarioFileName = TeachingAssistantUniValidator.SCENARIO_FILE_NAME;
		TaPlasterBasicRunner.main(null);

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
