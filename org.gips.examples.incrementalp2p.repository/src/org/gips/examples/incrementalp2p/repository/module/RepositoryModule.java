package org.gips.examples.incrementalp2p.repository.module;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gips.examples.incrementalp2p.common.TimeAggregator;
import org.gips.examples.incrementalp2p.repository.api.RepositoryAPI;
import org.gips.examples.incrementalp2p.repository.api.RepositoryHiPEApp;
import org.gips.examples.incrementalp2p.repository.contracts.P2PNetworkRepository;
import org.gips.examples.incrementalp2p.repository.implementation.GtClientRepository;

import com.google.inject.AbstractModule;

public class RepositoryModule extends AbstractModule {
	private RepositoryAPI api;

	public RepositoryModule(final URI uri) {
		var app = new RepositoryHiPEApp();
		app.createModel(uri);
		
		TimeAggregator.gtTick();
		api = app.initAPI();
		TimeAggregator.gtTock();
		
	}

	public RepositoryModule(final ResourceSet model) {
		var app = new RepositoryHiPEApp();
		app.setModel(model);
		app.registerMetaModels();
		
		TimeAggregator.gtTick();
		api = app.initAPI();
		TimeAggregator.gtTock();
	}

	public ResourceSet getModel() {
		return api.getModel();
	}

	@Override
	protected void configure() {
		super.configure();
		bindRepositories();
	}

	private void bindRepositories() {
		bind(P2PNetworkRepository.class).to(GtClientRepository.class);
		bind(RepositoryAPI.class).toInstance(api);
	}
	
}
