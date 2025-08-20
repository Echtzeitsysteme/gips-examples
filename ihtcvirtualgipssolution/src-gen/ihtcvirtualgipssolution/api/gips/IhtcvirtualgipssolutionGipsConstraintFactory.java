package ihtcvirtualgipssolution.api.gips;

import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint34OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint34OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint34OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint44OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.DisjunctMappingConstraint36OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint29OnotsPerSurgeon;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint44OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint44OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint6OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint43OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint35OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint43OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint43OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint11OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint43OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint25OnotForSurgeon;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint35OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint35OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint35OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint43OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.DisjunctMappingConstraint42OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint35OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint10OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.DisjunctMappingConstraint46OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint5OncountPatientsForRoom;
import org.emoflon.gips.intermediate.GipsIntermediate.MappingConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint35OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint35OnageGroupsInRoom_EQ5;
import org.emoflon.gips.core.GipsGlobalConstraint;
import org.emoflon.gips.intermediate.GipsIntermediate.Constraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint43OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint43OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint8OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint58OnnurseWorkloadForDay;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint24OnopenOTs;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint7OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint41OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint16OnselectedOperationDay;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint41OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint41OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint41OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint41OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint41OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint41OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint61OnassignedNurseForPatient;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint52OnassignedPatientsToRoom;
import org.emoflon.gips.intermediate.GipsIntermediate.TypeConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint56OnassignedNursesToWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint47OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint47OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.DisjunctMappingConstraint32OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint47OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint47OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint47OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint14OnselectedOccupantNodes;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint47OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint55OnassignedNursesToWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint33OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint33OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint33OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint33OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint40OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint33OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint40OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint33OnageGroupsInRoom_EQ5;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint40OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint33OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint40OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint40OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint40OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint40OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.TypeConstraint13OnPatient;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint59OnnurseWorkloadForDay;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint47OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.IhtcvirtualgipssolutionGipsAPI;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint60OnnurseWorkloadForDay;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint48OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.DisjunctMappingConstraint39OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint48OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint23OnassignedGenderToRoomOnShift;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint48OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint48OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint48OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint48OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint48OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint34OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint53OnassignedPatientsToRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint34OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint34OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint34OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint57OnassignedNursesToWorkload;
import org.emoflon.gips.intermediate.GipsIntermediate.PatternConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint62OnassignedNurseForPatient;
import org.emoflon.gips.core.GipsMappingConstraint;
import org.emoflon.gips.core.api.GipsConstraintFactory;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint3OnmandatoryPatients;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint0OnroomForShift;
import org.emoflon.gips.core.GipsConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint22OnassignedGenderToRoomOnShift;
import org.emoflon.gips.core.gt.GipsPatternConstraint;
import org.emoflon.gips.core.GipsTypeConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint27OnotsPerSurgeon;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint54OnassignedPatientsToRoom;
import ihtcvirtualgipssolution.api.gips.constraint.RuleConstraint15OnselectShiftToFirstWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint2OnotCapacityTupel;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint19OnassignedPatientsToRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint28OnotsPerSurgeon;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint37OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint37OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.DisjunctMappingConstraint49OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint37OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint37OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint37OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint30OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint37OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint37OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.TypeConstraint12OnPatient;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint50OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint50OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint31OnageGroupsInRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint50OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint50OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.RuleConstraint18OnselectExtendingShiftToFirstWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint50OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint50OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint4OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint50OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint45OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint9OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint45OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint45OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint38OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint45OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint38OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint45OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.RuleConstraint17OnselectExtendingShiftToFirstWorkload;
import ihtcvirtualgipssolution.api.gips.constraint.PatternConstraint1OnsurgeonOptimeTupel;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint45OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint38OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint45OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint38OnageGroupsInRoom_EQ1;
import org.emoflon.gips.intermediate.GipsIntermediate.RuleConstraint;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint38OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint38OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint26OnotForSurgeon;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint38OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint21OncountPatientsForRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint44OnageGroupsInRoom_EQ6;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint44OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint44OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint44OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint51OnageGroupsInRoom_EQ0;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint51OnageGroupsInRoom_EQ1;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint51OnageGroupsInRoom_EQ2;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint51OnageGroupsInRoom_EQ3;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint51OnageGroupsInRoom_EQ4;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint20OnassignedPatientsToRoom;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint51OnageGroupsInRoom_EQ5;
import ihtcvirtualgipssolution.api.gips.constraint.MappingConstraint51OnageGroupsInRoom_EQ6;

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
			case "MappingConstraint24OnopenOTs" -> {
			return new MappingConstraint24OnopenOTs(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint25OnotForSurgeon" -> {
			return new MappingConstraint25OnotForSurgeon(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint26OnotForSurgeon" -> {
			return new MappingConstraint26OnotForSurgeon(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint27OnotsPerSurgeon" -> {
			return new MappingConstraint27OnotsPerSurgeon(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint28OnotsPerSurgeon" -> {
			return new MappingConstraint28OnotsPerSurgeon(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint29OnotsPerSurgeon" -> {
			return new MappingConstraint29OnotsPerSurgeon(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint30OnageGroupsInRoom" -> {
			return new MappingConstraint30OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint31OnageGroupsInRoom" -> {
			return new MappingConstraint31OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "DisjunctMappingConstraint32OnageGroupsInRoom" -> {
			return new DisjunctMappingConstraint32OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint33OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint33OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint33OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint33OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint33OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint33OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint33OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint33OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint33OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint33OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint33OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint33OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint33OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint33OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint34OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint34OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint34OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint34OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint34OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint34OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint34OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint34OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint34OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint34OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint34OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint34OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint34OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint34OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint35OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint35OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint35OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint35OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint35OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint35OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint35OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint35OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint35OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint35OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint35OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint35OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint35OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint35OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "DisjunctMappingConstraint36OnageGroupsInRoom" -> {
			return new DisjunctMappingConstraint36OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint37OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint37OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint37OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint37OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint37OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint37OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint37OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint37OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint37OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint37OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint37OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint37OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint37OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint37OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint38OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint38OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint38OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint38OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint38OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint38OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint38OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint38OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint38OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint38OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint38OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint38OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint38OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint38OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "DisjunctMappingConstraint39OnageGroupsInRoom" -> {
			return new DisjunctMappingConstraint39OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint40OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint40OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint40OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint40OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint40OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint40OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint40OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint40OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint40OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint40OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint40OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint40OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint40OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint40OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint41OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint41OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint41OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint41OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint41OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint41OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint41OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint41OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint41OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint41OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint41OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint41OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint41OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint41OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "DisjunctMappingConstraint42OnageGroupsInRoom" -> {
			return new DisjunctMappingConstraint42OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint43OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint43OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint43OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint43OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint43OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint43OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint43OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint43OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint43OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint43OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint43OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint43OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint43OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint43OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint44OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint44OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint44OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint44OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint44OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint44OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint44OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint44OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint44OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint44OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint44OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint44OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint44OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint44OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint45OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint45OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint45OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint45OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint45OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint45OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint45OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint45OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint45OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint45OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint45OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint45OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint45OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint45OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "DisjunctMappingConstraint46OnageGroupsInRoom" -> {
			return new DisjunctMappingConstraint46OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint47OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint47OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint47OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint47OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint47OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint47OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint47OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint47OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint47OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint47OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint47OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint47OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint47OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint47OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint48OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint48OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint48OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint48OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint48OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint48OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint48OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint48OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint48OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint48OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint48OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint48OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint48OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint48OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "DisjunctMappingConstraint49OnageGroupsInRoom" -> {
			return new DisjunctMappingConstraint49OnageGroupsInRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint50OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint50OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint50OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint50OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint50OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint50OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint50OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint50OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint50OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint50OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint50OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint50OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint50OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint50OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint51OnageGroupsInRoom_EQ0" -> {
			return new MappingConstraint51OnageGroupsInRoom_EQ0(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint51OnageGroupsInRoom_EQ1" -> {
			return new MappingConstraint51OnageGroupsInRoom_EQ1(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint51OnageGroupsInRoom_EQ2" -> {
			return new MappingConstraint51OnageGroupsInRoom_EQ2(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint51OnageGroupsInRoom_EQ3" -> {
			return new MappingConstraint51OnageGroupsInRoom_EQ3(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint51OnageGroupsInRoom_EQ4" -> {
			return new MappingConstraint51OnageGroupsInRoom_EQ4(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint51OnageGroupsInRoom_EQ5" -> {
			return new MappingConstraint51OnageGroupsInRoom_EQ5(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint51OnageGroupsInRoom_EQ6" -> {
			return new MappingConstraint51OnageGroupsInRoom_EQ6(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint52OnassignedPatientsToRoom" -> {
			return new MappingConstraint52OnassignedPatientsToRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint53OnassignedPatientsToRoom" -> {
			return new MappingConstraint53OnassignedPatientsToRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint54OnassignedPatientsToRoom" -> {
			return new MappingConstraint54OnassignedPatientsToRoom(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint55OnassignedNursesToWorkload" -> {
			return new MappingConstraint55OnassignedNursesToWorkload(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint56OnassignedNursesToWorkload" -> {
			return new MappingConstraint56OnassignedNursesToWorkload(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint57OnassignedNursesToWorkload" -> {
			return new MappingConstraint57OnassignedNursesToWorkload(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint58OnnurseWorkloadForDay" -> {
			return new MappingConstraint58OnnurseWorkloadForDay(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint59OnnurseWorkloadForDay" -> {
			return new MappingConstraint59OnnurseWorkloadForDay(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint60OnnurseWorkloadForDay" -> {
			return new MappingConstraint60OnnurseWorkloadForDay(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint61OnassignedNurseForPatient" -> {
			return new MappingConstraint61OnassignedNurseForPatient(engine, (MappingConstraint)constraint);
			}
			case "MappingConstraint62OnassignedNurseForPatient" -> {
			return new MappingConstraint62OnassignedNurseForPatient(engine, (MappingConstraint)constraint);
			}
			default -> {
				throw new IllegalArgumentException("Unknown constraint type: "+constraint);	
			}
		}
			
	}
}