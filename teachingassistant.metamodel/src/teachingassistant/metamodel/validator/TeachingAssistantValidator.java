package teachingassistant.metamodel.validator;

import java.util.HashSet;
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
import metamodel.Tutorial;

public class TeachingAssistantValidator {

	public final static String SCENARIO_FILE_NAME = "solved.xmi";

	public static void main(final String[] args) {
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../teachingassistant.metamodel/instances/";
		final String filePath = instanceFolder + SCENARIO_FILE_NAME;

		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		final URI fileURI = URI.createFileURI(filePath);
		final Resource r = rs.getResource(fileURI, true);
		final Department model = (Department) r.getContents().get(0);
		final boolean valid = new TeachingAssistantValidator().validate(model);

		if (valid) {
			System.out.println("Result: Model is valid.");
		} else {
			System.out.println("Result: Model is not valid.");
		}

	}

	public boolean validate(final Department model) {
		for (final Tutorial tutorial : model.getTutorials()) {
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
		}

		for (final Assistant assistant : model.getAssistants()) {
			int cummulatedHours = 0;
			final Set<Day> workingDays = new HashSet<>();
			final Set<Integer> usedTimeslots = new HashSet<Integer>();
			for (final Tutorial tutorial : model.getTutorials()) {
				if (tutorial.getGivenBy() != null && tutorial.getGivenBy().equals(assistant)) {
					// Add working days
					workingDays.add(tutorial.getTimeslot().getDay());

					// SkillType of the tutorial must be matched by the assistant
					boolean skillTypeMatched = false;
					for (final Skill s : assistant.getSkills()) {
						if (tutorial.getType().equals(s.getType())) {
							skillTypeMatched = true;
						}
					}
					if (!skillTypeMatched) {
						return false;
					}
					cummulatedHours += tutorial.getDuration();

					// M1: An assistant must not have two tutorials at the same time slot
					// M0 model instances do not have time slots, hence, the null check
					if (tutorial.getTimeslot() != null) {
						if (!usedTimeslots.add(tutorial.getTimeslot().getId())) {
							return false;
						}
					}
				}
			}

			// Assistant's hour limit must be matched by the cummulative duration
			if (!(assistant.getMinimumHoursPerWeek() <= cummulatedHours)
					|| !(assistant.getMaximumHoursPerWeek() >= cummulatedHours)) {
				return false;
			}

			for (final Lecturer lecturer : model.getLecturers()) {
				final Set<Assistant> employedAssistants = new HashSet<>();
				// Lecturers must only have tutorials with a matching type
				for (final Tutorial t : lecturer.getTutorials()) {
					if (t.getType() != lecturer.getType()) {
						return false;
					}

					employedAssistants.add(t.getGivenBy());
				}

				// lecturers must not have a number of maximum TAs that is smaller than zero
				if (lecturer.getMaximumNumberOfTas() < 0) {
					return false;
				}

				// The number of assigned TAs must not be larger than the maximum number of TAs
				// (limit given by the specific lecturer)
				if (employedAssistants.size() > lecturer.getMaximumNumberOfTas()) {
					return false;
				}
			}

			// Number of assigned work days must be smaller or equal to the maximum number
			// of work days of this specific assistant
			if (workingDays.size() > assistant.getMaximumDaysPerWeek()) {
				return false;
			}
		}

		return true;
	}

}
