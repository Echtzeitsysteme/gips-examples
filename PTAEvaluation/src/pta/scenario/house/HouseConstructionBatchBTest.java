package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.ilp.ILPSolverOutput;
import PTAConstraintConfigB.api.gips.PTAConstraintConfigBGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.scenario.ScenarioRunner;
import pta.scenario.ScenarioValidator;

public class HouseConstructionBatchBTest extends ScenarioRunner<PTAConstraintConfigBGipsAPI>{
	
	static public String projectFolder = System.getProperty("user.dir");
	static public String instancesFolder = projectFolder + "/instances/examples";

	public static void main(String[] args) {
		String file = instancesFolder + "/ConstructionProject1.xmi";		
		HouseConstructionBatchBTest runner = new HouseConstructionBatchBTest();
		runner.init(file);
		runner.run();
		//System.exit(0);
	}

	@Override
	public PTAConstraintConfigBGipsAPI newAPI() {
		return new PTAConstraintConfigBGipsAPI();
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
