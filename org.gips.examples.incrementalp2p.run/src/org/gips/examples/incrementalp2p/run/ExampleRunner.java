package org.gips.examples.incrementalp2p.run;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.TimeAggregator;
import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.distribution.contracts.ConnectionLog;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementalNodeDistributionEngine;
import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;
import org.gips.examples.incrementalp2p.repository.contracts.models.ClientModel;
import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;
import org.gips.examples.incrementalp2p.visualization.contracts.CachingVisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.contracts.GraphVisualizer;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConnection;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationNode;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationUpdatesDataProvider;
import org.gips.examples.incrementalp2p.visualization.implementation.mappers.Mapper;

import com.google.inject.Inject;

public class ExampleRunner {
	final static Logger logger = Logger.getLogger(ExampleRunner.class);
	private static final String NodeName = "NodeDistribution";
	private static final String Folder = "src-sim";
	private static final String RelativeFolder = "." + File.separator + Folder;

	@Inject
	P2PNetworkRepository repository;
	@Inject
	IncrementalNodeDistributionEngine nodeDistributionEngine;
	@Inject
	GraphVisualizer visualizer;
	@Inject
	VisualizationUpdatesDataProvider visualizationUpdatesDataProvider;
	@Inject
	CachingVisualizationDataProvider cachingVisualizationDataProvider;
	@Inject
	ConnectionLog connectionLog;

	public void run(final List<WaitingClient> clients, final List<WaitingClient> additionalClients,
			final boolean openBrowser) {
		repository.init().save(RelativeFolder, "Init");
		incrementalNodeDistribution(clients);
		removeRelayClientAndRedistribute();
		incrementalNodeDistributionForAdditionalClients(additionalClients);
		if (openBrowser) {
			visualizer.createGraph(RelativeFolder, NodeName);
			openHtmlFileInBrowser();
		}
	}

	private void incrementalNodeDistribution(final List<WaitingClient> clients) {
		nodeDistributionEngine.distributeNodes(clients).save(RelativeFolder, "IncrementalNodeDistribution");

		// For UI: Use First Incremental distr. as first view --> Cache network data
		cachingVisualizationDataProvider.cache();
	}

	private void removeRelayClientAndRedistribute() {
		var removedClients = repository.removeRelayClientsAndAttachOrphansAsWaiting(1);

		// Redistribute
		connectionLog.clear();
		nodeDistributionEngine.distributeNodes().save(Folder, "IncrementalNodeDistribution_RemovedClients");

		// For UI: Save Nodes & Edges for first update
		var edges = connectionLog.getLog().stream().map(this::toVisualisationConnection).collect(Collectors.toList());

		visualizationUpdatesDataProvider.setRemovdNodes(toVisualisationNodes(removedClients));
		visualizationUpdatesDataProvider.setUpdatedEdges(edges);
	}

	private void incrementalNodeDistributionForAdditionalClients(final List<WaitingClient> clients) {
		connectionLog.clear();

		nodeDistributionEngine.distributeNodes(clients).save(RelativeFolder,
				"IncrementalNodeDistribution_AdditionalClients");

		// For UI: Save Nodes & Edges for second update
		var clientIds = clients.stream().map(x -> x.id()).collect(Collectors.toList());
		var nodes = getCurrentNodeAsVisualizationNode(clientIds);
		var edges = connectionLog.getLog().stream().map(this::toVisualisationConnection).collect(Collectors.toList());

		visualizationUpdatesDataProvider.setAdditionalNodes(nodes);
		visualizationUpdatesDataProvider.setAdditionalEdges(edges);
	}

	private void openHtmlFileInBrowser() {
		try {
			var htmlFile = visualizer.getGraphFile(Folder, NodeName);
			Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (final IOException e) {
			logger.error("Error while opening simulation", e);
			e.printStackTrace();
		}
	}

	private List<VisualizationNode> getCurrentNodeAsVisualizationNode(final List<String> nodeIds) {
		var currentClients = repository.getClients(nodeIds);
		return toVisualisationNodes(currentClients);
	}

	private static List<VisualizationNode> toVisualisationNodes(List<ClientModel> currentClients) {
		return currentClients.stream().map(Mapper::toVisualizationNode).collect(Collectors.toList());
	}

	private VisualizationConnection toVisualisationConnection(ConnectionModel x) {
		return new VisualizationConnection(x.server(), x.client(), (int) x.bandwidth());
	}

}
