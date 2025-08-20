package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.emoflon.gips.core.GipsMapper;
import org.emoflon.gips.core.GipsMappingConstraint;
import org.emoflon.gips.core.GlobalMappingIndexer;
import org.emoflon.gips.core.MappingIndexer;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.milp.model.Term;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;

import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualgipssolution.api.gips.mapping.AgeGroupsInRoomMapping;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;

public class MappingConstraint30OnageGroupsInRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AgeGroupsInRoomMapping>{
	public MappingConstraint30OnageGroupsInRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AgeGroupsInRoomMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AgeGroupsInRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		terms.add(new Term(context, builder_1(context)));
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final AgeGroupsInRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final AgeGroupsInRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final AgeGroupsInRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final AgeGroupsInRoomMapping context) {
        final GipsMapper<?> mapper = engine.getMapper("assignedPatientsToRoom");
		final GlobalMappingIndexer globalIndexer = GlobalMappingIndexer.getInstance();
		globalIndexer.createIndexer(mapper);
		final MappingIndexer indexer = globalIndexer.getIndexer(mapper);
		if (!indexer.isInitialized()) {
			mapper.getMappings().values().parallelStream()
					.map(mapping -> (AssignedPatientsToRoomMapping) mapping).forEach(elt -> {
						indexer.putMapping(elt.getP(), elt);
						indexer.putMapping(elt.getR(), elt);
						indexer.putMapping(elt.getS(), elt);
						indexer.putMapping(elt.getVsw(), elt);
						indexer.putMapping(elt.getW(), elt);
					});
		}
		
		indexer.getMappingsOfNode(context.getR()).parallelStream()
				.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
				.filter(elt -> elt.getR().equals(context.getR()) && elt.getS().getShiftNo() == (context.getD().getNumber()) * (3) && elt.getP().getAgeGroup() == context.getAg().getGroup())
				.forEach(elt -> {
					terms.add(new Term(elt, (double)1.0));
				});
		
//		engine.getMapper("assignedPatientsToRoom").getMappings().values().parallelStream()
//					.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
//		.filter(elt -> elt.getR().equals(context.getR()) && elt.getS().getShiftNo() == (context.getD().getNumber()) * (3) && elt.getP().getAgeGroup() == context.getAg().getGroup())
//		.forEach(elt -> {
//			terms.add(new Term(elt, (double)1.0));
//		});
	}
	protected double builder_1(final AgeGroupsInRoomMapping context) {
		return (-1.0) * ((1.0) * (1000));
	}
}
