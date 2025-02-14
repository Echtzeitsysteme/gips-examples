package ihtcgipssolution.runner;

import java.util.ArrayList;
import java.util.List;

/**
 * This example runner can be used to load multiple IHTC 2024 JSON-based problem
 * files, convert them to XMI files, solve the problems using our GIPS(L)
 * implementation, and writing the solutions to JSON files as required by the
 * contest.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsLoopRunner extends IhtcGipsRunner {

	/**
	 * List of scenario names to be executed.
	 */
	private List<String> scenarioNames = new ArrayList<String>();

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcGipsLoopRunner runner = new IhtcGipsLoopRunner();
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
	private void setUpScenarioNames() {
		for (int i = 1; i <= 9; i++) {
			String name = "test";
			if (i < 10) {
				name = name.concat("0");
			}
			name = name.concat(String.valueOf(i));
			name = name.concat(".json");
			this.scenarioNames.add(name);
		}
	}

	/**
	 * Returns the list of all scenario names.
	 * 
	 * @return List of all scenario names.
	 */
	private List<String> getScenarioNames() {
		return scenarioNames;
	}

	/**
	 * Sets a new scenario name for the runner.
	 * 
	 * @param name New scenario name to be executed by the runner.
	 */
	private void setCurrentScenarioName(final String name) {
		this.scenarioFileName = name;
	}

}
