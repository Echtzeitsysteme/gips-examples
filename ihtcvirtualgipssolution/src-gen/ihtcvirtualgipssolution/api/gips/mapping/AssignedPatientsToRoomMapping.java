package ihtcvirtualgipssolution.api.gips.mapping;
		
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualgipssolution.api.matches.PatientForRoomMatch;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualmetamodel.Room;
import org.emoflon.gips.core.gt.GipsGTMapping;
import ihtcvirtualgipssolution.api.rules.PatientForRoomPattern;
import java.util.Map;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Workload;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.Shift;
		
public class AssignedPatientsToRoomMapping extends GipsGTMapping<PatientForRoomMatch, PatientForRoomPattern> {
	protected IntegerVariable skillDiff;
	protected IntegerVariable penalizedSkillDiff;
	public AssignedPatientsToRoomMapping(final String ilpVariable, final PatientForRoomMatch match) {
		super(ilpVariable, match);
		skillDiff = new IntegerVariable(name + "->skillDiff");
		penalizedSkillDiff = new IntegerVariable(name + "->penalizedSkillDiff");
	}

	public Patient getP() {
		return match.getP();
	}
	public Room getR() {
		return match.getR();
	}
	public Shift getS() {
		return match.getS();
	}
	public VirtualShiftToWorkload getVsw() {
		return match.getVsw();
	}
	public Workload getW() {
		return match.getW();
	}

	public IntegerVariable getSkillDiff() {
		return skillDiff;
	}
	
	public IntegerVariable getPenalizedSkillDiff() {
		return penalizedSkillDiff;
	}
	
	public void setSkillDiff(final IntegerVariable skillDiff) {
		this.skillDiff = skillDiff;
	}
	
	public void setPenalizedSkillDiff(final IntegerVariable penalizedSkillDiff) {
		this.penalizedSkillDiff = penalizedSkillDiff;
	}
	
	public int getValueOfSkillDiff() {
		return skillDiff.getValue();
	}
	
	public int getValueOfPenalizedSkillDiff() {
		return penalizedSkillDiff.getValue();
	}
	
	public void setValueOfSkillDiff(final int skillDiff) {
		this.skillDiff.setValue(skillDiff);
	}
	
	public void setValueOfPenalizedSkillDiff(final int penalizedSkillDiff) {
		this.penalizedSkillDiff.setValue(penalizedSkillDiff);
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
		return List.of("skillDiff",
		"penalizedSkillDiff");
	}
	
	@Override
	public Map<String, Variable<?>> getAdditionalVariables() {
		return Map.of("skillDiff", skillDiff,
		"penalizedSkillDiff", penalizedSkillDiff);
	}
	
	@Override
	public Collection<String> getFreeVariableNames() {
		return List.of("skillDiff",
		"penalizedSkillDiff");
	}
	
	@Override
	public Map<String, Variable<?>> getFreeVariables() {
		return Map.of("skillDiff", skillDiff,
		"penalizedSkillDiff", penalizedSkillDiff);
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
			case "skillDiff" : {
				skillDiff.setValue(Math.round((int) value));
				break;
			}
			case "penalizedSkillDiff" : {
				penalizedSkillDiff.setValue(Math.round((int) value));
				break;
			}
			default: throw new IllegalArgumentException("This mapping <" + name + "> does not have a variable with the symbolic name <" + valName + ">.");
		}
	}
}