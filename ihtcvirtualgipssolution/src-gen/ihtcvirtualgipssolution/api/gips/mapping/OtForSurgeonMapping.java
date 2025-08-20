package ihtcvirtualgipssolution.api.gips.mapping;
		
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import ihtcvirtualmetamodel.Surgeon;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualgipssolution.api.rules.SurgeonOTForDayPattern;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualgipssolution.api.matches.SurgeonOTForDayMatch;
		
public class OtForSurgeonMapping extends GipsGTMapping<SurgeonOTForDayMatch, SurgeonOTForDayPattern> {
	public OtForSurgeonMapping(final String ilpVariable, final SurgeonOTForDayMatch match) {
		super(ilpVariable, match);
	}

	public OpTime getOp() {
		return match.getOp();
	}
	public OT getOt() {
		return match.getOt();
	}
	public Surgeon getS() {
		return match.getS();
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