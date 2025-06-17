package teachingassistant.kcl.metamodelalt.generator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import metamodel.EmploymentApproval;
import metamodel.EmploymentRating;
import metamodel.Module;
import metamodel.SessionOccurrence;
import metamodel.TaAllocation;
import metamodel.TeachingAssistant;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import metamodel.Week;
import teachingassistant.kcl.gips.utils.DateTimeUtil;
import teachingassistant.kcl.gips.utils.LoggingUtils;
import teachingassistant.kcl.metamodelalt.export.ModelToJsonExporter;

/**
 * Generator that creates a TaAllocation model aligned with the updated
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
 * <li>TaAllocation has "modules", "tas", and "timetable".</li>
 * </ul>
 */
public class SimpleTaKclGenerator extends TeachingAssistantKclGenerator {

	/**
	 * Logger for system outputs.
	 */
	protected final static Logger logger = Logger.getLogger(SimpleTaKclGenerator.class.getName());

	// Configuration
	static int NUMBER_OF_MODULES = 7;
	static int NUMBER_OF_TAS = 20;

	// TA constraints
	public static int TA_MAXIMUM_HOURS_PER_WEEK = 20;
	public static int TA_MAXIMUM_HOURS_PER_YEAR = 312;

	// Probability that a module has a second distinct session type
	static double PROBABILITY_SECOND_SESSION_TYPE = 0.4;

	// Possible session-activity labels
	static String[] SESSION_TYPES = { "Lab", "Tutorial", "Workshop", "Marking" };

	// Teaching weeks range
	static int START_WEEK = 23;
	static int END_WEEK = 26;

//	// Possible daily start times (in hours)
//	static int[] POSSIBLE_START_HOURS = { 9, 11, 13, 15 };

	// Bounds for the randomized creation of blocked time table entries for each TA
	static int MIN_NUMBER_TA_BLOCKED = 0;
	static int MAX_NUMBER_TA_BLOCKED = 2;

	/**
	 * Constructor taking a seed for random generation.
	 */
	public SimpleTaKclGenerator(int seed) {
		LoggingUtils.configureLogging(logger);
		rand = new Random(seed);
	}

