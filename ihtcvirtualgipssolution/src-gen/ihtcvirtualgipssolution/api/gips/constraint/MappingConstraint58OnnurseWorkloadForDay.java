package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import ihtcvirtualgipssolution.api.gips.mapping.NurseWorkloadForDayMapping;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import java.util.stream.Collectors;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedNursesToWorkloadMapping;

public class MappingConstraint58OnnurseWorkloadForDay extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, NurseWorkloadForDayMapping>{
	public MappingConstraint58OnnurseWorkloadForDay(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final NurseWorkloadForDayMapping context) {
		return 0.0 - ((-1.0) * ((-1.0) * (context.getRo().getMaxWorkload())));
	}
		
	@Override
	protected List<Term> buildVariableLhs(final NurseWorkloadForDayMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "workloadDiff"), 1.0));
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final NurseWorkloadForDayMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final NurseWorkloadForDayMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final NurseWorkloadForDayMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final NurseWorkloadForDayMapping context) {
		engine.getMapper("assignedNursesToWorkload").getMappings().values().parallelStream()
					.map(mapping -> (AssignedNursesToWorkloadMapping) mapping)
		.filter(elt -> elt.getVsr().getRoster().equals(context.getRo()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(-1.0) * ((1.0) * (elt.getVsw().getWorkload().getWorkloadValue()))));
		});
	}
}
