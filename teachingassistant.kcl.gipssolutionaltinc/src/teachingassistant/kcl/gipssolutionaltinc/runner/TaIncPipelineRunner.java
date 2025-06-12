package teachingassistant.kcl.gipssolutionaltinc.runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.TAAllocation;
import teachingassistant.kcl.gipssolutioninc.preprocessing.PreprocessingGtApp;
import teachingassistant.kcl.metamodelalt.comparator.SolutionComparator;
import teachingassistant.kcl.metamodelalt.export.FileUtils;
import teachingassistant.kcl.metamodelalt.generator.SimpleTaKclGenerator;
import teachingassistant.kcl.metamodelalt.generator.TeachingAssistantKclManipulator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaIncPipelineRunner {

	final String projectFolder = System.getProperty("user.dir");
	final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodelalt/instances/";
	final String filePath = instanceFolder + TeachingAssistantKclValidator.SCENARIO_FILE_NAME;
	final String filePathPlain = instanceFolder + "/kcl_ta_allocation.xmi";
	final String filePathReplan = instanceFolder + "/kcl_ta_allocation_total-replan.xmi";

	private TaIncPipelineRunner() {
	}

	public static void main(final String[] args) {
		new TaIncPipelineRunner().run();
	}

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
		TeachingAssistantKclValidator.main(null);

		// Save second solution
		final Resource secondResource = FileUtils.loadModel(filePath);
		final TAAllocation secondSolution = (TAAllocation) secondResource.getContents().get(0);

		// Compare
		SolutionComparator.compareSolutions(firstSolution, secondSolution);

		System.exit(0);
	}

	private TAAllocation prepareScenarioGen() {
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

		final Resource firstResource = FileUtils.loadModel(filePath);
		final TAAllocation firstSolution = (TAAllocation) firstResource.getContents().get(0);

		//
		// Alter the solution, i.e., violate a constraint by changing the model
		//

		final TeachingAssistantKclManipulator manipulator = new TeachingAssistantKclManipulator(filePath);
		manipulator.executeBlocking();

		// Model should now be invalid
		TeachingAssistantKclValidator.main(null);

		return firstSolution;
	}

	private TAAllocation prepareScenarioReplan() {
		try {
			Files.copy(Path.of(filePathReplan), Path.of(filePathPlain), StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			throw new InternalError(e);
		}

		//
		// Optimize/solve the initial model/problem
		//

		teachingassistant.kcl.gipssolutionalt.runner.TaBatchRunner.main(null);

		// Validate the solution
		TeachingAssistantKclValidator.main(null);

		// Save initial solution

		final Resource firstResource = FileUtils.loadModel(filePath);
		final TAAllocation firstSolution = (TAAllocation) firstResource.getContents().get(0);

		//
		// Alter the solution, i.e., violate a constraint by changing the model
		//

		final TeachingAssistantKclManipulator manipulator = new TeachingAssistantKclManipulator(filePath);
		manipulator.executeBlocking();

		// Model should now be invalid
		TeachingAssistantKclValidator.main(null);

		return firstSolution;
	}

}
