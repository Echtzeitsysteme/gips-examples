package org.emoflon.gips.ihtc.runner.softcnstr.optional.patients;

import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsLoopRunner;

/**
 * GIPS-based IHTC 2024 loop runner for the competition instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstancesGipsSoftCnstrOptionalPatientsLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcInstancesGipsSoftCnstrOptionalPatientsLoopRunner() {
		super();
		this.datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcInstancesGipsSoftCnstrOptionalPatientsLoopRunner runner = new IhtcInstancesGipsSoftCnstrOptionalPatientsLoopRunner();
		runner.setDatasetFolder(runner.competitionInstancesPath);
		runner.setUpInstanceScenarioNames();
		runner.executeScenarios();
	}

	@Override
	protected void run() {
		final IhtcGipsSoftCnstrOptionalPatientsRunner gipsRunner = new IhtcGipsSoftCnstrOptionalPatientsRunner();
		overwritePaths(gipsRunner, this);
		gipsRunner.run();
	}

}
