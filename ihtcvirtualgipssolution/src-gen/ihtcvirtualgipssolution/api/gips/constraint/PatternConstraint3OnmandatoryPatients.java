package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.intermediate.GipsIntermediate.PatternConstraint;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOperationDayMapping;
import org.emoflon.gips.core.gt.GipsPatternConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import java.util.List;
import ihtcvirtualgipssolution.api.rules.MandatoryPatientsPattern;
import java.util.LinkedList;
import ihtcvirtualgipssolution.api.matches.MandatoryPatientsMatch;
import java.util.Collections;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class PatternConstraint3OnmandatoryPatients extends GipsPatternConstraint<IhtcvirtualgipssolutionGipsAPI, MandatoryPatientsMatch, MandatoryPatientsPattern>{
	public PatternConstraint3OnmandatoryPatients(final IhtcvirtualgipssolutionGipsAPI engine, final PatternConstraint constraint, final MandatoryPatientsPattern pattern) {
		super(engine, constraint, pattern);
	}
	
	@Override
	protected double buildConstantRhs(final MandatoryPatientsMatch context) {
		return 2;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final MandatoryPatientsMatch context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		builder_1(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final MandatoryPatientsMatch context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final MandatoryPatientsMatch context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final MandatoryPatientsMatch context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_1(final List<Term> terms, final MandatoryPatientsMatch context) {
		engine.getMapper("selectedOperationDay").getMappings().values().parallelStream()
					.map(mapping -> (SelectedOperationDayMapping) mapping)
		.filter(elt -> elt.getP().equals(context.getP()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)1.0));
		});
	}
	protected void builder_0(final List<Term> terms, final MandatoryPatientsMatch context) {
		engine.getMapper("selectedShiftToFirstWorkload").getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
		.filter(elt -> elt.getP().equals(context.getP()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)1.0));
		});
	}
}
