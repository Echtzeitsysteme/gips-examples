package ihtcmetamodel.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ihtcmetamodel.Hospital;
import ihtcmetamodel.Nurse;
import ihtcmetamodel.Patient;
import ihtcmetamodel.Room;
import ihtcmetamodel.RoomsShiftNurseAssignment;
import ihtcmetamodel.ShiftType;

/**
 * This model exporter can be used to convert an EMF model to the respective
 * JSON output format required by the competition.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class ModelToJsonExporter {

	private Hospital model = null;

	public ModelToJsonExporter(final Hospital model) {
		if (model == null) {
			throw new IllegalArgumentException("Given model was null.");
		}

		this.model = model;
	}

	public void modelToJson(final String outputPath) {
		if (outputPath == null || outputPath.isBlank()) {
			throw new IllegalArgumentException("Given path <" + outputPath + "> was null or blank.");
		}

		// If path contains at least one slash `/`, create the folder if not existent
		if (outputPath.contains("/")) {
			final int lastSlashIndex = outputPath.lastIndexOf("/");
			FileUtils.prepareFolder(outputPath.substring(0, lastSlashIndex));
		}

		final JsonArray patientsJson = new JsonArray();
		for (final Patient p : this.model.getPatients()) {
			patientsJson.add(convertPatientToJson(p));
		}

		final JsonArray nursesJson = new JsonArray();
		for (final Nurse n : this.model.getNurses()) {
			nursesJson.add(convertNurseToJson(n));
		}

		final JsonArray costsJson = convertModelToCostsJson(this.model);

		// Global JSON object
		final JsonObject json = new JsonObject();
		json.add("patients", patientsJson);
		json.add("nurses", nursesJson);
		json.add("costs", costsJson);

		// Write to output JSON file
		FileUtils.writeFileFromJson(outputPath, json);
	}

	private JsonObject convertPatientToJson(final Patient patient) {
		final JsonObject patientJson = new JsonObject();
		patientJson.addProperty("id", patient.getName());

		// If patient was scheduled
		if (patient.getAdmissionDay() != null) {
			patientJson.addProperty("admission_day", patient.getAdmissionDay().getId());
			patientJson.addProperty("room", patient.getAssignedRoom().getName());
			patientJson.addProperty("operating_theater", patient.getAssignedOperatingTheater().getName());
		} else {
			patientJson.addProperty("admission_day", "none");
		}

		return patientJson;
	}

	private JsonObject convertNurseToJson(final Nurse nurse) {
		final JsonObject nurseJson = new JsonObject();
		nurseJson.addProperty("id", nurse.getName());

		final JsonArray assignmentsJson = new JsonArray();
		for (final RoomsShiftNurseAssignment rsna : nurse.getAssignedRoomShifts()) {
			// Sanity check
			if (!nurse.equals(rsna.getNurse())) {
				throw new InternalError("Nurse <" + nurse.getName()
						+ "> has an assigned room shift object that does not belong to this nurse.");
			}
			final JsonObject assignment = new JsonObject();
			assignment.addProperty("day", rsna.getShift().getDay().getId());
			assignment.addProperty("shift", convertShiftTypeName(rsna.getShift().getType()));
			final JsonArray roomsJson = new JsonArray();
			for (final Room r : rsna.getRooms()) {
				roomsJson.add(r.getName());
			}
			assignment.add("rooms", roomsJson);
			assignmentsJson.add(assignment);
		}

		nurseJson.add("assignments", assignmentsJson);

		return nurseJson;
	}

	private JsonArray convertModelToCostsJson(final Hospital model) {
		final ModelCostCalculator calc = new ModelCostCalculator();
		final int unscheduled = calc.calculateUnscheduledPatientsCost(model);
		final int delay = calc.calculateAdmissionDelayCost(model);
		final int openOt = calc.calculateOpenOtCost(model);
		final int ageMix = calc.calculateAgeMixCost(model);
		final int skill = calc.calculateSkillLevelCost(model);
		final int excess = calc.calculateExcessCost(model);
		final int continuity = calc.calculateContinuityCost(model);
		final int surgeonTransfer = calc.calculateSurgeonTransferCost(model);

		// total cost is the sum of all individual costs
		final int costs = unscheduled + delay + openOt + ageMix + skill + excess + continuity + surgeonTransfer;

		// Weirdly, the output costs is a JSON array with only one concatenated String
		final StringBuilder sb = new StringBuilder();
		sb.append("Cost: ");
		sb.append(costs);
		sb.append(", Unscheduled: ");
		sb.append(unscheduled);
		sb.append(", Delay: ");
		sb.append(delay);
		sb.append(", OpenOT: ");
		sb.append(openOt);
		sb.append(", AgeMix: ");
		sb.append(ageMix);
		sb.append(", Skill: ");
		sb.append(skill);
		sb.append(", Excess: ");
		sb.append(excess);
		sb.append(", Continuity: ");
		sb.append(continuity);
		sb.append(", SurgeonTransfer: ");
		sb.append(surgeonTransfer);

		final JsonArray costsJson = new JsonArray();
		costsJson.add(sb.toString());
		return costsJson;
	}

	private String convertShiftTypeName(final ShiftType shiftType) {
		switch (shiftType) {
		case ShiftType.EARLY: {
			return "early";
		}
		case ShiftType.LATE: {
			return "late";
		}
		case ShiftType.NIGHT: {
			return "night";
		}
		default:
			throw new IllegalArgumentException("Unexpected shift type: " + shiftType.getName());
		}
	}

}
