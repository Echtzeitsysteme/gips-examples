package ihtcvirtualmetamodel.importexport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.Roster;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToOpTime;

/**
 * This model exporter can be used to convert an EMF model to the respective
 * JSON output format required by the competition. Noteworthy: This exporter
 * assumes no post-processing took place, i.e., it directly operates on virtual
 * objects.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class ModelToJsonNoPostProcExporter extends ModelToJsonExporter {

	/**
	 * Initializes a new model to JSON exporter object with a given Hospital model.
	 * 
	 * @param model Hospital model.
	 */
	public ModelToJsonNoPostProcExporter(final Root model) {
		super(model);
	}

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(ModelToJsonNoPostProcExporter.class.getName());

	/**
	 * Converts a given patient to a JSON object.
	 * 
	 * @param patient Patient.
	 * @return JSON object.
	 */
	@Override
	protected JsonObject convertPatientToJson(final Patient patient, final boolean verbose) {
		Objects.requireNonNull(patient);

		final JsonObject patientJson = new JsonObject();
		patientJson.addProperty("id", patient.getName());

		// Find the selected admission shift
		final Collection<VirtualShiftToWorkload> possibleShiftAssignments = patient.getFirstWorkload()
				.getVirtualShift();
		Shift admissionShift = null;
		VirtualShiftToWorkload selectedvsw = null;
		for (final VirtualShiftToWorkload v : possibleShiftAssignments) {
			if (v.isIsSelected()) {
				admissionShift = v.getShift();
				selectedvsw = v;
				break;
			}
		}

		// Find the selected OT
		final Collection<VirtualWorkloadToCapacity> possibleOtAssignments = patient.getFirstWorkload()
				.getVirtualCapacity();
		OT scheduledOt = null;
		VirtualWorkloadToCapacity selectedvwc = null;
		for (final VirtualWorkloadToCapacity v : possibleOtAssignments) {
			if (v.isIsSelected()) {
				scheduledOt = v.getCapacity().getOt();
				selectedvwc = v;
				break;
			}
		}
		// Find the selected OpTime -> Not necessary for export
		final Collection<VirtualWorkloadToOpTime> possibleOpTimeAssignments = patient.getFirstWorkload()
				.getVirtualOpTime();
		OpTime selectedOpTime = null;
		VirtualWorkloadToOpTime selectedvwop = null;
		VirtualOpTimeToCapacity selectedvopc = null;
		for (final VirtualWorkloadToOpTime v : possibleOpTimeAssignments) {
			if (v.isIsSelected()) {
				selectedOpTime = v.getOpTime();
				selectedvwop = v;
				final Collection<VirtualOpTimeToCapacity> possibleSurgeonOTAssignments = selectedOpTime.getVirtualCapacity();
				for (final VirtualOpTimeToCapacity vopc : possibleSurgeonOTAssignments) {
					if (vopc.isIsSelected()) {
						selectedvopc = vopc;
						break;
					}
				}
				break;
			}
		}
		
		// Checks if assignments for a patient are viable
		if(admissionShift != null) {
			Objects.requireNonNull(selectedvsw);
			Objects.requireNonNull(selectedvwc);
			Objects.requireNonNull(selectedvwop);
			Objects.requireNonNull(selectedvopc);
			if(!selectedvwop.getRequires_virtualOpTimeToCapacity().contains(selectedvopc)) {
				logger.warning("VirtualWorkloadToOpTime is selected but not enabled by a VirtualOpTimeToCapacity-Object!");
			}
			if(!selectedvwc.getRequires_virtualWorkloadToOpTime().contains(selectedvwop)) {
				logger.warning("VirtualWorkloadToCapacity is selected but not enabled by a VirtualWorkloadToOpTime-Object!");
			}
			if(!selectedvsw.getRequires_virtualWorkloadToCapacity().contains(selectedvwc)) {
				logger.warning("VirtualShiftToWorkload is selected but not enabled by a VirtualWorkloadToCapacity-Object!");
			}
			if((admissionShift.getShiftNo() / 3 != selectedvwc.getCapacity().getDay()) | 
					(admissionShift.getShiftNo() / 3 != selectedOpTime.getDay()) |
					(selectedvwc.getCapacity().getDay() != selectedOpTime.getDay())) {
				logger.warning("The selected virtual Nodes are not on the same day! shift: " + admissionShift.getShiftNo() + " capacity.day = " + selectedvwc.getCapacity().getDay() + " opTime.day = " + selectedOpTime.getDay());
			}
			if(patient.getSurgeon() != selectedvwop.getOpTime().getSurgeon()) {
				logger.warning("The patient is not assigned to the correct surgeon! Expected: " + patient.getSurgeon() + " Assigned: " + selectedvwop.getOpTime().getSurgeon()); 
			}
		}
		
		// If patient was scheduled
		if (admissionShift != null) {
			patientJson.addProperty("admission_day", convertShiftToDay(admissionShift.getShiftNo()));
			if (admissionShift.getRoom() != null) {
				patientJson.addProperty("room", admissionShift.getRoom().getName());
			}

			Objects.requireNonNull(scheduledOt);
			patientJson.addProperty("operating_theater", scheduledOt.getName());

			if(verbose) {
				System.out.println("Patient " + patient.getName() + " was assigned to room " + admissionShift.getRoom().getName() + " on shift " + admissionShift.getShiftNo() + 
							". The operation by surgeon " + selectedOpTime.getSurgeon().getName() + " is scheduled in OT " + scheduledOt.getName() + " on day " + selectedvwc.getCapacity().getDay() + ".");
			}
		} else {
			patientJson.addProperty("admission_day", "none");
			if(verbose) {
				System.out.println("Patient " + patient.getName() + " was not scheduled.");
			}
		}
		return patientJson;
	}

	/**
	 * Converts a given nurse to a JSON object.
	 * 
	 * @param nurse Nurse.
	 * @return JSON object.
	 */
	@Override
	protected JsonObject convertNurseToJson(final Nurse nurse) {
		Objects.requireNonNull(nurse);

		final JsonObject nurseJson = new JsonObject();
		nurseJson.addProperty("id", nurse.getName());

		final JsonArray assignmentsJson = new JsonArray();
		for (final Roster r : nurse.getRosters()) {
			final Map<Integer, Set<String>> shift2Room = new HashMap<>();
			final Collection<VirtualShiftToRoster> assignments = r.getVirtualShift();
			for (final VirtualShiftToRoster v : assignments) {
				if (v.isIsSelected()) {
					if (!shift2Room.containsKey(v.getShift().getShiftNo())) {
						shift2Room.put(v.getShift().getShiftNo(), new HashSet<String>());
					}

					shift2Room.get(v.getShift().getShiftNo()).add(v.getShift().getRoom().getName());
				}
			}

			for (final int shiftId : shift2Room.keySet()) {
				final JsonObject assignment = new JsonObject();
				assignment.addProperty("day", convertShiftToDay(shiftId));
				assignment.addProperty("shift", convertShiftType(shiftId % 3));
				final JsonArray roomsJson = new JsonArray();
				for (final String room : shift2Room.get(shiftId)) {
					roomsJson.add(room);
				}
				assignment.add("rooms", roomsJson);
				assignmentsJson.add(assignment);
			}

		}

		nurseJson.add("assignments", assignmentsJson);

		return nurseJson;
	}

	/**
	 * Converts the overall model costs to a JSON array with the possibility to
	 * print all values on the console.
	 * 
	 * @param model   Complete model to calculate all costs for.
	 * @param verbose If true, the method will print all costs on the console.
	 * @return JSON array.
	 */
	@Override
	protected JsonArray convertModelToCostsJson(final Root model, final boolean verbose) {
		Objects.requireNonNull(model);

//		final ModelCostNoPostProcCalculator calc = new ModelCostNoPostProcCalculator();
//		final int unscheduled = calc.calculateUnscheduledPatientsCost(model);
//		final int delay = calc.calculateAdmissionDelayCost(model);
//		final int openOt = calc.calculateOpenOtCost(model);
//		final int ageMix = calc.calculateAgeMixCost(model);
//		final int skill = calc.calculateSkillLevelCost(model);
//		final int excess = calc.calculateExcessCost(model);
//		final int continuity = calc.calculateContinuityCost(model);
//		final int surgeonTransfer = calc.calculateSurgeonTransferCost(model);
//
//		// total cost is the sum of all individual costs
//		//
//		// summing the individual values is faster than re-calculating the complete
//		// model within the cost calculator
//		final int costs = unscheduled + delay + openOt + ageMix + skill + excess + continuity + surgeonTransfer;
//
//		if (verbose) {
//			logger.info("Costs: " + costs);
//			logger.info("Unscheduled: " + unscheduled);
//			logger.info("Delay: " + delay);
//			logger.info("OpenOT: " + openOt);
//			logger.info("AgeMix: " + ageMix);
//			logger.info("Skill: " + skill);
//			logger.info("Excess: " + excess);
//			logger.info("Continuity: " + continuity);
//			logger.info("SurgeonTransfer: " + surgeonTransfer);
//		}
//
//		// Weirdly, the output costs is a JSON array with only one concatenated String
//		final StringBuilder sb = new StringBuilder();
//		sb.append("Cost: ");
//		sb.append(costs);
//		sb.append(", Unscheduled: ");
//		sb.append(unscheduled);
//		sb.append(", Delay: ");
//		sb.append(delay);
//		sb.append(", OpenOT: ");
//		sb.append(openOt);
//		sb.append(", AgeMix: ");
//		sb.append(ageMix);
//		sb.append(", Skill: ");
//		sb.append(skill);
//		sb.append(", Excess: ");
//		sb.append(excess);
//		sb.append(", Continuity: ");
//		sb.append(continuity);
//		sb.append(", SurgeonTransfer: ");
//		sb.append(surgeonTransfer);

		final JsonArray costsJson = new JsonArray();
//		costsJson.add(sb.toString());
		return costsJson;
	}

}
