package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.gt.GipsPatternMapper;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.matches.RoomForShiftMatch;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.gips.mapping.CountPatientsForRoomMapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.rules.RoomForShiftPattern;
		
public class CountPatientsForRoomMapper extends GipsPatternMapper<CountPatientsForRoomMapping, RoomForShiftMatch, RoomForShiftPattern> {
	public CountPatientsForRoomMapper(final GipsEngine engine, final Mapping mapping, final RoomForShiftPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected CountPatientsForRoomMapping convertMatch(final String ilpVariable, final RoomForShiftMatch match) {
		return new CountPatientsForRoomMapping(ilpVariable, match);
	}
}