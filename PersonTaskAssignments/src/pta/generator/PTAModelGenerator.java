package pta.generator;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import PersonTaskAssignments.Offer;
import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.PersonTaskAssignmentsFactory;
import PersonTaskAssignments.PersonTaskAssignmentsPackage;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Requirement;
import PersonTaskAssignments.Skill;
import PersonTaskAssignments.SkillType;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;

public class PTAModelGenerator {
	protected PersonTaskAssignmentsFactory factory = PersonTaskAssignmentsFactory.eINSTANCE;

	protected Map<String, SkillType> skillTypes = new LinkedHashMap<>();
	protected Map<String, Person> persons = new LinkedHashMap<>();
	protected Map<Integer, Week> weeks = new LinkedHashMap<>();
	protected Map<String, Project> projects = new LinkedHashMap<>();
	protected Map<String, Map<String, Task>> tasks = new LinkedHashMap<>();

	protected PersonTaskAssignmentModel root;

	public PersonTaskAssignmentModel generate() {
		root = factory.createPersonTaskAssignmentModel();
		root.getSkillTypes().addAll(skillTypes.values());
		root.getPersons().addAll(persons.values());
		root.getWeeks().addAll(weeks.values());
		root.getProjects().addAll(projects.values());
		return root;
	}

	public static void save(PersonTaskAssignmentModel model, String path) throws IOException {
		URI uri = URI.createFileURI(path);
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(PersonTaskAssignmentsPackage.eNS_URI, PersonTaskAssignmentsPackage.eINSTANCE);
		Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		r.unload();
	}

	public static Resource saveAndReturn(PersonTaskAssignmentModel model, String path) throws IOException {
		URI uri = URI.createFileURI(path);
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(PersonTaskAssignmentsPackage.eNS_URI, PersonTaskAssignmentsPackage.eINSTANCE);
		Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}

	public SkillType addSkillType(String name) {
		SkillType type = factory.createSkillType();
		type.setName(name);
		skillTypes.put(name, type);
		return type;
	}

	public Collection<SkillType> addSkillType(String... names) {
		Collection<SkillType> st = new LinkedHashSet<>();
		for (String name : names) {
			SkillType type = factory.createSkillType();
			type.setName(name);
			skillTypes.put(name, type);
			st.add(type);
		}
		return st;
	}

	public Person addPerson(String name, double salary, double flexibility, double overtimeSalary,
			Map<String, Integer> skills) {
		Person p = factory.createPerson();
		p.setName(name);
		p.setSalary(salary);
		p.setOvertimeFlexibility(flexibility);
		p.setOvertimeBonus(overtimeSalary);
		skills.forEach((sName, sLevel) -> {
			Skill skill = factory.createSkill();
			SkillType type = skillTypes.get(sName);
			if (type == null) {
				type = addSkillType(sName);
			}
			skill.setName(type.getName());
			skill.setType(skillTypes.get(sName));
			skill.setLevel(sLevel);
			p.getSkills().add(skill);
		});
		persons.put(name, p);
		return p;
	}

	public Collection<Offer> addOffer(String person, int week, int... hours) {
		Collection<Offer> os = new LinkedHashSet<>();
		for (int hour : hours) {
			Offer offer = factory.createOffer();
			offer.setHours(hour);
			os.add(offer);
			offer.setWeek(weeks.get(week));
		}
		persons.get(person).getOffers().addAll(os);
		return os;
	}

	public Project addProject(String name, double reward, int weeksUntilLoss, double lossPerWeek, int start) {
		Project p = factory.createProject();
		p.setName(name);
		p.setStart(weeks.get(start));
		p.setReward(reward);
		p.setWeeksUntilLoss(weeksUntilLoss);
		p.setLossPerWeek(lossPerWeek);
		p.setInitialWeekNumber(start);
		projects.put(name, p);
		return p;
	}

	public Task addTask(String project, String name, String... previousTasks) {
		Task t = factory.createTask();
		t.setName(name);
		projects.get(project).getTasks().add(t);
		Map<String, Task> pTasks = tasks.get(project);
		if (pTasks == null) {
			pTasks = new LinkedHashMap<>();
			tasks.put(project, pTasks);
		}
		pTasks.put(name, t);
		for (String prevTask : previousTasks) {
			t.getPrevious().add(pTasks.get(prevTask));
		}
		return t;
	}

	public Requirement addRequirement(String project, String task, int hours, int skillLevel, String skill,
			double salary, double overtimebonus) {
		Requirement r = factory.createRequirement();
		Task t = tasks.get(project).get(task);
		t.getRequirements().add(r);
		r.setSkillType(skillTypes.get(skill));
		r.setName(r.getSkillType().getName());
		r.setHours(hours);
		r.setSkillLevel(skillLevel);
		r.setSalary(salary);
		r.setOvertimeBonus(overtimebonus);
		return r;
	}

	public Collection<Week> addWeeks(int start, int end) {
		Collection<Week> ws = new LinkedHashSet<>();
		Week prev = null;
		for (int i = start; i <= end; i++) {
			Week w = factory.createWeek();
			w.setNumber(i);
			weeks.put(i, w);
			ws.add(w);
			if (prev != null)
				w.setPrevious(prev);

			prev = w;
		}

		return ws;
	}
}
