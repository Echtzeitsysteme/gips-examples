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

}
