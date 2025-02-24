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
		this.datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_test_dataset/";
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

	@Override
	protected void run() {
		final IhtcGipsStrategyRunner gipsRunner = new IhtcGipsStrategyRunner();
		overwritePaths(gipsRunner, this);
		gipsRunner.setupDefaultPaths();
		gipsRunner.run();
	}

}
