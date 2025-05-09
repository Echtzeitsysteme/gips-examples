package org.emoflon.gips.ihtc.virtual.runner;

import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.ihtc.virtual.runner.utils.FileUtils;
import org.emoflon.gips.ihtc.virtual.runner.utils.XmiSetupUtil;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.importexport.ModelToJsonExporter;

public class IhtcVirtualGipsRunner extends AbstractIhtcVirtualGipsRunner {

	/**
	 * If true, the runner will print more detailed information.
	 */
	private boolean verbose = true;

	/**
	 * Create a new instance of this class.
	 */
	public IhtcVirtualGipsRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);

		final IhtcVirtualGipsRunner runner = new IhtcVirtualGipsRunner();
		runner.setupDefaultPaths();
		runner.run();
	}

	/**
	 * Sets the default paths up.
	 */
	void setupDefaultPaths() {
		// Update output JSON file path
		this.datasetSolutionFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/runner/";
		this.outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";
	}

	@Override
	public void run() {
		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(inputPath, instancePath);

		//
		// Initialize GIPS API
		//

		if (verbose) {
			logger.info("=> Start GIPS init.");
		}

		final IhtcvirtualgipssolutionGipsAPI gipsApi = new IhtcvirtualgipssolutionGipsAPI();
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, instancePath);

		//
		// Run GIPS solution
		//

		buildAndSolve(gipsApi, verbose);
		applySolution(gipsApi, verbose);
		gipsSave(gipsApi, gipsOutputPath);
		exportToJson(gipsOutputPath, outputPath);

		//
		// The end
		//

		gipsApi.terminate();
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
		Objects.requireNonNull(xmiOutputPath);
		Objects.requireNonNull(jsonOutputPath);

		final Resource loadedResource = FileUtils.loadModel(xmiOutputPath);
		final Root solvedHospital = (Root) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(jsonOutputPath, verbose);
	}

	/**
	 * Sets the verbose flag to the given value.
	 * 
	 * @param verbose Verbose flag value.
	 */
	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

}
