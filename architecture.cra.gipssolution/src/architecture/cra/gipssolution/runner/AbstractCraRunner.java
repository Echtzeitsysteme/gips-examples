package architecture.cra.gipssolution.runner;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import architecture.cra.gipssolution.api.gips.GipssolutionGipsAPI;
import architecture.cra.gipssolution.utils.external.ArchitectureUtil;
import architecture.util.CRAIndexCalculator;
import architectureCRA.ArchitectureCRAPackage;
import architectureCRA.ClassModel;

/**
 * Abstract CRA runner class that contains most of the functionality to run a
 * CRA experiment.
 */
public abstract class AbstractCraRunner {

	/**
	 * Saves the result of a run of a given GIPS API to a given path as XMI file.
	 * 
	 * @param gipsApi GIPS API to save results from.
	 * @param path    (XMI) path to save the results to.
	 */
	protected void gipsSave(final GipssolutionGipsAPI gipsApi, final String path) {
		try {
			gipsApi.saveResult(path);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the given ResourceSet to an XMI file at the given file path.
	 * 
	 * @param path File path to save the ResourceSet's contents to.
	 * @param rs   ResourceSet which should be saved to file.
	 */
	protected void writeXmiToFile(final String path, final ResourceSet rs) {
		// Workaround: Always use absolute path
		final URI absPath = URI.createFileURI(path);

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
	}

	/**
	 * Builds and solves the ILP problem for the given GIPS API. Also prints the
	 * objective value to the console and throws an error if no solution could be
	 * found.
	 * 
	 * @param gipsApi GIPS API to build and solve the ILP problem for.
	 * @return Returns the objective value.
	 */
	protected double buildAndSolve(final GipssolutionGipsAPI gipsApi) {
		gipsApi.buildProblemTimed(true);
		final SolverOutput output = gipsApi.solveProblemTimed();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());
		System.out.println("---");
		return output.objectiveValue();
	}

	/**
	 * Checks if a file for the given path exists and throws an exception otherwise.
	 * 
	 * @param path Path to check the file existence for.
	 */
	protected void checkIfFileExists(final String path) {
		final File xmiInputFile = new File(path);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("Input XMI file <" + path + "> could not be found.");
		}
	}

	/**
	 * Creates the empty classes within the model.
	 * 
	 * @param xmiPath XMI path to read the initial model from.
	 * @return ResourceSet containing the altered model.
	 */
	protected ResourceSet createEmptyClasses(final String xmiPath) {
		final URI uri = URI.createFileURI(xmiPath);

		final ResourceSet rs = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));

		rs.getPackageRegistry().put(ArchitectureCRAPackage.eINSTANCE.getNsURI(), ArchitectureCRAPackage.eINSTANCE);
		rs.getResource(uri, true);

//		ArchitectureUtil.preProcess(rs.getResources().get(0));
		ArchitectureUtil.preProcessHalfNumberOfClasses(rs.getResources().get(0));
		return rs;
	}

	/**
	 * Prints and applies the best found solution (aka all non-zero mappings) with a
	 * given CRA GIPS API object.
	 * 
	 * @param gipsApi       CRA GIPS API object to get all mapping information from.
	 *                      This API will also be used to apply all non-zero
	 *                      mappings.
	 * @param printSolution If true, the solution will be printed to the console.
	 */
	protected void printAndApplySolution(final GipssolutionGipsAPI gipsApi, final boolean printSolution) {
		if (printSolution) {
			System.out.println("Embeddings (Attributes): ");
			gipsApi.getEmbedAttribute().getMappings().forEach((k, v) -> {
				if (v.getValue() == 1) {
					System.out.println("  " + v.getMatch().getA().getName() + " -> " + v.getMatch().getC().getName());
				}
			});

			System.out.println("Embeddings (Methods): ");
			gipsApi.getEmbedMethod().getMappings().forEach((k, v) -> {
				if (v.getValue() == 1) {
					System.out.println("  " + v.getMatch().getM().getName() + " -> " + v.getMatch().getC().getName());
				}
			});

		}

		// Apply found solution
		gipsApi.getEmbedAttribute().applyNonZeroMappings();
		gipsApi.getEmbedMethod().applyNonZeroMappings();
	}

	/**
	 * Counts and prints all violations. This method also does the post-processing
	 * (i.e., it removes all unused classes from the model).
	 * 
	 * @param gipsApi GIPS API to get information from.
	 */
	protected void countViolations(final GipssolutionGipsAPI gipsApi) {
		// Count violations
		final int violationsCounterGips = countViolationsGips(gipsApi);
		System.out.println("---");

		// Remove all empty classes (i.e., classes without an applied mapping)
		ArchitectureUtil.postProcess(gipsApi.getEMoflonAPI().getModel().getResources().get(0), false);

		// Evaluate model (with the `CRAIndexCalculator`)
		CRAIndexCalculator.evaluateModel(
				(ClassModel) gipsApi.getEMoflonAPI().getModel().getResources().get(0).getContents().get(0));

		// Evaluate model (with the violations counter by Lars)
		final ClassModel cm = (ClassModel) gipsApi.getEMoflonApp().getModel().getResources().get(0).getContents()
				.get(0);
		final int violationsCounterLars = ArchitectureUtil.countViolations(cm);
		System.out.println("---");
		System.out.println("#Violations (Lars): " + violationsCounterLars);
		System.out.println("#Violations (Max) : " + violationsCounterGips);
	}

	/**
	 * Counts all assignment violations via the given GIPS API.
	 * 
	 * @param gipsApi GIPS API to get the violations from.
	 * @return Number of assignment violations.
	 */
	private int countViolationsGips(final GipssolutionGipsAPI gipsApi) {
		// Violation counter
		int globalViolationsCounter = 0;

		// Violation A counter
		int mappingCounter = 0;
		for (var k : gipsApi.getViolationA().getMappings().keySet()) {
			if (gipsApi.getViolationA().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationA Counter:  " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		// Violation C counter
		mappingCounter = 0;
		for (var k : gipsApi.getViolationC().getMappings().keySet()) {
			if (gipsApi.getViolationC().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationC Counter:  " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		// Violation D1 counter
		mappingCounter = 0;
		for (var k : gipsApi.getViolationD1().getMappings().keySet()) {
			if (gipsApi.getViolationD1().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationD1 Counter: " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		// Violation D2 counter
		mappingCounter = 0;
		for (var k : gipsApi.getViolationD2().getMappings().keySet()) {
			if (gipsApi.getViolationD2().getMappings().get(k).getValue() == 1) {
				mappingCounter++;
			}
		}
		System.out.println("ViolationD2 Counter: " + mappingCounter);
		globalViolationsCounter += mappingCounter;

		return globalViolationsCounter;
	}

}
