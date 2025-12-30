package teachingassistant.uni.batch.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.core.gt.GipsPatternMapper;
import org.emoflon.gips.core.gt.PatternMatch2MappingSorter;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

import metamodel.TaAllocation;
import teachingassistant.uni.batch.api.gips.BatchGipsAPI;
import teachingassistant.uni.batch.api.gips.mapper.TaToOccurrenceMapper;
import teachingassistant.uni.batch.api.matches.AssignTaMatch;
import teachingassistant.uni.batch.api.matches.FindTaUnavailableSessionMatch;
import teachingassistant.uni.metamodel.export.FileUtils;
import teachingassistant.uni.metamodel.export.JsonToModelImporter;
import teachingassistant.uni.metamodel.export.ModelToJsonExporter;
import teachingassistant.uni.metamodel.validator.TeachingAssistantUniValidator;
import teachingassistant.uni.utils.AbstractGipsTeachingAssistantRunner;

public class TaBatchRunner extends AbstractGipsTeachingAssistantRunner {

	public static void main(final String[] args) {
		new TaBatchRunner().run();
	}

	public TaBatchRunner() {
		super();
	}

	public void run() {
		FileUtils.checkIfFileExists(inputPath);
		final long start = System.nanoTime();

		//
		// Load an XMI model
		//

		log("=> Start JSON model loader.");

		JsonToModelImporter.main(new String[] { inputPath, instancePath });

		final long modelLoadedTime = System.nanoTime();
		log("Runtime model load: " + tickTockToSeconds(start, modelLoadedTime) + "s.");

		//
		// Initialize GIPS API
		//

		log("=> Start GIPS.");
		Observer.getInstance().setCurrentSeries("Eval");
		final long gipsStart = System.nanoTime();
		final BatchGipsAPI gipsApi = new BatchGipsAPI();
		log("GIPS init.");
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, instancePath);
		// enableTracing(gipsApi);
		final long gipsInitDone = System.nanoTime();
		log("Runtime GIPS init: " + tickTockToSeconds(gipsStart, gipsInitDone) + "s.");

		// Set GIPS configuration parameters from this object
		setGurobiVerbose(gipsApi, verbose);
		setGipsConfig(gipsApi);

		// Workaround for broken NACs
		gipsApi.setMatchSorter(new PatternMatch2MappingSorter() {
			@Override
			public <M extends GraphTransformationMatch<M, ?>> List<M> sort(GipsPatternMapper<?, M, ?> mapper,
					List<M> matches) {
				// If the mapper is not an instance of our mapper of interest, return its
				// matches as they are.
				if (!(mapper instanceof TaToOccurrenceMapper)) {
					return matches;
				}

				// Get NAC matches from eMoflon
				final Collection<FindTaUnavailableSessionMatch> nacs = gipsApi.getEMoflonAPI()
						.findTaUnavailableSession().findMatches(false);

				// Build index for NAC matches (for faster look-ups). We only need to create an
				// index for the `TA` and `entry` node of the NAC's matches.
				final Map<Object, Set<FindTaUnavailableSessionMatch>> localIndex = new HashMap<>();
				for (final FindTaUnavailableSessionMatch nacMatch : nacs) {
					// TA
					if (!localIndex.containsKey(nacMatch.getTa())) {
						localIndex.put(nacMatch.getTa(), new HashSet<FindTaUnavailableSessionMatch>());
					}
					localIndex.get(nacMatch.getTa()).add(nacMatch);

					// Entry
					if (!localIndex.containsKey(nacMatch.getEntry())) {
						localIndex.put(nacMatch.getEntry(), new HashSet<FindTaUnavailableSessionMatch>());
					}
					localIndex.get(nacMatch.getEntry()).add(nacMatch);
				}

				// Check for every match of the mapper in question, if there is at least one
				// corresponding match of the NAC. If there is no NAC's match, the match of the
				// mapper in question can be retained.
				final List<M> filteredMatches = new ArrayList<>();
				for (final M match : matches) {
					final AssignTaMatch typedMatch = (AssignTaMatch) match;
					boolean noNacFound = true;
					// NAC can only exist if both the TA and the entry were indexed beforehand
					if (localIndex.containsKey(typedMatch.getTa()) && localIndex.containsKey(typedMatch.getEntry())) {
						// Entry
						final Set<FindTaUnavailableSessionMatch> nacCandidates = new HashSet<>();
						nacCandidates.addAll(localIndex.get(typedMatch.getEntry()));
						// TA
						nacCandidates.retainAll(localIndex.get(typedMatch.getTa()));
						noNacFound = nacCandidates.isEmpty();
					}

					if (noNacFound) {
						filteredMatches.add(match);
					}
//					else {
//						logger.info("=> filtered match: " + typedMatch.getTa() + "; " + typedMatch.getEntry());
//					}
				}
				return filteredMatches;
			}
		});

