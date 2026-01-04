package pipeline;

import java.util.Objects;

import org.emoflon.gips.core.api.GipsEngineAPI;

public class IhtcVirtualPipeline extends AbstractPipeline{
	
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
	
	/**
	 * If true, the application of the GT rules of the GIPSL specification will only
	 * be simulated by manually written Java code instead of actually applying GT
	 * rule matches with eMoflon::IBeX-GT.
	 * Only necessary for models with virtual nodes.
	 */
	private boolean applicationNoGt = true;
	
	private String instanceFolder = projectFolder + "/../ihtcvirtualmetamodel/resources/ihtc2024_competition_instances/";
	private String instance = "i01.json";
	private String inputPath = instanceFolder + instance;

	IhtcVirtualPipeline(boolean verbose, boolean parallelBuild) {
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
		logger.info("Ihtcvirtual Pipeline instantiated!");	
		
		setInstancePath(inputPath);
	}

}
