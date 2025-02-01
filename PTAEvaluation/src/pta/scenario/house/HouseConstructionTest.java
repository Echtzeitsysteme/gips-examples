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
			generator.offerSplitRate = 0.0;
			
			generator.additionalOfferRate = 0.25;
			/*	Config and upper Limit for the Batch evaluation Scenario for Runners using BATCH-A through E
			 *  generator.offerSplitRate = 0.5;
			 *  generator.additionalOfferRate = 0.2;
			 * 	generator.scale(10, 1, 0, 6, 0, 5, 0);
			 */
			
			/*	Upper Limit for the Incremental evaluation Scenario for Runners using BATCH-A, BATCH-F and BATCH-G, where the two latter of which will not find all optimal solutions.
			 *  generator.offerSplitRate = 0.0;
			 *  generator.additionalOfferRate = 0.25;	
			 *  generator.scale(10, 1, 0, 5, 0, 30, 0);
			 */
			int scale = 2;
			int run = 4;
			generator.scale(scale, 1, 0, 6, 0, 5, 0);
			//PersonTaskAssignmentModel model = generator.generate("EpicSeed".hashCode());
			// Seed used for Scale 1-10 with the exception of 7, 9 and 10: "EpicSeed"
			// Seed used for Scale 7: "EpicSeed12"
			// Seed used for Scale 9: "EpicSeed1"
			// Seed used for Scale 10: "EpicSeed2"
			PersonTaskAssignmentModel model = generator.generate("EpicSeed".hashCode());
			String runId = runner.getType()+"-inc-"+scale+"-"+run;

			System.out.println("##########\tRunning "+runId+" ...\t##########");
			Observer obs = Observer.getInstance();
			obs.setCurrentSeries(runId);
			obs.observe("INIT", ()->runner.init(model));
			EvaluationResult result;
			try {
				result = runner.run();
				System.out.println(result);
				results.put(runner.name, result);
				HouseConstructionHeadless.resultToCSV("./inc-eval-2024-11-13-1.csv", result, runner);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("##########\tFinished "+runId+".\t##########");
		});
	}

}
