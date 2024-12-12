package org.gips.examples.incrementalp2p.visualization.contracts;

import java.io.File;

public interface GraphVisualizer {
	public void createGraph(String path, String id);

	public File getGraphFile(String path, String id);
	
}
