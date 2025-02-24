package org.emoflon.gips.ihtc.runner.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsRunner;
import org.emoflon.gips.ihtc.runner.utils.StringUtils;

import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.utils.FileUtils;

/**
 * Runnable headless CLI runner for the IHTC 2024 GIPS-based solution.
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class IhtcGipsHeadlessRunner extends AbstractIhtcGipsRunner {

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
	 * XMI output model file path to save the transformed output model to.
	 */
	private static String xmiOutputModelPath = null;

	/**
	 * Boolean flag to enable the debug output.
	 */
	private static boolean debugOutputEnabled = false;

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
		new IhtcGipsHeadlessRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario. This method relies on the
	 * previous parsing of arguments.
	 */
	private void run() {
		tick();

		checkIfFileExists(jsonInputPath);

		//
		// Convert input JSON file to input XMI file
		//

		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(jsonInputPath);
		final Hospital model = loader.getModel();
		try {
			FileUtils.save(model, xmiInputModelPath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}

		//
		// Initialize GIPS API
		//

		final HardonlyGipsAPI gipsApi = new HardonlyGipsAPI();
		gipsApi.init(URI.createFileURI(xmiInputModelPath));

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi, debugOutputEnabled);

		//
		// Apply the solution
		//

		applySolution(gipsApi, debugOutputEnabled);

		//
		// Convert solution XMI model to JSON output file
		//

		final Hospital solvedHospital = (Hospital) gipsApi.getResourceSet().getResources().get(0).getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(jsonOutputPath, debugOutputEnabled);

		//
		// Save output XMI file
		//

		if (xmiOutputModelPath != null) {
			gipsSave(gipsApi, xmiOutputModelPath);
		}

		//
		// The end
		//

		tock();
		if (debugOutputEnabled) {
			printWallClockRuntime();
		}
		gipsApi.terminate();
		System.exit(0);
	}

	/**
	 * Parses the given arguments to configure the runner.
	 * <ol>
	 * <li>"i": input JSON file to load (required)</li>
	 * <li>"o": output JSON file to store (required)</li>
	 * <li>"q": model input XMI file to store (optional)</li>
	 * <li>"r": model output XMI file to store (optional)</li>
	 * <li>"d": debug output flag (optional)</li>
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
			jsonOutputPath = StringUtils.replaceLast(jsonInputPath, "/", "/sol_");
		}
		if (cmd.hasOption("modelinputxmi")) {
			xmiInputModelPath = cmd.getOptionValue("modelinputxmi");
		}
		if (cmd.hasOption("modeloutputxmi")) {
			xmiOutputModelPath = cmd.getOptionValue("modeloutputxmi");
		}
		IhtcGipsHeadlessRunner.debugOutputEnabled = cmd.hasOption("debug");
	}

}
