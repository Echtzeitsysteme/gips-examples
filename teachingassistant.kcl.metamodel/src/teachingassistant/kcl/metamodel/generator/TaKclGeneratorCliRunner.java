package teachingassistant.kcl.metamodel.generator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.gson.JsonObject;

import metamodel.Department;
import teachingassistant.kcl.metamodel.export.FileUtils;
import teachingassistant.kcl.metamodel.export.ModelToJsonExporter;

public class TaKclGeneratorCliRunner extends TeachingAssistantKclGenerator {

	/**
	 * Boolean flag to enable the debug output.
	 */
	private static boolean debugOutputEnabled = false;

	/**
	 * Random seed for the generator.
	 */
	private static int randomSeed = -1;

	/**
	 * JSON input file path to load an instance from.
	 */
	private static String jsonInputPath = "./config.json";

	/**
	 * JSON output file path to save the solution to.
	 */
	private static String jsonOutputPath = "./model.json";

	/**
	 * No public instances of this class allowed.
	 */
	private TaKclGeneratorCliRunner() {
	}

	/**
	 * Main method to start the headless runner. String array of arguments will be
	 * parsed.
	 * 
	 * @param args See {@link #parseArgs(String[])}.
	 */
	public static void main(final String[] args) {
		parseArgs(args);
		new TaKclGeneratorCliRunner().execute();
	}

	/**
	 * Runs the execution of the configured scenario. This method relies on the
	 * previous parsing of arguments.
	 */
	private void execute() {
		final SimpleTaKclGenerator gen = new SimpleTaKclGenerator(randomSeed);

		// Set the parsed configuration from the read JSON file
		final GeneratorConfig config = parseJsonConfig(jsonInputPath);
		gen.NUMBER_OF_LECTURERS = config.numberOfLecturers;
		gen.LECTURERS_MINIMUM_NUMBER_OF_ASSISTANTS = config.lecturersMinimumNumberOfAssistants;
		gen.LECTURERS_MAXIMUM_NUMBER_OF_ASSISTANTS = config.lecturersMaximumNumberOfAssistants;
		gen.NUMBER_OF_ASSISTANTS = config.numberOfAssistants;
		gen.ASSISTANTS_MAXIMUM_NUMBER_OF_DAYS_PER_WEEK = config.assistantsMaximumNumberOfDaysPerWeek;
		gen.ASSISTANTS_MINIMUM_NUMBER_OF_HOURS_PER_WEEK = config.assistantsMinimumNumberOfHoursPerWeek;
		gen.ASSISTANTS_MAXIMUM_NUMBER_OF_HOURS_PER_WEEK = config.assistantsMaximumNumberOfHoursPerWeek;
		gen.ASSISTANTS_MAXIMUM_HOURS_TOTAL = config.assistantsMaximumHoursTotal;
		gen.ASSISTANTS_MINIMUM_NUMBER_OF_BLOCKED_DAYS = config.assistantsMinimumNumberOfBlockedDays;
		gen.ASSISTANTS_MAXIMUM_NUMBER_OF_BLOCKED_DAYS = config.assistantsMaximumNumberOfBlockedDays;
		gen.NUMBER_OF_TIMESLOTS_PER_WEEK = config.numberOfTimeSlotsPerWeek;
		gen.NUMBER_OF_TUTORIALS_PER_WEEK = config.numberOfTimeSlotsPerWeek;
		gen.NUMBER_OF_WEEKS = config.numberOfWeeks;

		final Department model = gen.constructModel();
		final ModelToJsonExporter exporter = new ModelToJsonExporter(model);
		exporter.modelToJson(jsonOutputPath);

		if (debugOutputEnabled) {
			System.out.println("=> Scenario generation finished.");
		}
	}

