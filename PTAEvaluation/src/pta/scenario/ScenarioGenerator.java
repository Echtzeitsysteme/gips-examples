package pta.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Requirement;
import PersonTaskAssignments.SkillType;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;
import pta.generator.PTAModelGenerator;

public class ScenarioGenerator extends PTAModelGenerator{
	
	public static DoubleRange mkRange(double lower, double upper) {
		return new DoubleRange(lower, upper);
	}
	
	public static IntRange mkRange(int lower, int upper) {
		return new IntRange(lower, upper);
	}
	
	protected Random rnd;
	protected Map<String, Map<Integer, List<Person>>> skillType2level2person;
	SkillType[] types;
	
	public IntRange nProjects = mkRange(1, 2);
	public IntRange tasksPerProject = mkRange(1, 2);
	public IntRange reqPerTask = mkRange(1, 2);
	public IntRange reqHours = mkRange(8, 9);
	public int skillLevels = 3;
	public double deadlineSlack = 1.25;
	public double planningHorizon = 2.0;
	public boolean superimposed = true;
	public double personSpawnRate = 0.5;
	public double skillTypeSpawnRate = 0.5;
	public double offerSplitRate = 0.5;
	public double additionalOfferRate = 0.2;
	
	public ScenarioGenerator() {
		rnd = new Random();
	}
	
	public ScenarioGenerator(final int seed) {
		rnd = new Random(seed);
	}
	
	public void scale(int scaling, int nP, double pSpread, int nTpP, double tSpread, int nRpT, double rSpread) {
		int nPLower = nP*scaling;
		int nPUpper = nPLower + (int)Math.ceil(nPLower*pSpread) + 1;
		nProjects = mkRange(nPLower, nPUpper);
		int nTpPUpper = nTpP + (int)Math.ceil(nTpP*tSpread) + 1;
		tasksPerProject = mkRange(nTpP, nTpPUpper);
		int nRpTUpper = nRpT + (int)Math.ceil(nRpT*rSpread) + 1;
		reqPerTask = mkRange(nRpT, nRpTUpper);
	}
	
	public PersonTaskAssignmentModel generate(final int seed) {
		rnd = new Random(seed);
		return this.generate();
	}
	
