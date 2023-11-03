package pta.example.house;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.SkillType;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;
import pta.generator.PTAModelGenerator;

public class HouseConstructionGenerator extends PTAModelGenerator {

	final static String[] firstNames = { //
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

	final static String[] lastNames = { "Chung", //
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

	final static double WORKHOURS_PER_DAY = 8;
	final static int WORKDAYS_PER_WEEK = 5;

	final static double HOURS_APPRENTICE = 20;
	final static double HOURS_JOURNEYMAN = 38;
	final static double HOURS_MASTER = 38;

	final static double FLEX_APPRENTICE = 1.1;
	final static double FLEX_JOURNEYMAN = 1.25;
	final static double FLEX_MASTER = 1.5;

	final static double SALARY_APPRENTICE = 30;
	final static double SALARY_JOURNEYMAN = 65;
	final static double SALARY_MASTER = 100;

	final static double BONUS_APPRENTICE = 65;
	final static double BONUS_JOURNEYMAN = 100;
	final static double BONUS_MASTER = 150;

	final static int SKILL_APPRENTICE = 1;
	final static int SKILL_JOURNEYMAN = 2;
	final static int SKILL_MASTER = 3;

	protected Random rnd;

	public static void main(String[] args) {
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		File iF = new File(instancesFolder);
		if (!iF.exists()) {
			iF.mkdirs();
		}

//		PersonTaskAssignmentModel model = new HouseConstructionGenerator("EpicSeed".hashCode()).constructProject1();
		PersonTaskAssignmentModel model = new HouseConstructionGenerator("EpicSeed".hashCode()).constructSimpleProject();

		try {
			save(model, instancesFolder + "./ConstructionSimpleProject.xmi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HouseConstructionGenerator(int seed) {
		rnd = new Random(seed);
	}
	
	public PersonTaskAssignmentModel constructSimpleProject() {
		addWeeks(1, 3);
		final String projectName = "Shed";
		final Project project = addProject(projectName, 10000, 3, 500, 1);
		
		// Tasks
		final Task basement = addTask(projectName, "Basement", new String[0]);
		final SkillType basementSkill = addSkillType("Concrete_work");
		addRequirement(project, basement, basementSkill, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 1 * 1);
		
		final Task shell = addTask(projectName, "Building_shell", basement.getName());
		final SkillType bricked = addSkillType("Bricking");
		final SkillType woodWork = addSkillType("Wood_work");
		addRequirement(project, shell, woodWork, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 1 * 1);
		addRequirement(project, shell, bricked, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 2 * 1);

		final Task roof = addTask(projectName, "Roof", shell.getName(), shell.getName());
		final SkillType roofSkill = addSkillType("Roof_construction");
		addRequirement(project, roof, roofSkill, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 2);
		
		// Persons
		createApprentices(1, 3, List.of(bricked.getName()));
		createJourneymen(1, 2, List.of(basementSkill.getName()));
		createMasters(1, 1, List.of(woodWork.getName(), roofSkill.getName()));
		
		return generate();
	}

	public PersonTaskAssignmentModel constructProject1() {
		addWeeks(1, 52);
		final String projectName = "ConstructHouse";
		Project project = addProject("ConstructHouse", 500000, 25, 10000, 1);
		Task aushub = addTask(projectName, "Aushub", new String[0]);
		SkillType baggern = addSkillType("Baggerfahren");
		SkillType graben = addSkillType("Erdarbeiten");
		addRequirement(project, aushub, baggern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 4 * 1);
		addRequirement(project, aushub, graben, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 4 * 1);
		addRequirement(project, aushub, graben, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 4 * 2);

		Task kanal = addTask(projectName, "Kanalarbeiten", new String[0]);
		SkillType klempnern = addSkillType("Klempnern");
		addRequirement(project, kanal, baggern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 3 * 1);
		addRequirement(project, kanal, klempnern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 3 * 1);
		addRequirement(project, kanal, klempnern, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 3 * 1);
		addRequirement(project, kanal, klempnern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 1 * 1);

		Task dachstuhl = addTask(projectName, "Dachstuhl", new String[0]);
		SkillType schreinern = addSkillType("Schreinern");
		addRequirement(project, dachstuhl, schreinern, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 20 * 1);
		addRequirement(project, dachstuhl, schreinern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 20 * 2);
		addRequirement(project, dachstuhl, schreinern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 20 * 1);

		Task fundament = addTask(projectName, "Fundament", aushub.getName(), kanal.getName());
		SkillType mischer = addSkillType("Betonmischerfahren");
		SkillType fundamente = addSkillType("Fundamentarbeiten");
		SkillType noop = addSkillType("NO_OP");
		addRequirement(project, fundament, mischer, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 1 * 1);
		addRequirement(project, fundament, fundamente, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 1 * 2);
		addRequirement(project, fundament, fundamente, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 1 * 1);
		addRequirement(project, fundament, noop, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 20);

		Task rohbau = addTask(projectName, "Rohbau", fundament.getName());
		SkillType mauern = addSkillType("Mauern");
		SkillType kran = addSkillType("Kranfahren");
		addRequirement(project, rohbau, kran, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 20 * 1);
		addRequirement(project, rohbau, mauern, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 20 * 2);
		addRequirement(project, rohbau, mauern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 20 * 5);
		addRequirement(project, rohbau, mauern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 20 * 1);

		Task dach = addTask(projectName, "Dachdecken", rohbau.getName(), dachstuhl.getName());
		SkillType dachdecken = addSkillType("Dachdecken");
		addRequirement(project, dach, kran, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 4 * 1);
		addRequirement(project, dach, dachdecken, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 4 * 2);
		addRequirement(project, dach, dachdecken, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 4 * 1);

		Task gws = addTask(projectName, "GasWasserAbwasser", dach.getName());
		addRequirement(project, gws, klempnern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 14 * 2);
		addRequirement(project, gws, klempnern, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 14 * 1);
		addRequirement(project, gws, klempnern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 4 * 1);

		Task strom = addTask(projectName, "Strom", dach.getName());
		SkillType elektro = addSkillType("Elektrikerarbeit");
		addRequirement(project, strom, elektro, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 8 * 1);
		addRequirement(project, strom, elektro, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 8 * 1);
		addRequirement(project, strom, elektro, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 8 * 1);

		Task fenster = addTask(projectName, "Fenster", dach.getName());
		SkillType fb = addSkillType("Fensterbau");
		addRequirement(project, fenster, fb, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 7 * 1);
		addRequirement(project, fenster, fb, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 7 * 2);
		addRequirement(project, fenster, fb, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 1 * 1);

		Task innen = addTask(projectName, "Innenausbau", gws.getName(), strom.getName(), fenster.getName());
		SkillType trockenbau = addSkillType("Trockenbau");
		SkillType malern = addSkillType("Malerarbeiten");
		SkillType fußboden = addSkillType("Fußbodenlegen");
		addRequirement(project, innen, trockenbau, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, innen, trockenbau, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 10 * 2);

		addRequirement(project, innen, malern, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, innen, malern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, innen, malern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 10 * 1);

		addRequirement(project, innen, fußboden, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, innen, fußboden, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, innen, fußboden, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 10 * 1);

		addRequirement(project, innen, schreinern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, innen, schreinern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 10 * 1);

		Task daemmung = addTask(projectName, "Daemmung", gws.getName(), strom.getName(), fenster.getName());
		SkillType daemmen = addSkillType("Daemmen");
		addRequirement(project, daemmung, daemmen, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 2 * 1);
		addRequirement(project, daemmung, daemmen, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 2 * 3);

		Task putz = addTask(projectName, "Verputzen", daemmung.getName());
		SkillType verputzen = addSkillType("Verputzen");
		addRequirement(project, putz, verputzen, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, putz, verputzen, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 10 * 2);
		addRequirement(project, putz, verputzen, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 10 * 1);

		addRequirement(project, putz, malern, WorkerLevel.APPRENTICE, WORKHOURS_PER_DAY * 10 * 1);
		addRequirement(project, putz, malern, WorkerLevel.JOURNEYMAN, WORKHOURS_PER_DAY * 10 * 2);
		addRequirement(project, putz, malern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 10 * 1);

		Task abnahme = addTask(projectName, "Abnahme", innen.getName(), putz.getName());
		SkillType architektur = addSkillType("Architektur");
		SkillType statik = addSkillType("Statik");
		addRequirement(project, abnahme, architektur, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 1 * 1);
		addRequirement(project, abnahme, statik, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 1 * 1);
		addRequirement(project, abnahme, elektro, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 1 * 1);
		addRequirement(project, abnahme, klempnern, WorkerLevel.MASTER, WORKHOURS_PER_DAY * 1 * 1);

//		Task endOfTasks = addTask(projectName, "END_OF_TASKS", abnahme.getName());

		// Create generic construction workers
		createApprentices(5, WORKDAYS_PER_WEEK, List.of(graben.getName(), fundamente.getName(), mauern.getName()));
		createJourneymen(5, WORKDAYS_PER_WEEK, List.of(graben.getName(), fundamente.getName(), mauern.getName()));
		createMasters(1, WORKDAYS_PER_WEEK, List.of(graben.getName(), fundamente.getName(), mauern.getName()));

		// Create machinists
		createJourneymen(2, WORKDAYS_PER_WEEK, List.of(baggern.getName(), mischer.getName(), kran.getName()));

		// Create carpenters and roofers
		createApprentices(4, WORKDAYS_PER_WEEK,
				List.of(schreinern.getName(), dachdecken.getName(), fußboden.getName()));
		createJourneymen(4, WORKDAYS_PER_WEEK, List.of(schreinern.getName(), dachdecken.getName(), fußboden.getName()));
		createMasters(2, WORKDAYS_PER_WEEK, List.of(schreinern.getName(), dachdecken.getName(), fußboden.getName()));

		// Create plumbers
		createApprentices(2, WORKDAYS_PER_WEEK, List.of(klempnern.getName()));
		createJourneymen(4, WORKDAYS_PER_WEEK, List.of(klempnern.getName()));
		createMasters(1, WORKDAYS_PER_WEEK, List.of(klempnern.getName()));

		// Create electricians
		createApprentices(2, WORKDAYS_PER_WEEK, List.of(elektro.getName()));
		createJourneymen(4, WORKDAYS_PER_WEEK, List.of(elektro.getName()));
		createMasters(1, WORKDAYS_PER_WEEK, List.of(elektro.getName()));

		// Create drywall builders
		createApprentices(2, WORKDAYS_PER_WEEK, List.of(trockenbau.getName(), fb.getName()));
		createJourneymen(4, WORKDAYS_PER_WEEK, List.of(trockenbau.getName(), fb.getName()));
		createMasters(2, WORKDAYS_PER_WEEK, List.of(trockenbau.getName(), fb.getName()));

		// Create painters
		createApprentices(2, WORKDAYS_PER_WEEK, List.of(daemmen.getName(), verputzen.getName(), malern.getName()));
		createJourneymen(4, WORKDAYS_PER_WEEK, List.of(daemmen.getName(), verputzen.getName(), malern.getName()));
		createMasters(2, WORKDAYS_PER_WEEK, List.of(daemmen.getName(), verputzen.getName(), malern.getName()));

		// Create architect
		createMasters(4, WORKDAYS_PER_WEEK, List.of(architektur.getName(), statik.getName()));

		// Create NO_OP
		createWorkers(1, 0.0, 10.0, 0.0, Map.of(noop.getName(), 1), 70, 7);

		return generate();
	}

	public void addRequirement(Project project, Task task, SkillType skill, WorkerLevel worker, double hours) {
		switch (worker) {
		case APPRENTICE:
			addRequirement(project.getName(), task.getName(), (int) hours, SKILL_APPRENTICE, skill.getName(),
					SALARY_APPRENTICE, BONUS_APPRENTICE);
			break;
		case JOURNEYMAN:
			addRequirement(project.getName(), task.getName(), (int) hours, SKILL_JOURNEYMAN, skill.getName(),
					SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
			break;
		case MASTER:
			addRequirement(project.getName(), task.getName(), (int) hours, SKILL_MASTER, skill.getName(), SALARY_MASTER,
					BONUS_MASTER);
			break;
		default:
			break;

		}

	}

	public void createApprentices(int num, int offersPerWeek, List<String> skills) {
		Map<String, Integer> skillLevels = new HashMap<>();
		for (String skill : skills) {
			skillLevels.put(skill, SKILL_APPRENTICE);
		}

		createWorkers(num, SALARY_APPRENTICE, FLEX_APPRENTICE, BONUS_APPRENTICE, skillLevels, HOURS_APPRENTICE,
				offersPerWeek);
	}

	public void createJourneymen(int num, int offersPerWeek, List<String> skills) {
		Map<String, Integer> skillLevels = new HashMap<>();
		for (String skill : skills) {
			skillLevels.put(skill, SKILL_JOURNEYMAN);
		}

		createWorkers(num, SALARY_JOURNEYMAN, FLEX_JOURNEYMAN, BONUS_JOURNEYMAN, skillLevels, HOURS_JOURNEYMAN,
				offersPerWeek);
	}

	public void createMasters(int num, int offersPerWeek, List<String> skills) {
		Map<String, Integer> skillLevels = new HashMap<>();
		for (String skill : skills) {
			skillLevels.put(skill, SKILL_MASTER);
		}

		createWorkers(num, SALARY_MASTER, FLEX_MASTER, BONUS_MASTER, skillLevels, HOURS_MASTER, offersPerWeek);
	}

	public void createWorkers(int num, double salary, double flexibility, double overtimeSalary,
			Map<String, Integer> skills, double hoursPerWeek, int offersPerWeek) {
		final int retries = 10;
		Set<String> names = new LinkedHashSet<>();
		for (int i = 0; i < num; i++) {
			StringBuilder sb = new StringBuilder();
			int t = 0;
			do {
				sb.append(firstNames[rnd.nextInt(firstNames.length)]);
				sb.append(" ");
				sb.append(lastNames[rnd.nextInt(lastNames.length)]);
				t++;
			} while ((persons.keySet().contains(sb.toString()) || names.contains(sb.toString())) && t <= retries);

			names.add(sb.toString());
		}

		for (String name : names) {
			Person p = addPerson(name, salary, flexibility, overtimeSalary, skills);
			for (Week week : weeks.values()) {
				for (int i = 0; i < offersPerWeek; i++) {
					addOffer(p.getName(), week.getNumber(), (int) (hoursPerWeek / offersPerWeek));
				}
			}

		}
	}
}

enum WorkerLevel {
	APPRENTICE, JOURNEYMAN, MASTER;
}
