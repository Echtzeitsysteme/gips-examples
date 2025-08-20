package ihtcvirtualgipssolution.api.gips.mapper;
		
import org.emoflon.gips.core.gt.GipsPatternMapper;
import ihtcvirtualgipssolution.api.matches.SurgeonOptimeTupelMatch;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.rules.SurgeonOptimeTupelPattern;
import ihtcvirtualgipssolution.api.gips.mapping.OtsPerSurgeonMapping;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
		
public class OtsPerSurgeonMapper extends GipsPatternMapper<OtsPerSurgeonMapping, SurgeonOptimeTupelMatch, SurgeonOptimeTupelPattern> {
	public OtsPerSurgeonMapper(final GipsEngine engine, final Mapping mapping, final SurgeonOptimeTupelPattern pattern) {
		super(engine, mapping, pattern);
	}
	
	@Override
	protected OtsPerSurgeonMapping convertMatch(final String ilpVariable, final SurgeonOptimeTupelMatch match) {
		return new OtsPerSurgeonMapping(ilpVariable, match);
	}
}