package pta.scenario.house;

import PTAConstraintConfigA.api.gips.PTAConstraintConfigAGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.scenario.EvaluationResult;
import pta.scenario.ScenarioGenerator;

public class HouseConstructionBatchATest extends HouseConstructionBatchGenericTest<PTAConstraintConfigAGipsAPI>{
	
	public HouseConstructionBatchATest(String name) {
		super(name);
	}

	public static void main(String[] args) {		
		HouseConstructionBatchATest runner = new HouseConstructionBatchATest("Batch-A");
		ScenarioGenerator generator = new ScenarioGenerator();
		generator.nProjects = ScenarioGenerator.mkRange(1, 2);
		generator.tasksPerProject = ScenarioGenerator.mkRange(4, 8);
		generator.reqPerTask = ScenarioGenerator.mkRange(1, 6);
		PersonTaskAssignmentModel model = generator.generate("EpicSeed".hashCode());

		runner.init(model);
		EvaluationResult result = runner.run();
		System.out.println(result);
		//System.exit(0);
	}

	@Override
	public PTAConstraintConfigAGipsAPI newAPI() {
		return new PTAConstraintConfigAGipsAPI();
	}

	@Override
	public void executeGT() {
		api.getAom().applyNonZeroMappings();
		api.getProjectCost().applyNonZeroMappings();
	}

}
