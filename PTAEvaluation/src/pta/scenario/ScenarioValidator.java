package pta.scenario;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.PersonTaskAssignmentsPackage;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Requirement;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;

public class ScenarioValidator {
	
	final protected PersonTaskAssignmentModel model;
	final protected ValidationLogger logger = new ValidationLogger();
	
	public ScenarioValidator(final PersonTaskAssignmentModel model) {
		this.model = model;
	}
	
	public ScenarioValidator(String file) {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(PersonTaskAssignmentsPackage.eNS_URI, PersonTaskAssignmentsPackage.eINSTANCE);
		URI fileURI = URI.createFileURI(file);
		Resource r = rs.getResource(fileURI, true);
		this.model = (PersonTaskAssignmentModel) r.getContents().get(0);
	}

	public boolean validate() {
		validateWeeks();
		validateProjects();
		validatePersons();

		return !logger.hasErrors();
	}
	
	public String getLog() {
		return logger.toString();
	}

	public void validateWeeks() {
		LinkedList<Week> weeks = new LinkedList<>(model.getWeeks());
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

	public void validateProjects() {
		boolean valid = true;
		for (Project p : model.getProjects()) {
			for (Task t : p.getTasks()) {
				checkAssignmentsTask(p, t);

			}
			
			valid = p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct().filter(
					week -> week.getNumber() < p.getInitialWeekNumber() || week.getNumber() < p.getStart().getNumber())
					.findAny().isEmpty();
			if(!valid) {
				logger.addError("Some tasks of Project("+p.getName()+","+p.getId()+") have been assigned before the project's time frame.");
			}

			valid = p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct()
					.filter(week -> week.getNumber() > p.getInitialWeekNumber() + p.getWeeksUntilLoss()
							|| week.getNumber() > p.getStart().getNumber() + p.getWeeksUntilLoss())
					.findAny().isEmpty();
			if(!valid) {
				logger.addError("Some tasks of Project("+p.getName()+","+p.getId()+") have been assigned after the project's time frame.");
			}

			List<Week> weeks = p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct()
					.collect(Collectors.toList());
			Collections.sort(weeks, new Comparator<Week>() {
				@Override
				public int compare(Week o1, Week o2) {
					return o2.getNumber() - o1.getNumber();
				}
			});

			int numOfWeeks = 1 + weeks.get(0).getNumber() - p.getInitialWeekNumber();
			if (valid) {
				logger.addInfo("The Project("+p.getName()+","+p.getId()+") will stay within the time limit and will take " + numOfWeeks + " weeks.");
			} else {
				logger.addError("The Project("+p.getName()+","+p.getId()+") exceeds the time limit and will take " + numOfWeeks + " weeks.");
			}

			valid = (weeks.get(weeks.size() - 1).getNumber() == p.getInitialWeekNumber());
			if (valid) {
				logger.addInfo("The Project("+p.getName()+","+p.getId()+") will start at the initial project week: KW#" + p.getInitialWeekNumber() + ".");
			} else {
				logger.addError("The Project("+p.getName()+","+p.getId()+") does not start at the required initial week: " + p.getInitialWeekNumber()
				+ ", but starts at: " + weeks.get(weeks.size() - 1).getNumber() + ".");
			}

			double totalCost = p.getSumSalary();
			logger.addInfo("The Project("+p.getName()+","+p.getId()+") has total salary costs of: " + totalCost);

		}

	}

	public void checkAssignmentsTask(final Project project, final Task task) {
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

			if(previousWeeks.get(0).getNumber() >= currentWeeks.get(0).getNumber()) {
				logger.addError("Task("+task.getName()+","+task.getId()+") of Project("+project.getName()+","+project.getId()+") has weeks that violate the partial order constraint.");
			}
		}
	}

	public void checkRequirement(final Task task, final Requirement requirement) {
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

	public void validatePersons() {
		for (Person person : model.getPersons()) {
			Set<Week> offeredWeeks = person.getOffers().stream().map(offer -> offer.getWeek())
					.collect(Collectors.toSet());
			for (Week week : model.getWeeks()) {
				if(!offeredWeeks.contains(week)) {
					logger.addError("Person("+person.getName()+","+person.getId()+") is placing an offer in a week that is not existing in the model.");
				}
			}
			
			if(!person.getOffers().stream().filter(offer -> offer.getRequirements().size() > 1).findAny()
					.isEmpty()) {
				logger.addInfo("Person("+person.getName()+","+person.getId()+") is assigning an offer to more than one requirement.");
			}
		}
	}
	
}