		log("Start GIPS update.");
		gipsApi.update();
		final long gipsUpdateDone = System.nanoTime();
		log("Runtime GIPS update: " + tickTockToSeconds(gipsInitDone, gipsUpdateDone) + "s.");

		//
		// Build and solve the ILP problem
		//

		buildAndSolve(gipsApi);
		final long gipsSolvingDone = System.nanoTime();
		log("Runtime GIPS build + solve: " + tickTockToSeconds(gipsUpdateDone, gipsSolvingDone) + "s.");

		//
		// Apply the solution
		//

		applySolution(gipsApi);
		final long gipsApplyDone = System.nanoTime();
		log("Runtime GIPS apply: " + tickTockToSeconds(gipsSolvingDone, gipsApplyDone) + "s.");

		//
		// Save output XMI file
		//

		gipsSave(gipsApi, gipsOutputPath);
		final long gipsSaveDone = System.nanoTime();
		log("Runtime GIPS save: " + tickTockToSeconds(gipsApplyDone, gipsSaveDone) + "s.");

		//
		// Model Validation
		//

		log("=> Start TA university validator.");
		TeachingAssistantUniValidator.FILE_PATH = gipsOutputPath;
		TeachingAssistantUniValidator.verbose = verbose;
		TeachingAssistantUniValidator.main(null);

		//
		// Verify continuity solution + print TA employment rating of the solution
		//

		log("=> Start continuity verification.");
		final int continuity = new ContinuityVariableValdidator().verifyContinuity(gipsApi);
		final int employmentRating = new TaApprovalObjectiveCalculator().calculate(gipsApi);

		// Objective statistics
		log("---------------------------------------");
		log("=> Objective value(s):");
		log("\tEmployment rating value: " + employmentRating);
		log("\tContinuity value:        " + continuity);
		log("\tOverall objective value: " + (continuity + employmentRating));
		log("---------------------------------------");

		final long afterValidator = System.nanoTime();
		log("Validator runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(gipsSaveDone, afterValidator)
				+ "s.");

		//
		// Export
		//

		if (outputPath != null && !outputPath.isBlank()) {
			log("=> Start JSON export.");
			final Resource model = FileUtils.loadModel(gipsOutputPath);
			final ModelToJsonExporter exporter = new ModelToJsonExporter((TaAllocation) model.getContents().get(0));
			exporter.modelToJson(outputPath);
			final long exportDone = System.nanoTime();
			log("Export runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(afterValidator, exportDone)
					+ "s.");
		}

		//
		// The end
		//

		gipsApi.terminate();

		final Map<String, IMeasurement> measurements = new LinkedHashMap<>(
				Observer.getInstance().getMeasurements("Eval"));
		Observer.getInstance().getMeasurements("Eval").clear();
		log("=> GIPS observer measurements:");
		log("\tPM: " + measurements.get("PM").maxDurationSeconds() + "s.");
		log("\tBUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds() + "s.");
		log("\tBUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds() + "s.");
		log("\tBUILD_TOTAL: " + measurements.get("BUILD").maxDurationSeconds() + "s.");
		log("\tSOLVE_MILP: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds() + "s.");

		final long end = System.nanoTime();
		log("Total runtime: " + AbstractGipsTeachingAssistantRunner.tickTockToSeconds(start, end) + "s.");
		log("=> Finished.");
		System.exit(0);
	}

	/**
	 * Sets the private GIPS API configuration parameters from this object to the
	 * actual GIPS API.
	 * 
	 * @param gipsApi GIPS API to set the configuration parameters for.
	 */
	private void setGipsConfig(final BatchGipsAPI gipsApi) {
		Objects.requireNonNull(gipsApi);

		if (callbackPath != null) {
			gipsApi.getSolverConfig().setEnableCallbackPath(true);
			gipsApi.getSolverConfig().setCallbackPath(callbackPath);
		}
		if (parameterPath != null) {
			gipsApi.getSolverConfig().setParameterPath(parameterPath);
		}
	}

	/**
	 * Enables/Disables the verbose flag for the Gurobi solver. Please notice that
	 * this value might get overwritten by the Gurobi parameter loading.
	 * 
	 * @param gipsApi GIPS API to set the Gurobi verbose for.
	 * @param verbose If true, the Gurobi solver will print verbose information.
	 */
	private void setGurobiVerbose(final BatchGipsAPI gipsApi, final boolean verbose) {
		gipsApi.getSolverConfig().setEnableOutput(verbose);
	}

}
