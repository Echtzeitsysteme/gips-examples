package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.matches.VirtualNodesForOccupantMatch;
import ihtcvirtualgipssolution.api.rules.VirtualNodesForOccupantRule;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import org.emoflon.gips.core.gt.GipsRuleMapper;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOccupantNodesMapping;
		
public class SelectedOccupantNodesMapper extends GipsRuleMapper<SelectedOccupantNodesMapping, VirtualNodesForOccupantMatch, VirtualNodesForOccupantRule> {
	public SelectedOccupantNodesMapper(final GipsEngine engine, final Mapping mapping, final VirtualNodesForOccupantRule rule) {
		super(engine, mapping, rule);
	}
	
	@Override
	protected SelectedOccupantNodesMapping convertMatch(final String ilpVariable, final VirtualNodesForOccupantMatch match) {
		return new SelectedOccupantNodesMapping(ilpVariable, match);
	}
}