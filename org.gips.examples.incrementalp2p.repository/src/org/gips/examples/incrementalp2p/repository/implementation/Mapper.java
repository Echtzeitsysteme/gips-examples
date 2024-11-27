package org.gips.examples.incrementalp2p.repository.implementation;

import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.repository.contracts.models.ClientModel;
import org.gips.examples.incrementalp2p.repository.contracts.models.ConnectionModel;

import LectureStudioModel.Connection;
import LectureStudioModel.Node;

class Mapper {
	public static WaitingClient toWaitingClient(final Node x) {
		return new WaitingClient(x.getId());
	}

	public static ClientModel toClientModel(final Node x) {
		return new ClientModel(x.getId(), x.isIsRelayClient(), x.getSendBandwidth());
	}

	public static ConnectionModel toConnectionModel(final Connection x) {
		return new ConnectionModel(x.getServer().getId(), x.getClient().getId(), x.getTransferRate());
	}
	
}
