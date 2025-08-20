package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.gt.GipsPatternMapper;
import ihtcvirtualgipssolution.api.rules.OtCapacityTupelPattern;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.matches.OtCapacityTupelMatch;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.gips.mapping.OpenOTsMapping;
		
public class OpenOTsMapper extends GipsPatternMapper<OpenOTsMapping, OtCapacityTupelMatch, OtCapacityTupelPattern> {
	public OpenOTsMapper(final GipsEngine engine, final Mapping mapping, final OtCapacityTupelPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected OpenOTsMapping convertMatch(final String ilpVariable, final OtCapacityTupelMatch match) {
		return new OpenOTsMapping(ilpVariable, match);
	}
}