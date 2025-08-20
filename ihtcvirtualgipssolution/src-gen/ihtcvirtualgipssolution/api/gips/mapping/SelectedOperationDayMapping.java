package ihtcvirtualgipssolution.api.gips.mapping;
		
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.rules.SelectOperationDayRule;
import org.emoflon.gips.core.gt.GipsGTMapping;
import ihtcvirtualgipssolution.api.matches.SelectOperationDayMatch;
import ihtcvirtualmetamodel.VirtualWorkloadToOpTime;
import java.util.Map;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Workload;
import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.Surgeon;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.OpTime;
		
public class SelectedOperationDayMapping extends GipsGTMapping<SelectOperationDayMatch, SelectOperationDayRule> {
	
	public SelectedOperationDayMapping(final String ilpVariable, final SelectOperationDayMatch match) {
		super(ilpVariable, match);
	}

	public Capacity getC() {
		return match.getC();
	}
	
	public OpTime getOpTime() {
		return match.getOpTime();
	}
	
	public Patient getP() {
		return match.getP();
	}
	
	public Surgeon getS() {
		return match.getS();
	}
	
	public VirtualOpTimeToCapacity getVopc() {
		return match.getVopc();
	}
	
	public VirtualWorkloadToCapacity getVwc() {
		return match.getVwc();
	}
	
	public VirtualWorkloadToOpTime getVwop() {
		return match.getVwop();
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