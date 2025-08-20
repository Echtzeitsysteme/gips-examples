package ihtcvirtualgipssolution.api.gips;

import org.emoflon.gips.intermediate.GipsIntermediate.MappingFunction;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.objective.CalculateOPenOTs;
import ihtcvirtualgipssolution.api.gips.objective.CalculateOtsPerSurgeon;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualgipssolution.api.gips.objective.CalculateNotAssignedOptionalPatients;
import org.emoflon.gips.core.GipsMappingLinearFunction;
import org.emoflon.gips.intermediate.GipsIntermediate.LinearFunction;
import ihtcvirtualgipssolution.api.gips.objective.CalculateNurseWorkload;
import ihtcvirtualgipssolution.api.gips.objective.CalculateWaitTime;
import ihtcvirtualgipssolution.api.gips.objective.CalculateUnqualifiedNurses;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.gips.objective.CalculateContinuityOfCare;
import org.emoflon.gips.core.api.GipsLinearFunctionFactory;
import org.emoflon.gips.intermediate.GipsIntermediate.PatternFunction;
import org.emoflon.gips.core.GipsLinearFunction;
import org.emoflon.gips.intermediate.GipsIntermediate.RuleFunction;
import org.emoflon.gips.core.GipsTypeLinearFunction;
import org.emoflon.gips.intermediate.GipsIntermediate.TypeFunction;
import ihtcvirtualgipssolution.api.gips.objective.CalculateAgeDifference;

public class IhtcvirtualgipssolutionGipsLinearFunctionFactory extends GipsLinearFunctionFactory<IhtcvirtualgipssolutionGipsAPI, IhtcvirtualgipssolutionAPI> {
	public IhtcvirtualgipssolutionGipsLinearFunctionFactory(final IhtcvirtualgipssolutionGipsAPI engine, final IhtcvirtualgipssolutionAPI eMoflonApi) {
		super(engine, eMoflonApi);
	}
	
	@Override
	public GipsLinearFunction<IhtcvirtualgipssolutionGipsAPI, ? extends LinearFunction, ? extends Object> createLinearFunction(final LinearFunction function) {
		switch(function.getName()) {
			case "calculateAgeDifference" -> {
				return new CalculateAgeDifference(engine, (PatternFunction)function, eMoflonApi.roomDayTupel());
			}
			case "calculateUnqualifiedNurses" -> {
				return new CalculateUnqualifiedNurses(engine, (MappingFunction)function);
			}
			case "calculateContinuityOfCare" -> {
				return new CalculateContinuityOfCare(engine, (TypeFunction)function);
			}
			case "calculateNurseWorkload" -> {
				return new CalculateNurseWorkload(engine, (MappingFunction)function);
			}
			case "calculateOPenOTs" -> {
				return new CalculateOPenOTs(engine, (TypeFunction)function);
			}
			case "calculateOtsPerSurgeon" -> {
				return new CalculateOtsPerSurgeon(engine, (MappingFunction)function);
			}
			case "calculateWaitTime" -> {
				return new CalculateWaitTime(engine, (MappingFunction)function);
			}
			case "calculateNotAssignedOptionalPatients" -> {
				return new CalculateNotAssignedOptionalPatients(engine, (PatternFunction)function, eMoflonApi.optionalPatients());
			}
			default -> {
				throw new IllegalArgumentException("Unknown linear function type: " + function);	
			}
		}
			
	}
}