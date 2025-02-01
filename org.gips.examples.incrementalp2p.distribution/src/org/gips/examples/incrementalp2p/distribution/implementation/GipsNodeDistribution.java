
package org.gips.examples.incrementalp2p.distribution.implementation;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.emoflon.gips.core.gt.GTMapper;
import org.gips.examples.incrementalp2p.common.Guard;
import org.gips.examples.incrementalp2p.common.TimeAggregator;
import org.gips.examples.incrementalp2p.distribution.contracts.ConnectionLog;
import org.gips.examples.incrementalp2p.distribution.contracts.NodeDistributionEngine;
import org.gips.examples.incrementalp2p.distribution.implementation.strategy.SquareRootIncrementStrategy;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.IncrementaldistributionGipsAPI;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.mapper.RelayClientMapper;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.mapper.StreamingClientLSMapper;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.mapper.StreamingClientRCMapper;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.mapping.RelayClientMapping;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.mapping.StreamingClientLSMapping;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.mapping.StreamingClientRCMapping;
import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;

import LectureStudioModel.ConnectionData;
import LectureStudioModel.Node;
import jakarta.inject.Inject;

public class GipsNodeDistribution implements NodeDistributionEngine {
	@Inject
	IncrementaldistributionGipsAPI api;
	@Inject
	ConnectionLog log;
	final static Logger logger = Logger.getLogger(SquareRootIncrementStrategy.class);

	@Override
	public NodeDistributionEngine distributeNodes() {
		TimeAggregator.gtTick();
		api.update();
		TimeAggregator.gtTock();

		api.buildILPProblem(false);

		TimeAggregator.ilpTick();
		api.solveILPProblem();
		TimeAggregator.ilpTock();

		relevantMappings().forEach(x -> x.applyNonZeroMappings(false));

		TimeAggregator.gtTick();
		api.update();
		TimeAggregator.gtTock();
		return this;
	}

	@Override
	public void save(final String directory, final String fileName) {
		try {
			Guard.againstMissingDirectory(directory);
			var path = directory + File.separator + fileName + ".xmi";
			api.saveResult(path);
			logger.info("Save network: " + path);

		} catch (final IOException e) {
			logger.error("Error while saving file", e);
			throw new RuntimeException("Error while saving file", e);
		}
	}

	@SuppressWarnings("rawtypes")
	private Stream<GTMapper> relevantMappings() {

		var getRelayClient = api.getRelayClient();
		var streamingClientLS = api.getStreamingClientLS();
		var streamingClientRC = api.getStreamingClientRC();

		logConnections(getRelayClient, streamingClientLS, streamingClientRC);

		return Stream.of(getRelayClient, streamingClientLS, streamingClientRC);
	}

	private void logConnections(final RelayClientMapper getRelayClient, final StreamingClientLSMapper streamingClientLS,
			final StreamingClientRCMapper streamingClientRC) {
		getRelayClient.getNonZeroVariableMappings().stream().map(x -> toModel(x)).forEach(this::log);

		streamingClientLS.getNonZeroVariableMappings().stream().map(x -> toModel(x)).forEach(this::log);

		streamingClientRC.getNonZeroVariableMappings().stream().map(x -> toModel(x)).forEach(this::log);
	}

	private ConnectionModel toModel(final StreamingClientRCMapping x) {
		return toConnectionModel(x.getServer(), x.getClient(), x.getConnectionData());
	}

	private ConnectionModel toModel(final StreamingClientLSMapping x) {
		return toConnectionModel(x.getServer(), x.getClient(), x.getConnectionData());
	}

	private ConnectionModel toModel(final RelayClientMapping x) {
		return toConnectionModel(x.getServer(), x.getClient(), x.getConnectionData());
	}

	private ConnectionModel toConnectionModel(final Node server, final Node client, final ConnectionData data) {
		var bandwidth = Math.min(server.getSendBandwidth(), data.getBandwidth());
		return new ConnectionModel(server.getId(), client.getId(), bandwidth);
	}

	private void log(final ConnectionModel nonZeroVariableMappings) {
		log.addToLog(nonZeroVariableMappings);
	}

}
