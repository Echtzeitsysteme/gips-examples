package org.emoflon.gips.ihtc.runner;

import org.eclipse.emf.common.util.URI;

import ihtcgipssolution.api.gips.IhtcgipssolutionGipsAPI;

/**
 * This example runner can be used to load an IHTC 2024 JSON-based problem file,
 * convert it to an XMI file, solve the problem using our GIPS(L)
 * implementation, and writing the solution to a JSON file as required by the
 * contest.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsRunner extends AbstractIhtcGipsRunner {

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new IhtcGipsRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	public void run() {
		tick();

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(inputPath, instancePath);

		//
		// Initialize GIPS API
		//

		final IhtcgipssolutionGipsAPI gipsApi = new IhtcgipssolutionGipsAPI();
		gipsApi.init(URI.createFileURI(instancePath));

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi, true);

		//
		// Apply the solution
		//

		applySolution(gipsApi, true);

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, gipsOutputPath);

		//
		// Convert solution XMI model to JSON output file
		//

		transformModelToJson();

		//
		// The end
		//

		tock();
		printWallClockRuntime();
		gipsApi.terminate();
	}

}
