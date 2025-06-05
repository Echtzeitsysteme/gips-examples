package org.emoflon.gips.ihtc.runner.strategy;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsRunner;
import org.emoflon.gips.ihtc.runner.utils.GurobiTuningUtil;
import org.emoflon.gips.ihtc.runner.utils.XmiSetupUtil;

import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.metrics.ModelCostCalculator;
import ihtcmetamodel.utils.FileUtils;

/**
 * This strategy runner can be used to load an IHTC 2024 JSON-based problem
 * file, convert it to an XMI file, solve the problem using our GIPS(L)
 * implementation, and writing the solution to a JSON file as required by the
 * contest.
 * 
 * The implemented strategy:
 * <ol>
 * <li>1: Solve the given problem with the hard constraints only. This is our
 * fallback solution if the more advanced problem can not be solved in
 * time.</li>
 * <li>2: Solve the given problem with all implemented optional constraints. If
 * it is able to find a solution in time, overwrite the previously found
 * solution.</li>
 * </ol>
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsStrategyRunner extends AbstractIhtcGipsRunner {

	/**
	 * If true, the runner will print more detailed information.
	 */
	private boolean verbose = true;

	/**
	 * Boolean flag to enable output JSON file splitting.
	 */
	private boolean splitOutputJsonEnabled = false;

	/**
	 * Random seed for the (M(ILP solver.
	 */
	private int randomSeed = -1;

	/**
	 * Create a new instance of this class.
	 */
	public IhtcGipsStrategyRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcGipsStrategyRunner runner = new IhtcGipsStrategyRunner();
		runner.setupDefaultPaths();
		runner.run();
	}

	/**
	 * Sets the default paths up.
	 */
	void setupDefaultPaths() {
		// Update output JSON file path
		this.datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/strategy_runner/";
		this.outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";
	}

	/**
	 * Sets the JSON input path.
	 * 
	 * @param jsonInputPath JSON input path.
	 */
	public void setJsonInputPath(final String jsonInputPath) {
		this.inputPath = jsonInputPath;
	}

	/**
	 * Sets the JSON output path.
	 * 
	 * @param jsonOutputPath JSON output path.
	 */
	public void setJsonOutputPath(final String jsonOutputPath) {
		this.outputPath = jsonOutputPath;
	}

	/**
	 * Sets the XMI input model path.
	 * 
	 * @param xmiInputModelPath XMI input model path.
	 */
	public void setXmiInputModelPath(final String xmiInputModelPath) {
		this.instancePath = xmiInputModelPath;
	}

	/**
	 * Sets the XMI output model path.
	 * 
	 * @param xmiOutputModelPath XMI output model path.
	 */
	public void setXmiOutputModelPath(final String xmiOutputModelPath) {
		this.gipsOutputPath = xmiOutputModelPath;
	}

	/**
	 * Sets the random seed of the (M)ILP solver.
	 * 
	 * @param randomSeed Random seed to set for the (M)ILP solver.
	 */
	public void setRandomSeed(final int randomSeed) {
		this.randomSeed = randomSeed;
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();
		final long tickStageOne = System.nanoTime();

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(inputPath, instancePath);

		//
		// Initialize GIPS API: Hard constraints only.
		//

//		if (verbose) {
//			System.out.println("=> Start stage 1.");
//		}
//
//		final HardonlyGipsAPI gipsApi = new HardonlyGipsAPI();
//		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, instancePath);
//		// Set presolve to "auto"
//		GurobiTuningUtil.updatePresolve(gipsApi, -1);
//		if (randomSeed != -1) {
//			GurobiTuningUtil.updateRandomSeed(gipsApi, randomSeed);
//		}
//
//		if (verbose) {
//			GurobiTuningUtil.setDebugOutput(gipsApi);
//		}
//
//		//
//		// Run first GIPS solution
//		//
//
//		buildAndSolve(gipsApi, verbose);
//		applySolution(gipsApi, verbose);
//		gipsSave(gipsApi, gipsOutputPath);
//		exportToJson(gipsOutputPath, outputPath);
//		gipsApi.terminate();
//		if (verbose) {
//			System.out.println("=> Stage 1 found a solution.");
//		}
//		final long tockStageOne = System.nanoTime();
//		final double stageOneRuntime = 1.0 * (tockStageOne - tickStageOne) / 1_000_000_000;
//		if (verbose) {
//			System.out.println("=> Stage 1 run time: " + stageOneRuntime + "s.");
//		}

		//
		// Initialize GIPS API: All optional constraints included.
		//

		final SoftcnstrtuningGipsAPI gipsApiOptional = new SoftcnstrtuningGipsAPI();
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApiOptional, instancePath);

		final long tockBeforeRunningStageTwo = System.nanoTime();
		final double remainingTime = 570 - 1.0 * (tockBeforeRunningStageTwo - tickStageOne) / 1_000_000_000;
		gipsApiOptional.setTimeLimit(remainingTime);
		// TODO: This should now be redundant
