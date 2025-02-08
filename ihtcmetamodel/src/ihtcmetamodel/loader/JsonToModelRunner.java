package ihtcmetamodel.loader;

import java.io.IOException;

import ihtcmetamodel.Hospital;

/**
 * TODO.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class JsonToModelRunner {

	/**
	 * TODO.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		new JsonToModelRunner().run(args);
	}

	/**
	 * TODO.
	 */
	private JsonToModelRunner() {
	}

	/**
	 * TODO.
	 * 
	 * @param args
	 */
	private void run(final String[] args) {
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
		final Hospital model = loader.getModel();

		// Write XMI to file
		try {
			FileUtils.save(model, outputPath);
		} catch (final IOException e) {
			e.printStackTrace();
			throw new InternalError(e.getLocalizedMessage());
		}
	}

}
