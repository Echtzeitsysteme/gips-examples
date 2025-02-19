package ihtcgipssolution.runner;

/**
 * GIPS-based IHTC 2024 loop runner for the test instances.
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
		runner.getScenarioNames().forEach(name -> {
			System.out.println("=> Running scenario : " + name);
			runner.setCurrentScenarioName(name);
			runner.run();
		});
	}

	/**
	 * Sets the scenario names up.
	 */
	protected void setUpScenarioNames() {
		for (int i = 1; i <= 1; i++) {
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
