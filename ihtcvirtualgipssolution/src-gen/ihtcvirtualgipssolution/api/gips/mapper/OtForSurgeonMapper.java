package ihtcvirtualgipssolution.api.gips.mapper;
		
import ihtcvirtualgipssolution.api.gips.mapping.OtForSurgeonMapping;
import org.emoflon.gips.core.gt.GipsPatternMapper;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.rules.SurgeonOTForDayPattern;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.SurgeonOTForDayMatch;
		
public class OtForSurgeonMapper extends GipsPatternMapper<OtForSurgeonMapping, SurgeonOTForDayMatch, SurgeonOTForDayPattern> {
	public OtForSurgeonMapper(final GipsEngine engine, final Mapping mapping, final SurgeonOTForDayPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected OtForSurgeonMapping convertMatch(final String ilpVariable, final SurgeonOTForDayMatch match) {
		return new OtForSurgeonMapping(ilpVariable, match);
	}
}