	public PersonTaskAssignmentModel generate() {
		skillType2level2person = new HashMap<>();
		createSkillTypes();
		
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
					SkillType skillType = types[rnd.nextInt(types.length)];
					
//					SkillType skillType = null;
//					if(rnd.nextDouble() <= skillTypeSpawnRate) {
//						skillType = addSkillType("SkillType_"+(skillTypes.size()+1));
//					} else {
//						skillType = Lists.newArrayList(skillTypes.values()).get(rnd.nextInt(skillTypes.size()));
//						
//					}
					
					addRequirement(currentProject.getName(), currentTask.getName(), 
							rnd.nextInt(reqHours.lower(), reqHours.upper()), skillLevel, skillType.getName(), 
							salary(skillLevel), overtimeSalary(skillLevel));
					
					if(!weeks.containsKey(currentWeek)) {
						addWeek();
					}
					
					currentWeek++;
					getOrSpawnPerson(skillType.getName(), skillLevel);
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
		double salary = salary(skillLevel);
		
		// Case 1: The skill with the given type does not exist at all
		if(level2person == null) {
			level2person = new HashMap<>();
			level2person.put(skillLevel, new ArrayList<>());
			skillType2level2person.put(skillType, level2person);
			
			// Case 1.1: Spawn a new person
			Person person = null;
			if(rnd.nextDouble() <= personSpawnRate) {
				person = addPerson("Person_"+(this.persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
			} else {
				List<Person> candidates = this.persons.values().stream().filter(p->p.getSalary()==salary).collect(Collectors.toList());
				// Case 1.2: No persons exist, spawn a new person
				if(candidates.isEmpty()) {
					person = addPerson("Person_"+(this.persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
				} else {	// Case 1.3: Persons exist, pick one at random if salary is suitable, else create a new person.
					person = candidates.get(rnd.nextInt(candidates.size()));
				}
			}
			
			// Add skill with the required type anyways, since it didn't exist in the first place.
			addSkill(person.getName(), skillLevel, skillType);
			level2person.get(skillLevel).add(person);
			return person;
		}
		
		// Case 2: A skill with the type exists, but not with the adequate level.
		List<Person> persons = level2person.get(skillLevel);
		if(persons == null) {
			persons = new ArrayList<>();
			level2person.put(skillLevel, persons);
			
			// Case 2.1: Spawn a new person
			Person person = null;
			if(rnd.nextDouble() <= personSpawnRate) {
				person = addPerson("Person_"+(this.persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
			} else {
				// Find persons that match the salary and do not have a skill with the required type, to prevent a person having multiple skill of the same skill type.
				List<Person> candidates = this.persons.values().stream()
						.filter(p->p.getSalary()==salary)
						.filter(p -> !p.getSkills().stream().filter(s -> s.getType().getName().equals(skillType)).findAny().isPresent())
						.collect(Collectors.toList());
				// Case 2.2: No person with the requirement as described above exists -> spawn a new person.
				if(candidates.isEmpty()) {
					person = addPerson("Person_"+(this.persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
				} else { 	// Case 2.2: A person with the requirement as described above exists -> pick one at random.
					person = candidates.get(rnd.nextInt(candidates.size()));
						
				}
			}
			
			// Add skill with the required type anyways, since it didn't exist in the first place.
			addSkill(person.getName(), skillLevel, skillType);
			persons.add(person);
			return person;
		}
		
		// Case 3.1: A suitable person exists, well spawn a new one anyways.
		if(rnd.nextDouble() <= personSpawnRate) {
			Person person = addPerson("Person_"+(this.persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
			addSkill(person.getName(), skillLevel, skillType);
			persons.add(person);
			return person;
		} else { // Case 3.2: A suitable person exists -> return the person if the salary matches, else create a new person.
			List<Person> candidates = persons.stream().filter(p->p.getSalary()==salary).collect(Collectors.toList());
			if(candidates.isEmpty()) {
				return addPerson("Person_"+(this.persons.size()+1), salary(skillLevel), flexibility(skillLevel), overtimeSalary(skillLevel), new HashMap<>());
			} else {
				return candidates.get(rnd.nextInt(candidates.size()));
			}
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
			
			double divisor = (list1.isEmpty() || list2.isEmpty()) ? 1.0 : 2.0;
			if(!list1.isEmpty()) {
				spawnOrSplitOffers(week, list1, (int) Math.ceil(hours/divisor));
			}
			if(!list2.isEmpty()) {
				spawnOrSplitOffers(week, list2, (int) Math.ceil(hours/divisor));
			}
		} else {
			Person p = persons.get(rnd.nextInt(persons.size()));
			addOffer(p.getName(), week.getNumber(), hours);
		}
	}
	
	public void createSkillTypes() {
		double avgReqs = expectedValue(nProjects) * expectedValue(tasksPerProject) * expectedValue(reqPerTask) * skillTypeSpawnRate;
		double upper = (nProjects.getUpper()-1) * (tasksPerProject.getUpper()-1) * (reqPerTask.getUpper()-1) * skillTypeSpawnRate;
		double lower = nProjects.getLower() * tasksPerProject.getLower() * reqPerTask.getLower() * skillTypeSpawnRate;
		double G = rnd.nextGaussian(avgReqs, (upper-lower)/2);
		int nTypes = (int) Math.ceil(Math.clamp(G, lower, upper)); 
		
		types = new SkillType[nTypes];
		for(int i = 0; i < nTypes; i++) {
			types[i] = addSkillType("SkillType_"+(i+1));
		}
	}
	
	public double expectedValue(IntRange range) {
		double n = range.upper()-range.lower();
		double sum = 0;
		for(int i = range.lower(); i < range.upper(); i++) {
			sum += i;
		}
		return sum/n;
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
