package pipeline.utils;

import java.util.Objects;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.api.GipsEngineAPI;

import hipe.engine.config.HiPEPathOptions;

/**
 * XMI setup utilities for the GIPS APIs.
 * 
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class XmiSetupUtil {

	/**
	 * No public instances of this class allowed.
	 */
	private XmiSetupUtil() {
	}

	/**
	 * Checks if XMI files exist and sets up the GIPS API accordingly.
	 * This method is used when executing a pipeline stage and assumes that the name of the
	 * gipsApi is identical to the necessary paths. 
	 * 
	 * @param gipsApi   GIPS API to set up.
	 * @param modelPath Path to the instance model to load.
	 */
	public static void checkIfEclipseOrJarSetup(final GipsEngineAPI<?, ?> gipsApi, final String modelPath) {
		Objects.requireNonNull(gipsApi, "Given GIPS API was null.");
		Objects.requireNonNull(modelPath);

		if (modelPath.isBlank()) {
			throw new IllegalArgumentException("Given model path was null or blank.");
		}
		
		String apiName = gipsApi.getClass().getName().toLowerCase();
		
		String baseFolder = apiName.substring(0, apiName.lastIndexOf("gipsapi"));
		
		String hipeNetworkXmiPath = "./" + baseFolder + "/hipe/engine/hipe-network.xmi";
		String hipeEngineClassname = baseFolder + ".hipe.engine.HiPEEngine";
		String gipsModelXmiPath = "./" + baseFolder + "/api/gips/gips-model.xmi";
		String ibexPatternXmiPath = "./" + baseFolder + "/api/ibex-patterns.xmi";
		
		setup( //
				gipsApi, //
				hipeNetworkXmiPath, //
				hipeEngineClassname, //
				gipsModelXmiPath, //
				modelPath, //
				ibexPatternXmiPath //
		);

	}

	/**
	 * Sets up the GIPS API with the given paths.
	 * 
	 * @param gipsApi             GIPS API to set up.
	 * @param hipeNetworkXmiPath  HiPE network XMI file path to load.
	 * @param hipeEngineClassname HiPE engine class name to configure.
	 * @param gipsModelXmiPath    GIPS intermediate model XMI file path to load.
	 * @param modelPath           Model path to load.
	 * @param ibexPatternXmiPath  IBeX pattern XMI file path to load.
	 */
	private static void setup(final GipsEngineAPI<?, ?> gipsApi, final String hipeNetworkXmiPath,
			final String hipeEngineClassname, final String gipsModelXmiPath, final String modelPath,
			final String ibexPatternXmiPath) {
		Objects.requireNonNull(gipsApi);
		Objects.requireNonNull(hipeNetworkXmiPath);
		Objects.requireNonNull(hipeEngineClassname);
		Objects.requireNonNull(gipsModelXmiPath);
		Objects.requireNonNull(modelPath);
		Objects.requireNonNull(ibexPatternXmiPath);

		final boolean runAsJar = FileUtils.checkIfFileExists(gipsModelXmiPath) //
				&& FileUtils.checkIfFileExists(ibexPatternXmiPath) //
				&& FileUtils.checkIfFileExists(hipeNetworkXmiPath);
		if (!runAsJar) {
			gipsApi.init(URI.createFileURI(modelPath));
		} else {
			HiPEPathOptions.getInstance().setNetworkPath( //
					URI.createFileURI(hipeNetworkXmiPath) //
			);
			HiPEPathOptions.getInstance().setEngineClassName( //
					hipeEngineClassname //
			);
			gipsApi.init( //
					URI.createFileURI(gipsModelXmiPath), //
					URI.createFileURI(modelPath), //
					URI.createFileURI(ibexPatternXmiPath) //
			);
		}
	}

}
