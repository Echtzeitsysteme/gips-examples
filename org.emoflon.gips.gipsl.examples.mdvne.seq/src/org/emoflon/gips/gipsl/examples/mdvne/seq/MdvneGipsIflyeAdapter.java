package org.emoflon.gips.gipsl.examples.mdvne.seq;

import java.util.ArrayList;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.gt.GTMapping;
import org.emoflon.gips.core.ilp.ILPIntegerVariable;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.gips.SeqGipsAPI;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Link2ServerRuleMatch;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Network2NetworkRuleMatch;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Server2ServerRuleMatch;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.matches.Switch2NodeRuleMatch;

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
	static SeqGipsAPI api;

	/**
	 * If false, the API must be initialized.
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

		// Initialize the API, if necessary
		if (!init) {
			api = new SeqGipsAPI();
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

		@SuppressWarnings("rawtypes")
		final var allSelectedMappings = new ArrayList<GTMapping>();

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
//		final var l2pMappings = api.getL2p().getNonZeroVariableMappings();
//		final var l2pRule = api.getL2p().getGTRule();
//		allSelectedMappings.addAll(l2pMappings);

		// Network 2 Network
		final var net2netMappings = api.getNet2net().getNonZeroVariableMappings();
		final var net2netRule = api.getNet2net().getGTRule();
		allSelectedMappings.addAll(net2netMappings);

		// Sort all mappings according to their index variable value
		allSelectedMappings.sort((o1, o2) -> {
			return ((ILPIntegerVariable) o1.getFreeVariables().get("index")).getValue()
					- ((ILPIntegerVariable) o2.getFreeVariables().get("index")).getValue();
		});

		// Apply all selected mappings in their respective index order
		allSelectedMappings.forEach(m -> {
			System.out
					.println(m.getName() + ": " + ((ILPIntegerVariable) m.getFreeVariables().get("index")).getValue());
			if (m.getMatch() instanceof Server2ServerRuleMatch) {
				srv2srvRule.apply((Server2ServerRuleMatch) m.getMatch(), true);
			} else if (m.getMatch() instanceof Switch2NodeRuleMatch) {
				sw2nodeRule.apply((Switch2NodeRuleMatch) m.getMatch(), true);
			} else if (m.getMatch() instanceof Link2ServerRuleMatch) {
				l2sRule.apply((Link2ServerRuleMatch) m.getMatch(), true);
//			}
//			else if (m.getMatch() instanceof Link2PathRuleMatch) {
//				l2pRule.apply((Link2PathRuleMatch) m.getMatch(), true);
			} else if (m.getMatch() instanceof Network2NetworkRuleMatch) {
				net2netRule.apply((Network2NetworkRuleMatch) m.getMatch(), true);
			} else {
				throw new InternalError();
			}
		});

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

		return output.solutionCount() > 0;
	}

	/**
	 * Resets the initialized state of the GIPS API.
	 */
	public static void resetInit() {
		init = false;
	}

}
