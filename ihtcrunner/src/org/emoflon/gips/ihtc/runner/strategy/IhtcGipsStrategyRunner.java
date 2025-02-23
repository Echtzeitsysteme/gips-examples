package org.emoflon.gips.ihtc.runner.strategy;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.ihtc.runner.IhtcGipsRunner;

import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.loader.FileUtils;
import ihtcmetamodel.loader.JsonToModelLoader;
import ihtcmetamodel.loader.ModelCostCalculator;
import ihtcmetamodel.loader.ModelToJsonExporter;

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
public class IhtcGipsStrategyRunner extends IhtcGipsRunner {

	/**
	 * If true, the runner will print more detailed information.
	 */
	public boolean verbose = true;

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsStrategyRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new IhtcGipsStrategyRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();
		final long tickStageOne = System.nanoTime();

		//
		// Folder and file definitions
		//
		final String inputPath = datasetFolder + scenarioFileName;

		// Input XMI file
		final String instanceFolder = projectFolder + "/../ihtcmetamodel/instances/";
		final String instancePath = instanceFolder + scenarioFileName.replace(".json", ".xmi");

		// Output XMI file
		final String gipsOutputPath = instanceFolder
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_solved.xmi";

		// Output JSON file
		final String datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/strategy_runner/";
		final String outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputPath);
		final Hospital model = loader.getModel();
		try {
			FileUtils.prepareFolder(instanceFolder);
			FileUtils.save(model, instancePath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}

		//
		// Initialize GIPS API: Hard constraints only.
		//

		if (verbose) {
			System.out.println("=> Start stage 1.");
		}

		final HardonlyGipsAPI gipsApi = new HardonlyGipsAPI();
		gipsApi.init(URI.createFileURI(instancePath));

		//
		// Run first GIPS solution
		//

		buildAndSolve(gipsApi, verbose);
		applySolution(gipsApi, verbose);
		gipsSave(gipsApi, gipsOutputPath);
		exportToJson(gipsOutputPath, outputPath);
		gipsApi.terminate();
		if (verbose) {
			System.out.println("=> Stage 1 found a solution.");
		}
		final long tockStageOne = System.nanoTime();
		final double stageOneRuntime = 1.0 * (tockStageOne - tickStageOne) / 1_000_000_000;

		//
		// Initialize GIPS API: All optional constraints included.
		//

		final SoftcnstrtuningGipsAPI gipsApiOptional = new SoftcnstrtuningGipsAPI();
		gipsApiOptional.init(URI.createFileURI(instancePath));
		gipsApiOptional.setTimeLimit(570 - stageOneRuntime);
		// TODO: set MIPFocus parameter here?

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
				// TODO: Maybe we have to remove the previously written file before writing the
				// new version
				exportToJson(gipsOutputPath, outputPath);
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

}
