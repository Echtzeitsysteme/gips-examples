package teachingassistant.kcl.metamodel.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import metamodel.Day;
import metamodel.Department;
import metamodel.Lecturer;
import metamodel.Week;
import teachingassistant.kcl.metamodel.export.ModelToJsonExporter;

public class SimpleTaKclGenerator extends TeachingAssistantKclGenerator {

	//
	// Configuration
	//

	// Lecturers
	int NUMBER_OF_LECTURERS = 2;
	int LECTURERS_MINIMUM_NUMBER_OF_ASSISTANTS = 1;
	int LECTURERS_MAXIMUM_NUMBER_OF_ASSISTANTS = 8;

	// Assistants
	int NUMBER_OF_ASSISTANTS = 10;
	int ASSISTANTS_MAXIMUM_NUMBER_OF_DAYS_PER_WEEK = 3;
	int ASSISTANTS_MINIMUM_NUMBER_OF_HOURS_PER_WEEK = 0;
	int ASSISTANTS_MAXIMUM_NUMBER_OF_HOURS_PER_WEEK = 20; // KCL
	int ASSISTANTS_MAXIMUM_HOURS_TOTAL = 312; // KCL
	int ASSISTANTS_MINIMUM_NUMBER_OF_BLOCKED_DAYS = 0;
	int ASSISTANTS_MAXIMUM_NUMBER_OF_BLOCKED_DAYS = 1;

	// Time slots
	int NUMBER_OF_TIMESLOTS_PER_WEEK = 10; // Must be at least 5 = 1 per day

	// Tutorials
	int NUMBER_OF_TUTORIALS_PER_WEEK = 6;

	// Weeks
	int NUMBER_OF_WEEKS = 1;

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

		final ModelToJsonExporter exporter = new ModelToJsonExporter(model);
		exporter.modelToJson(instanceFolderPath + "/kcl_department.json");

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
		for (int i = 0; i < NUMBER_OF_ASSISTANTS; i++) {
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

			// Assumption: every assistant gets a random number of blocked dates equally
			// drawn from MIN to MAX as configured above.
			final Set<Day> blockedDates = new HashSet<Day>();
			final int numberOfBlockedDays = getRandInt(ASSISTANTS_MINIMUM_NUMBER_OF_BLOCKED_DAYS,
					ASSISTANTS_MAXIMUM_NUMBER_OF_BLOCKED_DAYS);
			int counter = 0;
			while (counter < numberOfBlockedDays) {
				if (blockedDates.add(getRandomDay())) {
					counter++;
				}
			}
			assistants.get("Assistant_" + i).getBlockedDates().addAll(blockedDates);
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
		for (int i = 0; i < NUMBER_OF_TUTORIALS_PER_WEEK * NUMBER_OF_WEEKS; i++) {
			// The ID of the time slot to choose should be drawn equally from random
			addTutorial("Tutorial_" + i, getRandomSkillTypeFromLecturer(), getRandInt(1, 2),
					getRandInt(0, this.timeslots.size() - 1));
		}

		return generate("KCL-Department");
	}

	private Day getRandomDay() {
		final int randomDayIndex = getRandInt(0, days.size() - 1);
		int counter = 0;
		final Iterator<Day> it = days.values().iterator();
		Day d = null;
		while (it.hasNext()) {
			d = it.next();
			counter++;
			if (counter == randomDayIndex) {
				break;
			}
		}
		return d;
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
