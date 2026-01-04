package pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.naming.OperationNotSupportedException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import pipeline.utils.*;

public abstract class AbstractPipeline{

	/**
	 * Contains all instantiated pipeline Stages with their respective configurations
	 */
	protected List<PipelineStage> pipelineStages;
	
	/**
	 * If true, the pipeline will by default print more detailed information.
	 * This can be overwritten for each pipeline stage 
	 */
	protected boolean verbose;
	
	/**
	 * If true, the pipeline will by default build the problem in parallel.
	 * This can be overwritten for each pipeline stage 
	 */
	protected boolean parallelBuild;
	
	/**
	 * Name of the xmi input file.
	 */
	protected String instance;
	
	/**
	 * Default instance path of the XMI folder.
	 */
	protected String instanceFolder;
	
	/**
	 * Default instance path of the solved XMI folder.
	 */
	protected String outputFolder;
	
	/**
	 * Project folder location.
	 */
	protected String projectFolder = System.getProperty("user.dir");
	
	/**
	 * Random seed for the (M)ILP solver.
	 */
	protected int randomSeed = 0;

	/**
	 * Time limit for the (M)ILP solver.
	 */
	protected int timeLimit = 30;

	/**
	 * Number of threads for the (M)ILP solver.
	 */
	protected int threads = 0;
	
	/**
	 * If true, the application of the GT rules of the GIPSL specification will only
	 * be simulated by manually written Java code instead of actually applying GT
	 * rule matches with eMoflon::IBeX-GT.
	 * To use Java application the method applySolutionNoGt must be implemented
	 */
	protected boolean applicationNoGt = false;
	
	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(AbstractPipeline.class.getName());
	
	public AbstractPipeline(boolean verbose, boolean parallelBuild){
		this.verbose = verbose;
		this.parallelBuild = parallelBuild;
		
		pipelineStages = new ArrayList<PipelineStage>();
		
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
		logger.info("Pipeline created!");
	}
	
	/**
	 * Needs to be called before creating any pipeline stages and sets the instance path.
	 * Calculates both input and output folder for the pipeline stages.
	 * If no default solution folder exists a new directory is created at the parent directory of the 
	 * instance file named "pipeline_solutions".
	 * @param instancePath compete path to the xmi model instance.
	 */
	public void setInstancePath(String instancePath) {
		if(!FileUtils.checkIfFileExists(instancePath)) {
			throw new IllegalArgumentException("File <" + instancePath + "> could not be found.");
		}
		
		this.instance = instancePath.substring(instancePath.lastIndexOf("/"));
		
		if(!this.instance.substring(this.instance.lastIndexOf(".")).equals("xmi")) {
			throw new IllegalArgumentException("Specified file must be must be a valid xmi.");
		}
		
		this.instanceFolder = instancePath.substring(0, instancePath.lastIndexOf("/") + 1);
		this.outputFolder = instanceFolder + "/../pipeline_solutions";
		FileUtils.prepareFolder(this.outputFolder);
	}
	
	/**
	 * Creates a new pipeline stage and calculates all necessary paths. 
	 * The global milp configuration is used for the new stage.
	 * @param gipsApi Api for the current stage, which specifies which specification is executed.
	 * @return The index of the just created pipeline stage, by which it can be 
	 * 		   further configured and executed
	 */
	public int setupNewStage(GipsEngineAPI<?, ?> gipsApi) {
		return setupNewStage(gipsApi, randomSeed, timeLimit, threads);
	}
	
	
	/**
	 * Creates a new pipeline stage and calculates all necessary paths. 
	 * @param gipsApi Api for the current stage, which specifies which specification is executed.
	 * @param parameter Path of the parameter file, being the primary means to configure 
	 * 	      Gurobi for the current stage
	 * @param callback Path of the callback file
	 * @return The index of the just created pipeline stage, by which it can be 
	 * 		   further configured and executed
	 */
	public int setupNewStage(GipsEngineAPI<?, ?> gipsApi, String parameterPath, String callbackPath) {
		Objects.requireNonNull(gipsApi);
		Objects.requireNonNull(instance);
		
		String inputPath = calculateInputPathForNextStage(gipsApi);
		String outputPath = calculateOutputPathForNextStage(gipsApi);
		
		PipelineStage stage = new PipelineStage(gipsApi, inputPath, parameterPath, callbackPath, outputPath, null, null, null);
		
		pipelineStages.add(stage);

		return pipelineStages.size() - 1;
	}
	
