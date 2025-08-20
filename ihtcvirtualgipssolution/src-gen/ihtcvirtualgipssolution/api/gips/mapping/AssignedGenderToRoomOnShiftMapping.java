package ihtcvirtualgipssolution.api.gips.mapping;
		
import java.util.Collection;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import ihtcvirtualgipssolution.api.matches.GenderRoomShiftMatch;
import ihtcvirtualgipssolution.api.rules.GenderRoomShiftPattern;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import ihtcvirtualmetamodel.Room;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualmetamodel.Gender;
import ihtcvirtualmetamodel.Shift;
		
public class AssignedGenderToRoomOnShiftMapping extends GipsGTMapping<GenderRoomShiftMatch, GenderRoomShiftPattern> {
	public AssignedGenderToRoomOnShiftMapping(final String ilpVariable, final GenderRoomShiftMatch match) {
		super(ilpVariable, match);
	}

	public Gender getG() {
		return match.getG();
	}
	public Room getR() {
		return match.getR();
	}
	public Shift getS() {
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