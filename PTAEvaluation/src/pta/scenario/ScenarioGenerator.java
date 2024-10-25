package pta.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Requirement;
import PersonTaskAssignments.SkillType;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;
import pta.generator.PTAModelGenerator;

public class ScenarioGenerator extends PTAModelGenerator{
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
	
	public static DoubleRange mkRange(double lower, double upper) {
		return new DoubleRange(lower, upper);
	}
	
	public static IntRange mkRange(int lower, int upper) {
		return new IntRange(lower, upper);
	}
	
	protected Random rnd;
	protected Map<String, Map<Integer, List<Person>>> skillType2level2person;
	
	public IntRange nProjects = mkRange(1, 2);
	public IntRange tasksPerProject = mkRange(1, 2);
	public IntRange reqPerTask = mkRange(1, 2);
	public IntRange reqHours = mkRange(8, 9);
	public IntRange nSkills = mkRange(1, 2);
	public int skillLevels = 3;
	public double deadlineSlack = 1.25;
	public double planningHorizon = 2.0;
	public boolean superimposed = true;
	public double spawnRate = 0.5;
	public double offerSplitRate = 0.5;
	public double additionalOfferRate = 0.2;
	
	public ScenarioGenerator() {
		rnd = new Random();
	}
	
	public ScenarioGenerator(final int seed) {
		rnd = new Random(seed);
	}
	
	public PersonTaskAssignmentModel generate(final int seed) {
		rnd = new Random(seed);
		return this.generate();
	}
	
	public PersonTaskAssignmentModel generate() {
		skillType2level2person = new HashMap<>();
		
		int numOfSkillTypes = rnd.nextInt(nSkills.lower(),nSkills.upper());
		SkillType[] types = new SkillType[numOfSkillTypes];
		for(int i = 0; i < numOfSkillTypes; i++) {
			types[i] = addSkillType("SkillType_"+(i+1));
		}
		
		// Create requirements
		int numOfProjects = rnd.nextInt(nProjects.lower(), nProjects.upper());
		int initialWeek = 1;
		int currentWeek = initialWeek;
		Map<Project, Week> finalWeeks = new HashMap<>();
		Project previousProject = null;
		for(int noP = 1; noP <= numOfProjects; noP++) {
			int start = (superimposed || previousProject==null) ? 1 : previousProject.getInitialWeekNumber()+previousProject.getWeeksUntilLoss();
			Project currentProject = addProject("Project_"+noP, 0, 0, 0, start);
			
			int numOfTasks = rnd.nextInt(tasksPerProject.lower(), tasksPerProject.upper());
			if(superimposed) {
				currentWeek = initialWeek;
			}
			Task previousTask = null;
			for(int noT = 1; noT <= numOfTasks; noT++) {
				Task currentTask = (previousTask == null)? addTask(currentProject.getName(), "Task_"+noT) : addTask(currentProject.getName(), "Task_"+noT, previousTask.getName());
				
				int numOfRequirements = rnd.nextInt(reqPerTask.lower(),reqPerTask.upper());
				for(int noR = 1; noR <= numOfRequirements; noR++) {
					int skillLevel = rnd.nextInt(skillLevels)+1;
					String skillType = types[rnd.nextInt(numOfSkillTypes)].getName();
					
					addRequirement(currentProject.getName(), currentTask.getName(), 
							rnd.nextInt(reqHours.lower(), reqHours.upper()), skillLevel, skillType, 
							salary(skillLevel), overtimeSalary(skillLevel));
					
					if(!weeks.containsKey(currentWeek)) {
						addWeek();
					}
					
					currentWeek++;
					getOrSpawnPerson(skillType, skillLevel);
				}
				previousTask = currentTask;
			}
			finalWeeks.put(currentProject, weeks.get(currentWeek-1));
			
			int duration = currentWeek - start;
			currentProject.setWeeksUntilLoss((int)Math.ceil(duration*deadlineSlack));
		}
		
		// Create minimum amount of offers
		for(Project p : projects.values()) {
			int weekNo = p.getInitialWeekNumber();
			for(Task t : p.getTasks()) {
				for(Requirement r : t.getRequirements()) {
					Week week = weeks.get(weekNo);
					List<Person> persons = skillType2level2person.get(r.getSkillType().getName()).get(r.getSkillLevel());
					spawnOrSplitOffers(week, persons, r.getHours());
					weekNo++;
				}
			}
		}
		
		// Create weeks and offers beyond deadline + slack
		for(Project p : projects.values()) {
			Week finalWeek = finalWeeks.get(p);
			int upperLimit = p.getInitialWeekNumber() + (int) Math.ceil(p.getWeeksUntilLoss()*planningHorizon);
			for(int currentWeekNumber = finalWeek.getNumber(); currentWeekNumber < upperLimit; currentWeekNumber++) {
				Week current = weeks.get(currentWeekNumber);
				if(current == null) {
					current = addWeek();
				}
				for(Task t : p.getTasks()) {
					for(Requirement r : t.getRequirements()) {
						if(rnd.nextDouble() <= additionalOfferRate) {
							List<Person> persons = skillType2level2person.get(r.getSkillType().getName()).get(r.getSkillLevel());
							spawnOrSplitOffers(current, persons, r.getHours());
						}
					}
				}
			}
		}
		
		return super.generate();
	}
	
