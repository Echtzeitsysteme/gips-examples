package org.emoflon.gips.gipsl.examples.mdvne;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;
import org.emoflon.gips.gipsl.examples.mdvne.api.gips.MdvneGipsAPI;

/**
 * Implementation adapter for GIPS and iflye. This is used to run the GIPS-based
 * MdVNE adapter from the iflye framework. Basically, this is an external entry
 * point to trigger the GIPS-based MdVNE implementation from other frameworks.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneGipsIflyeAdapter {

	/**
	 * MdVNE GIPS API object.
	 */
	final static MdvneGipsAPI api = new MdvneGipsAPI();

	/**
	 * If true, the API was already initialized.
	 */
	static boolean init = false;

	/**
	 * Executes the embedding GIPS-based VNE algorithm.
	 * 
	 * @param model Resource set that contains the model (= the root node of the
	 *              model).
	 * @return True if embedding was successful.
	 */
	public static boolean execute(final ResourceSet model) {
		if (model == null) {
			throw new IllegalArgumentException("Model was null.");
		}

		if (model.getResources() == null || model.getResources().isEmpty()) {
			throw new IllegalArgumentException("Model resource set was null or empty.");
		}

		// Init if not already initialized
		if (!init) {
			api.init(model);
			init = true;
		}

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
