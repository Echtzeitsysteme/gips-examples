package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.intermediate.GipsIntermediate.PatternConstraint;
import ihtcvirtualgipssolution.api.rules.OtCapacityTupelPattern;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOperationDayMapping;
import org.emoflon.gips.core.gt.GipsPatternConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import java.util.List;
import ihtcvirtualgipssolution.api.matches.OtCapacityTupelMatch;
import java.util.LinkedList;
import java.util.Collections;

public class PatternConstraint2OnotCapacityTupel extends GipsPatternConstraint<IhtcvirtualgipssolutionGipsAPI, OtCapacityTupelMatch, OtCapacityTupelPattern>{
	public PatternConstraint2OnotCapacityTupel(final IhtcvirtualgipssolutionGipsAPI engine, final PatternConstraint constraint, final OtCapacityTupelPattern pattern) {
		super(engine, constraint, pattern);
	}
	
	@Override
	protected double buildConstantRhs(final OtCapacityTupelMatch context) {
		return context.getC().getMaxCapacity();
	}
		
	@Override
	protected List<Term> buildVariableLhs(final OtCapacityTupelMatch context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final OtCapacityTupelMatch context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final OtCapacityTupelMatch context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final OtCapacityTupelMatch context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final OtCapacityTupelMatch context) {
		engine.getMapper("selectedOperationDay").getMappings().values().parallelStream()
					.map(mapping -> (SelectedOperationDayMapping) mapping)
		.filter(elt -> elt.getVopc().getCapacity().getDay() == context.getC().getDay() && elt.getVwc().getCapacity().getOt().equals(context.getOt()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(elt.getP().getSurgeryDuration()) * (1.0)));
		});
	}
}
