package org.gips.examples.incrementalp2p.distribution.implementation;

import java.util.ArrayList;
import java.util.List;

import org.gips.examples.incrementalp2p.distribution.contracts.ConnectionLog;
import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;

public class GipsConnectionLog implements ConnectionLog {
	private List<ConnectionModel> cacheConnections;

	GipsConnectionLog() {
		initConnections();
	}

	private void initConnections() {
		cacheConnections = new ArrayList<ConnectionModel>();
	}

	@Override
	public void addToLog(final ConnectionModel connection) {
		cacheConnections.add(connection);
	}

	@Override
	public void addToLog(final List<ConnectionModel> connections) {
		cacheConnections.addAll(connections);
	}

	@Override
	public List<ConnectionModel> getLog() {
		return cacheConnections;
	}

	@Override
	public ConnectionLog clear() {
		initConnections();
		return this;
	}

}
