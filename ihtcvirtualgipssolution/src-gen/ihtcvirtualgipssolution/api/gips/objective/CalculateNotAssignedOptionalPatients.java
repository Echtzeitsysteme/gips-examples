package ihtcvirtualgipssolution.api.gips.objective;		

import org.emoflon.gips.core.gt.GipsPatternLinearFunction;
import java.util.List;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.intermediate.GipsIntermediate.PatternFunction;
import org.emoflon.gips.core.milp.model.Constant;
import ihtcvirtualgipssolution.api.matches.OptionalPatientsMatch;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualgipssolution.api.rules.OptionalPatientsPattern;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class CalculateNotAssignedOptionalPatients extends GipsPatternLinearFunction<IhtcvirtualgipssolutionGipsAPI, OptionalPatientsMatch, OptionalPatientsPattern>{
	public CalculateNotAssignedOptionalPatients(final IhtcvirtualgipssolutionGipsAPI engine, final PatternFunction function, final OptionalPatientsPattern pattern) {
		super(engine, function, pattern);
	}
	
	@Override
	protected void buildTerms(final OptionalPatientsMatch context) {
		
		constantTerms.add(new Constant(1));
		builder_0(terms, context);
	}
	
	protected void builder_0(final List<Term> terms, final OptionalPatientsMatch context) {
		engine.getMapper("selectedShiftToFirstWorkload").getMappings().values().parallelStream()
					.map(mapping -> (SelectedShiftToFirstWorkloadMapping) mapping)
		.filter(elt -> elt.getP().equals(context.getP()))
		.forEach(elt -> {
			terms.add(new Term(elt, (double)(-1.0) * (1.0)));
		});
	}
	
}
