package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import org.emoflon.gips.core.milp.model.Term;
import ihtcvirtualgipssolution.api.gips.mapping.AgeGroupsInRoomMapping;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import org.emoflon.gips.intermediate.GipsIntermediate.RelationalOperator;

public class DisjunctMappingConstraint32OnageGroupsInRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AgeGroupsInRoomMapping>{
	public DisjunctMappingConstraint32OnageGroupsInRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AgeGroupsInRoomMapping context) {
		return 1.0 + -1.0 + -1.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AgeGroupsInRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "DisjunctMappingConstraint32OnageGroupsInRoom_symbolic2"), builder_0(context)));
		terms.add(new Term(engine.getNonMappingVariable(context, "DisjunctMappingConstraint32OnageGroupsInRoom_symbolic1"), builder_1(context)));
		terms.add(new Term(engine.getNonMappingVariable(context, "DisjunctMappingConstraint32OnageGroupsInRoom_symbolic0"), 1.0));
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
		
		List<Constraint> additionalConstraints = new LinkedList<>();
		Constraint constraint = null;
		List<Term> terms = new LinkedList<>();
		double constTerm = 0.0;
		
		
		return additionalConstraints;
	}
	protected double builder_0(final AgeGroupsInRoomMapping context) {
		return (-1.0) * (1.0);
	}
	protected double builder_1(final AgeGroupsInRoomMapping context) {
		return (-1.0) * (1.0);
	}
}
