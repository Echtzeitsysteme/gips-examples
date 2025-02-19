package ihtcgipssolution.runner;

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
	 * Sets the scenario names up.
	 */
	protected abstract void setUpScenarioNames();

}
