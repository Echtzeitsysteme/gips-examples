package architecture.cra.gipssolution.example;

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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import architecture.cra.gipssolution.api.gips.GipssolutionGipsAPI;
import architecture.cra.gipssolution.utils.external.ArchitectureUtil;
import architecture.util.CRAIndexCalculator;
import architectureCRA.ArchitectureCRAPackage;
import architectureCRA.ClassModel;

/**
 * Runnable headless CLI runner for the CRA assignment problem (taken from the
 * TGG3 project).
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class CraHeadlessRunner {

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

		//
		// Check if input file exists
		//

		final File xmiInputFile = new File(xmiInputPath);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("Input XMI file <" + xmiInputPath + "> could not be found.");
		}

		//
		// Create empty classes in the model
		//

		final URI uri = URI.createFileURI(xmiInputPath);

		final ResourceSet rs = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));

		rs.getPackageRegistry().put(ArchitectureCRAPackage.eINSTANCE.getNsURI(), ArchitectureCRAPackage.eINSTANCE);
		rs.getResource(uri, true);

		ArchitectureUtil.preProcess(rs.getResources().get(0));

		//
		// Write changed model to file
		//

		// Workaround: Always use absolute path
		final URI absPath = URI.createFileURI(xmiPrePath);

		// Create new model for saving
		final ResourceSet rs2 = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl(null));
		// ^null is okay if all paths are absolute
		final Resource r = rs2.createResource(absPath);
		// Fetch model contents from eMoflon
		r.getContents().add(rs.getResources().get(0).getContents().get(0));
		try {
			r.save(null);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		//
		// Initialize GIPS API
		//

		final GipssolutionGipsAPI gipsApi = new GipssolutionGipsAPI();
		gipsApi.init(absPath);

		//
		// Build and solve the ILP problem
		//

		gipsApi.buildILPProblem(true);
		final ILPSolverOutput output = gipsApi.solveILPProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());
		System.out.println("---");

		//
		// Evaluation
		//

		// Print and apply the best found solution
		printAndApplySolution(gipsApi);

		System.out.println("---");

		// Count violations
		final int violationsCounterGips = countViolationsGips(gipsApi);
		System.out.println("---");

		// Remove all empty classes (i.e., classes without an applied mapping)
		ArchitectureUtil.postProcess(gipsApi.getEMoflonAPI().getModel().getResources().get(0), false);

		// Evaluate model (with the `CRAIndexCalculator`)
		CRAIndexCalculator.evaluateModel(
				(ClassModel) gipsApi.getEMoflonAPI().getModel().getResources().get(0).getContents().get(0));

		// Evaluate model (with the violations counter by Lars)
		final ClassModel cm = (ClassModel) gipsApi.getEMoflonApp().getModel().getResources().get(0).getContents()
				.get(0);
		final int violationsCounterLars = ArchitectureUtil.countViolations(cm);
		System.out.println("---");
		System.out.println("#Violations (Lars): " + violationsCounterLars);
		System.out.println("#Violations (Max) : " + violationsCounterGips);
		System.out.println("---");

		//
		// Save output XMI file
		//

		try {
			gipsApi.saveResult(xmiOutputPath);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		//
		// Terminate everything
		//

		gipsApi.terminate();
		System.out.println("=> Finished Java headless runner execution.");
		System.exit(0);
	}

	/**
	 * Counts all assignment violations via the given GIPS API.
	 * 
	 * @param gipsApi GIPS API to get the violations from.
	 * @return Number of assignment violations.
	 */
	private static int countViolationsGips(final GipssolutionGipsAPI gipsApi) {
		// Violation counter
		int globalViolationsCounter = 0;

		// Violation A counter
		int mappingCounter = 0;
		for (var k : gipsApi.getViolationA().getMappings().keySet()) {
			if (gipsApi.getViolationA().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationA Counter:  " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		// Violation C counter
		mappingCounter = 0;
		for (var k : gipsApi.getViolationC().getMappings().keySet()) {
			if (gipsApi.getViolationC().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationC Counter:  " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		// Violation D1 counter
		mappingCounter = 0;
		for (var k : gipsApi.getViolationD1().getMappings().keySet()) {
			if (gipsApi.getViolationD1().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationD1 Counter: " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		// Violation D2 counter
		mappingCounter = 0;
		for (var k : gipsApi.getViolationD2().getMappings().keySet()) {
			if (gipsApi.getViolationD2().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationD2 Counter: " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		return globalViolationsCounter;
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
		printSolution = cmd.hasOption("printsolution");
	}

	/**
	 * Prints and applies the best found solution (aka all non-zero mappings) with a
	 * given CRA GIPS API object.
	 * 
	 * @param gipsApi CRA GIPS API object to get all mapping information from. This
	 *                API will also be used to apply all non-zero mappings.
	 */
	private static void printAndApplySolution(final GipssolutionGipsAPI gipsApi) {
		if (printSolution) {
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
