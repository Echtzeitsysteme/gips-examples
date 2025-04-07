package teachingassistant.kcl.metamodelalt.validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import metamodel.MetamodelPackage;
import metamodel.NamedElement;
import metamodel.TA;
import metamodel.TAAllocation;
import metamodel.Week;
import metamodel.TimeTableEntry;

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
		final String instanceFolder = projectFolder + "/../teachingassistant.kcl.metamodelalt/instances/";
		final String filePath = instanceFolder + SCENARIO_FILE_NAME;

		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		final URI fileURI = URI.createFileURI(filePath);
		final Resource r = rs.getResource(fileURI, true);
		final TAAllocation model = (TAAllocation) r.getContents().get(0);
		final boolean valid = new TeachingAssistantKclValidator().validate(model);

		if (valid) {
			System.out.println("Result: Model is valid.");
		} else {
			System.out.println("Result: Model is not valid.");
		}

	}

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
				weeksValid = weeksValid && validate(week);
			}
			weeksValid = weeksValid && validateWeekNumberUnique(allFoundWeeks);
			System.out.println("=> All weeks are valid: " + weeksValid);
			valid = valid && weeksValid;
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
				timeTableEntriesValid = timeTableEntriesValid && validate(entry);
			}
			System.out.println("=> All time table entries are valid: " + timeTableEntriesValid);
			valid = valid && timeTableEntriesValid;
		}

		// TAs
		{
			boolean tasValid = true;
			for (final TA ta : model.getTas()) {
				tasValid = tasValid && validate(ta);
			}
			System.out.println("=> All TAs are valid: " + tasValid);
			valid = valid && tasValid;
		}
		
		// TODO: modules
		// TODO: employment approvals
		// TODO: session occurrences
		// TODO: teaching sessions

		return valid;
	}

	private boolean validate(final TA ta) {
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

		// Unavailable sessions will be checked by the time table entry check.

		// TODO: Check time limit per week
		// TODO: Check time limit per year

		return true;
	}

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

		// TODO: TeachingSession?
		// TODO: timeTableWeeks?

		return true;
	}

}
