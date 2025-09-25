package ihtcvirtualpreprocessing;

import java.io.IOException;
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

import hipe.engine.config.HiPEPathOptions;
import ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.utils.FileUtils;
import ihtcvirtualpreprocessing.api.IhtcvirtualpreprocessingAPI;
import ihtcvirtualpreprocessing.api.IhtcvirtualpreprocessingHiPEApp;

/**
 * This eMoflon::IBeX-GT app can be used to run all pre-processing rules of the
 * defined GT rule set on a given model.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class PreprocessingGtApp extends IhtcvirtualpreprocessingHiPEApp {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(PreprocessingGtApp.class.getName());

	/**
	 * Global limit of the number of GT rule applications per GT rule.
	 */
	private static final int GT_RULE_APPLICATION_LIMIT = 40_000;

	// TODO: This field can be removed
	/**
	 * XMI model input file path. This value will be used to read the input model
	 * and write the output model to.
	 */
	@Deprecated
	private final String xmiInputFilePath;

	/**
	 * XMI model output file path. This value will be used to write the output model
	 * to.
	 */
	private final String xmiOutputFilePath;

	/**
	 * Creates a new instance of the pre-processing GT app. The given
	 * `xmiInputFilePath` will be used as input as well as output file path.
	 * 
	 * @param xmiInputFilePath Input file path.
	 */
	@Deprecated
	public PreprocessingGtApp(final String xmiInputFilePath) {
		this(xmiInputFilePath, xmiInputFilePath);
	}

	/**
	 * Creates a new instance of the pre-processing GT app. The given
	 * `xmiInputFilePath` will be used as input file path. The given
	 * `xmiOutputFilePath` will be used as output file path.
	 * 
	 * @param xmiInputFilePath  Input file path.
	 * @param xmiOutputFilePath Output file path.
	 */
	public PreprocessingGtApp(final String xmiInputFilePath, final String xmiOutputFilePath) {
		super(EmoflonGtAppUtils.createTempDir().normalize().toString() + "/");
		Objects.requireNonNull(xmiInputFilePath);
		Objects.requireNonNull(xmiOutputFilePath);
		EmoflonGtAppUtils.extractFiles(workspacePath);

		// Load model from given XMI file path
		Root hospital = null;
		try {
			hospital = loadModel(xmiInputFilePath);
		} catch (final IOException e) {
			logger.warning("IOException occurred while reading the input XMI file." + e.getMessage());
			System.exit(1);
		}

		// Proceed with the app creation
		if (hospital.eResource() == null) {
			createModel(URI.createURI(xmiInputFilePath));
			resourceSet.getResources().get(0).getContents().add(hospital);
		} else {
			resourceSet = hospital.eResource().getResourceSet();
		}

		this.xmiInputFilePath = xmiInputFilePath;
		this.xmiOutputFilePath = xmiOutputFilePath;

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
		final IhtcvirtualpreprocessingAPI api = setup( //
				"./ihtcvirtualpreprocessing/hipe/engine/hipe-network.xmi", //
				"ihtcvirtualpreprocessing.hipe.engine.HiPEEngine", //
				"./ihtcvirtualpreprocessing/api/ibex-patterns.xmi" //
		);

		// Apply all GT rule matches until the specified limit hits
		// New GT rules (that should be applied) must be added here
		applyMatches(api.preprocessOccupantsFirstWorkload(), GT_RULE_APPLICATION_LIMIT);
		applyMatches(api.preprocessOccupantsWorkload(), GT_RULE_APPLICATION_LIMIT);
		applyMatches(api.assignNurseToRoom(), GT_RULE_APPLICATION_LIMIT);
		applyMatches(api.assignSurgeonToOt(), GT_RULE_APPLICATION_LIMIT);
		applyMatches(api.fixOperationDayOpTime(), GT_RULE_APPLICATION_LIMIT);
		applyMatches(api.fixOperationDayCapacity(), GT_RULE_APPLICATION_LIMIT);
		applyMatches(api.assignPatientToRoom(), GT_RULE_APPLICATION_LIMIT);
		applyMatches(api.extendPatientStay(), GT_RULE_APPLICATION_LIMIT);

		// Persist model to XMI path
		try {
			logger.info("Started writing the XMI file.");
			final Resource res = api.getModel().getResources().get(0);
			Objects.requireNonNull(res);
			FileUtils.save((Root) res.getContents().get(0), xmiOutputFilePath);
		} catch (final IOException e) {
			logger.warning("IOException occurred while writing the output XMI file." + e.getMessage());
			System.exit(1);
		}

		// Terminate the eMoflon::IBeX-GT (HiPE) API
		api.terminate();
		HiPEPathOptions.getInstance().resetNetworkPath();
		HiPEPathOptions.getInstance().resetEngineClassName();
	}

	//
	// Utility methods.
	//

	/**
	 * Initializes the virtual pre-processing API depending on the context this
	 * program runs in, which could either be as JAR file or within Eclipse.
	 * 
	 * @param hipeNetworkXmiPath  HiPE network XMI file path to load.
	 * @param hipeEngineClassname HiPE engine class name to configure.
	 * @param ibexPatternXmiPath  IBeX pattern XMI file path to load.
	 * @return Initialized `IhtcvirtualpreprocessingAPI` eMoflon::IBeX-GT API.
	 */
	public IhtcvirtualpreprocessingAPI setup(final String hipeNetworkXmiPath, final String hipeEngineClassname,
			final String ibexPatternXmiPath) {
		Objects.requireNonNull(hipeNetworkXmiPath);
		Objects.requireNonNull(hipeEngineClassname);
		Objects.requireNonNull(ibexPatternXmiPath);

		final boolean runAsJar = FileUtils.checkIfFileExists(ibexPatternXmiPath) //
				&& FileUtils.checkIfFileExists(hipeNetworkXmiPath);
		if (!runAsJar) {
			return this.initAPI();
		} else {
			HiPEPathOptions.getInstance().setNetworkPath( //
					URI.createFileURI(hipeNetworkXmiPath) //
			);
			HiPEPathOptions.getInstance().setEngineClassName( //
					hipeEngineClassname //
			);
			return this.initAPI(URI.createFileURI(ibexPatternXmiPath));
		}
	}

	/**
	 * Applies the given GT rule until it either does not have any more matches or
	 * the global GT rule application limit was hit.
	 * 
	 * @param rule  GT rule to apply.
	 * @param limit Maximum number of GT rule applications.
	 * @param api
	 */
	private void applyMatches(final GraphTransformationRule<?, ?> rule, final int limit) {
		logger.info(this.getClass().getSimpleName() + ": Initial number of matches of GT rule " + rule.getPatternName()
				+ " " + rule.countMatches() + ".");
		int counter = 0;
		while (rule.isApplicable()) {
			if (counter >= limit) {
				logger.info(this.getClass().getSimpleName() + ": GT rule application limit of " + limit + " reached.");
				break;
			}
			rule.apply();
			counter++;
		}

		logger.info(this.getClass().getSimpleName() + ": I applied the GT rule " + rule.getPatternName() + " " + counter
				+ " times.");
		logger.info(this.getClass().getSimpleName() + ": Remaining number of matches of GT rule "
				+ rule.getPatternName() + " " + rule.countMatches() + ".");
	}

	/**
	 * Loads the used model as XMI file from the given file path.
	 * 
	 * @param path File path from which the model should be read as XMI file.
	 * @throws IOException If an IOException occurs during read, this method will
	 *                     pass it.
	 */
	private Root loadModel(final String path) throws IOException {
		Objects.requireNonNull(path);

		final ResourceSet rs = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(IhtcvirtualmetamodelPackage.eNS_URI, IhtcvirtualmetamodelPackage.eINSTANCE);
		final Resource model = rs.getResource(URI.createFileURI(path), true);
		return (Root) model.getContents().get(0);
	}

}
