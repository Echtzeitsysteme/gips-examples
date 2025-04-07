package teachingassistant.kcl.metamodelalt.generator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import metamodel.EmploymentApproval;
import metamodel.EmploymentRating;
import metamodel.Module;
import metamodel.SessionOccurrence;
import metamodel.TA;
import metamodel.TAAllocation;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import teachingassistant.kcl.metamodelalt.export.ModelToJsonExporter;

/**
 * Generator that creates a TAAllocation model aligned with the updated
 * metamodel:
 * 
 * <ul>
 * <li>TA has "name", "maxHoursPerWeek", "maxHoursPerYear".</li>
 * <li>Module has "name", "approvals", "sessions".</li>
 * <li>EmploymentApproval has "ta" and "rating" (RED, AMBER, GREEN).</li>
 * <li>TeachingSession has "name", "hoursPaidPerOccurrence", "numTAsPerSession",
 * plus contained "occurrences" and "entries".</li>
 * <li>SessionOccurrence has "timeTableWeek" and "tas".</li>
 * <li>TimeTableEntry has multi-valued "timeTableWeeks" (EList<Integer>),
 * "room", "weekDay", and Date-based "startTime", "endTime", plus reference
 * "session".</li>
 * <li>TAAllocation has "modules", "tas", and "timetable".</li>
 * </ul>
 */
public class SimpleTaKclGenerator extends TeachingAssistantKclGenerator {

	// Configuration
	static int NUMBER_OF_MODULES = 7;
	static int NUMBER_OF_TAS = 10;

	// TA constraints
	static int TA_MAXIMUM_HOURS_PER_WEEK = 20;
	static int TA_MAXIMUM_HOURS_PER_YEAR = 312;

	// Probability that a module has a second distinct session type
	static double PROBABILITY_SECOND_SESSION_TYPE = 0.4;

	// Possible session-activity labels
	static String[] SESSION_TYPES = { "Lab", "Tutorial", "Workshop", "Marking" };

	// Teaching weeks range
	static int START_WEEK = 23;
	static int END_WEEK = 26;

	// Possible daily start times (in hours)
	static int[] POSSIBLE_START_HOURS = { 9, 11, 13, 15 };

	/**
	 * Constructor taking a seed for random generation.
	 */
	public SimpleTaKclGenerator(int seed) {
		rand = new Random(seed);
	}

