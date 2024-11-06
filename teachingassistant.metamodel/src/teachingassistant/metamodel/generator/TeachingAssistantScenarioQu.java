package teachingassistant.metamodel.generator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import metamodel.Department;
import metamodel.SkillType;

/**
 * Implements the scenario described in: Xiaobo Qu, Wen Yi, Tingsong Wang,
 * Shuaian Wang, Lin Xiao, and Zhiyuan Liu. Mixed-Integer Linear Programming
 * Models for Teaching Assistant Assignment and Extensions. 2017.
 * https://doi.org/10.1155/2017/9057947
 */
public class TeachingAssistantScenarioQu extends TeachingAssistantGenerator {

	protected Random rand;
	private final static int NUMBER_OF_TUTORIALS = 20;
	private final static int NUMBER_OF_TAS = 15;

	public static void main(final String[] args) {
		final TeachingAssistantScenarioQu gen = new TeachingAssistantScenarioQu(0);
		final String instanceFolderPath = gen.prepareFolder();

		final Department model = gen.constructModelM0(NUMBER_OF_TUTORIALS, NUMBER_OF_TAS);
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
	 * an integer uniformly drawn between 1 and 2.
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

		return generate("QuEtAlDepartment");
	}

	//
	// Utility methods.
	//

	private String prepareFolder() {
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final File f = new File(instancesFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		return instancesFolder;
	}

	private int getRandInt(final int min, final int max) {
		return rand.nextInt((max - min) + 1) + min;
	}

	private SkillType getRandomSkillType() {
		final List<SkillType> allSkillTypes = SkillType.VALUES;
		final int randomIndex = getRandInt(0, allSkillTypes.size() - 1);
		return allSkillTypes.get(randomIndex);
	}

}
