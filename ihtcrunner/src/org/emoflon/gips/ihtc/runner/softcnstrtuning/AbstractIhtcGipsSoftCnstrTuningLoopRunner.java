package org.emoflon.gips.ihtc.runner.softcnstrtuning;

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
public abstract class AbstractIhtcGipsSoftCnstrTuningLoopRunner extends IhtcGipsSoftCnstrTuningRunner {

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

	/**
	 * Sets the scenario names of all scenarios up that could be solved by the
	 * hard-constraints-only solution.
	 */
	protected void setUpHardOnlySuccessfulScenarios() {
		final String[] hardOnlySuccessfulRuns = new String[] { //
				"i01.json", "i02.json", "i03.json", "i04.json", "i05.json", "i06.json", "i07.json", "i08.json", //
				"i09.json", "i10.json", "i11.json", "i12.json", "i13.json", "i14.json", "i15.json", "i16.json", //
				"i17.json", "i18.json", "i19.json", "i20.json", "i21.json", "i25.json", "i28.json", "i29.json" };

		for (int i = 0; i < hardOnlySuccessfulRuns.length; i++) {
			addScenarioName(hardOnlySuccessfulRuns[i]);
		}
	}

}
