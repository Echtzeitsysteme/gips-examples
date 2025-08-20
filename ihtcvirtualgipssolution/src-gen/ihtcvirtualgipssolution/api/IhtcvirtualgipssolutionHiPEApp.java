package ihtcvirtualgipssolution.api;

import org.emoflon.ibex.gt.hipe.runtime.HiPEGTEngine;

/**
 * An application using the IhtcvirtualgipssolutionAPI with HiPE.
 */
public class IhtcvirtualgipssolutionHiPEApp extends IhtcvirtualgipssolutionApp {

	/**
	 * Creates the application with HiPE.
	 */
	public IhtcvirtualgipssolutionHiPEApp() {
		super(new HiPEGTEngine());
	}

	/**
	 * Creates the application with HiPE.
	 * 
	 * @param workspacePath
	 *            the workspace path
	 */
	public IhtcvirtualgipssolutionHiPEApp(final String workspacePath) {
		super(new HiPEGTEngine(), workspacePath);
	}
}
