package org.gips.examples.incrementalp2p.visualization.module;

import org.gips.examples.incrementalp2p.visualization.contracts.CachingVisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.contracts.GraphVisualizer;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConfiguration;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationUpdatesDataProvider;
import org.gips.examples.incrementalp2p.visualization.implementation.VisJsGraphVisualizer;
import org.gips.examples.incrementalp2p.visualization.implementation.mappers.VisJsMapper;
import org.gips.examples.incrementalp2p.visualization.implementation.providers.CachingGtVisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.implementation.providers.GtApiVisualizationDataProvider;
import org.gips.examples.incrementalp2p.visualization.implementation.providers.InMemoryVisualizationUpdatesDataProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class VisualizationModule extends AbstractModule {
	private VisualizationConfiguration config;

	public VisualizationModule(final VisualizationConfiguration config) {
		this.config = config;
	}

	@Override
	protected void configure() {
		super.configure();
		bind(VisualizationConfiguration.class).toInstance(config);
		bind(GtApiVisualizationDataProvider.class);
		bind(VisJsMapper.class);

		bind(CachingVisualizationDataProvider.class).to(CachingGtVisualizationDataProvider.class);
		bind(VisualizationDataProvider.class).to(CachingGtVisualizationDataProvider.class);
		bind(CachingGtVisualizationDataProvider.class).in(Scopes.SINGLETON);

		bind(GraphVisualizer.class).to(VisJsGraphVisualizer.class);
		bind(VisualizationUpdatesDataProvider.class).to(InMemoryVisualizationUpdatesDataProvider.class)
				.in(Scopes.SINGLETON);
	}
	
}