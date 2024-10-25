package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;

import PTAConstraintConfigD.api.gips.PTAConstraintConfigDGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.generator.PTAModelGenerator;
import pta.scenario.ScenarioGenerator;
import pta.scenario.ScenarioRunner;
import pta.scenario.ScenarioValidator;

public class HouseConstructionBatchDTest extends ScenarioRunner<PTAConstraintConfigDGipsAPI>{
	
	static public String projectFolder = System.getProperty("user.dir");
	static public String instancesFolder = projectFolder + "/instances/examples";

	public static void main(String[] args) {
		String file = instancesFolder + "/RndEval1.xmi";		
		HouseConstructionBatchDTest runner = new HouseConstructionBatchDTest();
		//PersonTaskAssignmentModel model = new HouseConstructionGenerator("EpicSeed".hashCode()).constructNEvaluationProjects(2);
		ScenarioGenerator generator = new ScenarioGenerator();
		generator.nProjects = ScenarioGenerator.mkRange(10, 11);
		generator.tasksPerProject = ScenarioGenerator.mkRange(2, 6);
		generator.reqPerTask = ScenarioGenerator.mkRange(1, 4);
		generator.nSkills = ScenarioGenerator.mkRange(2, 5);
		PersonTaskAssignmentModel model = generator.generate("EpicSeed".hashCode());
		try {
			PTAModelGenerator.save(model, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		runner.init(file);
		runner.run();
		//System.exit(0);
	}

	@Override
	public PTAConstraintConfigDGipsAPI newAPI() {
		return new PTAConstraintConfigDGipsAPI();
	}

	@Override
	public void run() {
		api.buildILPProblem(true);
		ILPSolverOutput output = api.solveILPProblem();
		if(output.status() != ILPSolverStatus.OPTIMAL) {
			System.out.println("Solution could not be found.");
			System.out.println(output.status());
		}
		api.getAom().applyNonZeroMappings();
		api.getProjectCost().applyNonZeroMappings();
		
		ScenarioValidator validator = new ScenarioValidator((PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0));
		if(!validator.validate()) {
			System.out.println("++ Validator: Solution is invalid.");
		} else {
			System.out.println("++ Validator: Solution seems to be valid.");
		}
		System.out.println(validator.getLog());
		
		String outputFile = instancesFolder + "/RndEval1_solved.xmi";
		try {
			api.saveResult(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		api.terminate();
	}

}
