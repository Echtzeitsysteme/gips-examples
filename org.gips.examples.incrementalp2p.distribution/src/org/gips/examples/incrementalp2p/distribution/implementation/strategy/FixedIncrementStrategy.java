package org.gips.examples.incrementalp2p.distribution.implementation.strategy;

import java.util.List;
import java.util.stream.Collectors;

import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementStrategy;

public class FixedIncrementStrategy implements IncrementStrategy {
	private int count;

	public FixedIncrementStrategy(final int count) {
		this.count = count;
	}

	@Override
	public List<WaitingClient> getClients(final List<WaitingClient> all, final List<WaitingClient> left) {
		return left.stream().limit(count).collect(Collectors.toList());
	}
	
}
