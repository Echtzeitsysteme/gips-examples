package ihtcvirtualgipssolution.api.gips.objective;		

import java.util.List;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingFunction;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constant;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.GipsMappingLinearFunction;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;
import ihtcvirtualgipssolution.api.gips.mapping.SelectedShiftToFirstWorkloadMapping;

public class CalculateWaitTime extends GipsMappingLinearFunction<IhtcvirtualgipssolutionGipsAPI, SelectedShiftToFirstWorkloadMapping>{
	public CalculateWaitTime(final IhtcvirtualgipssolutionGipsAPI engine, final MappingFunction function) {
		super(engine, function);
	}
	
	@Override
	protected void buildTerms(final SelectedShiftToFirstWorkloadMapping context) {
		
		terms.add(new Term(context, builder_0(context)));
	}
	
	protected double builder_0(final SelectedShiftToFirstWorkloadMapping context) {
		return ((context.getVsw().getShift().getShiftNo()) / (3) + (-1.0) * (context.getP().getEarliestDay())) * (1.0);
	}
	
}
