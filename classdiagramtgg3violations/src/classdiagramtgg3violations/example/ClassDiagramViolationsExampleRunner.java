package classdiagramtgg3violations.example;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;

import classdiagramtgg3violations.api.gips.Classdiagramtgg3violationsGipsAPI;

public class ClassDiagramViolationsExampleRunner {

	public static void main(final String[] args) {
		final Classdiagramtgg3violationsGipsAPI gipsApi = new Classdiagramtgg3violationsGipsAPI();

		final String projectFolder = System.getProperty("user.dir");
		final String scenarioName = "Example_small";
		final String file = projectFolder + "/../../TGG-3.0-Prototype/Refactoring/resources/classDiagram/"
				+ scenarioName + ".xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		final ILPSolverOutput output = gipsApi.solveILPProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Found violations: " + output.objectiveValue());

//		gipsApi.terminate();
		java.lang.System.exit(0);
	}

}
