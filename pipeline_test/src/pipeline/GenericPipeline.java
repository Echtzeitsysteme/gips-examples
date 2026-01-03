package pipeline;

import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import java.util.Objects;

import org.emoflon.gips.core.api.GipsEngineAPI;

//import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;

public class GenericPipeline extends AbstractPipeline {
	
	private String instance = "i01.json";
	
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
		boolean verbose = true;
		boolean parallelBuild = true;
		final GenericPipeline pipeline = new GenericPipeline(verbose, parallelBuild);
		pipeline.run();
	}
	
	@Override
	public void run() {
		logger.info("Generic Pipeline instantiated!");
		final SoftcnstrtuningGipsAPI gipsApi = new SoftcnstrtuningGipsAPI();
		
	}

	@Override
	public void setupFolder() {

		
	}

	@Override
	public void checkIfEclipseOrJarSetup(GipsEngineAPI<?, ?> gipsApi, String modelPath) {
		// TODO Auto-generated method stub
		
	}
	
}
