package ihtcgips.virtualrules;

import org.eclipse.emf.common.util.URI;

import ihtcgips.patterns.utils.Utilities;
import ihtcgips.virtualrules.api.VirtualrulesAPI;
import ihtcmetamodel.Hospital;

public class TimedRunner {

	private static String INPUT_FOLDER_PATH = "/home/mkratz/git/gips-examples/ihtcmetamodel/resources/ihtc2024_competition_instances/";
	private static String INPUT_PATH = null;
	private static String MODEL_PATH = null;

	public static void main(final String[] args) {
		new TimedRunner().run();
	}

	private void run() {
		for (int i = 1; i <= 30; i++) {
			System.out.println("==> Running scenario " + i);
			runSingleFile(i);
			System.out.println("---");
		}
		System.exit(0);
	}

	private void runSingleFile(final int scenarioNumber) {
		INPUT_PATH = INPUT_FOLDER_PATH + "i" + (scenarioNumber <= 9 ? "0" : "") + scenarioNumber + ".json";
		MODEL_PATH = INPUT_PATH.replace(".json", ".xmi");

		Utilities.transformJsonToModel(INPUT_PATH, MODEL_PATH);
		final Hospital model = Utilities.loadHospitalFromFile(URI.createFileURI(MODEL_PATH));
		runGtRules(model);
	}

	private void runGtRules(final Hospital model) {
		final long tick = System.nanoTime();

		final VirtualrulesAPI api = new GtApp(model).initAPI();
		System.out.println("assignNurseToRoomShift: " + api.assignNurseToRoomShift().findMatches().size());
		api.assignNurseToRoomShift().findMatches().forEach(m -> {
			api.assignNurseToRoomShift().apply(m);
		});
		System.out.println("assignAdmissionDayToPatient: " + api.assignAdmissionDayToPatient().findMatches().size());
		api.assignAdmissionDayToPatient().findMatches().forEach(m -> {
			api.assignAdmissionDayToPatient().apply(m);
		});
		System.out.println("assignRoomToPatient: " + api.assignRoomToPatient().findMatches().size());
		api.assignRoomToPatient().findMatches().forEach(m -> {
			api.assignRoomToPatient().apply(m);
		});
		System.out.println("assignSurgeryToPatient: " + api.assignSurgeryToPatient().findMatches().size());
		api.assignSurgeryToPatient().findMatches().forEach(m -> {
			api.assignSurgeryToPatient().apply(m);
		});
		Utilities.deleteFile(MODEL_PATH);

		final long tock = System.nanoTime();
		final double time = Utilities.tickTockToSeconds(tick, tock);
		System.out.println("GT application runtime: " + time + "s.");
		api.terminate();
	}

}
