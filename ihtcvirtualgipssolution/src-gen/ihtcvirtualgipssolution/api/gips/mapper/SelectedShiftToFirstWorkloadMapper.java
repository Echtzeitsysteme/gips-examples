package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.rules.SelectShiftToFirstWorkloadRule;
import ihtcvirtualgipssolution.api.matches.SelectShiftToFirstWorkloadMatch;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import org.emoflon.gips.core.gt.GipsRuleMapper;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class SelectedShiftToFirstWorkloadMapper extends GipsRuleMapper<SelectedShiftToFirstWorkloadMapping, SelectShiftToFirstWorkloadMatch, SelectShiftToFirstWorkloadRule> {
	public SelectedShiftToFirstWorkloadMapper(final GipsEngine engine, final Mapping mapping, final SelectShiftToFirstWorkloadRule rule) {
		super(engine, mapping, rule);
	}
	
	@Override
	protected SelectedShiftToFirstWorkloadMapping convertMatch(final String ilpVariable, final SelectShiftToFirstWorkloadMatch match) {
		return new SelectedShiftToFirstWorkloadMapping(ilpVariable, match);
	}
}