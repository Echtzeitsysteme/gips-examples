package pta.scenario;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.emoflon.gips.core.ilp.ILPSolverStatus;

import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Requirement;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;
import pta.evaluation.util.SolverOutput;

public class ScenarioValidator {
	
	final protected PersonTaskAssignmentModel model;
	final protected ValidationLogger logger = new ValidationLogger();
	final protected SolverOutput output;
	
	protected int numberOfProjects = 0;
	protected int numberOfTasks = 0;
	protected int numberOfRequirements = 0;
	protected int numberOfOffers = 0;
	protected int numberOfWeeks = 0;
	protected int numberOfPersons = 0;
	
	protected double successRate = 0.0;
	
	public ScenarioValidator(final PersonTaskAssignmentModel model, final SolverOutput output) {
		this.model = model;
		this.output = output;
	}


	public boolean validate() {
		try {
			validateWeeks();
		} catch(Exception e) {
			logger.addError("Exception during validation occurred: " + e.getMessage());
		}
		try {
			validateProjects();
		} catch(Exception e) {
			logger.addError("Exception during validation occurred: " + e.getMessage());
		}
		try {
			validatePersons();
		} catch(Exception e) {
			logger.addError("Exception during validation occurred: " + e.getMessage());
		}
		try {
			validateOutput();
		} catch(Exception e) {
			logger.addError("Exception during validation occurred: " + e.getMessage());
		}
		return !logger.hasErrors();
	}
	
	protected void validateOutput() {
		if(output.isOptimal()) {
			logger.addInfo("**Result: All solutions were found and are optimal. Objective function value: "+output.getObjectiveValue());
		} else {
			logger.addError("**Result: Some solutions could not be found.\nRatio:" + output.optimality());
			logger.addError("\tTherefore, not all projects were embedded sucessfully.\nRatio:" + successRate);
			if(!output.noStaticConstraintViolation()) {
				logger.addError("The GIPS pre-solver prevented some solver execution due to conflicting constraints.");
			}
		}
	}

	protected void validateWeeks() {
		LinkedList<Week> weeks = new LinkedList<>(model.getWeeks());
		numberOfWeeks = weeks.size();
		
		Week previous = null;
		int num = 1;
		do {
			Week current = weeks.poll();
			if (current.getNumber() != num++) {
				logger.addError("Weeks are not ordered by their number in ascending fashion. Found on Week("+current.getNumber()+","+current.getId()+").");
				return;
			}

			if (previous != null) {
				if (!previous.getNext().equals(current)) {
					logger.addError("Weeks are not contiguously linked. Found on Week("+current.getNumber()+","+current.getId()+").");
					return;
				}
				if (previous.getNumber() >= current.getNumber()) {
					logger.addError("Weeks are not contiguously linked. Found on Week("+current.getNumber()+","+current.getId()+").");
					return;
				}
			}

			previous = current;

		} while (!weeks.isEmpty());
	}

	protected void validateProjects() {
		numberOfProjects = model.getProjects().size();
		numberOfTasks = model.getProjects().stream().map(p->p.getTasks().size()).reduce(0, (sum, val) -> sum+val);
		numberOfRequirements = model.getProjects().stream().flatMap(p -> p.getTasks().stream()).map(t -> t.getRequirements().size()).reduce(0, (sum, val)->sum+val);
		boolean valid = true;
		
		Map<Project, Boolean> skipList = new LinkedHashMap<>();
		
		if(numberOfProjects == output.getOutputs().size()) {
			for(Project p : model.getProjects()) {
				var out = output.getOutputs().get(p);
				if(out != null) {
					skipList.put(p, out.status() != ILPSolverStatus.OPTIMAL);
				} else {
					skipList.put(p, true);
				}
			}
		} else if(numberOfTasks == output.getOutputs().size()) {
			for(Project p : model.getProjects()) {
				for(Task t : p.getTasks()) {
					var out = output.getOutputs().get(t);
					if(out != null && skipList.containsKey(p)) {
						skipList.put(p, skipList.get(p) || out.status() != ILPSolverStatus.OPTIMAL);
						continue;
					} else if(out != null && !skipList.containsKey(p)) {
						skipList.put(p, out.status() != ILPSolverStatus.OPTIMAL);
					} else {
						skipList.put(p, true);
						continue;
					}
				}
			}
		}
		
		successRate = ((double) skipList.values().stream().filter(b -> !b.booleanValue()).count())/(double) numberOfProjects;
		
		for (Project p : model.getProjects()) {	
			if(skipList.containsKey(p) && skipList.get(p)) {
				logger.addError("Some or all tasks of Project("+p.getName()+","+p.getId()+") were not assigned to an offer.");
				continue;
			}
			
			for (Task t : p.getTasks()) {
				checkAssignmentsTask(p, t);
			}
			
			try {
				valid = p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct().filter(
						week -> week.getNumber() < p.getInitialWeekNumber() || week.getNumber() < p.getInitialWeekNumber())
						.findAny().isEmpty();
				if(!valid) {
					logger.addError("Some tasks of Project("+p.getName()+","+p.getId()+") have been assigned before the project's time frame.");
				}
				
				valid = p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct()
						.filter(week -> week.getNumber() > p.getInitialWeekNumber() + p.getWeeksUntilLoss()
								|| week.getNumber() > p.getInitialWeekNumber() + p.getWeeksUntilLoss())
						.findAny().isEmpty();
				if(!valid) {
					logger.addError("Some tasks of Project("+p.getName()+","+p.getId()+") have been assigned after the project's time frame.");
				}
			} catch (Exception e) {
				valid = false;
			}

			List<Week> weeks = p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct()
					.collect(Collectors.toList());
			Collections.sort(weeks, new Comparator<Week>() {
				@Override
				public int compare(Week o1, Week o2) {
					return o2.getNumber() - o1.getNumber();
				}
			});

			int numOfWeeks = -1;
			try {
				numOfWeeks = 1 + weeks.get(0).getNumber() - p.getInitialWeekNumber();
				if (valid) {
					logger.addInfo("The Project("+p.getName()+","+p.getId()+") will stay within the time limit and will take " + numOfWeeks + " weeks.");
				} else {
					logger.addError("The Project("+p.getName()+","+p.getId()+") exceeds the time limit and will take " + numOfWeeks + " weeks.");
				}
			} catch (Exception e) {
				valid = false;
			}
			
			try {
				valid = (weeks.get(weeks.size() - 1).getNumber() == p.getInitialWeekNumber());
				if (valid) {
					logger.addInfo("The Project("+p.getName()+","+p.getId()+") will start at the initial project week: KW#" + p.getInitialWeekNumber() + ".");
				} else {
					logger.addInfo("The Project("+p.getName()+","+p.getId()+") does not start at the initial week: " + p.getInitialWeekNumber()
					+ ", but starts at: " + weeks.get(weeks.size() - 1).getNumber() + ".");
				}
			} catch (Exception e) {
				valid = false;
			}

			double totalCost = p.getSumSalary();
			logger.addInfo("The Project("+p.getName()+","+p.getId()+") has total salary costs of: " + totalCost);

		}

	}

