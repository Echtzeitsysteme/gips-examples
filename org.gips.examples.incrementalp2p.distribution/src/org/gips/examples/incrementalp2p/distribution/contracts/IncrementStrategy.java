package org.gips.examples.incrementalp2p.distribution.contracts;

import java.util.List;

import org.gips.examples.incrementalp2p.common.models.WaitingClient;

public interface IncrementStrategy {
	List<WaitingClient> getClients(List<WaitingClient> all, List<WaitingClient> left);
}
