package org.emoflon.gips.gipsl.examples.mdvne;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.gipsl.examples.mdvne.api.gips.MdvneGipsAPI;

import hipe.engine.config.HiPEPathOptions;

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
	 * Executes the embedding GIPS-based VNE algorithm.
	 * 
	 * @param model     Resource set that contains the model (= the root node of the
	 *                  model).
	 * @param gipsXmi   Path to the GIPS intermediate model XMI file.
	 * @param ibexXmi   Path to the IBeX model XMI file.
	 * @param hipeXmi   Path to the HiPE XMI file.
	 * @param networkId The network ID to embed the virtual network for.
	 * @return True if embedding was successful.
	 */
	public static boolean execute(final ResourceSet model, final String gipsXmi, final String ibexXmi,
			final String hipeXmi, final String networkId) {
		if (model == null) {
			throw new IllegalArgumentException("Model was null.");
		}

		if (model.getResources() == null || model.getResources().isEmpty()) {
			throw new IllegalArgumentException("Model resource set was null or empty.");
		}

		if (gipsXmi == null || gipsXmi.isBlank()) {
			throw new IllegalArgumentException("GIPS intermediate XMI path was null or empty.");
		}

		if (ibexXmi == null || ibexXmi.isBlank()) {
			throw new IllegalArgumentException("IBeX XMI path was null or empty.");
		}

		if (hipeXmi == null || hipeXmi.isBlank()) {
			throw new IllegalArgumentException("HiPE XMI path was null or empty.");
		}

		if (networkId == null || networkId.isBlank()) {
			throw new IllegalArgumentException("Given virtual network ID was null or blank.");
		}

		// Set HiPE configuration parameters
		HiPEPathOptions.getInstance().setNetworkPath(URI.createFileURI(hipeXmi));
		HiPEPathOptions.getInstance()
				.setEngineClassName("org.emoflon.gips.gipsl.examples.mdvne.hipe.engine.HiPEEngine");

		if (!init) {
			api = new MdvneGipsAPI();
			api.init(URI.createFileURI(gipsXmi), model, URI.createFileURI(ibexXmi));
			init = true;
		}

		// Check if multiple substrate networks are present
		MdvneGipsIflyeAdapterUtil.checkMultipleSubstrateNetworks(model);

		return buildAndSolve(networkId);
	}

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

		// Check if multiple substrate networks are present
		MdvneGipsIflyeAdapterUtil.checkMultipleSubstrateNetworks(model);

		return buildAndSolve(networkId);
	}

	/**
	 * Builds and solves the ILP problem using the GIPS API object.
	 * 
	 * @param networkId The network ID to embed the virtual network for.
	 * @return true, if a valid solution could be found.
	 */
	private static boolean buildAndSolve(final String networkId) {
		// Build the ILP problem (including updates)
		api.buildProblem(true);

		// Solve the ILP problem
		final SolverOutput output = api.solveProblem();

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
		HiPEPathOptions.getInstance().resetNetworkPath();
		HiPEPathOptions.getInstance().resetEngineClassName();
	}

}
