package pta.example.house;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import pta.generator.PTAModelGenerator;

public class HouseConstructionGenerator extends PTAModelGenerator{
	
	final static String[] firstNames = { 
			"Michael",
			"Christopher",
			"Jessica",
			"Matthew",
			"Ashley",
			"Jennifer",
			"Joshua",
			"Amanda",
			"Daniel",
			"David",
			"James",
			"Robert",
			"John",
			"Joseph",
			"Andrew",
			"Ryan",
			"Brandon",
			"Jason",
			"Justin",
			"Sarah",
			"William",
			"Jonathan",
			"Stephanie",
			"Brian",
			"Nicole",
			"Nicholas",
			"Anthony",
			"Heather",
			"Eric",
			"Elizabeth",
			"Adam",
			"Megan",
			"Melissa",
			"Kevin",
			"Steven",
			"Thomas",
			"Timothy",
			"Christina",
			"Kyle",
			"Rachel",
			"Laura",
			"Lauren",
			"Amber",
			"Brittany",
			"Danielle",
			"Richard",
			"Kimberly",
			"Jeffrey",
			"Amy",
			"Crystal",
			"Michelle",
			"Tiffany",
			"Jeremy",
			"Benjamin",
			"Mark",
			"Emily",
			"Aaron",
			"Charles",
			"Rebecca",
			"Jacob",
			"Stephen",
			"Patrick",
			"Sean",
			"Erin",
			"Zachary",
			"Jamie",
			"Kelly",
			"Samantha",
			"Nathan",
			"Sara",
			"Dustin",
			"Paul",
			"Angela",
			"Tyler",
			"Scott",
			"Katherine",
			"Andrea",
			"Gregory",
			"Erica",
			"Mary",
			"Travis",
			"Lisa",
			"Kenneth",
			"Bryan",
			"Lindsey",
			"Kristen",
			"Jose",
			"Alexander",
			"Jesse",
			"Katie",
			"Lindsay",
			"Shannon",
			"Vanessa",
			"Courtney",
			"Christine",
			"Alicia",
			"Cody",
			"Allison",
			"Bradley",
			"Samuel"
	};
	
	final static String[] lastNames = { 
			"Chung",
			"Chen",
			"Melton",
			"Hill",
			"Puckett",
			"Song",
			"Hamilton",
			"Bender",
			"Wagner",
			"McLaughlin",
			"McNamara",
			"Raynor",
			"Moon",
			"Woodard",
			"Desai",
			"Wallace",
			"Lawrence",
			"Griffin",
			"Dougherty",
			"Powers",
			"May",
			"Steele",
			"Teague",
			"Vick",
			"Gallagher",
			"Solomon",
			"Walsh",
			"Monroe",
			"Connolly",
			"Hawkins",
			"Middleton",
			"Goldstein",
			"Watts",
			"Johnston",
			"Weeks",
			"Wilkerson",
			"Barton",
			"Walton",
			"Hall",
			"Ross",
			"Woods",
			"Mangum",
			"Joseph",
			"Rosenthal",
			"Bowden",
			"Underwood",
			"Jones",
			"Baker",
			"Merritt",
			"Cross",
			"Cooper",
			"Holmes",
			"Sharpe",
			"Morgan",
			"Hoyle",
			"Allen",
			"Rich",
			"Grant",
			"Proctor",
			"Diaz",
			"Graham",
			"Watkins",
			"Hinton",
			"Marsh",
			"Hewitt",
			"Branch",
			"O'Brien",
			"Case",
			"Christensen",
			"Parks",
			"Hardin",
			"Lucas",
			"Eason",
			"Davidson",
			"Whitehead",
			"Rose",
			"Sparks",
			"Moore",
			"Pearson",
			"Rodgers",
			"Graves",
			"Scarborough",
			"Sutton",
			"Sinclair",
			"Bowman",
			"Olsen",
			"Love",
			"McLean",
			"Christian",
			"Lamb",
			"James",
			"Chandler",
			"Stout",
			"Cowan",
			"Golden",
			"Bowling",
			"Beasley",
			"Clapp",
			"Abrams",
			"Tilley"
	};
	
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
		if(!iF.exists()) {
			iF.mkdirs();
		}
		
		PersonTaskAssignmentModel model = new HouseConstructionGenerator("EpicSeed".hashCode()).constructProject1();
		
