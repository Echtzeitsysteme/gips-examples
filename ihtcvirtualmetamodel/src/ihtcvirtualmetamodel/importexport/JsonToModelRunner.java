package ihtcvirtualmetamodel.importexport;

import java.io.IOException;
import java.util.Objects;

import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.utils.FileUtils;

/**
 * This runner can be used to load a given JSON model and save it as XMI model.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class JsonToModelRunner {

	/**
	 * Main method to start the runner.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);
		new JsonToModelRunner().run(args);
	}

	/**
	 * No instances of this class allowed.
	 */
	protected JsonToModelRunner() {
	}

	/**
	 * Uses the `JsonToModelLoader` to read a given JSON file (first argument is the
	 * input path), convert it to an XMI file, and write this file to the output
	 * path (second argument is the output path).
	 * 
	 * @param args first argument = input path, second argument = output path.
	 */
	private void run(final String[] args) {
		Objects.requireNonNull(args);

		if (args == null || args.length == 0) {
			throw new IllegalArgumentException("Given args were null or empty.");
		}

		if (args.length != 2) {
			throw new IllegalArgumentException( //
					"You must specific two parameters: " //
							+ "1. parameter is the input .json path; " //
							+ "2. parameter is the output .xmi path.");
		}

		final String inputPath = args[0];
		final String outputPath = args[1];

		if (inputPath == null || inputPath.isBlank()) {
			throw new IllegalArgumentException("Given input path was empty.");
		}

		if (outputPath == null || outputPath.isBlank()) {
			throw new IllegalArgumentException("Given output path was empty.");
		}

		// Load model from file
		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputPath);
		final Root model = loader.getModel();

		// Write XMI to file
		try {
			FileUtils.prepareFolder(outputPath.substring(0, outputPath.lastIndexOf("/")));
			FileUtils.save(model, outputPath);
		} catch (final IOException e) {
			e.printStackTrace();
			throw new InternalError(e.getLocalizedMessage());
		}
	}

}
