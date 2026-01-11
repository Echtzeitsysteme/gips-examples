package pipeline;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;

import ihtcvirtualgipssolution.hardonly.api.gips.HardonlyGipsAPI;
import pipeline.utils.*;


import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.importexport.JsonToModelLoader;
import ihtcvirtualmetamodel.importexport.ModelToJsonNoPostProcExporter;
import ihtcvirtualpreprocessing.PreprocessingNoGtApp;


public class IhtcVirtualPipeline extends AbstractVirtualPipeline {

	final private String instance = "i01.json";
	
	final private String datasetFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/ihtc2024_competition_instances/";
	
	final private String inputJsonPath = datasetFolder + instance;
	
	final private String outputXmiPath = projectFolder + "/../ihtcvirtualmetamodel/instances/" + instance.replace(".json", ".xmi");;
	
	final private String jsonOutputPath = projectFolder + "/../ihtcvirtualmetamodel/resources/runner/" + "sol_pipeline_"
			+ instance.substring(0, instance.lastIndexOf(".json")) + "_gips.json";
	
	public IhtcVirtualPipeline(boolean verbose, boolean parallelBuild) {
		super(verbose, parallelBuild);
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);
		final IhtcVirtualPipeline pipeline = new IhtcVirtualPipeline(true, true);
		pipeline.run();
	}
	
	@Override
	public void run() {
		int stage;
		String xmiOutputPath;
		logger.info("Ihtc virtual Pipeline instantiated!");
		
		importIntoXMI(inputJsonPath, outputXmiPath);

		setInstancePath(outputXmiPath);
		
		final HardonlyGipsAPI gipsApiHardOnly = new HardonlyGipsAPI();
		final IhtcvirtualgipssolutionGipsAPI gipsApiSoftConstraints = new IhtcvirtualgipssolutionGipsAPI();
		
		stage = setupNewStage(gipsApiHardOnly, 0, 10, 0);
		xmiOutputPath = executeStage(stage);
		
		stage = setupNewStage(gipsApiSoftConstraints, 0, 30, 0);
		xmiOutputPath = executeStage(stage);
		
		exportSolutionNoPostProc(xmiOutputPath, jsonOutputPath);
		
	}
	
	@Override
	public void importIntoXMI(final String inputJsonPath, final String outputXmiPath){
		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputJsonPath);
		final Root model = loader.getModel();
		try {
			// Prepare folder if necessary
			if (outputXmiPath.contains("/")) {
				FileUtils.prepareFolder(outputXmiPath.substring(0, outputXmiPath.lastIndexOf("/")));
			}
			FileUtils.save(model, outputXmiPath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
	@Override
	public void exportSolutionNoPostProc(final String xmiOutputPath, final String jsonOutputPath){
		Objects.requireNonNull(xmiOutputPath);
		Objects.requireNonNull(jsonOutputPath);

		final Resource loadedResource = FileUtils.loadModelIhtcVirtual(xmiOutputPath);
		final Root solvedHospital = (Root) loadedResource.getContents().get(0);
		final ModelToJsonNoPostProcExporter exporter = new ModelToJsonNoPostProcExporter(solvedHospital);
		logger.info("Writing output JSON file to: " + jsonOutputPath);
		exporter.modelToJson(jsonOutputPath, verbose);
	}
	
	@Override
	public void preprocessNoGt(final String instancePath, final String outputPath){
		Objects.requireNonNull(instancePath);

		final PreprocessingNoGtApp app = new PreprocessingNoGtApp(instancePath, outputPath);
		app.run();
	}

}
