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
	
	final public static String[] firstNames = { //
			"Michael", //
			"Christopher", //
			"Jessica", //
			"Matthew", //
			"Ashley", //
			"Jennifer", //
			"Joshua", //
			"Amanda", //
			"Daniel", //
			"David", //
			"James", //
			"Robert", //
			"John", //
			"Joseph", //
			"Andrew", //
			"Ryan", //
			"Brandon", //
			"Jason", //
			"Justin", //
			"Sarah", //
			"William", //
			"Jonathan", //
			"Stephanie", //
			"Brian", //
			"Nicole", //
			"Nicholas", //
			"Anthony", //
			"Heather", //
			"Eric", //
			"Elizabeth", //
			"Adam", //
			"Megan", //
			"Melissa", //
			"Kevin", //
			"Steven", //
			"Thomas", //
			"Timothy", //
			"Christina", //
			"Kyle", //
			"Rachel", //
			"Laura", //
			"Lauren", //
			"Amber", //
			"Brittany", //
			"Danielle", //
			"Richard", //
			"Kimberly", //
			"Jeffrey", //
			"Amy", //
			"Crystal", //
			"Michelle", //
			"Tiffany", //
			"Jeremy", //
			"Benjamin", //
			"Mark", //
			"Emily", //
			"Aaron", //
			"Charles", //
			"Rebecca", //
			"Jacob", //
			"Stephen", //
			"Patrick", //
			"Sean", //
			"Erin", //
			"Zachary", //
			"Jamie", //
			"Kelly", //
			"Samantha", //
			"Nathan", //
			"Sara", //
			"Dustin", //
			"Paul", //
			"Angela", //
			"Tyler", //
			"Scott", //
			"Katherine", //
			"Andrea", //
			"Gregory", //
			"Erica", //
			"Mary", //
			"Travis", //
			"Lisa", //
			"Kenneth", //
			"Bryan", //
			"Lindsey", //
			"Kristen", //
			"Jose", //
			"Alexander", //
			"Jesse", //
			"Katie", //
			"Lindsay", //
			"Shannon", //
			"Vanessa", //
			"Courtney", //
			"Christine", //
			"Alicia", //
			"Cody", //
			"Allison", //
			"Bradley", //
			"Samuel" //
	};

	final public static String[] lastNames = { "Chung", //
			"Chen", //
			"Melton", //
			"Hill", //
			"Puckett", //
			"Song", //
			"Hamilton", //
			"Bender", //
			"Wagner", //
			"McLaughlin", //
			"McNamara", //
			"Raynor", //
			"Moon", //
			"Woodard", //
			"Desai", //
			"Wallace", //
			"Lawrence", //
			"Griffin", //
			"Dougherty", //
			"Powers", //
			"May", //
			"Steele", //
			"Teague", //
			"Vick", //
			"Gallagher", //
			"Solomon", //
			"Walsh", //
			"Monroe", //
			"Connolly", //
			"Hawkins", //
			"Middleton", //
			"Goldstein", //
			"Watts", //
			"Johnston", //
			"Weeks", //
			"Wilkerson", //
			"Barton", //
			"Walton", //
			"Hall", //
			"Ross", //
			"Woods", //
			"Mangum", //
			"Joseph", //
			"Rosenthal", //
			"Bowden", //
			"Underwood", //
			"Jones", //
			"Baker", //
			"Merritt", //
			"Cross", //
			"Cooper", //
			"Holmes", //
			"Sharpe", //
			"Morgan", //
			"Hoyle", //
			"Allen", //
			"Rich", //
			"Grant", //
			"Proctor", //
			"Diaz", //
			"Graham", //
			"Watkins", //
			"Hinton", //
			"Marsh", //
			"Hewitt", //
			"Branch", //
			"O'Brien", //
			"Case", //
			"Christensen", //
			"Parks", //
			"Hardin", //
			"Lucas", //
			"Eason", //
			"Davidson", //
			"Whitehead", //
			"Rose", //
			"Sparks", //
			"Moore", //
			"Pearson", //
			"Rodgers", //
			"Graves", //
			"Scarborough", //
			"Sutton", //
			"Sinclair", //
			"Bowman", //
			"Olsen", //
			"Love", //
			"McLean", //
			"Christian", //
			"Lamb", //
			"James", //
			"Chandler", //
			"Stout", //
			"Cowan", //
			"Golden", //
			"Bowling", //
			"Beasley", //
			"Clapp", //
			"Abrams", //
			"Tilley" //
	};
	
	protected PersonTaskAssignmentsFactory factory = PersonTaskAssignmentsFactory.eINSTANCE;

	int id = 1;
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
		if(skillTypes.containsKey(name))
			return skillTypes.get(name);
		
		SkillType type = factory.createSkillType();
		type.setName(name);
		type.setId(id++);
		skillTypes.put(name, type);
		return type;
	}

	public Collection<SkillType> addSkillType(String... names) {
		Collection<SkillType> st = new LinkedHashSet<>();
		for (String name : names) {
			st.add(addSkillType(name));
		}
		return st;
	}

	public Person addPerson(String name, double salary, double flexibility, double overtimeSalary,
			Map<String, Integer> skills) {
		if(persons.containsKey(name))
			return persons.get(name);
		
		Person p = factory.createPerson();
		p.setName(name);
		p.setId(id++);
		p.setSalary(salary);
		p.setOvertimeFlexibility(flexibility);
		p.setOvertimeBonus(overtimeSalary);
		skills.forEach((sName, sLevel) -> {
			addSkill(name, sLevel, sName);
		});
		persons.put(name, p);
		return p;
	}
	
	public Skill addSkill(String person, int level, String skillType) {
		Person p  = persons.get(person);
		Skill s = p.getSkills().stream()
			.filter(skill -> skill.getLevel() == level && skill.getType().getName().equals(skillType))
			.findAny()
			.orElseGet(() -> {
				Skill skill = factory.createSkill();
				SkillType type = skillTypes.get(skillType);
				if (type == null) {
					type = addSkillType(skillType);
				}
				skill.setName(type.getName());
				skill.setId(id++);
				skill.setType(type);
				skill.setLevel(level);
				p.getSkills().add(skill);
				return skill;
			});
		return s;
	}

	public Collection<Offer> addOffer(String person, int week, int... hours) {
		Collection<Offer> os = new LinkedHashSet<>();
		for (int hour : hours) {
			os.add(addOffer(person, week, hour));
		}
		persons.get(person).getOffers().addAll(os);
		return os;
	}
	
	public Offer addOffer(String person, int week, int hours) {
		Offer offer = factory.createOffer();
		offer.setId(id++);
		offer.setHours(hours);
		offer.setWeek(weeks.get(week));
		persons.get(person).getOffers().add(offer);
		return offer;
	}

	public Project addProject(String name, double reward, int weeksUntilLoss, double lossPerWeek, int start) {
		if(projects.containsKey(name))
			return projects.get(name);
		
		Project p = factory.createProject();
		p.setName(name);
		p.setId(id++);
		p.setWeeksUntilLoss(weeksUntilLoss);
		p.setInitialWeekNumber(start);
		projects.put(name, p);
		return p;
	}

	public Task addTask(String project, String name, String... previousTasks) {
		Task t = factory.createTask();
		t.setName(name);
		t.setId(id++);
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
		r.setId(id++);
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
			w.setId(id++);
			w.setNumber(i);
			weeks.put(i, w);
			ws.add(w);
			if (prev != null)
				w.setPrevious(prev);

			prev = w;
		}

		return ws;
	}
	
	public Week addWeek() {
		Week prev = null;
		if(!weeks.isEmpty()) {
			prev = weeks.values().stream().toList().getLast();
		}
		
		Week w = factory.createWeek();
		w.setId(id++);
		w.setNumber((prev == null) ? 1 : prev.getNumber()+1);
		if(prev != null) {
			w.setPrevious(prev);
		}
		weeks.put(w.getNumber(), w);
		
		return w;
	}
}
