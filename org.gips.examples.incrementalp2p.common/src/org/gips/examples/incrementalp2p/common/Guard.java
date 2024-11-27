package org.gips.examples.incrementalp2p.common;

import java.io.File;

public class Guard {
	
	public static void againstMissingDirectory(final String directory) {
		final File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
}
