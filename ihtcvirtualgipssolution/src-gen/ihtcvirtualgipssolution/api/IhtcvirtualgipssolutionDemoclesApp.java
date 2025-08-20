package ihtcvirtualgipssolution.api;

import org.emoflon.ibex.gt.democles.runtime.DemoclesGTEngine;

/**
 * An application using the IhtcvirtualgipssolutionAPI with Democles.
 */
public class IhtcvirtualgipssolutionDemoclesApp extends IhtcvirtualgipssolutionApp {

	/**
	 * Creates the application with Democles.
	 */
	public IhtcvirtualgipssolutionDemoclesApp() {
		super(new DemoclesGTEngine());
	}

	/**
	 * Creates the application with Democles.
	 * 
	 * @param workspacePath
	 *            the workspace path
	 */
	public IhtcvirtualgipssolutionDemoclesApp(final String workspacePath) {
		super(new DemoclesGTEngine(), workspacePath);
	}
}