	public Person getOrSpawnPerson(final String skillType, int skillLevel) {
		Map<Integer, List<Person>> level2person = skillType2level2person.get(skillType);
		boolean spawned = false;
		if(level2person == null) {
			level2person = new HashMap<>();
			level2person.put(skillLevel, new ArrayList<>());
			skillType2level2person.put(skillType, level2person);
			
			Person person = null;
			if(rnd.nextDouble() <= spawnRate) {
				person = addPerson("Person_"+(persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
			} else {
				List<Person> candidates = persons.values().stream()
					.filter(p -> p.getSkills().stream().filter(s -> !s.getType().getName().equals(skillType)).findAny().isPresent())
					.collect(Collectors.toList());
				if(candidates.isEmpty()) {
					person = addPerson("Person_"+(persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
				} else {
					person = candidates.get(rnd.nextInt(candidates.size()));
					
				}
			}
			addSkill(person.getName(), skillLevel, skillType);
			level2person.get(skillLevel).add(person);
			return person;
		}
		List<Person> persons = level2person.get(skillLevel);
		if(persons == null) {
			persons = new ArrayList<>();
			level2person.put(skillLevel, persons);
			Person person = null;
			if(rnd.nextDouble() <= spawnRate) {
				person = addPerson("Person_"+(persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
			} else {
				List<Person> candidates = this.persons.values().stream()
						.filter(p -> p.getSkills().stream().filter(s -> !s.getType().getName().equals(skillType)).findAny().isPresent())
						.collect(Collectors.toList());
				if(candidates.isEmpty()) {
					person = addPerson("Person_"+(persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
				} else {
					person = candidates.get(rnd.nextInt(candidates.size()));
						
				}
			}
			addSkill(person.getName(), skillLevel, skillType);
			persons.add(person);
			return person;
		}
		
		if(rnd.nextDouble() <= spawnRate && !spawned) {
			Person person = addPerson("Person_"+(persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
			addSkill(person.getName(), skillLevel, skillType);
			persons.add(person);
			return person;
		} else {
			return persons.get(rnd.nextInt(persons.size()));
		}
	}
	
	public void spawnOrSplitOffers(final Week week, final List<Person> persons, int hours) {
		if(rnd.nextDouble() <= offerSplitRate && persons.size() > 1 && hours >= persons.size()) {
			List<Person> list1 = new ArrayList<>();
			List<Person> list2 = new ArrayList<>();
			Collections.shuffle(persons);
			int idx = 0;
			for(Person person : persons) {
				if(idx % 2 == 0) {
					list1.add(person);
				} else {
					list2.add(person);
				}
				idx++;
			}
			if(!list1.isEmpty()) {
				spawnOrSplitOffers(week, list1, (int) Math.ceil(hours/2.0));
			}
			if(!list2.isEmpty()) {
				spawnOrSplitOffers(week, list2, (int) Math.ceil(hours/2.0));
			}
		} else {
			Person p = persons.get(rnd.nextInt(persons.size()));
			addOffer(p.getName(), week.getNumber(), hours);
		}
	}
	
	public double flexibility(int skillLevel) {
		return 1.0 + 0.25*(skillLevel-1);
	}
	
	public double salary(int skillLevel) {
		return skillLevel * 50;
	}
	
	public double overtimeSalary(int skillLevel) {
		return salary(skillLevel) * 1.5;
	}
	
}

interface Range <N extends Number>{
	N getLower();
	N getUpper();
}

record DoubleRange(double lower, double upper) implements Range<Double> {
	@Override
	public Double getLower() {
		return lower;
	}

	@Override
	public Double getUpper() {
		return upper;
	}
}

record IntRange(int lower, int upper) implements Range<Integer> {
	@Override
	public Integer getLower() {
		return lower;
	}

	@Override
	public Integer getUpper() {
		return upper;
	}
}
