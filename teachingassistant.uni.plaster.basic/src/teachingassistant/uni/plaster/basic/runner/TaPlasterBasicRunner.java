package teachingassistant.uni.plaster.basic.runner;

import org.eclipse.emf.common.util.URI;

import teachingassistant.uni.utils.AbstractGipsTeachingAssistantRunner;
import teachingassistant.uni.plaster.basic.api.gips.BasicGipsAPI;

public class TaPlasterBasicRunner extends AbstractGipsTeachingAssistantRunner {

	public static void main(final String[] args) {
		new TaPlasterBasicRunner().run();
	}

	public TaPlasterBasicRunner() {
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

		final BasicGipsAPI gipsApi = new BasicGipsAPI();
		gipsApi.init(URI.createFileURI(filePath));
		enableTracing(gipsApi);

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
