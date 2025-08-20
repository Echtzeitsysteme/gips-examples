package ihtcvirtualgipssolution.api.gips.mapping;
		
import ihtcvirtualgipssolution.api.matches.SurgeonOptimeTupelMatch;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.rules.SurgeonOptimeTupelPattern;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualmetamodel.Surgeon;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.OpTime;
		
public class OtsPerSurgeonMapping extends GipsGTMapping<SurgeonOptimeTupelMatch, SurgeonOptimeTupelPattern> {
	protected IntegerVariable otCount;
	protected IntegerVariable penalizedOTs;
	public OtsPerSurgeonMapping(final String ilpVariable, final SurgeonOptimeTupelMatch match) {
		super(ilpVariable, match);
		otCount = new IntegerVariable(name + "->otCount");
		penalizedOTs = new IntegerVariable(name + "->penalizedOTs");
	}

	public OpTime getOp() {
		return match.getOp();
	}
	public Surgeon getS() {
		return match.getS();
	}

	public IntegerVariable getOtCount() {
		return otCount;
	}
	
	public IntegerVariable getPenalizedOTs() {
		return penalizedOTs;
	}
	
	public void setOtCount(final IntegerVariable otCount) {
		this.otCount = otCount;
	}
	
	public void setPenalizedOTs(final IntegerVariable penalizedOTs) {
		this.penalizedOTs = penalizedOTs;
	}
	
	public int getValueOfOtCount() {
		return otCount.getValue();
	}
	
	public int getValueOfPenalizedOTs() {
		return penalizedOTs.getValue();
	}
	
	public void setValueOfOtCount(final int otCount) {
		this.otCount.setValue(otCount);
	}
	
	public void setValueOfPenalizedOTs(final int penalizedOTs) {
		this.penalizedOTs.setValue(penalizedOTs);
	}
	
	@Override
	public boolean hasAdditionalVariables() {
		return true;
	}
	
	@Override
	public boolean hasBoundVariables() {
		return false;
	}
	
	@Override
	public boolean hasFreeVariables() {
		return true;
	}
	
	@Override
	public Collection<String> getAdditionalVariableNames() {
		return List.of("otCount",
		"penalizedOTs");
	}
	
	@Override
	public Map<String, Variable<?>> getAdditionalVariables() {
		return Map.of("otCount", otCount,
		"penalizedOTs", penalizedOTs);
	}
	
	@Override
	public Collection<String> getFreeVariableNames() {
		return List.of("otCount",
		"penalizedOTs");
	}
	
	@Override
	public Map<String, Variable<?>> getFreeVariables() {
		return Map.of("otCount", otCount,
		"penalizedOTs", penalizedOTs);
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
		switch(valName) {
			case "otCount" : {
				otCount.setValue(Math.round((int) value));
				break;
			}
			case "penalizedOTs" : {
				penalizedOTs.setValue(Math.round((int) value));
				break;
			}
			default: throw new IllegalArgumentException("This mapping <" + name + "> does not have a variable with the symbolic name <" + valName + ">.");
		}
	}
}