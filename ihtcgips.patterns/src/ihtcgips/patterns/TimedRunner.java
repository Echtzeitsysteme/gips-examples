package ihtcgips.patterns;

import org.eclipse.emf.common.util.URI;

import ihtcgips.patterns.api.PatternsAPI;
import ihtcgips.patterns.utils.Utilities;
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
		final double javaTime = runJavaPatterns(model);
		final double gtTime = runGtPatterns(model);

		System.out.println("GT/Java ration: " + gtTime / javaTime);
	}

	private double runJavaPatterns(final Hospital model) {
		final long tick = System.nanoTime();

		final Patterns javaPatterns = new Patterns();
		javaPatterns.dayRoomTuple(model);
		javaPatterns.daySurgeonTuple(model);
		javaPatterns.dayOperatingTheaterTuple(model);
		javaPatterns.roomShiftTuple(model);
		javaPatterns.dayRoomGenderTruple(model);

		final long tock = System.nanoTime();
		final double time = Utilities.tickTockToSeconds(tick, tock);
		System.out.println("Java pattern runtime: " + time + "s.");
		return time;
	}

	private double runGtPatterns(final Hospital model) {
		final long tick = System.nanoTime();

		final PatternsAPI api = new GtApp(model).initAPI();
		api.dayRoomTuple().findMatches();
		api.daySurgeonTuple().findMatches();
		api.dayOperatingTheaterTuple().findMatches();
		api.roomShiftTuple().findMatches();
		api.dayRoomGender().findMatches();
		Utilities.deleteFile(MODEL_PATH);

		final long tock = System.nanoTime();
		final double time = Utilities.tickTockToSeconds(tick, tock);
		System.out.println("GT PM runtime: " + time + "s.");
		api.terminate();
		return time;
	}

}
