package org.gips.examples.incrementalp2p.visualization.contracts;

import java.util.List;

public interface VisualizationDataProvider {
	List<VisualizationNode> getNodes();

	List<VisualizationConnection> getConnections();
	
}
