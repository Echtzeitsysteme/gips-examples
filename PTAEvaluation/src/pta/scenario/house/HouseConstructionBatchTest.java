package pta.scenario.house;

import java.io.IOException;

import org.emoflon.gips.core.ilp.ILPSolverOutput;

import PTAProblem.api.gips.PTAProblemGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import pta.scenario.ScenarioRunner;
import pta.scenario.ScenarioValidator;

public class HouseConstructionBatchTest extends ScenarioRunner<PTAProblemGipsAPI>{
	
	static public String projectFolder = System.getProperty("user.dir");
	static public String instancesFolder = projectFolder + "/instances/examples";

	public static void main(String[] args) {
		String file = instancesFolder + "/ConstructionProject1.xmi";		
		HouseConstructionBatchTest runner = new HouseConstructionBatchTest();
		runner.init(file);
		runner.run();
	}

	@Override
	public PTAProblemGipsAPI newAPI() {
		return new PTAProblemGipsAPI();
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
