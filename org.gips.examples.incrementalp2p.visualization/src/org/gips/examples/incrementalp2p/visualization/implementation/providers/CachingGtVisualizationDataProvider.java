package org.gips.examples.incrementalp2p.visualization.implementation.providers;

import java.util.List;

import org.gips.examples.incrementalp2p.visualization.contracts.CachingVisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConnection;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationNode;

import com.google.inject.Inject;

public class CachingGtVisualizationDataProvider implements CachingVisualizationDataProvider {
	List<VisualizationNode> nodes;
	List<VisualizationConnection> connections;

	@Inject
	GtApiVisualizationDataProvider provider;

	@Override
	public void cache() {
		setNodes();
		setConnections();
	}

	private void setConnections() {
		connections = provider.getConnections();
	}

	private void setNodes() {
		nodes = provider.getNodes();
	}

	@Override
	public List<VisualizationNode> getNodes() {
		if (nodes == null) {
			setNodes();
		}
		return nodes;
	}

	@Override
	public List<VisualizationConnection> getConnections() {
		if (connections == null) {
			setConnections();
		}
		return connections;
	}
	
}
