package org.emoflon.gips.ihtc.runner;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract runner can be used to load multiple IHTC 2024 JSON-based
 * problem files, convert them to XMI files, solve the problems using our
 * GIPS(L) implementation, and writing the solutions to JSON files as required
 * by the contest.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public abstract class AbstractIhtcGipsLoopRunner extends IhtcGipsRunner {

	/**
	 * Folder path of the competition instances.
	 */
	protected final String competitionInstancesPath = "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";

	/**
	 * Folder path of the test instances.
	 */
	protected final String testInstancesPath = "/../ihtcmetamodel/resources/ihtc2024_test_dataset/";

	/**
	 * List of scenario names to be executed.
	 */
	private List<String> scenarioNames = new ArrayList<String>();

	/**
	 * Returns the list of all scenario names.
	 * 
	 * @return List of all scenario names.
	 */
	protected List<String> getScenarioNames() {
		return scenarioNames;
	}

	/**
	 * Adds a new scenario name to the list.
	 * 
	 * @param name New scenario name to add.
	 */
	protected void addScenarioName(final String name) {
		this.scenarioNames.add(name);
	}

	/**
	 * Sets a new scenario name for the runner.
	 * 
	 * @param name New scenario name to be executed by the runner.
	 */
	protected void setCurrentScenarioName(final String name) {
		this.scenarioFileName = name;
	}

	/**
	 * Sets the data set folder path to the given value.
	 * 
	 * @param path New data set folder path to set.
	 */
	protected void setDatasetFolder(final String path) {
		this.datasetFolder = projectFolder + path;
	}

	/**
	 * Prints a log separator line on the console.
	 */
	protected void printLogSeparator() {
		System.out.println("--------------------------------------------------------------------------------");
	}

	/**
	 * Sets the test scenario names up.
	 */
	protected void setUpTestScenarioNames() {
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

	/**
	 * Sets the instance scenario names up.
	 */
	protected void setUpInstanceScenarioNames() {
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
	 * Executes the configured scenarios one by one.
	 */
	protected void executeScenarios() {
		printLogSeparator();
		getScenarioNames().forEach(name -> {
			System.out.println("=> Running scenario : " + name);
			setCurrentScenarioName(name);
			try {
				run();
			} catch (final InternalError err) {
				System.err.println("=> No solution found.");
			}
			printLogSeparator();
		});
	}

}
