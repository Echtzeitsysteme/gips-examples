package ihtcvirtualgipssolution.api.gips.objective;		

import java.util.List;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingFunction;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constant;
import ihtcvirtualgipssolution.api.gips.mapping.NurseWorkloadForDayMapping;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.GipsMappingLinearFunction;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;

public class CalculateNurseWorkload extends GipsMappingLinearFunction<IhtcvirtualgipssolutionGipsAPI, NurseWorkloadForDayMapping>{
	public CalculateNurseWorkload(final IhtcvirtualgipssolutionGipsAPI engine, final MappingFunction function) {
		super(engine, function);
	}
	
	@Override
	protected void buildTerms(final NurseWorkloadForDayMapping context) {
		
		terms.add(new Term(engine.getNonMappingVariable(context, "excessiveWorkload"), 1.0));
	}
	
	
}
