package org.emoflon.gips.gipsl.examples.mdvne.migration;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.mdvne.migration.api.gips.MigrationGipsAPI;

/**
 * Implementation adapter for GIPS and iflye. This is used to run the GIPS-based
 * MdVNE migration adapter from the iflye framework. Basically, this is an
 * external entry point to trigger the GIPS-based MdVNE migration implementation
 * from other frameworks.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneMigrationGipsIflyeAdapter {

	/**
	 * MdVNE GIPS migration API object.
	 */
	static MigrationGipsAPI api;

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
			api = new MigrationGipsAPI();
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

		return output.solutionCount() > 0;
	}

	/**
	 * Resets the initialized state of the GIPS API.
	 */
	public static void resetInit() {
		init = false;
		api.terminate();
	}

}
