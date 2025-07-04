package teachingassistant.uni.metamodel.validator;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.EmploymentApproval;
import metamodel.Module;
import metamodel.NamedElement;
import metamodel.SessionOccurrence;
import metamodel.TaAllocation;
import metamodel.TeachingAssistant;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import metamodel.Week;
import teachingassistant.uni.utils.DateTimeUtil;
import teachingassistant.uni.utils.LoggingUtils;
import teachingassistant.uni.metamodel.export.FileUtils;

/**
 * Model validator for the teaching assistant example.
 */
public class TeachingAssistantUniValidator {

	/**
	 * Logger for system outputs.
	 */
	protected final static Logger logger = Logger.getLogger(TeachingAssistantUniValidator.class.getName());

	/**
	 * Model file name to load.
	 */
	public final static String SCENARIO_FILE_NAME = "solved.xmi";
//	public final static String SCENARIO_FILE_NAME = "uni_ta_allocation.xmi";

	/**
	 * If true, the validator will output more detailed information for violated
	 * rules.
	 */
	public static boolean verbose = true;

	/**
	 * If true, the validator will check all relevant constraints regarding
	 * assignments. For example: TA hour limits, session occurrences and their
	 * requested number of TAs, etc.
	 */
	public static boolean checkForValidSolution = true;

	/**
	 * Main method to run the stand-alone model validation.
	 * 
	 * @param args Arguments that will be ignored.
	 */
	public static void main(final String[] args) {
		LoggingUtils.configureLogging(logger);

		// Construct file path
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances/";
		final String filePath = instanceFolder + SCENARIO_FILE_NAME;

		// Load model
		final Resource r = FileUtils.loadModel(filePath);
		final TaAllocation model = (TaAllocation) r.getContents().get(0);

		// Validate
		final boolean valid = new TeachingAssistantUniValidator().validate(model);

		if (valid) {
			logger.info("Result: Model is valid.");
		} else {
			logger.warning("Result: Model is not valid.");
		}
	}

	//
	// Validation methods.
	//

