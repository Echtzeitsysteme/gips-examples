package ihtcvirtualmetamodel.importexport;

import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;

import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.utils.FileUtils;

/**
 * This runner can be used to load a given XMI file and export it as JSON file.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class ModelToJsonRunner {

	/**
	 * Main method to start the runner.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		Objects.requireNonNull(args);
		new ModelToJsonRunner().run(args);
	}

	/**
	 * No instances of this class allowed.
	 */
	protected ModelToJsonRunner() {
	}

	/**
	 * Uses the `ModelToJsonExporter` to read a given XMI file (first argument is
	 * the input path), convert it to an JSON file, and write this file to the
	 * output path (second argument is the output path).
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
							+ "1. parameter is the input .xmi path; " //
							+ "2. parameter is the output .json path.");
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
		final Resource r = FileUtils.loadModel(inputPath);
		final Root model = (Root) r.getContents().get(0);

		final ModelToJsonExporter exporter = new ModelToJsonExporter(model);
		exporter.modelToJson(outputPath, true);
	}

}
