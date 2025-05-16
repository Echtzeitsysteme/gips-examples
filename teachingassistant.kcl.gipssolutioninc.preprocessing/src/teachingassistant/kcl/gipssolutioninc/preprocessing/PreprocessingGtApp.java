package teachingassistant.kcl.gipssolutioninc.preprocessing;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.ibex.gt.api.GraphTransformationRule;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import metamodel.MetamodelPackage;
import metamodel.TAAllocation;
import teachingassistant.kcl.gipssolutioninc.preprocessing.api.PreprocessingAPI;
import teachingassistant.kcl.gipssolutioninc.preprocessing.api.PreprocessingHiPEApp;
import teachingassistant.kcl.gipssolutioninc.preprocessing.api.matches.MakeAssignmentEdgePreviousMatch;

/**
 * This eMoflon::IBeX-GT app can be used to run all pre-processing rules of the
 * defined GT rule set on a given model.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class PreprocessingGtApp extends PreprocessingHiPEApp {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(PreprocessingGtApp.class.getName());

	/**
	 * Global limit of the number of GT rule applications per GT rule.
	 */
	private static final int GT_RULE_APPLICATION_LIMIT = 1_000;

	/**
	 * Creates a new instance of the pre-processing GT app. The given `xmiFilePath`
	 * will be used as input and output file path.
	 * 
	 * @param xmiFilePath Input and output file path.
	 */
	public PreprocessingGtApp(final String xmiFilePath) {
		super(EmoflonGtAppUtils.createTempDir().normalize().toString() + "/");
		Objects.requireNonNull(xmiFilePath);
		EmoflonGtAppUtils.extractFiles(workspacePath);

		// Load model from given XMI file path
		TAAllocation model = null;
		try {
			model = loadModel(xmiFilePath);
		} catch (final IOException e) {
			logger.warning("IOException occurred while reading the input XMI file." + e.getMessage());
			System.exit(1);
		}

		// Proceed with the app creation
		if (model.eResource() == null) {
			createModel(URI.createURI(xmiFilePath));
			resourceSet.getResources().get(0).getContents().add(model);
		} else {
			resourceSet = model.eResource().getResourceSet();
		}

		// Configure logging
		logger.setUseParentHandlers(false);
		final ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {
			@Override
			public String format(final LogRecord record) {
				Objects.requireNonNull(record, "Given log entry was null.");
				return record.getMessage() + System.lineSeparator();
			}
		});
		logger.addHandler(handler);
	}

	/**
	 * Executes the GT rules of this app according to the configuration.
	 */
	public void run() {
		// Create the API object
		final PreprocessingAPI api = this.initAPI();

		// Apply all GT rule matches until the specified limit hits
		applyMatches(api.makeAssignmentEdgePrevious());

		// Persist model to XMI path
		try {
			save();
		} catch (final IOException e) {
			logger.warning("IOException occurred while writing the output XMI file." + e.getMessage());
			System.exit(1);
		}

		// Terminate the eMoflon::IBeX-GT (HiPE) API
		api.terminate();
	}

	//
	// Utility methods.
	//

	/**
	 * Applies the given GT rule until it either does not have any more matches or
	 * the global GT rule application limit was hit.
	 * 
	 * @param rule GT rule to apply.
	 */
	private void applyMatches(final GraphTransformationRule<MakeAssignmentEdgePreviousMatch, ?> rule) {
		final Collection<MakeAssignmentEdgePreviousMatch> matches = rule.findMatches();
		int counter = 0;
		for (final var match : matches) {
			if (counter >= GT_RULE_APPLICATION_LIMIT) {
				break;
			}
			rule.apply(match);
			counter++;
		}

		logger.info(this.getClass().getSimpleName() + ": I migrated " + counter + " assigned TA objects.");
	}

	/**
	 * Loads the used model as XMI file from the given file path.
	 * 
	 * @param path File path from which the model should be read as XMI file.
	 * @throws IOException If an IOException occurs during read, this method will
	 *                     pass it.
	 */
	private TAAllocation loadModel(final String path) throws IOException {
		Objects.requireNonNull(path);

		final ResourceSet rs = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		final Resource model = rs.getResource(URI.createFileURI(path), true);
		return (TAAllocation) model.getContents().get(0);
	}

	/**
	 * Saves the used model as XMI file.
	 * 
	 * @throws IOException If an IOException occurs during read, this method will
	 *                     pass it.
	 */
	private void save() throws IOException {
		Objects.requireNonNull(getModel());
		getModel().getResources().get(0).save(null);
	}

}
