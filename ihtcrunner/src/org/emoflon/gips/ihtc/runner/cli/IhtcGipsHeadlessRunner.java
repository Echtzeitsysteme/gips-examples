package org.emoflon.gips.ihtc.runner.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.emoflon.gips.ihtc.runner.strategy.IhtcGipsStrategyRunner;
import org.emoflon.gips.ihtc.runner.utils.StringUtils;

import ihtcmetamodel.utils.FileUtils;

/**
 * Runnable headless CLI runner for the IHTC 2024 GIPS-based solution.
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class IhtcGipsHeadlessRunner extends IhtcGipsStrategyRunner {

	/**
	 * JSON input file path to load an instance from.
	 */
	private static String jsonInputPath = null;

	/**
	 * JSON output file path to save the solution to.
	 */
	private static String jsonOutputPath = null;

	/**
	 * XMI input model file path to save the transformed input model to.
	 */
	private static String xmiInputModelPath = "./model_in.xmi";

	/**
	 * If true, the necessary XMI input model file will be deleted after the
	 * execution.
	 */
	private static boolean setXmiInputModelPathDelete = true;

	/**
	 * XMI output model file path to save the transformed output model to.
	 */
	private static String xmiOutputModelPath = "./model_out.xmi";

	/**
	 * If true, the necessary XMI output model file will be deleted after the
	 * execution.
	 */
	private static boolean setXmiOutputModelPathDelete = true;

	/**
	 * Boolean flag to enable the debug output.
	 */
	private static boolean debugOutputEnabled = false;

	/**
	 * Boolean flag to enable output JSON file splitting.
	 */
	private static boolean splitOutputJsonEnabled = false;

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsHeadlessRunner() {
	}

	/**
	 * Main method to start the headless runner. String array of arguments will be
	 * parsed.
	 * 
	 * @param args See {@link #parseArgs(String[])}.
	 */
	public static void main(final String[] args) {
		parseArgs(args);
		new IhtcGipsHeadlessRunner().execute();
	}

	/**
	 * Runs the execution of the configured scenario. This method relies on the
	 * previous parsing of arguments.
	 */
	private void execute() {
		// Create a new IHTC GIPS strategy runner
		final IhtcGipsStrategyRunner strategyRunner = new IhtcGipsStrategyRunner();

		// Set values configured by the given arguments
		strategyRunner.setVerbose(debugOutputEnabled);
		strategyRunner.setSplitOutputJsonEnabled(splitOutputJsonEnabled);
		strategyRunner.setJsonInputPath(jsonInputPath);
		strategyRunner.setJsonOutputPath(jsonOutputPath);
		strategyRunner.setXmiInputModelPath(xmiInputModelPath);
		strategyRunner.setXmiOutputModelPath(xmiOutputModelPath);

		// Execute the runner
		strategyRunner.run();

		// Delete XMI files if configured
		if (setXmiInputModelPathDelete) {
			FileUtils.deleteFile(xmiInputModelPath);
		}
		if (setXmiOutputModelPathDelete) {
			FileUtils.deleteFile(xmiOutputModelPath);
		}
	}

	/**
	 * Parses the given arguments to configure the runner.
	 * <ol>
	 * <li>"i": input JSON file to load (required)</li>
	 * <li>"o": output JSON file to store (required)</li>
	 * <li>"q": model input XMI file to store (optional)</li>
	 * <li>"r": model output XMI file to store (optional)</li>
	 * <li>"d": debug output flag (optional)</li>
	 * <li>"s": split output JSON file flag (optional)</li>
	 * </ol>
	 * 
	 * @param args Arguments to parse.
	 */
	private static void parseArgs(final String[] args) {
		final Options options = new Options();

		// JSON input file to load
		final Option jsonInputFile = new Option("i", "inputjson", true, "input JSON file to load");
		jsonInputFile.setRequired(true);
		options.addOption(jsonInputFile);

		// JSON output file to write
		final Option jsonOutputFile = new Option("o", "outputjson", true, "output JSON file to store");
		jsonOutputFile.setRequired(false);
		options.addOption(jsonOutputFile);

		// XMI model file path to save the transformed input model to
		final Option xmiModelInputFile = new Option("q", "modelinputxmi", true, "model input XMI file to store");
		xmiModelInputFile.setRequired(false);
		options.addOption(xmiModelInputFile);

		// XMI model file path to save the output model to
		final Option xmiModelOutputFile = new Option("r", "modeloutputxmi", true, "model output XMI file to store");
		xmiModelOutputFile.setRequired(false);
		options.addOption(xmiModelOutputFile);

		// Debug output enabled flag
		final Option debugOutputEnabled = new Option("d", "debug", false, "debug output flag");
		debugOutputEnabled.setRequired(false);
		options.addOption(debugOutputEnabled);

		// Split output JSON file flag
		final Option splitOutputEnabled = new Option("s", "split", false, "split output JSON file flag");
		splitOutputEnabled.setRequired(false);
		options.addOption(splitOutputEnabled);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException ex) {
			System.err.println(ex.getMessage());
			formatter.printHelp("CLI parameters", options);
			System.exit(1);
		}

		// Get and save values
		jsonInputPath = cmd.getOptionValue("inputjson");
		if (cmd.hasOption("outputjson")) {
			jsonOutputPath = cmd.getOptionValue("outputjson");
		} else {
			// If no value is provided, set the default output path to the same location
			// as the input path but precede the JSON file name with `sol_` according to
			// the competition description.
			jsonOutputPath = StringUtils.replaceLast(jsonInputPath, "/", "/sol_");
		}
		if (cmd.hasOption("modelinputxmi")) {
			xmiInputModelPath = cmd.getOptionValue("modelinputxmi");
			setXmiInputModelPathDelete = false;
		}
		if (cmd.hasOption("modeloutputxmi")) {
			xmiOutputModelPath = cmd.getOptionValue("modeloutputxmi");
			setXmiOutputModelPathDelete = false;
		}
		IhtcGipsHeadlessRunner.debugOutputEnabled = cmd.hasOption("debug");
		IhtcGipsHeadlessRunner.splitOutputJsonEnabled = cmd.hasOption("split");
	}

}