	public static void main(final String[] args) {
		final SimpleTaKclGenerator gen = new SimpleTaKclGenerator(0);
		final String instanceFolderPath = gen.prepareFolder();

		// Build the model
		final TAAllocation model = gen.constructModel();

		// Save as XMI
		try {
			save(model, instanceFolderPath + "/kcl_ta_allocation.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// Export to JSON (if desired)
		final ModelToJsonExporter exporter = new ModelToJsonExporter(model);
		exporter.modelToJson(instanceFolderPath + "/kcl_ta_allocation.json");

		System.out.println("=> Scenario generation finished.");
	}

	/**
	 * Constructs the full TAAllocation model instance. No @Override here because
	 * parent doesn't declare this as abstract.
	 */
	public TAAllocation constructModel() {
		// 1) Create the root TAAllocation
		this.root = factory.createTAAllocation();

		// 2) Create Modules
		final String[] moduleCodes = { "4CCS1FCOM", "4CCS1DBS", "4CCS1PRP", "5CCS2OS", "5CCS2SEG", "6CCS3ML",
				"6CCS3ALG" };
		final String[] moduleTitles = { "Foundations of Computing", "Database Systems", "Programming Practice",
				"Operating Systems", "Software Engineering", "Machine Learning", "Algorithms" };

		for (int i = 0; i < NUMBER_OF_MODULES; i++) {
			Module module = factory.createModule();
			// The new metamodel only has "name", so store code & title in that
			module.setName(moduleCodes[i] + " - " + moduleTitles[i]);
			modules.put(moduleCodes[i], module);
		}

		// 3) Create TAs
		for (int i = 0; i < NUMBER_OF_TAS; i++) {
			String taName = "TA_" + (i + 1);
			int maxWeeklyHours = getRandInt(6, TA_MAXIMUM_HOURS_PER_WEEK);

			TA ta = factory.createTA();
			ta.setName(taName);
			ta.setMaxHoursPerWeek(maxWeeklyHours);
			ta.setMaxHoursPerYear(TA_MAXIMUM_HOURS_PER_YEAR);

			tas.put(taName, ta);
		}

		// 4) Assign EmploymentApprovals
		for (Module module : modules.values()) {
			int numApplicants = getRandInt(3, Math.min(NUMBER_OF_TAS, 7));
			List<TA> shuffledTAs = new ArrayList<>(tas.values());
			java.util.Collections.shuffle(shuffledTAs, rand);

			List<TA> applicants = shuffledTAs.subList(0, numApplicants);
			int highestRatingNum = -1;

			for (TA ta : applicants) {
				// ratingVal: 0 => RED, 1 => AMBER, 2 => GREEN
				int ratingVal = getRandInt(0, 2);
				EmploymentRating rating;
				switch (ratingVal) {
				case 0:
					rating = EmploymentRating.RED;
					break;
				case 1:
					rating = EmploymentRating.AMBER;
					break;
				default:
					rating = EmploymentRating.GREEN;
					break;
				}

				highestRatingNum = Math.max(highestRatingNum, ratingVal);

				EmploymentApproval approval = factory.createEmploymentApproval();
				approval.setTa(ta);
				approval.setRating(rating);

				module.getApprovals().add(approval);
			}

			// Ensure at least one is > RED
			if (highestRatingNum < 1 && !module.getApprovals().isEmpty()) {
				module.getApprovals().get(0).setRating(EmploymentRating.GREEN);
			}
		}

		// 5) Create TeachingSessions + SessionOccurrences + TimeTableEntries
		LocalDateTime baseWeekMonday = LocalDateTime.of(2024, Month.FEBRUARY, 12, 0, 0);

		for (Module module : modules.values()) {
			// Possibly 1 or 2 session types
			int sessionTypeCount = (rand.nextDouble() < PROBABILITY_SECOND_SESSION_TYPE) ? 2 : 1;
			List<String> chosenTypes = new ArrayList<>();

			for (int i = 0; i < sessionTypeCount; i++) {
				String type;
				do {
					type = SESSION_TYPES[rand.nextInt(SESSION_TYPES.length)];
				} while (chosenTypes.contains(type));
				chosenTypes.add(type);
			}

			// Build each TeachingSession
			for (String typeName : chosenTypes) {
				TeachingSession session = factory.createTeachingSession();
				session.setName(module.getName() + "_" + typeName);

				// We'll treat "hoursPaidPerOccurrence" as the 'duration' in hours
				int durationHours = getRandInt(1, 2);
				session.setHoursPaidPerOccurrence(durationHours);

				// TAs needed
				int requiredTAs = (rand.nextDouble() < 0.7) ? 1 : 2;
				session.setNumTAsPerSession(requiredTAs);

				// Attach to module
				module.getSessions().add(session);

				// Decide how many occurrences
				int occurrenceCount;
				if (sessionTypeCount == 1) {
					occurrenceCount = getRandInt(4, 6);
				} else {
					if (typeName.equals(chosenTypes.get(0))) {
						occurrenceCount = getRandInt(3, 6);
					} else {
						occurrenceCount = getRandInt(1, 3);
					}
				}

				// Now create each occurrence
				for (int occIdx = 0; occIdx < occurrenceCount; occIdx++) {
					// 5.1) SessionOccurrence
					SessionOccurrence occ = factory.createSessionOccurrence();
					int chosenWeek = getRandInt(START_WEEK, END_WEEK);
					occ.setTimeTableWeek(chosenWeek);

					// Assign 1 or 2 TAs to this occurrence
					List<TA> allTAs = new ArrayList<>(tas.values());
					java.util.Collections.shuffle(allTAs, rand);
					int nTAsForOccurrence = getRandInt(1, 2);
					occ.getTas().addAll(allTAs.subList(0, nTAsForOccurrence));

					session.getOccurrences().add(occ);

					// 5.2) TimeTableEntry
					TimeTableEntry entry = factory.createTimeTableEntry();

					// TODO: Fix this
					entry.getTimeTableWeeks(); // .add(chosenWeek);

					entry.setRoom("Room" + getRandInt(1, 5));

					// Pick a day offset (0..4 => Mon..Fri)
					LocalDateTime dayTime = baseWeekMonday.plusDays((chosenWeek - START_WEEK) * 7 + getRandInt(0, 4));

					entry.setWeekDay(dayTime.getDayOfWeek().toString());

					// Convert LocalDateTime => Date
					Date startDate = Date.from(dayTime.atZone(ZoneId.systemDefault()).toInstant());
					Date endDate = Date
							.from(dayTime.plusHours(durationHours).atZone(ZoneId.systemDefault()).toInstant());

					entry.setStartTime(startDate);
					entry.setEndTime(endDate);

					// Link session <-> entry
					entry.setSession(session);

					// Finally, add this entry to root's timetable
					root.getTimetable().add(entry);
				}
			}
		}

		// 6) Add modules + TAs to root
		root.getModules().addAll(modules.values());
		root.getTas().addAll(tas.values());

		return root;
	}
}
