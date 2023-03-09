package org.emoflon.gips.gipsl.examples.extened.sdr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.sdr.extended.api.gips.ExtendedGipsAPI;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonSdrRunner {

	/**
	 * XMI file that gets generated from the JSON input.
	 */
	public static final String JSON_MODEL_URI = "json-model.xmi";

	/**
	 * XMI file that is the output of the GIPS run.
	 */
	public static final String JSON_MODEL_RESULT_URI = "json-model-result.xmi";

	/**
	 * JSON input file path. (Will be set by the argument passing method).
	 */
	private static String jsonInputPathString = "";

	/**
	 * Method to run the JSON-based SDR runner. Input arguments must contain the
	 * JSON file location (input file).
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		parseArgs(args);
		convertJsonToXmiModel();
		runGips();
		System.out.println("GIPS run finished.");

		// TODO: Convert XMI model to JSON output
		System.exit(0);
	}

	private static void runGips() {
		// Create the API
		final ExtendedGipsAPI api = new ExtendedGipsAPI();
		api.init(URI.createFileURI("JSON_MODEL_URI"));

		api.buildILPProblem(true);
		final ILPSolverOutput output = api.solveILPProblem();
		System.out.println("Solver status: " + output.status());
		System.out.println("Objective value: " + output.objectiveValue());

		api.getB2t().applyNonZeroMappings();
		api.getF2i().applyNonZeroMappings();
		api.getF2t().applyNonZeroMappings();
		api.getUsedThread().applyNonZeroMappings();

		try {
			api.saveResult("JSON_MODEL_RESULT_URI");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void convertJsonToXmiModel() {
		// Load JSON model/configuration file
		String jsonData = null;
		try {
			jsonData = new String(Files.readAllBytes(Paths.get(jsonInputPathString)));
		} catch (final IOException e) {
			System.err.println("File " + jsonInputPathString + " could not be read.");
			System.exit(1);
		}
		
		final JsonElement outerJsonElement = JsonParser.parseString(jsonData);
		
		// TODO
	}

	private static void parseArgs(final String[] args) {
		final Options options = new Options();
		final Option jsonInputPath = new Option("i", "input", true, "JSON input file path to use");
		jsonInputPath.setRequired(true);
		options.addOption(jsonInputPath);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();

		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("cli parameters", options);
			System.exit(1);
		}

		jsonInputPathString = cmd.getOptionValue("input");
	}

}
