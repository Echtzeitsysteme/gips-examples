package ihtcvirtualmetamodel.importexport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.IhtcvirtualmetamodelFactory;
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.Roster;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.Surgeon;
import ihtcvirtualmetamodel.Weight;
import ihtcvirtualmetamodel.utils.FileUtils;

/**
 * JSON file to EMF model loader for the IHTC 2024 example.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class JsonToModelLoader {

	/**
	 * The hospital model to work with.
	 */
	private Root model = null;

	/**
	 * Number of days of the loaded model.
	 */
	private int numberOfFoundDays = 0;

	/**
	 * Number of shifts per day is static = 3.
	 */
	private final static int numberOfShiftsPerDay = 3;

	/**
	 * Found ages as map from "name" to integer value.
	 */
	private Map<String, Integer> foundAges = new HashMap<String, Integer>();

	/**
	 * Found genders as map from "name" to integer value.
	 */
	private Map<String, Integer> foundGenders = new HashMap<String, Integer>();

	/**
	 * Creates a new instance of this class with an empty hospital model.
	 */
	public JsonToModelLoader() {
		model = IhtcvirtualmetamodelFactory.eINSTANCE.createRoot();
	}

	/**
	 * Returns the hospital model contained in this loader object.
	 * 
	 * @return Hospital model.
	 */
	public Root getModel() {
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

		// days
		final JsonPrimitive days = json.getAsJsonPrimitive("days");
		convertDays(days);

		// shift types as String array
//		final JsonArray shiftTypes = json.getAsJsonArray("shift_types");
//		checkShiftTypes(shiftTypes);
		// This should not be necessary because the IHTC always uses three shift types
		// per definition.

		// age groups as String array
		final JsonArray ageGroups = json.getAsJsonArray("age_groups");
		convertAgeGroups(ageGroups);

//		final JsonArray occupants = json.getAsJsonArray("occupants");
//		convertOccupants(occupants);
		// TODO

		final JsonArray surgeons = json.getAsJsonArray("surgeons");
		convertSurgeons(surgeons);

		final JsonArray rooms = json.getAsJsonArray("rooms");
		convertRooms(rooms);

//		final JsonArray patients = json.getAsJsonArray("patients");
//		convertPatients(patients);
		// TODO

		final JsonArray operatingTheaters = json.getAsJsonArray("operating_theaters");
		convertOperatingTheaters(operatingTheaters);

		final JsonArray nurses = json.getAsJsonArray("nurses");
		convertNurses(nurses);

		// global weights
		final JsonObject weights = json.getAsJsonObject("weights");
		convertWeights(weights);
	}

	/**
	 * Converts the given JSON array of age groups to the model representations.
	 * 
	 * @param ageGroups JSON array of age groups.
	 */
	private void convertAgeGroups(final JsonArray ageGroups) {
		int ageCounter = 0;
		for (final JsonElement ag : ageGroups) {
			final String name = ag.getAsString();
			this.foundAges.put(name, ageCounter);
			ageCounter++;
		}
	}

	/*
	 * Utility methods.
	 */

	/**
	 * Converts the given JSON object representing the input weights to the model
	 * representation.
	 * 
	 * @param weights JSON object containing the input weights.
	 */
	private void convertWeights(final JsonObject weights) {
		final Weight w = IhtcvirtualmetamodelFactory.eINSTANCE.createWeight();
		w.setRoomMixedAge(weights.get("room_mixed_age").getAsInt());
		w.setRoomNurseSkill(weights.get("room_nurse_skill").getAsInt());
		w.setContinuityOfCare(weights.get("continuity_of_care").getAsInt());
		w.setNurseExcessiveWorkload(weights.get("nurse_eccessive_workload").getAsInt());
		w.setOpenOperatingTheater(weights.get("open_operating_theater").getAsInt());
		w.setSurgeonTransfer(weights.get("surgeon_transfer").getAsInt());
		w.setPatientDelay(weights.get("patient_delay").getAsInt());
		w.setUnscheduledOptional(weights.get("unscheduled_optional").getAsInt());
		this.model.setWeight(w);
	}

	/**
	 * Converts the given JSON array of operating theaters to the model
	 * representations.
	 * 
	 * @param operatingTheaters JSON array of operating theaters.
	 */
	private void convertOperatingTheaters(final JsonArray operatingTheaters) {
		for (final JsonElement ot : operatingTheaters) {
			final String name = ((JsonObject) ot).get("id").getAsString();
			final JsonArray availability = ((JsonObject) ot).get("availability").getAsJsonArray();
			createOperatingTheater(name, availability);
		}
	}

	/**
	 * Creates one operating theater with the given name and the given JSON array of
	 * availability values.
	 * 
	 * @param name         Operating theater name.
	 * @param availability Operating theater availabilities.
	 */
	private void createOperatingTheater(final String name, final JsonArray availability) {
		final OT ot = IhtcvirtualmetamodelFactory.eINSTANCE.createOT();
		ot.setName(name);
		// Create capacities, i.e., the `Capacity` objects
		int dayCounter = 0;
		for (final JsonElement a : availability) {
			final Capacity c = IhtcvirtualmetamodelFactory.eINSTANCE.createCapacity();
			c.setDay(dayCounter);
			c.setMaxCapacity(a.getAsInt());
			ot.getCapacities().add(c);
			dayCounter++;
		}
		this.model.getOts().add(ot);
	}

	private void convertSurgeons(final JsonArray surgeons) {
		for (final JsonElement s : surgeons) {
			final String name = ((JsonObject) s).get("id").getAsString();
			final JsonArray maxSurgeryTime = ((JsonObject) s).get("max_surgery_time").getAsJsonArray();
			createSurgeon(name, maxSurgeryTime);
		}
	}

	/**
	 * Creates one new surgeon object with the given name and JSON array of maximum
	 * surgery times within the model.
	 * 
	 * @param name           Surgeon name.
	 * @param maxSurgeryTime Surgeon maximum surgery time values as JSON array.
	 */
	private void createSurgeon(final String name, final JsonArray maxSurgeryTime) {
		final Surgeon s = IhtcvirtualmetamodelFactory.eINSTANCE.createSurgeon();
		s.setName(name);
		// Create max surgery times, i.e., the `OpTime` objects
		int dayCounter = 0;
		for (final JsonElement maxSurgeryTimeElement : maxSurgeryTime) {
			final OpTime opt = IhtcvirtualmetamodelFactory.eINSTANCE.createOpTime();
			opt.setDay(dayCounter);
			opt.setMaxOpTime(maxSurgeryTimeElement.getAsInt());
			s.getOpTimes().add(opt);
			dayCounter++;
		}
		this.model.getSurgeons().add(s);
	}

	private void convertDays(final JsonPrimitive days) {
		final int numberOfDays = days.getAsInt();

		if (numberOfDays <= 0) {
			throw new IllegalArgumentException("Number of days was <= 0.");
		}

		this.numberOfFoundDays = numberOfDays;
	}

	/**
	 * Converts the given JSON array of rooms to the model representations.
	 * 
	 * @param rooms JSON array of rooms.
	 */
	private void convertRooms(final JsonArray rooms) {
		for (final JsonElement r : rooms) {
			final String name = ((JsonObject) r).get("id").getAsString();
			final int capacity = ((JsonObject) r).get("capacity").getAsInt();
			createRoom(name, capacity);
		}
	}

	/**
	 * Creates one room with the given name and given capacity within the model.
	 * 
	 * @param name     Room name.
	 * @param capacity Room capacity.
	 */
	private void createRoom(final String name, final int capacity) {
		final Room r = IhtcvirtualmetamodelFactory.eINSTANCE.createRoom();
		r.setName(name);
		r.setBeds(capacity);

		// Create all shifts for this specific room
		for (int i = 0; i < numberOfFoundDays * numberOfShiftsPerDay; i++) {
			final Shift s = IhtcvirtualmetamodelFactory.eINSTANCE.createShift();
			s.setShiftNo(i);
			// `next` und `prev` will be set afterwards
			r.getShifts().add(s);
		}

		// Assign `prev` and `next`
		final Map<Integer, Shift> number2Shift = new HashMap<Integer, Shift>();
		r.getShifts().forEach(s -> {
			number2Shift.put(s.getShiftNo(), s);
		});
		r.getShifts().forEach(s -> {
			final int shiftNumber = s.getShiftNo();

			// prev
			if (shiftNumber != 0) {
				s.setPrev(number2Shift.get(shiftNumber - 1));
			}

			// next
			if (shiftNumber != r.getShifts().size() - 1) {
				s.setNext(number2Shift.get(shiftNumber + 1));
			}
		});

		this.model.getRooms().add(r);
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
		final Nurse nurse = IhtcvirtualmetamodelFactory.eINSTANCE.createNurse();
		nurse.setName(name);
		nurse.setSkillLevel(skillLevel);
		final Set<Roster> rosters = convertRosters(workingShifts);
		nurse.getRosters().addAll(rosters);
		this.model.getNurses().add(nurse);
	}

	private Set<Roster> convertRosters(final JsonArray workingShifts) {
		final Set<Roster> rosters = new HashSet<Roster>();
		for (final JsonElement s : workingShifts) {
			final Roster r = IhtcvirtualmetamodelFactory.eINSTANCE.createRoster();
			r.setMaxWorkload(((JsonObject) s).get("max_load").getAsInt());
			final int shiftNumber = convertDayType(((JsonObject) s).get("day").getAsInt())
					+ convertShiftType(((JsonObject) s).get("shift").getAsString());
			r.setShiftNo(shiftNumber);
			rosters.add(r);
		}
		return rosters;
	}

	private int convertDayType(final int dayNumber) {
		return dayNumber * 3;
	}

	private int convertShiftType(final String shiftType) {
		checkNotNull(shiftType);

		switch (shiftType) {
		case "early": {
			return 0;
		}
		case "late": {
			return 1;
		}
		case "night": {
			return 2;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + shiftType);
		}
	}

	private void checkNotNull(final Object o) {
		if (o == null) {
			throw new IllegalArgumentException("Given object was null.");
		}
	}

}
