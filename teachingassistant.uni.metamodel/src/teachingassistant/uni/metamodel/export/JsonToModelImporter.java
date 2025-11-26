package teachingassistant.uni.metamodel.export;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import metamodel.Campus;
import metamodel.EmploymentApproval;
import metamodel.EmploymentRating;
import metamodel.MetamodelFactory;
import metamodel.Room;
import metamodel.SessionOccurrence;
import metamodel.TaAllocation;
import metamodel.TeachingAssistant;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import metamodel.Week;
import teachingassistant.uni.utils.DateTimeUtil;

public class JsonToModelImporter {

	public final static int MAX_HOURS_PER_YEAR = 156;

	// Transfered base week Monday from `SimpleTaUniGenerator`
	private final static LocalDateTime BASE_WEEK_MONDAY = LocalDateTime.of(2024, Month.FEBRUARY, 12, 0, 0);

	private TaAllocation model = null;

	public static void main(final String[] args) {
		Objects.requireNonNull(args);

		if (args.length != 2) {
			throw new IllegalArgumentException("First argument: JSON input path; second argument: XMI output path.");
		}

		final String jsonInputPath = args[0];
		final String xmiOutputPath = args[1];

		// Load model from file
		final JsonToModelImporter importer = new JsonToModelImporter();
		importer.jsonToModel(jsonInputPath);
		final TaAllocation model = importer.getModel();

		// Write XMI to file
		try {
			FileUtils.save(model, xmiOutputPath);
		} catch (final IOException e) {
			e.printStackTrace();
			throw new InternalError(e.getLocalizedMessage());
		}
	}

	public JsonToModelImporter() {
		model = MetamodelFactory.eINSTANCE.createTaAllocation();
	}

	public TaAllocation getModel() {
		return model;
	}

	public void jsonToModel(final String inputPath) {
		if (inputPath == null || inputPath.isBlank()) {
			throw new IllegalArgumentException("Given path <" + inputPath + "> was null or blank.");
		}

		// global JSON object
		final JsonObject json = FileUtils.readFileToJson(inputPath);

		// campuses
		final JsonArray campuses = json.getAsJsonArray("campuses");
		convertCampuses(campuses);

		// rooms
		final JsonArray rooms = json.getAsJsonArray("rooms");
		convertRooms(rooms);

		// modules
		final JsonArray modules = json.getAsJsonArray("modules");
		convertModules(modules);

		// weeks
		final JsonArray weeks = json.getAsJsonArray("weeks");
		convertWeeks(weeks);

		// sessions
		final JsonArray sessions = json.getAsJsonArray("sessions");
		convertSessions(sessions);

		// TAs
		final JsonArray tas = json.getAsJsonArray("tas");
		convertTeachingAssistants(tas);

		// employment approvals
		final JsonArray eas = json.getAsJsonArray("employmentapprovals");
		convertEmploymentApprovals(eas);

		// TA availability is currently covered via the TAs themselves
//		// TA availability
//		final JsonArray taAvailability = json.getAsJsonArray("ta_availability");

		// All primitives are currently not needed
		// primitives
//		final int nTas = json.getAsJsonPrimitive("n_tas").getAsInt();
//		final int nModules = json.getAsJsonPrimitive("n_modules").getAsInt();
//		final int nSessions = json.getAsJsonPrimitive("n_sessions").getAsInt();
//		final int nWeeks = json.getAsJsonPrimitive("n_weeks").getAsInt();
//		final int nWeekdays = json.getAsJsonPrimitive("n_weekdays").getAsInt();
//		final int nRooms = json.getAsJsonPrimitive("n_rooms").getAsInt();

	}

	private void convertTeachingAssistants(final JsonArray json) {
		Objects.requireNonNull(json);

		for (final JsonElement ta : json) {
			final int taId = ((JsonObject) ta).get("id").getAsInt() - 1;
			final String name = ((JsonObject) ta).get("name").getAsString();
			final int maxHoursPerWeek = ((JsonObject) ta).get("maxHoursPerWeek").getAsInt();

			// Instead of "availability" we only have to mark blocked dates

			final Set<TimeTableEntry> blockedEntries = new HashSet<TimeTableEntry>();

			// Multi-dimensional array of weeks(days(slots)))
			final JsonArray availability = ((JsonObject) ta).get("availability").getAsJsonArray();
			// Iterate over all weeks
			int weekCounter = 0;
			for (final JsonElement week : availability) {
				// Iterate over all days
				int dayCounter = 0;
				for (final JsonElement day : week.getAsJsonArray()) {
					// Iterate over all slots
					int slotCounter = 0;
					for (final JsonElement slot : day.getAsJsonArray()) {
						final int available = slot.getAsInt();
						// If the TA is unavailable in this specific slot
						if (available == 0) {
							// Find all time table entries that would be affected
							// Duplicates will not be added
							blockedEntries.addAll(findTimeTableEntriesWithSlot(weekCounter, dayCounter, slotCounter));
						}
						slotCounter++;
					}
					dayCounter++;
				}
				weekCounter++;
			}

			// "qualifiedModules" are not necessary, since they are already contained in the
			// employment approvals

			// Create the new teaching assistant object including the necessary containment
			createTeachingAssistant(taId, name, maxHoursPerWeek, MAX_HOURS_PER_YEAR, blockedEntries);
		}
	}

