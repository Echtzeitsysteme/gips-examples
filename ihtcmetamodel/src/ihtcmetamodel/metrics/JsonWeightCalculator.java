package ihtcmetamodel.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ihtcmetamodel.Hospital;
import ihtcmetamodel.Weight;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.utils.StatisticsResult;

/**
 * This calculator class can be used to read all IHTC 2024 scenario files and
 * calculate statistical values for the weights across all tasks.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class JsonWeightCalculator {

	/**
	 * List if all JSON input files to be loaded.
	 */
	private List<String> jsonInputFileNames = new ArrayList<String>();

	/**
	 * List of all statistical results.
	 */
	private List<StatisticsResult> results = new ArrayList<StatisticsResult>();

	/**
	 * Main method to run the calculator. All arguments will be ignored.
	 * 
	 * @param args All arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new JsonWeightCalculator().run();
	}

	/**
	 * Constructor for setting up all scenario file names.
	 */
	protected JsonWeightCalculator() {
		setUpScenarioNames();
	}

	/**
	 * Sets the scenario names up.
	 */
	private void setUpScenarioNames() {
		for (int i = 1; i <= 30; i++) {
			String name = "i";
			if (i < 10) {
				name = name.concat("0");
			}
			name = name.concat(String.valueOf(i));
			name = name.concat(".json");
			this.jsonInputFileNames.add(name);
		}
	}

	/**
	 * Actual run method to run the calculator.
	 */
	private void run() {
		final Set<Hospital> hospitals = new HashSet<Hospital>();
		this.jsonInputFileNames.forEach(scenario -> {
			final JsonToModelLoader loader = new JsonToModelLoader();
			loader.jsonToModel("./resources/ihtc2024_competition_instances/" + scenario);
			hospitals.add(loader.getModel());
		});

		final Set<Weight> weights = new HashSet<Weight>();
		hospitals.forEach(h -> {
			weights.add(h.getWeight());
		});

		calculateResults(weights);
		printStatistics(this.results);
	}

	//
	// Utility methods.
	//

	/**
	 * Calculates the statistical results for all specific weights of all given
	 * weight objects.
	 * 
	 * @param weights Weight objects to gather data from.
	 */
	private void calculateResults(final Set<Weight> weights) {
		// room_mixed_age
		final Set<Integer> values = new HashSet<Integer>();
		for (final Weight w : weights) {
			values.add(w.getRoomMixedAge());
		}
		this.results.add(new StatisticsResult("room_mixed_age", findMax(values), findMin(values), calculateMean(values),
				calculateStdDev(values)));

		// room_nurse_skill
		values.clear();
		for (final Weight w : weights) {
			values.add(w.getRoomNurseSkill());
		}
		this.results.add(new StatisticsResult("room_nurse_skill", findMax(values), findMin(values),
				calculateMean(values), calculateStdDev(values)));

		// continuity_of_care
		values.clear();
		for (final Weight w : weights) {
			values.add(w.getContinuityOfCare());
		}
		this.results.add(new StatisticsResult("continuity_of_care", findMax(values), findMin(values),
				calculateMean(values), calculateStdDev(values)));

		// nurse_eccessive_workload
		values.clear();
		for (final Weight w : weights) {
			values.add(w.getNurseExcessiveWorkload());
		}
		this.results.add(new StatisticsResult("nurse_eccessive_workload", findMax(values), findMin(values),
				calculateMean(values), calculateStdDev(values)));

		// open_operating_theater
		values.clear();
		for (final Weight w : weights) {
			values.add(w.getOpenOperatingTheater());
		}
		this.results.add(new StatisticsResult("open_operating_theater", findMax(values), findMin(values),
				calculateMean(values), calculateStdDev(values)));

		// surgeon_transfer
		values.clear();
		for (final Weight w : weights) {
			values.add(w.getSurgeonTransfer());
		}
		this.results.add(new StatisticsResult("surgeon_transfer", findMax(values), findMin(values),
				calculateMean(values), calculateStdDev(values)));

		// patient_delay
		values.clear();
		for (final Weight w : weights) {
			values.add(w.getPatientDelay());
		}
		this.results.add(new StatisticsResult("patient_delay", findMax(values), findMin(values), calculateMean(values),
				calculateStdDev(values)));

		// unscheduled_optional
		values.clear();
		for (final Weight w : weights) {
			values.add(w.getUnscheduledOptional());
		}
		this.results.add(new StatisticsResult("unscheduled_optional", findMax(values), findMin(values),
				calculateMean(values), calculateStdDev(values)));
	}

	/**
	 * Finds the minimum value of a given set of integers.
	 * 
	 * @param values Set of integers.
	 * @return Minimum value within the given set of integers.
	 */
	private int findMin(final Set<Integer> values) {
		if (values == null || values.size() == 0) {
			throw new IllegalArgumentException("Given values were null or empty.");
		}

		int min = Integer.MAX_VALUE;
		for (final int v : values) {
			if (min > v) {
				min = v;
			}
		}

		return min;
	}

	/**
	 * Finds the maximum value of a given set of integers.
	 * 
	 * @param values Set of integers.
	 * @return Maximum value within the given set of integers.
	 */
	private int findMax(final Set<Integer> values) {
		if (values == null || values.size() == 0) {
			throw new IllegalArgumentException("Given values were null or empty.");
		}

		int max = Integer.MIN_VALUE;
		for (final int v : values) {
			if (max < v) {
				max = v;
			}
		}

		return max;
	}

	/**
	 * Calculates the mean of a given set of integers.
	 * 
	 * @param values Set of integers.
	 * @return Mean value of the given set of integers.
	 */
	private double calculateMean(final Set<Integer> values) {
		if (values == null || values.size() == 0) {
			throw new IllegalArgumentException("Given values were null or empty.");
		}

		int sum = 0;
		for (final int v : values) {
			sum += v;
		}

		return 1.0 * sum / values.size();
	}

	/**
	 * Calculates the stddev of a given set of integers.
	 * 
	 * @param values Set of integers.
	 * @return Stddev of the given set of integers.
	 */
	private double calculateStdDev(final Set<Integer> values) {
		if (values == null || values.size() == 0) {
			throw new IllegalArgumentException("Given values were null or empty.");
		}

		final double mean = calculateMean(values);
		double innerSum = 0;
		for (final int v : values) {
			innerSum += Math.pow(v - mean, 2);
		}

		return Math.sqrt(innerSum / values.size());
	}

	/**
	 * Prints the statistics as a table for all given result objects.
	 * 
	 * @param results Result objects to print.
	 */
	private void printStatistics(final List<StatisticsResult> results) {
		System.out.println("=> Statistics results");

		String leftAlignFormat = "| %-25s | %4d | %4d | ";

		System.out.format("+---------------------------+------+------+--------+--------+%n");
		System.out.format("| Weight                    | min  | max  | mean   | stddev |%n");
		System.out.format("+---------------------------+------+------+--------+--------+%n");
		results.forEach(r -> {
			System.out.format(leftAlignFormat, r.name(), r.min(), r.max());
			System.out.println(fill(round(r.mean())) + " | " + fill(round(r.stddev())) + " |");
		});
		System.out.format("+---------------------------+------+------+--------+--------+%n");
	}

	/**
	 * Rounds the given double value to two decimal points.
	 * 
	 * @param value Double value to be rounded.
	 * @return Rounded value.
	 */
	private double round(final double value) {
		return Math.round(100 * value) / 100.0;
	}

	/**
	 * Creates a string representation of the given double value that has correct
	 * leading spaces for the table printing.
	 * 
	 * @param value Double value to be converted.
	 * @return String representation with correct leading spaces.
	 */
	private String fill(final double value) {
		final String format = "%,3.2f";
		String formattedString = String.format(format, value);
		if (formattedString.length() < 6) {
			for (int i = 0; i <= 6 - formattedString.length(); i++) {
				formattedString = " ".concat(formattedString);
			}
		}
		return formattedString;
	}

}
