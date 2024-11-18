package teachingassistant.metamodel.generator;

import java.io.IOException;
import java.util.Random;

import metamodel.Department;
import metamodel.SkillType;

/**
 * Implements the scenarios described in: Xiaobo Qu, Wen Yi, Tingsong Wang,
 * Shuaian Wang, Lin Xiao, and Zhiyuan Liu. Mixed-Integer Linear Programming
 * Models for Teaching Assistant Assignment and Extensions. 2017.
 * https://doi.org/10.1155/2017/9057947
 */
public class TeachingAssistantScenarioQu extends TeachingAssistantGenerator {

	// M0 related constants
	private final static int NUMBER_OF_TUTORIALS = 20;
	private final static int NUMBER_OF_TAS = 15;

	// M1 related constants
	private final static int NUMBER_OF_TIMESLOTS = 10;

	// M3 related constants
	private final static int NUMBER_OF_LECTURERS = 5;
	private final static int MAXIMUM_NUMBER_OF_TAS_PER_LECTURER = 10;

	public static void main(final String[] args) {
		final TeachingAssistantScenarioQu gen = new TeachingAssistantScenarioQu(0);
		final String instanceFolderPath = gen.prepareFolder();

//		final Department model = gen.constructModelM0(NUMBER_OF_TUTORIALS, NUMBER_OF_TAS);
//		final Department model = gen.constructModelM1(NUMBER_OF_TUTORIALS, NUMBER_OF_TAS, NUMBER_OF_TIMESLOTS);
		final Department model = gen.constructModelM3(NUMBER_OF_TUTORIALS, NUMBER_OF_TAS, NUMBER_OF_TIMESLOTS,
				NUMBER_OF_LECTURERS);
		try {
			save(model, instanceFolderPath + "/qu_department.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println("Scenario generation finished.");
	}

	public TeachingAssistantScenarioQu(final int seed) {
		rand = new Random(seed);
	}

	/**
	 * Constructs a model based on M0 of the publication mentioned above: "The
	 * instances are generated as follows. In [M0], for each tutorial-TA
	 * combination, there is 1/3 chance that the TA cannot deliver the tutorial, 1/3
	 * chance that the TA can but is not the most suitable, and 1/3 chance that the
	 * TA is the most suitable for delivering tutorial; the minimum number of
	 * tutorials that must be delivered by a TA is 0; the maximum number of
	 * tutorials that can be delivered by a TA is an integer uniformly drawn between
	 * 1 and 3; the minimum number of available hours per week for a TA is 0; the
	 * maximum number of available hours per week for TA is an integer uniformly
	 * drawn between 1 and 5; the number of contact hours required for a tutorial is
	 * an integer uniformly drawn between 1 and 2."
	 * 
	 * Assumption: minimum number of tutorials per TA and number of hours was merged
	 * in our metamodel. Therefore, a teaching assistant can give as many tutorials
	 * as their aggregated duration is lower or equal to their maximum number of
	 * hours per week.
	 * 
	 * @param numberOfTutorials Number of tutorials to generate.
	 * @param numberOfTas       Number of teaching assistants to generate.
	 * @return Department model instance.
	 */
	public Department constructModelM0(final int numberOfTutorials, final int numberOfTas) {
		// Assistants
		for (int i = 0; i < numberOfTas; i++) {
			addAssistant("Assistant_" + i, 0, getRandInt(0, 5));

			// Assumption: every assistant has every SkillType but the preference is
			// uniformly random.
			// Warning: this may lead to non-feasible models!
			for (final SkillType st : SkillType.VALUES) {
				addSkillToAssistant("Assistant_" + i, st, getRandInt(0, 2));
			}
		}

		// Tutorials
		for (int i = 0; i < numberOfTutorials; i++) {
			addTutorial("Tutorial_" + i, getRandomSkillType(), getRandInt(1, 2));
		}

		return generate("QuEtAlDepartmentM0");
	}

	/**
	 * Constructs a model based on M1 of the publication mentioned above. In
	 * addition to the construction of scenario M0, the following specification will
	 * be used: "4.1. Time Conflict of Two Tutorials. Usually the schedules of the
	 * tutorials are a priori determined. For instance, if the course “Basics of
	 * Calculus” has 3 tutorials, then the 3 tutorials are usually scattered
	 * uniformly in a week. As a result, it is likely that one tutorial (say,
	 * tutorial i is from 4:00 pm to 5:00 pm on Monday) has time overlap with
	 * another tutorial (say, tutorial 𝑘 is from 3:00 pm to 5:00 pm on Monday). In
	 * this case, the two tutorials cannot be taught by the same TA."
	 * 
	 * Assumption: minimum number of tutorials per TA and number of hours was merged
	 * in our metamodel. Therefore, a teaching assistant can give as many tutorials
	 * as their aggregated duration is lower or equal to their maximum number of
	 * hours per week.
	 * 
	 * Assumption: Time slots will be generated according to the given
	 * `numberOfTimeslots`. Each tutorial will get a random time slot assign
	 * (equally distributed).
	 * 
	 * @param numberOfTutorials Number of tutorials to generate.
	 * @param numberOfTas       Number of teaching assistants to generate.
	 * @param numberOfTimeslots Number of time slots to generate.
	 * @return Department model instance.
	 */
	public Department constructModelM1(final int numberOfTutorials, final int numberOfTas,
			final int numberOfTimeslots) {
		// Assistants
		for (int i = 0; i < numberOfTas; i++) {
			addAssistant("Assistant_" + i, 0, getRandInt(0, 5));

			// Assumption: every assistant has every SkillType but the preference is
			// uniformly random.
			// Warning: this may lead to non-feasible models!
			for (final SkillType st : SkillType.VALUES) {
				addSkillToAssistant("Assistant_" + i, st, getRandInt(0, 2));
			}
		}

		// Time slots
		for (int i = 0; i < numberOfTimeslots; i++) {
			addTimeslot(i);
		}

		// Tutorials
		for (int i = 0; i < numberOfTutorials; i++) {
			// The ID of the time slot to choose should be drawn equally from random
			addTutorial("Tutorial_" + i, getRandomSkillType(), getRandInt(1, 2), getRandInt(0, numberOfTimeslots - 1));
		}

		return generate("QuEtAlDepartmentM1");
	}

	/**
	 * Constructs a model based on M3 of the publication mentioned above. In
	 * addition to the construction of scenario M0, the following specification will
	 * be used: "4.3. Controlling the Number of TAs for One Course. Some lecturers
	 * may want to control the number of TAs for his course. For instance, no
	 * lecturer wants to have 12 TAs to teach his tutorials."
	 * 
	 * Assumption: minimum number of tutorials per TA and number of hours was merged
	 * in our metamodel. Therefore, a teaching assistant can give as many tutorials
	 * as their aggregated duration is lower or equal to their maximum number of
	 * hours per week.
	 * 
	 * Assumption: Time slots will be generated according to the given
	 * `numberOfTimeslots`. Each tutorial will get a random time slot assign
	 * (equally distributed).
	 * 
	 * Assumption: Lecturers will be generated according to the given
	 * `numberOfLecturers`. Each lecturer will get a random maximum number of
	 * teaching assistants.
	 * 
	 * 
	 * @param numberOfTutorials Number of tutorials to generate.
	 * @param numberOfTas       Number of teaching assistants to generate.
	 * @param numberOfTimeslots Number of time slots to generate.
	 * @param numberOfLecturers Number of lecturers to generate.
	 * @return Department model instance.
	 */
	public Department constructModelM3(final int numberOfTutorials, final int numberOfTas, final int numberOfTimeslots,
			final int numberOfLecturers) {
		// Assistants
		for (int i = 0; i < numberOfTas; i++) {
			addAssistant("Assistant_" + i, 0, getRandInt(0, 5));

			// Assumption: every assistant has every SkillType but the preference is
			// uniformly random.
			// Warning: this may lead to non-feasible models!
			for (final SkillType st : SkillType.VALUES) {
				addSkillToAssistant("Assistant_" + i, st, getRandInt(0, 2));
			}
		}

		// Time slots
		for (int i = 0; i < numberOfTimeslots; i++) {
			addTimeslot(i);
		}

		// Tutorials
		for (int i = 0; i < numberOfTutorials; i++) {
			// The ID of the time slot to choose should be drawn equally from random
			addTutorial("Tutorial_" + i, getRandomSkillType(), getRandInt(1, 2), getRandInt(0, numberOfTimeslots - 1));
		}

		// Lecturers
		for (int i = 0; i < numberOfLecturers; i++) {
			// The number of maximum assigned TAs will be drawn equally from random
			addLecturer("Lecturer_" + i, getRandomSkillType(), getRandInt(0, MAXIMUM_NUMBER_OF_TAS_PER_LECTURER));
		}

		return generate("QuEtAlDepartmentM3");
	}

}
