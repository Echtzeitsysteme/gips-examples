package pta.extended.example.house;

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

import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.PersonTaskAssignmentsPackage;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Requirement;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;
import PersonTaskAssignments.Person;

public class HouseConstructionValidator {

	protected PersonTaskAssignmentModel model;

	public static void main(String[] args) {
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/ConstructionProject1_solved.xmi";
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(PersonTaskAssignmentsPackage.eNS_URI, PersonTaskAssignmentsPackage.eINSTANCE);
		URI fileURI = URI.createFileURI(file);
		Resource r = rs.getResource(fileURI, true);
		final PersonTaskAssignmentModel model = (PersonTaskAssignmentModel) r.getContents().get(0);
		boolean valid = new HouseConstructionValidator().validate(model);

		if (valid) {
			System.out.println("INFO: Model is valid!");
		} else {
			System.out.println("INFO: Model is not valid!");
		}
	}

	public boolean validate(final PersonTaskAssignmentModel model) {
		this.model = model;
		boolean valid = true;
		valid &= validateWeeks();
		valid &= validateProjects();
		valid &= validatePersons();

		return valid;
	}

	public boolean validateWeeks() {
		LinkedList<Week> weeks = new LinkedList<>(model.getWeeks());
		Week previous = null;
		int num = 1;
		do {
			Week current = weeks.poll();
			if (current.getNumber() != num++)
				return false;

			if (previous != null) {
				if (!previous.getNext().equals(current)) {
					return false;
				}
				if (previous.getNumber() >= current.getNumber()) {
					return false;
				}
			}

			previous = current;

		} while (!weeks.isEmpty());

		return true;
	}

	public boolean validateProjects() {
		boolean valid = true;
		for (Project p : model.getProjects()) {
			for (Task t : p.getTasks()) {
				valid &= checkAssignmentsTask(t);
			}

			if (valid) {
				System.out.println("INFO: All tasks are assigned according to constraints.");
			} else {
				System.out.println("INFO: Some tasks are not assigned according to constraints.");
			}

			valid &= p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct().filter(
					week -> week.getNumber() < p.getInitialWeekNumber() || week.getNumber() < p.getStart().getNumber())
					.findAny().isEmpty();

			valid &= p.getTasks().stream().flatMap(t -> t.getWeeks().stream()).distinct()
					.filter(week -> week.getNumber() > p.getInitialWeekNumber() + p.getWeeksUntilLoss()
							|| week.getNumber() > p.getStart().getNumber() + p.getWeeksUntilLoss())
					.findAny().isEmpty();

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
				System.out.println(
						"INFO: The project will stay within the time limit and will take " + numOfWeeks + " weeks.");
			} else {
				System.out.println("INFO: The project exceeds the time limit and will take " + numOfWeeks + " weeks.");
			}

			valid &= (weeks.get(weeks.size() - 1).getNumber() == p.getInitialWeekNumber());
			if (valid) {
				System.out.println("INFO: The project will start at the initial project week: KW#"
						+ p.getInitialWeekNumber() + ".");
			} else {
				System.out.println(
						"INFO: The project does not start at the required initial week: " + p.getInitialWeekNumber()
								+ ", but starts at: " + weeks.get(weeks.size() - 1).getNumber() + ".");
			}
//			
//			double totalCost = p.getTasks().stream()
//				.flatMap(t -> t.getRequirements().stream())
//				.map(r -> {
//					int offeredHours = r.getOffers().stream().map(offer -> offer.getHours())
//					.reduce(0, (sum, hours) -> sum + hours);
//					if(offeredHours < r.getHours()) {
//						// pay wages and add overtime bonus
//						int diff = r.getHours() - offeredHours;
//						return  r.getHours()*r.getSalary() + diff * r.getOvertimeBonus();
//					} else {
//						// pay only the required amount of hours ignore "over offers"
//						return r.getHours()*r.getSalary();
//					}
//				}).reduce(0.0, (sum, cost) -> sum + cost);
			double totalCost = p.getSumSalary();

			if (totalCost <= p.getReward()) {
				System.out.println("INFO: Project planning is within budget. Total salary is: " + totalCost
						+ "€, while the planned spending limit was: " + p.getReward() + "€.");
			} else {
				System.out.println("INFO: Project planning exceeds budget. Total salary is: " + totalCost
						+ "€, while the planned spending limit was: " + p.getReward() + "€.");
			}

		}

		return valid;
	}

	public boolean checkAssignmentsTask(final Task task) {
		if (task.getWeeks() == null || task.getWeeks().isEmpty()) {
			if (task.getRequirements() == null || task.getRequirements().isEmpty()) {
				return true;
			} else {
				return false;
			}
		}

		boolean valid = true;
		for (Requirement requirement : task.getRequirements()) {
			valid &= checkRequirement(requirement);
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

			valid &= previousWeeks.get(0).getNumber() < currentWeeks.get(0).getNumber();
//			System.out.println(valid);
		}

		return valid;
	}

	public boolean checkRequirement(final Requirement requirement) {
		boolean valid = requirement.getOffers().stream()
				.map(offer -> offer.getHours() * ((Person) offer.eContainer()).getOvertimeFlexibility())
				.reduce(0.0, (sum, offer) -> sum + offer) >= requirement.getHours();

		valid &= !requirement.getOffers().stream().map(offer -> (Person) offer.eContainer())
				.filter(person -> person.getSkills().stream()
						.filter(skill -> skill.getName().equals(requirement.getName())
								&& skill.getLevel() >= requirement.getSkillLevel())
						.findAny().isEmpty())
				.findAny().isPresent();

		return valid;
	}

	public boolean validatePersons() {
		boolean valid = true;
		for (Person person : model.getPersons()) {
			Set<Week> offeredWeeks = person.getOffers().stream().map(offer -> offer.getWeek())
					.collect(Collectors.toSet());
			for (Week week : model.getWeeks()) {
				valid &= offeredWeeks.contains(week);
			}
			valid &= person.getOffers().stream().filter(offer -> offer.getRequirements().size() > 1).findAny()
					.isEmpty();
		}
		return valid;
	}

}
