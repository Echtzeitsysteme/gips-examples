package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;
import org.emoflon.gips.core.util.Observer;

import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.evaluation.util.EvaluationResult;
import pta.evaluation.util.SolverOutput;
import pta.scenario.ScenarioRunner;
import pta.scenario.ScenarioValidator;

public abstract class HouseConstructionGeneric<API extends GipsEngineAPI<?, ?>> extends ScenarioRunner<API> {
	public HouseConstructionGeneric(String name) {
		super(name);
	}

	@Override
	public EvaluationResult run() throws IOException {
		return run("");
	}

	@Override
	public EvaluationResult run(String outputFile) throws IOException {
		Observer obs = Observer.getInstance();
		api.buildILPProblemTimed(true);
		ILPSolverOutput output = api.solveILPProblemTimed();
		if (output.status() == ILPSolverStatus.OPTIMAL) {
			obs.observe("APPLY", () -> executeGT());
		}

		PersonTaskAssignmentModel model = (PersonTaskAssignmentModel) api.getEMoflonAPI().getModel().getResources()
				.get(0).getContents().get(0);
		ScenarioValidator validator = new ScenarioValidator(
				(PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0),
				new SolverOutput(model, output));
		validator.validate();

		if (outputFile != null && !outputFile.isBlank() && !outputFile.isEmpty()) {
			api.saveResult(outputFile);
		}

		api.terminate();
		return new EvaluationResult(obs.getCurrentSeries(), validator, new SolverOutput(output),
				obs.getMeasurements(obs.getCurrentSeries()));
	}

	public abstract void executeGT();
}
