package nurserosteringmodel.validator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;
import org.emoflon.smartemf.runtime.collections.LinkedSmartESet;

import nurserosteringmodel.Contract;
import nurserosteringmodel.CoverRequirement;
import nurserosteringmodel.Day;
import nurserosteringmodel.Employee;
import nurserosteringmodel.NurserosteringmodelPackage;
import nurserosteringmodel.Root;
import nurserosteringmodel.Shift;
import nurserosteringmodel.Skill;

public class NurseRosteringModelValidator {

	public final static String SCENARIO_FILE_NAME = "solved.xmi";

	public static void main(final String[] args) {
		new NurseRosteringModelValidator().run();
	}

	private void run() {
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/resources/";
		final String filePath = instanceFolder + SCENARIO_FILE_NAME;

		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(NurserosteringmodelPackage.eNS_URI, NurserosteringmodelPackage.eINSTANCE);
		final URI fileURI = URI.createFileURI(filePath);
		final Resource r = rs.getResource(fileURI, true);
		final Root model = (Root) r.getContents().get(0);
		final boolean valid = validate(model);

		if (valid) {
			System.out.println("Result: Model is valid.");
		} else {
			System.out.println("Result: Model is not valid.");
		}
	}

	private boolean validate(final Root model) {
		if (model == null) {
			throw new IllegalArgumentException("Model was null.");
		}

		boolean valid = true;

		// Check dates
		valid = valid && checkDate(model.getStartDate());
		valid = valid && checkDate(model.getEndDate());
		valid = valid && checkStartDateBeforeEndDate(model.getStartDate(), model.getEndDate());

		// Check skills
		valid = valid && checkSkills(model.getSkills());

		// Check employees
		valid = valid && checkEmployees(model.getEmployees(), model);

		// Check days
		valid = valid && checkDays(model.getDays(), model);

		// Check shifts
		final Set<Shift> shifts = getAllShifts(model);
		valid = valid && checkShifts(shifts, model);

		// Check contracts
		valid = valid && checkContracts(model.getContracts());

		return valid;
	}

	private Set<Shift> getAllShifts(final Root model) {
		final Set<Shift> shifts = new HashSet<Shift>();
		shifts.addAll(model.getShifts());
		for (final Day d : model.getDays()) {
			for (final CoverRequirement cr : d.getRequirements()) {
				shifts.add(cr.getShift());
			}
		}
		return shifts;
	}

	private boolean checkDate(final String dateString) {
		if (dateString == null || dateString.isBlank()) {
			return false;
		}

		// Check if date can be parsed
		try {
			LocalDate.parse(dateString);
		} catch (final DateTimeParseException e) {
			return false;
		}

		return true;
	}

	private boolean checkStartDateBeforeEndDate(final String startDate, final String endDate) {
		final LocalDate start = LocalDate.parse(startDate);
		final LocalDate end = LocalDate.parse(endDate);
		return end.isAfter(start);
	}

	private boolean checkSkills(final LinkedSmartESet<Skill> skills) {
		if (skills == null || skills.isEmpty()) {
			return false;
		}

		for (final Skill s : skills) {
			if (s.getName() == null || s.getName().isBlank()) {
				return false;
			}
		}

		return true;
	}

	private boolean checkEmployees(final LinkedSmartESet<Employee> employees, final Root model) {
		if (employees == null || employees.isEmpty()) {
			return false;
		}

		for (final Employee e : employees) {
			if (e.getName() == null || e.getName().isBlank()) {
				return false;
			}

			// An employee needs at least one skill
			if (e.getSkills().isEmpty()) {
				return false;
			}

			// An employee needs a contract
			if (e.getContract() == null) {
				return false;
			}

			// The contract must be contained within the root model
			if (!model.getContracts().contains(e.getContract())) {
				return false;
			}

			// All skills of the employee must be contained within the root model
			for (final Skill s : e.getSkills()) {
				if (!model.getSkills().contains(s)) {
					return false;
				}
			}

			// Check if assignments are within the boundaries of the respective contract
			// TODO
		}

		return true;
	}

