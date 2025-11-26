package teachingassistant.uni.metamodel.generator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import com.google.common.base.Preconditions;

import metamodel.MetamodelFactory;
import metamodel.MetamodelPackage;
import metamodel.Module;
import metamodel.TaAllocation;
import metamodel.TeachingAssistant;

/**
 * Parent generator class that sets up basic structures and utilities for
 * generating a TaAllocation model. Updated to remove or rename any fields that
 * do not exist in the alternative metamodel.
 */
public class TeachingAssistantUniGenerator {

	// Factory for creating model elements
	protected MetamodelFactory factory = MetamodelFactory.eINSTANCE;
	// Root model object
	protected TaAllocation root;

	// Mappings from code/name => model objects (just for convenience)
	protected Map<String, TeachingAssistant> tas = new LinkedHashMap<>();
	protected Map<String, Module> modules = new LinkedHashMap<>();

	// Random generator for producing synthetic data
	protected Random rand;

	/**
	 * Prepares (creates) a folder path for storing the generated XMI/JSON. Adjust
	 * if you want a different output location.
	 */
	protected String prepareFolder() {
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/../teachingassistant.uni.metamodel/instances";
		final File f = new File(instancesFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		return instancesFolder;
	}

	/**
	 * Utility: produce an integer between [min..max], inclusive.
	 */
	protected int getRandInt(final int min, final int max) {
		Preconditions.checkArgument(min <= max, "min must be <= max");
		return rand.nextInt((max - min) + 1) + min;
	}

	/**
	 * Returns a random integer between [min..max], inclusive, but never an integer
	 * that is part of the given set of blocked values.
	 * 
	 * @param min     Minimum value.
	 * @param max     Maximum value.
	 * @param blocked Set of already taken values, i.e., these values must not be
	 *                returned.
	 * @return Random number between [min..max], inclusive, without the values in
	 *         `blocked`.
	 */
	protected int getRandIntWithBlocklist(final int min, final int max, final Set<Integer> blocked) {
		Objects.requireNonNull(blocked);
		// Get all valid numbers
		final List<Integer> numbers = new LinkedList<Integer>();
		for (int i = min; i <= max; i++) {
			if (!blocked.contains(i)) {
				numbers.add(i);
			}
		}

		// If there are no valid numbers, throw an exception
		if (numbers.size() == 0) {
			throw new IllegalArgumentException("All values are blocked.");
		}

		// If there is only one number available, return it
		if (numbers.size() == 1) {
			return numbers.get(0);
		}

		// Otherwise, find a random number of the list of available numbers an return it
		return numbers.get(getRandInt(0, numbers.size() - 1));
	}

	/**
	 * Example helper method: add a new TA, specifying only data fields that exist
	 * in the new metamodel. If your new Ecore has different attributes, adjust
	 * accordingly.
	 */
	public void addTA(final String name, final int maxHoursPerWeek, final int maxHoursPerYear) {
		Objects.requireNonNull(name);
		final TeachingAssistant ta = factory.createTeachingAssistant();

		// According to the new metamodel, TA typically has: name, maxHoursPerWeek,
		// maxHoursPerYear
		ta.setName(name);
		ta.setMaxHoursPerWeek(maxHoursPerWeek);
		ta.setMaxHoursTotal(maxHoursPerYear);

		tas.put(name, ta);
	}

	/**
	 * Creates and returns the TaAllocation root object, adding all TAs and Modules
	 * that we've accumulated into 'tas'/'modules'. If your new Ecore doesn't define
	 * any extra attributes on TaAllocation, you can keep it simple like this.
	 */
	public TaAllocation generate(final String allocationName) {
		Objects.requireNonNull(allocationName);
		root = factory.createTaAllocation();
		// If there's a 'name' attribute on TaAllocation, you could set it:
		// root.setName(allocationName);

		// Add the TAs and Modules we've collected
		root.getTas().addAll(tas.values());
		root.getModules().addAll(modules.values());
		// If TaAllocation also has a 'timetable' reference or anything else, populate
		// it here.

		return root;
	}

	/**
	 * Helper to save the model to XMI format on disk.
	 */
	public static void save(final TaAllocation model, final String path) throws IOException {
		Objects.requireNonNull(model);
		Objects.requireNonNull(path);
		final Resource r = saveAndReturn(model, path);
		r.unload();
	}

	/**
	 * Same as above, but returns the Resource if needed.
	 */
	public static Resource saveAndReturn(final TaAllocation model, final String path) throws IOException {
		Objects.requireNonNull(model);
		Objects.requireNonNull(path);

		final URI uri = URI.createFileURI(path);
		final ResourceSet rs = new ResourceSetImpl();

		// If using SmartEMF or a custom factory, register it
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));

		// Register your new EPackage
		rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);

		final Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}

}
