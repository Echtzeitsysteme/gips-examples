package ihtcmetamodel.analyzer;

import ihtcmetamodel.Hospital;
import ihtcmetamodel.loader.JsonToModelLoader;

/**
 * Analyzer class to show details of IHTC instances.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcInstanceAnalyzer {

	/**
	 * The hospital model to work with.
	 */
	private Hospital model = null;

	/**
	 * Create a new instance of the IHTC instance analyzer with the given JSON input
	 * path.
	 * 
	 * @param jsonInputPath JSON input path of the IHTC instance file to load.
	 */
	public IhtcInstanceAnalyzer(final String jsonInputPath) {
		if (jsonInputPath == null || jsonInputPath.isBlank()) {
			throw new IllegalArgumentException("Given path <" + jsonInputPath + "> was null or blank.");
		}

		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(jsonInputPath);
		this.model = loader.getModel();
	}

	/**
	 * Prints the analyze information of the model to the console.
	 */
	public void analyze() {
		if (this.model == null) {
			throw new UnsupportedOperationException("Hospital model was null.");
		}

//		System.out.println("=> Running Analyzer");
//		System.out.println("#days: " + model.getDays().size());
//		System.out.println("#nurses: " + model.getNurses().size());
//		System.out.println("#occupants: " + model.getOccupants().size());
//		System.out.println("#patients: " + model.getPatients().size());
//		System.out.println("#surgeons: " + model.getSurgeons().size());
//		System.out.println("#ots: " + model.getOperatingTheaters().size());
//		System.out.println("#rooms: " + model.getRooms().size());
//		System.out.println("#genders: " + model.getGenders().size());
//		System.out.println("#age_groups: " + model.getAgeGroups().size());

		final String leftAlignFormat = "| %-12s | %3d |";

		System.out.format("+--------------+--------+%n");
		System.out.format("| Metric       | Value  |%n");
		System.out.format("+--------------+--------+%n");
		System.out.format(leftAlignFormat, "#days", model.getDays().size());
		System.out.format(leftAlignFormat, "#nurses", model.getNurses().size());
		System.out.format(leftAlignFormat, "#occupants", model.getOccupants().size());
		System.out.format(leftAlignFormat, "#patients", model.getPatients().size());
		System.out.format(leftAlignFormat, "#surgeons", model.getSurgeons().size());
		System.out.format(leftAlignFormat, "#ots", model.getOperatingTheaters().size());
		System.out.format(leftAlignFormat, "#rooms", model.getRooms().size());
		System.out.format(leftAlignFormat, "#genders", model.getGenders().size());
		System.out.format(leftAlignFormat, "#age_groups", model.getAgeGroups().size());
		System.out.format("+--------------+--------+%n");
	}

}
