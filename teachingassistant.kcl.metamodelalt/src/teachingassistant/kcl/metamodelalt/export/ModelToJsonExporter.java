package teachingassistant.kcl.metamodelalt.export;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import metamodel.EmploymentApproval;
import metamodel.EmploymentRating;
import metamodel.Module;
import metamodel.SessionOccurrence;
import metamodel.TA;
import metamodel.TAAllocation;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import metamodel.Week;

/**
 * Exports the new metamodel to JSON. This version removes references to old
 * fields like "code", "status", "minimumHoursPerWeek", etc., and uses only the
 * new fields from your updated Ecore (TA: name/maxHoursPerWeek/..., Module:
 * name, TeachingSession: name/hoursPaidPerOccurrence/numTAsPerSession, etc.).
 */
public class ModelToJsonExporter {

	private TAAllocation model;

	public ModelToJsonExporter(final TAAllocation model) {
		this.model = model;
	}

	public void modelToJson(final String outputPath) {
		// 1) Flatten TAs
		JsonArray tasJson = new JsonArray();
		for (TA ta : model.getTas()) {
			tasJson.add(convertTAtoJson(ta));
		}

		// 2) Flatten Modules
		JsonArray modulesJson = new JsonArray();
		for (Module m : model.getModules()) {
			modulesJson.add(convertModuleToJson(m));
		}

		// 3) Flatten TeachingSessions (collected across all modules)
		JsonArray sessionsJson = flattenTeachingSessionsFromModules();

		// 4) Flatten SessionOccurrences
		JsonArray occurrencesJson = flattenSessionOccurrencesFromModules();

		// 5) Flatten TimeTableEntries
		JsonArray timeEntriesJson = flattenTimeTableEntriesFromModules();

		// 6) Flatten EmploymentApprovals
		JsonArray approvalsJson = flattenApprovalsFromModules();

		// Build final JSON object
		JsonObject json = new JsonObject();
		json.add("tas", tasJson);
		json.add("modules", modulesJson);
		json.add("teachingsessions", sessionsJson);
		json.add("sessionoccurrences", occurrencesJson);
		json.add("timetableentries", timeEntriesJson);
		json.add("employmentapprovals", approvalsJson);

		// Write JSON to file (assuming FileUtils is a helper you already have)
		FileUtils.writeFileFromJson(outputPath, json);
	}

	// --------------------------------------------------------------------------------
	// TA flattening
	// --------------------------------------------------------------------------------

	private JsonObject convertTAtoJson(TA ta) {
		JsonObject taJson = new JsonObject();
		taJson.addProperty("name", ta.getName());
		taJson.addProperty("maxHoursPerWeek", ta.getMaxHoursPerWeek());
		taJson.addProperty("maxHoursPerYear", ta.getMaxHoursPerYear());
		// Remove old references like setMinimumHoursPerWeek, setMaximumDaysPerWeek,
		// etc.
		return taJson;
	}

	// --------------------------------------------------------------------------------
	// Module flattening
	// --------------------------------------------------------------------------------

	private JsonObject convertModuleToJson(Module module) {
		JsonObject moduleJson = new JsonObject();
		// In the new Ecore, we only have "name" for the module:
		moduleJson.addProperty("name", module.getName());
		// If desired, you can also embed the session names or something similar:
		JsonArray sessionNames = new JsonArray();
		for (TeachingSession s : module.getSessions()) {
			sessionNames.add(s.getName());
		}
		moduleJson.add("sessionNames", sessionNames);

		// Also optionally embed the rating summary or something else
		// but keep it simple for now
		return moduleJson;
	}

	// --------------------------------------------------------------------------------
	// TeachingSession flattening
	// --------------------------------------------------------------------------------

	/**
	 * Gathers all TeachingSessions across all Modules, returns a single JSON array.
	 */
	private JsonArray flattenTeachingSessionsFromModules() {
		JsonArray flatSessions = new JsonArray();
		Set<TeachingSession> seen = new HashSet<>();
		for (Module m : model.getModules()) {
			for (TeachingSession s : m.getSessions()) {
				if (!seen.contains(s)) {
					seen.add(s);
					JsonObject sessionObj = new JsonObject();
					sessionObj.addProperty("name", s.getName());
					sessionObj.addProperty("hoursPaidPerOccurrence", s.getHoursPaidPerOccurrence());
					sessionObj.addProperty("numTAsPerSession", s.getNumTAsPerSession());

					// Add timeTableWeeks as an array
					JsonArray weeksArr = new JsonArray();
					if (s.getTimeTableWeeks() != null) {
						for (Week week : s.getTimeTableWeeks()) {
							if (week != null) {
								weeksArr.add(week.getNumber());
							}
						}
					}
					sessionObj.add("timeTableWeeks", weeksArr);

					// Optionally note which Module it's in:
					sessionObj.addProperty("moduleName", m.getName());

					flatSessions.add(sessionObj);
				}
			}
		}
		return flatSessions;
	}

