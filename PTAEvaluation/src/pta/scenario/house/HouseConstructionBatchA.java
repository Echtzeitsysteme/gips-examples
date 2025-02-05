package pta.scenario.house;

import java.io.IOException;

import PTAConstraintConfigA.api.gips.PTAConstraintConfigAGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.evaluation.util.EvaluationResult;
import pta.scenario.ScenarioGenerator;

public class HouseConstructionBatchA extends HouseConstructionGeneric<PTAConstraintConfigAGipsAPI> {

	final public static String TYPE = "BATCH-A";

	public HouseConstructionBatchA(String name) {
		super(name);
	}

	public static void main(String[] args) {
		HouseConstructionBatchA runner = new HouseConstructionBatchA("Batch-A");
		ScenarioGenerator generator = new ScenarioGenerator();
		generator.nProjects = ScenarioGenerator.mkRange(1, 2);
		generator.tasksPerProject = ScenarioGenerator.mkRange(4, 8);
		generator.reqPerTask = ScenarioGenerator.mkRange(1, 6);
		PersonTaskAssignmentModel model = generator.generate("EpicSeed".hashCode());

		runner.init(model);
		EvaluationResult result;
		try {
			result = runner.run();
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public PTAConstraintConfigAGipsAPI newAPI() {
		return new PTAConstraintConfigAGipsAPI();
	}

	@Override
	public void executeGT() {
		api.getAom().applyNonZeroMappings(false);
		api.getProjectCost().applyNonZeroMappings(false);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getGipsModelPath() {
		return "PTAConstraintConfigA/api/gips/gips-model.xmi";
	}

	@Override
	public String getIbexModelPath() {
		return "PTAConstraintConfigA/api/ibex-patterns.xmi";
	}

	@Override
	public String getHiPEModelPath() {
		return "PTAConstraintConfigA/hipe/engine/hipe-network.xmi";
	}

	@Override
	public String getHiPEEngineFQN() {
		return "PTAConstraintConfigA.hipe.engine.HiPEEngine";
	}

}
