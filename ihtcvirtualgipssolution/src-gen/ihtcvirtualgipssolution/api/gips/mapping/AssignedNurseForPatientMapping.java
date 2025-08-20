package ihtcvirtualgipssolution.api.gips.mapping;
		
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualgipssolution.api.rules.NursePatientTupelPattern;
import java.util.List;
import org.emoflon.gips.core.milp.model.Variable;
import org.emoflon.gips.core.gt.GipsGTMapping;
import java.util.Map;
import java.util.Collection;
import ihtcvirtualmetamodel.Patient;
import org.emoflon.gips.core.milp.model.RealVariable;
import org.emoflon.gips.core.milp.model.BinaryVariable;
import org.emoflon.gips.core.milp.model.IntegerVariable;
import ihtcvirtualgipssolution.api.matches.NursePatientTupelMatch;
		
public class AssignedNurseForPatientMapping extends GipsGTMapping<NursePatientTupelMatch, NursePatientTupelPattern> {
	public AssignedNurseForPatientMapping(final String ilpVariable, final NursePatientTupelMatch match) {
		super(ilpVariable, match);
	}

	public Nurse getN() {
		return match.getN();
	}
	public Patient getP() {
		return match.getP();
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