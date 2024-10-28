package pta.scenario.house;

import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;

import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.scenario.EvaluationResult;
import pta.scenario.ScenarioRunner;
import pta.scenario.ScenarioValidator;

public abstract class HouseConstructionBatchGenericTest<API extends GipsEngineAPI<?,?>> extends ScenarioRunner<API> {
	public HouseConstructionBatchGenericTest(String name) {
		super(name);
	}

	@Override
	public EvaluationResult run() {
		api.buildILPProblem(true);
		ILPSolverOutput output = api.solveILPProblem();
		if(output.status() == ILPSolverStatus.OPTIMAL) {
			executeGT();
		}
		ScenarioValidator validator = new ScenarioValidator((PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0), output);
		validator.validate();
		api.terminate();
		
		return new EvaluationResult(validator, output);
	}
	
	public abstract void executeGT();
}
