package pta.scenario.house;

import pta.evaluation.util.EvaluationResult;
import pta.scenario.ScenarioRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.Map.entry;    

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.emoflon.gips.core.util.Observer;

public class HouseConstructionHeadless {
	
	// Model statistic headers
	public static String HEADER_ID = "id";
	public static String HEADER_TYPE = "type";
	public static String HEADER_PROJECTS = "projects";
	public static String HEADER_TASKS = "tasks";
	public static String HEADER_REQS = "requirements";
	public static String HEADER_OFFERS = "offers";
	public static String HEADER_WEEKS = "weeks";
	public static String HEADER_PERSONS = "persons";
	public static String HEADER_NODES = "model_size";
	public static String HEADER_MAPPINGS = "mappings";
	public static String HEADER_VARS = "vars";
	public static String HEADER_CONSTRAINTS = "constraints";
	public static String HEADER_OPTIMAL = "is_optimal";
	public static String HEADER_SOLVED_RATIO = "solved_ratio";
	public static String HEADER_PROJECT_RATIO = "project_ratio";
	// Time statistic headers
	// Average
	public static String HEADER_TOTAL_T = "total_time_avg";
	public static String HEADER_PM_T = "pm_time_avg";
	public static String HEADER_BUILD_T = "build_time_avg";
	public static String HEADER_BUILD_GIPS_T = "build_gips_time_avg";
	public static String HEADER_BUILD_SOLVER_T = "build_solver_time_avg";
	public static String HEADER_SOLVE_T = "solve_time_avg";
	public static String HEADER_APPLY_T = "apply_time_avg";
	// Time statistic headers
	// Maximum
	public static String HEADER_PM_T_MAX = "pm_time_max";
	public static String HEADER_BUILD_T_MAX = "build_time_max";
	public static String HEADER_BUILD_GIPS_T_MAX = "build_gips_time_max";
	public static String HEADER_BUILD_SOLVER_T_MAX = "build_solver_time_max";
	public static String HEADER_SOLVE_T_MAX = "solve_time_max";
	public static String HEADER_APPLY_T_MAX = "apply_time_max";
	// Time statistic headers
	// Minimum
	public static String HEADER_PM_T_MIN = "pm_time_min";
	public static String HEADER_BUILD_T_MIN = "build_time_min";
	public static String HEADER_BUILD_GIPS_T_MIN = "build_gips_time_min";
	public static String HEADER_BUILD_SOLVER_T_MIN = "build_solver_time_min";
	public static String HEADER_SOLVE_T_MIN = "solve_time_min";
	public static String HEADER_APPLY_T_MIN = "apply_time_min";
	// Memory statistic headers
	// Average
	public static String HEADER_PM_MEM = "pm_memory_avg";
	public static String HEADER_BUILD_MEM = "build_memory_avg";
	public static String HEADER_BUILD_GIPS_MEM = "build_gips_memory_avg";
	public static String HEADER_BUILD_SOLVER_MEM = "build_solver_memory_avg";
	public static String HEADER_SOLVE_MEM = "solve_memory_avg";
	public static String HEADER_APPLY_MEM = "apply_memory_avg";
	// Memory statistic headers
	// Maximum
	public static String HEADER_PM_MEM_MAX = "pm_memory_max";
	public static String HEADER_BUILD_MEM_MAX = "build_memory_max";
	public static String HEADER_BUILD_GIPS_MEM_MAX = "build_gips_memory_max";
	public static String HEADER_BUILD_SOLVER_MEM_MAX = "build_solver_memory_max";
	public static String HEADER_SOLVE_MEM_MAX = "solve_memory_max";
	public static String HEADER_APPLY_MEM_MAX = "apply_memory_max";
	// Memory statistic headers
	// Minimum
	public static String HEADER_PM_MEM_MIN = "pm_memory_min";
	public static String HEADER_BUILD_MEM_MIN = "build_memory_min";
	public static String HEADER_BUILD_GIPS_MEM_MIN = "build_gips_memory_min";
	public static String HEADER_BUILD_SOLVER_MEM_MIN = "build_solver_memory_min";
	public static String HEADER_SOLVE_MEM_MIN = "solve_memory_min";
	public static String HEADER_APPLY_MEM_MIN = "apply_memory_min";
	
