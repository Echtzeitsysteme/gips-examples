package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;
import org.emoflon.gips.core.util.Observer;

import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.evaluation.util.EvaluationResult;
import pta.scenario.ScenarioRunner;
import pta.scenario.ScenarioValidator;

public abstract class HouseConstructionBatchGeneric<API extends GipsEngineAPI<?,?>> extends ScenarioRunner<API> {
	public HouseConstructionBatchGeneric(String name) {
		super(name);
	}

	@Override
	public EvaluationResult run() {
		api.buildILPProblemTimed(true);
		ILPSolverOutput output = api.solveILPProblemTimed();
		if(output.status() == ILPSolverStatus.OPTIMAL) {
			executeGT();
		}
		ScenarioValidator validator = new ScenarioValidator((PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0), output);
		validator.validate();
		api.terminate();
		
		Observer obs = Observer.getInstance();
		return new EvaluationResult(obs.getCurrentSeries(), validator, output, obs.getMeasurements(obs.getCurrentSeries()));
	}
	
	@Override
	public EvaluationResult run(String outputFile) throws IOException {
		api.buildILPProblemTimed(true);
		ILPSolverOutput output = api.solveILPProblemTimed();
		if(output.status() == ILPSolverStatus.OPTIMAL) {
			executeGT();
		}
		ScenarioValidator validator = new ScenarioValidator((PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0), output);
		validator.validate();
		api.saveResult(outputFile);
		api.terminate();
		
		Observer obs = Observer.getInstance();
		return new EvaluationResult(obs.getCurrentSeries(), validator, output, obs.getMeasurements(obs.getCurrentSeries()));
	}
	
	public abstract void executeGT();
}
