package ihtcmetamodel.loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.emoflon.smartemf.runtime.collections.LinkedSmartESet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ihtcmetamodel.AgeGroup;
import ihtcmetamodel.Day;
import ihtcmetamodel.Gender;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.IhtcmetamodelFactory;
import ihtcmetamodel.Nurse;
import ihtcmetamodel.NurseShiftMaxLoad;
import ihtcmetamodel.Occupant;
import ihtcmetamodel.OccupantSkillLevelRequired;
import ihtcmetamodel.OccupantWorkloadProduced;
import ihtcmetamodel.OperatingTheater;
import ihtcmetamodel.OperatingTheaterAvailability;
import ihtcmetamodel.Patient;
import ihtcmetamodel.PatientSkillLevelRequired;
import ihtcmetamodel.PatientWorkloadProduced;
import ihtcmetamodel.Room;
import ihtcmetamodel.Shift;
import ihtcmetamodel.ShiftType;
import ihtcmetamodel.Surgeon;
import ihtcmetamodel.SurgeonAvailability;
import ihtcmetamodel.Weight;

/**
 * JSON file to EMF model loader for the IHTC 2024 example.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class JsonToModelLoader {

	private Hospital model = null;
	private int skillLevel = -1;
	private Set<String> foundGenders = new HashSet<String>();

	/**
	 * Creates a new instance of this class with an empty hospital model.
	 */
	public JsonToModelLoader() {
		model = IhtcmetamodelFactory.eINSTANCE.createHospital();
	}

	/**
	 * Returns the hospital model contained in this loader object.
	 * 
	 * @return Hospital model.
	 */
	public Hospital getModel() {
		return model;
	}

	/**
	 * Loads a JSON input file from the given path and converts it to an EMF model
	 * instance.
	 * 
	 * @param inputPath File path for the JSON input file to read.
	 */
	public void jsonToModel(final String inputPath) {
		if (inputPath == null || inputPath.isBlank()) {
			throw new IllegalArgumentException("Given path <" + inputPath + "> was null or blank.");
		}

		// global JSON object
		final JsonObject json = FileUtils.readFileToJson(inputPath);

		// number of days
		final JsonPrimitive days = json.getAsJsonPrimitive("days");
		convertDays(days);

		// create shifts
		createShifts(this.model.getDays());

		// number of skill levels
		final JsonPrimitive skillLevels = json.getAsJsonPrimitive("skill_levels");
		convertSkillLevels(skillLevels);

		// shift types as String array
		final JsonArray shiftTypes = json.getAsJsonArray("shift_types");
		checkShiftTypes(shiftTypes);

		// age groups as String array
		final JsonArray ageGroups = json.getAsJsonArray("age_groups");
		convertAgeGroups(ageGroups);

		// complex data types as arrays
		// occupants, patients, surgeons, operating theaters, rooms, nurses
		final JsonArray occupants = json.getAsJsonArray("occupants");
		convertOccupants(occupants);
		final JsonArray surgeons = json.getAsJsonArray("surgeons");
		convertSurgeons(surgeons);
		final JsonArray rooms = json.getAsJsonArray("rooms");
		convertRooms(rooms);
		final JsonArray patients = json.getAsJsonArray("patients");
		convertPatients(patients);
		final JsonArray operatingTheaters = json.getAsJsonArray("operating_theaters");
		convertOperatingTheaters(operatingTheaters);
		final JsonArray nurses = json.getAsJsonArray("nurses");
		convertNurses(nurses);

		// create one gender object per found gender of all patients
		createGenders(this.foundGenders);

		// global weights
		final JsonObject weights = json.getAsJsonObject("weights");
		convertWeights(weights);
	}

	/*
	 * Utility methods.
	 */

	private void createShifts(final LinkedSmartESet<Day> days) {
		int globalShiftCounter = 0;
		for (final Day d : days) {
			createShift("shift_" + globalShiftCounter, globalShiftCounter, d, ShiftType.EARLY);
			globalShiftCounter++;
			createShift("shift_" + globalShiftCounter, globalShiftCounter, d, ShiftType.LATE);
			globalShiftCounter++;
			createShift("shift_" + globalShiftCounter, globalShiftCounter, d, ShiftType.NIGHT);
			globalShiftCounter++;
		}
	}

	private void createShift(final String name, final int id, final Day day, final ShiftType type) {
		final Shift s = IhtcmetamodelFactory.eINSTANCE.createShift();
		s.setName(name);
		s.setId(id);
		s.setDay(day);
		s.setType(type);
		this.model.getShifts().add(s);
	}

	private void convertWeights(final JsonObject weights) {
		final Weight w = IhtcmetamodelFactory.eINSTANCE.createWeight();
		w.setRoomMixedAge(weights.get("room_mixed_age").getAsInt());
		w.setRoomNurseSkill(weights.get("room_nurse_skill").getAsInt());
		w.setContinuityOfCare(weights.get("continuity_of_care").getAsInt());
		w.setNurseEccessiveWorkload(weights.get("nurse_eccessive_workload").getAsInt());
		w.setOpenOperatingTheater(weights.get("open_operating_theater").getAsInt());
		w.setSurgeonTransfer(weights.get("surgeon_transfer").getAsInt());
		w.setPatientDelay(weights.get("patient_delay").getAsInt());
		w.setUnscheduledOptional(weights.get("unscheduled_optional").getAsInt());
		this.model.setWeight(w);
	}

	private void convertDays(final JsonPrimitive days) {
		final int numberOfDays = days.getAsInt();
		for (int i = 0; i < numberOfDays; i++) {
			createDay(i);
		}
	}

	private void createDay(final int id) {
		final Day d = IhtcmetamodelFactory.eINSTANCE.createDay();
		d.setId(id);
		d.setName("day_" + id);
		this.model.getDays().add(d);
	}

	private void convertSkillLevels(final JsonPrimitive skillLevels) {
		this.skillLevel = skillLevels.getAsInt();
	}

	private void checkShiftTypes(final JsonArray shiftTypes) {
		// Shift types should always be `early`, `late`, `night`
		if (shiftTypes.size() != 3) {
			throw new UnsupportedOperationException("There are not exactly three shift types.");
		}

		for (final JsonElement st : shiftTypes) {
			final String name = st.getAsString();
			if (!name.equals(ShiftType.EARLY.getName().toLowerCase()) //
					&& !name.equals(ShiftType.LATE.getName().toLowerCase()) //
					&& !name.equals(ShiftType.NIGHT.getName().toLowerCase())) {
				throw new UnsupportedOperationException(
						"Shift type <" + name + "> was not `early`, `late`, or `night`.");
			}
		}
	}

	private void convertAgeGroups(final JsonArray ageGroups) {
		for (final JsonElement ag : ageGroups) {
			final String name = ag.getAsString();
			createAgeGroup(name);
		}
	}

	private void createAgeGroup(final String name) {
		final AgeGroup ag = IhtcmetamodelFactory.eINSTANCE.createAgeGroup();
		ag.setName(name);
		this.model.getAgeGroups().add(ag);
	}

	private void convertRooms(final JsonArray rooms) {
		for (final JsonElement r : rooms) {
			final String name = ((JsonObject) r).get("id").getAsString();
			final int capacity = ((JsonObject) r).get("capacity").getAsInt();
			createRoom(name, capacity);
		}

	}

	private void createRoom(final String name, final int capacity) {
		final Room r = IhtcmetamodelFactory.eINSTANCE.createRoom();
		r.setName(name);
		r.setCapacity(capacity);
		this.model.getRooms().add(r);
	}

	private void convertOperatingTheaters(final JsonArray operatingTheaters) {
		for (final JsonElement ot : operatingTheaters) {
			final String name = ((JsonObject) ot).get("id").getAsString();
			final JsonArray availability = ((JsonObject) ot).get("availability").getAsJsonArray();
			createOperatingTheater(name, availability);
		}
	}

	private void createOperatingTheater(final String name, final JsonArray availability) {
		final OperatingTheater ot = IhtcmetamodelFactory.eINSTANCE.createOperatingTheater();
		ot.setName(name);
		int dayCounter = 0;
		for (final JsonElement a : availability) {
			ot.getAvailabilities().add(createOperatingTheaterAvailability(dayCounter, a.getAsInt()));
			dayCounter++;
		}
		this.model.getOperatingTheaters().add(ot);
	}

	private OperatingTheaterAvailability createOperatingTheaterAvailability(final int dayId, final int availability) {
		final OperatingTheaterAvailability ota = IhtcmetamodelFactory.eINSTANCE.createOperatingTheaterAvailability();
		final Day day = this.model.getDays().get(dayId);
		// sanity check
		if (day.getId() != dayId) {
			throw new InternalError("Day ID did not match.");
		}
		ota.setDay(day);
		ota.setAvailability(availability);
		// ota.setOperatingTheater(...) will be set automatically by EMF
		return ota;
	}

	private void convertSurgeons(final JsonArray surgeons) {
		for (final JsonElement s : surgeons) {
			final String name = ((JsonObject) s).get("id").getAsString();
			final JsonArray maxSurgeryTime = ((JsonObject) s).get("max_surgery_time").getAsJsonArray();
			createSurgeon(name, maxSurgeryTime);
		}

	}

	private void createSurgeon(final String name, final JsonArray maxSurgeryTime) {
		final Surgeon s = IhtcmetamodelFactory.eINSTANCE.createSurgeon();
		s.setName(name);
		int dayCounter = 0;
		for (final JsonElement maxSurgeryTimeElement : maxSurgeryTime) {
			s.getAvailabilities().add(createSurgeonAvailability(dayCounter, maxSurgeryTimeElement.getAsInt()));
			dayCounter++;
		}
		this.model.getSurgeons().add(s);
	}

	private SurgeonAvailability createSurgeonAvailability(final int dayId, final int maxSurgeryTime) {
		final SurgeonAvailability sa = IhtcmetamodelFactory.eINSTANCE.createSurgeonAvailability();
		final Day day = this.model.getDays().get(dayId);
		// sanity check
		if (day.getId() != dayId) {
			throw new InternalError("Day ID did not match.");
		}
		sa.setDay(day);
		sa.setAvailability(maxSurgeryTime);
		// sa.setSurgeon(...) will be set automatically by EMF
		return sa;
	}

	private void convertOccupants(final JsonArray occupants) {
		for (final JsonElement o : occupants) {
			final String name = ((JsonObject) o).get("id").getAsString();
			final String gender = ((JsonObject) o).get("gender").getAsString();
			final String ageGroup = ((JsonObject) o).get("age_group").getAsString();
			final int lengthOfStay = ((JsonObject) o).get("length_of_stay").getAsInt();
			final JsonArray workloadProduced = ((JsonObject) o).get("workload_produced").getAsJsonArray();
			final JsonArray skillLevelRequired = ((JsonObject) o).get("skill_level_required").getAsJsonArray();
			final String roomId = ((JsonObject) o).get("room_id").getAsString();

			// TODO: add relative days

			createOccupant(name, gender, ageGroup, lengthOfStay, workloadProduced, skillLevelRequired, roomId);
		}
	}

	private void createOccupant(final String name, final String gender, final String ageGroup, final int lengthOfStay,
			final JsonArray workloadProduced, final JsonArray skillLevelRequired, final String roomId) {
		final Occupant o = IhtcmetamodelFactory.eINSTANCE.createOccupant();
		o.setName(name);
		o.setGender(gender);
		o.setAgeGroup(ageGroup);
		o.setLengthOfStay(lengthOfStay);
		int shiftCounter = 0;
		for (final JsonElement workload : workloadProduced) {
			o.getWorkloadsProduced().add(createOccupantWorkloadProduced(shiftCounter, workload.getAsInt()));
			shiftCounter++;
		}
		shiftCounter = 0;
		for (final JsonElement skillLevel : skillLevelRequired) {
			o.getSkillLevelsRequired().add(createOccupantSkillLevelRequired(shiftCounter, skillLevel.getAsInt()));
			shiftCounter++;
		}
		o.setRoomId(roomId);
		this.model.getOccupants().add(o);
	}

	private OccupantWorkloadProduced createOccupantWorkloadProduced(final int shiftId, final int workload) {
		final OccupantWorkloadProduced owp = IhtcmetamodelFactory.eINSTANCE.createOccupantWorkloadProduced();
		final Shift shift = getShiftById(shiftId);
		owp.setShift(shift);
		owp.setWorkloadProduced(workload);
		return owp;
	}

	private OccupantSkillLevelRequired createOccupantSkillLevelRequired(final int shiftId, int skillLevel) {
		final OccupantSkillLevelRequired osr = IhtcmetamodelFactory.eINSTANCE.createOccupantSkillLevelRequired();
		final Shift shift = getShiftById(shiftId);
		osr.setShift(shift);
		osr.setSkillLevelRequired(skillLevel);
		return osr;
	}

	private void convertPatients(final JsonArray patients) {
		for (final JsonElement p : patients) {
			final String name = ((JsonObject) p).get("id").getAsString();
			final boolean mandatory = ((JsonObject) p).get("mandatory").getAsBoolean();
			final String gender = ((JsonObject) p).get("gender").getAsString();
			final String ageGroup = ((JsonObject) p).get("age_group").getAsString();
			final int lengthOfStay = ((JsonObject) p).get("length_of_stay").getAsInt();
			final int surgeryReleaseDay = ((JsonObject) p).get("surgery_release_day").getAsInt();
			final int surgeryDuration = ((JsonObject) p).get("surgery_duration").getAsInt();
			final String surgeonId = ((JsonObject) p).get("surgeon_id").getAsString();
			final JsonArray incompatibleRoomIds = ((JsonObject) p).get("incompatible_room_ids").getAsJsonArray();
			final JsonArray workloadProduced = ((JsonObject) p).get("workload_produced").getAsJsonArray();
			final JsonArray skillLevelRequired = ((JsonObject) p).get("skill_level_required").getAsJsonArray();

			int surgeryDueDay = -1;
			if (mandatory) {
				surgeryDueDay = ((JsonObject) p).get("surgery_due_day").getAsInt();
			} else {
				surgeryDueDay = 100;
			}

			// TODO: add relative days

			// TODO: remove me
//			if(!mandatory) {
//				return;
//			}

			// add gender to all found genders if not already existent
			this.foundGenders.add(gender);

			createPatient(name, mandatory, gender, ageGroup, lengthOfStay, surgeryReleaseDay, surgeryDueDay,
					surgeryDuration, surgeonId, incompatibleRoomIds, workloadProduced, skillLevelRequired);
		}
	}

	private void createPatient(final String name, final boolean mandatory, final String gender, final String ageGroup,
			final int lengthOfStay, final int surgeryReleaseDay, final int surgeryDueDay, final int surgeryDuration,
			final String surgeonId, final JsonArray incompatibleRoomIds, final JsonArray workloadProduced,
			final JsonArray skillLevelRequired) {
		final Patient p = IhtcmetamodelFactory.eINSTANCE.createPatient();
		p.setName(name);
		p.setMandatory(mandatory);
		p.setGender(gender);
		p.setAgeGroup(ageGroup);
		p.setLengthOfStay(lengthOfStay);
		p.setSurgeryReleaseDay(surgeryReleaseDay);
//		if (surgeryDueDay != -1) {
//			p.setSurgeryDueDate(surgeryDueDay);
//		}
		p.setSurgeryDueDate(surgeryDueDay);
		p.setSurgeryDuration(surgeryDuration);
		final Surgeon s = findSurgeonByName(surgeonId);
		p.setSurgeon(s);
		for (final JsonElement incompatibleRoom : incompatibleRoomIds) {
			p.getIncompatibleRooms().add(findRoomByName(incompatibleRoom.getAsString()));
		}
		int shiftOffsetCounter = 0;
		for (final JsonElement workload : workloadProduced) {
			p.getWorkloadsProduced().add(createPatientWorkloadProduced(shiftOffsetCounter, workload.getAsInt()));
			shiftOffsetCounter++;
		}
		shiftOffsetCounter = 0;
		for (final JsonElement skillLevel : skillLevelRequired) {
			p.getSkillLevelsRequired().add(createPatientSkillLevelRequired(shiftOffsetCounter, skillLevel.getAsInt()));
			shiftOffsetCounter++;
		}

		this.model.getPatients().add(p);
	}

	private Surgeon findSurgeonByName(final String name) {
		for (final Surgeon s : this.model.getSurgeons()) {
			if (s.getName() != null && s.getName().equals(name)) {
				return s;
			}
		}
		throw new UnsupportedOperationException("Surgeon with name <" + name + "> not found.");
	}

	private Room findRoomByName(final String name) {
		for (final Room r : this.model.getRooms()) {
			if (r.getName() != null && r.getName().equals(name)) {
				return r;
			}
		}
		throw new UnsupportedOperationException("Room with name <" + name + "> not found.");
	}

	private PatientSkillLevelRequired createPatientSkillLevelRequired(final int shiftOffset, int skillLevel) {
		final PatientSkillLevelRequired psr = IhtcmetamodelFactory.eINSTANCE.createPatientSkillLevelRequired();
		psr.setShiftOffset(shiftOffset);
		psr.setSkillLevelRequired(skillLevel);
		return psr;
	}

	private PatientWorkloadProduced createPatientWorkloadProduced(int shiftOffset, int workload) {
		final PatientWorkloadProduced pwp = IhtcmetamodelFactory.eINSTANCE.createPatientWorkloadProduced();
		pwp.setShiftOffset(shiftOffset);
		pwp.setWorkloadProduced(workload);
		return pwp;
	}

	private void convertNurses(final JsonArray nurses) {
		for (final JsonElement n : nurses) {
			final String name = ((JsonObject) n).get("id").getAsString();
			final int skillLevel = ((JsonObject) n).get("skill_level").getAsInt();
			final JsonArray workingShifts = ((JsonObject) n).get("working_shifts").getAsJsonArray();
			createNurse(name, skillLevel, workingShifts);
		}

	}

	private void createNurse(final String name, final int skillLevel, final JsonArray workingShifts) {
		final Nurse n = IhtcmetamodelFactory.eINSTANCE.createNurse();
		n.setName(name);
		n.setSkillLevel(skillLevel);
		for (final JsonElement workingShift : workingShifts) {
			final int dayId = ((JsonObject) workingShift).get("day").getAsInt();
			final String shiftTypeName = ((JsonObject) workingShift).get("shift").getAsString();
			final int maxLoad = ((JsonObject) workingShift).get("max_load").getAsInt();
			n.getShiftMaxLoads().add(createNurseShiftMaxLoad(dayId, shiftTypeName, maxLoad));
		}
		this.model.getNurses().add(n);
	}

	private NurseShiftMaxLoad createNurseShiftMaxLoad(final int dayId, final String shiftTypeName, final int maxLoad) {
		final NurseShiftMaxLoad nsml = IhtcmetamodelFactory.eINSTANCE.createNurseShiftMaxLoad();
		final Shift s = getShift(dayId, getShiftTypeByName(shiftTypeName));
		nsml.setShift(s);
		nsml.setMaxLoad(maxLoad);
		// nsml.setNurse(...) will be set automatically by EMF
		return nsml;
	}

	private void createGenders(final Set<String> foundGenders) {
		foundGenders.forEach(g -> {
			final Gender newGender = IhtcmetamodelFactory.eINSTANCE.createGender();
			newGender.setName(g);
			this.model.getGenders().add(newGender);
		});
	}

	private Shift getShift(final int dayId, final ShiftType type) {
		for (final Shift s : this.model.getShifts()) {
			if (s.getDay().getId() == dayId && s.getType() == type) {
				return s;
			}
		}
		throw new UnsupportedOperationException(
				"Shift with day ID <" + dayId + "> and shift type < " + type.getName() + "> not found.");
	}

	private ShiftType getShiftTypeByName(final String name) {
		switch (name) {
		case "early": {
			return ShiftType.EARLY;
		}
		case "late": {
			return ShiftType.LATE;
		}
		case "night": {
			return ShiftType.NIGHT;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + name);
		}
	}

	private Shift getShiftById(final int shiftId) {
		for (final Shift s : this.model.getShifts()) {
			if (s.getId() == shiftId) {
				return s;
			}
		}
		throw new UnsupportedOperationException("Shift with ID <" + shiftId + "> not found.");
	}

}
