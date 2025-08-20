package ihtcvirtualgipssolution.api.gips.mapping;
		
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.matches.RoomForShiftMatch;
import ihtcvirtualmetamodel.Room;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualgipssolution.api.rules.RoomForShiftPattern;
		
public class CountPatientsForRoomMapping extends GipsGTMapping<RoomForShiftMatch, RoomForShiftPattern> {
	protected IntegerVariable patientCount;
	public CountPatientsForRoomMapping(final String ilpVariable, final RoomForShiftMatch match) {
		super(ilpVariable, match);
		patientCount = new IntegerVariable(name + "->patientCount");
	}

	public Room getR() {
		return match.getR();
	}
	public Shift getS() {
		return match.getS();
	}

	public IntegerVariable getPatientCount() {
		return patientCount;
	}
	
	public void setPatientCount(final IntegerVariable patientCount) {
		this.patientCount = patientCount;
	}
	
	public int getValueOfPatientCount() {
		return patientCount.getValue();
	}
	
	public void setValueOfPatientCount(final int patientCount) {
		this.patientCount.setValue(patientCount);
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
		return List.of("patientCount");
	}
	
	@Override
	public Map<String, Variable<?>> getAdditionalVariables() {
		return Map.of("patientCount", patientCount);
	}
	
	@Override
	public Collection<String> getFreeVariableNames() {
		return List.of("patientCount");
	}
	
	@Override
	public Map<String, Variable<?>> getFreeVariables() {
		return Map.of("patientCount", patientCount);
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
			case "patientCount" : {
				patientCount.setValue(Math.round((int) value));
				break;
			}
			default: throw new IllegalArgumentException("This mapping <" + name + "> does not have a variable with the symbolic name <" + valName + ">.");
		}
	}
}