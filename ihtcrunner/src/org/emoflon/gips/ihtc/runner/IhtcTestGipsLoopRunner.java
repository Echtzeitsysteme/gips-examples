package org.emoflon.gips.ihtc.runner;

/**
 * GIPS-based IHTC 2024 loop runner for the test instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcTestGipsLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcTestGipsLoopRunner() {
		super();
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcTestGipsLoopRunner runner = new IhtcTestGipsLoopRunner();
		runner.setDatasetFolder(runner.testInstancesPath);
		runner.setUpInstanceScenarioNames();
		runner.executeScenarios();
	}

}
