package org.gips.examples.incrementalp2p.distribution.implementation.strategy;

import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementStrategy;

public class SquareRootIncrementStrategy implements IncrementStrategy {
	final static Logger logger = Logger.getLogger(SquareRootIncrementStrategy.class);

	@Override
	public List<WaitingClient> getClients(final List<WaitingClient> all, final List<WaitingClient> left) {
		var sqaureRoot = Math.sqrt(all.size());

		logger.debug("Increment Size: " + sqaureRoot);

		return left.stream().sorted(Comparator.comparing(WaitingClient::id)).limit((int) sqaureRoot).toList();
	}

}
