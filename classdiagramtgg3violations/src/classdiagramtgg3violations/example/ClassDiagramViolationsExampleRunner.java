package classdiagramtgg3violations.example;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.milp.SolverOutput;

import classdiagramtgg3violations.api.gips.Classdiagramtgg3violationsGipsAPI;

public class ClassDiagramViolationsExampleRunner {

	public static void main(final String[] args) {
		final Classdiagramtgg3violationsGipsAPI gipsApi = new Classdiagramtgg3violationsGipsAPI();

		final String projectFolder = System.getProperty("user.dir");
		final String scenarioName = "Example_small";
		final String file = projectFolder + "/../../TGG-3.0-Prototype/RefactoringAC/resources/classDiagram/"
				+ scenarioName + ".xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildProblem(true);
		final SolverOutput output = gipsApi.solveProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Found violations: " + output.objectiveValue());

		gipsApi.terminate();
		java.lang.System.exit(0);
	}

}
