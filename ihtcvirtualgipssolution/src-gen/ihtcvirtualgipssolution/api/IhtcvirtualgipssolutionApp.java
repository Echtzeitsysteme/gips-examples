package ihtcvirtualgipssolution.api;

import ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage;
import org.eclipse.emf.common.util.URI;
import org.emoflon.ibex.common.operational.IContextPatternInterpreter;
import org.emoflon.ibex.gt.api.GraphTransformationApp;

/**
 * An application using the IhtcvirtualgipssolutionAPI.
 */
public class IhtcvirtualgipssolutionApp extends GraphTransformationApp<IhtcvirtualgipssolutionAPI> {

	/**
	 * Creates the application with the given engine.
	 * 
	 * @param engine
	 *            the pattern matching engine
	 */
	public IhtcvirtualgipssolutionApp(final IContextPatternInterpreter engine) {
		super(engine);
	}

	/**
	 * Creates the application with the given engine.
	 * 
	 * @param engine
	 *            the pattern matching engine
	 * @param workspacePath
	 *            the workspace path
	 */
	public IhtcvirtualgipssolutionApp(final IContextPatternInterpreter engine, final String workspacePath) {
		super(engine, workspacePath);
	}

	@Override
	public void registerMetaModels() {
		registerMetaModel(IhtcvirtualmetamodelPackage.eINSTANCE);
	}

	@Override
	public IhtcvirtualgipssolutionAPI initAPI() {
		if (defaultResource.isPresent()) {
			return new IhtcvirtualgipssolutionAPI(engine, resourceSet, defaultResource.get(), workspacePath);
		}
		return new IhtcvirtualgipssolutionAPI(engine, resourceSet, workspacePath);
	}
	
	/**
	 * Initializes the API with a given (dynamic) IBeX pattern path (URI).
	 *
	 * @param patternPath
	 *            the (dynamic) IBeX pattern path (URI) to load the XMI file from.
	 */
	@Override
	public IhtcvirtualgipssolutionAPI initAPI(final URI patternPath) {
		return new IhtcvirtualgipssolutionAPI(engine, resourceSet, patternPath);
	}
}
