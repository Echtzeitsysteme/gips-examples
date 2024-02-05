package pta.gipsl.simplified.example;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;

import PTAOptimizerSimplified.api.gips.PTAOptimizerSimplifiedGipsAPI;

public class SimplifiedPersonTaskAssignmentExample {

	public static void main(String[] args) {

		final PTAOptimizerSimplifiedGipsAPI gipsApi = new PTAOptimizerSimplifiedGipsAPI();
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final String file = instancesFolder + "/PersonSimplifiedExample.xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		gipsApi.solveILPProblem();

		// Print out all matches
		gipsApi.getReqToPerson().getMappings().forEach((s, m) -> {
			System.out.println(m.getMatch());
		});

		// Apply all selected rule matches
		gipsApi.getReqToPerson().applyNonZeroMappings();

		final String outputFile = instancesFolder + "/PersonSimplifiedExample_solved.xmi";
		try {
			gipsApi.saveResult(outputFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}

//		gipsApi.terminate();
		System.exit(0);
	}

}
