package org.emoflon.roam.roamslang.examples.mdvne;

import org.eclipse.emf.common.util.URI;
import org.emoflon.roam.core.ilp.ILPSolverOutput;
import org.emoflon.roam.core.ilp.ILPSolverStatus;
import org.emoflon.roam.roamslang.examples.mdvne.api.roam.MdvneRoamAPI;

/**
 * Implementation adapter for Roam and iflye. This is used to run the Roab-based
 * MdVNE adapter from the iflye framework.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneRoamIflyeAdapter {

	/**
	 * Executes the embedding Roam-based VNE algorithm.
	 * 
	 * @param modelPath Path to the input model (XMI) that should be used for the
	 *                  embedding.
	 * @return True if embedding was successful.
	 */
	public static boolean execute(final String modelPath) {
		if (modelPath == null || modelPath.isBlank()) {
			throw new IllegalArgumentException("Model path was invalid.");
		}

		// Create new MdVNE Roam API and load the model
		final MdvneRoamAPI api = new MdvneRoamAPI();
		api.init(URI.createFileURI(modelPath));

		// Build the ILP problem (including updates)
		api.buildILPProblem(true);

		// Solve the ILP problem
		final ILPSolverOutput output = api.solveILPProblem();

		// TODO: Remove system outputs
		System.out.println("=> Roam iflye adapter: Solver status: " + output.status());
		System.out.println("=> Roam iflye adapter: Objective value: " + output.objectiveValue());

		// Apply all valid mappings
		api.getSrv2srv().applyNonZeroMappings();
		api.getSw2node().applyNonZeroMappings();
		api.getL2p().applyNonZeroMappings();
		api.getL2s().applyNonZeroMappings();

		// Terminate API
//		api.terminate();

		return (output.status() == ILPSolverStatus.OPTIMAL || output.status() == ILPSolverStatus.TIME_OUT);
	}

}
