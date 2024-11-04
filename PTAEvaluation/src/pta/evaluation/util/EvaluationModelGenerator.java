package pta.evaluation.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.SkillType;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;
import pta.generator.PTAModelGenerator;
import pta.scenario.ScenarioGenerator;

public class EvaluationModelGenerator extends ScenarioGenerator {

	public static void main(String[] args) {
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances/evaluation/scenarios/";
		File iF = new File(instancesFolder);
		if (!iF.exists()) {
			iF.mkdirs();
		}
		
		for(int i = 1; i<=10; i++) {
			PersonTaskAssignmentModel model = generateBatch(i);
			try {
				save(model, instancesFolder + "/Batch"+i+".xmi");
			} catch (IOException e) {
				e.printStackTrace();
			}
			model = generateIncremental(i);
			try {
				save(model, instancesFolder + "/Inc"+i+".xmi");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static PersonTaskAssignmentModel generateBatch(int size) {
		ScenarioGenerator generator = new ScenarioGenerator();
		generator.offerSplitRate = 0.5;
		generator.additionalOfferRate = 0.2;
		generator.scale(size, 1, 0, 6, 0, 5, 0);
		return generator.generate("EpicSeed".hashCode());
	}
	
	public static PersonTaskAssignmentModel generateIncremental(int size) {
		ScenarioGenerator generator = new ScenarioGenerator();
		generator.offerSplitRate = 0.0;
		generator.additionalOfferRate = 0.4;
		generator.scale(size, 1, 0, 10, 0, 15, 0);
		return generator.generate("EpicSeed".hashCode());
	}
	
}