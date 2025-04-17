package teachingassistant.kcl.gipssolutionalt.runner;

import org.eclipse.emf.common.util.URI;

import teachingassistant.kcl.gipssolutionalt.api.gips.GipssolutionaltGipsAPI;

public class TeachingAssistantRunner extends AbstractTeachingAssistantRunner {

	private final String scenarioFileName = "kcl_ta_allocation.xmi";

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

		final GipssolutionaltGipsAPI gipsApi = new GipssolutionaltGipsAPI();
		gipsApi.init(URI.createFileURI(filePath));

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

		System.out.println("Building + solving took " + 1.0 * (tock - tick) / 1_000_000_000 + "s.");
		gipsApi.terminate();
	}

}
