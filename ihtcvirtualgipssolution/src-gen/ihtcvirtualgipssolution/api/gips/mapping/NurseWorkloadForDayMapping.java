package ihtcvirtualgipssolution.api.gips.mapping;
		
import ihtcvirtualmetamodel.Nurse;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.rules.NurseRosterTupelPattern;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualmetamodel.Roster;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualgipssolution.api.matches.NurseRosterTupelMatch;
		
public class NurseWorkloadForDayMapping extends GipsGTMapping<NurseRosterTupelMatch, NurseRosterTupelPattern> {
	protected IntegerVariable workloadDiff;
	protected IntegerVariable excessiveWorkload;
	public NurseWorkloadForDayMapping(final String ilpVariable, final NurseRosterTupelMatch match) {
		super(ilpVariable, match);
		workloadDiff = new IntegerVariable(name + "->workloadDiff");
		excessiveWorkload = new IntegerVariable(name + "->excessiveWorkload");
	}

	public Nurse getN() {
		return match.getN();
	}
	public Roster getRo() {
		return match.getRo();
	}

	public IntegerVariable getWorkloadDiff() {
		return workloadDiff;
	}
	
	public IntegerVariable getExcessiveWorkload() {
		return excessiveWorkload;
	}
	
	public void setWorkloadDiff(final IntegerVariable workloadDiff) {
		this.workloadDiff = workloadDiff;
	}
	
	public void setExcessiveWorkload(final IntegerVariable excessiveWorkload) {
		this.excessiveWorkload = excessiveWorkload;
	}
	
	public int getValueOfWorkloadDiff() {
		return workloadDiff.getValue();
	}
	
	public int getValueOfExcessiveWorkload() {
		return excessiveWorkload.getValue();
	}
	
	public void setValueOfWorkloadDiff(final int workloadDiff) {
		this.workloadDiff.setValue(workloadDiff);
	}
	
	public void setValueOfExcessiveWorkload(final int excessiveWorkload) {
		this.excessiveWorkload.setValue(excessiveWorkload);
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
		return List.of("workloadDiff",
		"excessiveWorkload");
	}
	
	@Override
	public Map<String, Variable<?>> getAdditionalVariables() {
		return Map.of("workloadDiff", workloadDiff,
		"excessiveWorkload", excessiveWorkload);
	}
	
	@Override
	public Collection<String> getFreeVariableNames() {
		return List.of("workloadDiff",
		"excessiveWorkload");
	}
	
	@Override
	public Map<String, Variable<?>> getFreeVariables() {
		return Map.of("workloadDiff", workloadDiff,
		"excessiveWorkload", excessiveWorkload);
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
			case "workloadDiff" : {
				workloadDiff.setValue(Math.round((int) value));
				break;
			}
			case "excessiveWorkload" : {
				excessiveWorkload.setValue(Math.round((int) value));
				break;
			}
			default: throw new IllegalArgumentException("This mapping <" + name + "> does not have a variable with the symbolic name <" + valName + ">.");
		}
	}
}