	private GeneratorConfig parseJsonConfig(final String jsonInputPath) {
		final JsonObject json = FileUtils.readFileToJson(jsonInputPath);
		return new GeneratorConfig( //
				json.getAsJsonPrimitive("number_of_lecturers").getAsInt(), //
				json.getAsJsonPrimitive("lecturers_minimum_number_of_assistants").getAsInt(), //
				json.getAsJsonPrimitive("lecturers_maximum_number_of_assistants").getAsInt(), //
				json.getAsJsonPrimitive("number_of_assistants").getAsInt(), //
				json.getAsJsonPrimitive("assistants_maximum_number_of_days_per_week").getAsInt(), //
				json.getAsJsonPrimitive("assistants_minimum_number_of_hours_per_week").getAsInt(), //
				json.getAsJsonPrimitive("assistants_maximum_number_of_hours_per_week").getAsInt(), //
				json.getAsJsonPrimitive("assistants_maximum_hours_total").getAsInt(), //
				json.getAsJsonPrimitive("assistants_minimum_number_of_blocked_days").getAsInt(), //
				json.getAsJsonPrimitive("assistants_maximum_number_of_blocked_days").getAsInt(), //
				json.getAsJsonPrimitive("number_of_time_slots_per_week").getAsInt(), //
				json.getAsJsonPrimitive("number_of_tutorials_per_week").getAsInt(), //
				json.getAsJsonPrimitive("number_of_weeks").getAsInt() //
		);
	}

	private static void parseArgs(final String[] args) {
		final Options options = new Options();

		// JSON input file to load
		final Option jsonInputFile = new Option("i", "inputjson", true, "input JSON file to load");
		jsonInputFile.setRequired(true);
		options.addOption(jsonInputFile);

		// JSON output file to write
		final Option jsonOutputFile = new Option("o", "outputjson", true, "output JSON file to store");
		jsonOutputFile.setRequired(false);
		options.addOption(jsonOutputFile);

		// Debug output enabled flag
		final Option debugOutputEnabled = new Option("d", "debug", false, "debug output flag");
		debugOutputEnabled.setRequired(false);
		options.addOption(debugOutputEnabled);

		// XMI model file path to save the output model to
		final Option randomSeed = new Option("n", "randomseed", true, "random seed for the (M)ILP solver");
		randomSeed.setRequired(false);
		options.addOption(randomSeed);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException ex) {
			System.err.println(ex.getMessage());
			formatter.printHelp("CLI parameters", options);
			System.exit(1);
		}

		// Get and save values
		if (cmd.hasOption("inputjson")) {
			jsonInputPath = cmd.getOptionValue("inputjson");
		}

		if (cmd.hasOption("outputjson")) {
			jsonOutputPath = cmd.getOptionValue("outputjson");
		}

		TaKclGeneratorCliRunner.debugOutputEnabled = cmd.hasOption("debug");
		if (cmd.hasOption("randomseed")) {
			final String randomSeedParameter = cmd.getOptionValue("randomseed");
			try {
				TaKclGeneratorCliRunner.randomSeed = Integer.valueOf(randomSeedParameter);
				if (TaKclGeneratorCliRunner.randomSeed < 0) {
					throw new IllegalArgumentException("Given random seed was negative, which is not supported.");
				}
			} catch (final Exception e) {
				throw new IllegalArgumentException("Given random seed was not an integer.");
			}
		}
	}

	public record GeneratorConfig(int numberOfLecturers, int lecturersMinimumNumberOfAssistants,
			int lecturersMaximumNumberOfAssistants, int numberOfAssistants, int assistantsMaximumNumberOfDaysPerWeek,
			int assistantsMinimumNumberOfHoursPerWeek, int assistantsMaximumNumberOfHoursPerWeek,
			int assistantsMaximumHoursTotal, int assistantsMinimumNumberOfBlockedDays,
			int assistantsMaximumNumberOfBlockedDays, int numberOfTimeSlotsPerWeek, int numberOfTutorialsPerWeek,
			int numberOfWeeks) {
	}

}
