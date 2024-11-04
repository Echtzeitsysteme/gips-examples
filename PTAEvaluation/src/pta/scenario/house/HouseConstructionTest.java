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

public class HouseConstructionTest {
	
	public static void main(String[] args) {
		List<ScenarioRunner<?>> runners = new LinkedList<>();
//		runners.add(new HouseConstructionIncF("INC-F"));
//		runners.add(new HouseConstructionIncG("INC-G"));
		runners.add(new HouseConstructionBatchA("Batch-A"));
//		runners.add(new HouseConstructionBatchB("Batch-B"));
//		runners.add(new HouseConstructionBatchC("Batch-C"));
//		runners.add(new HouseConstructionBatchD("Batch-D"));
//		runners.add(new HouseConstructionBatchE("Batch-E"));
		
		Map<String, EvaluationResult> results = new LinkedHashMap<>();
		runners.forEach(runner -> {
			ScenarioGenerator generator = new ScenarioGenerator();
//			generator.nProjects = ScenarioGenerator.mkRange(3, 6);
//			generator.tasksPerProject = ScenarioGenerator.mkRange(4, 8);
//			generator.reqPerTask = ScenarioGenerator.mkRange(4, 8);
			generator.offerSplitRate = 0.0;
			
			generator.additionalOfferRate = 0.4;
			/*	Config and upper Limit for the Batch evaluation Scenario for Runners using BATCH-A through D
			 *  generator.offerSplitRate = 0.5;
			 *  generator.additionalOfferRate = 0.2;
			 * 	generator.scale(10, 1, 0, 6, 0, 5, 0);
			 */
			/*	Config and upper Limit for the Batch evaluation Scenario for Runners using BATCH-E, which will not find a valid but optimal solution.
			 *  generator.offerSplitRate = 0.5;
			 *  generator.additionalOfferRate = 0.2;	
			 *  generator.scale(7, 1, 0, 6, 0, 5, 0);
			 */
			
			/*	Upper Limit for the Incremental evaluation Scenario for Runners using BATCH-A, BATCH-F and BATCH-G, where the two latter of which will not find all optimal solutions.
			 *  generator.offerSplitRate = 0.0;
			 *  generator.additionalOfferRate = 0.4;	
			 *  generator.scale(10, 1, 0, 10, 0, 15, 0);
			 */
			generator.scale(1, 1, 0, 10, 0, 15, 0);
			PersonTaskAssignmentModel model = generator.generate("EpicSeed".hashCode());

			System.out.println("##########\tRunning "+runner.name+" ...\t##########");
			Observer obs = Observer.getInstance();
			obs.setCurrentSeries(runner.name);
			obs.observe("INIT", ()->runner.init(model));
			EvaluationResult result;
			try {
				result = runner.run();
				System.out.println(result);
				results.put(runner.name, result);
				HouseConstructionHeadless.resultToCSV("./test.csv", result, runner);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("##########\tFinished "+runner.name+".\t##########");
		});
	}

}
