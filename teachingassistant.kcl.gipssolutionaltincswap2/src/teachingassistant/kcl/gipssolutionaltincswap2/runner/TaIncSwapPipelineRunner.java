package teachingassistant.kcl.gipssolutionaltincswap2.runner;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.TAAllocation;
import teachingassistant.kcl.metamodelalt.comparator.SolutionComparator;
import teachingassistant.kcl.metamodelalt.export.FileUtils;
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
		final Resource firstResource = FileUtils.loadModel(filePath);
		final TAAllocation firstSolution = (TAAllocation) firstResource.getContents().get(0);

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

		// Save second solution
		final Resource secondResource = FileUtils.loadModel(filePath);
		final TAAllocation secondSolution = (TAAllocation) secondResource.getContents().get(0);

		// Compare
		SolutionComparator.compareSolutions(firstSolution, secondSolution);

		System.exit(0);
	}

}
