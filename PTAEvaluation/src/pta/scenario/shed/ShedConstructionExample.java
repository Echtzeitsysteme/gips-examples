package pta.scenario.shed;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;

import PTAProblem.api.gips.PTAProblemGipsAPI;

public class ShedConstructionExample {

	public static void main(String[] args) {

		PTAProblemGipsAPI gipsApi = new PTAProblemGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/dissertation_full_example.xmi";
		URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		ILPSolverOutput output = gipsApi.solveILPProblem();
		gipsApi.getAom().applyNonZeroMappings();
		gipsApi.getProjectCost().applyNonZeroMappings();

		String outputFile = instancesFolder + "/dissertation_full_example_solved.xmi";
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
