package org.gips.examples.incrementalp2p.visualization.implementation.mappers;

import org.gips.examples.incrementalp2p.common.CommonConstants;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConfiguration;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConnection;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationNode;

import com.google.inject.Inject;

public class VisJsMapper {
	@Inject
	private VisualizationConfiguration config;

	private static final String arrowsConfig = "arrows: { to: { enabled:true, type: 'arrow'}}";

	public String createNode(final VisualizationNode node) {
		var color = toNodeColor(node);
		var value = getJsProperty("value", Integer.toString(node.value()), config.showNodeBandiwdth());

		return String.format("{id: '%s', label: '%s', color: %s %s},", node.id(), node.name(), color, value);
	}

	public String createEdge(final VisualizationConnection connection) {
		var id = connection.serverId() + connection.clientId();
		var serverId = connection.serverId();
		var clientId = connection.clientId();
		var value = getJsProperty("value", Integer.toString(connection.width()), config.showConnectionBandiwdth());
		var label = getJsProperty("label", connection.label(), !connection.label().equals(""));

		var arrow = config.useArrows() ? ", " + arrowsConfig : "";
		var color = toEdgeColor(connection);

		return String.format("{id: '%s', from: '%s', to: '%s' %s %s %s %s },", id, serverId, clientId, value, arrow,
				color, label);
	}

	private String getJsProperty(final String name, final String value, final boolean condition) {
		if (!condition)
			return "";

		return String.format(", %s: '%s'", name, value);
	}

	private String toEdgeColor(final VisualizationConnection connection) {
		return String.format(", color: {color: %s, opacity: %s}", UIConstants.EdgeColor, connection.opacity());
	}

	public static String toNodeColor(final VisualizationNode node) {
		if (isRoot(node)) {
			return UIConstants.RootColor;
		} else if (node.isRelayClient()) {
			return UIConstants.RelayColor;
		}

		return UIConstants.ClientColor;
	}

	private static boolean isRoot(final VisualizationNode node) {
		return node.id().equals(CommonConstants.RootName);
	}
	
}