//		GurobiTuningUtil.updateTimeLimit(gipsApiOptional, remainingTime);
		// Set presolve to "auto"
//		GurobiTuningUtil.updatePresolve(gipsApiOptional, -1);
//		// TODO: set MIPFocus parameter here?
//		if (randomSeed != -1) {
//			GurobiTuningUtil.updateRandomSeed(gipsApiOptional, randomSeed);
//		}
//
//		if (verbose) {
//			GurobiTuningUtil.setDebugOutput(gipsApiOptional);
//		}

		//
		// Run second GIPS solution
		//

		if (verbose) {
			System.out.println("=> Start stage 2.");
		}

		try {
			buildAndSolve(gipsApiOptional, verbose);
			applySolution(gipsApiOptional, verbose);
			final int stageATotalCost = getCost(gipsOutputPath);
			final int stageBTotalCost = getCost(gipsApiOptional.getEMoflonAPI().getModel().getResources().get(0));
			if (stageBTotalCost < stageATotalCost) {
				gipsSave(gipsApiOptional, gipsOutputPath);
				exportToJson(gipsOutputPath, outputPath + (splitOutputJsonEnabled ? "2" : ""));
				if (verbose) {
					System.out.println("=> Stage 2 found a solution.");
				}
			} else {
				if (verbose) {
					System.out.println("=> Stage 2 found a solution but its cost was higher. Skipping export.");
				}
			}
		} catch (final InternalError e) {
			// The second stage threw an error -> it does not find a valid solution in time.
			if (verbose) {
				System.out.println("=> Stage 2 did not find a valid solution.");
			}
		} finally {
			gipsApiOptional.terminate();
		}

		//
		// The end
		//

		tock();
		printWallClockRuntime();
	}

	/**
	 * Takes an XMI output path (of a GIPS-generated solution model) and writes the
	 * corresponding JSON output to `jsonOutputPath`.
	 * 
	 * @param xmiOutputPath  GIPS-generated solution model to convert.
	 * @param jsonOutputPath JSON output file location to write the JSON output file
	 *                       to.
	 */
	private void exportToJson(final String xmiOutputPath, final String jsonOutputPath) {
		final Resource loadedResource = FileUtils.loadModel(xmiOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(jsonOutputPath);
	}

	/**
	 * Returns the total cost value of a given solved hospital model at the XMI
	 * path.
	 * 
	 * @param xmiPath XMI file path of a solved hospital model.
	 * @return Total cost of the given solved hospital model.
	 */
	private int getCost(final String xmiPath) {
		final Resource loadedResource = FileUtils.loadModel(xmiPath);
		return getCost(loadedResource);
	}

	/**
	 * Returns the total cost value of a given solved hospital model in the form of
	 * a resource.
	 * 
	 * @param model Resource with the hospital model.
	 * @return Total cost of the given solved hospital model.
	 */
	private int getCost(final Resource model) {
		final Hospital solvedHospital = (Hospital) model.getContents().get(0);
		final ModelCostCalculator calc = new ModelCostCalculator();
		return calc.calculateTotalCost(solvedHospital);
	}

	/**
	 * Sets the verbose flag to the given value.
	 * 
	 * @param verbose Verbose flag.
	 */
	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets the split output JSON flag to the given value.
	 * 
	 * @param splitOutputJsonEnabled Split output JSON flag.
	 */
	public void setSplitOutputJsonEnabled(final boolean splitOutputJsonEnabled) {
		this.splitOutputJsonEnabled = splitOutputJsonEnabled;
	}

}