	private boolean checkDays(final LinkedSmartESet<Day> days, final Root model) {
		if (days == null || days.isEmpty()) {
			return false;
		}

		for (final Day d : days) {
			// Strings for null or blankness
			if (d.getName() == null || d.getName().isBlank()) {
				return false;
			}

			if (d.getDayOfWeek() == null || d.getDayOfWeek().isBlank()) {
				return false;
			}

			if (d.getDate() == null || d.getDate().isBlank()) {
				return false;
			}

			// Day of the week must be a correct name
			if (!checkDayOfWeekName(d.getDayOfWeek())) {
				return false;
			}

			// Actual date can be parsed
			if (!checkDate(d.getDate())) {
				return false;
			}

			// There must be at least one requirement
			if (d.getRequirements().isEmpty()) {
				return false;
			}

			// Check all cover requirements
			for (final CoverRequirement cr : d.getRequirements()) {
				if (cr.getPreferred() < 1) {
					return false;
				}

				if (cr.getShift() == null) {
					return false;
				}

				if (!getAllShifts(model).contains(cr.getShift())) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean checkShifts(final Collection<Shift> shifts, final Root model) {
		if (shifts == null || shifts.isEmpty()) {
			return false;
		}

		for (final Shift s : shifts) {
			if (s.getName() == null || s.getName().isBlank()) {
				return false;
			}

			if (s.getStartTime() < 0 || s.getEndTime() < 0) {
				return false;
			}

			// startTime < endTime is not a requirement because of night shifts

			if (s.getSkills().isEmpty()) {
				return false;
			}

			// Every skill must be contained within the root model
			for (final Skill skill : s.getSkills()) {
				if (!model.getSkills().contains(skill)) {
					return false;
				}
			}

			if (s.getDay() == null) {
				return false;
			}

			if (!model.getDays().contains(s.getDay())) {
				return false;
			}

			// Check assigned employees
			// TODO
		}

		return true;
	}

	private boolean checkContracts(final LinkedSmartESet<Contract> contracts) {
		if (contracts == null || contracts.isEmpty()) {
			return false;
		}

		for (final Contract c : contracts) {
			// Check if integer values are in a meaningful range
			if (c.getMaximumNoOfAssignments() < 1) {
				return false;
			}
			if (c.getMinimumNoOfAssignments() < 0) {
				return false;
			}
			if (c.getMaximumNoOfConsWorkDays() < 1) {
				return false;
			}
			if (c.getMinimumNoOfConsWorkDays() < 1) {
				return false;
			}
			if (c.getMaximumNoOfConsFreeDays() < 0) {
				return false;
			}
			if (c.getMinimumNoOfConsFreeDays() < 0) {
				return false;
			}
			if (c.getMaximumNoOfConsWorkWeekends() < 0) {
				return false;
			}
			if (c.getMaximumNoOfWorkWeekInFourWeeks() < 0 || c.getMaximumNoOfWorkWeekInFourWeeks() > 4) {
				return false;
			}
			if (c.getNoOfDaysOffAfterSeriesOfNightShifts() < 0) {
				return false;
			}

			// Check min/max integer values
			if (c.getMaximumNoOfAssignments() < c.getMinimumNoOfAssignments()) {
				return false;
			}
			if (c.getMaximumNoOfConsWorkDays() < c.getMinimumNoOfConsWorkDays()) {
				return false;
			}
			if (c.getMaximumNoOfConsFreeDays() < c.getMinimumNoOfConsFreeDays()) {
				return false;
			}

			// Check that requested days are not blocked days
			for (final Day d : c.getRequestedDays()) {
				if (c.getBlockedDays().contains(d)) {
					return false;
				}
			}

			// Check that requested shifts are not blocked shifts
			for (final Shift s : c.getRequestedShifts()) {
				if (c.getBlockedShifts().contains(s)) {
					return false;
				}
			}

			// Blocked shifts can also be assigned shifts because this is not a hard
			// requirement.
			// It is also possible to get assigned shifts that are on blocked days because
			// this is not a hard requirement.
		}

		return true;
	}

	private boolean checkDayOfWeekName(final String dayOfWeekName) {
		if (dayOfWeekName == null || dayOfWeekName.isBlank()) {
			return false;
		}
		switch (dayOfWeekName) {
		case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday": {
			return true;
		}
		default:
			return false;
		}
	}

}
