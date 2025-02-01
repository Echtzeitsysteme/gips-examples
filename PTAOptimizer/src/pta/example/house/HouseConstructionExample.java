package pta.example.house;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.milp.SolverOutput;

import PTAOptimizer.api.gips.PTAOptimizerGipsAPI;

public class HouseConstructionExample {

	public static void main(String[] args) {

		PTAOptimizerGipsAPI gipsApi = new PTAOptimizerGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/ConstructionProject1.xmi";

		// Generate input XMI file if it was not already created before
		if (!Files.exists(Path.of(file))) {
			HouseConstructionGenerator.main(null);
		}

		URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

//		PTAOptimizerApp app = new PTAOptimizerHiPEApp();
//		app.registerMetaModels();
//		app.setModel(gipsApi.getEMoflonAPI().getModel());
//		PTAOptimizerAPI api = app.initAPI();
//		api.updateMatches();

		gipsApi.buildProblem(true);
		SolverOutput output = gipsApi.solveProblem();
		gipsApi.getAom().applyNonZeroMappings();

		String outputFile = instancesFolder + "/ConstructionProject1_solved.xmi";
		try {
			gipsApi.saveResult(outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// If GIPS created a solution file, run the validator
		if (Files.exists(Path.of(outputFile))) {
			HouseConstructionValidator.main(null);
		}

		gipsApi.terminate();
		System.exit(0);
	}

}
