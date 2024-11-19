package org.gips.nodevalue;

import java.util.LinkedList;
import java.util.List;

import org.emoflon.gips.core.util.MatchUtil.IMatchConverter;
import org.emoflon.gips.core.util.MatchUtil.MatchProperty;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;
import org.gips.nodevalue.api.matches.ConnectTwoNodesMatch;

public class NodeMatchConverter implements IMatchConverter {

	/**
	 * Converts the given GT match into a list of match properties.
	 * 
	 * @param match GT match to convert.
	 * @return List of match properties based on the given GT match.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<MatchProperty> convertMatch(final GraphTransformationMatch match) {
		if (!(match instanceof ConnectTwoNodesMatch)) {
			throw new UnsupportedOperationException();
		}
		final ConnectTwoNodesMatch m = (ConnectTwoNodesMatch) match;
		final List<MatchProperty> properties = new LinkedList<>();
		
		properties.add(new MatchProperty("n1", m.getN1().getValue()));
		properties.add(new MatchProperty("n2", m.getN2().getValue()));
		
		return properties;
	}

}
