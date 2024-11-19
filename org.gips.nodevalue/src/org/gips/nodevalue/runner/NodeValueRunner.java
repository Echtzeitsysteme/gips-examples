package org.gips.nodevalue.runner;

import org.eclipse.emf.common.util.URI;
import org.gips.nodevalue.api.gips.NodevalueGipsAPI;

public class NodeValueRunner extends AbstractNodeValueRunner {

	private final String scenarioFileName = "model.xmi";

	public static void main(final String[] args) {
		new NodeValueRunner().run();
	}

	public void run() {
		//
		// Load an XMI model
		//

		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../nodevaluemetamodel/instances/";
		final String filePath = instanceFolder + scenarioFileName;

		checkIfFileExists(filePath);

		//
		// Initialize GIPS API
		//

		final NodevalueGipsAPI gipsApi = new NodevalueGipsAPI();
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
