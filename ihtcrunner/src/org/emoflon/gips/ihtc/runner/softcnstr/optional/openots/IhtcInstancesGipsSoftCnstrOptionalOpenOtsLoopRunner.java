package org.emoflon.gips.ihtc.runner.softcnstr.optional.openots;

import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsLoopRunner;

/**
 * GIPS-based IHTC 2024 loop runner for the competition instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstancesGipsSoftCnstrOptionalOpenOtsLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcInstancesGipsSoftCnstrOptionalOpenOtsLoopRunner() {
		super();
		this.datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcInstancesGipsSoftCnstrOptionalOpenOtsLoopRunner runner = new IhtcInstancesGipsSoftCnstrOptionalOpenOtsLoopRunner();
		runner.setDatasetFolder(runner.competitionInstancesPath);
		runner.setUpInstanceScenarioNames();
		runner.executeScenarios();
	}

	@Override
	protected void run() {
		final IhtcGipsSoftCnstrOptionalOpenOtsRunner gipsRunner = new IhtcGipsSoftCnstrOptionalOpenOtsRunner();
		overwritePaths(gipsRunner, this);
		gipsRunner.run();
	}

}
