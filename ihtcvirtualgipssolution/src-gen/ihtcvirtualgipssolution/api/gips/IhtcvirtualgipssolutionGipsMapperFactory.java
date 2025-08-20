package ihtcvirtualgipssolution.api.gips;

import ihtcvirtualgipssolution.api.gips.mapper.AssignedPatientsToRoomMapper;
import ihtcvirtualgipssolution.api.gips.mapper.AssignedNursesToWorkloadMapper;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedShiftToFirstWorkloadMapper;
import org.emoflon.gips.intermediate.GipsIntermediate.Mapping;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import org.emoflon.gips.core.GipsMapping;
import ihtcvirtualgipssolution.api.gips.mapper.CountPatientsForRoomMapper;
import org.emoflon.gips.core.GipsMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedOperationDayMapper;
import org.emoflon.gips.core.api.GipsMapperFactory;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedOccupantNodesMapper;
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
			case "assignedNursesToWorkload" -> {
				return new AssignedNursesToWorkloadMapper(engine, mapping, eMoflonApi.nursetoWorkload());
			}
			default -> {
				throw new IllegalArgumentException("Unknown mapping type: "+mapping);	
			}
		}
			
	}
}