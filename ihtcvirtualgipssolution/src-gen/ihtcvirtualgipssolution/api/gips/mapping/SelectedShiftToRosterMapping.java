package ihtcvirtualgipssolution.api.gips.mapping;
		
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.matches.SelectShiftToRosterMatch;
import ihtcvirtualgipssolution.api.rules.SelectShiftToRosterRule;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
		
public class SelectedShiftToRosterMapping extends GipsGTMapping<SelectShiftToRosterMatch, SelectShiftToRosterRule> {
	
	public SelectedShiftToRosterMapping(final String ilpVariable, final SelectShiftToRosterMatch match) {
		super(ilpVariable, match);
	}

	public VirtualShiftToRoster getVsr() {
		return match.getVsr();
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