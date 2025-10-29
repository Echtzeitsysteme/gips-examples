package org.emoflon.gips.gipsl.examples.mdvne;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.gipsl.examples.mdvne.api.gips.MdvneGipsAPI;

import gips.examples.dependencies.GipsExamplesLogger;

public class ExampleMdVNEInfeasible extends GipsExamplesLogger {

	public static void main(final String[] args) {
		// Create new MdVNE Gips API and load a model
		final MdvneGipsAPI api = new MdvneGipsAPI();
		api.init(URI.createFileURI("./resources/example-models/model-in-inf.xmi"));
		api.getTracer().enableTracing(true);
		api.getEclipseIntegrationConfig().setSolutionValuesSynchronizationEnabled(true);
		api.getSolverConfig().setEnableIIS(true);

		// Build the ILP problem (including updates)
		api.buildProblem(true);
		final SolverOutput output = api.solveProblem();
		logger.info("Solver status: " + output.status());
		logger.info("Objective value: " + output.objectiveValue());

		api.terminate();

		logger.info("GIPS run finished.");
		System.exit(0);
	}

}
