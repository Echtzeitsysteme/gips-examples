package org.emoflon.gips.gipsl.examples.mdvne;

import gips.examples.dependencies.GipsExamplesLogger;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.gipsl.examples.mdvne.api.gips.MdvneGipsAPI;

public class ExampleMdVNE extends GipsExamplesLogger {

	public static void main(final String[] args) {
		// Create new MdVNE Gips API and load a model
		final MdvneGipsAPI api = new MdvneGipsAPI();
		api.init(URI.createFileURI("./resources/example-models/model-in.xmi"));
		api.getTracer().enableTracing(true);
		api.getEclipseIntegrationConfig().setSolutionValuesSynchronizationEnabled(true);

		// Build the ILP problem (including updates)
		api.buildProblem(true);
		final SolverOutput output = api.solveProblem();
		logger.info("Solver status: " + output.status());
		logger.info("Objective value: " + output.objectiveValue());

		api.getSrv2srv().applyNonZeroMappings();
		api.getSw2node().applyNonZeroMappings();
		api.getL2p().applyNonZeroMappings();
		api.getL2s().applyNonZeroMappings();
		api.getNet2net().applyNonZeroMappings();

		try {
			api.saveResult("./resources/example-models/model-out.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}

		api.terminate();

		logger.info("GIPS run finished.");
		System.exit(0);
	}

}
