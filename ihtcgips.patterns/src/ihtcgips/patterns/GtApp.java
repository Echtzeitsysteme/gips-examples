package ihtcgips.patterns;

import org.eclipse.emf.common.util.URI;

import ihtcgips.patterns.api.PatternsHiPEApp;
import ihtcmetamodel.Hospital;

public class GtApp extends PatternsHiPEApp {

	public GtApp(final Hospital hospital) {
		super(EmoflonGtAppUtils.createTempDir().normalize().toString() + "/");
		EmoflonGtAppUtils.extractFiles(workspacePath);
		if (hospital.eResource() == null) {
			createModel(URI.createURI("model.xmi"));
			resourceSet.getResources().get(0).getContents().add(hospital);
		} else {
			resourceSet = hospital.eResource().getResourceSet();
		}
	}

}
