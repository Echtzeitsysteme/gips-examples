package ihtcvirtualgipssolution.api.gips.mapping;
		
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualgipssolution.api.matches.SelectShiftToFirstWorkloadMatch;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.rules.SelectShiftToFirstWorkloadRule;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Workload;
import org.emoflon.gips.core.milp.model.IntegerVariable;
		
public class SelectedShiftToFirstWorkloadMapping extends GipsGTMapping<SelectShiftToFirstWorkloadMatch, SelectShiftToFirstWorkloadRule> {
	
	public SelectedShiftToFirstWorkloadMapping(final String ilpVariable, final SelectShiftToFirstWorkloadMatch match) {
		super(ilpVariable, match);
	}

	public Patient getP() {
		return match.getP();
	}
	
	public VirtualShiftToWorkload getVsw() {
		return match.getVsw();
	}
	
	public VirtualWorkloadToCapacity getVwc() {
		return match.getVwc();
	}
	
	public Workload getW() {
		return match.getW();
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