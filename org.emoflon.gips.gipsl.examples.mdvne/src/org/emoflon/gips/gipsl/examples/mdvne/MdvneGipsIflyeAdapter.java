package org.emoflon.gips.gipsl.examples.mdvne;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;
import org.emoflon.gips.gipsl.examples.mdvne.gips.MdvneGipsApi;
import org.emoflon.gips.gipsl.examples.mdvne.gips.MdvneHiPEGipsApi;

/**
 * Implementation adapter for GIPS and iflye. This is used to run the GIPS-based
 * MdVNE adapter from the iflye framework. Basically, this is an external entry
 * point to trigger the GIPS-based MdVNE implementation from other frameworks.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneGipsIflyeAdapter {

	/**
	 * Executes the embedding GIPS-based VNE algorithm.
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

		// Create new MdVNE GIPS API and load the model
		final MdvneGipsApi<?> api = new MdvneHiPEGipsApi();
		api.init(absPath);

		// Build the ILP problem (including updates)
		api.buildILPProblem(true);

		// Solve the ILP problem
		final ILPSolverOutput output = api.solveILPProblem();

		// TODO: Remove system outputs
		System.out.println("=> GIPS iflye adapter: Solver status: " + output.status());
		System.out.println("=> GIPS iflye adapter: Objective value: " + output.objectiveValue());

		// Apply all valid mappings
		api.getSrv2srv().applyNonZeroMappings();
		api.getSw2node().applyNonZeroMappings();
		api.getL2p().applyNonZeroMappings();
		api.getL2s().applyNonZeroMappings();
		api.getNet2net().applyNonZeroMappings();

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
		// at org.emoflon.gips.core.gt.GTMapper.terminate(GTMapper.java:69)
		// at org.emoflon.gips.core.GipsEngine.lambda$1(GIPSEngine.java:71)
		// at java.base/java.util.HashMap.forEach(HashMap.java:1421)
		// at org.emoflon.gips.core.GipsEngine.terminate(GIPSEngine.java:71)

		return (output.status() == ILPSolverStatus.OPTIMAL || output.status() == ILPSolverStatus.TIME_OUT);
	}

}
