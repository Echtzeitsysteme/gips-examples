package ihtcvirtualgipssolution.api.gips.mapping;
		
import java.util.Collection;
import ihtcvirtualgipssolution.api.rules.NursetoWorkloadPattern;
import ihtcvirtualgipssolution.api.matches.NursetoWorkloadMatch;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.Shift;
		
public class AssignedNursesToWorkloadMapping extends GipsGTMapping<NursetoWorkloadMatch, NursetoWorkloadPattern> {
	public AssignedNursesToWorkloadMapping(final String ilpVariable, final NursetoWorkloadMatch match) {
		super(ilpVariable, match);
	}

	public Shift getS() {
		return match.getS();
	}
	public VirtualShiftToRoster getVsr() {
		return match.getVsr();
	}
	public VirtualShiftToWorkload getVsw() {
		return match.getVsw();
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