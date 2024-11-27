package org.gips.examples.incrementalp2p.distribution.module;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.gips.examples.incrementalp2p.distribution.contracts.ConnectionLog;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementStrategy;
import org.gips.examples.incrementalp2p.distribution.contracts.IncrementalNodeDistributionEngine;
import org.gips.examples.incrementalp2p.distribution.contracts.NodeDistributionEngine;
import org.gips.examples.incrementalp2p.distribution.implementation.GipsConnectionLog;
import org.gips.examples.incrementalp2p.distribution.implementation.GipsNodeDistribution;
import org.gips.examples.incrementalp2p.distribution.implementation.IncrementalGipsNodeDistribution;
import org.gips.examples.incrementalp2p.distribution.implementation.strategy.SquareRootIncrementStrategy;
import org.gips.examples.incrementalp2p.gips.incrementaldistribution.api.gips.IncrementaldistributionGipsAPI;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class IncrementalGipsModule extends AbstractModule {
	private IncrementaldistributionGipsAPI api;

	public IncrementalGipsModule(final ResourceSet model) {
		api = new IncrementaldistributionGipsAPI();
		api.init(model);
	}

	public IncrementalGipsModule(final URI uri) {
		api = new IncrementaldistributionGipsAPI();
		api.init(uri);
	}

	public ResourceSet getModel() {
		return api.getResourceSet();
	}

	@Override
	protected void configure() {
		bind(NodeDistributionEngine.class).to(GipsNodeDistribution.class);
		bind(IncrementalNodeDistributionEngine.class).to(IncrementalGipsNodeDistribution.class);
		bind(IncrementStrategy.class).to(SquareRootIncrementStrategy.class);
		bind(ConnectionLog.class).to(GipsConnectionLog.class).in(Scopes.SINGLETON);
		bind(IncrementaldistributionGipsAPI.class).toInstance(api);
	}
	
}
