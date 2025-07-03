package teachingassistant.uni.recomp.runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.TaAllocation;
import teachingassistant.uni.utils.AbstractGipsTeachingAssistantRunner;
import teachingassistant.uni.metamodel.export.FileUtils;
import teachingassistant.uni.metamodel.generator.SimpleTaUniGenerator;
import teachingassistant.uni.metamodel.generator.TeachingAssistantUniManipulator;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;

public abstract class AbstractGipsTeachingAssistantIncrementalPipelineRunner {

	public final String projectFolder = System.getProperty("user.dir");
	public final String instanceFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances/";
	public final String filePath = instanceFolder + "solved.xmi";
	public final String filePathPlain = instanceFolder + AbstractGipsTeachingAssistantRunner.scenarioFileName;
	public final String filePathReplan = instanceFolder + "/uni_ta_allocation_total-replan.xmi";

	/**
	 * Runs the pipeline.
	 */
	protected abstract void run();

	/**
	 * Prepares (and returns) the scenario using the generator implementation to
	 * block a TA's entry.
	 * 
	 * @return TaAllocation scenario.
	 */
	protected TaAllocation prepareScenarioBlockedGen() {
		// Generate the initial model
		SimpleTaUniGenerator.main(null);
		final TaAllocation firstSolution = solveAndValidateBatch();

		// Alter the solution, i.e., violate a constraint by changing the model
		manipulateBlocking();
		return firstSolution;
	}

	/**
	 * Prepares (and returns) the re-plan scenario hard-coded.
	 * 
	 * @return TaAllocation scenario.
	 */
	protected TaAllocation prepareScenarioBlockedReplan() {
		// Copy re-planning scenario XMI file
		try {
			Files.copy(Path.of(filePathReplan), Path.of(filePathPlain), StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			throw new InternalError(e);
		}
		final TaAllocation firstSolution = solveAndValidateBatch();

		// Alter the solution, i.e., violate a constraint by changing the model
		manipulateBlocking();
		return firstSolution;
	}

	/**
	 * Prepares (and returns) the scenario using the generator implementation to
	 * reduce a TA's weekly time limit.
	 * 
	 * @return TaAllocation scenario.
	 */
	protected TaAllocation prepareScenarioTimelimitGen() {
		// Generate the initial model
		SimpleTaUniGenerator.main(null);
		final TaAllocation firstSolution = solveAndValidateBatch();

		// Alter the solution, i.e., violate a constraint by changing the model
		manipulateTimeLimit();

		return firstSolution;
	}

	/**
	 * Manipulates the model by blocking an entry of a TA.
	 */
	private void manipulateBlocking() {
		// Alter the solution, i.e., violate a constraint by changing the model
		final TeachingAssistantUniManipulator manipulator = new TeachingAssistantUniManipulator(filePath);
		manipulator.executeBlocking();

		// Model should now be invalid
		validate();
	}

	/**
	 * Manipulates the model by reducing the weekly time limit of a TA.
	 */
	private void manipulateTimeLimit() {
		// Alter the solution, i.e., violate a constraint by changing the model
		final TeachingAssistantUniManipulator manipulator = new TeachingAssistantUniManipulator(filePath);
		manipulator.executeHourReduction(1);

		// Model should now be invalid
		validate();
	}

	/**
	 * Common preparation steps necessary for all preparation methods.
	 * 
	 * @return TaAllocation after the batch runner was executed.
	 */
	private TaAllocation solveAndValidateBatch() {
		// Optimize/solve the initial model/problem
		teachingassistant.uni.batch.runner.TaBatchRunner.main(null);

		// Validate the solution
		validate();

		// Save initial solution
		final TaAllocation firstSolution = loadModelFromFile(filePath);
		return firstSolution;
	}

	/**
	 * Uses the teaching assistant university validator to validate the model.
	 */
	protected void validate() {
		TeachingAssistantUniValidator.main(null);
	}

	/**
	 * Loads a TaAllocation model from a given file path.
	 * 
	 * @param filePath File path to load the model from.
	 * @return TaAllocation model loaded from the given file path.
	 */
	protected TaAllocation loadModelFromFile(final String filePath) {
		Objects.requireNonNull(filePath);
		final Resource resource = FileUtils.loadModel(filePath);
		final TaAllocation model = (TaAllocation) resource.getContents().get(0);
		Objects.requireNonNull(model);
		return model;
	}

}