	private static String[] CSV_COLUMNS = {
			HEADER_ID,
			HEADER_TYPE,
			HEADER_PROJECTS,
			HEADER_TASKS,
			HEADER_REQS,
			HEADER_OFFERS,
			HEADER_WEEKS,
			HEADER_PERSONS,
			HEADER_NODES,
			HEADER_MAPPINGS,
			HEADER_VARS,
			HEADER_CONSTRAINTS,
			HEADER_OPTIMAL,
			HEADER_SOLVED_RATIO,
			HEADER_PROJECT_RATIO,
			//
			HEADER_TOTAL_T,
			HEADER_BUILD_T,
			HEADER_PM_T,
			HEADER_BUILD_GIPS_T,
			HEADER_BUILD_SOLVER_T,
			HEADER_SOLVE_T,
			HEADER_APPLY_T,
			//
			HEADER_PM_T_MAX,
			HEADER_BUILD_T_MAX,
			HEADER_BUILD_GIPS_T_MAX,
			HEADER_BUILD_SOLVER_T_MAX,
			HEADER_SOLVE_T_MAX,
			HEADER_APPLY_T_MAX,
			//
			HEADER_PM_T_MIN,
			HEADER_BUILD_T_MIN,
			HEADER_BUILD_GIPS_T_MIN,
			HEADER_BUILD_SOLVER_T_MIN,
			HEADER_SOLVE_T_MIN,
			HEADER_APPLY_T_MIN,
			//
			HEADER_PM_MEM,
			HEADER_BUILD_MEM,
			HEADER_BUILD_GIPS_MEM,
			HEADER_BUILD_SOLVER_MEM,
			HEADER_SOLVE_MEM,
			HEADER_APPLY_MEM,
			//
			HEADER_PM_MEM_MAX,
			HEADER_BUILD_MEM_MAX,
			HEADER_BUILD_GIPS_MEM_MAX,
			HEADER_BUILD_SOLVER_MEM_MAX,
			HEADER_SOLVE_MEM_MAX,
			HEADER_APPLY_MEM_MAX,
			//
			HEADER_PM_MEM_MIN,
			HEADER_BUILD_MEM_MIN,
			HEADER_BUILD_GIPS_MEM_MIN,
			HEADER_BUILD_SOLVER_MEM_MIN,
			HEADER_SOLVE_MEM_MIN,
			HEADER_APPLY_MEM_MIN
			};

	
	private static CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setHeader(CSV_COLUMNS).build();
	
