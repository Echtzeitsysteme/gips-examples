package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.gt.GipsPatternMapper;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.rules.NurseRosterTupelPattern;
import ihtcvirtualgipssolution.api.gips.mapping.NurseWorkloadForDayMapping;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.matches.NurseRosterTupelMatch;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class NurseWorkloadForDayMapper extends GipsPatternMapper<NurseWorkloadForDayMapping, NurseRosterTupelMatch, NurseRosterTupelPattern> {
	public NurseWorkloadForDayMapper(final GipsEngine engine, final Mapping mapping, final NurseRosterTupelPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected NurseWorkloadForDayMapping convertMatch(final String ilpVariable, final NurseRosterTupelMatch match) {
		return new NurseWorkloadForDayMapping(ilpVariable, match);
	}
}