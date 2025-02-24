package org.emoflon.gips.ihtc.runner.pipeline;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsRunner;

import ihtcgipssolution.nursesrooms.api.gips.NursesroomsGipsAPI;
import ihtcgipssolution.patientssurgeonsrooms.api.gips.PatientssurgeonsroomsGipsAPI;

/**
 * This example runner can be used to load an IHTC 2024 JSON-based problem file,
 * convert it to an XMI file, solve the problem using our GIPS(L)
 * implementation, and writing the solution to a JSON file as required by the
 * contest.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsPipelineRunner extends AbstractIhtcGipsRunner {

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsPipelineRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new IhtcGipsPipelineRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();

		// Output JSON file
		this.datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/pipeline/";
		this.outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(instancePath, instancePath);

		//
		// Pipeline stage (1): assign patients to days, rooms, and operating theaters
		//

		final long tick = System.nanoTime();
		final PatientssurgeonsroomsGipsAPI gipsApiA = new PatientssurgeonsroomsGipsAPI();
		gipsApiA.init(URI.createFileURI(instancePath));
		buildAndSolve(gipsApiA, true);
		applySolution(gipsApiA, true);
		gipsSave(gipsApiA, instancePath);
		final long tock = System.nanoTime();
		final double stageATimeConsumed = 1.0 * (tock - tick) / 1_000_000_000;

		if (600 - stageATimeConsumed < 0) {
			System.err.println("=> StageA consumed more than 600s. Aborting StageB.");
			tock();
			printWallClockRuntime();
			gipsApiA.terminate();
		}

		//
		// Pipeline stage (2): assign nurses to rooms
		//

		final NursesroomsGipsAPI gipsApiB = new NursesroomsGipsAPI();
		gipsApiB.init(URI.createFileURI(instancePath));
		gipsApiB.setTimeLimit(600 - stageATimeConsumed);
		buildAndSolve(gipsApiB, true);
		applySolution(gipsApiB, true);
		gipsSave(gipsApiB, gipsOutputPath);

		//
		// Convert solution XMI model to JSON output file
		//

		transformModelToJson();

		//
		// The end
		//

		tock();
		printWallClockRuntime();
		gipsApiA.terminate();
		gipsApiB.terminate();
	}

}
