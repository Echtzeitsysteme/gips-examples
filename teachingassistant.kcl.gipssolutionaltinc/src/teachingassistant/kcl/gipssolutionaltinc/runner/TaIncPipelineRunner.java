package teachingassistant.kcl.gipssolutionaltinc.runner;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.TAAllocation;
import teachingassistant.kcl.gips.utils.Tuple;
import teachingassistant.kcl.gipssolutioninc.preprocessing.PreprocessingGtApp;
import teachingassistant.kcl.metamodelalt.export.FileUtils;
import teachingassistant.kcl.metamodelalt.generator.SimpleTaKclGenerator;
import teachingassistant.kcl.metamodelalt.generator.TeachingAssistantKclManipulator;
import teachingassistant.kcl.metamodelalt.validator.TeachingAssistantKclValidator;

/**
 * Runs the teaching assistant pipeline (scenario generator, GIPSL optimization,
 * and validator).
 */
public class TaIncPipelineRunner {

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
		compareSolutions(firstSolution, secondSolution);

		System.exit(0);
	}

	/**
	 * Compares to given solutions regarding the number of identical mappings
	 * chosen.
	 * 
	 * @param first  First solution.
	 * @param second Second solution.
	 */
	private static void compareSolutions(final TAAllocation first, final TAAllocation second) {
		Objects.requireNonNull(first);
		Objects.requireNonNull(second);

		final Set<Tuple<String, String>> firstTuples = new HashSet<>();
		first.getModules().forEach(module -> module.getSessions()
				.forEach(session -> session.getOccurrences().forEach(occ -> occ.getTas().forEach(ta -> {
					firstTuples.add(new Tuple<String, String>(occ.getName(), ta.getName()));
				}))));

		final Set<Tuple<String, String>> secondTuples = new HashSet<>();
		second.getModules().forEach(module -> module.getSessions()
				.forEach(session -> session.getOccurrences().forEach(occ -> occ.getTas().forEach(ta -> {
					secondTuples.add(new Tuple<String, String>(occ.getName(), ta.getName()));
				}))));

		// Sanity check: both sets must be equal in size
		if (firstTuples.size() != secondTuples.size()) {
			throw new InternalError("Set sizes are different: " + firstTuples.size() + " vs. " + secondTuples.size());
		}

		// Count identical tuples
		int counter = 0;
		for (final Tuple<String, String> t : secondTuples) {
			for (final Tuple<String, String> tOrig : firstTuples) {
				if (t.equals(tOrig)) {
					counter++;
					break;
				}
			}
		}

		System.out.println(counter + " out of " + firstTuples.size() + " mappings were identical.");
	}

}
