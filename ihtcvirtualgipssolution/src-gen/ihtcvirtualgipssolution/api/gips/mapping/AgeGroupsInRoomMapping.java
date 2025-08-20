package ihtcvirtualgipssolution.api.gips.mapping;
		
import ihtcvirtualgipssolution.api.rules.AgeGroupsRoomDayPattern;
import java.util.Collection;
import ihtcvirtualmetamodel.Day;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualgipssolution.api.matches.AgeGroupsRoomDayMatch;
import ihtcvirtualmetamodel.Room;
import org.emoflon.gips.core.gt.GipsGTMapping;
import ihtcvirtualmetamodel.AgeGroup;
import java.util.Map;
import org.emoflon.gips.core.milp.model.IntegerVariable;
		
public class AgeGroupsInRoomMapping extends GipsGTMapping<AgeGroupsRoomDayMatch, AgeGroupsRoomDayPattern> {
	protected BinaryVariable maxAgeGroup;
	protected BinaryVariable minAgeGroup;
	public AgeGroupsInRoomMapping(final String ilpVariable, final AgeGroupsRoomDayMatch match) {
		super(ilpVariable, match);
		maxAgeGroup = new BinaryVariable(name + "->maxAgeGroup");
		minAgeGroup = new BinaryVariable(name + "->minAgeGroup");
	}

	public AgeGroup getAg() {
		return match.getAg();
	}
	public Day getD() {
		return match.getD();
	}
	public Room getR() {
		return match.getR();
	}

	public BinaryVariable getMaxAgeGroup() {
		return maxAgeGroup;
	}
	
	public BinaryVariable getMinAgeGroup() {
		return minAgeGroup;
	}
	
	public void setMaxAgeGroup(final BinaryVariable maxAgeGroup) {
		this.maxAgeGroup = maxAgeGroup;
	}
	
	public void setMinAgeGroup(final BinaryVariable minAgeGroup) {
		this.minAgeGroup = minAgeGroup;
	}
	
	public boolean getValueOfMaxAgeGroup() {
		return maxAgeGroup.getValue() != 0;
	}
	
	public boolean getValueOfMinAgeGroup() {
		return minAgeGroup.getValue() != 0;
	}
	
	public void setValueOfMaxAgeGroup(final boolean maxAgeGroup) {
		this.maxAgeGroup.setValue(maxAgeGroup ? 1 : 0);
	}
	
	public void setValueOfMinAgeGroup(final boolean minAgeGroup) {
		this.minAgeGroup.setValue(minAgeGroup ? 1 : 0);
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
		return List.of("maxAgeGroup",
		"minAgeGroup");
	}
	
	@Override
	public Map<String, Variable<?>> getAdditionalVariables() {
		return Map.of("maxAgeGroup", maxAgeGroup,
		"minAgeGroup", minAgeGroup);
	}
	
	@Override
	public Collection<String> getFreeVariableNames() {
		return List.of("maxAgeGroup",
		"minAgeGroup");
	}
	
	@Override
	public Map<String, Variable<?>> getFreeVariables() {
		return Map.of("maxAgeGroup", maxAgeGroup,
		"minAgeGroup", minAgeGroup);
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
			case "maxAgeGroup" : {
				maxAgeGroup.setValue((int) value);
				break;
			}
			case "minAgeGroup" : {
				minAgeGroup.setValue((int) value);
				break;
			}
			default: throw new IllegalArgumentException("This mapping <" + name + "> does not have a variable with the symbolic name <" + valName + ">.");
		}
	}
}