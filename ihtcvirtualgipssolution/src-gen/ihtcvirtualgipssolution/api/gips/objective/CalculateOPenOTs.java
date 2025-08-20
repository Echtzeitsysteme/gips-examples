package ihtcvirtualgipssolution.api.gips.objective;		

import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualmetamodel.Day;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.eclipse.emf.ecore.EObject;
import org.emoflon.gips.core.milp.model.Term;
import java.util.stream.Collectors;
import org.emoflon.gips.core.GipsMapping;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.emoflon.gips.core.milp.model.Constant;
import java.util.LinkedList;
import org.emoflon.gips.core.GipsTypeLinearFunction;
import org.emoflon.gips.intermediate.GipsIntermediate.TypeFunction;
import ihtcvirtualgipssolution.api.gips.mapping.OpenOTsMapping;

public class CalculateOPenOTs extends GipsTypeLinearFunction<IhtcvirtualgipssolutionGipsAPI, Day> {
	public CalculateOPenOTs(final IhtcvirtualgipssolutionGipsAPI engine, final TypeFunction function) {
		super(engine, function);
	}
	
	@Override
	protected void buildTerms(final Day context) {
		
		builder_0(terms, context);
	}
		
	protected void builder_0(final List<Term> terms, final Day context) {
		engine.getMapper("openOTs").getMappings().values().parallelStream()
					.map(mapping -> (OpenOTsMapping) mapping)
		.filter(elt -> elt.getC().getDay() == context.getNumber())
		.forEach(elt -> {
			terms.add(new Term(elt, (double)1.0));
		});
	}
	
}
