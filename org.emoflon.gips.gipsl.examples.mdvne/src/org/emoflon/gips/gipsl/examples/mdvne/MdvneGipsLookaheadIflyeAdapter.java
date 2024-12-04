package org.emoflon.gips.gipsl.examples.mdvne;

import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.gipsl.examples.mdvne.api.gips.MdvneGipsAPI;

/**
 * Implementation adapter for GIPS lookahead and iflye. This is used to run the
 * GIPS-based MdVNE adapter from the iflye framework. Basically, this is an
 * external entry point to trigger the GIPS-based MdVNE implementation from
 * other frameworks.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneGipsLookaheadIflyeAdapter {

	/**
	 * MdVNE GIPS API object.
	 */
	static MdvneGipsAPI api;

	/**
	 * If false, the API must be initialized.
	 */
	static boolean init = false;

	/**
	 * Executes the embedding GIPS-based VNE algorithm. Only the virtual network
	 * with the given networkId will be embedded.
	 * 
	 * @param model     Resource set that contains the model (= the root node of the
	 *                  model).
	 * @param networkId The network ID to embed the virtual network for.
	 * @return True if embedding was successful.
	 */
	public static boolean execute(final ResourceSet model, final String networkId) {
		if (model == null) {
			throw new IllegalArgumentException("Model was null.");
		}

		if (model.getResources() == null || model.getResources().isEmpty()) {
			throw new IllegalArgumentException("Model resource set was null or empty.");
		}

		if (networkId == null || networkId.isBlank()) {
			throw new IllegalArgumentException("Given virtual network ID was null or blank.");
		}
		
		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		// Initialize the API, if necessary
		if (!init) {
			api = new MdvneGipsAPI();
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
		
		final Map<String, IMeasurement> measurements = obs.getMeasurements("Eval");
		System.out.println("PM: " + measurements.get("PM").maxDurationSeconds());
		System.out.println("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds());
		System.out.println("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds());
		System.out.println("BUILD: " + measurements.get("BUILD").maxDurationSeconds());
		System.out.println("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds());

		// Apply only selected mappings filtered according to the network ID
		api.getSrv2srv().getNonZeroVariableMappings().forEach(t -> {
			if (t.getVirtualNetwork().getName().equals(networkId)) {
				api.getSrv2srv().getGTRule().apply(t.getMatch());
			}
		});
		api.getSw2node().getNonZeroVariableMappings().forEach(t -> {
			if (t.getVirtualNetwork().getName().equals(networkId)) {
				api.getSw2node().getGTRule().apply(t.getMatch());
			}
		});
		api.getL2p().getNonZeroVariableMappings().forEach(t -> {
			if (t.getVirtualNetwork().getName().equals(networkId)) {
				api.getL2p().getGTRule().apply(t.getMatch());
			}
		});
		api.getL2s().getNonZeroVariableMappings().forEach(t -> {
			if (t.getVirtualNetwork().getName().equals(networkId)) {
				api.getL2s().getGTRule().apply(t.getMatch());
			}
		});
		api.getNet2net().getNonZeroVariableMappings().forEach(t -> {
			if (t.getVirtualNetwork().getName().equals(networkId)) {
				api.getNet2net().getGTRule().apply(t.getMatch());
			}
		});

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
