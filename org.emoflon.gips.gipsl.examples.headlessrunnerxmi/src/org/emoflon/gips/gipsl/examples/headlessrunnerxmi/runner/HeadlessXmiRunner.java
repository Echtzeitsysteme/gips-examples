package org.emoflon.gips.gipsl.examples.headlessrunnerxmi.runner;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.headlessrunnerxmi.api.gips.HeadlessrunnerxmiGipsAPI;

import hipe.engine.config.HiPEPathOptions;

public class HeadlessXmiRunner {

	private final static String gipsXmi = "./gips-model.xmi";
	private final static String ibexXmi = "./ibex-patterns.xmi";
	private final static String hipeXmi = "./hipe-network.xmi";

	/**
	 * Runs the headless XMI example. First argument must be the file path for the
	 * input model to load.
	 * 
	 * This method uses hard-coded values for the XMI file paths to load, i.e., it
	 * uses the local path.
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

		// Set HiPE configuration parameters
		HiPEPathOptions.getInstance().setNetworkPath(URI.createFileURI(hipeXmi));
		HiPEPathOptions.getInstance()
				.setEngineClassName("org.emoflon.gips.gipsl.examples.headlessrunnerxmi.hipe.engine.HiPEEngine");

		final HeadlessrunnerxmiGipsAPI api = new HeadlessrunnerxmiGipsAPI();
		api.init(URI.createFileURI(gipsXmi), URI.createFileURI(arg), URI.createFileURI(ibexXmi));

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
