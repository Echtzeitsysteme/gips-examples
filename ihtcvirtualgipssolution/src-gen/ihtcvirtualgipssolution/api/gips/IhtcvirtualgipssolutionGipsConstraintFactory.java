package ihtcvirtualgipssolution.api.gips;

import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint16OnselectedOperationDay;
import org.emoflon.gips.intermediate.GipsIntermediate.PatternConstraint;
import org.emoflon.gips.core.GipsMappingConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint6OncountPatientsForRoom;
import org.emoflon.gips.core.api.GipsConstraintFactory;
import org.emoflon.gips.intermediate.GipsIntermediate.TypeConstraint;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.gips.constraint.TypeConstraint12OnPatient;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint3OnmandatoryPatients;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint0OnroomForShift;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint11OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.RuleConstraint18OnselectExtendingShiftToFirstWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint14OnselectedOccupantNodes;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint4OncountPatientsForRoom;
import org.emoflon.gips.core.GipsConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint9OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint26OnassignedNursesToWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint10OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint22OnassignedGenderToRoomOnShift;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint1OnsurgeonOptimeTupel;
import ihtcvirtualgipssolution.api.gips.constraint.RuleConstraint17OnselectExtendingShiftToFirstWorkload;
import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.intermediate.GipsIntermediate.RuleConstraint;
import org.emoflon.gips.core.gt.GipsPatternConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint5OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.TypeConstraint13OnPatient;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import org.emoflon.gips.core.GipsTypeConstraint;
import org.emoflon.gips.core.GipsGlobalConstraint;
import org.emoflon.gips.intermediate.GipsIntermediate.Constraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint25OnassignedNursesToWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint8OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint23OnassignedGenderToRoomOnShift;
import ihtcvirtualgipssolution.api.gips.constraint.RuleConstraint15OnselectShiftToFirstWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint21OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint24OnassignedNursesToWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint2OnotCapacityTupel;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint19OnassignedPatientsToRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint20OnassignedPatientsToRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint7OncountPatientsForRoom;

public class IhtcvirtualgipssolutionGipsConstraintFactory extends GipsConstraintFactory<IhtcvirtualgipssolutionGipsAPI, IhtcvirtualgipssolutionAPI> {
	public IhtcvirtualgipssolutionGipsConstraintFactory(final IhtcvirtualgipssolutionGipsAPI engine, final IhtcvirtualgipssolutionAPI eMoflonApi) {
		super(engine, eMoflonApi);
	}
	
	@Override
	public GipsConstraint<IhtcvirtualgipssolutionGipsAPI, ? extends Constraint, ? extends Object> createConstraint(final Constraint constraint) {
		switch(constraint.getName()) {
			case "PatternConstraint0OnroomForShift" -> {
			return new PatternConstraint0OnroomForShift(engine, (PatternConstraint)constraint, eMoflonApi.roomForShift());
			}
			case "PatternConstraint1OnsurgeonOptimeTupel" -> {
			return new PatternConstraint1OnsurgeonOptimeTupel(engine, (PatternConstraint)constraint, eMoflonApi.surgeonOptimeTupel());
			}
			case "PatternConstraint2OnotCapacityTupel" -> {
			return new PatternConstraint2OnotCapacityTupel(engine, (PatternConstraint)constraint, eMoflonApi.otCapacityTupel());
			}
			case "PatternConstraint3OnmandatoryPatients" -> {
			return new PatternConstraint3OnmandatoryPatients(engine, (PatternConstraint)constraint, eMoflonApi.mandatoryPatients());
			}
			case "MappingConstraint4OncountPatientsForRoom" -> {
			return new MappingConstraint4OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint5OncountPatientsForRoom" -> {
			return new MappingConstraint5OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint6OncountPatientsForRoom" -> {
			return new MappingConstraint6OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint7OncountPatientsForRoom" -> {
			return new MappingConstraint7OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint8OncountPatientsForRoom" -> {
			return new MappingConstraint8OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint9OncountPatientsForRoom" -> {
			return new MappingConstraint9OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint10OncountPatientsForRoom" -> {
			return new MappingConstraint10OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint11OncountPatientsForRoom" -> {
			return new MappingConstraint11OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "TypeConstraint12OnPatient" -> {
			return new TypeConstraint12OnPatient(engine, (TypeConstraint)constraint);
			}
			case "TypeConstraint13OnPatient" -> {
			return new TypeConstraint13OnPatient(engine, (TypeConstraint)constraint);
			}
			case "MappingConstraint14OnselectedOccupantNodes" -> {
			return new MappingConstraint14OnselectedOccupantNodes(engine, (MappingConstraint)constraint);
			}
			case "RuleConstraint15OnselectShiftToFirstWorkload" -> {
			return new RuleConstraint15OnselectShiftToFirstWorkload(engine, (RuleConstraint)constraint, eMoflonApi.selectShiftToFirstWorkload());
			}
			case "MappingConstraint16OnselectedOperationDay" -> {
			return new MappingConstraint16OnselectedOperationDay(engine, (MappingConstraint)constraint);
			}
			case "RuleConstraint17OnselectExtendingShiftToFirstWorkload" -> {
			return new RuleConstraint17OnselectExtendingShiftToFirstWorkload(engine, (RuleConstraint)constraint, eMoflonApi.selectExtendingShiftToFirstWorkload());
			}
			case "RuleConstraint18OnselectExtendingShiftToFirstWorkload" -> {
			return new RuleConstraint18OnselectExtendingShiftToFirstWorkload(engine, (RuleConstraint)constraint, eMoflonApi.selectExtendingShiftToFirstWorkload());
			}
			case "MappingConstraint19OnassignedPatientsToRoom" -> {
			return new MappingConstraint19OnassignedPatientsToRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint20OnassignedPatientsToRoom" -> {
			return new MappingConstraint20OnassignedPatientsToRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint21OncountPatientsForRoom" -> {
			return new MappingConstraint21OncountPatientsForRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint22OnassignedGenderToRoomOnShift" -> {
			return new MappingConstraint22OnassignedGenderToRoomOnShift(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint23OnassignedGenderToRoomOnShift" -> {
			return new MappingConstraint23OnassignedGenderToRoomOnShift(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint24OnassignedNursesToWorkload" -> {
			return new MappingConstraint24OnassignedNursesToWorkload(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint25OnassignedNursesToWorkload" -> {
			return new MappingConstraint25OnassignedNursesToWorkload(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint26OnassignedNursesToWorkload" -> {
			return new MappingConstraint26OnassignedNursesToWorkload(engine, (MappingConstraint)constraint);
			}
			default -> {
				throw new IllegalArgumentException("Unknown constraint type: "+constraint);	
			}
		}
			
	}
}