package org.gips.examples.incrementalp2p.visualization.implementation.mappers;

import org.gips.examples.incrementalp2p.repository.contracts.models.ClientModel;
import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConnection;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationNode;

public class Mapper {
	public static VisualizationNode toVisualizationNode(final ClientModel x) {
		return new VisualizationNode(x.id(), x.id(), x.isRelayClient(), (int) x.sendBandiwdth());
	}

	public static VisualizationConnection toVisualizationConnection(final ConnectionModel x) {
		return new VisualizationConnection(x.server(), x.client(), (int) x.bandwidth());
	}
	
}
