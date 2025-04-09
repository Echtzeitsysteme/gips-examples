package teachingassistant.kcl.metamodelalt.validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.EmploymentApproval;
import metamodel.Module;
import metamodel.NamedElement;
import metamodel.SessionOccurrence;
import metamodel.TA;
import metamodel.TAAllocation;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import metamodel.Week;
import teachingassistant.kcl.metamodelalt.export.FileUtils;

/**
 * Model validator for the teaching assistant example (alternative metamodel).
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class TeachingAssistantKclValidator {

	/**
	 * Model file name to load.
	 */
	public final static String SCENARIO_FILE_NAME = "solved.xmi";

	/**
	 * Main method to run the stand-alone model validation.
	 * 
	 * @param args Arguments that will be ignored.
	 */
	public static void main(final String[] args) {
		// Construct file path
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodelalt/instances/";
		final String filePath = instanceFolder + SCENARIO_FILE_NAME;

		// Load model
		final Resource r = FileUtils.loadModel(filePath);
		final TAAllocation model = (TAAllocation) r.getContents().get(0);

		// Validate
		final boolean valid = new TeachingAssistantKclValidator().validate(model);

		if (valid) {
			System.out.println("Result: Model is valid.");
		} else {
			System.out.println("Result: Model is not valid.");
		}
	}

	//
	// Validation methods.
	//
	
	/**
	 * Validate the given TAAllocation object.
	 * 
	 * @param model TAAllocation object to validate.
	 * @return True if complete model is valid.
	 */
	public boolean validate(final TAAllocation model) {
		if (model == null) {
			System.out.println("=> Given model was null.");
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
			System.out.println("=> All weeks are valid: " + weeksValid);
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
				allFoundTimeTableEntries.addAll(ta.getUnavailable_because_lessons());
			});
			boolean timeTableEntriesValid = true;
			for (final TimeTableEntry entry : allFoundTimeTableEntries) {
				timeTableEntriesValid = timeTableEntriesValid & validate(entry);
			}
			System.out.println("=> All time table entries are valid: " + timeTableEntriesValid);
			valid = valid & timeTableEntriesValid;
		}

		// TAs
		{
			boolean tasValid = true;
			for (final TA ta : model.getTas()) {
				tasValid = tasValid & validate(ta, model);
			}
			System.out.println("=> All TAs are valid: " + tasValid);
			valid = valid & tasValid;
		}

		// Modules
		{
			boolean modulesValid = true;
			for (final metamodel.Module m : model.getModules()) {
				modulesValid = modulesValid & validate(m, model);
			}
			System.out.println("=> All modules are valid: " + modulesValid);
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
			System.out.println("=> All employment approvals are valid: " + employmentApprovalsValid);
			valid = valid & employmentApprovalsValid;
		}

		// SessionOccurrences
		{
			// Collect all session occurrences
			final Set<SessionOccurrence> allFoundSessionOccurrences = new HashSet<>();
			model.getModules().forEach(module -> {
				module.getSessions().forEach(session -> {
					allFoundSessionOccurrences.addAll(session.getOccurrences());
				});
			});
			boolean sessionOccurrencesValid = true;
			for (final SessionOccurrence so : allFoundSessionOccurrences) {
				sessionOccurrencesValid = sessionOccurrencesValid & validate(so, model);
			}
			System.out.println("=> All session occurrences are valid: " + sessionOccurrencesValid);
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
			System.out.println("=> All teaching sessions are valid: " + teachingSessionsValid);
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
	private boolean validate(final Module m, final TAAllocation model) {
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
	private boolean validate(final TeachingSession ts, final TAAllocation model) {
		if (ts == null) {
			return false;
		}

		if (ts.getHoursPaidPerOccurrence() < 0) {
			return false;
		}

		if (ts.getNumTAsPerSession() < 1) {
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
				return false;
			}
		}

		return true;
	}

	/**
	 * Validate a given session occurrence `so` in the context of the complete
	 * model.
	 * 
	 * @param so    Session occurrence to validate.
	 * @param model Complete model.
	 * @return True if the given session occurrence `so` was valid.
	 */
	private boolean validate(final SessionOccurrence so, final TAAllocation model) {
		if (so == null) {
			return false;
		}

		if (so.getTimeTableWeek() < getFirstWeekNumber(model.getWeeks())) {
			return false;
		}

		if (so.getTimeTableWeek() > getLastWeekNumber(model.getWeeks())) {
			return false;
		}

		if (so.getTas().isEmpty()) {
			return false;
		}

		for (final TA ta : so.getTas()) {
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
	private boolean validate(final TA ta, final TAAllocation model) {
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
		for (final TimeTableEntry unavailable : ta.getUnavailable_because_lessons()) {
			if (allShifts.contains(unavailable)) {
				return false;
			}
		}

		// TODO: Check time limit per week

		// TODO: Check time limit per year

		// TODO: Check for conflicting assignments per TA

		return true;
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

		if (entry.getSession() == null) {
			return false;
		}

		// TeachingSession
		if (!entry.getSession().getEntries().contains(entry)) {
			return false;
		}

		// timeTableWeeks
		if (!entry.getSession().getTimeTableWeeks().containsAll(entry.getTimeTableWeeks())) {
			return false;
		}

		// startTime < endTime is required
		if (entry.getStartTime().compareTo(entry.getEndTime()) >= 0) {
			return false;
		}

		// TODO: startTime with timeTableWeeks?
		// TODO: endTime with timeTableWeeks?
		// TODO: `weekDay` must be correct in `startTime` and `endTime`

		return true;
	}

	//
	// Utility methods.
	//

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
	private Set<TimeTableEntry> findAllShiftsOfTa(final TA ta, final TAAllocation model) {
		final Set<TimeTableEntry> shifts = new HashSet<>();
		model.getModules().forEach(module -> {
			boolean assigned = false;
			for (final EmploymentApproval ea : module.getApprovals()) {
				if (ea.getTa() != null && ea.getTa().equals(ta)) {
					assigned = true;
					break;
				}
			}

			// If the TA is not assigned in any approval, skip remaining checks.
			if (!assigned) {
				return;
			}

			module.getSessions().forEach(session -> {
				session.getOccurrences().forEach(o -> {
					if (o.getTas().contains(ta)) {
						shifts.addAll(session.getEntries());
					}
				});
			});
		});

		// TODO: Check if this method is correctly implemented.
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
	private Set<TimeTableEntry> findAllShiftsOfTaInWeek(final TA ta, final int week, final TAAllocation model) {
		final Set<TimeTableEntry> shifts = new HashSet<>();
		model.getModules().forEach(module -> {
			boolean assigned = false;
			for (final EmploymentApproval ea : module.getApprovals()) {
				if (ea.getTa() != null && ea.getTa().equals(ta)) {
					assigned = true;
					break;
				}
			}

			// If the TA is not assigned in any approval, skip remaining checks.
			if (!assigned) {
				return;
			}

			module.getSessions().forEach(session -> {
				session.getOccurrences().forEach(o -> {
					if (o.getTimeTableWeek() == week) {
						if (o.getTas().contains(ta)) {
							shifts.addAll(session.getEntries());
						}
					}
				});
			});
		});

		// TODO: Check if this method is correctly implemented.
		return shifts;
	}

}