	// --------------------------------------------------------------------------------
	// SessionOccurrence flattening
	// --------------------------------------------------------------------------------

	private JsonArray flattenSessionOccurrencesFromModules() {
		JsonArray flatOccurrences = new JsonArray();
		// We'll collect them from every session in every module
		for (Module m : model.getModules()) {
			for (TeachingSession s : m.getSessions()) {
				for (SessionOccurrence occ : s.getOccurrences()) {
					JsonObject occJson = new JsonObject();
					// No "id" in the new Ecore unless you added it
					occJson.addProperty("timeTableWeek", occ.getTimeTableWeek());
					// Collect the TAs assigned
					JsonArray assignedTAs = new JsonArray();
					for (TA ta : occ.getTas()) {
						assignedTAs.add(ta.getName());
					}
					occJson.add("tas", assignedTAs);

					// Link back to the session/module
					occJson.addProperty("sessionName", s.getName());
					occJson.addProperty("moduleName", m.getName());

					flatOccurrences.add(occJson);
				}
			}
		}
		return flatOccurrences;
	}

	// --------------------------------------------------------------------------------
	// TimeTableEntry flattening
	// --------------------------------------------------------------------------------

	private JsonArray flattenTimeTableEntriesFromModules() {
		JsonArray flatEntries = new JsonArray();
		Set<TimeTableEntry> seen = new HashSet<>();

		// Each TeachingSession has getEntries(), which references TimeTableEntry
		for (Module m : model.getModules()) {
			for (TeachingSession s : m.getSessions()) {
				for (TimeTableEntry entry : s.getEntries()) {
					if (!seen.contains(entry)) {
						seen.add(entry);
						// Convert to JSON
						flatEntries.add(convertTimeTableEntryToJson(entry, s, m));
					}
				}
			}
		}
		return flatEntries;
	}

	private JsonObject convertTimeTableEntryToJson(TimeTableEntry entry, TeachingSession session, Module module) {
		JsonObject entryJson = new JsonObject();
		// timeTableWeeks is a multi-valued EAttribute, so we collect them
		JsonArray weeksArr = new JsonArray();
		if (entry.getTimeTableWeeks() != null) {
			for (Week w : entry.getTimeTableWeeks()) {
				if (w != null) {
					weeksArr.add(w.getNumber());
				}
			}
		}
		entryJson.add("timeTableWeeks", weeksArr);

		// day/room/time info
		entryJson.addProperty("weekDay", entry.getWeekDay());
		entryJson.addProperty("room", entry.getRoom());

		// The Ecore uses Date for startTime/endTime, so we might format them as
		// strings:
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		entryJson.addProperty("startTime", formatIfNotNull(sdf, entry.getStartTime()));
		entryJson.addProperty("endTime", formatIfNotNull(sdf, entry.getEndTime()));

		// Link back to the session
		entryJson.addProperty("sessionName", session.getName());
		entryJson.addProperty("moduleName", module.getName());

		return entryJson;
	}

	private String formatIfNotNull(SimpleDateFormat sdf, Date d) {
		return (d == null) ? "" : sdf.format(d);
	}

	// --------------------------------------------------------------------------------
	// EmploymentApproval flattening
	// --------------------------------------------------------------------------------

	/**
	 * Each Module has getApprovals() referencing EmploymentApproval objects. We
	 * export them with "moduleName", "taName", and "rating".
	 */
	private JsonArray flattenApprovalsFromModules() {
		JsonArray approvalsArr = new JsonArray();
		for (Module m : model.getModules()) {
			for (EmploymentApproval ap : m.getApprovals()) {
				JsonObject apJson = new JsonObject();
				apJson.addProperty("moduleName", m.getName());
				if (ap.getTa() != null) {
					apJson.addProperty("taName", ap.getTa().getName());
				}
				// rating is an EEnum: RED, AMBER, GREEN
				EmploymentRating rating = ap.getRating();
				apJson.addProperty("rating", (rating == null ? "UNKNOWN" : rating.toString()));

				approvalsArr.add(apJson);
			}
		}
		return approvalsArr;
	}

}
