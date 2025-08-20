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

public class MappingConstraint60OnnurseWorkloadForDay extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, NurseWorkloadForDayMapping>{
	public MappingConstraint60OnnurseWorkloadForDay(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final NurseWorkloadForDayMapping context) {
		return 0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final NurseWorkloadForDayMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "excessiveWorkload"), 1.0));
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
}
