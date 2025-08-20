package ihtcvirtualgipssolution.api.gips.objective;		

import java.util.List;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingFunction;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.milp.model.Constant;
import ihtcvirtualgipssolution.api.gips.mapping.AssignedPatientsToRoomMapping;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.GipsMappingLinearFunction;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.Term;

public class CalculateUnqualifiedNurses extends GipsMappingLinearFunction<IhtcvirtualgipssolutionGipsAPI, AssignedPatientsToRoomMapping>{
	public CalculateUnqualifiedNurses(final IhtcvirtualgipssolutionGipsAPI engine, final MappingFunction function) {
		super(engine, function);
	}
	
	@Override
	protected void buildTerms(final AssignedPatientsToRoomMapping context) {
		
		terms.add(new Term(engine.getNonMappingVariable(context, "penalizedSkillDiff"), 1.0));
	}
	
	
}
