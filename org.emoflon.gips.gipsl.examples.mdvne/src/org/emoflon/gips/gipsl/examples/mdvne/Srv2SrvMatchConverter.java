package org.emoflon.gips.gipsl.examples.mdvne;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.emoflon.gips.core.util.MatchUtil.IMatchConverter;
import org.emoflon.gips.core.util.MatchUtil.MatchProperty;
import org.emoflon.gips.gipsl.examples.mdvne.api.matches.ServerMatchPositiveMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

import model.impl.SubstrateLinkImpl;

/**
 * Converts server 2 server matches into a property representation.
 */
public class Srv2SrvMatchConverter implements IMatchConverter {

	/**
	 * Converts the given GT match into a list of match properties.
	 * 
	 * @param match GT match to convert.
	 * @return List of match properties based on the given GT match.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<MatchProperty> convertMatch(final GraphTransformationMatch match) {
		final ServerMatchPositiveMatch m = (ServerMatchPositiveMatch) match;
		final List<MatchProperty> properties = new LinkedList<>();

		// Substrate server properties
		properties.add(new MatchProperty("substrateServer_resCPU", m.getSubstrateServer().getResidualCpu()));
		properties.add(new MatchProperty("substrateServer_resMem", m.getSubstrateServer().getResidualMemory()));
		properties.add(new MatchProperty("substrateServer_resSto", m.getSubstrateServer().getResidualStorage()));

		// Virtual server properties
		properties.add(new MatchProperty("virtualServer_CPU", m.getVirtualNode().getCpu()));
		properties.add(new MatchProperty("virtualServer_Mem", m.getVirtualNode().getMemory()));
		properties.add(new MatchProperty("virtualServer_Sto", m.getVirtualNode().getStorage()));

		// Context properties
		MatchProperty context;
		try {
			// Use the residual link bandwidth of the substrate server (if a link is
			// available)
			context = new MatchProperty("context",
					((SubstrateLinkImpl) m.getSubstrateServer().getIncomingLinks().getFirst()).getResidualBandwidth());
		} catch (final NoSuchElementException ex) {
			// If no incoming substrate link is available, we default to `-1`
			context = new MatchProperty("context", -1);
		}
		properties.add(context);

		return properties;
	}

}
