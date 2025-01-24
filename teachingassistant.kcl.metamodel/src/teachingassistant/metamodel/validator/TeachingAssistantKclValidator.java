package teachingassistant.metamodel.validator;

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
		boolean valid = true;

		// Tutorials
		for (final Tutorial tutorial : model.getTutorials()) {
			valid = valid && validate(tutorial);
		}
		valid = valid && validateTutorialNameUnique(model.getTutorials());

		// Assistants
		for (final Assistant assistant : model.getAssistants()) {
			valid = valid && validate(assistant, model);
		}
		valid = valid && validateAssistantNameUnique(model.getAssistants());

		// Time slots
		for (final Timeslot timeslot : model.getTimeslots()) {
			valid = valid && validate(timeslot);
		}
		valid = valid && validateTimeSlotsIdUnique(model.getTimeslots());

		// Days
		for (final Day day : model.getDays()) {
			valid = valid && validate(day);
		}
		valid = valid && validateDayNameUnique(model.getDays());

		// Lecturers
		for (final Lecturer lecturer : model.getLecturers()) {
			valid = valid && validate(lecturer);
		}
		valid = valid && validateLecturerNameUnique(model.getLecturers());

		return valid;
	}

	/**
	 * Checks the given collection of tutorials for unique names.
	 * 
	 * @param tutorials Collection of tutorials to check.
	 * @return True if all names were unique.
	 */
	private boolean validateTutorialNameUnique(final Collection<Tutorial> tutorials) {
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
		final Set<Integer> ids = new HashSet<>();
		for (final Timeslot ts : timeslots) {
			if (!ids.add(ts.getId())) {
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
		final Set<String> names = new HashSet<>();
		for (final Day d : days) {
			if (!names.add(d.getName())) {
				return false;
			}
		}

		return true;
	}

	private boolean validate(final Tutorial tutorial) {
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
					return false;
				}
				cumulatedTotalHours += tutorial.getDuration();

				// An assistant must not have two tutorials at the same time slot
				if (tutorial.getTimeslot() != null) {
					if (!usedTimeslots.add(tutorial.getTimeslot().getId())) {
						return false;
					}
				}
			}
		}

		// Assistant's total hour limit must be matched by the cumulative duration
		if (!(cumulatedTotalHours <= assistant.getMaximumHoursTotal())) {
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
						|| hoursInWeek > assistant.getMaximumDaysPerWeek()) {
					return false;
				}
			}

			if (week2Days.containsKey(w)) {
				final int daysInWeek = week2Days.get(w).size();
				if (daysInWeek > assistant.getMaximumDaysPerWeek()) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean validate(final Lecturer lecturer) {
		// Name
		if (lecturer.getName() == null || lecturer.getName().isBlank()) {
			return false;
		}

		// Tutorial types
		for (final Tutorial t : lecturer.getTutorials()) {
			if (!t.getSkillType().equals(lecturer.getSkillTypeName())) {
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
			return false;
		}
		if (employedAssistants.size() > lecturer.getMaximumNumberOfTas()) {
			return false;
		}

		// A lecturer must have at least one tutorial
		if (lecturer.getTutorials().isEmpty()) {
			return false;
		}

		return true;
	}

	private boolean validate(final Timeslot timeslot) {
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

	private boolean validate(final Day day) {
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
