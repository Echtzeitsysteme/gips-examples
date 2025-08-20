package ihtcvirtualgipssolution.api.gips.constraint;		

import org.emoflon.gips.intermediate.GipsIntermediate.PatternConstraint;
import ihtcvirtualgipssolution.api.matches.SurgeonOptimeTupelMatch;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOperationDayMapping;
import org.emoflon.gips.core.gt.GipsPatternConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import java.util.List;
import ihtcvirtualgipssolution.api.rules.SurgeonOptimeTupelPattern;
import java.util.LinkedList;
import java.util.Collections;

public class PatternConstraint1OnsurgeonOptimeTupel extends GipsPatternConstraint<IhtcvirtualgipssolutionGipsAPI, SurgeonOptimeTupelMatch, SurgeonOptimeTupelPattern>{
	public PatternConstraint1OnsurgeonOptimeTupel(final IhtcvirtualgipssolutionGipsAPI engine, final PatternConstraint constraint, final SurgeonOptimeTupelPattern pattern) {
		super(engine, constraint, pattern);
	}
	
	@Override
	protected double buildConstantRhs(final SurgeonOptimeTupelMatch context) {
		return context.getOp().getMaxOpTime();
	}
		
	@Override
	protected List<Term> buildVariableLhs(final SurgeonOptimeTupelMatch context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final SurgeonOptimeTupelMatch context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final SurgeonOptimeTupelMatch context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final SurgeonOptimeTupelMatch context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final SurgeonOptimeTupelMatch context) {
		engine.getMapper("selectedOperationDay").getMappings().values().parallelStream()
					.map(mapping -> (SelectedOperationDayMapping) mapping)
		.filter(elt -> elt.getVwop().getOpTime().equals(context.getOp()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(elt.getP().getSurgeryDuration()) * (1.0)));
		});
	}
}
