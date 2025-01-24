package teachingassistant.kcl.metamodel.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import metamodel.Day;
import metamodel.Department;
import metamodel.Lecturer;
import metamodel.Week;

public class SimpleTaKclGenerator extends TeachingAssistantKclGenerator {

	//
	// Configuration
	//

	// Lecturers
	private final int NUMBER_OF_LECTURERS = 2;
	private final int LECTURERS_MINIMUM_NUMBER_OF_ASSISTANTS = 1;
	private final int LECTURERS_MAXIMUM_NUMBER_OF_ASSISTANTS = 8;

	// Assistants
	private final int NUMBER_OR_ASSISTANTS = 5;
	private final int ASSISTANTS_MAXIMUM_NUMBER_OF_DAYS_PER_WEEK = 3;
	private final int ASSISTANTS_MINIMUM_NUMBER_OF_HOURS_PER_WEEK = 1;
	private final int ASSISTANTS_MAXIMUM_NUMBER_OF_HOURS_PER_WEEK = 20; // KCL
	private final int ASSISTANTS_MAXIMUM_HOURS_TOTAL = 312; // KCL

	// Time slots
	private final int NUMBER_OF_TIMESLOTS_PER_WEEK = 10; // Must be at least 5 = 1 per day

	// Tutorials
	private final int NUMBER_OF_TUTORIALS_PER_WEEK = 6;

	// Weeks
	private final int NUMBER_OF_WEEKS = 1;

	/**
	 * Runs the scenario generation and writes a XMI file to the configured output
	 * path. All arguments will be ignored.
	 * 
	 * @param args Arguments will be ignored.
	 */
	public static void main(final String[] args) {
		final SimpleTaKclGenerator gen = new SimpleTaKclGenerator(0);
		final String instanceFolderPath = gen.prepareFolder();

		final Department model = gen.constructModel();
		try {
			save(model, instanceFolderPath + "/kcl_department.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println("=> Scenario generation finished.");
	}

	/**
	 * Initializes a new generator object with the given random seed.
	 * 
	 * @param seed Random seed.
	 */
	public SimpleTaKclGenerator(final int seed) {
		rand = new Random(seed);
	}

	public Department constructModel() {
		// Weeks and days
		for (int i = 0; i < NUMBER_OF_WEEKS; i++) {
			addWeekWithDays("Week_" + i);
		}

		// Lecturers
		for (int i = 0; i < NUMBER_OF_LECTURERS; i++) {
			addLecturer("Prof. " + i, getRandomSkillType());
			this.lecturers.get("Prof. " + i).setMaximumNumberOfTas(getRandInt( //
					LECTURERS_MINIMUM_NUMBER_OF_ASSISTANTS, //
					LECTURERS_MAXIMUM_NUMBER_OF_ASSISTANTS));
		}

		// Assistants
		for (int i = 0; i < NUMBER_OR_ASSISTANTS; i++) {
			addAssistant("Assistant_" + i, //
					ASSISTANTS_MINIMUM_NUMBER_OF_HOURS_PER_WEEK, //
					ASSISTANTS_MAXIMUM_NUMBER_OF_HOURS_PER_WEEK, //
					ASSISTANTS_MAXIMUM_NUMBER_OF_DAYS_PER_WEEK, //
					ASSISTANTS_MAXIMUM_HOURS_TOTAL //
			);

			// Assumption: every assistant has every SkillType but the preference is
			// uniformly random.
			// Warning: this may lead to non-feasible models!
			for (final String st : getAllLecturerSkills()) {
				addSkillToAssistant("Assistant_" + i, st, getRandInt(0, 2));
			}

			// Assumption: every assistant gets a random number of possible work days per
			// week equally drawn from 1 to `maximumDaysPerWeek`
			assistants.get("Assistant_" + i).setMaximumDaysPerWeek(getRandInt( //
					1, //
					ASSISTANTS_MAXIMUM_NUMBER_OF_DAYS_PER_WEEK //
			));
		}

		// Time slots
		for (int i = 0; i < NUMBER_OF_WEEKS; i++) {
			for (int j = 0; j < NUMBER_OF_TIMESLOTS_PER_WEEK; j++) {
				final int timeslotId = (i * NUMBER_OF_TIMESLOTS_PER_WEEK) + j;
				addTimeslot(timeslotId);
				final Week w = this.weeks.get("Week_" + i);
				if (w == null) {
					throw new UnsupportedOperationException("Week not found.");
				}
				final Day d = w.getDays().get(j % 5);
				if (d == null) {
					throw new UnsupportedOperationException("Day not found.");
				}
				d.getTimeslots().add(this.timeslots.get(timeslotId));
			}
		}

		// Tutorials
		for (int i = 0; i < NUMBER_OF_TUTORIALS_PER_WEEK; i++) {
			// The ID of the time slot to choose should be drawn equally from random
			addTutorial("Tutorial_" + i, getRandomSkillTypeFromLecturer(), getRandInt(1, 2),
					getRandInt(0, this.timeslots.size() - 1));
		}

		return generate("KCL-Department");
	}

	private List<String> getAllLecturerSkills() {
		final List<String> lecturerSkillTypes = new ArrayList<String>();
		for (final Lecturer l : this.lecturers.values()) {
			final String skillType = l.getSkillTypeName();
			if (!lecturerSkillTypes.contains(skillType)) {
				lecturerSkillTypes.add(skillType);
			}
		}
		return lecturerSkillTypes;
	}

	private String getRandomSkillTypeFromLecturer() {
		if (this.lecturers.size() == 0) {
			throw new UnsupportedOperationException("No lecturer found.");
		}
		final List<String> lecturerSkillTypes = getAllLecturerSkills();
		return lecturerSkillTypes.get(getRandInt(0, lecturerSkillTypes.size() - 1));
	}
}
