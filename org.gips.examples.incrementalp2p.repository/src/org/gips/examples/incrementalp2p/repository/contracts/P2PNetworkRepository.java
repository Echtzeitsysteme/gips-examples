package org.gips.examples.incrementalp2p.repository.contracts;

import java.util.List;

import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.repository.contracts.models.ClientModel;
import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;

public interface P2PNetworkRepository extends ApiSaver {
	ClientModel getRootServer();

	List<ClientModel> getClients();

	List<ClientModel> getClients(List<String> clientIds);

	List<ConnectionModel> getConnections();

	P2PNetworkRepository init();

	void addWaitingClients(List<WaitingClient> clients);

	List<ClientModel> removeRelayClientsAndAttachOrphansAsWaiting(int count);

	void removeAllPossibleConnections();

}
