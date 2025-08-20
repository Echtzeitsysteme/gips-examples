package ihtcvirtualgipssolution.api.gips.constraint;		

import ihtcvirtualgipssolution.api.gips.mapping.OtForSurgeonMapping;
import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.OtsPerSurgeonMapping;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import java.util.stream.Collectors;

public class MappingConstraint27OnotsPerSurgeon extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, OtsPerSurgeonMapping>{
	public MappingConstraint27OnotsPerSurgeon(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final OtsPerSurgeonMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final OtsPerSurgeonMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "otCount"), 1.0));
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final OtsPerSurgeonMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final OtsPerSurgeonMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final OtsPerSurgeonMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final OtsPerSurgeonMapping context) {
		engine.getMapper("otForSurgeon").getMappings().values().parallelStream()
					.map(mapping -> (OtForSurgeonMapping) mapping)
		.filter(elt -> elt.getOp().equals(context.getOp()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
		});
	}
}
