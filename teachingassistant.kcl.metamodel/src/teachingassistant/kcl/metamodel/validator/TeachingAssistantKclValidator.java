package teachingassistant.kcl.metamodel.validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import metamodel.Assistant;
import metamodel.Day;
import metamodel.Department;
import metamodel.Lecturer;
import metamodel.MetamodelPackage;
import metamodel.Skill;
import metamodel.Timeslot;
import metamodel.Tutorial;
import metamodel.Week;

/**
 * Model validator for the teaching assistant example.
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
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodel/instances/";
		final String filePath = instanceFolder + SCENARIO_FILE_NAME;

		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		final URI fileURI = URI.createFileURI(filePath);
		final Resource r = rs.getResource(fileURI, true);
		final Department model = (Department) r.getContents().get(0);
		final boolean valid = new TeachingAssistantKclValidator().validate(model);

		if (valid) {
			System.out.println("Result: Model is valid.");
		} else {
			System.out.println("Result: Model is not valid.");
		}

	}

	/**
	 * Validate the given Department object.
	 * 
	 * @param model Department object to validate.
	 * @return True if complete model is valid.
	 */
	public boolean validate(final Department model) {
		if (model == null) {
			System.out.println("=> Given model was null.");
			return false;
		}

		boolean valid = true;

		// Tutorials
		{
			boolean tutorialsValid = true;
			for (final Tutorial tutorial : model.getTutorials()) {
				tutorialsValid = tutorialsValid && validate(tutorial);
			}
			tutorialsValid = tutorialsValid && validateTutorialNameUnique(model.getTutorials());
			System.out.println("=> All tutorials valid: " + tutorialsValid);
			valid = valid && tutorialsValid;
		}

		// Assistants
		{
			boolean assistantsValid = true;
			for (final Assistant assistant : model.getAssistants()) {
				assistantsValid = assistantsValid && validate(assistant, model);
			}
			assistantsValid = assistantsValid && validateAssistantNameUnique(model.getAssistants());
			System.out.println("=> All assistants valid: " + assistantsValid);
			valid = valid && assistantsValid;
		}

		// Time slots
		{
			boolean timeslotsValid = true;
			for (final Timeslot timeslot : model.getTimeslots()) {
				timeslotsValid = timeslotsValid && validate(timeslot);
			}
			timeslotsValid = timeslotsValid && validateTimeSlotsIdUnique(model.getTimeslots());
			System.out.println("=> All time slots valid: " + timeslotsValid);
			valid = valid && timeslotsValid;
		}

		// Weeks
		{
			boolean weeksValid = true;
			for (final Week week : model.getWeeks()) {
				weeksValid = weeksValid && validate(week);
			}
			weeksValid = weeksValid && validateWeekNameUnique(model.getWeeks());
			System.out.println("=> All weeks are valid: " + weeksValid);
			valid = valid && weeksValid;
		}

		// Days
		{
			boolean daysValid = true;
			for (final Day day : model.getDays()) {
				daysValid = daysValid && validate(day);
			}
			daysValid = daysValid && validateDayNameUnique(model.getDays());
			System.out.println("=> All days valid: " + daysValid);
			valid = valid && daysValid;
		}

		// Lecturers
		{
			boolean lecturersValid = true;
			for (final Lecturer lecturer : model.getLecturers()) {
				lecturersValid = lecturersValid && validate(lecturer);
			}
			lecturersValid = lecturersValid && validateLecturerNameUnique(model.getLecturers());
			System.out.println("=> All lecturers valid: " + lecturersValid);
			valid = valid && lecturersValid;
		}

		return valid;
	}

	/**
	 * Checks the given collection of tutorials for unique names.
	 * 
	 * @param tutorials Collection of tutorials to check.
	 * @return True if all names were unique.
	 */
	private boolean validateTutorialNameUnique(final Collection<Tutorial> tutorials) {
		if (tutorials == null) {
			return false;
		}

		final Set<String> names = new HashSet<>();
		for (final Tutorial t : tutorials) {
			if (!names.add(t.getName())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks the given collection of assistants for unique names.
	 * 
	 * @param assistants Collection of assistants to check.
	 * @return True if all names were unique.
	 */
	private boolean validateAssistantNameUnique(final Collection<Assistant> assistants) {
		if (assistants == null) {
			return false;
		}

		final Set<String> names = new HashSet<>();
		for (final Assistant a : assistants) {
			if (!names.add(a.getName())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks the given collection of lecturers for unique names.
	 * 
	 * @param lecturers Collection of lecturers to check.
	 * @return True if all names were unique.
	 */
	private boolean validateLecturerNameUnique(final Collection<Lecturer> lecturers) {
		if (lecturers == null) {
			return false;
		}

		final Set<String> names = new HashSet<>();
		for (final Lecturer l : lecturers) {
			if (!names.add(l.getName())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks the given collection of time slots for unique IDs.
	 * 
	 * @param timeslots Collection of time slots to check.
	 * @return True if all IDs were unique.
	 */
	private boolean validateTimeSlotsIdUnique(final Collection<Timeslot> timeslots) {
		if (timeslots == null) {
			return false;
		}

		final Set<Integer> ids = new HashSet<>();
		for (final Timeslot ts : timeslots) {
			if (!ids.add(ts.getId())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks the given collection of weeks for unique names.
	 * 
	 * @param weeks Collection of weeks to check.
	 * @return True if all names were unique.
	 */
	private boolean validateWeekNameUnique(final Collection<Week> weeks) {
		if (weeks == null) {
			return false;
		}

		final Set<String> names = new HashSet<>();
		for (final Week w : weeks) {
			if (!names.add(w.getName())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks the given collection of days for unique names.
	 * 
	 * @param days Collection of days to check.
	 * @return True if all names were unique.
	 */
	private boolean validateDayNameUnique(final Collection<Day> days) {
		if (days == null) {
			return false;
		}

		final Set<String> names = new HashSet<>();
		for (final Day d : days) {
			if (!names.add(d.getName())) {
				return false;
			}
		}

		return true;
	}

	private boolean validate(final Tutorial tutorial) {
		if (tutorial == null) {
			return false;
		}

		// Every tutorial must be given by some assistant
		if (tutorial.getGivenBy() == null) {
			return false;
		}

		// If a tutorial has a lecturer, the tutorial must also be contained in the
		// collection of tutorials of the same lecturer
		if (tutorial.getLecturer() != null) {
			if (!tutorial.getLecturer().getTutorials().contains(tutorial)) {
				return false;
			}
		}

		// The tutorial's skill type must match the lecturer's skill type
		if (tutorial.getLecturer() != null) {
			if (!tutorial.getSkillType().equals(tutorial.getLecturer().getSkillTypeName())) {
				return false;
			}
		}

		// The tutorial's duration must be larger than zero
		if (tutorial.getDuration() <= 0) {
			return false;
		}

		// The tutorial's time slot must not be empty
		if (tutorial.getTimeslot() == null) {
			return false;
		}

		return true;
	}

	private boolean validate(final Assistant assistant, final Department model) {
		if (assistant == null) {
			return false;
		}

		if (model == null) {
			throw new IllegalArgumentException("Given model was null.");
		}

		// Name
		if (assistant.getName() == null || assistant.getName().isBlank()) {
			return false;
		}

		int cumulatedTotalHours = 0;
		final Set<Integer> usedTimeslots = new HashSet<Integer>();
		final Set<Tutorial> allGivenTutorials = new HashSet<Tutorial>();
		for (final Tutorial tutorial : model.getTutorials()) {
			if (tutorial.getGivenBy() != null && tutorial.getGivenBy().equals(assistant)) {
				allGivenTutorials.add(tutorial);
				// SkillType of the tutorial must be matched by the assistant
				boolean skillTypeMatched = false;
				for (final Skill s : assistant.getSkills()) {
					if (tutorial.getSkillType().equals(s.getName())) {
						skillTypeMatched = true;
					}
				}
				if (!skillTypeMatched) {
					System.out.println("=> Skill type of assistant <" + assistant.getName() + "> and tutorial <"
							+ tutorial.getName() + "> not matched.");
					return false;
				}
				cumulatedTotalHours += tutorial.getDuration();

				// An assistant must not have two tutorials at the same time slot
				if (tutorial.getTimeslot() != null) {
					if (!usedTimeslots.add(tutorial.getTimeslot().getId())) {
						System.out.println("=> Assistant <" + assistant.getName()
								+ "> has two tutorials on overlapping time slots: " + tutorial.getTimeslot().getId());
						;
						return false;
					}
				}
			}
		}

		// Assistant's total hour limit must be matched by the cumulative duration
		if (!(cumulatedTotalHours <= assistant.getMaximumHoursTotal())) {
			System.err.println(
					"=> Assistant <" + assistant.getName() + "> exceeds their maximum hours total. Specified limit: "
							+ assistant.getMaximumHoursTotal() + ", actual assignment: " + cumulatedTotalHours);
			return false;
		}

		// Number of assigned work days must be smaller or equal to the maximum number
		// Check maximum number of work days per week and cumulative hours per week
		//
		// Extract two information of all given tutorials:
		// 1) total number of hours per week
		// 2) working days per week
		final Map<Week, Integer> week2Hours = new HashMap<Week, Integer>();
		final Map<Week, Set<Day>> week2Days = new HashMap<Week, Set<Day>>();
		for (final Tutorial t : allGivenTutorials) {
			final Week w = t.getTimeslot().getDay().getWeek();

			// Hours
			if (!week2Hours.containsKey(w)) {
				week2Hours.put(w, 0);
			}

			final int previousValue = week2Hours.remove(w);
			week2Hours.put(w, previousValue + t.getDuration());

			// Days
			if (!week2Days.containsKey(w)) {
				week2Days.put(w, new HashSet<Day>());
			}

			week2Days.get(w).add(t.getTimeslot().getDay());
		}

		// Check found values against assistant's values from the model
		for (final Week w : model.getWeeks()) {
			if (week2Hours.containsKey(w)) {
				final int hoursInWeek = week2Hours.get(w);
				if (hoursInWeek < assistant.getMinimumHoursPerWeek()
						|| hoursInWeek > assistant.getMaximumHoursPerWeek()) {
					System.out.println("=> Assistant <" + assistant.getName()
							+ "> number of hours per week is not within their boundaries: "
							+ assistant.getMinimumHoursPerWeek() + " <=? " + hoursInWeek + " <=? "
							+ assistant.getMaximumHoursPerWeek());
					return false;
				}
			}

			if (week2Days.containsKey(w)) {
				final int daysInWeek = week2Days.get(w).size();
				if (daysInWeek > assistant.getMaximumDaysPerWeek()) {
					System.out.println("=> Assistant <" + assistant.getName() + "> maximum number of days exceeded: "
							+ assistant.getMaximumDaysPerWeek() + " <=? " + daysInWeek);
					;
					return false;
				}
			}
		}

		return true;
	}

	private boolean validate(final Lecturer lecturer) {
		if (lecturer == null) {
			return false;
		}

		// Name
		if (lecturer.getName() == null || lecturer.getName().isBlank()) {
			return false;
		}

		// Tutorial types
		for (final Tutorial t : lecturer.getTutorials()) {
			if (!t.getSkillType().equals(lecturer.getSkillTypeName())) {
				System.out.println("=> Lecturer <" + lecturer.getName() + "> has a tutorial <" + t.getName()
						+ "> with the wrong skill type <" + t.getSkillType() + "> instead of <"
						+ lecturer.getSkillTypeName() + ">.");
				return false;
			}
		}

		// Maximum number of TAs
		final Set<Assistant> employedAssistants = new HashSet<>();
		for (final Tutorial t : lecturer.getTutorials()) {
			employedAssistants.add(t.getGivenBy());
		}
		// lecturers must not have a number of maximum TAs that is smaller than zero
		if (lecturer.getMaximumNumberOfTas() < 0) {
			System.out.println("=> The number of maximum number of TAs of lecturer <" + lecturer.getName()
					+ "> was negative: " + lecturer.getMaximumNumberOfTas());
			return false;
		}
		if (employedAssistants.size() > lecturer.getMaximumNumberOfTas()) {
			System.out.println("=> The number of assigned TAs (" + employedAssistants.size() + ")of lecturer <"
					+ lecturer.getName() + "> was larger than the configured limit: "
					+ lecturer.getMaximumNumberOfTas());
			return false;
		}

		// A lecturer must have at least one tutorial
		if (lecturer.getTutorials().isEmpty()) {
			System.out.println("=> Lecturer <" + lecturer.getName() + "> has no tutorials.");
			return false;
		}

		return true;
	}

	private boolean validate(final Timeslot timeslot) {
		if (timeslot == null) {
			return false;
		}

		if (timeslot.getName() == null || timeslot.getName().isBlank()) {
			return false;
		}

		if (!timeslot.getName().equals(String.valueOf(timeslot.getId()))) {
			return false;
		}

		if (timeslot.getDay() == null) {
			return false;
		}

		return true;
	}

	private boolean validate(final Week week) {
		if (week == null) {
			return false;
		}

		// Name of the week must not be null or blank
		if (week.getName() == null || week.getName().isBlank()) {
			return false;
		}

		// A week must have at least one day
		if (week.getDays().isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean validate(final Day day) {
		if (day == null) {
			return false;
		}

		// Name of the day must not be null or blank
		if (day.getName() == null || day.getName().isBlank()) {
			return false;
		}

		if (day.getWeek() == null) {
			return false;
		}

		// It is allowed for a day to not have any time slots

		return true;
	}

}
