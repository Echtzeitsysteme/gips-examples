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
		this.datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_test_dataset/";
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcTestGipsLoopRunner runner = new IhtcTestGipsLoopRunner();
		runner.setDatasetFolder(runner.testInstancesPath);
		runner.setUpTestScenarioNames();
		runner.executeScenarios();
	}

	@Override
	protected void run() {
		final IhtcGipsRunner gipsRunner = new IhtcGipsRunner();
		overwritePaths(gipsRunner, this);
		gipsRunner.run();
	}

}
