package org.emoflon.gips.gipsl.examples.mdvne.seq;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.gt.GipsGTMapping;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.gipsl.examples.mdvne.MdvneGipsIflyeAdapterUtil;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.gips.SeqGipsAPI;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Link2PathRuleMatch;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Link2ServerRuleMatch;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Network2NetworkRuleMatch;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Server2ServerRuleMatch;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Switch2NodeRuleMatch;

import hipe.engine.config.HiPEPathOptions;

/**
 * Implementation adapter for GIPS and iflye. This is used to run the GIPS-based
 * MdVNE adapter from the iflye framework. Basically, this is an external entry
 * point to trigger the GIPS-based MdVNE implementation from other frameworks.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class MdvneSeqGipsIflyeAdapter {

	/**
	 * MdVNE GIPS API object.
	 */
	static SeqGipsAPI api;

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
				.setEngineClassName("org.emoflon.gips.gipsl.examples.mdvne.seq.hipe.engine.HiPEEngine");

		if (!init) {
			api = new SeqGipsAPI();
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

		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		// Initialize the API, if necessary
		if (!init) {
			api = new SeqGipsAPI();
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
		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		// Build the ILP problem (including updates)
		api.buildProblemTimed(true);

		// Solve the ILP problem
		final SolverOutput output = api.solveProblemTimed();

		// TODO: Remove system outputs
		System.out.println("=> GIPS iflye adapter: Solver status: " + output.status());
		System.out.println("=> GIPS iflye adapter: Objective value: " + output.objectiveValue());

		final Map<String, IMeasurement> measurements = obs.getMeasurements("Eval");
		System.out.println("PM: " + measurements.get("PM").maxDurationSeconds());
		System.out.println("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds());
		System.out.println("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds());
		System.out.println("BUILD: " + measurements.get("BUILD").maxDurationSeconds());
		System.out.println("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds());

		@SuppressWarnings("rawtypes")
		final var allSelectedMappings = new ArrayList<GipsGTMapping>();

		// Server 2 Server
		final var srv2srvMappings = api.getSrv2srv().getNonZeroVariableMappings();
		final var srv2srvRule = api.getSrv2srv().getGTRule();
		allSelectedMappings.addAll(srv2srvMappings);

		// Switch 2 Node
		final var sw2nodeMappings = api.getSw2node().getNonZeroVariableMappings();
		final var sw2nodeRule = api.getSw2node().getGTRule();
		allSelectedMappings.addAll(sw2nodeMappings);

		// Link 2 Server
		final var l2sMappings = api.getL2s().getNonZeroVariableMappings();
		final var l2sRule = api.getL2s().getGTRule();
		allSelectedMappings.addAll(l2sMappings);

		// Link 2 Path
		final var l2pMappings = api.getL2p().getNonZeroVariableMappings();
		final var l2pRule = api.getL2p().getGTRule();
		allSelectedMappings.addAll(l2pMappings);

		// Network 2 Network
		final var net2netMappings = api.getNet2net().getNonZeroVariableMappings();
		final var net2netRule = api.getNet2net().getGTRule();
		allSelectedMappings.addAll(net2netMappings);

		// Sort all mappings according to their index variable value
		allSelectedMappings.sort((o1, o2) -> {
			return ((IntegerVariable) o1.getFreeVariables().get("index")).getValue()
					- ((IntegerVariable) o2.getFreeVariables().get("index")).getValue();
		});

		// Apply all selected mappings in their respective index order
		allSelectedMappings.forEach(m -> {
			System.out.println(m.getName() + ": " + ((IntegerVariable) m.getFreeVariables().get("index")).getValue());
			if (m.getMatch() instanceof Server2ServerRuleMatch) {
				srv2srvRule.apply((Server2ServerRuleMatch) m.getMatch(), true);
			} else if (m.getMatch() instanceof Switch2NodeRuleMatch) {
				sw2nodeRule.apply((Switch2NodeRuleMatch) m.getMatch(), true);
			} else if (m.getMatch() instanceof Link2ServerRuleMatch) {
				l2sRule.apply((Link2ServerRuleMatch) m.getMatch(), true);
			} else if (m.getMatch() instanceof Link2PathRuleMatch) {
				l2pRule.apply((Link2PathRuleMatch) m.getMatch(), true);
			} else if (m.getMatch() instanceof Network2NetworkRuleMatch) {
				net2netRule.apply((Network2NetworkRuleMatch) m.getMatch(), true);
			} else {
				throw new InternalError();
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
