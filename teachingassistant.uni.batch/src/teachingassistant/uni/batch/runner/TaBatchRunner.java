package teachingassistant.uni.batch.runner;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;

import teachingassistant.uni.batch.api.gips.BatchGipsAPI;
import teachingassistant.uni.utils.AbstractGipsTeachingAssistantRunner;

public class TaBatchRunner extends AbstractGipsTeachingAssistantRunner {

	public static void main(final String[] args) {
		new TaBatchRunner().run();
	}

	public TaBatchRunner() {
		super();
	}

	public void run() {
		//
		// Load an XMI model
		//

		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances/";
		final String filePath = instanceFolder + scenarioFileName;

		checkIfFileExists(filePath);

		//
		// Initialize GIPS API
		//

		Observer.getInstance().setCurrentSeries("Eval");
		final long gipsStart = System.nanoTime();
		final BatchGipsAPI gipsApi = new BatchGipsAPI();
		logger.info("GIPS init.");
		gipsApi.init(URI.createFileURI(filePath));
//		enableTracing(gipsApi);
		final long gipsInitDone = System.nanoTime();
		logger.info("Runtime GIPS init: " + tickTockToSeconds(gipsStart, gipsInitDone) + "s.");

		logger.info("GIPS update.");
		gipsApi.update();
		final long gipsUpdateDone = System.nanoTime();
		logger.info("Runtime GIPS update: " + tickTockToSeconds(gipsInitDone, gipsUpdateDone) + "s.");

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi);
		final long gipsSolvingDone = System.nanoTime();
		logger.info("Runtime GIPS build + solve: " + tickTockToSeconds(gipsUpdateDone, gipsSolvingDone) + "s.");

		//
		// Apply the solution
		//

		applySolution(gipsApi);
		final long gipsApplyDone = System.nanoTime();
		logger.info("Runtime GIPS apply: " + tickTockToSeconds(gipsSolvingDone, gipsApplyDone) + "s.");

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, instanceFolder + "solved.xmi");
		final long gipsSaveDone = System.nanoTime();
		logger.info("Runtime GIPS save: " + tickTockToSeconds(gipsApplyDone, gipsSaveDone) + "s.");

		final Map<String, IMeasurement> measurements = new LinkedHashMap<>(
				Observer.getInstance().getMeasurements("Eval"));
		Observer.getInstance().getMeasurements("Eval").clear();
		logger.info("GIPS observer measurements:");
		logger.info("\tPM: " + measurements.get("PM").maxDurationSeconds() + "s.");
		logger.info("\tBUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds() + "s.");
		logger.info("\tBUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds() + "s.");
		logger.info("\tBUILD_TOTAL: " + measurements.get("BUILD").maxDurationSeconds() + "s.");
		logger.info("\tSOLVE_MILP: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds() + "s.");

		//
		// Verify continuity solution + print TA employment rating of the solution
		//
		
		final int continuity = new ContinuityVariableValdidator().verifyContinuity(gipsApi);
		final int employmentRating = new TaApprovalObjectiveCalculator().calculate(gipsApi);

		//
		// The end
		//
		
		gipsApi.terminate();

		// Objective statistics
		logger.info("---------------------------------------");
		logger.info("=> Objective value(s):");
		logger.info("\tEmployment rating value: " + employmentRating);
		logger.info("\tContinuity value:        " + continuity);
		logger.info("\tOverall objective value: " + (continuity + employmentRating));
		logger.info("---------------------------------------");
	}

}
