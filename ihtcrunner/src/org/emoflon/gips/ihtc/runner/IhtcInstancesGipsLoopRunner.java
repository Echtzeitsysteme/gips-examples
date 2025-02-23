package org.emoflon.gips.ihtc.runner;

/**
 * GIPS-based IHTC 2024 loop runner for the competition instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstancesGipsLoopRunner extends AbstractIhtcGipsLoopRunner {

	/**
	 * No public instances of this class allowed.
	 */
	private IhtcInstancesGipsLoopRunner() {
		super();
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcInstancesGipsLoopRunner runner = new IhtcInstancesGipsLoopRunner();
		runner.setDatasetFolder("/../ihtcmetamodel/resources/ihtc2024_competition_instances/");
		runner.setUpScenarioNames();
		runner.printLogSeparator();
		runner.getScenarioNames().forEach(name -> {
			System.out.println("=> Running scenario : " + name);
			runner.setCurrentScenarioName(name);
			try {
				runner.run();
			} catch (final InternalError err) {
				System.err.println("=> No solution found.");
			}
			runner.printLogSeparator();
		});
	}

	/**
	 * Sets the scenario names up.
	 */
	@Override
	protected void setUpScenarioNames() {
		for (int i = 1; i <= 30; i++) {
			String name = "i";
			if (i < 10) {
				name = name.concat("0");
			}
			name = name.concat(String.valueOf(i));
			name = name.concat(".json");
			addScenarioName(name);
		}
	}

}