	public static void main(final String[] args) {
		HouseConstructionHeadless ctrl = new HouseConstructionHeadless();
		try {
			ctrl.parseArgs(args);
			ctrl.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static ScenarioRunner<?> createRunner(String type, String id) {
		switch(type) {
			case HouseConstructionBatchA.TYPE : return new HouseConstructionBatchA(id);
			case HouseConstructionBatchB.TYPE : return new HouseConstructionBatchB(id);
			case HouseConstructionBatchC.TYPE : return new HouseConstructionBatchC(id);
			case HouseConstructionBatchD.TYPE : return new HouseConstructionBatchD(id);
			case HouseConstructionBatchE.TYPE : return new HouseConstructionBatchE(id);
			case HouseConstructionIncF.TYPE : return new HouseConstructionIncF(id);
			case HouseConstructionIncG.TYPE : return new HouseConstructionIncG(id);
			default: throw new IllegalArgumentException("Unknown runner type: "+type);
		}
	}
	
	/**
	 * Checks if a file for the given path exists and throws an exception otherwise.
	 * 
	 * @param path Path to check the file existence for.
	 */
	public static void checkIfFileExists(final String path) {
		final File inputFile = new File(path);
		if (!inputFile.exists() || inputFile.isDirectory()) {
			throw new IllegalArgumentException("Input file <" + path + "> could not be found.");
		}
	}
	
	public static void writeCsvLine(final String csvPath, final String[] content) throws IOException {
		BufferedWriter out;
		// If file does not exist, write header to it
		if (Files.notExists(Path.of(csvPath))) {
			out = Files.newBufferedWriter(Paths.get(csvPath), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			final CSVPrinter printer = new CSVPrinter(out, CSV_FORMAT);
			printer.close();
		}

		out = Files.newBufferedWriter(Paths.get(csvPath), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT);
		printer.printRecord((Object[]) content);
		printer.close();
		out.close();
	}
	
	public static void resultToCSV(final String csvPath, final EvaluationResult result, final ScenarioRunner<?> runner) throws Exception {
		String[] content = new String[CSV_COLUMNS.length];
		
		setColumn(HEADER_ID, result.id(), content);
		setColumn(HEADER_TYPE, runner.getType(), content);
		setColumn(HEADER_PROJECTS, result.validator().getNumberOfProjects(), content);
		setColumn(HEADER_TASKS, result.validator().getNumberOfTasks(), content);
		setColumn(HEADER_REQS, result.validator().getNumberOfRequirements(), content);
		setColumn(HEADER_OFFERS, result.validator().getNumberOfOffers(), content);
		setColumn(HEADER_WEEKS, result.validator().getNumberOfWeeks(), content);
		setColumn(HEADER_PERSONS, result.validator().getNumberOfPersons(), content);
		setColumn(HEADER_NODES, result.validator().getModelSize(), content);
		setColumn(HEADER_MAPPINGS, result.output().getOutputs().values().iterator().next().stats().mappings(), content);
		setColumn(HEADER_VARS, result.output().getOutputs().values().iterator().next().stats().vars(), content);
		setColumn(HEADER_CONSTRAINTS, result.output().getOutputs().values().iterator().next().stats().constraints(), content);
		setColumn(HEADER_OPTIMAL, result.validator().isValid(), content);
		setColumn(HEADER_SOLVED_RATIO, result.output().optimality(), content);
		setColumn(HEADER_PROJECT_RATIO, result.validator().getSuccessRate(), content);
		
		setColumn(HEADER_PM_T, result.measurements().get("PM").avgDurationSeconds(), content);
		setColumn(HEADER_PM_T_MIN, result.measurements().get("PM").minDurationSeconds(), content);
		setColumn(HEADER_PM_T_MAX, result.measurements().get("PM").maxDurationSeconds(), content);
		setColumn(HEADER_PM_MEM, result.measurements().get("PM").avgMemoryMB(), content);
		setColumn(HEADER_PM_MEM_MIN, result.measurements().get("PM").minMemoryMB(), content);
		setColumn(HEADER_PM_MEM_MAX, result.measurements().get("PM").maxMemoryMB(), content);
		
		setColumn(HEADER_BUILD_T, result.measurements().get("BUILD").avgDurationSeconds(), content);
		setColumn(HEADER_BUILD_T_MIN, result.measurements().get("BUILD").minDurationSeconds(), content);
		setColumn(HEADER_BUILD_T_MAX, result.measurements().get("BUILD").maxDurationSeconds(), content);
		setColumn(HEADER_BUILD_MEM, result.measurements().get("BUILD").avgMemoryMB(), content);
		setColumn(HEADER_BUILD_MEM_MIN, result.measurements().get("BUILD").minMemoryMB(), content);
		setColumn(HEADER_BUILD_MEM_MAX, result.measurements().get("BUILD").maxMemoryMB(), content);
		
		setColumn(HEADER_BUILD_GIPS_T, result.measurements().get("BUILD_GIPS").avgDurationSeconds(), content);
		setColumn(HEADER_BUILD_GIPS_T_MIN, result.measurements().get("BUILD_GIPS").minDurationSeconds(), content);
		setColumn(HEADER_BUILD_GIPS_T_MAX, result.measurements().get("BUILD_GIPS").maxDurationSeconds(), content);
		setColumn(HEADER_BUILD_GIPS_MEM, result.measurements().get("BUILD_GIPS").avgMemoryMB(), content);
		setColumn(HEADER_BUILD_GIPS_MEM_MIN, result.measurements().get("BUILD_GIPS").minMemoryMB(), content);
		setColumn(HEADER_BUILD_GIPS_MEM_MAX, result.measurements().get("BUILD_GIPS").maxMemoryMB(), content);
		
		setColumn(HEADER_BUILD_SOLVER_T, result.measurements().get("BUILD_SOLVER").avgDurationSeconds(), content);
		setColumn(HEADER_BUILD_SOLVER_T_MIN, result.measurements().get("BUILD_SOLVER").minDurationSeconds(), content);
		setColumn(HEADER_BUILD_SOLVER_T_MAX, result.measurements().get("BUILD_SOLVER").maxDurationSeconds(), content);
		setColumn(HEADER_BUILD_SOLVER_MEM, result.measurements().get("BUILD_SOLVER").avgMemoryMB(), content);
		setColumn(HEADER_BUILD_SOLVER_MEM_MIN, result.measurements().get("BUILD_SOLVER").minMemoryMB(), content);
		setColumn(HEADER_BUILD_SOLVER_MEM_MAX, result.measurements().get("BUILD_SOLVER").maxMemoryMB(), content);
		
		setColumn(HEADER_SOLVE_T, result.measurements().get("SOLVE_PROBLEM").avgDurationSeconds(), content);
		setColumn(HEADER_SOLVE_T_MIN, result.measurements().get("SOLVE_PROBLEM").minDurationSeconds(), content);
		setColumn(HEADER_SOLVE_T_MAX, result.measurements().get("SOLVE_PROBLEM").maxDurationSeconds(), content);
		setColumn(HEADER_SOLVE_MEM, result.measurements().get("SOLVE_PROBLEM").avgMemoryMB(), content);
		setColumn(HEADER_SOLVE_MEM_MIN, result.measurements().get("SOLVE_PROBLEM").minMemoryMB(), content);
		setColumn(HEADER_SOLVE_MEM_MAX, result.measurements().get("SOLVE_PROBLEM").maxMemoryMB(), content);
		
		setColumn(HEADER_APPLY_T, result.measurements().get("APPLY").avgDurationSeconds(), content);
		setColumn(HEADER_APPLY_T_MIN, result.measurements().get("APPLY").minDurationSeconds(), content);
		setColumn(HEADER_APPLY_T_MAX, result.measurements().get("APPLY").maxDurationSeconds(), content);
		setColumn(HEADER_APPLY_MEM, result.measurements().get("APPLY").avgMemoryMB(), content);
		setColumn(HEADER_APPLY_MEM_MIN, result.measurements().get("APPLY").minMemoryMB(), content);
		setColumn(HEADER_APPLY_MEM_MAX, result.measurements().get("APPLY").maxMemoryMB(), content);
		
		double total_time_avg = result.measurements().get("PM").avgDurationSeconds() + 
				result.measurements().get("BUILD").avgDurationSeconds() + 
				result.measurements().get("SOLVE_PROBLEM").avgDurationSeconds() + 
				result.measurements().get("APPLY").avgDurationSeconds();
		
		setColumn(HEADER_TOTAL_T, total_time_avg, content);
		
		writeCsvLine(csvPath, content);
	}
	
	public static <T> void setColumn(String header, T value, String[] target) {
		int idx = 0;
		for(String entry : CSV_COLUMNS) {
			if(entry.equals(header)) {
				target[idx] = String.valueOf(value);
				return;
			}
			idx++;
		}
	}

	private String xmiInputPath;
	private String xmiOutputPath;
	private String scenarioID;
	private String runnerType;
	private String csvOutputPath;
	private boolean printSolution;
	
	public void run() throws Exception {
		checkIfFileExists(xmiInputPath);
		//checkIfFileExists(xmiOutputPath);
		//checkIfFileExists(csvOutputPath);
		ScenarioRunner<?> runner = createRunner(runnerType, scenarioID);
		Observer obs = Observer.getInstance();
		obs.setCurrentSeries(scenarioID);
		obs.observe("INIT", ()->runner.init(runner.getGipsModelPath(), xmiInputPath, runner.getIbexModelPath(), runner.getHiPEModelPath(), runner.getHiPEEngineFQN()));
		EvaluationResult result = runner.run(xmiOutputPath);
		if(printSolution)
			System.out.println(result);
		
		resultToCSV(csvOutputPath, result, runner);
	}
	
	/**
	 * Parses the given arguments to configure the runner.
	 * <ol>
	 * <li>#0: XMI input file path (required)</li>
	 * <li>#1: XMI output file path (required)</li>
	 * <li>#2: XMI pre-processing file path (required)</li>
	 * <li>#3: Print the found solution to console (optional)</li>
	 * </ol>
	 * 
	 * @param args Arguments to parse.
	 */
	public void parseArgs(final String[] args) {
		final Options options = new Options();

		// XMI file to load
		final Option xmiInputFile = new Option("i", "inputxmi", true, "input XMI file to load");
		xmiInputFile.setRequired(true);
		options.addOption(xmiInputFile);

		// XMI file to save
		final Option xmiOutputFile = new Option("o", "outputxmi", true, "output XMI file to save");
		xmiOutputFile.setRequired(true);
		options.addOption(xmiOutputFile);
		
		// Id or label of the evaluation scenario.
		final Option scenarioID = new Option("id", "scenarioid", true, "label of the scenario");
		scenarioID.setRequired(true);
		options.addOption(scenarioID);
		
		// Runner type, i.e., which runner type to use (e.g., BATCH-A = HouseConstructionBatchA)
		final Option runnerType = new Option("r", "runnertype", true, "runner type to use");
		runnerType.setRequired(true);
		options.addOption(runnerType);

		// CSV output path
		final Option csvOutputFile = new Option("c", "outputcsv", true, "output CSV file to save");
		csvOutputFile.setRequired(false);
		options.addOption(csvOutputFile);

		// Print solution flag
		final Option printSolutionOption = new Option("p", "printsolution", false, "print solution");
		printSolutionOption.setRequired(false);
		options.addOption(printSolutionOption);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			System.err.println(e.getMessage());
			formatter.printHelp("CLI parameters", options);
			System.exit(1);
		}

		// Get values
		xmiInputPath = cmd.getOptionValue("inputxmi");
		xmiOutputPath = cmd.getOptionValue("outputxmi");
		this.scenarioID = cmd.getOptionValue("scenarioid");
		this.runnerType = cmd.getOptionValue("runnertype");
		csvOutputPath = cmd.getOptionValue("outputcsv");
		printSolution = cmd.hasOption("printsolution");
	}
}
