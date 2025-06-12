package teachingassistant.kcl.gipssolutionaltinc.runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.TAAllocation;
import teachingassistant.kcl.gips.utils.AbstractGipsTeachingAssistantRunner;
import teachingassistant.kcl.metamodelalt.export.FileUtils;
import teachingassistant.kcl.metamodelalt.generator.SimpleTaKclGenerator;
import teachingassistant.kcl.metamodelalt.generator.TeachingAssistantKclManipulator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

public abstract class AbstractGipsTeachingAssistantIncrementalPipelineRunner {

	public final String projectFolder = System.getProperty("user.dir");
	public final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodelalt/instances/";
	public final String filePath = instanceFolder + "solved.xmi";
	public final String filePathPlain = instanceFolder + AbstractGipsTeachingAssistantRunner.scenarioFileName;
	public final String filePathReplan = instanceFolder + "/kcl_ta_allocation_total-replan.xmi";

	/**
	 * Prepares (and returns) the scenario using the generator implementation.
	 * 
	 * @return TAAllocation scenario.
	 */
	protected TAAllocation prepareScenarioGen() {
		// Generate the initial model
		SimpleTaKclGenerator.main(null);
		return commonPreparation();
	}

	/**
	 * Prepares (and returns) the re-plan scenario hard-coded.
	 * 
	 * @return TAAllocation scenario.
	 */
	protected TAAllocation prepareScenarioReplan() {
		// Copy re-planning scenario XMI file
		try {
			Files.copy(Path.of(filePathReplan), Path.of(filePathPlain), StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			throw new InternalError(e);
		}
		return commonPreparation();
	}

	/**
	 * Common preparation steps necessary for all preparation methods.
	 * 
	 * @return TAAllocation after the batch runner was executed.
	 */
	private TAAllocation commonPreparation() {
		// Optimize/solve the initial model/problem
		teachingassistant.kcl.gipssolutionalt.runner.TaBatchRunner.main(null);

		// Validate the solution
		validate();

		// Save initial solution
		final TAAllocation firstSolution = loadModelFromFile(filePath);

		// Alter the solution, i.e., violate a constraint by changing the model
		final TeachingAssistantKclManipulator manipulator = new TeachingAssistantKclManipulator(filePath);
		manipulator.executeBlocking();

		// Model should now be invalid
		validate();

		return firstSolution;
	}

	/**
	 * Uses the teaching assistant KCL validator to validate the model.
	 */
	protected void validate() {
		TeachingAssistantKclValidator.main(null);
	}

	/**
	 * Loads a TAAllocation model from a given file path.
	 * 
	 * @param filePath File path to load the model from.
	 * @return TAAllocation model loaded from the given file path.
	 */
	protected TAAllocation loadModelFromFile(final String filePath) {
		Objects.requireNonNull(filePath);
		final Resource resource = FileUtils.loadModel(filePath);
		final TAAllocation model = (TAAllocation) resource.getContents().get(0);
		Objects.requireNonNull(model);
		return model;
	}

}
