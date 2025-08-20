package ihtcvirtualgipssolution.api.gips;

import ihtcvirtualgipssolution.api.gips.mapper.AssignedPatientsToRoomMapper;
import ihtcvirtualgipssolution.api.gips.mapper.AssignedNursesToWorkloadMapper;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.mapper.AssignedNurseForPatientMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedShiftToFirstWorkloadMapper;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import org.emoflon.gips.core.GipsMapping;
import ihtcvirtualgipssolution.api.gips.mapper.CountPatientsForRoomMapper;
import org.emoflon.gips.core.GipsMapper;
import ihtcvirtualgipssolution.api.gips.mapper.NurseWorkloadForDayMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedOperationDayMapper;
import ihtcvirtualgipssolution.api.gips.mapper.OtForSurgeonMapper;
import org.emoflon.gips.core.api.GipsMapperFactory;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedOccupantNodesMapper;
import ihtcvirtualgipssolution.api.gips.mapper.OpenOTsMapper;
import ihtcvirtualgipssolution.api.gips.mapper.AgeGroupsInRoomMapper;
import ihtcvirtualgipssolution.api.gips.mapper.OtsPerSurgeonMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedExtendingShiftToFirstWorkloadMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedShiftToRosterMapper;
import ihtcvirtualgipssolution.api.gips.mapper.AssignedGenderToRoomOnShiftMapper;

public class IhtcvirtualgipssolutionGipsMapperFactory extends GipsMapperFactory<IhtcvirtualgipssolutionAPI> {
	public IhtcvirtualgipssolutionGipsMapperFactory(final GipsEngine engine, final IhtcvirtualgipssolutionAPI eMoflonApi) {
		super(engine, eMoflonApi);
	}
	
	@Override
	public GipsMapper<? extends GipsMapping> createMapper(final Mapping mapping) {
		switch(mapping.getName()) {
			case "selectedShiftToFirstWorkload" -> {
				return new SelectedShiftToFirstWorkloadMapper(engine, mapping, eMoflonApi.selectShiftToFirstWorkload());
			}
			case "selectedExtendingShiftToFirstWorkload" -> {
				return new SelectedExtendingShiftToFirstWorkloadMapper(engine, mapping, eMoflonApi.selectExtendingShiftToFirstWorkload());
			}
			case "selectedOperationDay" -> {
				return new SelectedOperationDayMapper(engine, mapping, eMoflonApi.selectOperationDay());
			}
			case "selectedShiftToRoster" -> {
				return new SelectedShiftToRosterMapper(engine, mapping, eMoflonApi.selectShiftToRoster());
			}
			case "selectedOccupantNodes" -> {
				return new SelectedOccupantNodesMapper(engine, mapping, eMoflonApi.virtualNodesForOccupant());
			}
			case "countPatientsForRoom" -> {
				return new CountPatientsForRoomMapper(engine, mapping, eMoflonApi.roomForShift());
			}
			case "assignedPatientsToRoom" -> {
				return new AssignedPatientsToRoomMapper(engine, mapping, eMoflonApi.patientForRoom());
			}
			case "assignedGenderToRoomOnShift" -> {
				return new AssignedGenderToRoomOnShiftMapper(engine, mapping, eMoflonApi.genderRoomShift());
			}
			case "openOTs" -> {
				return new OpenOTsMapper(engine, mapping, eMoflonApi.otCapacityTupel());
			}
			case "otForSurgeon" -> {
				return new OtForSurgeonMapper(engine, mapping, eMoflonApi.surgeonOTForDay());
			}
			case "otsPerSurgeon" -> {
				return new OtsPerSurgeonMapper(engine, mapping, eMoflonApi.surgeonOptimeTupel());
			}
			case "ageGroupsInRoom" -> {
				return new AgeGroupsInRoomMapper(engine, mapping, eMoflonApi.ageGroupsRoomDay());
			}
			case "assignedNursesToWorkload" -> {
				return new AssignedNursesToWorkloadMapper(engine, mapping, eMoflonApi.nursetoWorkload());
			}
			case "nurseWorkloadForDay" -> {
				return new NurseWorkloadForDayMapper(engine, mapping, eMoflonApi.nurseRosterTupel());
			}
			case "assignedNurseForPatient" -> {
				return new AssignedNurseForPatientMapper(engine, mapping, eMoflonApi.nursePatientTupel());
			}
			default -> {
				throw new IllegalArgumentException("Unknown mapping type: "+mapping);	
			}
		}
			
	}
}