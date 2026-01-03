package pipeline;

import java.util.Objects;

import org.emoflon.gips.core.api.GipsEngineAPI;

/**
 * Record holding the individual configuration of a specific PipelineStage.
 */
public record PipelineStage(GipsEngineAPI<?, ?> gipsApi, String inputPath, String outputPath, String parameterPath, String callbackPath, Integer randomSeed, Integer timeLimit, Integer threads) {
	
//	/*
//	 * Gips Api of the pipeline Stage 
//	 */
//	protected GipsEngineAPI<?, ?> gipsApi;
//	
//	/*
//	 * Path of the input Model as an xmi.  
//	 */
//	protected String inputPath;
//	
//	/**
//	 * Gurobi parameter path.
//	 */
//	protected String parameterPath;
//	
//	/**
//	 * Gurobi callback path.
//	 */
//	protected String callbackPath;
//	
//	/**
//	 * Default instance solved XMI path.
//	 */
//	protected String outputPath;


}
