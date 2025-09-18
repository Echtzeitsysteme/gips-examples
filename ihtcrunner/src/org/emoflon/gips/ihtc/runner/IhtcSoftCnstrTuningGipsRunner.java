package org.emoflon.gips.ihtc.runner;

import java.util.Objects;

import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.ihtc.runner.utils.XmiSetupUtil;

import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.metrics.ModelCostCalculator;

public class IhtcSoftCnstrTuningGipsRunner extends AbstractIhtcGipsRunner {

	/**
	 * Create a new instance of this class.
	 */
	public IhtcSoftCnstrTuningGipsRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);

		final IhtcSoftCnstrTuningGipsRunner runner = new IhtcSoftCnstrTuningGipsRunner();
		runner.setupDefaultPaths();
		runner.run();
	}

	@Override
	public void run() {
		checkIfFileExists(inputPath);
		final long startTime = System.nanoTime();

		//
		// Convert JSON input file to XMI file
		//

		if (verbose) {
			logger.info("=> Start JSON model loader.");
		}

		transformJsonToModel(inputPath, instancePath);
		final long modelLoadedTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime model load: " + tickTockToElapsedSeconds(startTime, modelLoadedTime) + "s.");
		}

		SoftcnstrtuningGipsAPI gipsApi = null;
		long gipsSaveDoneTime = -1;
		int bestFoundSolution = Integer.MAX_VALUE;

		final long timeLimitNano = 10l * 60 * 1_000_000_000;
		while (System.nanoTime() <= startTime + timeLimitNano) {
			//
			// Initialize GIPS API
			//

			if (verbose) {
				logger.info("=> Start GIPS init.");
			}

			Observer.getInstance().setCurrentSeries("Eval");
			gipsApi = new SoftcnstrtuningGipsAPI();
			XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, instancePath);
			final long gipsInitDoneTime = System.nanoTime();
			if (verbose) {
				logger.info("Runtime GIPS init: " + tickTockToElapsedSeconds(startTime, gipsInitDoneTime) + "s.");
			}

			// Set GIPS configuration parameters from this object
			setGipsConfig(gipsApi);

			//
			// Run GIPS solution
			//

			buildAndSolve(gipsApi, verbose);
			final long gipsSolvingDoneTime = System.nanoTime();

			applySolution(gipsApi, verbose);

			final long solutionApplicationDoneTime = System.nanoTime();
			if (verbose) {
				logger.info("Runtime solution application: "
						+ tickTockToElapsedSeconds(gipsSolvingDoneTime, solutionApplicationDoneTime) + "s.");
			}
			gipsSave(gipsApi, gipsOutputPath);
			gipsSaveDoneTime = System.nanoTime();
			if (verbose) {
				logger.info("Runtime GIPS save: "
						+ tickTockToElapsedSeconds(solutionApplicationDoneTime, gipsSaveDoneTime) + "s.");
			}

			gipsApi.terminate();

			// Calculate cost and (potentially) update best found solution value
			final Hospital solution = (Hospital) gipsApi.getEMoflonApp().getModel().getResources().get(0).getContents()
					.get(0);
			final ModelCostCalculator calc = new ModelCostCalculator();
			int newSolution = calc.calculateTotalCost(solution);
			if (newSolution < bestFoundSolution) {
				bestFoundSolution = newSolution;
				logger.info("Found a new best solution with obj: " + bestFoundSolution);
			}
		}

		//
		// Export
		//

		if (verbose) {
			logger.info("=> Start JSON export.");
		}
		exportToJson(gipsOutputPath, outputPath);
		final long exportDoneTime = System.nanoTime();
		if (verbose) {
			logger.info("Runtime JSON export: " + tickTockToElapsedSeconds(gipsSaveDoneTime, exportDoneTime) + "s.");
		}

		//
		// The end
		//

		gipsApi.terminate();

		if (verbose) {
			logger.info("Runtime total: " + tickTockToElapsedSeconds(startTime, System.nanoTime()) + "s.");
		}
	}

}
