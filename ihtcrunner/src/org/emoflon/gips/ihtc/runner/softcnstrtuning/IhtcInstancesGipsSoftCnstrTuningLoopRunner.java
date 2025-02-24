package org.emoflon.gips.ihtc.runner.softcnstrtuning;

import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsLoopRunner;

/**
 * GIPS-based IHTC 2024 loop runner for the competition instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstancesGipsSoftCnstrTuningLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcInstancesGipsSoftCnstrTuningLoopRunner() {
		super();
		this.datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcInstancesGipsSoftCnstrTuningLoopRunner runner = new IhtcInstancesGipsSoftCnstrTuningLoopRunner();
		runner.setDatasetFolder(runner.competitionInstancesPath);
		runner.setUpInstanceScenarioNames();
		runner.executeScenarios();
	}

	@Override
	protected void run() {
		final IhtcGipsSoftCnstrTuningRunner gipsRunner = new IhtcGipsSoftCnstrTuningRunner();
		overwritePaths(gipsRunner, this);
		gipsRunner.run();
	}

}
