package ihtcvirtualmetamodel.importexport;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ihtcvirtualmetamodel.AgeGroup;
import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.Day;
import ihtcvirtualmetamodel.Gender;
import ihtcvirtualmetamodel.IhtcvirtualmetamodelFactory;
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.Roster;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.Surgeon;
import ihtcvirtualmetamodel.Weight;
import ihtcvirtualmetamodel.Workload;
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
	 * Found genders as names.
	 */
	private LinkedHashSet<String> foundGenders = new LinkedHashSet<String>();

	/**
	 * Look up data structure: mapping of name -> surgeon for all surgeons.
	 */
	private Map<String, Surgeon> name2Surgeon = new HashMap<String, Surgeon>();

	/**
	 * Look up data structure: mapping of name -> room for all rooms.
	 */
	private Map<String, Room> name2Room = new HashMap<String, Room>();

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
		Objects.requireNonNull(inputPath);

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
		// This should not be necessary because the IHTC always uses three static shift
		// types per definition.

		final JsonArray rooms = json.getAsJsonArray("rooms");
		convertRooms(rooms);

		// age groups as String array
		final JsonArray ageGroups = json.getAsJsonArray("age_groups");
		convertAgeGroups(ageGroups);

		final JsonArray occupants = json.getAsJsonArray("occupants");
		convertOccupants(occupants);

		final JsonArray surgeons = json.getAsJsonArray("surgeons");
		convertSurgeons(surgeons);

		final JsonArray patients = json.getAsJsonArray("patients");
		convertPatients(patients);
		createGenders();

		final JsonArray operatingTheaters = json.getAsJsonArray("operating_theaters");
		convertOperatingTheaters(operatingTheaters);

		final JsonArray nurses = json.getAsJsonArray("nurses");
		convertNurses(nurses);

		// global weights
		final JsonObject weights = json.getAsJsonObject("weights");
		convertWeights(weights);
	}

	/*
	 * Utility methods.
	 */

	/**
	 * Converts the given JSON array of all occupants to their model
	 * representations.
	 * 
	 * @param occupants JSON array of all occupants.
	 */
	private void convertOccupants(final JsonArray occupants) {
		Objects.requireNonNull(occupants);

		for (final JsonElement o : occupants) {
			final String name = ((JsonObject) o).get("id").getAsString();
			final String gender = ((JsonObject) o).get("gender").getAsString();
			final String ageGroup = ((JsonObject) o).get("age_group").getAsString();
			final int lengthOfStay = ((JsonObject) o).get("length_of_stay").getAsInt();
			final JsonArray workloadProduced = ((JsonObject) o).get("workload_produced").getAsJsonArray();
			final JsonArray skillLevelRequired = ((JsonObject) o).get("skill_level_required").getAsJsonArray();
			final String roomId = ((JsonObject) o).get("room_id").getAsString();

			createOccupant(name, gender, ageGroup, lengthOfStay, workloadProduced, skillLevelRequired, roomId);
		}
	}

	/**
	 * Creates one occupant with the given parameters within the model.
	 * 
	 * @param name               Name.
	 * @param gender             Gender.
	 * @param ageGroup           Age group.
	 * @param lengthOfStay       Length of the stay in number of days.
	 * @param workloadProduced   JSON array of the produced work loads per shift.
	 * @param skillLevelRequired JSON array of the required skill levels per shift.
	 * @param roomId             ID of the assigned room.
	 */
	private void createOccupant(final String name, final String gender, final String ageGroup, final int lengthOfStay,
			final JsonArray workloadProduced, final JsonArray skillLevelRequired, final String roomId) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(gender);
		Objects.requireNonNull(ageGroup);
		Objects.requireNonNull(workloadProduced);
		Objects.requireNonNull(skillLevelRequired);
		Objects.requireNonNull(roomId);

		final Patient p = IhtcvirtualmetamodelFactory.eINSTANCE.createPatient();
		p.setName(name);
		p.setGender(gender);
		p.setAgeGroup(foundAges.get(ageGroup));
		p.setStayLength(lengthOfStay);
		p.setIsOccupant(true);

		// Create workload objects
		if (workloadProduced.size() != skillLevelRequired.size()) {
			throw new UnsupportedOperationException(
					"Number of workloads produced did not match the number of skill levels required.");
		}

		for (int i = 0; i < workloadProduced.size(); i++) {
			final JsonElement workload = workloadProduced.get(i);
			final JsonElement skillLevel = skillLevelRequired.get(i);

			final Workload w = IhtcvirtualmetamodelFactory.eINSTANCE.createWorkload();
			w.setWorkloadValue(workload.getAsInt());
			w.setMinNurseSkill(skillLevel.getAsInt());

			// Add "derived" (in case of the occupants pre-determined) workload to shift
			// assignment
			{
				// Get room
				final Room assignedRoom = name2Room.get(roomId);

				// Get shift
				final int shiftNumber = p.getEarliestDay() * numberOfShiftsPerDay + i;
				final Shift assignedShift = shiftNoToObject(shiftNumber, assignedRoom.getShifts());

				// assign workload to shift
				w.setDerivedShift(assignedShift);
			}

			p.getWorkloads().add(w);

			if (i == 0) {
				p.setFirstWorkload(w);
			}
		}

		// Set `prev` and `next` edges for all produced workloads
		linkWorkloads(p);
		this.model.getPatients().add(p);
	}

	/**
	 * Creates the necessary `prev` and `next` edges for a given patient p's
	 * workloads.
	 * 
	 * @param p Patient to create workload edges for.
	 */
	private void linkWorkloads(final Patient p) {
		// Set `prev` and `next` edges for all produced workloads
		for (int i = 0; i < p.getWorkloads().size(); i++) {
			// Prev
			if (i > 0) {
				p.getWorkloads().get(i).setPrev(p.getWorkloads().get(i - 1));
			}

			// Next
			if (i < p.getWorkloads().size() - 1) {
				p.getWorkloads().get(i).setNext(p.getWorkloads().get(i + 1));
			}
		}
	}

	/**
	 * Returns a shift object for a given shift number of a given collection of
	 * shifts.
	 * 
	 * @param shiftNo   Shift number to search for.
	 * @param allShifts Collection of all shifts.
	 * @return Shift object with matching shift number.
	 */
	private Shift shiftNoToObject(final int shiftNo, final Collection<Shift> allShifts) {
		Objects.requireNonNull(allShifts);

		for (final Shift s : allShifts) {
			if (s.getShiftNo() == shiftNo) {
				return s;
			}
		}

		throw new IllegalArgumentException("Shift with number <" + shiftNo + "> could not be found.");
	}

	/**
	 * Converts a given JSON array of patients to their model representations.
	 * 
	 * @param patients JSON array of patients.
	 */
	private void convertPatients(final JsonArray patients) {
		Objects.requireNonNull(patients);

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

			// add gender to all found genders if not already existent
			this.foundGenders.add(gender);

			createPatient(name, mandatory, gender, ageGroup, lengthOfStay, surgeryReleaseDay, surgeryDueDay,
					surgeryDuration, surgeonId, incompatibleRoomIds, workloadProduced, skillLevelRequired);
		}
	}

	/**
	 * Creates a new patient object in the model with the given parameters.
	 * 
	 * @param name                Name.
	 * @param mandatory           If true, the scheduling of the patient is
	 *                            required.
	 * @param gender              Gender.
	 * @param ageGroup            Age group.
	 * @param lengthOfStay        Length of the stay in number of days.
	 * @param surgeryReleaseDay   First possible admission day.
	 * @param surgeryDueDay       Latest possible admission day.
	 * @param surgeryDuration     Duration of the surgery in minutes.
	 * @param surgeonId           ID of the surgeon for the surgery.
	 * @param incompatibleRoomIds JSON array containing incompatible rooms.
	 * @param workloadProduced    JSON array containing the produced work load per
	 *                            shift.
	 * @param skillLevelRequired  JSON array containing the required nurse skill
	 *                            level per shift.
	 */
	private void createPatient(final String name, final boolean mandatory, final String gender, final String ageGroup,
			final int lengthOfStay, final int surgeryReleaseDay, final int surgeryDueDay, final int surgeryDuration,
			final String surgeonId, final JsonArray incompatibleRoomIds, final JsonArray workloadProduced,
			final JsonArray skillLevelRequired) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(gender);
		Objects.requireNonNull(ageGroup);
		Objects.requireNonNull(surgeonId);
		Objects.requireNonNull(incompatibleRoomIds);
		Objects.requireNonNull(workloadProduced);
		Objects.requireNonNull(skillLevelRequired);

		final Patient p = IhtcvirtualmetamodelFactory.eINSTANCE.createPatient();
		p.setName(name);
		p.setMandatory(mandatory);
		p.setGender(gender);
		p.setAgeGroup(foundAges.get(ageGroup));
		p.setStayLength(lengthOfStay);
		p.setEarliestDay(surgeryReleaseDay);
		p.setDueDay(surgeryDueDay);
		p.setSurgeryDuration(surgeryDuration);
		p.setIsOccupant(false);
		final Surgeon s = name2Surgeon.get(surgeonId);
		p.setSurgeon(s);
		for (final JsonElement incompatibleRoom : incompatibleRoomIds) {
			p.getIncompatibleRooms().add(name2Room.get((incompatibleRoom.getAsString())));
		}

		// Create workload objects
		if (workloadProduced.size() != skillLevelRequired.size()) {
			throw new UnsupportedOperationException(
					"Number of workloads produced did not match the number of skill levels required.");
		}

		for (int i = 0; i < workloadProduced.size(); i++) {
			final JsonElement workload = workloadProduced.get(i);
			final JsonElement skillLevel = skillLevelRequired.get(i);

			final Workload w = IhtcvirtualmetamodelFactory.eINSTANCE.createWorkload();
			w.setWorkloadValue(workload.getAsInt());
			w.setMinNurseSkill(skillLevel.getAsInt());
			p.getWorkloads().add(w);

			if (i == 0) {
				p.setFirstWorkload(w);
			}
		}

		// Set `prev` and `next` edges for all produced workloads
		linkWorkloads(p);

		this.model.getPatients().add(p);
	}
	
	/**
	 * Creates a new gender object for all unique string values found in the patient objects.
	 */
	public void createGenders() {
		for(String gender : foundGenders) {
			final Gender g = IhtcvirtualmetamodelFactory.eINSTANCE.createGender();
			g.setName(gender);
			this.model.getGenders().add(g);
		}
	}
	
	/**
	 * Converts the given JSON array of age groups to the model representations.
	 * Creates a Node for each unique integer representation of the age group. 
	 * 
	 * @param ageGroups JSON array of age groups.
	 */
	private void convertAgeGroups(final JsonArray ageGroups) {
		Objects.requireNonNull(ageGroups);

		int ageCounter = 0;
		for (final JsonElement ag : ageGroups) {
			final String name = ag.getAsString();
			this.foundAges.put(name, ageCounter);
			final AgeGroup agegroup = IhtcvirtualmetamodelFactory.eINSTANCE.createAgeGroup();
			agegroup.setGroup(ageCounter);
			this.model.getAgeGroups().add(agegroup);
			ageCounter++;
		}
	}

	/**
	 * Converts the given JSON object representing the input weights to the model
	 * representation.
	 * 
	 * @param weights JSON object containing the input weights.
	 */
	private void convertWeights(final JsonObject weights) {
		Objects.requireNonNull(weights);

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
		Objects.requireNonNull(operatingTheaters);

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
		Objects.requireNonNull(name);
		Objects.requireNonNull(availability);

		final OT ot = IhtcvirtualmetamodelFactory.eINSTANCE.createOT();
		ot.setName(name);
		// Create capacities, i.e., the `Capacity` objects
		for (int i = 0; i < availability.size(); i++) {
			final JsonElement a = availability.get(i);
			if (a.getAsInt() <= 0) {
				continue;
			}
			final Capacity c = IhtcvirtualmetamodelFactory.eINSTANCE.createCapacity();
			c.setDay(i);
			c.setMaxCapacity(a.getAsInt());
			ot.getCapacities().add(c);
		}
		this.model.getOts().add(ot);
	}

	/**
	 * Converts the given JSON array of surgeons to model objects.
	 * 
	 * @param surgeons JSON array of surgeons to convert.
	 */
	private void convertSurgeons(final JsonArray surgeons) {
		Objects.requireNonNull(surgeons);

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
		Objects.requireNonNull(name);
		Objects.requireNonNull(maxSurgeryTime);

		final Surgeon s = IhtcvirtualmetamodelFactory.eINSTANCE.createSurgeon();
		s.setName(name);
		// Create max surgery times, i.e., the `OpTime` objects
		for (int i = 0; i < maxSurgeryTime.size(); i++) {
			final JsonElement maxSurgeryTimeElement = maxSurgeryTime.get(i);
			if (maxSurgeryTimeElement.getAsInt() <= 0) {
				continue;
			}
			final OpTime opt = IhtcvirtualmetamodelFactory.eINSTANCE.createOpTime();
			opt.setDay(i);
			opt.setMaxOpTime(maxSurgeryTimeElement.getAsInt());
			s.getOpTimes().add(opt);
		}
		this.name2Surgeon.put(name, s);
		this.model.getSurgeons().add(s);
	}

	/**
	 * Converts the given JSON primitive of days to actual model information. In
	 * this case, the method saves the number of days from the JSON primitive
	 * to a field of this load class and saves it in the root of the model. 
	 * For each day a new Node of type "Day" is created and added to the model. 
	 * 
	 * @param days JSON primitive with the day-specific information.
	 */
	private void convertDays(final JsonPrimitive days) {
		Objects.requireNonNull(days);

		final int numberOfDays = days.getAsInt();

		if (numberOfDays <= 0) {
			throw new IllegalArgumentException("Number of days was <= 0.");
		}
		
		for(int i = 0; i<numberOfDays; i++) {
			final Day day = IhtcvirtualmetamodelFactory.eINSTANCE.createDay();
			day.setName("d"+i);
			day.setNumber(i);
			this.model.getDays().add(day);
		}

		this.numberOfFoundDays = numberOfDays;
		this.model.setPeriod(numberOfDays);
	}

	/**
	 * Converts the given JSON array of rooms to the model representations.
	 * 
	 * @param rooms JSON array of rooms.
	 */
	private void convertRooms(final JsonArray rooms) {
		Objects.requireNonNull(rooms);

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
		Objects.requireNonNull(name);

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

		this.name2Room.put(name, r);
		this.model.getRooms().add(r);
	}

	/**
	 * Converts the given JSON array of nurses to actual model elements.
	 * 
	 * @param nurses JSON array of nurses.
	 */
	private void convertNurses(final JsonArray nurses) {
		Objects.requireNonNull(nurses);

		for (final JsonElement n : nurses) {
			final String name = ((JsonObject) n).get("id").getAsString();
			final int skillLevel = ((JsonObject) n).get("skill_level").getAsInt();
			final JsonArray workingShifts = ((JsonObject) n).get("working_shifts").getAsJsonArray();
			createNurse(name, skillLevel, workingShifts);
		}
	}

	/**
	 * Creates a new nurse object within the model with the given details.
	 * 
	 * @param name          Name of the new nurse.
	 * @param skillLevel    Skill level of the new nurse.
	 * @param workingShifts JSON array of working shifts for the new nurse.
	 */
	private void createNurse(final String name, final int skillLevel, final JsonArray workingShifts) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(workingShifts);

		final Nurse nurse = IhtcvirtualmetamodelFactory.eINSTANCE.createNurse();
		nurse.setName(name);
		nurse.setSkillLevel(skillLevel);
		final List<Roster> rosters = convertRosters(workingShifts);
		nurse.getRosters().addAll(rosters);
		this.model.getNurses().add(nurse);
	}

	/**
	 * Converts the given JSON array of working shifts to model objects.
	 * 
	 * @param workingShifts JSON array representing the working shifts of a nurse.
	 * @return List of roster objects representing the given JSON array of working
	 *         shifts.
	 */
	private List<Roster> convertRosters(final JsonArray workingShifts) {
		Objects.requireNonNull(workingShifts);

		final List<Roster> rosters = new LinkedList<Roster>();
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

	/**
	 * Converts the given number of days to the corresponding number of shifts.
	 * 
	 * @param dayNumber Number Of days.
	 * @return Number of days times 3 (i.e., 3 shifts per day).
	 */
	private int convertDayType(final int dayNumber) {
		return dayNumber * 3;
	}

	/**
	 * Converts the given string to the numeric representation of a shift type.
	 * 
	 * @param shiftType String representation of a shift type.
	 * @return Numeric representation of the given shift type.
	 */
	private int convertShiftType(final String shiftType) {
		Objects.requireNonNull(shiftType);

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

}
