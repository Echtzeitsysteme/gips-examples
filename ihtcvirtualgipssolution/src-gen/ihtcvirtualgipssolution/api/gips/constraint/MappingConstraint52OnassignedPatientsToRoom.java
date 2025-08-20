package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToRosterMapping;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import java.util.stream.Collectors;

public class MappingConstraint52OnassignedPatientsToRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AssignedPatientsToRoomMapping>{
	public MappingConstraint52OnassignedPatientsToRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AssignedPatientsToRoomMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AssignedPatientsToRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "skillDiff"), 1.0));
		builder_0(terms, context);
		builder_1(terms, context);
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
	protected void builder_0(final List<Term> terms, final AssignedPatientsToRoomMapping context) {
		engine.getMapper("assignedPatientsToRoom").getMappings().values().parallelStream()
					.map(mapping -> (AssignedPatientsToRoomMapping) mapping)
		.filter(elt -> elt.getVsw().equals(context.getVsw()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(-1.0) * ((1.0) * (elt.getW().getMinNurseSkill()))));
		});
	}
	protected void builder_1(final List<Term> terms, final AssignedPatientsToRoomMapping context) {
		engine.getMapper("selectedShiftToRoster").getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToRosterMapping) mapping)
		.filter(elt -> elt.getVsr().getShift().equals(context.getS()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)((-1.0) * (-1.0)) * ((1.0) * (elt.getVsr().getRoster().getNurse().getSkillLevel()))));
		});
	}
}
