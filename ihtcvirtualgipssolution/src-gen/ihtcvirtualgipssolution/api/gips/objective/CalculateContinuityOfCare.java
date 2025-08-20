package ihtcvirtualgipssolution.api.gips.objective;		

import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.eclipse.emf.ecore.EObject;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import org.emoflon.gips.core.GipsMapping;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedNurseForPatientMapping;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.emoflon.gips.core.milp.model.Constant;
import ihtcvirtualmetamodel.Patient;
import java.util.LinkedList;
import org.emoflon.gips.core.GipsTypeLinearFunction;
import org.emoflon.gips.intermediate.GipsIntermediate.TypeFunction;

public class CalculateContinuityOfCare extends GipsTypeLinearFunction<IhtcvirtualgipssolutionGipsAPI, Patient> {
	public CalculateContinuityOfCare(final IhtcvirtualgipssolutionGipsAPI engine, final TypeFunction function) {
		super(engine, function);
	}
	
	@Override
	protected void buildTerms(final Patient context) {
		
		builder_0(terms, context);
	}
		
	protected void builder_0(final List<Term> terms, final Patient context) {
		engine.getMapper("assignedNurseForPatient").getMappings().values().parallelStream()
					.map(mapping -> (AssignedNurseForPatientMapping) mapping)
		.filter(elt -> elt.getP().equals(context))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)1.0));
		});
	}
	
}
