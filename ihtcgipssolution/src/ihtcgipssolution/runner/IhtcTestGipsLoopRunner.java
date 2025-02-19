package ihtcgipssolution.runner;

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
	protected void setUpScenarioNames() {
		for (int i = 1; i <= 10; i++) {
			String name = "test";
			if (i < 10) {
				name = name.concat("0");
			}
			name = name.concat(String.valueOf(i));
			name = name.concat(".json");
			addScenarioName(name);
		}
	}

}
