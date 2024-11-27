package org.gips.examples.incrementalp2p.distribution.implementation;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementStrategy;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementalNodeDistributionEngine;
import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;

import com.google.inject.Inject;

public class IncrementalGipsNodeDistribution extends GipsNodeDistribution implements IncrementalNodeDistributionEngine {
	final static Logger logger = Logger.getLogger(IncrementalGipsNodeDistribution.class);

	@Inject
	private IncrementStrategy incrementStrategy;
	@Inject
	private P2PNetworkRepository repository;

	@Override
	public IncrementalNodeDistributionEngine distributeNodes(final List<WaitingClient> clients) {
		var count = 0;

		var clientsLeft = clients.stream().collect(Collectors.toList());

		while (!clientsLeft.isEmpty()) {
			logger.debug("Run: " + ++count);
			addClients(clients, clientsLeft);
		}
		return this;
	}

	private void addClients(final List<WaitingClient> totalClients, final List<WaitingClient> clientsLeft) {
		var addedClients = incrementStrategy.getClients(totalClients, clientsLeft);
		clientsLeft.removeAll(addedClients);

		logger.debug("All clients:     " + toIdList(totalClients));
		logger.debug("Missing clients: " + toIdList(clientsLeft));
		logger.debug("Added clients:   " + toIdList(addedClients));

		repository.addWaitingClients(addedClients);
		distributeNodes();
		if (logger.isDebugEnabled()) {
			save("src-sim", "run" + toIdList(addedClients));
		}
	}

	private String toIdList(final List<WaitingClient> clients) {
		return clients.stream().map(x -> x.id()).collect(Collectors.joining(","));
	}

}
