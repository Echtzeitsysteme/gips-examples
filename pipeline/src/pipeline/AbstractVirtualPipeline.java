package pipeline;

import java.util.Objects;

import javax.naming.OperationNotSupportedException;

import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.util.Observer;

import pipeline.utils.XmiSetupUtil;

public abstract class AbstractVirtualPipeline  extends AbstractPipeline  {
	
	/**
	 * If true, the post processing will be skipped and the JSON output will be
	 * directly derived from the GIPS solution model (i.e., it will search for
	 * `isSelected == true` virtual objects.
	 */
	private boolean postProc = false;

	/**
	 * If true, the pre-processing will be executed with the Java-only (i.e., no GT)
	 * implementation.
	 */
	private boolean preProcNoGt = true;
	
	public AbstractVirtualPipeline(boolean verbose, boolean parallelBuild) {
		super(verbose, parallelBuild);
	}
	
	/**
	 * Pre-processing method that runs the separated Java-based pre-processing
	 * implementation. The given `instancePath` will be used to load the XMI model.
	 * The produced (altered) model file will be written to `outputPath`.
	 * 
	 * @param instancePath Model (XMI) to load.
	 * @param outputPath   Model (XMI) to save the result to.
	 * @throws OperationNotSupportedException 
	 */
	public void preprocessNoGt(final String instancePath, final String outputPath) throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
	}

	/**
	 * Pre-processing method that runs the separated GT rule set. The given
	 * `instancePath` will be used to load the XMI model. The produced (altered)
	 * model file will be written to `outputPath`.
	 * 
	 * @param instancePath Model (XMI) to load.
	 * @param outputPath   Model (XMI) to save the result to.
	 * @throws OperationNotSupportedException 
	 */
	public void preprocess(final String instancePath, final String outputPath) throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
	}
	
	/**
	 * Takes an XMI output path (of a GIPS-generated solution model) and writes the
	 * corresponding JSON output to `jsonOutputPath`. This method relies on the
	 * non-post-processed model.
	 * 
	 * @param xmiOutputPath  GIPS-generated solution model to convert.
	 * @param jsonOutputPath JSON output file location to write the JSON output file
	 *                       to.
	 * @throws OperationNotSupportedException 
	 */
	public void exportSolutionNoPostProc(final String xmiOutputPath, final String jsonOutputPath) throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
	}
	
	/**
	 * Post-processing method that runs the separated GT rule set. The given
	 * `instancePath` will be used to load the XMI model. The produced (altered)
	 * model file be written to `outputPath`.
	 * 
	 * @param instancePath Model (XMI) to load.
	 * @param outputPath   Model (XMI) output path.
	 * @throws OperationNotSupportedException 
	 */
	protected void postprocess(final String instancePath, final String outputPath) throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
	}
	
	public String executeStage(final int stage) {
		
		PipelineStage currentStage = pipelineStages.get(stage);
		GipsEngineAPI<?, ?> gipsApi = currentStage.gipsApi();
		
		checkIfFileExists(currentStage.inputPath());
		
		if (verbose) {
			logger.info("=> Start Stage " + stage);
			logger.info("=> Starting Preprocessing " + (preProcNoGt ? "without GT" : "with GT"));
		}
		
		String preprocessingPath = outputFolder + "/" + instance.substring(0, instance.lastIndexOf(".xmi")) + "_preproc_stage_" + stage + ".xmi";
		
		if (preProcNoGt) {
			try {
				preprocessNoGt(currentStage.inputPath(), preprocessingPath);
			} catch (OperationNotSupportedException e) {
				logger.info("preprocessNoGt() is not implemented for stage " + stage + "!");
			}
		} else {
			try {
				preprocess(currentStage.inputPath(), preprocessingPath);
			} catch (Exception e) {
				logger.info("preprocess() is not implemented for stage " + stage + "!");
			}

		}
		
		if (verbose) {
			logger.info("=> Initializing GIPS Api: " + gipsApi.toString());
		}
		
		Observer.getInstance().setCurrentSeries("Eval");
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, preprocessingPath);
		
		// Set GIPS configuration parameters from this object
		this.setGipsConfig(currentStage);

		//
		// Run GIPS solution
		//
		
		this.buildAndSolve(gipsApi);
		
		if (applicationNoGt) {
			try {
				applySolutionNoGt(gipsApi);
			} catch (OperationNotSupportedException e) {
				logger.info("applySolutionNoGt(gipsApi) is not implemented! The solution was applied via the GT rules." + stage);
				applySolution(gipsApi);
			}
		} else {
			applySolution(gipsApi);
		}
		
		gipsSave(currentStage);

		//
		// The end
		//

		gipsApi.terminate();
		
		if(postProc) {
			
			String postprocessingPath = outputFolder + "/" + instance.substring(0, instance.lastIndexOf(".xmi")) + "_postproc_stage_" + stage + ".xmi";
			if (verbose) {
				logger.info("=> Start post-processing GT.");
			}
			try {
				postprocess(currentStage.outputPath(), postprocessingPath);
			} catch (Exception e) {
				logger.info("postprocess is not implemented for stage " + stage);
			}
		}
		
		if (verbose) {
			logger.info("=> Finished Stage " + stage);
		}
		
		return currentStage.outputPath();
	}
	
	/**
	 * 
	 * @param postProc Sets if there should be a postprocessing 
	 */
	public void setPostProc(boolean postProc) {
		this.postProc = postProc;
	}
	
	/**
	 * 
	 * @param preProcNoGt Sets if the preprocessing is executed via GT rules or java-based
	 */
	public void setPreProcNoGt(boolean preProcNoGt) {
		this.preProcNoGt = preProcNoGt;
	}
	
}
