package classdiagramtgg3.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.milp.SolverOutput;

import classdiagramtgg3.api.gips.Classdiagramtgg3GipsAPI;

public class ClassDiagramExampleRunner {

	public static void main(final String[] args) {
		final Classdiagramtgg3GipsAPI gipsApi = new Classdiagramtgg3GipsAPI();

		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
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
		System.out.println("=> Objective value: " + output.objectiveValue());

		System.out.println("---");

		System.out.println("Violation Mappings: ");
		gipsApi.getViolationA().getMappings().forEach((k, v) -> {
			System.out.println(v.getValue() + ": " + v.getA1().getName() + " -> " + v.getC1().getName() + "; "
					+ v.getM1().getName() + " -> " + v.getC2().getName());
		});

		System.out.println("Embeddings (Attributes): ");
		gipsApi.getEmbedAttribute().getMappings().forEach((k, v) -> {
			if (v.getValue() == 1) {
				System.out.println("  " + v.getMatch().getA().getName() + " -> " + v.getMatch().getC().getName());
			}
		});
		gipsApi.getEmbedAttribute().applyNonZeroMappings();

		System.out.println("Embeddings (Methods): ");
		gipsApi.getEmbedMethod().getMappings().forEach((k, v) -> {
			if (v.getValue() == 1) {
				System.out.println("  " + v.getMatch().getM().getName() + " -> " + v.getMatch().getC().getName());
			}
		});
		gipsApi.getEmbedMethod().applyNonZeroMappings();

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
