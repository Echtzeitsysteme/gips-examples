package pta.scenario.house;

import pta.evaluation.util.EvaluationResult;
import pta.scenario.ScenarioRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
	
	public static Entry<Integer, String> HEADER_ID = entry(0, "id");
	public static Entry<Integer, String> HEADER_TYPE = entry(1, "type");
	public static Entry<Integer, String> HEADER_PROJECTS = entry(2, "projects");
	public static Entry<Integer, String> HEADER_TASKS = entry(3, "tasks");
	public static Entry<Integer, String> HEADER_REQS = entry(4, "requirements");
	public static Entry<Integer, String> HEADER_OFFERS = entry(5, "offers");
	public static Entry<Integer, String> HEADER_WEEKS = entry(6, "weeks");
	public static Entry<Integer, String> HEADER_PERSONS = entry(7, "persons");
	public static Entry<Integer, String> HEADER_TOTAL_T = entry(8, "total_time");
	public static Entry<Integer, String> HEADER_PM_T = entry(9, "pm_time");
	public static Entry<Integer, String> HEADER_BUILD_GIPS_T = entry(10, "build_gips_time");
	public static Entry<Integer, String> HEADER_BUILD_SOLVER_T = entry(11, "build_solver_time");
	public static Entry<Integer, String> HEADER_SOLVE_T = entry(12, "solve_time");
	public static Entry<Integer, String> HEADER_APPLY_T = entry(13, "apply_time");
	
	private static Map<Integer, String> CSV_COLUMNS = Map.ofEntries(
			HEADER_ID,
			HEADER_TYPE,
			HEADER_PROJECTS,
			HEADER_TASKS,
			HEADER_REQS,
			HEADER_OFFERS,
			HEADER_WEEKS,
			HEADER_PERSONS,
			HEADER_TOTAL_T,
			HEADER_PM_T,
			HEADER_BUILD_GIPS_T,
			HEADER_BUILD_SOLVER_T,
			HEADER_SOLVE_T,
			HEADER_APPLY_T
	);
	
	private static CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setHeader(getHeader()).build();
	
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
	
	private static String[] getHeader() {
		String[] header = new String[CSV_COLUMNS.size()];
		for(var entry : CSV_COLUMNS.entrySet()) {
			header[entry.getKey()] = entry.getValue();
		}
		return header;
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
		String[] content = new String[CSV_COLUMNS.size()];
		
		content[HEADER_ID.getKey()] = result.id();
		content[HEADER_TYPE.getKey()] = runner.getType();
		content[HEADER_PROJECTS.getKey()] = String.valueOf(result.validator().getNumberOfProjects());
		content[HEADER_TASKS.getKey()] = String.valueOf(result.validator().getNumberOfTasks());
		content[HEADER_REQS.getKey()] = String.valueOf(result.validator().getNumberOfRequirements());
		content[HEADER_OFFERS.getKey()] = String.valueOf(result.validator().getNumberOfOffers());
		content[HEADER_WEEKS.getKey()] = String.valueOf(result.validator().getNumberOfWeeks());
		content[HEADER_PERSONS.getKey()] = String.valueOf(result.validator().getNumberOfPersons());
		
//		double init = result.measurements().get("INIT").durationSeconds();
//		double build = result.measurements().get("BUILD").durationSeconds();
//		double build_pm =  result.measurements().get("PM").durationSeconds();
//		double build_gips =  result.measurements().get("BUILD_GIPS").durationSeconds();
//		double build_solver =  result.measurements().get("BUILD_SOLVER").durationSeconds();
//		double solve = result.measurements().get("SOLVE_PROBLEM").durationSeconds();
//		double total = init + build + solve;
//		
//		content[HEADER_TOTAL_T.getKey()] = String.valueOf(total);
//		content[HEADER_PM_T.getKey()] = String.valueOf(build_pm);
//		content[HEADER_BUILD_GIPS_T.getKey()] = String.valueOf(build_gips);
//		content[HEADER_BUILD_SOLVER_T.getKey()] = String.valueOf(build_solver);
//		content[HEADER_SOLVE_T.getKey()] = String.valueOf(solve);
		
		writeCsvLine(csvPath, content);
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
