package classdiagramtgg3.example;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;

import classdiagramtgg3.api.gips.Classdiagramtgg3GipsAPI;

/**
 * Runnable headless CLI runner for the Class Diagram Assignment problem (taken
 * from the TGG3 project).
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class ClassDiagramHeadlessRunner {

	/**
	 * XMI input file path to load a model from.
	 */
	private static String xmiInputPath;

	/**
	 * XMI output file path to save the solution model to.
	 */
	private static String xmiOutputPath;

	/**
	 * If true, the runner will print the complete solution to the console.
	 */
	private static boolean printSolution = true;

	/**
	 * Main method to start the headless runner. String array of arguments will be
	 * parsed.
	 * 
	 * @param args See {@link #parseArgs(String[])}.
	 */
	public static void main(final String[] args) {
		parseArgs(args);

		// Check if input file exists
		final File xmiInputFile = new File(xmiInputPath);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("Input XMI file <" + xmiInputPath + "> could not be found.");
		}

		final Classdiagramtgg3GipsAPI gipsApi = new Classdiagramtgg3GipsAPI();

		// Initialize GIPS API
		final URI uri = URI.createFileURI(xmiInputPath);
		gipsApi.init(uri);

		// Build and solve the ILP problem
		gipsApi.buildILPProblem(true);
		final ILPSolverOutput output = gipsApi.solveILPProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());
		System.out.println("---");

		// Print and apply the best found solution
		printAndApplySolution(gipsApi);

		// Save output XMI file
		try {
			gipsApi.saveResult(xmiOutputPath);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// Terminate everything
		gipsApi.terminate();
		System.out.println("=> Finished Java headless runner execution.");
		System.exit(0);
	}

	/**
	 * Parses the given arguments to configure the runner.
	 * <ol>
	 * <li>#0: XMI input file path (required)</li>
	 * <li>#1: XMI output file path (required)</li>
	 * <li>#2: Print the found solution to console (optional)</li>
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
		printSolution = cmd.hasOption("printsolution");
	}

	/**
	 * Prints and applies the best found solution (aka all non-zero mappings) with a
	 * given Class Diagram TGG3 GIPS API object.
	 * 
	 * @param gipsApi Class Diagram TGG3 GIPS API object to get all mapping
	 *                information from. This API will also be used to apply all
	 *                non-zero mappings.
	 */
	private static void printAndApplySolution(final Classdiagramtgg3GipsAPI gipsApi) {
		if (printSolution) {
			System.out.println("Violation Mappings: ");
			gipsApi.getViolationA().getMappings().forEach((k, v) -> {
				System.out.println(v.getValue() + ": " + v.getA1().getName() + " -> " + v.getC1().getName() + "; "
						+ v.getM1().getName() + " -> " + v.getC2().getName());
			});

			System.out.println("Embeddings (Attributes): ");
			gipsApi.getEmbedAttribute().getMappings().forEach((k, v) -> {
				if (v.getValue() == 1) {
					System.out.println("  " + v.getMatch().getA().getName() + " -> " + v.getMatch().getC().getName());
				}
			});

			System.out.println("Embeddings (Methods): ");
			gipsApi.getEmbedMethod().getMappings().forEach((k, v) -> {
				if (v.getValue() == 1) {
					System.out.println("  " + v.getMatch().getM().getName() + " -> " + v.getMatch().getC().getName());
				}
			});
		}

		// Apply found solution
		gipsApi.getEmbedAttribute().applyNonZeroMappings();
		gipsApi.getEmbedMethod().applyNonZeroMappings();
	}

}
