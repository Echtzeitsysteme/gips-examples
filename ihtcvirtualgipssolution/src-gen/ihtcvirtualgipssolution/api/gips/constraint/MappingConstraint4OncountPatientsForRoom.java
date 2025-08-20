package ihtcvirtualgipssolution.api.gips.constraint;		

import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import ihtcvirtualgipssolution.api.gips.mapping.CountPatientsForRoomMapping;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;

public class MappingConstraint4OncountPatientsForRoom extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, CountPatientsForRoomMapping>{
	public MappingConstraint4OncountPatientsForRoom(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final CountPatientsForRoomMapping context) {
		return context.getR().getBeds();
	}
		
	@Override
	protected List<Term> buildVariableLhs(final CountPatientsForRoomMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(engine.getNonMappingVariable(context, "patientCount"), 1.0));
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
}
