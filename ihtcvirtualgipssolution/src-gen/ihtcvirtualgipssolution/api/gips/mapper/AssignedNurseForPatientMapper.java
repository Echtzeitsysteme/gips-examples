package ihtcvirtualgipssolution.api.gips.mapper;
		
import ihtcvirtualgipssolution.api.gips.mapping.AssignedNurseForPatientMapping;
import ihtcvirtualgipssolution.api.rules.NursePatientTupelPattern;
import org.emoflon.gips.core.gt.GipsPatternMapper;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.matches.NursePatientTupelMatch;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class AssignedNurseForPatientMapper extends GipsPatternMapper<AssignedNurseForPatientMapping, NursePatientTupelMatch, NursePatientTupelPattern> {
	public AssignedNurseForPatientMapper(final GipsEngine engine, final Mapping mapping, final NursePatientTupelPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected AssignedNurseForPatientMapping convertMatch(final String ilpVariable, final NursePatientTupelMatch match) {
		return new AssignedNurseForPatientMapping(ilpVariable, match);
	}
}