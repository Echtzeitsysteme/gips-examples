package teachingassistant.uni.batch.runner;

import org.eclipse.emf.common.util.URI;

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

		final BatchGipsAPI gipsApi = new BatchGipsAPI();
		logger.info("GIPS init.");
		gipsApi.init(URI.createFileURI(filePath));
//		enableTracing(gipsApi);

		logger.info("GIPS update.");
		gipsApi.update();

		//
		// Build and solve the ILP problem
		//

		final long tick = System.nanoTime();
		buildAndSolve(gipsApi);
		final long tock = System.nanoTime();

		//
		// Apply the solution
		//

		applySolution(gipsApi);

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, instanceFolder + "solved.xmi");

		//
		// The end
		//

		logger.info("Building + solving took " + tickTockToSeconds(tick, tock) + "s.");
		gipsApi.terminate();
	}

}
