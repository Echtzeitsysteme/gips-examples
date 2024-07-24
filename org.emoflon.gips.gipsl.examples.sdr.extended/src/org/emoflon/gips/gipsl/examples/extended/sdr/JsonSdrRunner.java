package org.emoflon.gips.gipsl.examples.extended.sdr;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.sdr.extended.api.gips.ExtendedGipsAPI;
import org.emoflon.gips.gipsl.examples.sdrmodel.generator.SDRModelGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hipe.engine.config.HiPEPathOptions;
import sdrmodel.Block;
import sdrmodel.Flow;
import sdrmodel.Job;
import sdrmodel.Root;

/**
 * JSON-based GIPS SDR example runner.
 */
public class JsonSdrRunner {

	/**
	 * XMI file that gets generated from the JSON input.
	 */
	public static final String JSON_MODEL_URI = "./json-model.xmi";

	/**
	 * XMI file that is the output of the GIPS run.
	 */
	public static final String JSON_MODEL_RESULT_URI = "./json-model-result.xmi";

	/**
	 * Method to run the JSON-based SDR runner. Input arguments must contain the
	 * JSON file location (input file).
	 * 
	 * @param args Array of string arguments to parse.
	 */
	public static void main(final String[] args) {
		final FilePaths paths = parseArgs(args);
		convertJsonToXmiModel(paths.input);
		final Collection<SolutionMapping> mappings = runGips();
		convertSolutionToJson(paths.output, mappings);
		System.out.println("GIPS run finished.");
		System.exit(0);
	}