	private void createTeachingAssistant(final int id, final String name, final int maxHoursPerWeek,
			final int maxHoursPerYear, final Set<TimeTableEntry> blockedEntries) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(blockedEntries);

		final TeachingAssistant ta = MetamodelFactory.eINSTANCE.createTeachingAssistant();
		ta.setId(id);
		ta.setName(name);
		ta.setMaxHoursPerWeek(maxHoursPerWeek);
		ta.setMaxHoursPerYear(maxHoursPerYear);
		ta.getUnavailable().addAll(blockedEntries);

		model.getTas().add(ta);
	}

	private List<TimeTableEntry> findTimeTableEntriesWithSlot(final int week, final int day, final int slot) {
		final List<TimeTableEntry> matches = new ArrayList<TimeTableEntry>();

		for (final TimeTableEntry candidate : model.getTimetable()) {
			// Week matches
			if (candidate.getTimeTableWeeks().contains(getWeekById(week))) {
				// Day matches
				if (candidate.getWeekDay().equals(convertDayIndexToName(day))) {
					// Slot lays in between `start` and `end` of the candidate
					final long startEntry = candidate.getStartEpoch();
					final long endEntry = candidate.getEndEpoch();
					final long startSlot = convertSlotStartToEpoch(week, day, slot);
					final long endSlot = convertSlotStartToEnd(week, day, slot);

					// Check if the two time horizons overlap in some way
					if (!(startEntry < endSlot && endEntry > startSlot)) {
						matches.add(candidate);
					}
				}
			}
		}

		return matches;
	}

	private long convertSlotStartToEpoch(final int week, final int day, final int slot) {
		final LocalDateTime dayTime = BASE_WEEK_MONDAY.plusDays(week * 7 + day);
		final LocalDateTime specificSlotStartTime = dayTime.plusHours(8).plusMinutes(slot * 30);
		return DateTimeUtil.convertDateTimeToSeconds(DateTimeUtil.localDateTimeToDate(specificSlotStartTime));
	}

	private long convertSlotStartToEnd(final int week, final int day, final int slot) {
		final LocalDateTime dayTime = BASE_WEEK_MONDAY.plusDays(week * 7 + day);
		final LocalDateTime specificSlotStartTime = dayTime.plusHours(8).plusMinutes(slot * 30 + 30);
		return DateTimeUtil.convertDateTimeToSeconds(DateTimeUtil.localDateTimeToDate(specificSlotStartTime));
	}

	private void convertEmploymentApprovals(final JsonArray json) {
		Objects.requireNonNull(json);

		for (final JsonElement ea : json) {
			final int taId = ((JsonObject) ea).get("ta").getAsInt() - 1;
			final int moduleId = ((JsonObject) ea).get("module").getAsInt() - 1;
			final String ratingString = ((JsonObject) ea).get("rating").getAsString();
			createEmploymentApproval(ratingString, taId, moduleId);
		}
	}

	private void createEmploymentApproval(final String ratingString, final int taId, final int moduleId) {
		Objects.requireNonNull(ratingString);

		final EmploymentApproval ea = MetamodelFactory.eINSTANCE.createEmploymentApproval();
		ea.setTa(getTaById(taId));
		final EmploymentRating ratingEnum = convertStringToEmploymentRatingEnum(ratingString);
		final int ratingNumeric = convertStringToEmploymentRatingNumeric(ratingString);
		ea.setRating(ratingEnum);
		ea.setRatingNumeric(ratingNumeric);

		final metamodel.Module module = getModuleById(moduleId);
		module.getApprovals().add(ea);
	}

	private EmploymentRating convertStringToEmploymentRatingEnum(final String rating) {
		Objects.requireNonNull(rating);
		switch (rating) {
		case "GREEN":
			return EmploymentRating.GREEN;
		case "AMBER":
			return EmploymentRating.AMBER;
		case "RED":
			return EmploymentRating.RED;
		default:
			throw new UnsupportedOperationException();
		}
	}

	private int convertStringToEmploymentRatingNumeric(final String rating) {
		Objects.requireNonNull(rating);
		switch (rating) {
		case "GREEN":
			return 2;
		case "AMBER":
			return 1;
		case "RED":
			return 0;
		default:
			throw new UnsupportedOperationException();
		}
	}

	private void convertRooms(final JsonArray json) {
		Objects.requireNonNull(json);

		for (final JsonElement r : json) {
			final int id = ((JsonObject) r).get("id").getAsInt() - 1;
			final String name = ((JsonObject) r).get("name").getAsString();
			final int campusId = ((JsonObject) r).get("campus").getAsInt() - 1;
			createRoom(id, name, campusId);
		}

	}

	private void createRoom(final int id, final String name, final int campusId) {
		Objects.requireNonNull(name);

		final Room room = MetamodelFactory.eINSTANCE.createRoom();
		room.setId(id);
		room.setName(name);
		room.setCampus(getCampusById(campusId));
		model.getRooms().add(room);
	}

	private void convertCampuses(final JsonArray json) {
		Objects.requireNonNull(json);

		for (final JsonElement c : json) {
			final int id = ((JsonObject) c).get("id").getAsInt() - 1;
			final String name = ((JsonObject) c).get("name").getAsString();
			createCampus(id, name);
		}
	}

	private void createCampus(final int id, final String name) {
		Objects.requireNonNull(name);

		final Campus campus = MetamodelFactory.eINSTANCE.createCampus();
		campus.setId(id);
		campus.setName(name);
		model.getCampuses().add(campus);
	}

	private void convertModules(final JsonArray json) {
		Objects.requireNonNull(json);

		for (final JsonElement m : json) {
			// ID currently not needed
			final int id = ((JsonObject) m).get("id").getAsInt() - 1;
			final String name = ((JsonObject) m).get("name").getAsString();
			createModule(name, id);
		}
	}

	private void createModule(final String name, final int id) {
		Objects.requireNonNull(name);

		final metamodel.Module module = MetamodelFactory.eINSTANCE.createModule();
		module.setName(name);
		module.setId(id);
		model.getModules().add(module);
	}

	private void convertWeeks(final JsonArray json) {
		Objects.requireNonNull(json);

		for (final JsonElement w : json) {
			final int id = ((JsonObject) w).get("id").getAsInt() - 1;
			createWeek(id);
		}
	}

	private void createWeek(final int id) {
		final Week week = MetamodelFactory.eINSTANCE.createWeek();
		week.setId(id);
		model.getWeeks().add(week);
	}

	private void convertSessions(final JsonArray json) {
		Objects.requireNonNull(json);

		for (final JsonElement s : json) {
			final int id = ((JsonObject) s).get("id").getAsInt() - 1;
			final String name = ((JsonObject) s).get("name").getAsString();
			final int moduleId = ((JsonObject) s).get("module").getAsInt() - 1;
			final int numTasRequired = ((JsonObject) s).get("numTAsRequired").getAsInt();
			final int hoursPaid = ((JsonObject) s).get("hoursPaid").getAsInt();
			final JsonArray occurrences = ((JsonObject) s).get("occurrences").getAsJsonArray();
			final List<SessionOccurrence> sessionOccurrences = convertOccurrencesToSessionOccurrences(occurrences);
			final List<TimeTableEntry> timeTableEntries = convertOccurrencesToTimeTableEntries(occurrences);

			final Set<Week> collectedTimeTableWeeks = new HashSet<Week>();
			timeTableEntries.forEach(tte -> {
				collectedTimeTableWeeks.addAll(tte.getTimeTableWeeks());
			});

			createTeachingSession(id, name, moduleId, hoursPaid, numTasRequired, sessionOccurrences, timeTableEntries,
					collectedTimeTableWeeks);
		}
	}

	private void createTeachingSession(final int id, final String name, final int moduleId,
			final int hoursPaidPerOccurrence, final int numTasPerSession,
			final List<SessionOccurrence> sessionOccurrences, final List<TimeTableEntry> timeTableEntries,
			final Set<Week> timeTableWeeks) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(sessionOccurrences);

		final TeachingSession session = MetamodelFactory.eINSTANCE.createTeachingSession();
		session.setId(id);
		session.setName(name);
		session.setHoursPaidPerOccurrence(hoursPaidPerOccurrence);
		session.setNumTasPerSession(numTasPerSession);
		session.getOccurrences().addAll(sessionOccurrences);
		session.getEntries().addAll(timeTableEntries);
		session.getTimeTableWeeks().addAll(timeTableWeeks);

		// Add reference from `TimeTableEntry` to `TeachingSession`
		session.getEntries().forEach(entry -> {
			entry.setSession(session);
		});

		// Containment
		getModuleById(moduleId).getSessions().add(session);
	}

	private List<SessionOccurrence> convertOccurrencesToSessionOccurrences(final JsonArray json) {
		Objects.requireNonNull(json);

		final List<SessionOccurrence> occurrences = new ArrayList<SessionOccurrence>();

		for (final JsonElement o : json) {
			final int weekNumber = ((JsonObject) o).get("week").getAsInt() - 1;
			final SessionOccurrence so = createSessionOccurrence(weekNumber);
			occurrences.add(so);
		}

		return occurrences;
	}

	private List<TimeTableEntry> convertOccurrencesToTimeTableEntries(final JsonArray json) {
		Objects.requireNonNull(json);

		final List<TimeTableEntry> timeTableEntries = new ArrayList<TimeTableEntry>();

		for (final JsonElement o : json) {
			final int weekNumber = ((JsonObject) o).get("week").getAsInt() - 1;
			final int roomId = ((JsonObject) o).get("room").getAsInt() - 1;
			final int start = ((JsonObject) o).get("start").getAsInt();
			final int day = ((JsonObject) o).get("day").getAsInt() - 1;
			final int end = ((JsonObject) o).get("end").getAsInt();
			final TimeTableEntry entry = createTimeTableEntry(weekNumber, day, start, end, roomId);
			timeTableEntries.add(entry);
		}

		// Containment
		model.getTimetable().addAll(timeTableEntries);
		return timeTableEntries;
	}

	private TimeTableEntry createTimeTableEntry(final int week, final int day, final int start, final int end,
			final int roomId) {
		final TimeTableEntry entry = MetamodelFactory.eINSTANCE.createTimeTableEntry();

		// Convert to week day name
		// Pick a day offset (0..4 => Mon..Fri)
		final LocalDateTime dayTime = BASE_WEEK_MONDAY.plusDays(week * 7 + day);
		entry.setWeekDay(dayTime.getDayOfWeek().toString());

		final Week w = getWeekById(week);
		entry.getTimeTableWeeks().add(w);

		// Start and end epochs - to be checked later on
		final LocalDateTime startTime = dayTime.plusMinutes(start);
		final LocalDateTime endTime = dayTime.plusMinutes(end);
		entry.setStartTime(DateTimeUtil.localDateTimeToDate(startTime));
		entry.setEndTime(DateTimeUtil.localDateTimeToDate(endTime));
		entry.setStartEpoch(DateTimeUtil.convertDateTimeToSeconds(entry.getStartTime()));
		entry.setEndEpoch(DateTimeUtil.convertDateTimeToSeconds(entry.getEndTime()));
		entry.setRoom(getRoomById(roomId));
		return entry;
	}

	private SessionOccurrence createSessionOccurrence(final int week) {
		final SessionOccurrence so = MetamodelFactory.eINSTANCE.createSessionOccurrence();
		so.setTimeTableWeek(week);
		return so;
	}

	//
	// Getters
	//

	private Campus getCampusById(final int id) {
		for (final Campus c : model.getCampuses()) {
			if (c.getId() == id) {
				return c;
			}
		}

		throw new UnsupportedOperationException("Campus with ID <" + id + "> not found.");
	}

	private Room getRoomById(final int id) {
		for (final Room r : model.getRooms()) {
			if (r.getId() == id) {
				return r;
			}
		}

		throw new UnsupportedOperationException("Room with ID <" + id + "> not found.");
	}

	private Week getWeekById(final int id) {
		for (final Week w : model.getWeeks()) {
			if (w.getId() == id) {
				return w;
			}
		}

		throw new UnsupportedOperationException("Week with ID <" + id + "> not found.");
	}

	private metamodel.Module getModuleById(final int id) {
		for (final metamodel.Module m : model.getModules()) {
			if (m.getId() == id) {
				return m;
			}
		}

		throw new UnsupportedOperationException("Module with ID <" + id + "> not found.");
	}

	private TeachingAssistant getTaById(final int id) {
		for (final TeachingAssistant ta : model.getTas()) {
			if (ta.getId() == id) {
				return ta;
			}
		}

		throw new UnsupportedOperationException("TA with ID <" + id + "> not found.");
	}

	private String convertDayIndexToName(final int day) {
		return BASE_WEEK_MONDAY.plusDays(day).getDayOfWeek().name();
	}

}
