package org.emoflon.gips.ihtc.runner.softcnstr.optional.delay;

import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsLoopRunner;

/**
 * GIPS-based IHTC 2024 loop runner for the competition instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstancesGipsSoftCnstrOptionalDelayLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcInstancesGipsSoftCnstrOptionalDelayLoopRunner() {
		super();
		this.datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcInstancesGipsSoftCnstrOptionalDelayLoopRunner runner = new IhtcInstancesGipsSoftCnstrOptionalDelayLoopRunner();
		runner.setDatasetFolder(runner.competitionInstancesPath);
		runner.setUpInstanceScenarioNames();
		runner.executeScenarios();
	}

}
