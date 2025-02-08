package ihtcmetamodel.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ihtcmetamodel.Hospital;
import ihtcmetamodel.IhtcmetamodelFactory;

/**
 * TODO.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class JsonToModelLoader {

	private Hospital model = null;

	public JsonToModelLoader() {
		model = IhtcmetamodelFactory.eINSTANCE.createHospital();
	}

	public Hospital getModel() {
		return model;
	}

	public void jsonToModel(final String path) {
		if (path == null || path.isBlank()) {
			throw new IllegalArgumentException("Given path <" + path + "> was null or blank.");
		}

		// global JSON object
		final JsonObject json = FileUtils.readFileToJson(path);

		// number of days
		final JsonPrimitive days = json.getAsJsonPrimitive("days");

		// number of skill levels
		final JsonPrimitive skillLevels = json.getAsJsonPrimitive("skill_levels");

		// shift types as String array
		final JsonArray shiftTypes = json.getAsJsonArray("shift_types");

		// age groups as String array
		final JsonArray ageGroups = json.getAsJsonArray("age_groups");

		// complex data types as arrays
		// occupants, patients, surgeons, operating theaters, rooms, nurses
		final JsonArray occupants = json.getAsJsonArray("occupants");
		final JsonArray patients = json.getAsJsonArray("patients");
		final JsonArray surgeons = json.getAsJsonArray("surgeons");
		final JsonArray operatingTheaters = json.getAsJsonArray("operating_theaters");
		final JsonArray rooms = json.getAsJsonArray("rooms");
		final JsonArray nurses = json.getAsJsonArray("nurses");

		// global weights
		final JsonObject weights = json.getAsJsonObject("weights");
		
		// TODO
	}

}
