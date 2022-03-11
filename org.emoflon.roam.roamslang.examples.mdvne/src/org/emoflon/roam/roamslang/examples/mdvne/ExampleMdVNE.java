package org.emoflon.roam.roamslang.examples.mdvne;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.roam.core.ilp.ILPSolverOutput;
import org.emoflon.roam.roamslang.examples.mdvne.api.roam.MdvneRoamAPI;

public class ExampleMdVNE {

	public static void main(final String[] args) {
		// Create new MdVNE Roam API and load a model
		final MdvneRoamAPI api = new MdvneRoamAPI();
		api.init(URI.createFileURI("model.xmi"));
		
		// Build the ILP problem (including updates)
		api.buildILPProblem(true);
		final ILPSolverOutput output = api.solveILPProblem();
		System.out.println("Solver status: " + output.status());
		System.out.println("Objective value: " + output.objectiveValue());
		
		api.getSrv2srv().applyNonZeroMappings();
		api.getSw2node().applyNonZeroMappings();
		api.getL2p().applyNonZeroMappings();
		api.getL2s().applyNonZeroMappings();
		
		try {
			api.saveResult("model-out.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Dat End.");
		System.exit(0);
	}

}
