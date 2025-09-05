package org.gips.examples.incrementalp2p.distribution.contracts;

import java.util.List;

import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;

public interface ConnectionLog {
	ConnectionLog clear();

	void addToLog(List<ConnectionModel> connections);

	void addToLog(ConnectionModel connections);

	List<ConnectionModel> getLog();

}
