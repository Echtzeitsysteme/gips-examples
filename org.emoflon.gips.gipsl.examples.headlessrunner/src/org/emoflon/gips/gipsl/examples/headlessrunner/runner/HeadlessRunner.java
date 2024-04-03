package org.emoflon.gips.gipsl.examples.headlessrunner.runner;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.headlessrunner.api.gips.HeadlessrunnerGipsAPI;

public class HeadlessRunner {

	/**
	 * Runs the headless example. First argument must be the file path for the input
	 * model to load.
	 * 
	 * @param args Array of arguments.
	 */
	public static void main(final String[] args) {
		if (args == null || args.length == 0) {
			throw new IllegalArgumentException("Arguments were null or empty.");
		}

		final String arg = args[0];
		if (arg == null || arg.isBlank()) {
			throw new IllegalArgumentException("First argument was null or empty.");
		}

		final HeadlessrunnerGipsAPI api = new HeadlessrunnerGipsAPI();
		api.init(URI.createFileURI(arg));

		api.buildILPProblem(true);
		final ILPSolverOutput output = api.solveILPProblem();
		System.out.println("Solver status: " + output.status());
		System.out.println("Objective value: " + output.objectiveValue());

		api.getZeroNode().applyNonZeroMappings();
		api.terminate();

		System.out.println("GIPSL run finished.");
		System.exit(0);
	}

}
