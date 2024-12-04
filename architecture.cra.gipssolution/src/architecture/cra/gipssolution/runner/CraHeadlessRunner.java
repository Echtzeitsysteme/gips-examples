package architecture.cra.gipssolution.runner;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;

import architecture.cra.gipssolution.api.gips.GipssolutionGipsAPI;
import architecture.cra.gipssolution.utils.CsvUtil;

/**
 * Runnable headless CLI runner for the CRA assignment problem (taken from the
 * TGG3 project).
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class CraHeadlessRunner extends AbstractCraRunner {

	/**
	 * XMI input file path to load a model from.
	 */
	private static String xmiInputPath;

	/**
	 * XMI output file path to save the solution model to.
	 */
	private static String xmiOutputPath;

	/**
	 * XMI pre-processing file path to save the pre-processing model to.
	 */
	private static String xmiPrePath;

	/**
	 * CSV output file path to save the metrics to.
	 */
	private static String csvOutputPath;

	/**
	 * If true, the runner will print the complete solution to the console.
	 */
	private static boolean printSolution = true;

	/**
	 * Run method to start the whole headless runner.
	 */
	private void run() {
		//
		// Check if input file exists
		//

		checkIfFileExists(xmiInputPath);

		//
		// Create empty classes in the model
		//

		final ResourceSet rs = createEmptyClasses(xmiInputPath);

		//
		// Write changed model to file
		//

		writeXmiToFile(xmiPrePath, rs);

		//
		// Initialize GIPS API
		//

		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		final long tickTotal = System.nanoTime();
		final GipssolutionGipsAPI gipsApi = new GipssolutionGipsAPI();
		gipsApi.init(URI.createFileURI(xmiPrePath));

		//
		// Build and solve the ILP problem
		//

		final long tickSolve = System.nanoTime();
		final double objectiveValue = buildAndSolve(gipsApi);
		final long tockSolve = System.nanoTime();

		//
		// Evaluation
		//

		// Print and apply the best found solution
		printAndApplySolution(gipsApi, printSolution);
		final long tockTotal = System.nanoTime();
		System.out.println("---");

		// Count violations
		countViolations(gipsApi);

		System.out.println("---");
		final double totalSolveTime = (tockSolve - tickSolve) * 1.0 / 1_000_000_000;
		final double totalRunTime = (tockTotal - tickTotal) * 1.0 / 1_000_000_000;
		System.out.println("Total solve time: " + totalSolveTime + " seconds");
		System.out.println("Total run time  : " + totalRunTime + " seconds");
		System.out.println("---");

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, xmiOutputPath);

		//
		// Write CSV output file
		//

		if (csvOutputPath != null && !csvOutputPath.isBlank()) {
			CsvUtil.writeCsvLine(csvOutputPath, new String[] { //
					String.valueOf(totalSolveTime), //
					String.valueOf(totalRunTime), //
					String.valueOf(objectiveValue) //
			});
		}

		//
		// Terminate everything
		//

		final Map<String, IMeasurement> measurements = obs.getMeasurements("Eval");
		System.out.println("PM: " + measurements.get("PM").maxDurationSeconds());
		System.out.println("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds());
		System.out.println("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds());
//		System.out.println("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds());

		gipsApi.terminate();
		System.out.println("=> Finished Java headless runner execution.");
		System.exit(0);
	}

	/**
	 * Main method to start the headless runner. String array of arguments will be
	 * parsed.
	 * 
	 * @param args See {@link #parseArgs(String[])}.
	 */
	public static void main(final String[] args) {
		parseArgs(args);
		new CraHeadlessRunner().run();
	}

	/**
	 * Parses the given arguments to configure the runner.
	 * <ol>
	 * <li>#0: XMI input file path (required)</li>
	 * <li>#1: XMI output file path (required)</li>
	 * <li>#2: XMI pre-processing file path (required)</li>
	 * <li>#3: Print the found solution to console (optional)</li>
	 * </ol>
	 * 
	 * @param args Arguments to parse.
	 */
	private static void parseArgs(final String[] args) {
		final Options options = new Options();

		// XMI file to load
		final Option xmiInputFile = new Option("i", "inputxmi", true, "input XMI file to load");
		xmiInputFile.setRequired(true);
		options.addOption(xmiInputFile);

		// XMI file to save
		final Option xmiOutputFile = new Option("o", "outputxmi", true, "output XMI file to save");
		xmiOutputFile.setRequired(true);
		options.addOption(xmiOutputFile);

		// XMI file for the pre-processing
		final Option xmiPreFile = new Option("q", "prexmi", true, "preprocessing XMI file to save");
		xmiPreFile.setRequired(true);
		options.addOption(xmiPreFile);

		// CSV output path
		final Option csvOutputFile = new Option("c", "outputcsv", true, "output CSV file to save");
		csvOutputFile.setRequired(false);
		options.addOption(csvOutputFile);

		// Print solution flag
		final Option printSolutionOption = new Option("p", "printsolution", false, "print solution");
		printSolutionOption.setRequired(false);
		options.addOption(printSolutionOption);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			System.err.println(e.getMessage());
			formatter.printHelp("CLI parameters", options);
			System.exit(1);
		}

		// Get values
		xmiInputPath = cmd.getOptionValue("inputxmi");
		xmiOutputPath = cmd.getOptionValue("outputxmi");
		xmiPrePath = cmd.getOptionValue("prexmi");
		csvOutputPath = cmd.getOptionValue("outputcsv");
		printSolution = cmd.hasOption("printsolution");
	}

}
