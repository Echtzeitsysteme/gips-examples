package pta.scenario;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import PersonTaskAssignments.PersonTaskAssignmentsPackage;
import hipe.engine.config.HiPEPathOptions;
import pta.evaluation.util.EvaluationResult;

public abstract class ScenarioRunner<API extends GipsEngineAPI<?, ?>> {
	protected API api;
	final public String name;

	public ScenarioRunner(final String name) {
		this.name = name;
	}

	public abstract API newAPI();

	public abstract String getType();

	public abstract String getGipsModelPath();

	public abstract String getIbexModelPath();

	public abstract String getHiPEModelPath();

	public abstract String getHiPEEngineFQN();

	public void init(final String file) {
		api = newAPI();
		URI uri = URI.createFileURI(file);
		api.init(uri);
	}

	public void init(String gipsModel, String inputModel, String ibexPatternModel, String hipePatternModel,
			String hipeEngineFQN) {
		api = newAPI();
		URI gipsUri = URI.createFileURI(gipsModel);
		URI inputUri = URI.createFileURI(inputModel);
		URI ibexUri = URI.createFileURI(ibexPatternModel);
		URI hipeUri = URI.createFileURI(hipePatternModel);
		HiPEPathOptions hiPEPathOptions = HiPEPathOptions.getInstance();
		hiPEPathOptions.setNetworkPath(hipeUri);
		hiPEPathOptions.setEngineClassName(hipeEngineFQN);
		api.init(gipsUri, inputUri, ibexUri);
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

	public abstract EvaluationResult run() throws IOException;

	public abstract EvaluationResult run(String outputFile) throws IOException;

}