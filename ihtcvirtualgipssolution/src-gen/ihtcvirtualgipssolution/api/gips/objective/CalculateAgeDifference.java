package ihtcvirtualgipssolution.api.gips.objective;		

import org.emoflon.gips.core.gt.GipsPatternLinearFunction;
import java.util.List;
import ihtcvirtualgipssolution.api.matches.RoomDayTupelMatch;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.intermediate.GipsIntermediate.PatternFunction;
import org.emoflon.gips.core.milp.model.Constant;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import ihtcvirtualgipssolution.api.rules.RoomDayTupelPattern;
import ihtcvirtualgipssolution.api.gips.mapping.AgeGroupsInRoomMapping;
import java.util.stream.Collectors;

public class CalculateAgeDifference extends GipsPatternLinearFunction<IhtcvirtualgipssolutionGipsAPI, RoomDayTupelMatch, RoomDayTupelPattern>{
	public CalculateAgeDifference(final IhtcvirtualgipssolutionGipsAPI engine, final PatternFunction function, final RoomDayTupelPattern pattern) {
		super(engine, function, pattern);
	}
	
	@Override
	protected void buildTerms(final RoomDayTupelMatch context) {
		
		builder_0(terms, context);
		builder_1(terms, context);
	}
	
	protected void builder_1(final List<Term> terms, final RoomDayTupelMatch context) {
		engine.getMapper("ageGroupsInRoom").getMappings().values().parallelStream()
					.map(mapping -> (AgeGroupsInRoomMapping) mapping)
		.filter(elt -> elt.getR().equals(context.getR()) && elt.getD().equals(context.getD()))
		.forEach(elt -> {
			terms.add(new Term(engine.getNonMappingVariable(elt, "minAgeGroup"), (double)(-1.0) * ((1.0) * (elt.getAg().getGroup()))));
		});
	}
	protected void builder_0(final List<Term> terms, final RoomDayTupelMatch context) {
		engine.getMapper("ageGroupsInRoom").getMappings().values().parallelStream()
					.map(mapping -> (AgeGroupsInRoomMapping) mapping)
		.filter(elt -> elt.getR().equals(context.getR()) && elt.getD().equals(context.getD()))
		.forEach(elt -> {
			terms.add(new Term(engine.getNonMappingVariable(elt, "maxAgeGroup"), (double)(1.0) * (elt.getAg().getGroup())));
		});
	}
	
}
