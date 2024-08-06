package architecture.cra.gipssolution.utils.external;

import java.util.HashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.smartemf.runtime.util.SmartEMFUtil;

import architectureCRA.ArchitectureCRAFactory;
import architectureCRA.ClassModel;

/**
 * 
 * @author Lars Fritsche
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

			var newClass = ArchitectureCRAFactory.eINSTANCE.createClass();
			newClass.setName("c_" + feature.getName());
			model.getClasses().add(newClass);
			newClass.getEncapsulates().add(feature);
		}
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

}
