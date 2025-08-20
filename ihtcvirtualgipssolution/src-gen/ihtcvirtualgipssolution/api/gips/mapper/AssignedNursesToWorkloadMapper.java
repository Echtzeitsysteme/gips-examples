package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.gt.GipsPatternMapper;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.rules.NursetoWorkloadPattern;
import ihtcvirtualgipssolution.api.matches.NursetoWorkloadMatch;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedNursesToWorkloadMapping;
		
public class AssignedNursesToWorkloadMapper extends GipsPatternMapper<AssignedNursesToWorkloadMapping, NursetoWorkloadMatch, NursetoWorkloadPattern> {
	public AssignedNursesToWorkloadMapper(final GipsEngine engine, final Mapping mapping, final NursetoWorkloadPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected AssignedNursesToWorkloadMapping convertMatch(final String ilpVariable, final NursetoWorkloadMatch match) {
		return new AssignedNursesToWorkloadMapping(ilpVariable, match);
	}
}