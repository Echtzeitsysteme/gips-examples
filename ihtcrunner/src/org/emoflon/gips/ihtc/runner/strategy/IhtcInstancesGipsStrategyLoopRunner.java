package org.emoflon.gips.ihtc.runner.strategy;

import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsLoopRunner;

/**
 * GIPS-based IHTC 2024 loop runner for the competition instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstancesGipsStrategyLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcInstancesGipsStrategyLoopRunner() {
		super();
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcInstancesGipsStrategyLoopRunner runner = new IhtcInstancesGipsStrategyLoopRunner();
		runner.setDatasetFolder(runner.competitionInstancesPath);
		runner.setUpInstanceScenarioNames();
		runner.executeScenarios();
	}

}
