package teachingassistant.kcl.metamodelalt.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import metamodel.Assistant;
import metamodel.Day;
import metamodel.Department;
import metamodel.Lecturer;
import metamodel.Skill;
import metamodel.Timeslot;
import metamodel.Tutorial;
import metamodel.Week;

/**
 * This model exporter can be used to convert an EMF model to the respective
 * JSON output format required by the competition.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class ModelToJsonExporter {

	/**
	 * Department model to work with.
	 */
	private Department model = null;

	/**
	 * Initializes a new model to JSON exporter object with a given Department
	 * model.
	 * 
	 * @param model Department model.
	 */
	public ModelToJsonExporter(final Department model) {
		if (model == null) {
			throw new IllegalArgumentException("Given model was null.");
		}

		this.model = model;
	}

	/**
	 * Converts the contained model to a JSON output file written to the given
	 * output path.
	 * 
	 * @param outputPath Output path to write the JSON output file to.
	 */
	public void modelToJson(final String outputPath) {
		modelToJson(outputPath, false);
	}

	/**
	 * Converts the contained model to a JSON output file written to the given
	 * output path.
	 * 
	 * @param outputPath Output path to write the JSON output file to.
	 * @param verbose    If true, the exporter will print more information about the
	 *                   model.
	 */
	public void modelToJson(final String outputPath, final boolean verbose) {
		if (outputPath == null || outputPath.isBlank()) {
			throw new IllegalArgumentException("Given path <" + outputPath + "> was null or blank.");
		}

		// If path contains at least one slash `/`, create the folder if not existent
		if (outputPath.contains("/")) {
			final int lastSlashIndex = outputPath.lastIndexOf("/");
			FileUtils.prepareFolder(outputPath.substring(0, lastSlashIndex));
		}

		final JsonArray assistantsJson = new JsonArray();
		for (final Assistant a : model.getAssistants()) {
			assistantsJson.add(convertAssistantToJson(a));
		}

		final JsonArray tutorialsJson = new JsonArray();
		for (final Tutorial t : model.getTutorials()) {
			tutorialsJson.add(convertTutorialToJson(t));
		}

		final JsonArray timeslotsJson = new JsonArray();
		for (final Timeslot t : model.getTimeslots()) {
			timeslotsJson.add(convertTimeslotToJson(t));
		}

		final JsonArray lecturersJson = new JsonArray();
		for (final Lecturer l : model.getLecturers()) {
			lecturersJson.add(lecturerToJson(l));
		}

		final JsonArray daysJson = new JsonArray();
		for (final Day d : model.getDays()) {
			daysJson.add(convertDayToJson(d));
		}

		final JsonArray weeksJson = new JsonArray();
		for (final Week w : model.getWeeks()) {
			weeksJson.add(convertWeekToJson(w));
		}

		// Global JSON object
		final JsonObject json = new JsonObject();
		json.add("assistants", assistantsJson);
		json.add("tutorials", tutorialsJson);
		json.add("timeslots", timeslotsJson);
		json.add("lecturers", lecturersJson);
		json.add("days", daysJson);
		json.add("weeks", weeksJson);

		// Write to output JSON file
		FileUtils.writeFileFromJson(outputPath, json);
	}

	private JsonObject convertAssistantToJson(final Assistant assistant) {
		final JsonObject assistantJson = new JsonObject();
		assistantJson.addProperty("minimumHoursPerWeek", assistant.getMinimumHoursPerWeek());
		assistantJson.addProperty("maximumHoursPerWeek", assistant.getMaximumHoursPerWeek());
		assistantJson.addProperty("maximumDaysPerWeek", assistant.getMaximumDaysPerWeek());
		assistantJson.addProperty("maximumHoursTotal", assistant.getMaximumHoursTotal());

		final JsonArray skills = new JsonArray();
		assistant.getSkills().forEach(s -> {
			skills.add(convertSkillToJson(s));
		});
		assistantJson.add("skills", skills);

		final JsonArray blockedDates = new JsonArray();
		assistant.getBlockedDates().forEach(bs -> {
			blockedDates.add(bs.getName());
		});

		assistantJson.add("blockedDates", blockedDates);
		return assistantJson;
	}

	private JsonObject convertSkillToJson(final Skill skill) {
		final JsonObject skillJson = new JsonObject();
		skillJson.addProperty("name", skill.getName());
		skillJson.addProperty("preference", skill.getPreference());
		return skillJson;
	}

	private JsonObject convertWeekToJson(final Week week) {
		final JsonObject weekJson = new JsonObject();
		weekJson.addProperty("name", week.getName());
		final JsonArray days = new JsonArray();
		week.getDays().forEach(d -> {
			days.add(d.getName());
		});
		weekJson.add("days", days);
		return weekJson;
	}

	private JsonObject convertDayToJson(final Day day) {
		final JsonObject dayJson = new JsonObject();
		dayJson.addProperty("name", day.getName());
		final JsonArray timeslots = new JsonArray();
		day.getTimeslots().forEach(ts -> {
			timeslots.add(ts.getId());
		});
		dayJson.add("timeslots", timeslots);
		dayJson.addProperty("week", day.getWeek().getName());
		return dayJson;
	}

	private JsonObject convertTimeslotToJson(final Timeslot timeslot) {
		final JsonObject timeslotJson = new JsonObject();
		timeslotJson.addProperty("id", timeslot.getId());
		timeslotJson.addProperty("day", timeslot.getDay().getName());
		timeslotJson.addProperty("name", timeslot.getName());
		return timeslotJson;
	}

	private JsonObject convertTutorialToJson(final Tutorial tutorial) {
		final JsonObject tutorialJson = new JsonObject();
		tutorialJson.addProperty("name", tutorial.getName());
		if (tutorial.getGivenBy() != null) {
			tutorialJson.addProperty("givenBy", tutorial.getGivenBy().getName());
		}
		tutorialJson.addProperty("duration", tutorial.getDuration());
		tutorialJson.addProperty("timeslot", tutorial.getTimeslot().getId());
		tutorialJson.addProperty("lecturer", tutorial.getLecturer().getName());
		tutorialJson.addProperty("skillType", tutorial.getSkillType());
		return tutorialJson;
	}

	private JsonObject lecturerToJson(final Lecturer lecturer) {
		final JsonObject lecturerJson = new JsonObject();
		lecturerJson.addProperty("name", lecturer.getName());
		final JsonArray tutorials = new JsonArray();
		lecturer.getTutorials().forEach(t -> {
			tutorials.add(t.getName());
		});
		lecturerJson.add("tutorials", tutorials);
		lecturerJson.addProperty("skillTypeName", lecturer.getSkillTypeName());
		lecturerJson.addProperty("maximumNumberOfTas", lecturer.getMaximumNumberOfTas());
		return lecturerJson;
	}

}
