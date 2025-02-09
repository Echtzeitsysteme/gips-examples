package ihtcmetamodel.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ihtcmetamodel.AgeGroup;
import ihtcmetamodel.Day;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.Nurse;
import ihtcmetamodel.Occupant;
import ihtcmetamodel.Patient;
import ihtcmetamodel.Room;
import ihtcmetamodel.RoomsShiftNurseAssignment;
import ihtcmetamodel.ShiftType;

/**
 * TODO.
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

	public void modelToJson(final String path) {
		if (path == null || path.isBlank()) {
			throw new IllegalArgumentException("Given path <" + path + "> was null or blank.");
		}

		// If path contains at least one slash `/`, create the folder if not existent
		if (path.contains("/")) {
			final int lastSlashIndex = path.lastIndexOf("/");
			FileUtils.prepareFolder(path.substring(0, lastSlashIndex));
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
		FileUtils.writeFileFromJson(path, json);
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
		final int unscheduled = calculateUnscheduledPatientsCost(model);
		final int delay = calculateAdmissionDelayCost(model);
		final int openOt = calculateOpenOtCost(model);
		final int ageMix = calculateAgeMixCost(model);
		final int skill = calculateSkillLevelCost(model);
		final int excess = calculateExcessCost(model);
		final int continuity = calculateContinuityCost(model);
		final int surgeonTransfer = calculateSurgeonTransferCost(model);

		// total cost is the sum of all individual costs
		final int costs = unscheduled + delay + openOt + ageMix + skill + excess + continuity + surgeonTransfer;

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

		// Weirdly, the output costs is a JSON array with only one concatenated String
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

	/**
	 * Soft constraint Patient Admission Scheduling, S1.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Age mix cost for the whole model.
	 */
	private int calculateAgeMixCost(final Hospital model) {
		int ageMixCost = 0;
		for (final Day d : model.getDays()) {
			for (final Room r : model.getRooms()) {
				ageMixCost += getMaxAgeDifferenceInRoomAtDay(r, d);
			}
		}
		return ageMixCost * model.getWeight().getRoomMixedAge();
	}

	/**
	 * Soft constraint Nurse-to-Room Assignment, S2.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Skill level cost for the whole model.
	 */
	private int calculateSkillLevelCost(final Hospital model) {
		int skillLevelCost = -1;
		// TODO
		return skillLevelCost * model.getWeight().getRoomNurseSkill();
	}

	/**
	 * Soft constraint Nurse-to-Room Assignment, S3.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Continuity cost for the whole model.
	 */
	private int calculateContinuityCost(final Hospital model) {
		int continuityCost = -1;
		// TODO
		return continuityCost * model.getWeight().getContinuityOfCare();
	}

	/**
	 * Soft constraint Nurse-to-Room Assignment, S4.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Excess cost for the whole model.
	 */
	private int calculateExcessCost(final Hospital model) {
		int excessCost = -1;
		// TODO
		return excessCost * model.getWeight().getNurseEccessiveWorkload();
	}

	/**
	 * Soft constraint Surgical Case Planning, S5.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Number of open OTs cost for the whole model.
	 */
	private int calculateOpenOtCost(final Hospital model) {
		int openOtCost = -1;
		// TODO
		return openOtCost * model.getWeight().getOpenOperatingTheater();
	}

	/**
	 * Soft constraint Surgical Case Planning, S6.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Surgeon transfer cost for the whole model.
	 */
	private int calculateSurgeonTransferCost(final Hospital model) {
		int surgeonTransferCost = -1;
		// TODO
		return surgeonTransferCost * model.getWeight().getSurgeonTransfer();
	}

	/**
	 * Soft constraint Global constraints, S7.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Admission delay cost for the whole model.
	 */
	private int calculateAdmissionDelayCost(final Hospital model) {
		int admissionDelayCost = -1;
		// TODO
		return admissionDelayCost * model.getWeight().getPatientDelay();
	}

	/**
	 * Soft constraint Global constraints, S8.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Unscheduled patients cost for the whole model.
	 */
	private int calculateUnscheduledPatientsCost(final Hospital model) {
		int unscheduledPatientsCost = -1;
		// TODO
		return unscheduledPatientsCost * model.getWeight().getUnscheduledOptional();
	}

	/**
	 * This method calculates the maximum age difference for a given room `r` on day
	 * `d` for all new patients and all previously assigned occupants which are
	 * already placed in this room.
	 * 
	 * @param r Room.
	 * @param d Day.
	 * @return Maximum age difference of all persons in room `r` on day `d`.
	 */
	private int getMaxAgeDifferenceInRoomAtDay(final Room r, final Day d) {
		int maxAgeFound = Integer.MIN_VALUE;
		int minAgeFound = Integer.MAX_VALUE;

		// find minimum and maximum age of new patients assigned to room `r` on day `d`
		for (final Patient p : this.model.getPatients()) {
			// assigned room must match
			if (p.getAssignedRoom() != null && p.getAssignedRoom().equals(r)) {
				// time frame must match
				if (d.getId() >= p.getAdmissionDay().getId()
						&& d.getId() <= p.getAdmissionDay().getId() + p.getLengthOfStay()) {
					final int age = convertAgeGroupToInt(p.getAgeGroup());
					if (maxAgeFound < age) {
						maxAgeFound = age;
					}
					if (minAgeFound > age) {
						minAgeFound = age;
					}
				}
			}
		}

		// find minimum and maximum age of occupants previously assigned to room `r` on
		// day `d`
		for (final Occupant o : this.model.getOccupants()) {
			// previously assigned room must match
			if (o.getRoomId() == r.getName()) {
				// time frame must match
				if (o.getLengthOfStay() <= d.getId()) {
					final int age = convertAgeGroupToInt(o.getAgeGroup());
					if (maxAgeFound < age) {
						maxAgeFound = age;
					}
					if (minAgeFound > age) {
						minAgeFound = age;
					}
				}
			}
		}

		return maxAgeFound - minAgeFound;
	}

	/**
	 * Converts the name of a given age group to an integer value. I.e., this method
	 * searches the index (starting with 0) of the given name of an age group in all
	 * possible age groups within the hospital model.
	 * 
	 * @param ageGroup Given age group to find the integer value (index) for.
	 * @return Integer value (index) of the given age group.
	 */
	private int convertAgeGroupToInt(final String ageGroup) {
		int ageCounter = 0;
		for (final AgeGroup ag : this.model.getAgeGroups()) {
			if (ag.getName().equals(ageGroup)) {
				break;
			}
		}
		return ageCounter;
	}

}
