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
import java.util.Collections;

public class MappingConstraint53OnassignedPatientsToRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AssignedPatientsToRoomMapping>{
	public MappingConstraint53OnassignedPatientsToRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AssignedPatientsToRoomMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AssignedPatientsToRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "penalizedSkillDiff"), 1.0));
		terms.add(new Term(engine.getNonMappingVariable(context, "skillDiff"), builder_0(context)));
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final AssignedPatientsToRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final AssignedPatientsToRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final AssignedPatientsToRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected double builder_0(final AssignedPatientsToRoomMapping context) {
		return (-1.0) * (1.0);
	}
}
