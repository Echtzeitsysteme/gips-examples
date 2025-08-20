package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import org.emoflon.gips.core.GipsMappingConstraint;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedGenderToRoomOnShiftMapping;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import java.util.stream.Collectors;

public class MappingConstraint22OnassignedGenderToRoomOnShift extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AssignedGenderToRoomOnShiftMapping>{
	public MappingConstraint22OnassignedGenderToRoomOnShift(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AssignedGenderToRoomOnShiftMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AssignedGenderToRoomOnShiftMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(context, 1.0));
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final AssignedGenderToRoomOnShiftMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final AssignedGenderToRoomOnShiftMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final AssignedGenderToRoomOnShiftMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final AssignedGenderToRoomOnShiftMapping context) {
		final GipsMapper<?> mapper = engine.getMapper("assignedPatientsToRoom");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (AssignedPatientsToRoomMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getS(), elt);
						//
						indexer.putMapping(elt.getP(), elt);
						indexer.putMapping(elt.getR(), elt);
						indexer.putMapping(elt.getVsw(), elt);
						indexer.putMapping(elt.getW(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getS()).parallelStream()
				.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
				.filter(elt -> elt.getP().getGender().equals(context.getG().getName()) && elt.getS().equals(context.getS()))
				.forEach(elt -> {
					terms.add(new Term(elt, (double)(-1.0) * (1.0)));
				});
		
		// Old generated code
//		engine.getMapper("assignedPatientsToRoom").getMappings().values().parallelStream()
//					.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
//		.filter(elt -> elt.getP().getGender().equals(context.getG().getName()) && elt.getS().equals(context.getS()))
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
//		});
	}
}
