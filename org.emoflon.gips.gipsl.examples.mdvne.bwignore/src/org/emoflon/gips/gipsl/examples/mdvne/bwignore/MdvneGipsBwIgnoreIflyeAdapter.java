package org.emoflon.gips.gipsl.examples.mdvne.bwignore;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.gipsl.examples.mdvne.MdvneGipsIflyeAdapter;
import org.emoflon.gips.gipsl.examples.mdvne.MdvneGipsIflyeAdapterUtil;
import org.emoflon.gips.gipsl.examples.mdvne.bwignore.api.gips.BwignoreGipsAPI;

import hipe.engine.config.HiPEPathOptions;

/**
 * Implementation adapter for GIPS and iflye. This is used to run the GIPS-based
 * MdVNE adapter from the iflye framework, while ignoring all bandwidth
 * constraints. Basically, this is an external entry point to trigger the
 * GIPS-based MdVNE implementation from other frameworks.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneGipsBwIgnoreIflyeAdapter extends MdvneGipsIflyeAdapter {

	/**
	 * MdVNE GIPS API object.
	 */
	private BwignoreGipsAPI api;

	/**
	 * If false, the API must be initialized.
	 */
	private boolean init = false;

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
	@Override
	public MdvneGipsIflyeAdapter.MdvneIflyeOutput execute(final ResourceSet model, final String gipsXmi,
			final String ibexXmi, final String hipeXmi) {
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
				.setEngineClassName("org.emoflon.gips.gipsl.examples.mdvne.bwignore.hipe.engine.HiPEEngine");

		if (!init) {
			api = new BwignoreGipsAPI();
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
	@Override
	public MdvneGipsIflyeAdapter.MdvneIflyeOutput execute(final ResourceSet model) {
		if (model == null) {
			throw new IllegalArgumentException("Model was null.");
		}

		if (model.getResources() == null || model.getResources().isEmpty()) {
			throw new IllegalArgumentException("Model resource set was null or empty.");
		}

		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		// Initialize the API, if necessary
		if (!init) {
			api = new BwignoreGipsAPI();
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
	private MdvneGipsIflyeAdapter.MdvneIflyeOutput buildAndSolve() {
		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		// Build the ILP problem (including updates)
		api.buildProblemTimed(true);

		// Solve the ILP problem
		final SolverOutput output = api.solveProblemTimed();

		// TODO: Remove system outputs
		logger.info("=> GIPS iflye adapter: Solver status: " + output.status());
		logger.info("=> GIPS iflye adapter: Objective value: " + output.objectiveValue());

		final Map<String, IMeasurement> measurements = new LinkedHashMap<>(obs.getMeasurements("Eval"));
		obs.getMeasurements("Eval").clear();
		logger.info("PM: " + measurements.get("PM").maxDurationSeconds());
		logger.info("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds());
		logger.info("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds());
		logger.info("BUILD: " + measurements.get("BUILD").maxDurationSeconds());
		logger.info("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds());

		final Map<String, String> matches = extractMatchedNodes(this.api.getMappers().values());

		// Apply all valid mappings
		api.getSrv2srv().applyNonZeroMappings();
		api.getSw2node().applyNonZeroMappings();
		api.getL2p().applyNonZeroMappings();
		api.getL2s().applyNonZeroMappings();
		api.getNet2net().applyNonZeroMappings();

		return new MdvneIflyeOutput(output, matches, measurements);
	}

	/**
	 * Resets the initialized state of the GIPS API.
	 */
	@Override
	public void resetInit() {
		init = false;
		if (api != null) {
			api.terminate();
		}
		HiPEPathOptions.getInstance().resetNetworkPath();
		HiPEPathOptions.getInstance().resetEngineClassName();
	}

}