	public static void main(final String[] args) {
		final SimpleTaKclGenerator gen = new SimpleTaKclGenerator(0);
		final String instanceFolderPath = gen.prepareFolder();

		// Build the model
		final TaAllocation model = gen.constructModel();

		// Save as XMI
		try {
			save(model, instanceFolderPath + "/kcl_ta_allocation.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// Export to JSON (if desired)
		final ModelToJsonExporter exporter = new ModelToJsonExporter(model);
		exporter.modelToJson(instanceFolderPath + "/kcl_ta_allocation.json");

		logger.info("=> Scenario generation finished.");
	}

	/**
	 * Constructs the full TaAllocation model instance. No @Override here because
	 * parent doesn't declare this as abstract.
	 */
	public TaAllocation constructModel() {
		// 1) Create the root TaAllocation
		this.root = factory.createTaAllocation();

		// 1.1) Create all weeks
		for (int i = START_WEEK; i <= END_WEEK; i++) {
			final Week week = factory.createWeek();
			week.setNumber(i);
			this.root.getWeeks().add(week);
		}

		// 2) Create Modules
		final String[] moduleCodes = { "4CCS1FCOM", "4CCS1DBS", "4CCS1PRP", "5CCS2OS", "5CCS2SEG", "6CCS3ML",
				"6CCS3ALG" };
		final String[] moduleTitles = { "Foundations of Computing", "Database Systems", "Programming Practice",
				"Operating Systems", "Software Engineering", "Machine Learning", "Algorithms" };

		for (int i = 0; i < NUMBER_OF_MODULES; i++) {
			final Module module = factory.createModule();
			// The new metamodel only has "name", so store code & title in that
			// TODO: Maybe we can extend the metamodel to hold these values in different
			// fields?
			// TODO: The following array accesses result in an IndexOutOfBoundsException if
			// more than 7 modules are configured above.
			module.setName(moduleCodes[i] + " - " + moduleTitles[i]);
			modules.put(moduleCodes[i], module);
		}

		// 3) Create TAs
		for (int i = 0; i < NUMBER_OF_TAS; i++) {
			final String taName = "TA_" + (i + 1);
			// TODO: The following constant `6` should probably be moved to a constant field
			// of this class.
			final int maxWeeklyHours = getRandInt(6, TA_MAXIMUM_HOURS_PER_WEEK);

			final TeachingAssistant ta = factory.createTeachingAssistant();
			ta.setName(taName);
			ta.setMaxHoursPerWeek(maxWeeklyHours);
			ta.setMaxHoursPerYear(TA_MAXIMUM_HOURS_PER_YEAR);

			tas.put(taName, ta);
		}

		// 4) Assign EmploymentApprovals
		for (final Module module : modules.values()) {
			final int numApplicants = getRandInt(3, Math.min(NUMBER_OF_TAS, 7));
			final List<TeachingAssistant> shuffledTAs = new ArrayList<>(tas.values());
			java.util.Collections.shuffle(shuffledTAs, rand);

			final List<TeachingAssistant> applicants = shuffledTAs.subList(0, numApplicants);
			int highestRatingNum = -1;

			for (final TeachingAssistant ta : applicants) {
				// ratingVal: 0 => RED, 1 => AMBER, 2 => GREEN
				final int ratingVal = getRandInt(0, 2);
				final EmploymentRating rating = convertRating(ratingVal);

				highestRatingNum = Math.max(highestRatingNum, ratingVal);

				final EmploymentApproval approval = factory.createEmploymentApproval();
				approval.setTa(ta);
				approval.setRating(rating);
				approval.setRatingNumeric(ratingVal);

				module.getApprovals().add(approval);
			}

			// Ensure at least one is > RED
			if (highestRatingNum < 1 && !module.getApprovals().isEmpty()) {
				module.getApprovals().get(0).setRating(EmploymentRating.GREEN);
			}
		}

		// TODO: The following constants should also be moved to the class fields.
		// 5) Create TeachingSessions + SessionOccurrences + TimeTableEntries
		final LocalDateTime baseWeekMonday = LocalDateTime.of(2024, Month.FEBRUARY, 12, 0, 0);

		for (final Module module : modules.values()) {
			// Possibly 1 or 2 session types
			int sessionTypeCount = (rand.nextDouble() < PROBABILITY_SECOND_SESSION_TYPE) ? 2 : 1;
			final List<String> chosenTypes = new ArrayList<>();

			for (int i = 0; i < sessionTypeCount; i++) {
				String type;
				do {
					type = SESSION_TYPES[rand.nextInt(SESSION_TYPES.length)];
				} while (chosenTypes.contains(type));
				chosenTypes.add(type);
			}

			// Build each TeachingSession
			for (final String typeName : chosenTypes) {
				final TeachingSession session = factory.createTeachingSession();
				session.setName(module.getName() + "_" + typeName);

				// We'll treat "hoursPaidPerOccurrence" as the 'duration' in hours
				final int durationHours = getRandInt(1, 2);
				session.setHoursPaidPerOccurrence(durationHours);

				// TODO: Move constants to the class.
				// TAs needed
				final int requiredTAs = (rand.nextDouble() < 0.7) ? 1 : 2;
				session.setNumTasPerSession(requiredTAs);

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

				// If the number of occurrences is > number of weeks, limit it
				final int numberOfWeeks = END_WEEK - START_WEEK + 1;
				if (occurrenceCount > numberOfWeeks) {
					occurrenceCount = numberOfWeeks;
				}

				// Now create each occurrence
				final Set<Integer> alreadyUsedWeeks = new HashSet<Integer>();
				for (int occIdx = 0; occIdx < occurrenceCount; occIdx++) {
					// 5.1) SessionOccurrence
					final SessionOccurrence occ = factory.createSessionOccurrence();
					final int chosenWeek = getRandIntWithBlocklist(START_WEEK, END_WEEK, alreadyUsedWeeks);
					alreadyUsedWeeks.add(chosenWeek);
					occ.setTimeTableWeek(chosenWeek);
					occ.setName(module.getName() + " " + session.getName() + " " + occIdx);

					// TODO: I've commented out this code because the assignment of TAs to
					// occurrences should be done via an external algorithm (e.g., GIPS-based or
					// something else).
//					// Assign 1 or 2 TAs to this occurrence
//					final List<TA> allTAs = new ArrayList<>(tas.values());
//					java.util.Collections.shuffle(allTAs, rand);
//					final int nTAsForOccurrence = getRandInt(1, 2);
//					occ.getTas().addAll(allTAs.subList(0, nTAsForOccurrence));

					session.getOccurrences().add(occ);

					// 5.2) TimeTableEntry
					final TimeTableEntry entry = factory.createTimeTableEntry();

					entry.getTimeTableWeeks().add(getWeek(chosenWeek));
					session.getTimeTableWeeks().add(getWeek(chosenWeek));

					entry.setRoom("Room" + getRandInt(1, 5));

					// Pick a day offset (0..4 => Mon..Fri)
					final LocalDateTime dayTime = baseWeekMonday
							.plusDays((chosenWeek - START_WEEK) * 7 + getRandInt(0, 4));

					entry.setWeekDay(dayTime.getDayOfWeek().toString());

					// Convert LocalDateTime => Date
					final Date startDate = Date.from(dayTime.atZone(ZoneId.systemDefault()).toInstant());
					final Date endDate = Date
							.from(dayTime.plusHours(durationHours).atZone(ZoneId.systemDefault()).toInstant());

					entry.setStartTime(startDate);
					entry.setEndTime(endDate);
					entry.setStartEpoch(DateTimeUtil.convertDateTimeToSeconds(startDate));
					entry.setEndEpoch(DateTimeUtil.convertDateTimeToSeconds(endDate));

					// Link session <-> entry
					entry.setSession(session);

					// Finally, add this entry to root's timetable
					root.getTimetable().add(entry);
				}
			}
		}

		// 5.3) Add (randomized) blocked time table entries to the TAs
		for (final TeachingAssistant ta : tas.values()) {
			final int numberOfBlockedDates = getRandInt(MIN_NUMBER_TA_BLOCKED, MAX_NUMBER_TA_BLOCKED);
			final List<TimeTableEntry> copiedTtes = new ArrayList<TimeTableEntry>();
			copiedTtes.addAll(root.getTimetable());

			for (int i = 0; i < numberOfBlockedDates; i++) {
				if (copiedTtes.isEmpty()) {
					break;
				}
				final int randomTteIndex = getRandInt(0, copiedTtes.size() - 1);
				ta.getUnavailableBecauseLessons().add(copiedTtes.remove(randomTteIndex));
			}
		}

		// 6) Add modules + TAs to root
		root.getModules().addAll(modules.values());
		root.getTas().addAll(tas.values());

		return root;
	}

	private Week getWeek(final int weekNumber) {
		for (final Week itW : this.root.getWeeks()) {
			if (itW.getNumber() == weekNumber) {
				return itW;
			}
		}
		throw new IllegalArgumentException("Week with number " + weekNumber + " not found in the model.");
	}

	private EmploymentRating convertRating(final int rating) {
		switch (rating) {
		case 0:
			return EmploymentRating.RED;
		case 1:
			return EmploymentRating.AMBER;
		case 2:
			return EmploymentRating.GREEN;
		default:
			throw new IllegalArgumentException("Given rating " + rating + " is not valid.");
		}
	}

}