	/**
	 * Validate the given TaAllocation object.
	 * 
	 * @param model TaAllocation object to validate.
	 * @return True if complete model is valid.
	 */
	public boolean validate(final TaAllocation model) {
		if (model == null) {
			logger.warning("=> Given model was null.");
			return false;
		}

		boolean valid = true;

		// Weeks
		{
			// Collect all weeks
			final Set<Week> allFoundWeeks = new HashSet<>();
			allFoundWeeks.addAll(model.getWeeks());
			model.getTimetable().forEach(timeTableEntry -> {
				allFoundWeeks.addAll(timeTableEntry.getTimeTableWeeks());
			});
			model.getModules().forEach(module -> {
				module.getSessions().forEach(session -> {
					allFoundWeeks.addAll(session.getTimeTableWeeks());
				});
			});
			boolean weeksValid = true;
			for (final Week week : allFoundWeeks) {
				weeksValid = weeksValid & validate(week);
			}
			weeksValid = weeksValid & validateWeekNumberUnique(allFoundWeeks);
			logger.info("=> All weeks are valid: " + weeksValid);
			valid = valid & weeksValid;
		}

		// TimeTableEntries
		{
			// Collect all TimeTableEntries
			final Set<TimeTableEntry> allFoundTimeTableEntries = new HashSet<>();
			allFoundTimeTableEntries.addAll(model.getTimetable());
			model.getModules().forEach(module -> {
				module.getSessions().forEach(session -> {
					allFoundTimeTableEntries.addAll(session.getEntries());
				});
			});
			model.getTas().forEach(ta -> {
				allFoundTimeTableEntries.addAll(ta.getUnavailableBecauseLessons());
			});
			boolean timeTableEntriesValid = true;
			for (final TimeTableEntry entry : allFoundTimeTableEntries) {
				timeTableEntriesValid = timeTableEntriesValid & validate(entry);
			}
			logger.info("=> All time table entries are valid: " + timeTableEntriesValid);
			valid = valid & timeTableEntriesValid;
		}

		// TAs
		{
			boolean tasValid = true;
			for (final TeachingAssistant ta : model.getTas()) {
				tasValid = tasValid & validate(ta, model);
			}
			logger.info("=> All TAs are valid: " + tasValid);
			valid = valid & tasValid;
		}

		// Modules
		{
			boolean modulesValid = true;
			for (final metamodel.Module m : model.getModules()) {
				modulesValid = modulesValid & validate(m, model);
			}
			logger.info("=> All modules are valid: " + modulesValid);
			valid = valid & modulesValid;
		}

		// EmploymentApprovals
		{
			// Collect all employment approvals
			final Set<EmploymentApproval> allFoundEmploymentApprovals = new HashSet<>();
			model.getModules().forEach(module -> {
				allFoundEmploymentApprovals.addAll(module.getApprovals());
			});
			boolean employmentApprovalsValid = true;
			for (final EmploymentApproval ea : allFoundEmploymentApprovals) {
				employmentApprovalsValid = employmentApprovalsValid & validate(ea);
				employmentApprovalsValid = employmentApprovalsValid & model.getTas().contains(ea.getTa());
			}
			logger.info("=> All employment approvals are valid: " + employmentApprovalsValid);
			valid = valid & employmentApprovalsValid;
		}

		// SessionOccurrences
		{
			boolean sessionOccurrencesValid = true;
			for (final Module module : model.getModules()) {
				for (final TeachingSession session : module.getSessions()) {
					for (final SessionOccurrence so : session.getOccurrences()) {
						sessionOccurrencesValid = sessionOccurrencesValid & validate(so, model, session);
					}
				}
			}
			logger.info("=> All session occurrences are valid: " + sessionOccurrencesValid);
			valid = valid & sessionOccurrencesValid;
		}

		// TeachingSessions
		{
			// Collect all teaching sessions
			final Set<TeachingSession> allFoundTeachingSessions = new HashSet<>();
			model.getModules().forEach(module -> {
				allFoundTeachingSessions.addAll(module.getSessions());
			});
			boolean teachingSessionsValid = true;
			for (final TeachingSession ts : allFoundTeachingSessions) {
				teachingSessionsValid = teachingSessionsValid & validate(ts, model);
			}
			logger.info("=> All teaching sessions are valid: " + teachingSessionsValid);
			valid = valid & teachingSessionsValid;
		}

		return valid;
	}

	/**
	 * Validate a given module `m` in the context of the complete model.
	 * 
	 * @param m     Module to validate.
	 * @param model Complete model.
	 * @return True if the given module `m` was valid.
	 */
	private boolean validate(final Module m, final TaAllocation model) {
		if (m == null) {
			return false;
		}

		if (m.getApprovals().isEmpty()) {
			return false;
		}

		for (final EmploymentApproval ea : m.getApprovals()) {
			if (!model.getTas().contains(ea.getTa())) {
				return false;
			}
		}

		if (m.getSessions().isEmpty()) {
			return false;
		}

		return true;
	}

