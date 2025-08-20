package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.gt.GipsPatternMapper;
import ihtcvirtualgipssolution.api.matches.PatientForRoomMatch;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import ihtcvirtualgipssolution.api.rules.PatientForRoomPattern;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class AssignedPatientsToRoomMapper extends GipsPatternMapper<AssignedPatientsToRoomMapping, PatientForRoomMatch, PatientForRoomPattern> {
	public AssignedPatientsToRoomMapper(final GipsEngine engine, final Mapping mapping, final PatientForRoomPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected AssignedPatientsToRoomMapping convertMatch(final String ilpVariable, final PatientForRoomMatch match) {
		return new AssignedPatientsToRoomMapping(ilpVariable, match);
	}
}