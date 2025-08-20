package ihtcvirtualgipssolution.api.gips.mapper;
		
import ihtcvirtualgipssolution.api.rules.AgeGroupsRoomDayPattern;
import org.emoflon.gips.core.gt.GipsPatternMapper;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.matches.AgeGroupsRoomDayMatch;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.gips.mapping.AgeGroupsInRoomMapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class AgeGroupsInRoomMapper extends GipsPatternMapper<AgeGroupsInRoomMapping, AgeGroupsRoomDayMatch, AgeGroupsRoomDayPattern> {
	public AgeGroupsInRoomMapper(final GipsEngine engine, final Mapping mapping, final AgeGroupsRoomDayPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected AgeGroupsInRoomMapping convertMatch(final String ilpVariable, final AgeGroupsRoomDayMatch match) {
		return new AgeGroupsInRoomMapping(ilpVariable, match);
	}
}