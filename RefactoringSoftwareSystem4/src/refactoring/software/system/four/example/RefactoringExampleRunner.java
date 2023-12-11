package refactoring.software.system.four.example;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;

import RefactoringSoftwareSystem4.api.gips.RefactoringSoftwareSystem4GipsAPI;

public class RefactoringExampleRunner {

	public static void main(final String[] args) {
		final RefactoringSoftwareSystem4GipsAPI gipsApi = new RefactoringSoftwareSystem4GipsAPI();
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final String scenarioName = "TestSystem1";
		final String file = projectFolder + "/../../TGG-3.0-Prototype/Refactoring/resources/" + scenarioName + ".xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		final ILPSolverOutput output = gipsApi.solveILPProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());

		gipsApi.getAddCtoS().applyNonZeroMappings();

		final String outputFile = instancesFolder + "/" + scenarioName + "_solved.xmi";
		try {
			gipsApi.saveResult(outputFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		gipsApi.terminate();
		java.lang.System.exit(0);
	}

}
