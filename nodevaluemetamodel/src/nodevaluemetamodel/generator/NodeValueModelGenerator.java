package nodevaluemetamodel.generator;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import nodevaluemetamodel.NodevaluemetamodelFactory;
import nodevaluemetamodel.NodevaluemetamodelPackage;
import nodevaluemetamodel.Root;
import nodevaluemetamodel.Node;

public class NodeValueModelGenerator {

	private final static int NUMBER_OF_NODES = 10;
	private final static int MIN_NODE_VALUE = 0;
	private final static int MAX_NODE_VALUE = NUMBER_OF_NODES * 100;

	protected NodevaluemetamodelFactory factory = NodevaluemetamodelFactory.eINSTANCE;
	protected Root root;

	protected Map<String, Node> nodes = new LinkedHashMap<>();

	protected Random rand;

	public static void main(final String[] args) {
		final NodeValueModelGenerator gen = new NodeValueModelGenerator(0);
		final String instanceFolderPath = gen.prepareFolder();
		final Root model = gen.generate(NUMBER_OF_NODES, MIN_NODE_VALUE, MAX_NODE_VALUE);

		try {
			save(model, instanceFolderPath + "/model.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println("=> Model generation finished.");
	}

	public NodeValueModelGenerator(final long seed) {
		rand = new Random(seed);
	}

	public Root generate(final int numberOfNodes, final int minNodeValue, final int maxNodeValue) {
		root = factory.createRoot();

		for (int i = 0; i < numberOfNodes; i++) {
			addRandomNode(minNodeValue, maxNodeValue);
		}

		root.getNodes().addAll(nodes.values());
		return root;
	}

	private void addRandomNode(final int minNodeValue, final int maxNodeValue) {
		addNode(getRandInt(minNodeValue, maxNodeValue));
	}

	private void addNode(final int value) {
		final Node n = factory.createNode();
		n.setValue(value);
		n.setName(String.valueOf(value));
		nodes.put(String.valueOf(value), n);
	}

	//
	// Utilities
	//

	private static void checkNotNull(final Object o, final String type) {
		if (o == null) {
			throw new IllegalArgumentException(type + " must not be null.");
		}
	}

	public static void save(final Root model, final String path) throws IOException {
		final Resource r = saveAndReturn(model, path);
		r.unload();
	}

	public static Resource saveAndReturn(final Root model, final String path) throws IOException {
		checkNotNull(model, "Model");
		checkNotNull(path, "Path");

		final URI uri = URI.createFileURI(path);
		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(NodevaluemetamodelPackage.eNS_URI, NodevaluemetamodelPackage.eINSTANCE);
		final Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}

	protected int getRandInt(final int min, final int max) {
		return rand.nextInt((max - min) + 1) + min;
	}

	protected String prepareFolder() {
		final String projectFolder = System.getProperty("user.dir");
		final String instancesFolder = projectFolder + "/../nodevaluemetamodel" + "/instances";
		final File f = new File(instancesFolder);
		if (!f.exists()) {
			f.mkdirs();
		}
		return instancesFolder;
	}

}
