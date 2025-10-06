package org.emoflon.gips.ihtc.virtual.runner.cli;

import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.emoflon.gips.ihtc.virtual.runner.IhtcVirtualGipsRunner;

import ihtcvirtualmetamodel.utils.FileUtils;

/**
 * Runnable headless CLI runner for the IHTC 2024 GIPS-based virtual solution.
 * 
 * Example arguments to execute this runner: --inputjson
 * /home/mkratz/git/gips-examples/ihtcvirtualmetamodel/resources/ihtc2024_test_dataset/test01.json
 * --outputjson
 * /home/mkratz/git/gips-examples/ihtcvirtualmetamodel/resources/runner/sol_test01_gips.json
 * --inputxmi
 * /home/mkratz/git/gips-examples/ihtcvirtualmetamodel/instances/test01.xmi
 * --outputxmi
 * /home/mkratz/git/gips-examples/ihtcvirtualmetamodel/instances/test01_solved.xmi
 * --verbose --randomseed 0 --timelimit 300 --threads 4 --callback
 * /tmp/gurobi-callback.json --parameter /tmp/gurobi-parameter.json
 * --preprocessing nogt
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class IhtcVirtualGipsHeadlessRunner {

	/**
	 * Logger for system outputs.
	 */
	protected final static Logger logger = Logger.getLogger(IhtcVirtualGipsHeadlessRunner.class.getName());

	/**
	 * Main method to start the headless runner. String array of arguments will be
	 * parsed.
	 * 
	 * @param args See {@link #parseArgs(String[])}.
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);
		configureLogging();
		final CliConfig config = parseArgs(args);
		new IhtcVirtualGipsHeadlessRunner().execute(config);
	}

	/**
	 * Runs the execution of the configured scenario. This method relies on the
	 * previous parsing of arguments.
	 */
	private void execute(final CliConfig config) {
		Objects.requireNonNull(config);

		logger.info("Using CLI config: " + config.toString());
		final IhtcVirtualGipsRunner runner = new IhtcVirtualGipsRunner();

		// Set parameters
		if (config.inputJsonPath != null) {
			runner.inputPath = config.inputJsonPath;
		}
		if (config.outputJsonPath != null) {
			runner.outputPath = config.outputJsonPath;
		}
		if (config.inputXmiPath != null) {
			runner.instancePath = config.inputXmiPath;
		}
		if (config.outputXmiPath != null) {
			runner.gipsOutputPath = config.outputXmiPath;
		}
		runner.setVerbose(config.verbose);
		runner.setRandomSeed(config.randomSeed);
		if (config.timeLimit > 0) {
			runner.setTimeLimit(config.timeLimit);
		}
		runner.setThreads(config.threads);
		if (config.callbackPath != null) {
			runner.setCallbackPath(config.callbackPath);
		}
		if (config.parameterPath != null) {
			runner.setParameterPath(config.parameterPath);
		}
		if (config.preprocessing != null) {
			if ("gt".equals(config.preprocessing)) {
				runner.setPreProcessingApproach(false);
			} else if ("nogt".equals(config.preprocessing)) {
				runner.setPreProcessingApproach(true);
			} else {
				throw new IllegalArgumentException(
						"Given pre-processing approach <" + config.preprocessing + "> is invalid.");
			}
		}

		// Execute the runner
		runner.run();

		// Delete XMI/JSON files if configured
		if (config.inputXmiPath == null) {
			FileUtils.deleteFile(runner.instancePath);
		}
		if (config.outputXmiPath == null) {
			FileUtils.deleteFile(runner.gipsOutputPath);
		}
		if (config.outputJsonPath == null) {
			FileUtils.deleteFile(runner.outputPath);
		}

		// Delete `Gurobi_ILP.log` if no `--verbose` configured
		if (!config.verbose) {
			FileUtils.deleteFile("./Gurobi_ILP.log");
		}
	}

	/**
	 * Parses the given arguments to configure the runner.
	 * <ol>
	 * <li>"i": input JSON file to load (required)</li>
	 * <li>"o": output JSON file to store (required)</li>
	 * <li>"q": model input XMI file to store (optional)</li>
	 * <li>"r": model output XMI file to store (optional)</li>
	 * <li>"v": verbose output flag (optional)</li>
	 * <li>"n": random seed for the (M)ILP solver (optional)</li>
	 * <li>"t": time limit for the (M)ILP solver (optional)</li>
	 * <li>"p": number of threads to use for the (M)ILP solver (optional)</li>
	 * <li>"c": callback configuration path for Gurobi (optional)</li>
	 * <li>"d": parameter path for Gurobi (optional)</li>
	 * <li>"k": pre-processing approach (optional)</li>
	 * </ol>
	 * 
	 * @param args Arguments to parse.
	 */
	private static CliConfig parseArgs(final String[] args) {
		Objects.requireNonNull(args);
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
		final Option xmiModelInputFile = new Option("q", "inputxmi", true, "model input XMI file to store");
		xmiModelInputFile.setRequired(false);
		options.addOption(xmiModelInputFile);

		// XMI model file path to save the output model to
		final Option xmiModelOutputFile = new Option("r", "outputxmi", true, "model output XMI file to store");
		xmiModelOutputFile.setRequired(false);
		options.addOption(xmiModelOutputFile);

		// Debug output enabled flag
		final Option debugOutputEnabled = new Option("v", "verbose", false, "verbose output flag");
		debugOutputEnabled.setRequired(false);
		options.addOption(debugOutputEnabled);

		// XMI model file path to save the output model to
		final Option randomSeed = new Option("n", "randomseed", true, "random seed for the (M)ILP solver");
		xmiModelOutputFile.setRequired(false);
		options.addOption(randomSeed);

		// Time limit for the (M)ILP solver
		final Option timeLimit = new Option("t", "timelimit", true, "time limit for the (M)ILP solver");
		timeLimit.setRequired(false);
		options.addOption(timeLimit);

		// Number of threads to use for the (M)ILP solver
		final Option threads = new Option("p", "threads", true, "number of threads to use for the (M)ILP solver");
		threads.setRequired(false);
		options.addOption(threads);

		// Gurobi callback path
		final Option callbackPath = new Option("c", "callback", true, "callback configuration path for Gurobi");
		callbackPath.setRequired(false);
		options.addOption(callbackPath);

		// Gurobi parameter path
		final Option parameterPath = new Option("d", "parameter", true, "parameter path for Gurobi");
		parameterPath.setRequired(false);
		options.addOption(parameterPath);

		// Pre-Processing option
		final Option preprocessing = new Option("k", "preprocessing", true, "pre-processing approach: gt or notgt");
		preprocessing.setRequired(false);
		options.addOption(preprocessing);

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

		// Check pre-conditions
		if (cmd.hasOption("randomseed") && Integer.valueOf(cmd.getOptionValue("randomseed")) < 0) {
			throw new IllegalArgumentException("Given random seed was negative, which is not supported.");
		}

		// Get and return values
		return new CliConfig( //
				cmd.getOptionValue("inputjson"), //
				cmd.hasOption("outputjson") ? cmd.getOptionValue("outputjson") : null, //
				cmd.hasOption("inputxmi") ? cmd.getOptionValue("inputxmi") : null, //
				cmd.hasOption("outputxmi") ? cmd.getOptionValue("outputxmi") : null, //
				cmd.hasOption("verbose"), //
				cmd.hasOption("randomseed") ? Integer.valueOf(cmd.getOptionValue("randomseed")) : 0, //
				cmd.hasOption("timelimit") ? Integer.valueOf(cmd.getOptionValue("timelimit")) : -1, //
				cmd.hasOption("threads") ? Integer.valueOf(cmd.getOptionValue("threads")) : 0, //
				cmd.hasOption("callback") ? cmd.getOptionValue("callback") : null, //
				cmd.hasOption("parameter") ? cmd.getOptionValue("parameter") : null, //
				cmd.hasOption("preprocessing") ? cmd.getOptionValue("preprocessing") : null //
		);
	}

	/**
	 * Record to hold the parsed CLI configuration parameters.
	 */
	private record CliConfig(String inputJsonPath, String outputJsonPath, String inputXmiPath, String outputXmiPath,
			boolean verbose, int randomSeed, int timeLimit, int threads, String callbackPath, String parameterPath,
			String preprocessing) {
	}

	/**
	 * Configures the logging of this class.
	 */
	public static void configureLogging() {
		// Configure logging
		logger.setUseParentHandlers(false);
		final ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {
			@Override
			public String format(final LogRecord record) {
				Objects.requireNonNull(record, "Given log entry was null.");
				return record.getMessage() + System.lineSeparator();
			}
		});
		logger.addHandler(handler);
	}

}
