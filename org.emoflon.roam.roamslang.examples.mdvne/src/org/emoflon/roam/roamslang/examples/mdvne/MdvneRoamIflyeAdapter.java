package org.emoflon.roam.roamslang.examples.mdvne;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.roam.core.ilp.ILPSolverOutput;
import org.emoflon.roam.core.ilp.ILPSolverStatus;
import org.emoflon.roam.roamslang.examples.mdvne.api.roam.MdvneRoamAPI;

/**
 * Implementation adapter for Roam and iflye. This is used to run the Roam-based
 * MdVNE adapter from the iflye framework. Basically, this is an external entry
 * point to trigger the Roam-based MdVNE implementation from other frameworks.
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

		final URI absPath = URI.createFileURI(System.getProperty("user.dir") + "/" + modelPath);

		// Create new MdVNE Roam API and load the model
		final MdvneRoamAPI api = new MdvneRoamAPI();
		api.init(absPath);

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

		// The solution must currently be written as a file to be read by iflye
		try {
			api.saveResult(absPath.toFileString());
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// Terminate API
		// api.terminate();
		// TODO: Currently, this throws an Exception:
		//
		// java.lang.IllegalArgumentException: Cannot remove a consumer which was not
		// registered before!
		// at
		// org.emoflon.ibex.gt.api.GraphTransformationPattern.unsubscribeAppearing(GraphTransformationPattern.java:310)
		// at org.emoflon.roam.core.gt.GTMapper.terminate(GTMapper.java:69)
		// at org.emoflon.roam.core.RoamEngine.lambda$1(RoamEngine.java:71)
		// at java.base/java.util.HashMap.forEach(HashMap.java:1421)
		// at org.emoflon.roam.core.RoamEngine.terminate(RoamEngine.java:71)

		return (output.status() == ILPSolverStatus.OPTIMAL || output.status() == ILPSolverStatus.TIME_OUT);
	}

}
