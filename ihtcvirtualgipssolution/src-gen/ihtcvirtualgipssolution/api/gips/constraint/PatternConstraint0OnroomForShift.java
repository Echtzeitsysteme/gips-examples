package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.intermediate.GipsIntermediate.PatternConstraint;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.gt.GipsPatternConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import ihtcvirtualgipssolution.api.rules.RoomForShiftPattern;
import java.util.List;
import ihtcvirtualgipssolution.api.matches.RoomForShiftMatch;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedGenderToRoomOnShiftMapping;
import java.util.LinkedList;
import java.util.Collections;

public class PatternConstraint0OnroomForShift extends GipsPatternConstraint<IhtcvirtualgipssolutionGipsAPI, RoomForShiftMatch, RoomForShiftPattern>{
	public PatternConstraint0OnroomForShift(final IhtcvirtualgipssolutionGipsAPI engine, final PatternConstraint constraint, final RoomForShiftPattern pattern) {
		super(engine, constraint, pattern);
	}
	
	@Override
	protected double buildConstantRhs(final RoomForShiftMatch context) {
		return 1;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final RoomForShiftMatch context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final RoomForShiftMatch context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final RoomForShiftMatch context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final RoomForShiftMatch context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final RoomForShiftMatch context) {
		final GipsMapper<?> mapper = engine.getMapper("assignedGenderToRoomOnShift");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (AssignedGenderToRoomOnShiftMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getS(), elt);
						//
						indexer.putMapping(elt.getG(), elt);
						indexer.putMapping(elt.getR(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getS()).parallelStream()
				.map(mapping -> (AssignedGenderToRoomOnShiftMapping) mapping)
				.filter(elt -> elt.getS().equals(context.getS()))
				.forEach(elt -> {
					terms.add(new Term(elt, (double)1.0));
				});
		
		// Old generated code
//		engine.getMapper("assignedGenderToRoomOnShift").getMappings().values().parallelStream()
//					.map(mapping -> (AssignedGenderToRoomOnShiftMapping) mapping)
//		.filter(elt -> elt.getS().equals(context.getS()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)1.0));
//		});
	}
}
