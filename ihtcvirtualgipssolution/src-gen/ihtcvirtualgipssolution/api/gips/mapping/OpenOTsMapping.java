package ihtcvirtualgipssolution.api.gips.mapping;
		
import ihtcvirtualgipssolution.api.rules.OtCapacityTupelPattern;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.matches.OtCapacityTupelMatch;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualmetamodel.Capacity;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.OT;
		
public class OpenOTsMapping extends GipsGTMapping<OtCapacityTupelMatch, OtCapacityTupelPattern> {
	public OpenOTsMapping(final String ilpVariable, final OtCapacityTupelMatch match) {
		super(ilpVariable, match);
	}

	public Capacity getC() {
		return match.getC();
	}
	public OT getOt() {
		return match.getOt();
	}

	@Override
	public boolean hasAdditionalVariables() {
		return false;
	}
	
	@Override
	public boolean hasBoundVariables() {
		return false;
	}
	
	@Override
	public boolean hasFreeVariables() {
		return false;
	}
	
	@Override
	public Collection<String> getAdditionalVariableNames() {
		return List.of();
	}
	
	@Override
	public Map<String, Variable<?>> getAdditionalVariables() {
		return Map.of();
	}
	
	@Override
	public Collection<String> getFreeVariableNames() {
		return List.of();
	}
	
	@Override
	public Map<String, Variable<?>> getFreeVariables() {
		return Map.of();
	}
	
	@Override
	public Collection<String> getBoundVariableNames() {
		return List.of();
	}
	
	@Override
	public Map<String, Variable<?>> getBoundVariables() {
		return Map.of();
	}
	
	@Override
	public void setAdditionalVariableValue(final String valName, final double value) {
		throw new UnsupportedOperationException("This mapping <" + name + "> does not have any additonal variables.");
	}
}