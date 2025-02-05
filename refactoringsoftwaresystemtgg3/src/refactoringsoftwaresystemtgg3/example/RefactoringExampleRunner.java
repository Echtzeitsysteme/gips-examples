package refactoringsoftwaresystemtgg3.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.milp.SolverOutput;

import refactoringsoftwaresystemtgg3.api.gips.Refactoringsoftwaresystemtgg3GipsAPI;

public class RefactoringExampleRunner {

	public static void main(final String[] args) {
		final Refactoringsoftwaresystemtgg3GipsAPI gipsApi = new Refactoringsoftwaresystemtgg3GipsAPI();
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final String scenarioName = "TestSystem1";
		final String file = projectFolder + "/../../TGG-3.0-Prototype/RefactoringAC/resources/softwareSystem/"
				+ scenarioName + ".xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildProblem(true);
		final SolverOutput output = gipsApi.solveProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());

		System.out.println("Embeddings: ");
		gipsApi.getEmbed().getMappings().forEach((k, v) -> {
			if (v.getValue() == 1) {
				System.out.println("  " + v.getMatch().getC().getName() + " -> " + v.getMatch().getSnew().getName());
			}
		});

		gipsApi.getEmbed().applyNonZeroMappings();

		final String outputFile = instancesFolder + "/" + scenarioName + "_solved.xmi";
		try {
			Files.createDirectories(Paths.get(instancesFolder));
			gipsApi.saveResult(outputFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		gipsApi.terminate();
		java.lang.System.exit(0);
	}

}
