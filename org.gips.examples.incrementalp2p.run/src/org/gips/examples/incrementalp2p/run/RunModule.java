package org.gips.examples.incrementalp2p.run;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.gips.examples.incrementalp2p.common.models.WaitingClient;
import org.gips.examples.incrementalp2p.distribution.module.IncrementalGipsModule;
import org.gips.examples.incrementalp2p.repository.module.RepositoryModule;
import org.gips.examples.incrementalp2p.visualization.contracts.GraphType;
import org.gips.examples.incrementalp2p.visualization.contracts.VisualizationConfiguration;
import org.gips.examples.incrementalp2p.visualization.module.VisualizationModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class RunModule extends AbstractModule {
	private static final URI Uri = URI.createFileURI("Model" + ".xmi");

	public void run(final List<WaitingClient> clients, final List<WaitingClient> additionalClients,
			final boolean openBrowser) {
		createInjector().getInstance(ExampleRunner.class).run(clients, additionalClients, openBrowser);
	}

	@Override
	protected void configure() {
		super.configure();
		initWithGtModel();
		install(new VisualizationModule(new VisualizationConfiguration(false, GraphType.Classic, false, true)));
	}

	private void initWithGtModel() {
		var repository = new RepositoryModule(Uri);
		var incrementalGipsModule = new IncrementalGipsModule(repository.getModel());

		install(repository);
		install(incrementalGipsModule);
		bind(ExampleRunner.class);
	}

	private Injector createInjector() {
		return Guice.createInjector(this);
	}

}
