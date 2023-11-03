package pta.example.house;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;

import PTAOptimizer.api.gips.PTAOptimizerGipsAPI;

public class ShedConstructionExample {

	public static void main(String[] args) {

		final PTAOptimizerGipsAPI gipsApi = new PTAOptimizerGipsAPI();
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final String file = instancesFolder + "./ConstructionSimpleProject.xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		gipsApi.solveILPProblem();
		gipsApi.getAom().applyNonZeroMappings();

		final String outputFile = instancesFolder + "./ConstructionSimpleProject_solved.xmi";
		try {
			gipsApi.saveResult(outputFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		gipsApi.terminate();
		System.exit(0);
	}

}
