package teachingassistant.kcl.metamodelalt.generator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.google.common.base.Preconditions;

import metamodel.TAAllocation;
import metamodel.TA;
import metamodel.Module;
import metamodel.MetamodelFactory;
import metamodel.MetamodelPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

/**
 * Parent generator class that sets up basic structures and utilities
 * for generating a TAAllocation model. Updated to remove or rename
 * any fields that do not exist in the alternative metamodel.
 */
public class TeachingAssistantKclGenerator {
    
    // Factory for creating model elements
    protected MetamodelFactory factory = MetamodelFactory.eINSTANCE;
    // Root model object
    protected TAAllocation root;
    
    // Mappings from code/name => model objects (just for convenience)
    protected Map<String, TA> tas = new LinkedHashMap<>();
    protected Map<String, Module> modules = new LinkedHashMap<>();
    
    // Random generator for producing synthetic data
    protected Random rand;

    /**
     * Prepares (creates) a folder path for storing the generated XMI/JSON.
     * Adjust if you want a different output location.
     */
    protected String prepareFolder() {
        final String projectFolder = System.getProperty("user.dir");
        final String instancesFolder = projectFolder + "/../teachingassistant.kcl.metamodel/instances";
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
     * Example helper method: add a new TA, specifying only data fields
     * that exist in the new metamodel. If your new Ecore has different
     * attributes, adjust accordingly.
     */
    public void addTA(final String name, final int maxHoursPerWeek, final int maxHoursPerYear) {
        Preconditions.checkNotNull(name, "Name");
        TA ta = factory.createTA();
        
        // According to the new metamodel, TA typically has: name, maxHoursPerWeek, maxHoursPerYear
        ta.setName(name);
        ta.setMaxHoursPerWeek(maxHoursPerWeek);
        ta.setMaxHoursPerYear(maxHoursPerYear);
        
        tas.put(name, ta);
    }

    /**
     * Creates and returns the TAAllocation root object,
     * adding all TAs and Modules that we've accumulated into 'tas'/'modules'.
     * If your new Ecore doesn't define any extra attributes on TAAllocation,
     * you can keep it simple like this.
     */
    public TAAllocation generate(final String allocationName) {
        root = factory.createTAAllocation();
        // If there's a 'name' attribute on TAAllocation, you could set it: 
        // root.setName(allocationName);
        
        // Add the TAs and Modules we've collected
        root.getTas().addAll(tas.values());
        root.getModules().addAll(modules.values());
        // If TAAllocation also has a 'timetable' reference or anything else, populate it here.
        
        return root;
    }

    /**
     * Helper to save the model to XMI format on disk.
     */
    public static void save(final TAAllocation model, final String path) throws IOException {
        final Resource r = saveAndReturn(model, path);
        r.unload();
    }

    /**
     * Same as above, but returns the Resource if needed.
     */
    public static Resource saveAndReturn(final TAAllocation model, final String path) throws IOException {
        Preconditions.checkNotNull(model, "Model");
        Preconditions.checkNotNull(path,  "Path");
        
        final URI uri = URI.createFileURI(path);
        final ResourceSet rs = new ResourceSetImpl();
        
        // If using SmartEMF or a custom factory, register it
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", 
            new SmartEMFResourceFactoryImpl("../"));
        
        // Register your new EPackage
        rs.getPackageRegistry().put(MetamodelPackage.eNS_URI, MetamodelPackage.eINSTANCE);
        
        final Resource r = rs.createResource(uri);
        r.getContents().add(model);
        r.save(null);
        return r;
    }
}
