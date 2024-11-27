package org.gips.examples.incrementalp2p.visualization.implementation.providers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConnection;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationNode;
import org.gips.examples.incrementalp2p.visualization.implementation.mappers.Mapper;

import com.google.inject.Inject;

public class GtApiVisualizationDataProvider implements VisualizationDataProvider {
	@Inject
	P2PNetworkRepository repository;

	@Override
	public List<VisualizationNode> getNodes() {
		var ls = Stream.of(repository.getRootServer());
		var clients = repository.getClients().stream();
		return Stream.concat(ls, clients).map(Mapper::toVisualizationNode).collect(Collectors.toList());
	}

	@Override
	public List<VisualizationConnection> getConnections() {
		return repository.getConnections().stream().map(Mapper::toVisualizationConnection).collect(Collectors.toList());
	}
	
}