	/**
	 * Creates a new pipeline stage and calculates all necessary paths. 
	 * Directly takes the most relevant milp configurations instead of parameter and callback files.
	 * @param gipsApi Api for the current stage, which specifies which specification is executed.
	 * @param randomSeed Random seed for the MILP solver to be used for the new stage
	 * @param timeLimit Time limit of the MILP solver for the new stage
	 * @param threads Amount of threads to be used for the new stage
	 * @return
	 */
	public int setupNewStage(GipsEngineAPI<?, ?> gipsApi, int randomSeed, int timeLimit, int threads) {
		Objects.requireNonNull(gipsApi);
		Objects.requireNonNull(instance);
		
		String inputPath = calculateInputPathForNextStage(gipsApi);
		String outputPath = calculateOutputPathForNextStage(gipsApi);
		
		PipelineStage stage = new PipelineStage(gipsApi, inputPath, "", "", outputPath, randomSeed, timeLimit, threads);
		
		pipelineStages.add(stage);

		return pipelineStages.size() - 1;
	}
	
	
	public String executeStage(final int stage) {
		
		// TODO auch schon methoden für preproc ... hinzufügen mit OperationNotSupportedException
		PipelineStage currentStage = pipelineStages.get(stage);
		GipsEngineAPI<?, ?> gipsApi = currentStage.gipsApi();
		
		checkIfFileExists(currentStage.inputPath());
		
		if (verbose) {
			logger.info("=> Start Stage " + stage);
			logger.info("=> Initializing GIPS Api: " + gipsApi.toString());
		}
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, currentStage.inputPath());
		
		// Set GIPS configuration parameters from this object
		this.setGipsConfig(currentStage);

		//
		// Run GIPS solution
		//
		
		this.buildAndSolve(gipsApi);

		if (applicationNoGt) {
			try {
				applySolutionNoGt(gipsApi);
			} catch (OperationNotSupportedException e) {
				logger.info("applySolutionNoGt(gipsApi) is not implemented! The solution was applied via the GT rules." + stage);
				applySolution(gipsApi);
			}
		} else {
			applySolution(gipsApi);
		}
		
		gipsSave(currentStage);

		//
		// The end
		//

		gipsApi.terminate();
		
		if (verbose) {
			logger.info("=> Finished Stage " + stage);
		}
		
		return currentStage.outputPath();
	}
	
	/**
	 * Calculates the input path 
	 * @param gipsApi Api of the current Stage
	 * @return The complete path of the input file for the pipeline stage. 
	 */
	private String calculateInputPathForNextStage(GipsEngineAPI<?, ?> gipsApi) {
		String filename;
		if(pipelineStages.size() > 0) {
			filename = pipelineStages.getLast().outputPath();
		}else {
			filename = instanceFolder + instance;
		}
		
		return filename;
	}
	
	/**
	 * Calculates the output path 
	 * @param gipsApi Api of the current Stage
	 * @return The complete path of the output file for the pipeline stage. 
	 */
	private String calculateOutputPathForNextStage(GipsEngineAPI<?, ?> gipsApi) {
		int stageNumber = this.pipelineStages.size() - 1;
		return outputFolder + instance.substring(0, instance.lastIndexOf(".xmi")) + "_solved_stage_" + stageNumber + ".xmi";
	}
	
	/**
	 * Checks if a file for the given path exists and throws an exception otherwise.
	 * 
	 * @param path Path to check the file existence for.
	 */
	private void checkIfFileExists(final String path) {
		Objects.requireNonNull(path);

		final File xmiInputFile = new File(path);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("File <" + path + "> could not be found.");
		}
	}
	
