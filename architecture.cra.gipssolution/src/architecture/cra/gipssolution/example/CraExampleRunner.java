package architecture.cra.gipssolution.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.gips.core.ilp.ILPSolverOutput;

import architecture.cra.gipssolution.api.gips.GipssolutionGipsAPI;
import architecture.cra.gipssolution.utils.external.ArchitectureUtil;
import architecture.util.CRAIndexCalculator;
import architectureCRA.ClassModel;

public class CraExampleRunner {

	public static void main(final String[] args) {
		final GipssolutionGipsAPI gipsApi = new GipssolutionGipsAPI();

		// Load a XMI model
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final String scenarioName = "TTC_InputRDG_A";
		final String file = projectFolder + "/../../TGG-3.0-Prototype/RefactoringAC/resources/architecture/"
				+ scenarioName + ".xmi";
		final URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		// Create empty classes in the model
		final Resource model = gipsApi.getEMoflonAPI().getModel().getResources().get(0);
		ArchitectureUtil.preProcess(model);

		// Solve
		gipsApi.buildILPProblem(true);
		final ILPSolverOutput output = gipsApi.solveILPProblem();
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
		// TODO: Sysouts for Violation C
		// TODO: Sysouts for Violation D1
		// TODO: Sysouts for Violation D2

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

		// Remove all empty classes (i.e., classes without an applied mapping)
		ArchitectureUtil.postProcess(model, false);

		// Evaluate model (with the `CRAIndexCalculator`
		CRAIndexCalculator.evaluateModel((ClassModel) model.getContents().get(0));

		// Save model to XMI file
		final String outputFile = instancesFolder + "/" + scenarioName + "_solved.xmi";
		try {
			Files.createDirectories(Paths.get(instancesFolder));
			gipsApi.saveResult(outputFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// The end
		gipsApi.terminate();
		java.lang.System.exit(0);
	}

}
