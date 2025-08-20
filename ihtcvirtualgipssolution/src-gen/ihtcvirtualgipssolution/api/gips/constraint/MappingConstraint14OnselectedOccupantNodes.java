package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedOccupantNodesMapping;

public class MappingConstraint14OnselectedOccupantNodes extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, SelectedOccupantNodesMapping>{
	public MappingConstraint14OnselectedOccupantNodes(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final SelectedOccupantNodesMapping context) {
		return 1;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final SelectedOccupantNodesMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(context, 1.0));
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final SelectedOccupantNodesMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final SelectedOccupantNodesMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final SelectedOccupantNodesMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
}
