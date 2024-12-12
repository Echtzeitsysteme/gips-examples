package org.gips.examples.incrementalp2p.visualization.contracts;

public record VisualizationConnection(String serverId, String clientId, String label, int width, double opacity) {
	public VisualizationConnection(String serverId, String clientId, int value) {
		this(serverId, clientId, "", value, 1.0);
	}

	public VisualizationConnection(String serverId, String clientId, String label) {
		this(serverId, clientId, label, 1, 1.0);
	}
	
}
