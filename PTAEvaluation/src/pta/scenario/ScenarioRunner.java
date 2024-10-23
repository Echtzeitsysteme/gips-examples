package pta.scenario;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.emoflon.gips.core.api.GipsEngineAPI;

public abstract class ScenarioRunner<API extends GipsEngineAPI<?,?>> {
	protected API api;
	
	public abstract API newAPI();
	
	public void init(final String file) {
		api = newAPI();
		URI uri = URI.createFileURI(file);
		api.init(uri);
	}
	
//	public void init(final EObject model) {
//		api = newAPI();
//		URI uri = URI.createFileURI(file);
//		api.init(uri);
//	}
	
	public abstract void run();
	
}
