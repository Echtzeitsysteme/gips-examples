package teachingassistant.uni.batch.runner;

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

import teachingassistant.uni.metamodel.export.FileUtils;

/**
 * Runnable headless CLI runner for the teaching assistant assignment problem.
 * 
 * Example arguments to execute this runner:
 * <ul>
 * <li>--inputjson
 * ./teachingassistant.metamodel/resources/kcl_semester_dataset.json</li>
 * <li>--outputjson ./teachingassistant.metamodel/resources/solution.json</li>
 * <li>--inputxmi ./teachingassistant.metamodel/instances/input.xmi</li>
 * <li>--outputxmi ./teachingassistant.metamodel/instances/output.xmi</li>
 * <li>--verbose</li>
 * <li>--callback
 * ./teachingassistant.uni.batch/resources/gurobi-callback.json</li>
 * <li>--parameter
 * ./teachingassistant.uni.batch/resources/gurobi-parameter.json</li>
 * </ul>
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class TaBatchCliRunner {

	/**
	 * Logger for system outputs.
	 */
	protected final static Logger logger = Logger.getLogger(TaBatchCliRunner.class.getName());

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
		new TaBatchCliRunner().execute(config);
	}

	/**
	 * Runs the execution of the configured scenario. This method relies on the
	 * previous parsing of arguments.
	 */
	private void execute(final CliConfig config) {
		Objects.requireNonNull(config);

		logger.info("Using CLI config: " + config.toString());
		final TaBatchRunner runner = new TaBatchRunner();

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
		if (config.parameterPath != null) {
			runner.setParameterPath(config.parameterPath);
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
	 * <li>"c": callback configuration path for Gurobi (optional)</li>
	 * <li>"d": parameter path for Gurobi (optional)</li>
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

		// Gurobi callback path
		final Option callbackPath = new Option("c", "callback", true, "callback configuration path for Gurobi");
		callbackPath.setRequired(false);
		options.addOption(callbackPath);

		// Gurobi parameter path
		final Option parameterPath = new Option("d", "parameter", true, "parameter path for Gurobi");
		parameterPath.setRequired(false);
		options.addOption(parameterPath);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException ex) {
			formatter.printHelp("CLI parameters", options);
			System.exit(1);
		}

		// Get and return values
		return new CliConfig( //
				cmd.getOptionValue("inputjson"), //
				cmd.hasOption("outputjson") ? cmd.getOptionValue("outputjson") : null, //
				cmd.hasOption("inputxmi") ? cmd.getOptionValue("inputxmi") : null, //
				cmd.hasOption("outputxmi") ? cmd.getOptionValue("outputxmi") : null, //
				cmd.hasOption("verbose"), //
				cmd.hasOption("callback") ? cmd.getOptionValue("callback") : null, //
				cmd.hasOption("parameter") ? cmd.getOptionValue("parameter") : null //
		);
	}

	/**
	 * Record to hold the parsed CLI configuration parameters.
	 */
	private record CliConfig(String inputJsonPath, String outputJsonPath, String inputXmiPath, String outputXmiPath,
			boolean verbose, String callbackPath, String parameterPath) {
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
