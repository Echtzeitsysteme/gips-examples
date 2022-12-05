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
	
	final static double HOURS_APPRENTICE = 16;
	final static double HOURS_JOURNYMAN = 38;
	final static double HOURS_MASTER = 50;
	
	final static double SALARY_APPRENTICE = 30;
	final static double SALARY_JOURNYMAN = 65;
	final static double SALARY_MASTER = 100;
	
	final static double BONUS_APPRENTICE = 65;
	final static double BONUS_JOURNYMAN = 100;
	final static double BONUS_MASTER = 150;
	
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
		addRequirement(projectName, aushub.getName(), 30, 2, baggern.getName(), SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, aushub.getName(), 30, 1, graben.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
			
		Task kanal = addTask(projectName, "Kanalarbeiten", new String[0]);
		SkillType klempnern = addSkillType("Klempnern");
		addRequirement(projectName, kanal.getName(), 8, 2, baggern.getName(), SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, kanal.getName(), 8, 1, graben.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, kanal.getName(), 8, 2, klempnern.getName(), SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, kanal.getName(), 4, 3, klempnern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task dachstuhl = addTask(projectName, "Dachstuhl", new String[0]);
		SkillType schreinern = addSkillType("Schreinern");
		addRequirement(projectName, dachstuhl.getName(), 120, 1, schreinern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, dachstuhl.getName(), 120, 2, schreinern.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, dachstuhl.getName(), 40, 3, schreinern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task fundament = addTask(projectName, "Fundament", aushub.getName(), kanal.getName());
		SkillType mischer = addSkillType("Betonmischerfahren");
		SkillType fundamente = addSkillType("Fundamentarbeiten");
		SkillType noop = addSkillType("NO_OP");
		addRequirement(projectName, fundament.getName(), 20, 2, mischer.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, fundament.getName(), 20, 2, fundamente.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, fundament.getName(), 8, 3, fundamente.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, fundament.getName(), 20*24, 1, noop.getName(), 0, 0);
		
		Task rohbau = addTask(projectName, "Rohbau", fundament.getName());
		SkillType mauern = addSkillType("Mauern");
		SkillType kran = addSkillType("Kranfahren");
		addRequirement(projectName, rohbau.getName(), 40, 1, mauern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, rohbau.getName(), 100, 2, mauern.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, rohbau.getName(), 40, 3, mauern.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, rohbau.getName(), 100, 2, kran.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		
		Task dach = addTask(projectName, "Dachdecken", rohbau.getName(), dachstuhl.getName());
		SkillType dachdecken = addSkillType("Dachdecken");
		addRequirement(projectName, dach.getName(), 70, 2, dachdecken.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, dach.getName(), 20, 3, dachdecken.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, dach.getName(), 70, 2, kran.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		
		Task gws = addTask(projectName, "GasWasserAbwasser", dach.getName());
		addRequirement(projectName, gws.getName(), 8, 2, klempnern.getName(), SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, gws.getName(), 4, 3, klempnern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task strom = addTask(projectName, "Strom", dach.getName());
		SkillType elektro = addSkillType("Elektrikerarbeit");
		addRequirement(projectName, strom.getName(), 40, 1, elektro.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, strom.getName(), 100, 2, elektro.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, strom.getName(), 40, 3, elektro.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task fenster = addTask(projectName, "Fenster", dach.getName());
		SkillType fb = addSkillType("Fensterbau");
		addRequirement(projectName, fenster.getName(), 40, 1, fb.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, fenster.getName(), 100, 2, fb.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		
		Task innen = addTask(projectName, "Innenausbau", gws.getName(), strom.getName(), fenster.getName());
		SkillType trockenbau = addSkillType("Trockenbau");
		SkillType malern = addSkillType("Malerarbeiten");
		SkillType fußboden = addSkillType("Fußbodenlegen");
		addRequirement(projectName, innen.getName(), 40, 1, trockenbau.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, 2, trockenbau.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, innen.getName(), 40, 1, malern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, 2, malern.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, innen.getName(), 40, 1, fußboden.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, 2, fußboden.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, innen.getName(), 40, 1, schreinern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, innen.getName(), 100, 2, schreinern.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		
		Task daemmung = addTask(projectName, "Daemmung", gws.getName(), strom.getName(), fenster.getName());
		SkillType daemmen= addSkillType("Daemmen");
		addRequirement(projectName, daemmung.getName(), 40, 1, daemmen.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, daemmung.getName(), 100, 2, daemmen.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		
		Task putz = addTask(projectName, "Verputzen", daemmung.getName());
		SkillType verputzen = addSkillType("Verputzen");
		addRequirement(projectName, putz.getName(), 40, 1, malern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, putz.getName(), 100, 2, malern.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, putz.getName(), 40, 1, verputzen.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, putz.getName(), 100, 2, verputzen.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		
		Task abnahme = addTask(projectName, "Abnahme", innen.getName(), putz.getName());
		SkillType architektur = addSkillType("Architektur");
		SkillType statik = addSkillType("Statik");
		addRequirement(projectName, abnahme.getName(), 40, 3, architektur.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, abnahme.getName(), 40, 3, statik.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, abnahme.getName(), 40, 3, elektro.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, abnahme.getName(), 40, 3, klempnern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		// Create generic construction workers
		createWorkers(5, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	graben.getName(), 2,
						fundamente.getName(), 2,
						mauern.getName(), 2), 
				40, 5);
		
		// Create machinists
		createWorkers(2, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	baggern.getName(), 2,
						mischer.getName(), 2,
						kran.getName(), 2), 
				40, 5);
		
		// Create carpenters and roofers
		createWorkers(4, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	schreinern.getName(), 2,
						dachdecken.getName(), 2,
						fußboden.getName(), 2),
				40, 5);
		
		// Create plumbers
		createWorkers(4, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	klempnern.getName(), 2,
						dachdecken.getName(), 2),
				40, 5);

		// Create electricians
		createWorkers(2, SALARY_APPRENTICE, 1.1, BONUS_APPRENTICE, 
				Map.of(	elektro.getName(), 1),
				40, 5);
		createWorkers(4, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	elektro.getName(), 2),
				40, 5);
		createWorkers(1, SALARY_MASTER, 1.1, BONUS_MASTER, 
				Map.of(	elektro.getName(), 3),
				40, 5);	
		
		// Create drywall builders
		createWorkers(4, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	trockenbau.getName(), 2,
						fb.getName(), 2),
				40, 5);
		
		// Create painters
		createWorkers(4, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	daemmen.getName(), 2,
						verputzen.getName(), 2,
						malern.getName(), 2),
				40, 5);
		
		// Create architect
		createWorkers(4, SALARY_JOURNYMAN, 1.1, BONUS_JOURNYMAN, 
				Map.of(	architektur.getName(), 3,
						statik.getName(), 3),
				40, 5);
		
		return generate();
	}
	
	public void createWorkers(int num, double salary, double flexibility, double overtimeSalary, Map<String, Integer> skills, int hoursPerWeek, int offersPerWeek) {
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
					addOffer(p.getName(), week.getNumber(), hoursPerWeek/offersPerWeek);
				}
			}
			
		}
	}
}