//	/**
//	 * Converts the two given time stamps (tick and tock) from nano seconds to
//	 * elapsed time in seconds.
//	 * 
//	 * @param tick First time stamp.
//	 * @param tock Second time stamp.
//	 * @return Elapsed time between tick and tock in seconds.
//	 */
//	protected double tickTockToElapsedSeconds(final long tick, final long tock) {
//		if (tick < 0 || tock < 0) {
//			throw new IllegalArgumentException("Given tick or tock was below zero.");
//		}
//		return 1.0 * (tock - tick) / 1_000_000_000;
//	}
	
	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * GIPS API object.
	 * 
	 * @param gipsApi GIPS API object to get all mapping information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	private void applySolution(final GipsEngineAPI<?, ?> gipsApi) {
		Objects.requireNonNull(gipsApi);

		// Apply found solution
		gipsApi.applyAllNonZeroMappings();

		// Update the pattern matcher after all rule applications once
		gipsApi.update();
	}
	
	/**
	 * This method does not utilize the GT engine built-in to GIPS but rather 
	 * manipulates the model directly.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 * @throws OperationNotSupportedException 
	 */
	private void applySolutionNoGt(final GipsEngineAPI<?, ?> gipsApi) throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
		// TODO
	}
	
	/**
	 * Loads the given problem instance from the given format (json, ...) if necessary and 
	 * saves it as an xmi. 
	 * Needs to be implemented if the problem is not given as an xmi.
	 * @throws OperationNotSupportedException
	 */
	public void importXMI(final String inputJsonPath, final String outputXmiPath) throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
	}
	
	/**
	 * Exports the xmi solution into the necessary format (json, ...) 
	 * Needs to be implemented if the solution is required in a separate format.
	 * @throws OperationNotSupportedException
	 */
	public void exportSolution(final String xmiOutputPath, final String jsonOutputPath) throws OperationNotSupportedException {
		throw new OperationNotSupportedException();
	}
	
	/**
	 * Builds and solves the ILP problem for the given GIPS API. Also prints the
	 * objective value to the console and throws an error if no solution could be
	 * found.
	 * 
	 * @param gipsApi GIPS API to build and solve the ILP problem for.
	 * @param verbose If true, the method will print some more information about the
	 *                objective value.
	 * @return Returns the objective value.
	 */
	private double buildAndSolve(final GipsEngineAPI<?, ?> gipsApi) {
		Objects.requireNonNull(gipsApi);

		gipsApi.buildProblemTimed(true, parallelBuild);
		final SolverOutput output = gipsApi.solveProblemTimed();
		if (output.solutionCount() == 0) {
			gipsApi.terminate();
			logger.warning("No solution found. Aborting.");
			throw new InternalError("No solution found!");
		}
		if (verbose) {
			logger.info("=> Objective value: " + output.objectiveValue());
			final Map<String, IMeasurement> measurements = new LinkedHashMap<>(
					Observer.getInstance().getMeasurements("Eval"));
			Observer.getInstance().getMeasurements("Eval").clear();
			logger.info("PM: " + measurements.get("PM").maxDurationSeconds() + "s.");
			logger.info("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds() + "s.");
			logger.info("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds() + "s.");
			logger.info("BUILD: " + measurements.get("BUILD").maxDurationSeconds() + "s.");
			logger.info("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds() + "s.");
		}
		return output.objectiveValue();
	}
	
	
	/**
	 * Saves the result of a run of a given GIPS API to a given path as XMI file.
	 * 
	 * @param gipsApi GIPS API to save results from.
	 * @param path    (XMI) path to save the results to.
	 */
	private void gipsSave(final PipelineStage stage) {
		Objects.requireNonNull(stage);
		logger.info("Saving GIPS output XMI file to: " + stage.outputPath());
		try {
			stage.gipsApi().saveResult(stage.outputPath());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the private GIPS API configuration parameters from this object to the
	 * actual GIPS API.
	 * 
	 * @param gipsApi GIPS API to set the configuration parameters for.
	 */
	private void setGipsConfig(final PipelineStage stage) {
		Objects.requireNonNull(stage);
		GipsEngineAPI<?, ?> gipsApi = stage.gipsApi();
		
		gipsApi.getSolverConfig().setRandomSeed(stage.randomSeed());
		if (timeLimit != -1) {
			gipsApi.getSolverConfig().setTimeLimit(stage.timeLimit());
		}
		gipsApi.getSolverConfig().setThreadCount(stage.threads());
		if (stage.callbackPath() != null) {
			gipsApi.getSolverConfig().setEnableCallbackPath(true);
			gipsApi.getSolverConfig().setCallbackPath(stage.callbackPath());
		}
		if (stage.parameterPath() != null) {
			gipsApi.getSolverConfig().setParameterPath(stage.parameterPath());
		}
	}
	
	/**
	 * 
	 * @param randomSeed New value for the default random seed
	 */
	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}
	
	/**
	 * 
	 * @param timeLimit New value for the default time limit
	 */
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	/**
	 * 
	 * @param threads New value for the default amount of threads used
	 */
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	/**
	 * 
	 * @param outputFolder Folder where the solved xmis are saved
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	
	/**
	 * 
	 * @param applicationNoGt If true, the application of the GT rules of 
	 * the GIPSL specification will only be simulated by manually written Java code
	 */
	public void setapplicationNoGt(boolean applicationNoGt) {
		this.applicationNoGt = applicationNoGt;
	}
	
	
	// Erstellen von Pipelinestufen 
		// Muss die Api Erstellung übernehmen 
	
	// Andere Möglichkeit als Interface 
		// Am Ende dann 2 Implementierte Varianten (virtual) und allgemeine Form
	
	// Ziel: AM Ende im Runner nur noch Parameter initialisieren und an die Pipeline übergeben 
	// Dann kann über einen Methodenaufruf eine Pipelinestufe ausgeführt werden. Diese muss die konkrete 
	// GIPS Api übergeben bekommen 
	// Die Pipeline übernimmt das Logging -> Erstellen im Konstruktor und intern mit der Stufe mitzählen
	// Die Stufe bekommt die individuellen Konfigurationen und einen Eingabepfad. Das heißt das Model muss als xmi bereits vorliegen
	// Preprocessing muss nur einmal pro Pipeline gemacht werden. Aber Regeln soerweitern dass schon Knoten selektiert sein können.
	// Ca so: 
	
	
	// inputpath = jsonToModel(...)
	// Ihtcpipeline pipeline = new Ihtcpipeline(verbose, parallel); // Instanzname benötigt?
	
	// Key = pipeline.setupNewStage(Api, inputpath, preproc, postproc, gtapplication, callback, parameter) -> Muss aus der APi die ganzen Pfade berechnen
	// Möglichkeit Parameter anzupassen
	// outputPath = pipeline.runStage()
	
	// Wo die Eingabe dateien liegen muss konfiguriert werden 
	
	// Die Standart Variante hat keine Parameter für preproc / postproc / gtapplication, ...
	
	public abstract void run();
	
}
