package org.gips.examples.incrementalp2p.visualization.implementation.providers;

import java.util.ArrayList;
import java.util.List;

import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConnection;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationNode;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationUpdatesDataProvider;

public class InMemoryVisualizationUpdatesDataProvider implements VisualizationUpdatesDataProvider {
	private List<VisualizationNode> removedNodes;
	private List<VisualizationConnection> updatedEdges;
	private List<VisualizationNode> additionalNodes;
	private List<VisualizationConnection> additionalEdges;

	InMemoryVisualizationUpdatesDataProvider() {
		removedNodes = new ArrayList<>();
		updatedEdges = new ArrayList<>();
		additionalNodes = new ArrayList<>();
		additionalEdges = new ArrayList<>();
	}

	@Override
	public List<VisualizationNode> getRemovdNodes() {
		return removedNodes;
	}

	@Override
	public List<VisualizationConnection> getUpdatedEdges() {
		return updatedEdges;
	}

	@Override
	public List<VisualizationNode> getAdditionalNodes() {
		return additionalNodes;
	}

	@Override
	public List<VisualizationConnection> getAdditionalEdges() {
		return additionalEdges;
	}

	@Override
	public void setRemovdNodes(final List<VisualizationNode> removedNodes) {
		this.removedNodes = removedNodes;
	}

	@Override
	public void setUpdatedEdges(final List<VisualizationConnection> updatedEdges) {
		this.updatedEdges = updatedEdges;
	}

	@Override
	public void setAdditionalNodes(final List<VisualizationNode> additionalNodes) {
		this.additionalNodes = additionalNodes;
	}

	@Override
	public void setAdditionalEdges(final List<VisualizationConnection> additionalEdges) {
		this.additionalEdges = additionalEdges;
	}

}
