package teachingassistant.uni.recomp.preprocessing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import teachingassistant.uni.recomp.preprocessing.api.PreprocessingAPI;

/**
 * Utility class for the eMoflon GT app.
 */
public class EmoflonGtAppUtils {

	/**
	 * Private constructor forbids instantiation of objects.
	 */
	private EmoflonGtAppUtils() {
	}

	/**
	 * Temporary directory (path).
	 */
	private static Path tempDir;

	/**
	 * Creates the temporary directory if it does not exist before.
	 *
	 * @return Path of the created temporary directory.
	 */
	public static Path createTempDir() {
		if (tempDir == null) {
			try {
				tempDir = Files.createTempDirectory("eMoflonTmp");
			} catch (final IOException e) {
				throw new RuntimeException("Unable to create temporary directory for eMoflon", e);
			}
		}
		return tempDir;
	}

	/**
	 * Extracts the specified 'ibex-patterns.xmi' file if not already present.
	 *
	 * @param workspacePath The path of the workspace.
	 */
	public static void extractFiles(final String workspacePath) {
		Objects.requireNonNull(workspacePath);

		final File target = new File(workspacePath + PreprocessingAPI.patternPath);
		if (target.exists()) {
			return;
		}
		try (final InputStream is = PreprocessingAPI.class
				.getResourceAsStream("/teachingassistant/uni/recomp/preprocessing/api/ibex-patterns.xmi")) {
			target.getParentFile().mkdirs();
			if (is == null) {
				throw new IllegalStateException("ibex-patterns are missing from the resources");
			}
			Files.copy(is, target.toPath());
			target.deleteOnExit();
		} catch (final IOException e) {
			throw new IllegalStateException("Something went wrong while copying emoflon resources", e);
		}
	}

}
