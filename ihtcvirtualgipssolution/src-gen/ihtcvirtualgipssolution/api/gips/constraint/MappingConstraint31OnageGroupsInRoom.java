package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import ihtcvirtualgipssolution.api.gips.mapping.AgeGroupsInRoomMapping;
import java.util.Collections;
import java.util.stream.Collectors;

public class MappingConstraint31OnageGroupsInRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AgeGroupsInRoomMapping>{
	public MappingConstraint31OnageGroupsInRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AgeGroupsInRoomMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AgeGroupsInRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(context, 1.0));
		builder_0(terms, context);
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
		engine.getMapper("assignedPatientsToRoom").getMappings().values().parallelStream()
					.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
		.filter(elt -> elt.getR().equals(context.getR()) && elt.getS().getShiftNo() == (context.getD().getNumber()) * (3) && elt.getP().getAgeGroup() == context.getAg().getGroup())
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
		});
	}
}
