package org.emoflon.gips.ihtc.runner.softcnstr.optional.patients;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.ihtc.runner.AbstractIhtcGipsRunner;
import org.emoflon.gips.ihtc.runner.utils.GurobiTuningUtil;

import ihtcgipssolution.softcnstr.optionalpatients.api.gips.OptionalpatientsGipsAPI;

/**
 * This example runner can be used to load an IHTC 2024 JSON-based problem file,
 * convert it to an XMI file, solve the problem using our GIPS(L)
 * implementation, and writing the solution to a JSON file as required by the
 * contest.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsSoftCnstrOptionalPatientsRunner extends AbstractIhtcGipsRunner {

	/**
	 * No public instances of this class allowed.
	 */
	protected IhtcGipsSoftCnstrOptionalPatientsRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		new IhtcGipsSoftCnstrOptionalPatientsRunner().run();
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();

		// Output JSON file
		this.datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/soft_cnstr_optional_patients/";
		this.outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(inputPath, instancePath);

		//
		// Initialize GIPS API
		//

		final OptionalpatientsGipsAPI gipsApi = new OptionalpatientsGipsAPI();
		gipsApi.init(URI.createFileURI(instancePath));

		// Set Gurobi tuning parameters
		GurobiTuningUtil.setMipFocus(gipsApi);
		GurobiTuningUtil.setMipGap(gipsApi);

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi, true);

		//
		// Apply the solution
		//

		applySolution(gipsApi, true);

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, gipsOutputPath);

		//
		// Convert solution XMI model to JSON output file
		//

		transformModelToJson();

		//
		// The end
		//

		tock();
		printWallClockRuntime();
		gipsApi.terminate();
	}

}
