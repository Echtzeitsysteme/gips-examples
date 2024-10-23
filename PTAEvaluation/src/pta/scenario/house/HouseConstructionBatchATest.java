package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.ilp.ILPSolverOutput;
import PTAConstraintConfigA.api.gips.PTAConstraintConfigAGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.scenario.ScenarioRunner;
import pta.scenario.ScenarioValidator;

public class HouseConstructionBatchATest extends ScenarioRunner<PTAConstraintConfigAGipsAPI>{
	
	static public String projectFolder = System.getProperty("user.dir");
	static public String instancesFolder = projectFolder + "/instances/examples";

	public static void main(String[] args) {
		String file = instancesFolder + "/ConstructionProject1.xmi";		
		HouseConstructionBatchATest runner = new HouseConstructionBatchATest();
		runner.init(file);
		runner.run();
		//System.exit(0);
	}

	@Override
	public PTAConstraintConfigAGipsAPI newAPI() {
		return new PTAConstraintConfigAGipsAPI();
	}

	@Override
	public void run() {
		api.buildILPProblem(true);
		ILPSolverOutput output = api.solveILPProblem();
		api.getAom().applyNonZeroMappings();
		api.getProjectCost().applyNonZeroMappings();
		
		ScenarioValidator validator = new ScenarioValidator((PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0));
		if(!validator.validate()) {
			System.out.println("++ Validator: Solution is invalid.");
		} else {
			System.out.println("++ Validator: Solution seems to be valid.");
		}
		System.out.println(validator.getLog());
		
		String outputFile = instancesFolder + "/ConstructionProject1_solved.xmi";
		try {
			api.saveResult(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		api.terminate();
	}

}
