package ihtcvirtualgipssolution.api.gips;

import org.emoflon.gips.intermediate.GipsIntermediate.MappingFunction;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.intermediate.GipsIntermediate.PatternFunction;
import org.emoflon.gips.core.GipsLinearFunction;
import org.emoflon.gips.intermediate.GipsIntermediate.RuleFunction;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.core.GipsMappingLinearFunction;
import org.emoflon.gips.intermediate.GipsIntermediate.LinearFunction;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import org.emoflon.gips.core.GipsTypeLinearFunction;
import org.emoflon.gips.core.api.GipsLinearFunctionFactory;
import org.emoflon.gips.intermediate.GipsIntermediate.TypeFunction;

public class IhtcvirtualgipssolutionGipsLinearFunctionFactory extends GipsLinearFunctionFactory<IhtcvirtualgipssolutionGipsAPI, IhtcvirtualgipssolutionAPI> {
	public IhtcvirtualgipssolutionGipsLinearFunctionFactory(final IhtcvirtualgipssolutionGipsAPI engine, final IhtcvirtualgipssolutionAPI eMoflonApi) {
		super(engine, eMoflonApi);
	}
	
	@Override
	public GipsLinearFunction<IhtcvirtualgipssolutionGipsAPI, ? extends LinearFunction, ? extends Object> createLinearFunction(final LinearFunction function) {
		throw new IllegalArgumentException("Unknown linear function type: " + function);
			
	}
}