package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOperationDayMapping;
import ihtcvirtualgipssolution.api.rules.SelectOperationDayRule;
import ihtcvirtualgipssolution.api.matches.SelectOperationDayMatch;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import org.emoflon.gips.core.gt.GipsRuleMapper;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class SelectedOperationDayMapper extends GipsRuleMapper<SelectedOperationDayMapping, SelectOperationDayMatch, SelectOperationDayRule> {
	public SelectedOperationDayMapper(final GipsEngine engine, final Mapping mapping, final SelectOperationDayRule rule) {
		super(engine, mapping, rule);
	}
	
	@Override
	protected SelectedOperationDayMapping convertMatch(final String ilpVariable, final SelectOperationDayMatch match) {
		return new SelectedOperationDayMapping(ilpVariable, match);
	}
}