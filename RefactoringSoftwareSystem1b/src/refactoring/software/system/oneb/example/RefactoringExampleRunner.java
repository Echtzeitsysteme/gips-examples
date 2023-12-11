package refactoring.software.system.oneb.example;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;

import RefactoringSoftwareSystem1b.api.gips.RefactoringSoftwareSystem1bGipsAPI;

public class RefactoringExampleRunner {

	public static void main(final String[] args) {
		final RefactoringSoftwareSystem1bGipsAPI gipsApi = new RefactoringSoftwareSystem1bGipsAPI();
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final String scenarioName = "TestSystem1_adapted";
		final String file = projectFolder + "/resources/" + scenarioName + ".xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		final ILPSolverOutput output = gipsApi.solveILPProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());

//		gipsApi.getRemovePreexistingEdges().applyNonZeroMappings();
		gipsApi.getUsedSystem().applyNonZeroMappings();
		gipsApi.getC2s().applyNonZeroMappings();

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
