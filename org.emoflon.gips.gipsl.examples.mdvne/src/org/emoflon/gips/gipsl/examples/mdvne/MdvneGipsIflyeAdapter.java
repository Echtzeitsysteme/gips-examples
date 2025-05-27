package org.emoflon.gips.gipsl.examples.mdvne;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.gt.GipsGTMapping;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.gipsl.examples.mdvne.api.gips.MdvneGipsAPI;
import org.emoflon.ibex.common.operational.IMatch;

import hipe.engine.config.HiPEPathOptions;

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
	private MdvneGipsAPI api;

	/**
	 * If false, the API must be initialized.
	 */
	private boolean init = false;

	/**
	 * Number of threads for the ILP solver. If set to -1, the default number of
	 * threads is used.
	 */
	int numberOfIlpSolverThreads = -1;

	/**
	 * GIPS output record for the iflye framework.
	 */
	public static record MdvneIflyeOutput(SolverOutput solverOutput, Map<String, String> matches,
			Map<String, IMeasurement> measurements) {
	}

	/**
	 * Set the number of threads to use for the ILP solver. If set to -1, the
	 * default number of threads is used.
	 * 
	 * @param numberOfThreads Number of threads to use for the ILP solver. Default
	 *                        is -1 (default number of threads).
	 */
	public void setIlpSolverThreadCount(final int numberOfThreads) {
		numberOfIlpSolverThreads = numberOfThreads;
	}

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
	public MdvneIflyeOutput execute(final ResourceSet model, final String gipsXmi, final String ibexXmi,
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
			api = new MdvneGipsAPI();
			api.init(URI.createFileURI(gipsXmi), model, URI.createFileURI(ibexXmi));
			init = true;
			if (numberOfIlpSolverThreads > 0) {
				this.api.setIlpSolverThreads(numberOfIlpSolverThreads);
			}
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
	public MdvneIflyeOutput execute(final ResourceSet model) {
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
			api = new MdvneGipsAPI();
			api.init(model);
			init = true;
			if (numberOfIlpSolverThreads > 0) {
				this.api.setIlpSolverThreads(numberOfIlpSolverThreads);
			}
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
	private MdvneIflyeOutput buildAndSolve() {
		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		// Build the ILP problem (including updates)
		api.buildProblemTimed(true);

		// Solve the ILP problem
		final SolverOutput output = api.solveProblemTimed();

		// TODO: Remove system outputs
		System.out.println("=> GIPS iflye adapter: Solver status: " + output.status());
		System.out.println("=> GIPS iflye adapter: Objective value: " + output.objectiveValue());

		final Map<String, IMeasurement> measurements = new LinkedHashMap<>(obs.getMeasurements("Eval"));
		obs.getMeasurements("Eval").clear();
		System.out.println("PM: " + measurements.get("PM").maxDurationSeconds());
		System.out.println("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds());
		System.out.println("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds());
		System.out.println("BUILD: " + measurements.get("BUILD").maxDurationSeconds());
		System.out.println("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds());

		final Map<String, String> matches = extractMatchedNodes(api.getMappers().values());

		// Apply all valid mappings
//		api.getSrv2srv().applyNonZeroMappings();
//		api.getSw2node().applyNonZeroMappings();
//		api.getL2p().applyNonZeroMappings();
//		api.getL2s().applyNonZeroMappings();
//		api.getNet2net().applyNonZeroMappings();
		api.applyAllNonZeroMappings();

		return new MdvneIflyeOutput(output, matches, measurements);
	}

	protected Map<String, String> extractMatchedNodes(final Collection<GipsMapper<?>> mappers) {
		final Map<String, String> matches = mappers.stream()
				.flatMap((mapper) -> mapper.getNonZeroVariableMappings().stream()).map((m) -> (GipsGTMapping<?, ?>) m)
				.map(m -> m.getMatch().toIMatch()).map(this::extractMatchedNodes).filter((m) -> m != null)
				.collect(Collectors.toUnmodifiableMap((m) -> m.getKey(), (m) -> m.getValue()));
		return matches;
	}

	protected <T extends IMatch> Map.Entry<String, String> extractMatchedNodes(final T m) {
		switch (m.getPatternName()) {
		case "serverMatchPositive":
			return Map.entry(((model.Element) m.get("virtualNode")).getName(),
					((model.Element) m.get("substrateServer")).getName());
		case "switchNodeMatchPositive":
			return Map.entry(((model.Element) m.get("virtualSwitch")).getName(),
					((model.Element) m.get("substrateNode")).getName());
		case "networkRule":
			return Map.entry(((model.Element) m.get("virtualNetwork")).getName(),
					((model.Element) m.get("substrateNetwork")).getName());
		case "linkPathMatchPositive":
			return Map.entry(((model.Element) m.get("virtualLink")).getName(),
					((model.Element) m.get("substratePath")).getName());
		case "linkServerMatchPositive":
			return Map.entry(((model.Element) m.get("virtualLink")).getName(),
					((model.Element) m.get("substrateServer")).getName());
		default:
			return null;
		}
	}

	/**
	 * Resets the initialized state of the GIPS API.
	 */
	public void resetInit() {
		init = false;
		if (api != null) {
			api.terminate();
		}
		HiPEPathOptions.getInstance().resetNetworkPath();
		HiPEPathOptions.getInstance().resetEngineClassName();
	}

}