	protected void checkAssignmentsTask(final Project project, final Task task) {
		if (task.getWeeks() == null || task.getWeeks().isEmpty()) {
			if (task.getRequirements() == null || task.getRequirements().isEmpty()) {
				return;
			} else {
				logger.addError("Task("+task.getName()+","+task.getId()+") of Project("+project.getName()+","+project.getId()+") has open unassigned requirements.");
				return;
			}
		}

		for (Requirement requirement : task.getRequirements()) {
			checkRequirement(task, requirement);
		}

		if (task.getPrevious() != null && !task.getPrevious().isEmpty()) {
			List<Week> previousWeeks = task.getPrevious().stream().flatMap(t -> t.getWeeks().stream()).distinct()
					.collect(Collectors.toList());
			Collections.sort(previousWeeks, new Comparator<Week>() {
				@Override
				public int compare(Week o1, Week o2) {
					return o2.getNumber() - o1.getNumber();
				}
			});
			List<Week> currentWeeks = new LinkedList<>(task.getWeeks());
			Collections.sort(currentWeeks, new Comparator<Week>() {
				@Override
				public int compare(Week o1, Week o2) {
					return o1.getNumber() - o2.getNumber();
				}
			});

			if(previousWeeks.get(0).getNumber() > currentWeeks.get(0).getNumber()) {
				logger.addError("Task("+task.getName()+","+task.getId()+") of Project("+project.getName()+","+project.getId()+") has weeks that violate the partial order constraint.");
			}
		}
	}

	protected void checkRequirement(final Task task, final Requirement requirement) {
		boolean valid = requirement.getOffers().stream()
				.map(offer -> offer.getHours() * ((Person) offer.eContainer()).getOvertimeFlexibility())
				.reduce(0.0, (sum, offer) -> sum + offer) >= requirement.getHours();
				
		if(!valid) {
			logger.addError("Requirement("+requirement.getName()+","+requirement.getId()+") of Task("+task.getName()+","+task.getId()+") assigned offers do not meet the required hours.");
		}

		valid = !requirement.getOffers().stream().map(offer -> (Person) offer.eContainer())
				.filter(person -> person.getSkills().stream()
						.filter(skill -> skill.getName().equals(requirement.getName())
								&& skill.getLevel() >= requirement.getSkillLevel())
						.findAny().isEmpty())
				.findAny().isPresent();

		if(!valid) {
			logger.addError("Requirement("+requirement.getName()+","+requirement.getId()+") of Task("+task.getName()+","+task.getId()+") assigned skill level or skill type is not matching the requirements.");
		}
	}

	protected void validatePersons() {
		numberOfPersons = model.getPersons().size();
		
		for (Person person : model.getPersons()) {
			numberOfOffers += person.getOffers().size();
			
			Set<Week> offeredWeeks = person.getOffers().stream().map(offer -> offer.getWeek())
					.collect(Collectors.toSet());
			if(!person.getOffers().stream().filter(offer -> offer.getRequirements().size() > 1).findAny()
					.isEmpty()) {
				logger.addInfo("Person("+person.getName()+","+person.getId()+") is assigning an offer to more than one requirement.");
			}
		}
	}

	public String getLog() {
		return logger.toString();
	}

	public int getNumberOfProjects() {
		return numberOfProjects;
	}

	public int getNumberOfTasks() {
		return numberOfTasks;
	}

	public int getNumberOfRequirements() {
		return numberOfRequirements;
	}

	public int getNumberOfOffers() {
		return numberOfOffers;
	}

	public int getNumberOfWeeks() {
		return numberOfWeeks;
	}

	public int getNumberOfPersons() {
		return numberOfPersons;
	}

	public double getSuccessRate() {
		return successRate;
	}
	
}