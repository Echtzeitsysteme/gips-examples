package pta.example.house;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;

import PTAOptimizer.api.PTAOptimizerAPI;
import PTAOptimizer.api.PTAOptimizerApp;
import PTAOptimizer.api.PTAOptimizerHiPEApp;
import PTAOptimizer.api.gips.PTAOptimizerGipsAPI;
import PersonTaskAssignments.PersonTaskAssignmentModel;

public class HouseConstructionExample {

	public static void main(String[] args) {

		
		PTAOptimizerGipsAPI gipsApi = new PTAOptimizerGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/ConstructionProject1.xmi";
		URI uri = URI.createFileURI(file);
		gipsApi.init(uri);
		
//		PTAOptimizerApp app = new PTAOptimizerHiPEApp();
//		app.registerMetaModels();
//		app.setModel(gipsApi.getEMoflonAPI().getModel());
//		PTAOptimizerAPI api = app.initAPI();
//		api.updateMatches();
		
		gipsApi.buildILPProblem(true);
		ILPSolverOutput output = gipsApi.solveILPProblem();
		gipsApi.getAom().applyNonZeroMappings();
		
		String outputFile = instancesFolder + "/ConstructionProject1_solved.xmi";
		try {
			gipsApi.saveResult(outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gipsApi.terminate();
		System.exit(0);
	}

}
