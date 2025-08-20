package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.gt.GipsPatternMapper;
import ihtcvirtualgipssolution.api.rules.GenderRoomShiftPattern;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedGenderToRoomOnShiftMapping;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.GenderRoomShiftMatch;
		
public class AssignedGenderToRoomOnShiftMapper extends GipsPatternMapper<AssignedGenderToRoomOnShiftMapping, GenderRoomShiftMatch, GenderRoomShiftPattern> {
	public AssignedGenderToRoomOnShiftMapper(final GipsEngine engine, final Mapping mapping, final GenderRoomShiftPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected AssignedGenderToRoomOnShiftMapping convertMatch(final String ilpVariable, final GenderRoomShiftMatch match) {
		return new AssignedGenderToRoomOnShiftMapping(ilpVariable, match);
	}
}