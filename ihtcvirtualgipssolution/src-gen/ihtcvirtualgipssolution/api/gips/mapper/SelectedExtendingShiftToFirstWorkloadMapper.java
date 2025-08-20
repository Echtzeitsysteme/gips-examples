package ihtcvirtualgipssolution.api.gips.mapper;
		
import ihtcvirtualgipssolution.api.gips.mapping.SelectedExtendingShiftToFirstWorkloadMapping;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.rules.SelectExtendingShiftToFirstWorkloadRule;
import ihtcvirtualgipssolution.api.matches.SelectExtendingShiftToFirstWorkloadMatch;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import org.emoflon.gips.core.gt.GipsRuleMapper;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class SelectedExtendingShiftToFirstWorkloadMapper extends GipsRuleMapper<SelectedExtendingShiftToFirstWorkloadMapping, SelectExtendingShiftToFirstWorkloadMatch, SelectExtendingShiftToFirstWorkloadRule> {
	public SelectedExtendingShiftToFirstWorkloadMapper(final GipsEngine engine, final Mapping mapping, final SelectExtendingShiftToFirstWorkloadRule rule) {
		super(engine, mapping, rule);
	}
	
	@Override
	protected SelectedExtendingShiftToFirstWorkloadMapping convertMatch(final String ilpVariable, final SelectExtendingShiftToFirstWorkloadMatch match) {
		return new SelectedExtendingShiftToFirstWorkloadMapping(ilpVariable, match);
	}
}