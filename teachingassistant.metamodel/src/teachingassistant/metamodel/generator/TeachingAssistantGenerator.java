package teachingassistant.metamodel.generator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import metamodel.Assistant;
import metamodel.Department;
import metamodel.MetamodelFactory;
import metamodel.MetamodelPackage;
import metamodel.Skill;
import metamodel.SkillType;
import metamodel.Tutorial;

public class TeachingAssistantGenerator {
	protected MetamodelFactory factory = MetamodelFactory.eINSTANCE;
	protected Department root;

	protected Map<String, Assistant> assistants = new LinkedHashMap<>();
	protected Map<String, Tutorial> tutorials = new LinkedHashMap<>();

	public Department generate(final String departmentName) {
		checkNotNull(departmentName, "Name");

		root = factory.createDepartment();
		root.setName(departmentName);
		root.getAssistants().addAll(assistants.values());
		root.getTutorials().addAll(tutorials.values());
		return root;
	}

	public void addTutorial(final String name, final SkillType type, final int duration) {
		checkNotNull(name, " Name");
		checkNotNull(type, "Type");

		final Tutorial t = factory.createTutorial();
		t.setName(name);
		t.setDuration(duration);
		t.setType(type);
		this.tutorials.put(name, t);
	}

	public void addAssistant(final String name, final int minHoursPerWeek, final int maxHoursPerWeek) {
		checkNotNull(name, "Name");

		final Assistant a = factory.createAssistant();
		a.setMinimumHoursPerWeek(minHoursPerWeek);
		a.setMaximumHoursPerWeek(maxHoursPerWeek);
		assistants.put(name, a);
	}

	public void addSkillToAssistant(final String name, final SkillType type, final int preference) {
		checkNotNull(name, "Name");
		checkNotNull(type, "Type");

		if (!this.assistants.containsKey(name)) {
			throw new UnsupportedOperationException("Assistent with name <" + name + "> does not exist.");
		}

		final Skill s = createSkill(type, preference);
		assistants.get(name).getSkills().add(s);
	}

	private Skill createSkill(final SkillType type, final int preference) {
		if (preference < 0) {
			throw new IllegalArgumentException("Skill preference must not be negative.");
		}
		checkNotNull(type, "SkillType");

		final Skill s = factory.createSkill();
		s.setType(type);
		s.setPreference(preference);
		return s;
	}

	//
	// Utility methods.
	//

	private static void checkNotNull(final Object o, final String type) {
		if (o == null) {
			throw new IllegalArgumentException(type + " must not be null.");
		}
	}

	public static void save(final Department model, final String path) throws IOException {
		final Resource r = saveAndReturn(model, path);
		r.unload();
	}

	public static Resource saveAndReturn(final Department model, final String path) throws IOException {
		checkNotNull(model, "Model");
		checkNotNull(path, "Path");

		final URI uri = URI.createFileURI(path);
		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		final Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}

}
