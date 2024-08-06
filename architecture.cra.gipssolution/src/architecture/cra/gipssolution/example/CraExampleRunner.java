package architecture.cra.gipssolution.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import architecture.cra.gipssolution.api.gips.GipssolutionGipsAPI;
import architecture.cra.gipssolution.utils.external.ArchitectureUtil;
import architecture.util.CRAIndexCalculator;
import architectureCRA.ArchitectureCRAPackage;
import architectureCRA.ClassModel;

public class CraExampleRunner {

	public static void main(final String[] args) {
		//
		// Load a XMI model
		//

		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/instances";
		final String scenarioName = "TTC_InputRDG_A";
		final String file = projectFolder + "/../../TGG-3.0-Prototype/RefactoringAC/resources/architecture/"
				+ scenarioName + ".xmi";
		final URI uri = URI.createFileURI(file);

		final ResourceSet rs = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));

		rs.getPackageRegistry().put(ArchitectureCRAPackage.eINSTANCE.getNsURI(), ArchitectureCRAPackage.eINSTANCE);
		rs.getResource(uri, true);

		//
		// Create empty classes in the model
		//

		ArchitectureUtil.preProcess(rs.getResources().get(0));

		//
		// Write changed model to file
		//

		// Workaround: Always use absolute path
		final URI absPath = URI.createFileURI(System.getProperty("user.dir") + "/" + "preprocessed.xmi");

		// Create new model for saving
		final ResourceSet rs2 = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl(null));
		// ^null is okay if all paths are absolute
		final Resource r = rs2.createResource(absPath);
		// Fetch model contents from eMoflon
		r.getContents().add(rs.getResources().get(0).getContents().get(0));
		try {
			r.save(null);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		//
		// Init GIPS API
		//

		GipssolutionGipsAPI gipsApi = new GipssolutionGipsAPI();
//		gipsApi.init(rs);
		gipsApi.init(absPath);

//		gipsApi.update();
//		var test = gipsApi.getEMoflonAPI().getInterpreter().getMatches();

		//
		// Solve
		//

		gipsApi.buildILPProblem(true);
		final ILPSolverOutput output = gipsApi.solveILPProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());

		System.out.println("---");

		//
		// Evaluation
		//

//		System.out.println("Violation Mappings: ");
//		gipsApi.getViolationA().getMappings().forEach((k, v) -> {
//			System.out.println(v.getValue() + ": " + v.getA1().getName() + " -> " + v.getC1().getName() + "; "
//					+ v.getM1().getName() + " -> " + v.getC2().getName());
//		});
//		// TODO: Sysouts for Violation C
//		// TODO: Sysouts for Violation D1
//		// TODO: Sysouts for Violation D2

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
		ArchitectureUtil.postProcess(gipsApi.getEMoflonAPI().getModel().getResources().get(0), false);

		// Evaluate model (with the `CRAIndexCalculator`
		CRAIndexCalculator.evaluateModel(
				(ClassModel) gipsApi.getEMoflonAPI().getModel().getResources().get(0).getContents().get(0));

		//
		// Save model to XMI file
		//

//		final String outputFile = instancesFolder + "/" + scenarioName + "_solved.xmi";
		final String outputFile = "./solved.xmi";
		try {
			Files.createDirectories(Paths.get(instancesFolder));
			gipsApi.saveResult(outputFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		//
		// The end
		//

		gipsApi.terminate();
		java.lang.System.exit(0);
	}

}
