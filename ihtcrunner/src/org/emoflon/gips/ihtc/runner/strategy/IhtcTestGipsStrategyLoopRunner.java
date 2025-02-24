package org.emoflon.gips.ihtc.runner.strategy;

import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsLoopRunner;

/**
 * GIPS-based IHTC 2024 loop runner for the test instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcTestGipsStrategyLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcTestGipsStrategyLoopRunner() {
		super();
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcTestGipsStrategyLoopRunner runner = new IhtcTestGipsStrategyLoopRunner();
		runner.setDatasetFolder(runner.testInstancesPath);
		runner.setUpTestScenarioNames();
		runner.executeScenarios();
	}

}
