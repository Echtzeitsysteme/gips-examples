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
import ihtcvirtualgipssolution.api.gips.mapping.OpenOTsMapping;

public class MappingConstraint24OnopenOTs extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, OpenOTsMapping>{
	public MappingConstraint24OnopenOTs(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final OpenOTsMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final OpenOTsMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		builder_0(terms, context);
		terms.add(new Term(context, builder_1(context)));
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final OpenOTsMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final OpenOTsMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final OpenOTsMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final OpenOTsMapping context) {
		engine.getMapper("selectedOperationDay").getMappings().values().parallelStream()
					.map(mapping -> (SelectedOperationDayMapping) mapping)
		.filter(elt -> elt.getVwc().getCapacity().equals(context.getC()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)1.0));
		});
	}
	protected double builder_1(final OpenOTsMapping context) {
		return (-1.0) * ((1.0) * (1000));
	}
}
