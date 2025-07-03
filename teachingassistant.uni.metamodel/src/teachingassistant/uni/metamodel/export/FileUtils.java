package teachingassistant.uni.metamodel.export;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import metamodel.MetamodelPackage;
import metamodel.TaAllocation;
import teachingassistant.uni.utils.AbstractFileUtils;

/**
 * File utilities for loading and saving files.
 *
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class FileUtils extends AbstractFileUtils {

	/**
	 * Private constructor to forbid instantiation of objects.
	 */
	private FileUtils() {
	}

	/**
	 * Saves the given model as XMI to the given path.
	 * 
	 * @param model Model to be saved.
	 * @param path  Output (XMI) path for the model to be saved at.
	 * @throws IOException Throws an IOException if the file could not be written.
	 */
	public static void save(final TaAllocation model, final String path) throws IOException {
		Objects.requireNonNull(model);
		Objects.requireNonNull(path);
		final Resource r = saveAndReturn(model, path);
		Objects.requireNonNull(r);
		r.unload();
	}

	/**
	 * Saves the given model as XMI to the given path and returns the respective
	 * resource.
	 * 
	 * @param model Model to be saved.
	 * @param path  Output (XMI) path for the model to be saved at.
	 * @return Respective resource of the model.
	 * @throws IOException Throws an IOException if the file could not be written.
	 */
	public static Resource saveAndReturn(final TaAllocation model, final String path) throws IOException {
		Objects.requireNonNull(model);
		Objects.requireNonNull(path);

		final URI uri = URI.createFileURI(path);
		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		final Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}

	/**
	 * Loads an EMF resource from a given (XMI) file path.
	 * 
	 * @param path (XMI) file path to load resource from.
	 * @return Loaded resource.
	 */
	public static Resource loadModel(final String path) {
		Objects.requireNonNull(path);
		final URI pathUri = URI.createFileURI(path);
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		resourceSet.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
		resourceSet.getResource(pathUri, true);
		return resourceSet.getResources().getFirst();
	}

}
