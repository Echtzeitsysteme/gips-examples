package teachingassistant.kcl.gipssolutionaltinchard.runner;

import org.eclipse.emf.common.util.URI;

import teachingassistant.kcl.gips.utils.AbstractGipsTeachingAssistantRunner;
import teachingassistant.kcl.gipssolutionaltinchard.api.gips.GipssolutionaltinchardGipsAPI;

public class TeachingAssistantRunner extends AbstractGipsTeachingAssistantRunner {

	public static void main(final String[] args) {
		new TeachingAssistantRunner().run();
	}

	public void run() {
		//
		// Load an XMI model
		//

		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodelalt/instances/";
		final String filePath = instanceFolder + scenarioFileName;

		checkIfFileExists(filePath);

		//
		// Initialize GIPS API
		//

		final GipssolutionaltinchardGipsAPI gipsApi = new GipssolutionaltinchardGipsAPI();
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

		System.out.println("Building + solving took " + tickTockToSeconds(tick, tock) + "s.");
		gipsApi.terminate();
	}

}