		try {
			save(model, instancesFolder + "/ConstructionProject1.xmi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HouseConstructionGenerator(int seed) {
		rnd = new Random(seed);
	}
	
	public PersonTaskAssignmentModel constructProject1(){
		addWeeks(1, 52);
		final String projectName = "ConstructHouse";
		addProject("ConstructHouse", 500000, 25, 10000, 1);
		Task aushub = addTask(projectName, "Aushub", new String[0]);
		SkillType baggern = addSkillType("Baggerfahren");
		SkillType graben = addSkillType("Erdarbeiten");
		addRequirement(projectName, aushub.getName(), 30, 2, baggern.getName(), SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, aushub.getName(), 30, 1, graben.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
			
		Task kanal = addTask(projectName, "Kanalarbeiten", new String[0]);
		SkillType klempnern = addSkillType("Klempnern");
		addRequirement(projectName, kanal.getName(), 8, SKILL_JOURNEYMAN, baggern.getName(), SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, kanal.getName(), 8, 1, graben.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, kanal.getName(), 8, SKILL_JOURNEYMAN, klempnern.getName(), SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, kanal.getName(), 4, 3, klempnern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task dachstuhl = addTask(projectName, "Dachstuhl", new String[0]);
		SkillType schreinern = addSkillType("Schreinern");
		addRequirement(projectName, dachstuhl.getName(), 120, 1, schreinern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, dachstuhl.getName(), 120, SKILL_JOURNEYMAN, schreinern.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, dachstuhl.getName(), 40, 3, schreinern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task fundament = addTask(projectName, "Fundament", aushub.getName(), kanal.getName());
		SkillType mischer = addSkillType("Betonmischerfahren");
		SkillType fundamente = addSkillType("Fundamentarbeiten");
		SkillType noop = addSkillType("NO_OP");
		addRequirement(projectName, fundament.getName(), 20, SKILL_JOURNEYMAN, mischer.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, fundament.getName(), 20, SKILL_JOURNEYMAN, fundamente.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, fundament.getName(), 8, 3, fundamente.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, fundament.getName(), 20*24, 1, noop.getName(), 0, 0);
		
		Task rohbau = addTask(projectName, "Rohbau", fundament.getName());
		SkillType mauern = addSkillType("Mauern");
		SkillType kran = addSkillType("Kranfahren");
		addRequirement(projectName, rohbau.getName(), 40, 1, mauern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, rohbau.getName(), 100, SKILL_JOURNEYMAN, mauern.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, rohbau.getName(), 40, 3, mauern.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, rohbau.getName(), 100, SKILL_JOURNEYMAN, kran.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		
		Task dach = addTask(projectName, "Dachdecken", rohbau.getName(), dachstuhl.getName());
		SkillType dachdecken = addSkillType("Dachdecken");
		addRequirement(projectName, dach.getName(), 70, SKILL_JOURNEYMAN, dachdecken.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, dach.getName(), 20, 3, dachdecken.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, dach.getName(), 70, SKILL_JOURNEYMAN, kran.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		
		Task gws = addTask(projectName, "GasWasserAbwasser", dach.getName());
		addRequirement(projectName, gws.getName(), 8, SKILL_JOURNEYMAN, klempnern.getName(), SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, gws.getName(), 4, 3, klempnern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task strom = addTask(projectName, "Strom", dach.getName());
		SkillType elektro = addSkillType("Elektrikerarbeit");
		addRequirement(projectName, strom.getName(), 40, 1, elektro.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, strom.getName(), 100, SKILL_JOURNEYMAN, elektro.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, strom.getName(), 40, 3, elektro.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task fenster = addTask(projectName, "Fenster", dach.getName());
		SkillType fb = addSkillType("Fensterbau");
		addRequirement(projectName, fenster.getName(), 40, 1, fb.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, fenster.getName(), 100, SKILL_JOURNEYMAN, fb.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		
		Task innen = addTask(projectName, "Innenausbau", gws.getName(), strom.getName(), fenster.getName());
		SkillType trockenbau = addSkillType("Trockenbau");
		SkillType malern = addSkillType("Malerarbeiten");
		SkillType fußboden = addSkillType("Fußbodenlegen");
		addRequirement(projectName, innen.getName(), 40, 1, trockenbau.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, SKILL_JOURNEYMAN, trockenbau.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, innen.getName(), 40, 1, malern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, SKILL_JOURNEYMAN, malern.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, innen.getName(), 40, 1, fußboden.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, SKILL_JOURNEYMAN, fußboden.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, innen.getName(), 40, 1, schreinern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, SKILL_JOURNEYMAN, schreinern.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		
		Task daemmung = addTask(projectName, "Daemmung", gws.getName(), strom.getName(), fenster.getName());
		SkillType daemmen= addSkillType("Daemmen");
		addRequirement(projectName, daemmung.getName(), 40, 1, daemmen.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, daemmung.getName(), 100, SKILL_JOURNEYMAN, daemmen.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		
		Task putz = addTask(projectName, "Verputzen", daemmung.getName());
		SkillType verputzen = addSkillType("Verputzen");
		addRequirement(projectName, putz.getName(), 40, 1, malern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, putz.getName(), 100, SKILL_JOURNEYMAN, malern.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		addRequirement(projectName, putz.getName(), 40, 1, verputzen.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, putz.getName(), 100, SKILL_JOURNEYMAN, verputzen.getName(),  SALARY_JOURNEYMAN, BONUS_JOURNEYMAN);
		
		Task abnahme = addTask(projectName, "Abnahme", innen.getName(), putz.getName());
		SkillType architektur = addSkillType("Architektur");
		SkillType statik = addSkillType("Statik");
		addRequirement(projectName, abnahme.getName(), 40, 3, architektur.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, abnahme.getName(), 40, 3, statik.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, abnahme.getName(), 40, 3, elektro.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, abnahme.getName(), 40, 3, klempnern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		// Create generic construction workers
		createApprentices(5, 5,
				List.of(graben.getName(),
						fundamente.getName(),
						mauern.getName()));
		createJourneymen(5, 5, 
				List.of(graben.getName(),
						fundamente.getName(),
						mauern.getName()));
		createMasters(1, 5, 
				List.of(graben.getName(),
						fundamente.getName(),
						mauern.getName()));
		
		// Create machinists
		createJourneymen(2, 5, 
				List.of(baggern.getName(),
						mischer.getName(),
						kran.getName()));
		
		// Create carpenters and roofers
		createApprentices(4, 5, 
				List.of(schreinern.getName(),
						dachdecken.getName(),
						fußboden.getName()));
		createJourneymen(4, 5, 
				List.of(schreinern.getName(),
						dachdecken.getName(),
						fußboden.getName()));
		createMasters(2, 5, 
				List.of(schreinern.getName(),
						dachdecken.getName(),
						fußboden.getName()));
		
		// Create plumbers
		createApprentices(2, 5, 
				List.of(klempnern.getName()));
		createJourneymen(4, 5, 
				List.of(klempnern.getName()));
		createMasters(1, 5, 
				List.of(klempnern.getName()));

		// Create electricians
		createApprentices(2, 5,
				List.of(elektro.getName()));
		createJourneymen(4, 5, 
				List.of(elektro.getName()));
		createMasters(1, 5, 
				List.of(elektro.getName()));
		
		// Create drywall builders
		createApprentices(2, 5, 
				List.of(trockenbau.getName(),
						fb.getName()));
		createJourneymen(4, 5, 
				List.of(trockenbau.getName(),
						fb.getName()));
		createMasters(2, 5, 
				List.of(trockenbau.getName(),
						fb.getName()));
		
		// Create painters
		createApprentices(2, 5, 
				List.of(daemmen.getName(),
						verputzen.getName(),
						malern.getName()));
		createJourneymen(4, 5, 
				List.of(daemmen.getName(),
						verputzen.getName(),
						malern.getName()));
		createMasters(2, 5, 
				List.of(daemmen.getName(),
						verputzen.getName(),
						malern.getName()));
		
		// Create architect
		createMasters(4, 5, 
				List.of(architektur.getName(),
						statik.getName()));
		
		// Create NO_OP
		createWorkers(1, 0.0, 10.0, 0.0, Map.of(noop.getName(), 1), 70, 7);
		
		return generate();
	}
	
	public void createApprentices(int num, int offersPerWeek, List<String> skills) {
		Map<String, Integer> skillLevels = new HashMap<>();
		for(String skill : skills) {
			skillLevels.put(skill, SKILL_APPRENTICE);
		}
		
		createWorkers(num, SALARY_APPRENTICE, FLEX_APPRENTICE, BONUS_APPRENTICE, skillLevels, HOURS_APPRENTICE, offersPerWeek);
	}
	
	public void createJourneymen(int num, int offersPerWeek, List<String> skills) {
		Map<String, Integer> skillLevels = new HashMap<>();
		for(String skill : skills) {
			skillLevels.put(skill, SKILL_JOURNEYMAN);
		}
		
		createWorkers(num, SALARY_JOURNEYMAN, FLEX_JOURNEYMAN, BONUS_JOURNEYMAN, skillLevels, HOURS_JOURNEYMAN, offersPerWeek);
	}
	
	public void createMasters(int num, int offersPerWeek, List<String> skills) {
		Map<String, Integer> skillLevels = new HashMap<>();
		for(String skill : skills) {
			skillLevels.put(skill, SKILL_MASTER);
		}
		
		createWorkers(num, SALARY_MASTER, FLEX_MASTER, BONUS_MASTER, skillLevels, HOURS_MASTER, offersPerWeek);
	}
	
	public void createWorkers(int num, double salary, double flexibility, double overtimeSalary, Map<String, Integer> skills, double hoursPerWeek, int offersPerWeek) {
		final int retries = 10;
		Set<String> names = new LinkedHashSet<>();
		for(int i = 0; i<num; i++) {
			StringBuilder sb = new StringBuilder();
			int t = 0;
			do {
				sb.append(firstNames[rnd.nextInt(firstNames.length)]);
				sb.append(" ");
				sb.append(lastNames[rnd.nextInt(lastNames.length)]);
				t++;
			} while((persons.keySet().contains(sb.toString()) || names.contains(sb.toString())) && t <= retries);
			
			names.add(sb.toString());
		}
		
		for(String name : names) {
			Person p = addPerson(name, salary, flexibility, overtimeSalary, skills);
			for(Week week : weeks.values()) {
				for(int i = 0; i<offersPerWeek; i++) {
					addOffer(p.getName(), week.getNumber(), (int)(hoursPerWeek/offersPerWeek));
				}
			}
			
		}
	}
}
