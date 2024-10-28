package pta.scenario;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import PersonTaskAssignments.PersonTaskAssignmentsPackage;

public abstract class ScenarioRunner<API extends GipsEngineAPI<?,?>> {
	protected API api;
	final public String name;
	
	public ScenarioRunner(final String name) {
		this.name = name;
	}
	
	public abstract API newAPI();
	
	public void init(final String file) {
		api = newAPI();
		URI uri = URI.createFileURI(file);
		api.init(uri);
	}
	
	public void init(final EObject model) {
		api = newAPI();
		
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(PersonTaskAssignmentsPackage.eNS_URI, PersonTaskAssignmentsPackage.eINSTANCE);
		Resource r = rs.createResource(URI.createFileURI("temp.xmi"));
		r.getContents().add(model);
		api.init(rs);
	}
	
//	public void init(final EObject model) {
//		api = newAPI();
//		URI uri = URI.createFileURI(file);
//		api.init(uri);
//	}
	
	public abstract EvaluationResult run();
	
}