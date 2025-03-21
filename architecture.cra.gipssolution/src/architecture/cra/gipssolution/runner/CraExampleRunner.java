package architecture.cra.gipssolution.runner;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;

import architecture.cra.gipssolution.api.gips.GipssolutionGipsAPI;

/**
 * Runnable example runner for the CRA assignment problem (taken from the TGG3
 * project).
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class CraExampleRunner extends AbstractCraRunner {

	/**
	 * Run method to start the whole example runner.
	 */
	private void run() {
		//
		// Load a XMI model
		//

		final String projectFolder = System.getProperty("user.dir");
		final String scenarioName = "TTC_InputRDG_C";
		final String file = projectFolder + "/../../TGG-3.0-Prototype/RefactoringAC/resources/architecture/"
				+ scenarioName + ".xmi";

		checkIfFileExists(file);

		//
		// Create empty classes in the model
		//

		final ResourceSet rs = createEmptyClasses(file);

		//
		// Write changed model to file
		//

		final String preprocessedPath = System.getProperty("user.dir") + "/" + "preprocessed.xmi";
		writeXmiToFile(preprocessedPath, rs);

		//
		// Initialize GIPS API
		//
		
		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		GipssolutionGipsAPI gipsApi = new GipssolutionGipsAPI();
		gipsApi.init(URI.createFileURI(preprocessedPath));

		//
		// Build and solve the ILP problem
		//

		final long tick = System.nanoTime();
		buildAndSolve(gipsApi);
		final long tock = System.nanoTime();

		//
		// Evaluation
		//

		printAndApplySolution(gipsApi, true);
		System.out.println("---");

		// Count violations
		countViolations(gipsApi);

		System.out.println("---");
		System.out.println("Total solve time: " + (tock - tick) * 1.0 / 1_000_000_000 + " seconds");
		System.out.println("---");

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, "./solved.xmi");

		//
		// The end
		//

		final Map<String, IMeasurement> measurements = obs.getMeasurements("Eval");
		System.out.println("PM: " + measurements.get("PM").maxDurationSeconds());
		System.out.println("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds());
		System.out.println("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds());
		System.out.println("BUILD: " + measurements.get("BUILD").maxDurationSeconds());
		System.out.println("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds());
		
		gipsApi.terminate();
		java.lang.System.exit(0);
	}

	public static void main(final String[] args) {
		new CraExampleRunner().run();
	}

}
