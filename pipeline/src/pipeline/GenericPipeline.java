package pipeline;

import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.utils.FileUtils;

import java.io.IOException;
import java.util.Objects;

import javax.naming.OperationNotSupportedException;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.core.api.GipsEngineAPI;

public class GenericPipeline extends AbstractPipeline {
	
	final private String instance = "i01.json";
	
	final private String datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_competition_instances/";
	
	final private String inputJsonPath = datasetFolder + instance;
	
	final private String outputXmiPath = projectFolder + "/../ihtcmetamodel/instances/" + instance.replace(".json", ".xmi");;
	
	final private String jsonOutputPath = projectFolder + "/../ihtcmetamodel/resources/" + "sol_pipeline_"
			+ instance.substring(0, instance.lastIndexOf(".json")) + "_gips.json";
	
	GenericPipeline(boolean verbose, boolean parallelBuild) {
		super(verbose, parallelBuild);
	}
	
	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);
		final GenericPipeline pipeline = new GenericPipeline(true, true);
		pipeline.run();
	}
	
	@Override
	public void importXMI(final String inputJsonPath, final String outputXmiPath){
		Objects.requireNonNull(inputJsonPath);
		Objects.requireNonNull(outputXmiPath);

		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputJsonPath);
		final Hospital model = loader.getModel();
		try {
			// Prepare folder if necessary
			if (inputJsonPath.contains("/")) {
				FileUtils.prepareFolder(outputXmiPath.substring(0, outputXmiPath.lastIndexOf("/")));
			}
			FileUtils.save(model, outputXmiPath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
	@Override
	public void exportSolution(final String xmiOutputPath, final String jsonOutputPath){
		Objects.requireNonNull(xmiOutputPath);
		Objects.requireNonNull(jsonOutputPath);

		final Resource loadedResource = FileUtils.loadModel(xmiOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		logger.info("Writing output JSON file to: " + jsonOutputPath);
		exporter.modelToJson(jsonOutputPath, verbose);
	}
	
	@Override
	public void run() {
		logger.info("Generic Pipeline instantiated!");
		
		importXMI(inputJsonPath, outputXmiPath);

		setInstancePath(outputXmiPath);
		
		final SoftcnstrtuningGipsAPI gipsApi = new SoftcnstrtuningGipsAPI();
		
		int stage = setupNewStage(gipsApi);
		String xmiOutputPath = executeStage(stage);
		
		exportSolution(xmiOutputPath, jsonOutputPath);
	}
	
}
