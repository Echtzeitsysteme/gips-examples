package architecture.cra.gipssolution.utils.external;

import java.util.HashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.smartemf.runtime.util.SmartEMFUtil;

import architectureCRA.ArchitectureCRAFactory;
import architectureCRA.Attribute;
import architectureCRA.ClassModel;
import architectureCRA.Methodd;

/**
 * 
 * @author Lars Fritsche
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 *
 */
public class ArchitectureUtil {

	/**
	 * Create classes for each feature
	 * 
	 * @param resource the resource that contains all features
	 */
	public static void preProcess(Resource resource) {
		ClassModel model = (ClassModel) resource.getContents().get(0);

		for (var feature : model.getFeatures()) {
			if (feature.getIsEncapsulatedBy() != null)
				continue;

			var newClass = ArchitectureCRAFactory.eINSTANCE.createClazz();
			newClass.setName("c_" + feature.getName());
			model.getClasses().add(newClass);
//			newClass.getEncapsulates().add(feature);
		}
	}

	/**
	 * Creates n empty classes in the given ClassModel.
	 * 
	 * @param resource Resource that contains the given ClassModel.
	 * @param n        Number of classes to create.
	 */
	public static void preProcessNClasses(final Resource resource, final int n) {
		final ClassModel model = (ClassModel) resource.getContents().get(0);

		for (int i = 0; i < n; i++) {
			var newClass = ArchitectureCRAFactory.eINSTANCE.createClazz();
			newClass.setName("c_" + i);
			model.getClasses().add(newClass);
		}
	}

	/**
	 * Creates half as much empty classes in the given ClassModel as there are
	 * features.
	 * 
	 * @param resource Resource that contains the given ClassModel.
	 */
	public static void preProcessHalfNumberOfClasses(final Resource resource) {
		final ClassModel model = (ClassModel) resource.getContents().get(0);
		preProcessNClasses(resource, model.getFeatures().size() / 2);
	}

	/**
	 * Remove all classes that contain no features
	 * 
	 * @param resource the resource that contains the result
	 */
	public static void postProcess(Resource resource, boolean leaveOneEmpty) {
		ClassModel model = (ClassModel) resource.getContents().get(0);
		var toBeRemoved = new HashSet<EObject>();

		var deleteEmptyClass = !leaveOneEmpty;
		for (var clazz : model.getClasses()) {
			if (clazz.getEncapsulates().isEmpty()) {
				if (deleteEmptyClass)
					toBeRemoved.add(clazz);
				else
					deleteEmptyClass = true;
			}
		}

		SmartEMFUtil.deleteNodes(toBeRemoved, false);
	}

	public static int countModelElements(Resource resource) {
		ClassModel model = (ClassModel) resource.getContents().get(0);
		return model.getClasses().size() + model.getFeatures().size();
	}

	public static int countViolations(ClassModel model) {
		var violations = 0;

		for (var clazz : model.getClasses()) {
			var numOfMethods = clazz.getEncapsulates().stream().filter(f -> f instanceof Methodd).count();
			var numOfAttribtutes = clazz.getEncapsulates().size() - numOfMethods;
			var cohesionViolations = numOfMethods * (numOfMethods - 1) + numOfMethods * numOfAttribtutes;
			for (var feature : clazz.getEncapsulates()) {

				if (feature instanceof Methodd method) {
					var methodCohesionMatches = 0;
					// first we count coupling violations
					for (Attribute dataDependency : method.getDataDependency()) {
						if (!dataDependency.getIsEncapsulatedBy().equals(method.getIsEncapsulatedBy())) {
							violations++;
						} else {
							methodCohesionMatches++;
						}
					}

					for (var functionalDependency : method.getFunctionalDependency()) {
						if (!functionalDependency.getIsEncapsulatedBy().equals(method.getIsEncapsulatedBy())) {
							violations++;
						} else {
							methodCohesionMatches++;
						}
					}

					// then we count cohesion violations
					cohesionViolations -= methodCohesionMatches;
				}

			}

			violations += cohesionViolations;

		}
		return violations;
	}

}