	/**
	 * Validate a given teaching session `ts` in the context of the complete model.
	 * 
	 * @param ts    Teaching session to validate.
	 * @param model Complete model.
	 * @return True if the given teaching session `ts` was valid.
	 */
	private boolean validate(final TeachingSession ts, final TaAllocation model) {
		if (ts == null) {
			return false;
		}

		if (ts.getHoursPaidPerOccurrence() < 0) {
			return false;
		}

		if (ts.getNumTasPerSession() < 1) {
			return false;
		}

		if (ts.getOccurrences().isEmpty()) {
			return false;
		}

		if (ts.getEntries().isEmpty()) {
			return false;
		}

		if (ts.getTimeTableWeeks().isEmpty()) {
			return false;
		}

		for (final Week w : ts.getTimeTableWeeks()) {
			if (!model.getWeeks().contains(w)) {
				return false;
			}
		}

		for (final TimeTableEntry tte : ts.getEntries()) {
			for (final Week w : tte.getTimeTableWeeks()) {
				if (!ts.getTimeTableWeeks().contains(w)) {
					return false;
				}
			}
		}

		for (final SessionOccurrence se : ts.getOccurrences()) {
			boolean contained = false;
			for (final Week w : ts.getTimeTableWeeks()) {
				if (w.getNumber() == se.getTimeTableWeek()) {
					contained = true;
					break;
				}
			}
			if (!contained) {
				logVerbose(ts, "Was not contained in a time table week.");
				return false;
			}
		}

		// "Every teaching session will only have one session occurrence in any given
		// timetable week."
		for (final Week w : ts.getTimeTableWeeks()) {
			int matchingOccurrences = 0;
			for (final SessionOccurrence so : ts.getOccurrences()) {
				if (so.getTimeTableWeek() == w.getNumber()) {
					matchingOccurrences++;
				}
			}

			if (matchingOccurrences != 1) {
				logVerbose(ts, "Did not have exactly one session occurence in time table week " + w.getNumber() + ".");
				return false;
			}
		}

		// "Equally, for any given timetable week, the teaching session will have at
		// most one associated TimeTableEntry."
		for (final Week w : ts.getTimeTableWeeks()) {
			int matchingOccurences = 0;
			for (final TimeTableEntry tte : ts.getEntries()) {
				for (final Week tteWeek : tte.getTimeTableWeeks()) {
					if (tteWeek.getNumber() == w.getNumber()) {
						matchingOccurences++;
					}
				}
			}

			if (matchingOccurences > 1) {
				logVerbose(ts, "Did not have at most one associated time table entry week " + w.getNumber() + ".");
				return false;
			}
		}

		// All `TimeTableEntry` objects must have the correct `TeachingSession` assigned
		for (final TimeTableEntry tte : ts.getEntries()) {
			if (tte.getSession() == null || !tte.getSession().equals(ts)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Validate a given session occurrence `so` in the context of the complete
	 * model.
	 * 
	 * @param so      Session occurrence to validate.
	 * @param model   Complete model.
	 * @param session Teaching session this occurrence belongs to.
	 * @return True if the given session occurrence `so` was valid.
	 */
	private boolean validate(final SessionOccurrence so, final TaAllocation model, final TeachingSession session) {
		if (so == null) {
			return false;
		}

		if (so.getTimeTableWeek() < getFirstWeekNumber(model.getWeeks())) {
			return false;
		}

		if (so.getTimeTableWeek() > getLastWeekNumber(model.getWeeks())) {
			return false;
		}

		if (checkForValidSolution && so.getTas().isEmpty()) {
			return false;
		}

		if (checkForValidSolution && session.getNumTasPerSession() != so.getTas().size()) {
			logger.warning(
					"Session occurrence " + so.getName() + " did not get the right amount of TAs assigned. Requested: "
							+ session.getNumTasPerSession() + "; assigned: " + so.getTas().size());
			return false;
		}

		for (final TeachingAssistant ta : so.getTas()) {
			if (!model.getTas().contains(ta)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Validates the given employment approval object.
	 * 
	 * @param ea Employment approval object to validate.
	 * @return True if the given object was valid.
	 */
	private boolean validate(final EmploymentApproval ea) {
		if (ea == null) {
			return false;
		}

		if (ea.getRating() == null) {
			return false;
		}

		if (ea.getTa() == null) {
			return false;
		}

		return true;
	}

	/**
	 * Validate a given teaching assistant `ta` in the context of the complete
	 * model.
	 * 
	 * @param ta    Teaching assistant to validate.
	 * @param model Complete model.
	 * @return True if the given teaching assistant `ta` was valid.
	 */
	private boolean validate(final TeachingAssistant ta, final TaAllocation model) {
		if (ta == null) {
			return false;
		}

		if (!validateName(ta)) {
			return false;
		}

		if (ta.getMaxHoursPerWeek() <= 0) {
			return false;
		}

		if (ta.getMaxHoursPerYear() <= 0) {
			return false;
		}

		// Unavailable sessions
		final Set<TimeTableEntry> allShifts = findAllShiftsOfTa(ta, model);
		for (final TimeTableEntry unavailable : ta.getUnavailableBecauseLessons()) {
			if (allShifts.contains(unavailable)) {
				if (verbose) {
					logger.warning("TA <" + ta.getName() + "> did get a session on time table entry <" + unavailable
							+ "> but is blocked on this time frame.");
				}
				return false;
			}
		}

		// Check session approvals
		for (final TimeTableEntry shift : allShifts) {
			final Module module = findModuleOfSession(shift.getSession(), model);
			boolean taApproved = false;
			for (final EmploymentApproval approval : module.getApprovals()) {
				if (approval.getTa().equals(ta)) {
					taApproved = true;
					break;
				}
			}
			if (!taApproved) {
				if (verbose) {
					logger.warning("TA <" + ta.getName() + "> not approved for module <" + module.getName()
							+ "> but was assigned.");
				}
				return false;
			}
		}

		// Check time limit per week
		for (final Week w : model.getWeeks()) {
			final Set<TimeTableEntry> weekShifts = findAllShiftsOfTaInWeek(ta, w.getNumber(), model);
			int hoursPaidInWeek = 0;
			for (final TimeTableEntry tte : weekShifts) {
				hoursPaidInWeek += tte.getSession().getHoursPaidPerOccurrence();
			}
			if (hoursPaidInWeek > ta.getMaxHoursPerWeek()) {
				if (verbose) {
					logger.warning("TA <" + ta.getName() + "> time limit violated in week <" + w.getNumber() + ">.");
				}
				return false;
			}
		}

		// Check time limit per year
		int totalHoursPaid = 0;
		for (final TimeTableEntry tte : allShifts) {
			totalHoursPaid += tte.getSession().getHoursPaidPerOccurrence();
		}
		if (totalHoursPaid > ta.getMaxHoursPerYear()) {
			if (verbose) {
				logger.warning("TA <" + ta.getName() + "> total time limit violated.");
			}
			return false;
		}

		// Check for conflicting assignments per TA
		if (checkForConflicts(allShifts)) {
			if (verbose) {
				logger.warning("TA <" + ta.getName() + "> has conflicting assignments.");
			}
			return false;
		}

		return true;
	}

	/**
	 * Finds and returns the module of a given session.
	 * 
	 * @param session Teaching session to find the module for.
	 * @param model   The complete model
	 * @return Module of a given teaching session.
	 */
	private Module findModuleOfSession(final TeachingSession session, final TaAllocation model) {
		for (final Module module : model.getModules()) {
			if (module.getSessions().contains(session)) {
				return module;
			}
		}

		throw new UnsupportedOperationException("Teaching session <" + session + "> not found in the given model.");
	}

	/**
	 * Validates the name of a given `NamedElement`.
	 * 
	 * @param element `NamedElement` to validate the name for.
	 * @return True if the name is valid.
	 */
	private boolean validateName(final NamedElement element) {
		if (element == null) {
			return false;
		}

		if (element.getName() == null) {
			return false;
		}

		if (element.getName().isBlank()) {
			return false;
		}

		return true;
	}

	/**
	 * Checks the given collection of weeks for unique numbers.
	 * 
	 * @param weeks Collection of weeks to check.
	 * @return True if all week numbers were unique.
	 */
	private boolean validateWeekNumberUnique(final Collection<Week> weeks) {
		if (weeks == null) {
			return false;
		}

		final Set<Integer> names = new HashSet<>();
		for (final Week w : weeks) {
			if (!names.add(w.getNumber())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Validates a given `Week` object.
	 * 
	 * @param week `Week` object to validate.
	 * @return True if the given `Week` object is valid.
	 */
	private boolean validate(final Week week) {
		if (week == null) {
			return false;
		}

		// Number of the week must be between 1 and 52
		if (week.getNumber() < 0 || week.getNumber() > 52) {
			return false;
		}

		return true;
	}

	/**
	 * Validates a given `TimeTableEntry` object.
	 * 
	 * @param entry `TimeTableEntry` object to validate.
	 * @return True if the given `TimeTableEntry` object is valid.
	 */
	private boolean validate(final TimeTableEntry entry) {
		if (entry == null) {
			return false;
		}

		if (entry.getRoom() == null || entry.getRoom().isBlank()) {
			return false;
		}

		if (entry.getWeekDay() == null || entry.getWeekDay().isBlank()) {
			return false;
		}

		if (entry.getStartTime() == null) {
			return false;
		}

		if (entry.getEndTime() == null) {
			return false;
		}

		if (entry.getStartEpoch() < 0) {
			return false;
		}

		if (entry.getEndEpoch() < 0) {
			return false;
		}

		if (entry.getSession() == null) {
			return false;
		}

		// TeachingSession
		if (!entry.getSession().getEntries().contains(entry)) {
			return false;
		}

		// timeTableWeeks
		// all timeTableWeeks of the entry must be part of the session's timeTableWeeks
		if (!entry.getSession().getTimeTableWeeks().containsAll(entry.getTimeTableWeeks())) {
			return false;
		}

		// startTime < endTime is required
		// We ignore the date and only check for time
		if (!startBeforeEndTimeOnly(entry.getStartTime(), entry.getEndTime())) {
			return false;
		}

		// startEpoch < endEpoch is required
		if (!(entry.getStartEpoch() < entry.getEndEpoch())) {
			return false;
		}

		return true;
	}

	//
	// Utility methods.
	//

	/**
	 * Checks the given set of `TimeTableEntry` objects for conflicting time frames.
	 * 
	 * @param entries Set of `TimeTableEntry` objects to check for conflicting time
	 *                frames.
	 * @return If true, there are at least two overlapping time frames in the given
	 *         set of `TimeTableEntry` objects.
	 */
	private boolean checkForConflicts(final Set<TimeTableEntry> entries) {
		for (final TimeTableEntry tte : entries) {
			for (final TimeTableEntry other : entries) {
				// Skip check if `tte` == `other`
				if (tte.equals(other)) {
					continue;
				}

				// Find overlapping weeks
				final Set<Week> overlappingWeeks = new HashSet<Week>();
				tte.getTimeTableWeeks().forEach(w -> {
					if (other.getTimeTableWeeks().contains(w)) {
						overlappingWeeks.add(w);
					}
				});

				// If the weekday does not match, we do not have to check for an overlapping
				// time frame
				if (tte.getWeekDay() != null && !tte.getWeekDay().equals(other.getWeekDay())) {
					continue;
				}

				// If there is at least one overlapping week check if the time frames overlaps
				if (!overlappingWeeks.isEmpty()) {
					final int firstStart = DateTimeUtil.convertDateTimeToSeconds(tte.getStartTime());
					final int firstEnd = DateTimeUtil.convertDateTimeToSeconds(tte.getEndTime());
					final int secondStart = DateTimeUtil.convertDateTimeToSeconds(other.getStartTime());
					final int secondEnd = DateTimeUtil.convertDateTimeToSeconds(other.getEndTime());
					if ((firstStart < secondEnd && firstEnd > secondStart)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Compares the two given Date objects and returns true if the time of the start
	 * object lays before the time of the end object. This method ignores the dates
	 * completely, i.e., it only relies on the hours, minutes, and seconds.
	 * 
	 * @param start Date object that represents the start.
	 * @param end   Date object that represents the end.
	 * @return True if start.time < end.time (not respecting the date).
	 */
	private boolean startBeforeEndTimeOnly(final Date start, final Date end) {
		final Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		final Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);

		if (startCal.get(Calendar.HOUR_OF_DAY) > endCal.get(Calendar.HOUR_OF_DAY)) {
			return false;
		} else if (startCal.get(Calendar.HOUR_OF_DAY) == endCal.get(Calendar.HOUR_OF_DAY)) {
			if (startCal.get(Calendar.MINUTE) > endCal.get(Calendar.MINUTE)) {
				return false;
			} else if (startCal.get(Calendar.MINUTE) == endCal.get(Calendar.MINUTE)) {
				if (startCal.get(Calendar.SECOND) > endCal.get(Calendar.SECOND)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns the number of the earliest week found in the collection of weeks
	 * given.
	 * 
	 * @param weeks Collection of weeks to search in.
	 * @return Number of the earliest week in the given collection.
	 */
	private int getFirstWeekNumber(final Collection<Week> weeks) {
		int firstWeek = Integer.MAX_VALUE;

		for (final Week w : weeks) {
			if (w.getNumber() < firstWeek) {
				firstWeek = w.getNumber();
			}
		}

		return firstWeek;
	}

	/**
	 * Returns the number of the latest week found in the collection of weeks given.
	 * 
	 * @param weeks Collection of weeks to search in.
	 * @return Number of the latest week in the given collection.
	 */
	private int getLastWeekNumber(final Collection<Week> weeks) {
		int lastWeek = Integer.MIN_VALUE;

		for (final Week w : weeks) {
			if (w.getNumber() > lastWeek) {
				lastWeek = w.getNumber();
			}
		}

		return lastWeek;
	}

	/**
	 * Finds all shifts of a given TA within the given model (i.e., for the complete
	 * duration of the model).
	 * 
	 * @param ta    Teaching assistant to gather all shifts for.
	 * @param model Model to gather all shifts from.
	 * @return Set of `TimeTableEntry` that represent the assigned shifts of the
	 *         given TA.
	 */
	private Set<TimeTableEntry> findAllShiftsOfTa(final TeachingAssistant ta, final TaAllocation model) {
		final Set<TimeTableEntry> shifts = new HashSet<>();
		model.getModules().forEach(module -> {
			// Removed on purpose to also find assigned shifts that are not approved.
			// This is necessary for another check that tests if a TA was assigned to a
			// module that they have not been approved for.
//			boolean assigned = false;
//			for (final EmploymentApproval ea : module.getApprovals()) {
//				if (ea.getTa() != null && ea.getTa().equals(ta)) {
//					assigned = true;
//					break;
//				}
//			}
//
//			// If the TA is not assigned in any approval, skip remaining checks.
//			if (!assigned) {
//				return;
//			}

			module.getSessions().forEach(session -> {
				session.getOccurrences().forEach(o -> {
					if (o.getTas().contains(ta)) {
						// Add all TimeTableEntry objects that contain the timeTableWeek of the
						// SessenOccurrence
						final Set<TimeTableEntry> matchingEntries = new HashSet<>();
						session.getEntries().forEach(tte -> {
							for (final Week w : tte.getTimeTableWeeks()) {
								if (w.getNumber() == o.getTimeTableWeek()) {
									matchingEntries.add(tte);
									break;
								}
							}
						});
						shifts.addAll(matchingEntries);
					}
				});
			});
		});
		return shifts;
	}

	/**
	 * Finds all shifts of a given TA within the given model for a specific week.
	 * 
	 * @param ta    Teaching assistant to gather all shifts for.
	 * @param week  Week to filter for.
	 * @param model Model to gather all shifts from.
	 * @return Set of `TimeTableEntry` that represent the assigned shifts of the
	 *         given TA.
	 */
	private Set<TimeTableEntry> findAllShiftsOfTaInWeek(final TeachingAssistant ta, final int week,
			final TaAllocation model) {
		final Set<TimeTableEntry> shifts = new HashSet<>();
		model.getModules().forEach(module -> {
			// Disabled on purpose to find all shifts of the given TA in the given week
			// regardless of the approvals.
//			boolean assigned = false;
//			for (final EmploymentApproval ea : module.getApprovals()) {
//				if (ea.getTa() != null && ea.getTa().equals(ta)) {
//					assigned = true;
//					break;
//				}
//			}
//
//			// If the TA is not assigned in any approval, skip remaining checks.
//			if (!assigned) {
//				return;
//			}

			module.getSessions().forEach(session -> {
				session.getOccurrences().forEach(o -> {
					if (o.getTas().contains(ta)) {
						// Add all TimeTableEntry objects that contain the timeTableWeek of the
						// SessenOccurrence
						final Set<TimeTableEntry> matchingEntries = new HashSet<>();
						session.getEntries().forEach(tte -> {
							for (final Week w : tte.getTimeTableWeeks()) {
								// Week number must match from occurrence to week but also to given week value
								if (w.getNumber() == o.getTimeTableWeek() && w.getNumber() == week) {
									matchingEntries.add(tte);
									break;
								}
							}
						});
						shifts.addAll(matchingEntries);
					}
				});
			});
		});
		return shifts;
	}

	/**
	 * If the global verbose setting is activated, this method prints an error
	 * message constructed by the class name of the given object as well as the
	 * given error message.
	 * 
	 * @param object  Object to print verbose error message for.
	 * @param message Error message to print.
	 */
	private void logVerbose(final Object object, final String message) {
		Objects.requireNonNull(object);
		Objects.requireNonNull(message);

		if (verbose) {
			logger.warning( //
					"Violation of " + object.getClass().getSimpleName() //
							+ " " + object + " failed." //
							+ System.lineSeparator() //
							+ "	Reason: " + message);
		}
	}

}
