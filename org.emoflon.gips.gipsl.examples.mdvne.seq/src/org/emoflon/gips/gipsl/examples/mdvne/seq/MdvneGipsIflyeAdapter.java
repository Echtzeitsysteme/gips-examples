package org.emoflon.gips.gipsl.examples.mdvne.seq;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.mdvne.seq.api.gips.SeqGipsAPI;

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

		final var srv2srvMappings = api.getSrv2srv().getNonZeroVariableMappings();
		final var srv2srvRule = api.getSrv2srv().getGTRule();
		srv2srvMappings.forEach(m -> {
			System.out.println("srv2srv: " + m.getName() + ": " + m.getFreeVariables().get("index").getValue());
//			m.getFreeVariables().get(0);
			srv2srvRule.apply(m.getMatch(), true);
		});

		final var sw2nodeMappings = api.getSw2node().getNonZeroVariableMappings();
		final var sw2nodeRule = api.getSw2node().getGTRule();
		sw2nodeMappings.forEach(m -> {
			System.out.println("sw2node: " + m.getName() + ": " + m.getFreeVariables().get("index").getValue());
			sw2nodeRule.apply(m.getMatch(), true);
		});

		final var l2sMappings = api.getL2s().getNonZeroVariableMappings();
		final var l2sRule = api.getL2s().getGTRule();
		l2sMappings.forEach(m -> {
			System.out.println("l2s: " + m.getName() + ": " + m.getFreeVariables().get("index").getValue());
			l2sRule.apply(m.getMatch(), true);
		});

//		// TODO: Print all variable values
//		api.getSrv2srv().getNonZeroVariableMappings().forEach(c -> {
//			// TODO: Fix print out
//			System.out.println(c.getName() + ": " + c.getFreeVariableNames());
//		});
//
//		// TODO: This must be done in the order of the index variables
//		// Apply all valid mappings
//		api.getSrv2srv().applyNonZeroMappings();
//		api.getSw2node().applyNonZeroMappings();
////		api.getL2p().applyNonZeroMappings();
//		api.getL2s().applyNonZeroMappings();
////		api.getNet2net().applyNonZeroMappings();		

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
