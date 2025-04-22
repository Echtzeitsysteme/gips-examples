package org.emoflon.gips.ihtc.runner.utils;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.api.GipsEngineAPI;

import hipe.engine.config.HiPEPathOptions;
import ihtcgipssolution.api.gips.IhtcgipssolutionGipsAPI;
import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.utils.FileUtils;

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
	 * 
	 * @param gipsApi   GIPS API to set up.
	 * @param modelPath Path to the instance model to load.
	 */
	public static void checkIfEclipseOrJarSetup(final GipsEngineAPI<?, ?> gipsApi, final String modelPath) {
		if (gipsApi == null) {
			throw new IllegalArgumentException("Given GIPS API was null.");
		}

		if (modelPath == null || modelPath.isBlank()) {
			throw new IllegalArgumentException("Given model path was null or blank.");
		}

		// If the GIPS API is a HardonlyGipsAPI ...
		if (gipsApi instanceof HardonlyGipsAPI) {
			setup( //
					gipsApi, //
					"./ihtcgipssolution/hardonly/hipe/engine/hipe-network.xmi", //
					"ihtcgipssolution.hardonly.hipe.engine.HiPEEngine", //
					"./ihtcgipssolution/hardonly/api/gips/gips-model.xmi", //
					modelPath, //
					"./ihtcgipssolution/hardonly/api/ibex-patterns.xmi" //
			);
			// If the GIPS API is a SoftcnstrtuninGipsAPI ...
		} else if (gipsApi instanceof SoftcnstrtuningGipsAPI) {
			setup( //
					gipsApi, //
					"./ihtcgipssolution/softcnstrtuning/hipe/engine/hipe-network.xmi", //
					"ihtcgipssolution.softcnstrtuning.hipe.engine.HiPEEngine", //
					"./ihtcgipssolution/softcnstrtuning/api/gips/gips-model.xmi", //
					modelPath, //
					"./ihtcgipssolution/softcnstrtuning/api/ibex-patterns.xmi" //
			);
		} else if (gipsApi instanceof IhtcgipssolutionGipsAPI) {
			setup( //
					gipsApi, //
					"./ihtcgipssolution/hipe/engine/hipe-network.xmi", //
					"ihtcgipssolutionhipe.engine.HiPEEngine", //
					"./ihtcgipssolution/api/gips/gips-model.xmi", //
					modelPath, //
					"./ihtcgipssolution/api/ibex-patterns.xmi" //
			);

		} else {
			throw new IllegalArgumentException("Given GIPS API was not supported.");
		}
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
