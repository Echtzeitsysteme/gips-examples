package org.gips.examples.incrementalp2p.repository.implementation;

import java.io.File;
import java.io.FileOutputStream;

import org.emoflon.ibex.gt.api.GraphTransformationAPI;
import org.gips.examples.incrementalp2p.common.Guard;

public abstract class AApiSaver {
	protected abstract GraphTransformationAPI saveApi();

	public void save(final String directory, final String fileName) {
		var file = fileWithDirectoryAssurance(directory, fileName + ".xmi");

		try {
			file.createNewFile();
			FileOutputStream f = new FileOutputStream(file, false);

			saveApi().getModel().getResources().get(0).save(f, null);

		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while saving Scenario");
		}
	}

	private static File fileWithDirectoryAssurance(final String directory, final String filename) {
		Guard.againstMissingDirectory(directory);

		if (directory == "") {
			return new File(filename);
		}

		return new File(directory + "/" + filename);
	}
	
}
