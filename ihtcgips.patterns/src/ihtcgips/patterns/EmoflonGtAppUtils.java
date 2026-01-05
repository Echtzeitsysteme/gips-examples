package ihtcgips.patterns;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ihtcgips.patterns.api.PatternsAPI;
import ihtcgips.patterns.utils.Utilities;

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
			tempDir = Utilities.createDir("emoflonTmp");
		}
		return tempDir;
	}

	/**
	 * Extracts the specified 'ibex-patterns.xmi' file if not already present.
	 *
	 * @param workspacePath The path of the workspace.
	 */
	public static void extractFiles(final String workspacePath) {
		final File target = new File(workspacePath + PatternsAPI.patternPath);
		if (target.exists()) {
			return;
		}
		try (final InputStream is = PatternsAPI.class.getResourceAsStream("/ihtcgips/patterns/api/ibex-patterns.xmi")) {
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