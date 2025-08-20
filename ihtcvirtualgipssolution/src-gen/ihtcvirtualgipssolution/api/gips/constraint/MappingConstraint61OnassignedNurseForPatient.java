package ihtcvirtualgipssolution.api.gips.constraint;		

import ihtcvirtualgipssolution.api.gips.mapping.AssignedNurseForPatientMapping;
import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constraint;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.Collections;
import java.util.stream.Collectors;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedNursesToWorkloadMapping;

public class MappingConstraint61OnassignedNurseForPatient extends GipsMappingConstraint<IhtcvirtualgipssolutionGipsAPI, AssignedNurseForPatientMapping>{
	public MappingConstraint61OnassignedNurseForPatient(final IhtcvirtualgipssolutionGipsAPI engine, final MappingConstraint constraint) {
		super(engine, constraint);
	}
	
	@Override
	protected double buildConstantRhs(final AssignedNurseForPatientMapping context) {
		return 0.0;
	}
		
	@Override
	protected List<Term> buildVariableLhs(final AssignedNurseForPatientMapping context) {
		List<Term> terms = Collections.synchronizedList(new LinkedList<>());
		terms.add(new Term(context, 1.0));
		builder_0(terms, context);
		return terms;
	}
	
	@Override
	protected double buildConstantLhs(final AssignedNurseForPatientMapping context) {
		throw new UnsupportedOperationException("Constraint has an lhs that contains ilp variables.");
	}
	
	@Override
	protected boolean buildConstantExpression(final AssignedNurseForPatientMapping context) {
		throw new UnsupportedOperationException("Constraint has no constant boolean expression.");
	}
		
	@Override
	protected List<Constraint> buildAdditionalConstraints(final AssignedNurseForPatientMapping context) {
		throw new UnsupportedOperationException("Constraint has no depending or substitute constraints.");
	}
	protected void builder_0(final List<Term> terms, final AssignedNurseForPatientMapping context) {
		engine.getMapper("assignedNursesToWorkload").getMappings().values().parallelStream()
					.map(mapping -> (AssignedNursesToWorkloadMapping) mapping)
		.filter(elt -> elt.getVsr().getRoster().getNurse().equals(context.getN()) && elt.getVsw().getWorkload().getPatient().equals(context.getP()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
		});
	}
}
