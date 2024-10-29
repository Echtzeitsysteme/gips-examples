package pta.scenario.house;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.emoflon.gips.core.util.Observer;

import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.evaluation.util.EvaluationResult;
import pta.scenario.ScenarioGenerator;
import pta.scenario.ScenarioRunner;

public class HouseConstructionBatchTest {
	
	public static void main(String[] args) {
		List<ScenarioRunner<?>> runners = new LinkedList<>();
		runners.add(new HouseConstructionBatchA("Batch-A"));
		runners.add(new HouseConstructionBatchB("Batch-B"));
		runners.add(new HouseConstructionBatchC("Batch-C"));
		runners.add(new HouseConstructionBatchD("Batch-D"));
		runners.add(new HouseConstructionBatchD("Batch-E"));
		
		Map<String, EvaluationResult> results = new LinkedHashMap<>();
		runners.forEach(runner -> {
			ScenarioGenerator generator = new ScenarioGenerator();
			generator.nProjects = ScenarioGenerator.mkRange(3, 6);
			generator.tasksPerProject = ScenarioGenerator.mkRange(4, 8);
			generator.reqPerTask = ScenarioGenerator.mkRange(4, 8);
			PersonTaskAssignmentModel model = generator.generate("EpicSeed".hashCode());

			System.out.println("##########\tRunning "+runner.name+" ...\t##########");
			Observer obs = Observer.getInstance();
			obs.setCurrentSeries(runner.name);
			obs.observe("INIT", ()->runner.init(model));
			EvaluationResult result = runner.run();
			System.out.println(result);
			
			results.put(runner.name, result);
			System.out.println("##########\tFinished "+runner.name+".\t##########");
		});
	}
}