	/**
	 * Converts the solution mappings to the specified JSON output schema and writes
	 * it to the given path.
	 * 
	 * @param outputPath JSON output file path.
	 * @param mappings   Collection of the solution mappings to get the solutions
	 *                   from.
	 */
	private static void convertSolutionToJson(final String outputPath, final Collection<SolutionMapping> mappings) {
		final List<SolutionMapping> block2Thread = mappings.stream().toList();
		final List<Block2ThreadMapping> block2ThreadJson = new ArrayList<>();
		block2Thread.forEach(m -> {
			final int blockId = Integer.valueOf(((Block) m.guest).getName());
			final String threadId = ((sdrmodel.Thread) m.host).getName();
			block2ThreadJson.add(new Block2ThreadMapping(blockId, threadId));
		});

		// Create and write JSON file
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			final FileWriter writer = new FileWriter(outputPath);
			gson.toJson(new OutputRecord(block2ThreadJson), writer);
			writer.flush();
			writer.close();
		} catch (final JsonIOException | IOException e) {
			System.err.println("File " + outputPath + " could not be written.");
			System.exit(1);
		}
	}

	/**
	 * Runs the GIPS calculation. Uses the `JSON_MODEL_URI` as input model and saves
	 * the result as XMI file to `JSON_MODEL_RESULT_URI`.
	 * 
	 * @return Collection of all chosen solution mappings.
	 */
	private static Collection<SolutionMapping> runGips() {
		HiPEPathOptions.setNetworkPath(URI.createFileURI("./org/emoflon/gips/gipsl/examples/sdr/extended/hipe/engine/hipe-network.xmi"));
		HiPEPathOptions.setEngineClassName("org.emoflon.gips.gipsl.examples.sdr.extended.hipe.engine.HiPEEngine");
		
		// Create the API
		final ExtendedGipsAPI api = new ExtendedGipsAPI();
		api.init(URI.createFileURI("./org/emoflon/gips/gipsl/examples/sdr/extended/api/gips/gips-model.xmi"), //
				URI.createFileURI(JSON_MODEL_URI), //
				URI.createFileURI("./org/emoflon/gips/gipsl/examples/sdr/extended/api/ibex-patterns.xmi"));

		api.buildILPProblem(true);
		final ILPSolverOutput output = api.solveILPProblem();
		System.out.println("Solver status: " + output.status());
		System.out.println("Objective value: " + output.objectiveValue());

		final Collection<SolutionMapping> allMappings = new HashSet<SolutionMapping>();

		api.getB2t().applyNonZeroMappings().forEach(m -> {
			if (m.isPresent()) {
				allMappings.add(new SolutionMapping( //
						m.get().getBlock(), //
						m.get().getThread() //
				));
			}
		});
		api.getF2i().applyNonZeroMappings();
		api.getF2t().applyNonZeroMappings();
		api.getUsedThread().applyNonZeroMappings();

		try {
			api.saveResult(JSON_MODEL_RESULT_URI);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		api.terminate();

		return allMappings;
	}

	/**
	 * Loads the JSON file provided as inputPath and constructs a XMI model instance
	 * according to the input data. The XMI model instance will be saved to
	 * `JSON_MODEL_URI`.
	 * 
	 * @param inputPath Path as string to load the JSON file from.
	 */
	private static void convertJsonToXmiModel(final String inputPath) {
		// Load JSON model/configuration file
		String jsonData = null;
		try {
			jsonData = new String(Files.readAllBytes(Paths.get(inputPath)));
		} catch (final IOException e) {
			System.err.println("File " + inputPath + " could not be read.");
			System.exit(1);
		}

		final JsonElement outerJsonElement = JsonParser.parseString(jsonData);
		final JsonObject outerJsonObject = outerJsonElement.getAsJsonObject();

		// Attributes
		final int cores = outerJsonObject.get("cores").getAsInt();
		final int threadPerCores = outerJsonObject.get("threadsPerCore").getAsInt();
		final double interTCF = outerJsonObject.get("interThreadCommunicationFactor").getAsDouble();

		// Arrays
		final JsonArray blocks = outerJsonObject.get("blocks").getAsJsonArray();
		final JsonArray flows = outerJsonObject.get("flows").getAsJsonArray();

		final SDRModelGenerator gen = new SDRModelGenerator(0);
		SDRModelGenerator.initFileSystem();
		gen.generateSimpleCPUModel(cores, threadPerCores);
		final String jobName = "job";
		final Job job = gen.addJob(jobName, 1);

		final Map<Integer, Block> modelBlocks = new HashMap<Integer, Block>();

		// Blocks
		blocks.forEach(b -> {
			final JsonObject blockObject = b.getAsJsonObject();
			final String name = String.valueOf(blockObject.get("id").getAsInt());
			final double complexity = blockObject.get("complexity").getAsDouble();
			final double inToOut = blockObject.get("inToOut").getAsDouble();
			modelBlocks.put(blockObject.get("id").getAsInt(), gen.addBlock(job, name, complexity, inToOut));
		});

		final Set<Flow> modelFlows = new HashSet<Flow>();

		// Flows
		flows.forEach(f -> {
			final JsonObject flowObject = f.getAsJsonObject();
			final int sourceId = flowObject.get("sourceId").getAsInt();
			final int targetId = flowObject.get("targetId").getAsInt();

			// Correct flow rate will be set later on
			modelFlows.add(gen.addFlow(job, modelBlocks.get(sourceId), modelBlocks.get(targetId), 1));
		});

		// Set input rate for the first Block to 1
		final Block firstBlock = findFirstBlock(modelBlocks, modelFlows);
		firstBlock.setInputRate(1);

		// Set output rate for the first Block
		firstBlock.setOutputRate(firstBlock.getInputRate() * firstBlock.getOutputRateMultiplier());

		// Calculate all output/flow rates beginning with the first block
		propagateRatesFromBlock(firstBlock);

		// Generate root element and save it as XMI file
		final Root root = gen.generate();
		root.setInterThreadCommunicationFactor(interTCF);
		try {
			SDRModelGenerator.save(root, JSON_MODEL_URI);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates all following rates for a given Block b and all of its outgoing
	 * flows. Can be called recursively to propagate all rates for a whole network.
	 * 
	 * This method adds every incoming flow of a block and multiplies it with the
	 * block's in-to-out rate.
	 * 
	 * Please notice that this method is unable to calculate flows if the whole
	 * graph is not contiguous.
	 * 
	 * @param b Block to start the calculations from.
	 */
	private static void propagateRatesFromBlock(final Block b) {
		if (!b.getInputs().isEmpty()) {
			final double inputSum = b.getInputs().stream().map(flow -> flow.getRate()).reduce(0.0,
					(sum, rate) -> sum + rate);
			b.setInputRate(inputSum);
			b.setOutputRate(inputSum * b.getOutputRateMultiplier());
		}

		b.getOutputs().forEach(f -> {
			f.setRate(b.getOutputRate());
			propagateRatesFromBlock(f.getTarget());
		});
	}

	/**
	 * Finds the first block for all given model blocks and model flows. I.e., this
	 * method searches for the first occurrence for a block that does not have any
	 * incoming flows.
	 * 
	 * @param modelBlocks All model blocks.
	 * @param modelFlows  All model flows.
	 * @return First found block that does not have any incoming flows.
	 */
	private static Block findFirstBlock(final Map<Integer, Block> modelBlocks, final Set<Flow> modelFlows) {
		// Get all target IDs/names
		final Set<String> targetIds = new HashSet<String>();
		final Iterator<Flow> flowIt = modelFlows.iterator();
		while (flowIt.hasNext()) {
			final Flow f = flowIt.next();
			targetIds.add(f.getTarget().getName());
		}

		// Find the block that is not contained in target IDs/names
		final Iterator<Integer> blockIt = modelBlocks.keySet().iterator();
		while (blockIt.hasNext()) {
			final Block b = modelBlocks.get(blockIt.next());
			if (!targetIds.contains(b.getName())) {
				return b;
			}
		}

		return null;
	}

	/**
	 * Parses the given array of string arguments to the JSON input and output path.
	 * 
	 * @param args Array of string arguments.
	 * @return JSON input and output paths as record.
	 */
	private static FilePaths parseArgs(final String[] args) {
		final Options options = new Options();
		final Option jsonInputPath = new Option("i", "input", true, "JSON input file path to use");
		final Option jsonOutputPath = new Option("o", "output", true, "JSON output file path to use");
		jsonInputPath.setRequired(true);
		jsonOutputPath.setRequired(true);
		options.addOption(jsonInputPath);
		options.addOption(jsonOutputPath);

		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();

		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("cli parameters", options);
			System.exit(1);
		}

		// Return paths
		return new FilePaths(cmd.getOptionValue("input"), cmd.getOptionValue("output"));
	}

	//
	// Utility types
	//

	/**
	 * Input and output paths combined into one record.
	 */
	public record FilePaths(String input, String output) {
	}

	/**
	 * Record as solution mapping for a given mapping from a guest to a host
	 * EObject. This was used to combine more than one possible object type.
	 */
	private record SolutionMapping(EObject guest, EObject host) {
	}

	/**
	 * Record for a block to thread mapping.
	 */
	private record Block2ThreadMapping(int blockId, String threadId) {
	}

	/**
	 * Output record that packages the block2thread list.
	 */
	private record OutputRecord(List<Block2ThreadMapping> block2Thread) {
	}

}
