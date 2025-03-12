package org.emoflon.gips.gipsl.examples.mdvne.heap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.gipsl.examples.mdvne.MdvneGipsIflyeAdapterUtil;
import org.emoflon.gips.gipsl.examples.mdvne.heap.api.gips.HeapGipsAPI;

import hipe.engine.config.HiPEPathOptions;

/**
 * Implementation adapter for GIPS and iflye. This is used to run the GIPS-based
 * MdVNE heap adapter from the iflye framework. Basically, this is an external
 * entry point to trigger the GIPS-based MdVNE heap implementation from other
 * frameworks.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneGipsHeapIflyeAdapter {

	/**
	 * MdVNE GIPS API object.
	 */
	static HeapGipsAPI api;

	/**
	 * If false, the API must be initialized.
	 */
	static boolean init = false;

	/**
	 * Executes the embedding GIPS-based VNE algorithm.
	 * 
	 * @param model   Resource set that contains the model (= the root node of the
	 *                model).
	 * @param gipsXmi Path to the GIPS intermediate model XMI file.
	 * @param ibexXmi Path to the IBeX model XMI file.
	 * @param hipeXmi Path to the HiPE XMI file.
	 * @return True if embedding was successful.
	 */
	public static boolean execute(final ResourceSet model, final String gipsXmi, final String ibexXmi,
			final String hipeXmi) {
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

		// Set HiPE configuration parameters
		HiPEPathOptions.getInstance().setNetworkPath(URI.createFileURI(hipeXmi));
		HiPEPathOptions.getInstance()
				.setEngineClassName("org.emoflon.gips.gipsl.examples.mdvne.hipe.engine.HiPEEngine");

		if (!init) {
			api = new HeapGipsAPI();
			api.init(URI.createFileURI(gipsXmi), model, URI.createFileURI(ibexXmi));
			init = true;
		}

		// Check if multiple substrate networks are present
		MdvneGipsIflyeAdapterUtil.checkMultipleSubstrateNetworks(model);

		return buildAndSolve();
	}

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

		// Initialize the API, if necessary
		if (!init) {
			api = new HeapGipsAPI();
			api.init(model);
			init = true;
		}

		// Check if multiple substrate networks are present
		MdvneGipsIflyeAdapterUtil.checkMultipleSubstrateNetworks(model);

		return buildAndSolve();
	}

	/**
	 * Builds and solves the ILP problem using the GIPS API object.
	 * 
	 * @return true, if a valid solution could be found.
	 */
	private static boolean buildAndSolve() {
		// Build the ILP problem (including updates)
		api.buildProblem(true);

		// Solve the ILP problem
		final SolverOutput output = api.solveProblem();

		// TODO: Remove system outputs
		System.out.println("=> GIPS iflye heap adapter: Solver status: " + output.status());
		System.out.println("=> GIPS iflye heap adapter: Objective value: " + output.objectiveValue());

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
