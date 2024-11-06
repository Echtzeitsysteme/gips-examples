package teachingassistant.metamodel.validator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import metamodel.Assistant;
import metamodel.Department;
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
		}

		for (final Assistant assistant : model.getAssistants()) {
			int cummulatedHours = 0;
			for (final Tutorial tutorial : model.getTutorials()) {
				// SkillType of the tutorial must be matched by the assistant
				if (tutorial.getGivenBy() != null && tutorial.getGivenBy().equals(assistant)) {
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
				}
			}

			// Assistant's hour limit must be matched by the cummulative duration
			if (!(assistant.getMinimumHoursPerWeek() <= cummulatedHours)
					|| !(assistant.getMaximumHoursPerWeek() >= cummulatedHours)) {
				return false;
			}
		}

		return true;
	}

}
