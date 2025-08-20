package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToRosterMapping;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import ihtcvirtualgipssolution.api.gips.mapping.CountPatientsForRoomMapping;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import java.util.stream.Collectors;

public class MappingConstraint11OncountPatientsForRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, CountPatientsForRoomMapping>{
	public MappingConstraint11OncountPatientsForRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final CountPatientsForRoomMapping context) {
		return 1;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final CountPatientsForRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final CountPatientsForRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final CountPatientsForRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final CountPatientsForRoomMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final CountPatientsForRoomMapping context) {
		engine.getMapper("selectedShiftToRoster").getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToRosterMapping) mapping)
		.filter(elt -> elt.getVsr().getShift().getRoom().equals(context.getR()) && elt.getVsr().getShift().getShiftNo() == context.getS().getShiftNo() + 2)
		.forEach(elt -> {
			terms.add(new Term(elt, (double)1.0));
		});
	}
}
