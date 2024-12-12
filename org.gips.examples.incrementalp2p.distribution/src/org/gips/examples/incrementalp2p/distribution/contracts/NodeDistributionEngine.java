package org.gips.examples.incrementalp2p.distribution.contracts;

import org.gips.examples.incrementalp2p.repository.contracts.ApiSaver;

public interface NodeDistributionEngine extends ApiSaver {
	NodeDistributionEngine distributeNodes();
}
