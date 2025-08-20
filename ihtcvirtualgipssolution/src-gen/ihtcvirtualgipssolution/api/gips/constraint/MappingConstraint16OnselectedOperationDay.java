package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOperationDayMapping;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import java.util.stream.Collectors;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class MappingConstraint16OnselectedOperationDay extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, SelectedOperationDayMapping>{
	public MappingConstraint16OnselectedOperationDay(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final SelectedOperationDayMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final SelectedOperationDayMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		builder_1(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final SelectedOperationDayMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final SelectedOperationDayMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final SelectedOperationDayMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final SelectedOperationDayMapping context) {
		engine.getMapper("selectedOperationDay").getMappings().values().parallelStream()
					.map(mapping -> (SelectedOperationDayMapping) mapping)
		.filter(elt -> elt.getVwc().equals(context.getVwc()) && elt.getVopc().equals(context.getVopc()) && elt.getVwop().equals(context.getVwop()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)1.0));
		});
	}
	protected void builder_1(final List<Term> terms, final SelectedOperationDayMapping context) {
		engine.getMapper("selectedShiftToFirstWorkload").getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
		.filter(elt -> elt.getVwc().equals(context.getVwc()) && (elt.getVsw().getShift().getShiftNo()) / (3) == context.getVopc().getCapacity().getDay())
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
		});
	}
}
