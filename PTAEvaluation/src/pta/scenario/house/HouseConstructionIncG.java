package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.util.Observer;

import PTAConstraintConfigG.api.gips.PTAConstraintConfigGGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Task;
import pta.evaluation.util.EvaluationResult;
import pta.evaluation.util.SolverOutput;
import pta.scenario.ScenarioValidator;

public class HouseConstructionIncG extends HouseConstructionGeneric<PTAConstraintConfigGGipsAPI> {

	final public static String TYPE = "INC-G";

	public HouseConstructionIncG(String name) {
		super(name);
	}

	@Override
	public PTAConstraintConfigGGipsAPI newAPI() {
		return new PTAConstraintConfigGGipsAPI();
	}

	@Override
	public EvaluationResult run(String evalId, String outputFile) throws IOException {
		Observer obs = new Observer();

		PersonTaskAssignmentModel model = (PersonTaskAssignmentModel) api.getEMoflonAPI().getModel().getResources()
				.get(0).getContents().get(0);
		SolverOutput output = new SolverOutput();

		for (Project p : model.getProjects()) {
			for (Task t : p.getTasks()) {
				double cost = p.getSumSalary();
				p.setSumSalary(0.0);
				api.getAom().getGTRule().bindProject(p);
				api.getAom().getGTRule().bindTask(t);
				api.getProjectCost().getGTRule().bindProject(p);
				api.getEMoflonAPI().taskToRequirement().bindTask(t);
				api.buildProblem(true);
				org.emoflon.gips.core.milp.SolverOutput out = api.solveProblem();

				if (out.status() == org.emoflon.gips.core.milp.SolverStatus.OPTIMAL) {
					obs.multiMeasurement("APPLY", "APPLY", () -> executeGT());
					p.setSumSalary(p.getSumSalary() + cost);
				}

				obs.merge(api.getLatestMetrics().measurements());

				output.addOutput(t, out);
			}
		}

		ScenarioValidator validator = new ScenarioValidator(
				(PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0),
				output);
		validator.validate();

		if (outputFile != null && !outputFile.isBlank() && !outputFile.isEmpty()) {
			api.saveResult(outputFile);
		}

		api.terminate();
		return new EvaluationResult(evalId, validator, output, obs.getAllStagesMerged());
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
		return "PTAConstraintConfigG/api/gips/gips-model.xmi";
	}

	@Override
	public String getIbexModelPath() {
		return "PTAConstraintConfigG/api/ibex-patterns.xmi";
	}

	@Override
	public String getHiPEModelPath() {
		return "PTAConstraintConfigG/hipe/engine/hipe-network.xmi";
	}

	@Override
	public String getHiPEEngineFQN() {
		return "PTAConstraintConfigG.hipe.engine.HiPEEngine";
	}
}
