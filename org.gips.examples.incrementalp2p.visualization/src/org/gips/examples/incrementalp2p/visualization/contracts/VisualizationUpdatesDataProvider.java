package org.gips.examples.incrementalp2p.visualization.contracts;

import java.util.List;

public interface VisualizationUpdatesDataProvider {
	List<VisualizationNode> getRemovdNodes();

	List<VisualizationConnection> getUpdatedEdges();

	List<VisualizationNode> getAdditionalNodes();

	List<VisualizationConnection> getAdditionalEdges();

	void setRemovdNodes(List<VisualizationNode> removedNodes);

	void setUpdatedEdges(List<VisualizationConnection> updatedEdges);

	void setAdditionalNodes(List<VisualizationNode> additionalNodes);

	void setAdditionalEdges(List<VisualizationConnection> additionalEdges);

}
