package org.emoflon.gips.ihtc.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.ihtc.runner.utils.XmiSetupUtil;

import ihtcgipssolution.api.gips.IhtcgipssolutionGipsAPI;
import ihtcmetamodel.Day;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.Occupant;
import ihtcmetamodel.Patient;
import ihtcmetamodel.Room;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.utils.FileUtils;

/**
 * TODO.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsDevRunner extends AbstractIhtcGipsRunner {

	/**
	 * If true, the runner will print more detailed information.
	 */
	private boolean verbose = true;

	/**
	 * Create a new instance of this class.
	 */
	public IhtcGipsDevRunner() {
	}

	/**
	 * Main method to execute the runner. Arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final IhtcGipsDevRunner runner = new IhtcGipsDevRunner();
		runner.setupDefaultPaths();
		runner.run();
	}

	/**
	 * Sets the default paths up.
	 */
	void setupDefaultPaths() {
		// Update output JSON file path
		this.datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/dev_runner/";
		this.outputPath = datasetSolutionFolder + "sol_"
				+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";
	}

	/**
	 * Sets the JSON input path.
	 * 
	 * @param jsonInputPath JSON input path.
	 */
	public void setJsonInputPath(final String jsonInputPath) {
		this.inputPath = jsonInputPath;
	}

	/**
	 * Sets the JSON output path.
	 * 
	 * @param jsonOutputPath JSON output path.
	 */
	public void setJsonOutputPath(final String jsonOutputPath) {
		this.outputPath = jsonOutputPath;
	}

	/**
	 * Sets the XMI input model path.
	 * 
	 * @param xmiInputModelPath XMI input model path.
	 */
	public void setXmiInputModelPath(final String xmiInputModelPath) {
		this.instancePath = xmiInputModelPath;
	}

	/**
	 * Sets the XMI output model path.
	 * 
	 * @param xmiOutputModelPath XMI output model path.
	 */
	public void setXmiOutputModelPath(final String xmiOutputModelPath) {
		this.gipsOutputPath = xmiOutputModelPath;
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	@Override
	public void run() {
		tick();
		final long tickStageOne = System.nanoTime();

		checkIfFileExists(inputPath);

		//
		// Convert JSON input file to XMI file
		//

		transformJsonToModel(inputPath, instancePath);

		//
		// Initialize GIPS API
		//

		if (verbose) {
			System.out.println("=> Start GIPS API.");
		}

		final IhtcgipssolutionGipsAPI gipsApi = new IhtcgipssolutionGipsAPI();
		XmiSetupUtil.checkIfEclipseOrJarSetup(gipsApi, instancePath);
		// Set presolve to "auto"
//		GurobiTuningUtil.updatePresolve(gipsApi, -1);
//		if (randomSeed != -1) {
//			GurobiTuningUtil.updateRandomSeed(gipsApi, randomSeed);
//		}
//
//		if (verbose) {
//			GurobiTuningUtil.setDebugOutput(gipsApi);
//		}

		//
		// Run GIPS solution
		//

		buildAndSolve(gipsApi, verbose);
		applySolution(gipsApi, verbose);
		gipsSave(gipsApi, gipsOutputPath);
		exportToJson(gipsOutputPath, outputPath);
		gipsApi.terminate();
		if (verbose) {
			System.out.println("=> GIPS found a solution.");
		}
		final long tockStageOne = System.nanoTime();
		final double stageOneRuntime = 1.0 * (tockStageOne - tickStageOne) / 1_000_000_000;
		if (verbose) {
			System.out.println("=> GIPS run time: " + stageOneRuntime + "s.");
		}

//		{
//			gipsApi.getNurseShiftRoomLoad().getMappings().forEach((n, m) -> {
//				System.out.println(n + ": " + m.getValueOfLoad());
//			});
//		}

//		{
//			final List<String> prints = new ArrayList<String>();
//			int sum = 0;
//			for (final String n : gipsApi.getRoomDayLoad().getMappings().keySet()) {
//				final RoomDayLoadMapping m = gipsApi.getRoomDayLoad().getMappings().get(n);
//				if (m.getValueOfLoad() > 0) {
////					System.out.println(
////							n + ": " + m.getValueOfLoad() + ", " + m.getValueOfMinAge() + ", " + m.getValueOfMaxAge());
//					prints.add(m.getR().getName() + "; " + m.getD().getName() + ": " + m.getValueOfLoad() + ", "
//							+ m.getValueOfMinAge() + ", " + m.getValueOfMaxAge());
//					sum += (m.getValueOfMaxAge() - m.getValueOfMinAge());
//				}
//			}
//			Collections.sort(prints);
//			prints.forEach(s -> System.out.println(s));
//			System.out.println("Sum : " + sum);
//
//			// Print max age diff in room and day
//			System.out.println("---");
//			gipsApi.getRoomDayLoad().getMappings().values().forEach(m -> {
//				if (Math.abs(m.getMaxAge().getValue() - m.getMinAge().getValue()) != 0) {
//					System.out.println(m.getR().getName() + " " + m.getD().getName() + ": maxAgeDiff = "
//							+ (m.getMaxAge().getValue() - m.getMinAge().getValue()));
//				}
//			});
//		}

//		{
//			final List<String> strings = new ArrayList<String>();
////			gipsApi.getRoomDayLoad().getMappings().forEach((n, m) -> {
////				if (m.getValueOfLoad() != 0) {
////					strings.add(m.getMatch().getR().getName() + " " + m.getMatch().getD().getName() + " load = "
////							+ m.getValueOfLoad());
////				}
////			});
//			gipsApi.getRoomDayPatientLoad().getMappings().forEach((n, m) -> {
//				if (m.getValue() != 0) {
//					strings.add(m.getMatch().getR().getName() + " " + m.getMatch().getD().getName() + " lpatient = "
//							+ m.getP().getName());
//				}
//			});
////			gipsApi.getOccupantRoomDayMapping().getMappings().forEach((n, m) -> {
////				if (m.getValue() != 0) {
////					strings.add(m.getMatch().getR().getName() + " " + m.getMatch().getD().getName() + " loccupant = "
////							+ m.getO().getName());
////				}
////			});
//			Collections.sort(strings, new Comparator<String>() {
//				public int compare(String o1, String o2) {
//					final int stringARoom = Integer.valueOf(o1.substring(o1.lastIndexOf("r") + 1, o1.indexOf(" ")));
//					final int stringBRoom = Integer.valueOf(o2.substring(o2.lastIndexOf("r") + 1, o2.indexOf(" ")));
//
//					if (stringARoom == stringBRoom) {
//						final int stringADay = Integer
//								.valueOf(o1.substring(o1.lastIndexOf("_") + 1, o1.lastIndexOf("l") - 1));
//						final int stringBDay = Integer
//								.valueOf(o2.substring(o2.lastIndexOf("_") + 1, o2.lastIndexOf("l") - 1));
//						return stringADay - stringBDay;
//					}
//
//					return stringARoom - stringBRoom;
//				}
//			});
//			System.out.println("==> Mapping values");
//			strings.forEach(System.out::println);
//			System.out.println("---");
//		}

		//
		// The end
		//

		tock();
		printWallClockRuntime();
	}

	/**
	 * Takes an XMI output path (of a GIPS-generated solution model) and writes the
	 * corresponding JSON output to `jsonOutputPath`.
	 * 
	 * @param xmiOutputPath  GIPS-generated solution model to convert.
	 * @param jsonOutputPath JSON output file location to write the JSON output file
	 *                       to.
	 */
	private void exportToJson(final String xmiOutputPath, final String jsonOutputPath) {
		final Resource loadedResource = FileUtils.loadModel(xmiOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);

//		// TODO: remove me: find age mixed rooms for debugging
//		final Map<Room, Map<Day, List<Patient>>> r2d2ps = new HashMap<Room, Map<Day, List<Patient>>>();
//		final Map<Room, Map<Day, List<Occupant>>> r2d2os = new HashMap<Room, Map<Day, List<Occupant>>>();
//		solvedHospital.getRooms().forEach(r -> {
//			final Map<Day, List<Patient>> newPatientMap = new HashMap<Day, List<Patient>>();
//			final Map<Day, List<Occupant>> newOccupantMap = new HashMap<Day, List<Occupant>>();
//			solvedHospital.getDays().forEach(d -> {
//				newPatientMap.put(d, new LinkedList<Patient>());
//				newOccupantMap.put(d, new LinkedList<Occupant>());
//			});
//			r2d2ps.put(r, newPatientMap);
//			r2d2os.put(r, newOccupantMap);
//		});
//		solvedHospital.getPatients().forEach(p -> {
//			if (p.getAssignedRoom() != null && p.getAdmissionDay() != null) {
//				final Room r = p.getAssignedRoom();
//				for (int i = p.getAdmissionDay().getId(); i < p.getLengthOfStay() + p.getAdmissionDay().getId(); i++) {
//					if (i >= solvedHospital.getDays().size()) {
//						break;
//					}
//					final Day d = getDayOfId(i, solvedHospital);
//					r2d2ps.get(r).get(d).add(p);
//				}
//			}
//		});
//		solvedHospital.getOccupants().forEach(o -> {
//			final Room r = getRoomById(o.getRoomId(), solvedHospital);
//			for (int i = 0; i < o.getLengthOfStay(); i++) {
//				if (i >= solvedHospital.getDays().size()) {
//					break;
//				}
//				final Day d = getDayOfId(i, solvedHospital);
//				r2d2os.get(r).get(d).add(o);
//			}
//
//		});
//
//		System.out.println("==> Model values");
//		for (final Room r : solvedHospital.getRooms()) {
//			final Map<Day, List<Patient>> day2Patients = r2d2ps.get(r);
////			for (final Day d : day2Patients.keySet()) {
////				for (final Patient p : day2Patients.get(d)) {
////					System.out.println(r.getName() + " " + d.getName() + " " + p.getName() + ", age "
////							+ p.getAgeGroup().getNumericAge());
////				}
////			}
//
//			final Map<Day, List<Occupant>> day2Occupants = r2d2os.get(r);
////			for (final Day d : day2Occupants.keySet()) {
////				for (final Occupant o : day2Occupants.get(d)) {
////					System.out.println(r.getName() + " " + d.getName() + " " + o.getName() + ", age "
////							+ o.getAgeGroup().getNumericAge());
////				}
////			}
//
//			for (final Day d : solvedHospital.getDays()) {
////				int load = 0;
////				load += day2Patients.get(d).size();
////				load += day2Occupants.get(d).size();
////				if (load != 0) {
////					System.out.println(r.getName() + " " + d.getName() + " load = " + load);
////				}
//				day2Patients.get(d).forEach(patient -> {
//					System.out.println(r.getName() + " " + d.getName() + " lpatient = " + patient.getName());
//				});
////				day2Occupants.get(d).forEach(occupant -> {
////					System.out.println(r.getName() + " " + d.getName() + " loccupant = " + occupant.getName());
////				});
//			}
//
//		}
//		// ---
//
//		// Find room day load (via the model)
////		solvedHospital.getRooms()
//		// ---

		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(jsonOutputPath);
	}

	private Day getDayOfId(final int id, final Hospital model) {
		for (final Day d : model.getDays()) {
			if (d.getId() == id) {
				return d;
			}
		}

		throw new InternalError(String.valueOf(id));
	}

	private Room getRoomById(final String id, final Hospital model) {
		for (final Room r : model.getRooms()) {
			if (r.getName() != null && r.getName().equals(id)) {
				return r;
			}
		}

		throw new InternalError(id);
	}

	/**
	 * Sets the verbose flag to the given value.
	 * 
	 * @param verbose Verbose flag.
	 */
	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

}
