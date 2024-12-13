package nodevaluemetamodel.validator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import nodevaluemetamodel.Node;
import nodevaluemetamodel.NodevaluemetamodelPackage;
import nodevaluemetamodel.Root;

public class NodeValueModelValidator {

	public final static String SCENARIO_FILE_NAME = "solved.xmi";

	public static void main(final String[] args) {
		final String projectFolder = System.getProperty("user.dir");
		final String instanceFolder = projectFolder + "/../nodevaluemetamodel/instances/";
		final String filePath = instanceFolder + SCENARIO_FILE_NAME;

		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(NodevaluemetamodelPackage.eNS_URI, NodevaluemetamodelPackage.eINSTANCE);
		final URI fileURI = URI.createFileURI(filePath);
		final Resource r = rs.getResource(fileURI, true);
		final Root model = (Root) r.getContents().get(0);
		final boolean valid = new NodeValueModelValidator().validate(model);

		if (valid) {
			System.out.println("Result: Model is valid.");
		} else {
			System.out.println("Result: Model is not valid.");
		}

	}

	public boolean validate(final Root model) {
		// Every node n that is connected to node m must also be contained in the
		// collection of connected nodes of m
		for (final Node n : model.getNodes()) {
			if (!n.getConnectedNodes().isEmpty()) {
				for (final Node m : n.getConnectedNodes()) {
					if (!m.getConnectedNodes().contains(n)) {
						return false;
					}
				}
			}
		}

//		// The highest x nodes must be connected
//		final List<Node> sortedNodes = new LinkedList<Node>();
//		sortedNodes.addAll(model.getNodes());
//		Collections.sort(sortedNodes, (n1, n2) -> {
//			return n2.getValue() - n1.getValue();
//		});
//
//		// From the top down: once a node has no other connected nodes, all following
//		// nodes must also be not connected to any nodes
//		boolean connected = true;
//		for (final Node n : sortedNodes) {
//			if (!n.getConnectedNodes().isEmpty() && !connected) {
//				return false;
//			}
//			if (n.getConnectedNodes().isEmpty() && connected) {
//				connected = false;
//			}
//		}

		// Every pair of nodes must be connected if their values are both >= 500
		for (final Node n1 : model.getNodes()) {
			for (final Node n2 : model.getNodes()) {
				// Nodes should be connected
				if (!n1.equals(n2) && n1.getValue() >= 500 && n2.getValue() >= 500) {
					if (!n1.getConnectedNodes().contains(n2)) {
						return false;
					}
					if (!n2.getConnectedNodes().contains(n1)) {
						return false;
					}
				}

				// Nodes should not be connected
				if (n1.getValue() < 500 || n1.getValue() < 500) {
					if (n1.getConnectedNodes().contains(n2)) {
						return false;
					}
					if (n2.getConnectedNodes().contains(n1)) {
						return false;
					}
				}
			}
		}

		return true;
	}

}
