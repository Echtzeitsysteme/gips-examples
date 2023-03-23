package org.emoflon.gips.gipsl.examples.extended.sdr;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.sdr.extended.api.gips.ExtendedGipsAPI;

public class ExampleSdr {

	public static void main(final String[] args) {
		final ExtendedGipsAPI api = new ExtendedGipsAPI();
//		api.init(URI.createFileURI("../org.emoflon.gips.gipsl.examples.sdrmodel/instances/CPU_4_8@B8_C1-00_R1-00_UNI.xmi"));
		api.init(URI.createFileURI(
				"../org.emoflon.gips.gipsl.examples.sdrmodel/instances/CPU_4_8@m2-00_kF1_fC10-00_N0-50_SimpleChain.xmi"));

		api.buildILPProblem(true);
		final ILPSolverOutput output = api.solveILPProblem();
		System.out.println("Solver status: " + output.status());
		System.out.println("Objective value: " + output.objectiveValue());

		api.getB2t().applyNonZeroMappings();
		api.getF2i().applyNonZeroMappings();
		api.getF2t().applyNonZeroMappings();
		api.getUsedThread().applyNonZeroMappings();

		try {
			api.saveResult("./CPU_4_8@m2-00_kF1_fC10-00_N0-50_SimpleChain_solved.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println("GIPSL run finished.");
		System.exit(0);
	}

}
