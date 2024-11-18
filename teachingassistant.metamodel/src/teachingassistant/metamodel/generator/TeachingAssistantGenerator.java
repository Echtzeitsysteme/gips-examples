package teachingassistant.metamodel.generator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import metamodel.Timeslot;
import metamodel.Tutorial;

public class TeachingAssistantGenerator {
	protected MetamodelFactory factory = MetamodelFactory.eINSTANCE;
	protected Department root;

	protected Map<String, Assistant> assistants = new LinkedHashMap<>();
	protected Map<String, Tutorial> tutorials = new LinkedHashMap<>();
	protected Map<Integer, Timeslot> timeslots = new LinkedHashMap<>();

	protected Random rand;

	public Department generate(final String departmentName) {
		checkNotNull(departmentName, "Name");

		root = factory.createDepartment();
		root.setName(departmentName);
		root.getAssistants().addAll(assistants.values());
		root.getTutorials().addAll(tutorials.values());
		root.getTimeslots().addAll(timeslots.values());
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

	public void addTutorial(final String name, final SkillType type, final int duration, final int timeslot) {
		checkNotNull(name, " Name");
		checkNotNull(type, "Type");
		checkNotNull(timeslot, "Time slot");

		final Tutorial t = factory.createTutorial();
		t.setName(name);
		t.setDuration(duration);
		t.setType(type);
		t.setTimeslot(timeslots.get(timeslot));
		this.tutorials.put(name, t);
	}

	public void addAssistant(final String name, final int minHoursPerWeek, final int maxHoursPerWeek) {
		checkNotNull(name, "Name");

		final Assistant a = factory.createAssistant();
		a.setMinimumHoursPerWeek(minHoursPerWeek);
		a.setMaximumHoursPerWeek(maxHoursPerWeek);
		a.setName(name);
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

	public void addTimeslot(final int id) {
		if (id < 0) {
			throw new IllegalArgumentException("Time slot ID must not be negative.");
		}
		final Timeslot t = factory.createTimeslot();
		t.setName(String.valueOf(id));
		t.setId(id);
		timeslots.put(Integer.valueOf(id), t);
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

	protected String prepareFolder() {
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/../teachingassistant.metamodel" + "/instances";
		final File f = new File(instancesFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		return instancesFolder;
	}

	protected int getRandInt(final int min, final int max) {
		return rand.nextInt((max - min) + 1) + min;
	}

	protected SkillType getRandomSkillType() {
		final List<SkillType> allSkillTypes = SkillType.VALUES;
		final int randomIndex = getRandInt(0, allSkillTypes.size() - 1);
		return allSkillTypes.get(randomIndex);
	}

}
