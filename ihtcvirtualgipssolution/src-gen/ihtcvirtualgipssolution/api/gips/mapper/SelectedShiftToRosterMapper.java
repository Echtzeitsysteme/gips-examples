package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.matches.SelectShiftToRosterMatch;
import ihtcvirtualgipssolution.api.rules.SelectShiftToRosterRule;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToRosterMapping;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import org.emoflon.gips.core.gt.GipsRuleMapper;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class SelectedShiftToRosterMapper extends GipsRuleMapper<SelectedShiftToRosterMapping, SelectShiftToRosterMatch, SelectShiftToRosterRule> {
	public SelectedShiftToRosterMapper(final GipsEngine engine, final Mapping mapping, final SelectShiftToRosterRule rule) {
		super(engine, mapping, rule);
	}
	
	@Override
	protected SelectedShiftToRosterMapping convertMatch(final String ilpVariable, final SelectShiftToRosterMatch match) {
		return new SelectedShiftToRosterMapping(ilpVariable, match);
	}
}