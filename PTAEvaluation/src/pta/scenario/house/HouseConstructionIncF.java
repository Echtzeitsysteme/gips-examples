package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;
import org.emoflon.gips.core.util.Observer;

import PTAConstraintConfigF.api.gips.PTAConstraintConfigFGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.Project;
import pta.evaluation.util.EvaluationResult;
import pta.evaluation.util.SolverOutput;
import pta.scenario.ScenarioValidator;

public class HouseConstructionIncF extends HouseConstructionGeneric<PTAConstraintConfigFGipsAPI> {

	final public static String TYPE = "INC-F";

	public HouseConstructionIncF(String name) {
		super(name);
	}

	@Override
	public PTAConstraintConfigFGipsAPI newAPI() {
		return new PTAConstraintConfigFGipsAPI();
	}

	@Override
	public EvaluationResult run(String outputFile) throws IOException {
		Observer obs = Observer.getInstance();
		PersonTaskAssignmentModel model = (PersonTaskAssignmentModel) api.getEMoflonAPI().getModel().getResources()
				.get(0).getContents().get(0);
		SolverOutput output = new SolverOutput();
		for (Project p : model.getProjects()) {
			p.setSumSalary(0.0);
			api.getAom().getGTRule().bindProject(p);
			api.getProjectCost().getGTRule().bindProject(p);
			api.getEMoflonAPI().taskToRequirement().bindProject(p);
			api.getEMoflonAPI().taskSequence().bindProject(p);
			api.getEMoflonAPI().finalTaskSequence().bindProject(p);
			api.buildILPProblemTimed(true);
			ILPSolverOutput out = api.solveILPProblemTimed();
			if (out.status() == ILPSolverStatus.OPTIMAL) {
				obs.observe("APPLY", () -> executeGT());
			}
			output.addOutput(p, out);
		}

		ScenarioValidator validator = new ScenarioValidator(
				(PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0),
				output);
		validator.validate();

		if (outputFile != null && !outputFile.isBlank() && !outputFile.isEmpty()) {
			api.saveResult(outputFile);
		}
		api.terminate();

		return new EvaluationResult(obs.getCurrentSeries(), validator, output,
				obs.getMeasurements(obs.getCurrentSeries()));
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
		return "PTAConstraintConfigF/api/gips/gips-model.xmi";
	}

	@Override
	public String getIbexModelPath() {
		return "PTAConstraintConfigF/api/ibex-patterns.xmi";
	}

	@Override
	public String getHiPEModelPath() {
		return "PTAConstraintConfigF/hipe/engine/hipe-network.xmi";
	}

	@Override
	public String getHiPEEngineFQN() {
		return "PTAConstraintConfigF.hipe.engine.HiPEEngine";
	}
}
