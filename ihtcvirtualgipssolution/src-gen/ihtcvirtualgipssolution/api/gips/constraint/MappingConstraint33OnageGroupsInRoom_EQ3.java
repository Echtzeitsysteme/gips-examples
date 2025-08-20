package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import ihtcvirtualgipssolution.api.gips.mapping.AgeGroupsInRoomMapping;
import java.util.Collections;

public class MappingConstraint33OnageGroupsInRoom_EQ3 extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AgeGroupsInRoomMapping>{
	public MappingConstraint33OnageGroupsInRoom_EQ3(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AgeGroupsInRoomMapping context) {
		return 1 - 1.0E-4;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AgeGroupsInRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "minAgeGroup"), 1.0));
		terms.add(new Term(engine.getNonMappingVariable(context, "MappingConstraint33OnageGroupsInRoom_EQslack2"), builder_0(context)));
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
	protected double builder_0(final AgeGroupsInRoomMapping context) {
		return (-10000.0) * (1.0);
	}
}
