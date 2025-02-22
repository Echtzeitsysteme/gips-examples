package ihtcmetamodel.analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * This runner can be used to load a given JSON model and analyze ist.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstanceAnalyzerRunner {

	/**
	 * List of scenario names to be executed.
	 */
	private List<String> scenarioNames = new ArrayList<String>();

	/**
	 * Main method to start the runner.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		new IhtcInstanceAnalyzerRunner().run();
	}

	/**
	 * Loads all IHTC instances one after another and prints the analyze statistics
	 * to the console.
	 */
	private void run() {
		final String instancesFolder = "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";
		scenarioNames.forEach(sn -> {
			System.out.println("=> " + sn);
			final IhtcInstanceAnalyzer analyzer = new IhtcInstanceAnalyzer(instancesFolder + sn);
			analyzer.analyze();
		});

	}

	/**
	 * Sets the scenario names up.
	 */
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

	/**
	 * Adds a new scenario name to the list.
	 * 
	 * @param name New scenario name to add.
	 */
	protected void addScenarioName(final String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Given scenario name was null or empty.");
		}

		this.scenarioNames.add(name);
	}

}
