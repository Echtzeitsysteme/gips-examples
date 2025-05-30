package facade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import com.google.common.collect.Lists;

import facade.config.ModelFacadeConfig;
import facade.pathgen.Dijkstra;
import facade.pathgen.IPathGen;
import facade.pathgen.Yen;
import model.Link;
import model.ModelFactory;
import model.ModelPackage;
import model.Network;
import model.Node;
import model.Root;
import model.Server;
import model.SubstrateHostLink;
import model.SubstrateLink;
import model.SubstrateNetwork;
import model.SubstrateNode;
import model.SubstratePath;
import model.SubstrateServer;
import model.SubstrateSwitch;
import model.Switch;
import model.VirtualElement;
import model.VirtualLink;
import model.VirtualNetwork;
import model.VirtualNode;
import model.VirtualServer;
import model.VirtualSwitch;

/**
 * Facade to access and manipulate the underlying model.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class ModelFacade {

	/**
	 * The singleton instance of this class.
	 */
	private static ModelFacade instance;

	/**
	 * Counter for generating new IDs.
	 */
	private AtomicInteger counter = new AtomicInteger();

	/**
	 * Path to import and export models.
	 */
	public static final String PERSISTENT_MODEL_PATH = "./model.xmi";

	/*
	 * Collections for the path creation methods.
	 */
	private final Set<Node> visitedNodes = new HashSet<>();
	private final List<SubstratePath> generatedMetaPaths = new LinkedList<>();
	private final Set<Link> linksUntilNode = new HashSet<>();
	private final Map<SubstrateNode, Set<SubstratePath>> pathSourceMap = new HashMap<>();

	/**
	 * Private constructor to disable direct object instantiation.
	 */
	public ModelFacade() {
		this.initEmptyRs();
	}

	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return Singleton instance.
	 */
	public static synchronized ModelFacade getInstance() {
		if (ModelFacade.instance == null) {
			ModelFacade.instance = new ModelFacade();
		}
		return ModelFacade.instance;
	}

	/**
	 * Replaces the current model with another one.
	 * 
	 * @param instance The new facade to apply
	 */
	public static void setInstance(ModelFacade instance) {
		ModelFacade.instance = instance;
	}

	/**
	 * Initializes an empty resource set (model).
	 */
	private synchronized void initEmptyRs() {
		initEmptyRs("model.xmi");
	}

	/**
	 * Initializes an empty resource set (model).
	 */
	private synchronized void initEmptyRs(final String path) {
		this.resourceSet = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		this.resourceSet.getPackageRegistry().put(ModelPackage.eINSTANCE.getNsURI(), ModelPackage.eINSTANCE);
		this.resourceSet.createResource(URI.createURI(path));
		this.resourceSet.getResources().get(0).getContents().add(ModelFactory.eINSTANCE.createRoot());
	}

	/**
	 * Initializes the resource set (model) from a given file path.
	 */
	private synchronized void initRsFromFile(final URI absPath) {
		this.resourceSet = new ResourceSetImpl();
		final Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		this.resourceSet.getPackageRegistry().put(ModelPackage.eINSTANCE.getNsURI(), ModelPackage.eINSTANCE);
		this.resourceSet.getResource(absPath, true);
	}

	/*
	 * Look-up data structures.
	 */
	private Map<String, SubstratePath> paths = new HashMap<>();
	private Map<String, Link> links = new HashMap<>();

	/**
	 * Resource set which contains the model.
	 */
	private ResourceSet resourceSet;

	/**
	 * Returns the current model instance as resource set.
	 * 
	 * @return Current model instance as resource set.
	 */
	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	/**
	 * Returns the root node.
	 *
	 * @return Root node.
	 */
	public Root getRoot() {
		return (Root) this.resourceSet.getResources().get(0).getContents().get(0);
	}

	/**
	 * Returns true if the resource set is empty.
	 * 
	 * @return True if resource set is empty.
	 */
	private boolean isResourceSetEmpty() {
		return this.resourceSet.getResources().get(0).getContents().size() == 0;
	}

	/**
	 * Returns a collection of all networks from the model.
	 *
	 * @return Collection of all networks from the model.
	 */
	public Collection<Network> getAllNetworks() {
		return getRoot().getNetworks();
	}

	/**
	 * Returns a list of nodes with a specific type of a given network.
	 *
	 * @param network The Network, either virtual or substrate, to get the nodes
	 *                for.
	 * @param type    The type of nodes to get. Must be a subclass of Node.
	 * @return List of all nodes of the given type within the network.
	 */
	public static <T extends Node> List<Node> getAllNodesOfType(final Network network, final Class<T> type) {
		Objects.requireNonNull(network, "The network has to be non null.");

		return network.getNodess().stream().filter(node -> type.isInstance(node)).collect(Collectors.toList());
	}

	/**
	 * Returns a list of nodes with all servers of a given network ID.
	 *
	 * @param networkId Network ID.
	 * @return List of nodes with all servers of the given network ID.
	 */
	public List<Node> getAllServersOfNetwork(final String networkId) {
		checkStringValid(networkId);
		ifNetworkNotExistentThrowException(networkId);

		return getAllServersOfNetwork(getNetworkById(networkId));
	}

	/**
	 * Returns a list of nodes with all servers of a given network.
	 *
	 * @param network The Network, either virtual or substrate, to get the servers
	 *                for.
	 * @return List of nodes with all servers of the given network.
	 */
	public static List<Node> getAllServersOfNetwork(final Network network) {
		return getAllNodesOfType(network, Server.class);
	}

	/**
	 * Returns a list of nodes with all switches of a given network ID.
	 *
	 * @param networkId Network ID.
	 * @return List of nodes with all switches of the given network ID.
	 */
	public List<Node> getAllSwitchesOfNetwork(final String networkId) {
		checkStringValid(networkId);
		ifNetworkNotExistentThrowException(networkId);

		return getAllSwitchesOfNetwork(getNetworkById(networkId));
	}

	/**
	 * Returns a list of nodes with all switches of a given network.
	 *
	 * @param network The Network, either virtual or substrate, to get the switches
	 *                for.
	 * @return List of nodes with all switches of the given network.
	 */
	public static List<Node> getAllSwitchesOfNetwork(final Network network) {
		return getAllNodesOfType(network, Switch.class);
	}

	/**
	 * Returns a list of all links of a given network ID.
	 *
	 * @param networkId Network ID.
	 * @return List of all links of the given network ID.
	 */
	public List<Link> getAllLinksOfNetwork(final String networkId) {
		checkStringValid(networkId);
		ifNetworkNotExistentThrowException(networkId);

		return getNetworkById(networkId).getLinks();
	}

	/**
	 * Returns a list of all paths of a given network ID.
	 *
	 * @param networkId Network ID.
	 * @return List of all paths of the given network ID.
	 */
	public List<SubstratePath> getAllPathsOfNetwork(final String networkId) {
		checkStringValid(networkId);
		ifNetworkNotExistentThrowException(networkId);

		final Network net = getNetworkById(networkId);
		if (net instanceof VirtualNetwork) {
			throw new IllegalArgumentException("Virtual networks do not have paths.");
		}

		return ((SubstrateNetwork) getNetworkById(networkId)).getPaths();
	}

	/**
	 * Returns a network object by its ID.
	 *
	 * @param id ID to return network object for.
	 * @return Network object for given ID.
	 */
	public Network getNetworkById(final String id) {
		checkStringValid(id);
		ifNetworkNotExistentThrowException(id);

		return getRoot().getNetworks().stream().filter(n -> n.getName().equals(id)).collect(Collectors.toList()).get(0);
	}

	/**
	 * If there is no network with the given ID in the model, throw an exception.
	 * 
	 * @param networkId Network ID to search for.
	 */
	private void ifNetworkNotExistentThrowException(final String networkId) {
		if (!networkExists(networkId)) {
			throw new IllegalArgumentException("The network with id <" + networkId + "> does not exist.");
		}
	}

	/**
	 * If there is no server with the given ID in the model, throw an exception.
	 * 
	 * @param serverId Server ID to search for.
	 */
	private void ifServerNotExistentThrowException(final String serverId) {
		if (!serverExists(serverId)) {
			throw new IllegalArgumentException("The server with id <" + serverId + "> does not exist.");
		}
	}

	/**
	 * If there is no switch with the given ID in the model, throw an exception.
	 * 
	 * @param switchId Switch ID to search for.
	 */
	private void ifSwitchNotExistentThrowException(final String switchId) {
		if (!switchExists(switchId)) {
			throw new IllegalArgumentException("The switch with id <" + switchId + "> does not exist.");
		}
	}

	/**
	 * If there is no node with the given ID in the model, throw an exception.
	 * 
	 * @param nodeId Node ID to search for.
	 */
	private void ifNodeNotExistentThrowException(final String nodeId) {
		if (!(switchExists(nodeId) || serverExists(nodeId))) {
			throw new IllegalArgumentException("The node with id <" + nodeId + "> does not exist.");
		}
	}

	/**
	 * Returns true if a network for a given ID exists.
	 *
	 * @param id ID to check network existence for.
	 * @return True if network does exist in model.
	 */
	public boolean networkExists(final String id) {
		checkStringValid(id);

		return getAllNetworks().stream().filter(n -> n.getName().equals(id)).collect(Collectors.toList()).size() != 0;
	}

	/**
	 * Returns true if a server for a given ID exists.
	 *
	 * @param id ID to check server existence for.
	 * @return True if server does exist in model.
	 */
	public boolean serverExists(final String id) {
		try {
			final Node n = getNodeById(id);
			return (n != null && n instanceof Server);
		} catch (final NullPointerException | IndexOutOfBoundsException | IllegalArgumentException ex) {
			return false;
		}
	}

	/**
	 * Returns true if a switch for a given ID exists.
	 *
	 * @param id ID to check switch existence for.
	 * @return True if switch does exist in model.
	 */
	public boolean switchExists(final String id) {
		try {
			final Node n = getNodeById(id);
			return (n != null && n instanceof Switch);
		} catch (final NullPointerException | IndexOutOfBoundsException | IllegalArgumentException ex) {
			return false;
		}
	}

	/**
	 * Returns true if a link for a given ID exists.
	 *
	 * @param id ID to check link existence for.
	 * @return True if link does exist in model.
	 */
	public boolean linkExists(final String id) {
		return links.containsKey(id);
	}

	/**
	 * Returns true if a path for a given ID exists.
	 *
	 * @param id ID to check path existence for.
	 * @return True if path does exist in model.
	 */
	public boolean pathExists(final String id) {
		return paths.containsKey(id);
	}

	/**
	 * Returns a server object for a given ID.
	 *
	 * @param id ID to return server object for.
	 * @return Server object for given ID.
	 */
	public Server getServerById(final String id) {
		checkStringValid(id);
		ifServerNotExistentThrowException(id);
		return (Server) getNodeById(id);
	}

	/**
	 * Returns a switch object for a given ID.
	 *
	 * @param id ID to return switch object for.
	 * @return Switch object for given ID.
	 */
	public Switch getSwitchById(final String id) {
		checkStringValid(id);
		ifSwitchNotExistentThrowException(id);
		return (Switch) getNodeById(id);
	}

	/**
	 * Returns a node object for a given ID.
	 *
	 * @param id ID to return node object for.
	 * @return Node object for given ID.
	 */
	public Node getNodeById(final String id) {
		checkStringValid(id);

		List<Network> nets = getRoot().getNetworks();
		List<Node> nodes = new ArrayList<>();
		nets.stream().forEach(net -> {
			net.getNodess().stream().filter(n -> n instanceof Node).filter(n -> n.getName().equals(id))
					.forEach(n -> nodes.add(n));
		});

		if (nodes.size() == 0) {
			throw new IllegalArgumentException("Node with ID <" + id + "> not found.");
		}

		return nodes.get(0);
	}

	/**
	 * Returns a link object for a given ID.
	 *
	 * @param id ID to return link object for.
	 * @return Link object for given ID.
	 */
	public Link getLinkById(final String id) {
		checkStringValid(id);

		// List<Network> nets = root.getNetworks();
		// List<Link> links = new ArrayList<Link>();
		// nets.stream().forEach(net -> {
		// net.getLinks().stream().filter(l -> l.getName().equals(id)).forEach(l ->
		// links.add(l));
		// });
		// return links.get(0);

		if (links.get(id) == null) {
			throw new IllegalArgumentException("Link with ID <" + id + "> not found.");
		}

		return links.get(id);
	}

	/**
	 * Returns a path object for a given ID.
	 *
	 * @param id ID to return path object for.
	 * @return Path object for given ID.
	 */
	public SubstratePath getPathById(final String id) {
		checkStringValid(id);

		// List<Network> nets = root.getNetworks();
		// List<Path> paths = new ArrayList<Path>();
		// nets.stream().forEach(net -> {
		// net.getPaths().stream().filter(p -> p.getName().equals(id)).forEach(p ->
		// paths.add(p));
		// });
		//
		// return paths.get(0);

		if (paths.get(id) == null) {
			throw new IllegalArgumentException("Path with ID <" + id + "> not found.");
		}

		return paths.get(id);
	}

	/**
	 * Creates and adds a new (substrate or virtual) network object with given ID to
	 * the root node of the model.
	 *
	 * @param id        ID of the new network to create.
	 * @param isVirtual True if new network should be virtual.
	 * @return True if creation was successful.
	 */
	public boolean addNetworkToRoot(final String id, final boolean isVirtual) {
		checkStringValid(id);

		if (networkExists(id)) {
			throw new IllegalArgumentException("A network with id " + id + " already exists!");
		}

		Network net;
		if (isVirtual) {
			net = ModelFactory.eINSTANCE.createVirtualNetwork();
		} else {
			net = ModelFactory.eINSTANCE.createSubstrateNetwork();
		}

		net.setName(id);
		net.setRoot(getRoot());
		return getRoot().getNetworks().add(net);
	}

	/**
	 * Creates and adds a new server to the network model.
	 *
	 * @param id        ID of the new server to create.
	 * @param networkId Network ID to add the new server to.
	 * @param cpu       CPU amount.
	 * @param memory    Memory amount.
	 * @param storage   Storage amount.
	 * @param depth     Depth inside the network.
	 * @return True if creation was successful.
	 */
	public boolean addServerToNetwork(final String id, final String networkId, final int cpu, final int memory,
			final int storage, final int depth) {
		checkStringValid(new String[] { id, networkId });
		checkIntValid(new int[] { cpu, memory, storage, depth });

		if (doesNodeIdExist(id, networkId)) {
			throw new IllegalArgumentException("A node with id " + id + " already exists!");
		}

		ifNetworkNotExistentThrowException(networkId);

		final Network net = getNetworkById(networkId);
		Server server;

		if (net instanceof VirtualNetwork) {
			server = ModelFactory.eINSTANCE.createVirtualServer();
		} else {
			server = ModelFactory.eINSTANCE.createSubstrateServer();
		}
		server.setName(id);
		server.setNetwork(net);
		server.setCpu(cpu);
		server.setMemory(memory);
		server.setStorage(storage);
		server.setDepth(depth);

		// Add residual values to server if it is a substrate server
		if (server instanceof SubstrateServer) {
			SubstrateServer subServer = (SubstrateServer) server;
			subServer.setResidualCpu(cpu);
			subServer.setResidualMemory(memory);
			subServer.setResidualStorage(storage);
		}

		return net.getNodess().add(server);
	}

	/**
	 * Creates and adds a new switch to the network model.
	 *
	 * @param id        ID of the new switch to create.
	 * @param networkId Network ID to add the new server to.
	 * @param depth     Depth inside the network.
	 * @return True if creation was successful.
	 */
	public boolean addSwitchToNetwork(final String id, final String networkId, final int depth) {
		checkStringValid(new String[] { id, networkId });
		checkIntValid(depth);

		if (doesNodeIdExist(id, networkId)) {
			throw new IllegalArgumentException("A node with id " + id + " already exists!");
		}

		ifNetworkNotExistentThrowException(networkId);

		final Network net = getNetworkById(networkId);
		Switch sw;

		if (net instanceof VirtualNetwork) {
			sw = ModelFactory.eINSTANCE.createVirtualSwitch();
		} else {
			sw = ModelFactory.eINSTANCE.createSubstrateSwitch();
		}
		sw.setName(id);
		sw.setNetwork(net);
		sw.setDepth(depth);

		return net.getNodess().add(sw);
	}

	/**
	 * Creates and adds a new link to a network.
	 *
	 * @param id        ID of the new link to create.
	 * @param networkId Network ID to add link to.
	 * @param bandwidth Bandwidth amount.
	 * @param sourceId  ID of the source node.
	 * @param targetId  ID of the target node.
	 * @return True if link creation was successful.
	 */
	public boolean addLinkToNetwork(final String id, final String networkId, final int bandwidth, final String sourceId,
			final String targetId) {
		checkStringValid(new String[] { id, networkId, sourceId, targetId });
		checkIntValid(bandwidth);

		if (doesLinkIdExist(id, networkId)) {
			throw new IllegalArgumentException("A link with id " + id + " already exists!");
		}

		if (!doesNodeIdExist(sourceId, networkId) || !doesNodeIdExist(targetId, networkId)) {
			throw new IllegalArgumentException("A node with given id does not exist!");
		}

		ifNetworkNotExistentThrowException(networkId);

		final Network net = getNetworkById(networkId);
		Link link;
		if (net instanceof VirtualNetwork) {
			link = ModelFactory.eINSTANCE.createVirtualLink();
		} else {
			link = ModelFactory.eINSTANCE.createSubstrateLink();
		}
		link.setName(id);
		link.setNetwork(net);
		link.setBandwidth(bandwidth);
		link.setSource(getNodeById(sourceId));
		link.setTarget(getNodeById(targetId));

		// Add residual values to link if it is a substrate link
		if (link instanceof SubstrateLink) {
			SubstrateLink subLink = (SubstrateLink) link;
			subLink.setResidualBandwidth(bandwidth);
		}

		links.put(id, link);
		return net.getLinks().add(link);
	}

	/**
	 * This method creates all necessary paths *after* all other components are
	 * added to the network.
	 *
	 * Assumptions: Every server of the given network is only connected to one
	 * switch.
	 *
	 * @param networkdId Network ID to add paths to.
	 */
	public void createAllPathsForNetwork(final String networkdId) {
		checkStringValid(networkdId);
		ifNetworkNotExistentThrowException(networkdId);
		final Network net = getNetworkById(networkdId);

		if (net instanceof VirtualNetwork) {
			throw new UnsupportedOperationException("Given network ID is virtual," + " which is not supported!");
		}

		final SubstrateNetwork snet = (SubstrateNetwork) net;

		// Check if maximum path length automatic is set
		if (ModelFacadeConfig.MAX_PATH_LENGTH_AUTO) {
			ModelFacadeConfig.MAX_PATH_LENGTH = determineMaxPathLengthForTree(networkdId);
		}

		getAllServersOfNetwork(networkdId).stream().forEach((s) -> {
			final SubstrateServer srv = (SubstrateServer) s;
			final IPathGen gen;

			// Decide whether the paths should be generated using the algorithm of Dijkstra
			// (shortest
			// path) or the algorithm of Yen (K shortest paths).
			if (ModelFacadeConfig.YEN_PATH_GEN) {
				gen = new Yen();

				final Map<SubstrateNode, List<List<SubstrateLink>>> actMap = gen.getAllKFastestPaths(snet, srv,
						ModelFacadeConfig.YEN_K);

				final List<SubstrateNode> sortedKeys = new LinkedList<>();
				sortedKeys.addAll(actMap.keySet());
				sortedKeys.sort(new Comparator<SubstrateNode>() {

					@Override
					public int compare(final SubstrateNode o1, final SubstrateNode o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});

				// Iterate over all "paths" of the current node
				for (final SubstrateNode n : sortedKeys) {
					for (final List<SubstrateLink> l : actMap.get(n)) {
						createBidirectionalPathFromLinks(l);
					}
				}
			} else {
				gen = new Dijkstra();

				final Map<SubstrateNode, List<SubstrateLink>> actMap = gen.getAllFastestPaths(snet, srv);

				final List<SubstrateNode> sortedKeys = new LinkedList<>();
				sortedKeys.addAll(actMap.keySet());
				sortedKeys.sort(new Comparator<SubstrateNode>() {

					@Override
					public int compare(final SubstrateNode o1, final SubstrateNode o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});

				for (final SubstrateNode n : sortedKeys) {
					createBidirectionalPathFromLinks(actMap.get(n));
				}
			}
		});
	}

	/**
	 * Determines the maximum path length needed to connect one server within the
	 * network with a core switch. Throws an {@link UnsupportedOperationException}
	 * if servers have different depths.
	 *
	 * @param networkId Network ID to calculate maximum needed path length for.
	 * @return Maximum path length.
	 */
	private int determineMaxPathLengthForTree(final String networkId) {
		ifNetworkNotExistentThrowException(networkId);
		int maxServerDepth = Integer.MAX_VALUE;

		final List<Node> servers = getAllServersOfNetwork(networkId);
		for (final Node n : servers) {
			final SubstrateServer srv = (SubstrateServer) n;

			if (srv.getDepth() != maxServerDepth) {
				if (maxServerDepth != Integer.MAX_VALUE) {
					throw new UnsupportedOperationException(
							"In network " + networkId + " are servers with different depths, which is not supported.");
				}

				if (srv.getDepth() < maxServerDepth) {
					maxServerDepth = srv.getDepth();
				}
			}
		}

		int minSwitchDepth = Integer.MAX_VALUE;
		final List<Node> switches = getAllSwitchesOfNetwork(networkId);
		for (final Node n : switches) {
			final SubstrateSwitch sw = (SubstrateSwitch) n;
			if (sw.getDepth() < minSwitchDepth) {
				minSwitchDepth = sw.getDepth();
			}
		}

		return maxServerDepth - minSwitchDepth;
	}

	/**
	 * Creates the bidirectional path (forward and backward) from a given list of
	 * links. The order of the list elements is important: The source node of the
	 * forward path is determined by the source node of the first link and the
	 * target node of the forward path is determined by the target node of the last
	 * link. For the backward path, both nodes described above are swapped.
	 *
	 * @param links Input list of links to generate paths from.
	 */
	private synchronized void createBidirectionalPathFromLinks(final List<SubstrateLink> links) {
		// Check path limits
		if (links.size() < ModelFacadeConfig.MIN_PATH_LENGTH || links.size() > ModelFacadeConfig.MAX_PATH_LENGTH) {
			return;
		}

		// Check if a server is used as forwarding node (which is forbidden)
		for (int i = 0; i < links.size(); i++) {
			if (i != 0) {
				if (links.get(i).getSource() instanceof Server) {
					return;
				}
			}

			if (i != links.size() - 1) {
				if (links.get(i).getTarget() instanceof Server) {
					return;
				}
			}
		}

		// Get all nodes from links
		final List<SubstrateNode> nodes = new LinkedList<>();

		for (final SubstrateLink l : links) {
			nodes.add((SubstrateNode) l.getSource());
			// nodes.add(l.getTarget());
		}
		nodes.add((SubstrateNode) links.get(links.size() - 1).getTarget());

		final int lastIndex = links.size() - 1;
		final SubstrateNode source = (SubstrateNode) links.get(0).getSource();
		final SubstrateNode target = (SubstrateNode) links.get(lastIndex).getTarget();

		// Create forward path
		if (!doesSpecificPathWithSourceAndTargetExist(source, target, nodes, links)) {
			final SubstratePath forward = genMetaPath(source, target);
			forward.setHops(links.size());
			forward.setNetwork((SubstrateNetwork) links.get(0).getNetwork());
			final String name = concatNodeNames(nodes);
			forward.setName(name);

			forward.getNodes().addAll(nodes);
			forward.getLinks().addAll(links);

			// Determine bandwidth
			final int bw = getMinimumBandwidthFromSubstrateLinks(links);
			forward.setBandwidth(bw);
			forward.setResidualBandwidth(bw);

			paths.put(name, forward);

			// Add path to lookup map
			if (!pathSourceMap.containsKey(source)) {
				pathSourceMap.put(source, new HashSet<SubstratePath>());
			}
			pathSourceMap.get(source).add(forward);
		}

		// Create reverse path
		final List<SubstrateNode> reversedNodes = Lists.reverse(nodes);
		// Get all opposite links
		final List<SubstrateLink> oppositeLinks = getOppositeLinks(links);

		if (!doesSpecificPathWithSourceAndTargetExist(target, source, reversedNodes, Lists.reverse(oppositeLinks))) {
			final SubstratePath reverse = genMetaPath(target, source);
			reverse.getLinks().addAll(Lists.reverse(oppositeLinks));

			reverse.setHops(links.size());
			reverse.setNetwork((SubstrateNetwork) links.get(0).getNetwork());
			final String name = concatNodeNames(reversedNodes);
			reverse.setName(name);
			reverse.getNodes().addAll(reversedNodes);

			final int revBw = getMinimumBandwidthFromSubstrateLinks(oppositeLinks);
			reverse.setBandwidth(revBw);
			reverse.setResidualBandwidth(revBw);

			paths.put(name, reverse);

			// Add path to lookup map
			if (!pathSourceMap.containsKey(target)) {
				pathSourceMap.put(target, new HashSet<SubstratePath>());
			}
			pathSourceMap.get(target).add(reverse);
		}
	}

	/**
	 * Creates a string with all names of given node list.
	 *
	 * @param nodes Input list of nodes.
	 * @return String with all names of given node list.
	 */
	private String concatNodeNames(final List<SubstrateNode> nodes) {
		String name = "path";

		for (final Node n : nodes) {
			name += "-";
			name += n.getName();
		}

		return name;
	}

	/**
	 * Calculates the minimum bandwidth found in a collection of links. This method
	 * is used to calculate the actual bandwidth of a path.
	 *
	 * @param links Collection of links to search the minimal value in.
	 * @return Minimal bandwidth value of all links from the collection.
	 */
	private int getMinimumBandwidthFromSubstrateLinks(final Collection<SubstrateLink> links) {
		int val = Integer.MAX_VALUE;

		for (final Link l : links) {
			if (l.getBandwidth() < val) {
				val = l.getBandwidth();
			}
		}

		return val;
	}

	/**
	 * Takes a given link and searches for the opposite one. The opposite link has
	 * the original target as source and vice versa.
	 *
	 * @param link Link to search opposite link for.
	 * @return Opposite link for given link.
	 */
	private Link getOppositeLink(final Link link) {
		final Node source = link.getSource();
		final Node target = link.getTarget();

		final Network net = link.getNetwork();
		final List<Link> allLinks = net.getLinks();

		for (Link l : allLinks) {
			if (l.getSource().equals(target) && l.getTarget().equals(source)) {
				return l;
			}
		}

		throw new UnsupportedOperationException("Opposite link could not be found!");
	}

	/**
	 * Returns a list of all opposite links for a given set of links. Basically, it
	 * calls the method {@link #getOppositeLink(Link)} for every link in the
	 * incoming collection.
	 *
	 * @param links Collection of links to get opposites for.
	 * @return List of opposite links.
	 */
	private List<SubstrateLink> getOppositeLinks(final Collection<SubstrateLink> links) {
		final List<SubstrateLink> opposites = new LinkedList<>();

		for (Link l : links) {
			opposites.add((SubstrateLink) getOppositeLink(l));
		}

		return opposites;
	}

	/**
	 * Generates a meta path that has only the source and the target node set up.
	 * This is a utility method for the path creation.
	 *
	 * @param source Source node for the path.
	 * @param target Target node for the path.
	 * @return Generated substrate (meta-)path.
	 */
	private SubstratePath genMetaPath(final Node source, final Node target) {
		SubstratePath path = ModelFactory.eINSTANCE.createSubstratePath();
		path.setSource((SubstrateNode) source);
		path.setTarget((SubstrateNode) target);
		return path;
	}

	/**
	 * This method checks the availability of a specific path with given source and
	 * target node.
	 *
	 * @param source Source to search path for.
	 * @param target Target to search path for.
	 * @param nodes  List of nodes that must be contained in the found path.
	 * @param links  List of links that must be contained in the found path.
	 * @return True if a path with given parameters already exists.
	 */
	private boolean doesSpecificPathWithSourceAndTargetExist(final SubstrateNode source, final SubstrateNode target,
			final List<SubstrateNode> nodes, final List<SubstrateLink> links) {
		final Set<SubstratePath> foundPaths = getPathsFromSourceToTarget(source, target);

		for (final SubstratePath p : foundPaths) {
			// Check that both "sets" of nodes and links are equal
			if (nodes.containsAll(p.getNodes()) && p.getNodes().containsAll(nodes) && links.containsAll(p.getLinks())
					&& p.getLinks().containsAll(links)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true, if a given node ID exists in a given network model.
	 *
	 * @param id        Node ID to check.
	 * @param networkId Network ID to check node ID in.
	 * @return True, if the given node ID exists.
	 */
	public boolean doesNodeIdExist(final String id, final String networkId) {
		checkStringValid(new String[] { id, networkId });
		ifNetworkNotExistentThrowException(networkId);

		return !getNetworkById(networkId).getNodess().stream().filter(n -> n.getName().equals(id))
				.collect(Collectors.toList()).isEmpty();
	}

	/**
	 * Returns true, if a given link ID exists in a given network model.
	 *
	 * @param id        Link ID to check.
	 * @param networkId Network ID to check node ID in.
	 * @return True, if the given link ID exists.
	 */
	public boolean doesLinkIdExist(final String id, final String networkId) {
		checkStringValid(id);
		checkStringValid(networkId);
		ifNetworkNotExistentThrowException(networkId);

		return !getNetworkById(networkId).getLinks().stream().filter(l -> l.getName().equals(id))
				.collect(Collectors.toList()).isEmpty();
	}

	/**
	 * Completely resets the network model. This method clears the collection of
	 * networks of the root node.
	 */
	public void resetAll() {
		// If the resource set is empty, there is no root to clear networks from
		if (!isResourceSetEmpty()) {
			getRoot().getNetworks().clear();
		}
		generatedMetaPaths.clear();
		visitedNodes.clear();
		linksUntilNode.clear();
		counter.set(0);
		links.clear();
		paths.clear();
		pathSourceMap.clear();
		initEmptyRs();
	}

	/**
	 * Returns a path from source node to target node if such a path exists. Else it
	 * returns null.
	 *
	 * @param source Source node.
	 * @param target Target node.
	 * @return Path if a path between source and target does exist.
	 */
	public SubstratePath getPathFromSourceToTarget(final SubstrateNode source, final SubstrateNode target) {
		final Set<SubstratePath> allPaths = pathSourceMap.get(source);

		// Check if there are any paths from source node to any other node
		if (allPaths == null) {
			return null;
		}

		for (final SubstratePath p : allPaths) {
			if (p.getSource().equals(source) && p.getTarget().equals(target)) {
				return p;
			}
		}

		return null;
	}

	/**
	 * Returns a path from source node ID to target node ID if such a path exists.
	 * Else it returns null.
	 *
	 * @param sourceId Source node ID.
	 * @param targetId Target node ID.
	 * @return Path if a path between source and target does exist.
	 */
	public SubstratePath getPathFromSourceToTarget(final String sourceId, final String targetId) {
		ifNodeNotExistentThrowException(sourceId);
		ifNodeNotExistentThrowException(targetId);

		final Node source = getNodeById(sourceId);
		final Node target = getNodeById(targetId);

		if (!(source instanceof SubstrateNode) || !(target instanceof SubstrateNode)) {
			throw new IllegalArgumentException(
					"One or both of the provided node IDs do not belong to a substrate network.");
		}

		return getPathFromSourceToTarget((SubstrateNode) source, (SubstrateNode) target);
	}

	/**
	 * Returns all paths from source node to target node if any exists. Else it
	 * returns an empty set.
	 *
	 * @param source Source node.
	 * @param target Target node.
	 * @return Set of paths if any exists or else an empty set.
	 */
	public Set<SubstratePath> getPathsFromSourceToTarget(final SubstrateNode source, final SubstrateNode target) {
		final Set<SubstratePath> allPaths = pathSourceMap.get(source);

		// Check if there are any paths from source node to any other node
		if (allPaths == null) {
			return new HashSet<>();
		}

		final Set<SubstratePath> foundPaths = Collections.synchronizedSet(new HashSet<SubstratePath>());

		allPaths.stream().forEach(p -> {
			if (p.getSource().equals(source) && p.getTarget().equals(target)) {
				foundPaths.add(p);
			}
		});

		return foundPaths;
	}

	/**
	 * Returns a link from source node to target node if such a link exists. Else it
	 * returns null.
	 *
	 * @param source Source node.
	 * @param target Target node.
	 * @return Link if a link between source and target does exist.
	 */
	public Link getLinkFromSourceToTarget(final Node source, final Node target) {
		final List<Link> allLinks = getAllLinksOfNetwork(source.getNetwork().getName());

		for (final Link l : allLinks) {
			if (l.getSource().equals(source) && l.getTarget().equals(target)) {
				return l;
			}
		}

		return null;
	}

	/**
	 * Checks string validity (null and blank).
	 *
	 * @param strings Possible array of strings to check.
	 */
	public void checkStringValid(final String... strings) {
		if (strings == null) {
			throw new IllegalArgumentException("Provided String(-array) was null!");
		}

		for (String string : strings) {
			if (string == null) {
				throw new IllegalArgumentException("Provided String was null!");
			}

			if (string.isBlank()) {
				throw new IllegalArgumentException("Provided String was blank!");
			}
		}
	}

	/**
	 * Checks integer validity (must be greater or equal to 0).
	 *
	 * @param ints Possible array of integers to check.
	 */
	public void checkIntValid(final int... ints) {
		if (ints == null) {
			throw new IllegalArgumentException("Provided int(-array) was null!");
		}

		for (final int cInt : ints) {
			if (cInt < 0) {
				throw new IllegalArgumentException("Provided int was smaller than zero!");
			}
		}
	}

	/**
	 * Returns the next ID.
	 *
	 * @return Next free ID.
	 */
	public String getNextId() {
		return String.valueOf(counter.getAndIncrement());
	}

	/**
	 * Saves the model to file.
	 */
	public void persistModel() {
		persistModel(PERSISTENT_MODEL_PATH);
	}

	/**
	 * Saves the model to given file path.
	 *
	 * @param path File path as string.
	 */
	public void persistModel(final String path) {
		// Workaround: Always use absolute path
		final URI absPath = URI.createFileURI(System.getProperty("user.dir") + "/" + path);

		// Create new model for saving
		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl(null));
		// ^null is okay if all paths are absolute
		final Resource r = rs.createResource(absPath);
		// Fetch model contents from eMoflon
		final Root root = getRoot();
		r.getContents().add(root);
		try {
			r.save(null);
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			// Re-add the root node (and all of its children) to the resource set contained
			// within this ModelFacade object.
			// This fixes a bug were the ModelFacade was basically broken after the persist
			// method was called.
			this.resourceSet.getResources().get(0).getContents().add(root);
		}
	}

	/**
	 * Loads the model from file.
	 */
	public void loadModel() {
		loadModel(PERSISTENT_MODEL_PATH);
	}

	/**
	 * Loads the model from given file path.
	 *
	 * @param path File path as string.
	 */
	public void loadModel(final String path) {
		checkStringValid(path);
		final URI absPath = URI.createFileURI(System.getProperty("user.dir") + "/" + path);
		initRsFromFile(absPath);

		// Restore other look-up data structures
		this.links.clear();
		this.paths.clear();
		this.pathSourceMap.clear();
		for (final Network n : getRoot().getNetworks()) {
			// Links
			for (final Link l : n.getLinks()) {
				this.links.put(l.getName(), l);
			}

			// Paths
			if (n instanceof SubstrateNetwork) {
				final SubstrateNetwork sNet = (SubstrateNetwork) n;
				for (final SubstratePath p : sNet.getPaths()) {
					this.paths.put(p.getName(), p);

					// Add path to lookup map
					final SubstrateNode source = p.getSource();
					if (!pathSourceMap.containsKey(source)) {
						pathSourceMap.put(source, new HashSet<SubstratePath>());
					}
					pathSourceMap.get(source).add(p);
				}
			}
		}
	}

	/*
	 * Embedding related methods.
	 */

	/**
	 * Adds an embedding of one virtual network to one substrate network.
	 *
	 * @param substrateId Substrate network id.
	 * @param virtualId   Virtual network id.
	 * @return True if embedding was successful.
	 */
	public boolean embedNetworkToNetwork(final String substrateId, final String virtualId) {
		// Check that both networks exist
		if (!networkExists(substrateId) || !networkExists(virtualId)) {
			throw new IllegalArgumentException("One of the two networks does not exist.");
		}

		final SubstrateNetwork subNet = (SubstrateNetwork) getNetworkById(substrateId);
		final VirtualNetwork virtNet = (VirtualNetwork) getNetworkById(virtualId);

		// Check that the virtual network was not embedded before
		if (virtNet.getHost() != null) {
			throw new IllegalArgumentException("Virtual network was embedded before.");
		}

		virtNet.setHost(subNet);
		return subNet.getGuests().add(virtNet);
	}

	/**
	 * Adds an embedding of one virtual server to one substrate server.
	 *
	 * @param substrateId Substrate server id.
	 * @param virtualId   Virtual server id.
	 * @return True if embedding was successful.
	 */
	public boolean embedServerToServer(final String substrateId, final String virtualId) {
		final SubstrateServer subServ = (SubstrateServer) getServerById(substrateId);
		final VirtualServer virtServ = (VirtualServer) getServerById(virtualId);
		boolean success = true;

		if (subServ.getResidualCpu() >= virtServ.getCpu() && subServ.getResidualMemory() >= virtServ.getMemory()
				&& subServ.getResidualStorage() >= virtServ.getStorage()) {
			success &= subServ.getGuestServers().add(virtServ);
			virtServ.setHost(subServ);

			// Update residual values of the host
			final long oldResCpu = subServ.getResidualCpu();
			final long oldResMem = subServ.getResidualMemory();
			final long oldResStor = subServ.getResidualStorage();
			subServ.setResidualCpu(oldResCpu - virtServ.getCpu());
			subServ.setResidualMemory(oldResMem - virtServ.getMemory());
			subServ.setResidualStorage(oldResStor - virtServ.getStorage());
		} else {
			throw new UnsupportedOperationException(
					"Embedding of server not possible due resource " + "constraint violation.");
		}

		return success;
	}

	/**
	 * Adds an embedding of one virtual switch to one substrate node. The substrate
	 * node may either be a substrate switch or a substrate server.
	 *
	 * @param substrateId Substrate Id.
	 * @param virtualId   Virtual Id.
	 * @return True if embedding was successful.
	 */
	public boolean embedSwitchToNode(final String substrateId, final String virtualId) {
		final SubstrateNode subNode = (SubstrateNode) getNodeById(substrateId);
		final VirtualSwitch virtSwitch = (VirtualSwitch) getSwitchById(virtualId);
		virtSwitch.setHost(subNode);
		return subNode.getGuestSwitches().add(virtSwitch);
	}

	/**
	 * Adds an embedding of one virtual link to one substrate server. There are no
	 * constraints to check in this particular case.
	 *
	 * @param substrateId Substrate Id.
	 * @param virtualId   Virtual Id.
	 * @return True if embedding was successful.
	 */
	public boolean embedLinkToServer(final String substrateId, final String virtualId) {
		final SubstrateServer subServ = (SubstrateServer) getServerById(substrateId);
		final VirtualLink virtLink = (VirtualLink) getLinkById(virtualId);

		// // Check conditions
		// // Source
		// if (virtLink.getSource() instanceof VirtualServer) {
		// if (((VirtualServer) virtLink.getSource()).getHost() == null) {
		// throw new UnsupportedOperationException("Virtual link source host is null.");
		// }
		// if (!((VirtualServer) virtLink.getSource()).getHost().equals(subServ)) {
		// throw new UnsupportedOperationException();
		// }
		// } else if (virtLink.getSource() instanceof VirtualSwitch) {
		// if (((VirtualSwitch) virtLink.getSource()).getHost() == null) {
		// throw new UnsupportedOperationException("Virtual link source host is null.");
		// }
		// if (!((VirtualSwitch) virtLink.getSource()).getHost().equals(subServ)) {
		// throw new UnsupportedOperationException();
		// }
		// }
		//
		// // Target
		// if (virtLink.getTarget() instanceof VirtualServer) {
		// if (((VirtualServer) virtLink.getTarget()).getHost() == null) {
		// throw new UnsupportedOperationException("Virtual link target host is null.");
		// }
		// if (!((VirtualServer) virtLink.getTarget()).getHost().equals(subServ)) {
		// throw new UnsupportedOperationException();
		// }
		// } else if (virtLink.getTarget() instanceof VirtualSwitch) {
		// if (((VirtualSwitch) virtLink.getTarget()).getHost() == null) {
		// throw new UnsupportedOperationException("Virtual link target host is null.");
		// }
		// if (!((VirtualSwitch) virtLink.getTarget()).getHost().equals(subServ)) {
		// throw new UnsupportedOperationException();
		// }
		// }

		// No constraints to check!
		virtLink.setHost(subServ);
		return subServ.getGuestLinks().add(virtLink);
	}

	/**
	 * Adds an embedding of one virtual link to one substrate path.
	 *
	 * @param substrateId Substrate Id.
	 * @param virtualId   Virtual Id.
	 * @return True if embedding was successful.
	 */
	public boolean embedLinkToPath(final String substrateId, final String virtualId) {
		final SubstratePath subPath = getPathById(substrateId);
		final VirtualLink virtLink = (VirtualLink) getLinkById(virtualId);
		boolean success = true;

		if (!ModelFacadeConfig.IGNORE_BW) {
			if (subPath.getResidualBandwidth() < virtLink.getBandwidth()) {
				throw new UnsupportedOperationException(
						"Embeding of link not possible due resource constraint violation.");
			}
		}

		success &= subPath.getGuestLinks().add(virtLink);
		virtLink.setHost(subPath);

		// Add guest link to all substrate links contained in the path?
		if (ModelFacadeConfig.LINK_HOST_EMBED_PATH) {
			for (final Link l : subPath.getLinks()) {
				final SubstrateLink sl = (SubstrateLink) l;
				sl.getGuestLinks().add(virtLink);
			}
		}

		// Update residual values of the host path
		if (!ModelFacadeConfig.IGNORE_BW) {
			final int oldResBw = subPath.getResidualBandwidth();
			subPath.setResidualBandwidth(oldResBw - virtLink.getBandwidth());

			// Update all residual bandwidths of all links of the path
			// This should only be done, if the virtual links are *not* embedded to the
			// substrate ones
			// before, because else we would subtract the virtual bandwidth twice.
			if (!ModelFacadeConfig.LINK_HOST_EMBED_PATH) {
				for (Link actLink : subPath.getLinks()) {
					SubstrateLink actSubLink = (SubstrateLink) actLink;
					final int resBw = actSubLink.getResidualBandwidth();
					actSubLink.setResidualBandwidth(resBw - virtLink.getBandwidth());
				}
			}
		}

		// Update residual bandwidth value of other paths containing the substrate links
		updateAllPathsResidualBandwidth(subPath.getNetwork().getName());

		return success;
	}

	/**
	 * Adds an embedding of one virtual element (server, switch, link, network) to
	 * one substrate element (server, switch, path, network). The type of the
	 * virtual element and the substrate element will be determined based on the
	 * given IDs.
	 * 
	 * Precedence: Network > Server > Switch > Path > Link
	 *
	 * @param virtualId   Virtual Id.
	 * @param substrateId Substrate Id.
	 * @return True if embedding was successful.
	 */
	public boolean embedGeneric(final String substrateId, final String virtualId) {
		// find substrate element
		final ElementType sub = findElementType(substrateId);

		// find virtual element
		final ElementType virt = findElementType(virtualId);

		// check if embedding is possible at all (besides resource constraints)
		if (!isEmbeddingPossibleGeneric(substrateId, virtualId, true)) {
			throw new UnsupportedOperationException(
					"Embedding of " + virtualId + " onto " + substrateId + " is not possible.");
		}

		// embedding itself
		boolean success = false;

		if (sub == ElementType.NETWORK && virt == ElementType.NETWORK) {
			success = embedNetworkToNetwork(substrateId, virtualId);
		} else if (sub == ElementType.SERVER && virt == ElementType.SERVER) {
			success = embedServerToServer(substrateId, virtualId);
		} else if (sub == ElementType.SERVER && virt == ElementType.SWITCH) {
			success = embedSwitchToNode(substrateId, virtualId);
		} else if (sub == ElementType.SERVER && virt == ElementType.LINK) {
			success = embedLinkToServer(substrateId, virtualId);
		} else if (sub == ElementType.SWITCH && virt == ElementType.SWITCH) {
			success = embedSwitchToNode(substrateId, virtualId);
		} else if (sub == ElementType.PATH && virt == ElementType.LINK) {
			success = embedLinkToPath(substrateId, virtualId);
		} else {
			throw new UnsupportedOperationException("Substrate element " + substrateId + " and virtual element "
					+ virtualId + " could be found but there is no possibility to embed the virtual element.");
		}

		return success;
	}

	/**
	 * Checks if an embedding is possible for a given virtual ID onto a given
	 * substrate ID. If ignoreResources is set to true, the method will not check if
	 * the available resources of the substrate element can fulfill the resource
	 * demand of the virtual element. In general, this method does not check if
	 * there is a pre-existing embedding.
	 * 
	 * @param substrateId     ID of the substrate element to check the embedding
	 *                        for.
	 * @param virtualId       ID of the virtual element to check the embedding for.
	 * @param ignoreResources If true, all resource constraints will be ignored.
	 * @return True if an embedding of the virtual element onto the substrate
	 *         element is possible.
	 */
	public boolean isEmbeddingPossibleGeneric(final String substrateId, final String virtualId,
			final boolean ignoreResources) {
		// find substrate element
		final ElementType sub = findElementType(substrateId);

		// find virtual element
		final ElementType virt = findElementType(virtualId);

		switch (virt) {
		// virtual element = switch
		case SWITCH:
			return sub == ElementType.SWITCH || sub == ElementType.SERVER;
		// virtual element = link
		case LINK:
			if (sub == ElementType.SERVER) {
				return true;
			} else if (sub == ElementType.PATH) {
				return ignoreResources
						|| getPathById(substrateId).getResidualBandwidth() >= getLinkById(virtualId).getBandwidth();
			}
			return false;
		// virtual element = server
		case SERVER:
			if (sub == ElementType.SERVER) {
				final SubstrateServer sserver = (SubstrateServer) getServerById(substrateId);
				final VirtualServer vserver = (VirtualServer) getServerById(virtualId);
				return (ignoreResources || (sserver.getResidualCpu() >= vserver.getCpu()
						&& sserver.getResidualMemory() >= vserver.getMemory()
						&& sserver.getResidualStorage() >= vserver.getStorage()));
			}
			return false;
		// virtual element = network
		case NETWORK:
			return sub == ElementType.NETWORK;
		default:
			throw new UnsupportedOperationException("Type of virtual element " + virtualId + " not found.");
		}
	}

	/**
	 * Finds the element type for a given element ID.
	 * 
	 * @param id The element ID to find the type for.
	 * @return Element type for the given ID.
	 */
	private ElementType findElementType(final String id) {
		ElementType type = ElementType.UNDEFINED;
		if (networkExists(id)) {
			type = ElementType.NETWORK;
		} else if (serverExists(id)) {
			type = ElementType.SERVER;
		} else if (switchExists(id)) {
			type = ElementType.SWITCH;
		} else if (pathExists(id)) {
			type = ElementType.PATH;
		} else if (linkExists(id)) {
			type = ElementType.LINK;
		} else {
			throw new IllegalArgumentException("Element with ID " + id + " not found.");
		}

		return type;
	}

	/**
	 * Removes a network embedding with the given ID from the substrate network.
	 *
	 * @param id Virtual network ID to remove embedding for.
	 */
	public void removeNetworkEmbedding(final String id) {
		checkStringValid(id);
		if (!networkExists(id)) {
			throw new IllegalArgumentException("A network with id " + id + " does not exists!");
		}

		final Network net = getNetworkById(id);

		if (net instanceof VirtualNetwork) {
			// Virtual network
			final VirtualNetwork vNet = (VirtualNetwork) net;
			unembedVirtualNetwork(vNet);
		}
	}

	/**
	 * Removes a network with the given ID from the model and re-creates the
	 * consistency of the model afterwards.
	 *
	 * @param id Network ID to remove.
	 */
	public void removeNetworkFromRoot(final String id) {
		removeNetworkFromRoot(id, true);
	}

	/**
	 * Removes a network with the given ID from the model and does not re-create the
	 * consistency of the model afterwards.
	 *
	 * @param id Network ID to remove.
	 */
	public void removeNetworkFromRootSimple(final String id) {
		removeNetworkFromRoot(id, false);
	}

	/**
	 * Removes a network with the given ID from the root.
	 *
	 * @param id                  Network ID to remove network for.
	 * @param recreateConsistency True if model must be consistent after the remove.
	 *                            Otherwise, the network will only be removed from
	 *                            the model.
	 */
	private void removeNetworkFromRoot(final String id, final boolean recreateConsistency) {
		checkStringValid(id);
		if (!networkExists(id)) {
			throw new IllegalArgumentException("A network with id " + id + " does not exists!");
		}

		final Network net = getNetworkById(id);

		if (recreateConsistency) {
			if (net instanceof SubstrateNetwork) {
				// Substrate network
				final SubstrateNetwork sNet = (SubstrateNetwork) net;

				final Set<VirtualNetwork> guestHostToNulls = new HashSet<>();
				for (final VirtualNetwork guest : sNet.getGuests()) {
					guestHostToNulls.add(guest);
					for (final Node n : getAllServersOfNetwork(guest.getName())) {
						final VirtualServer vsrv = (VirtualServer) n;
						vsrv.setHost(null);
					}

					for (final Node n : getAllSwitchesOfNetwork(guest.getName())) {
						final VirtualSwitch vsw = (VirtualSwitch) n;
						vsw.setHost(null);
					}

					for (final Link l : guest.getLinks()) {
						final VirtualLink vl = (VirtualLink) l;
						vl.setHost(null);
					}
				}

				guestHostToNulls.forEach(g -> {
					g.setHost(null);
				});
			} else {
				unembedVirtualNetwork((VirtualNetwork) net);
			}
		}

		getRoot().getNetworks().remove(net);
	}

	/**
	 * Removes the embedding of a virtual network.
	 *
	 * @param vNet Virtual network to remove embedding for.
	 */
	public void unembedVirtualNetwork(final VirtualNetwork vNet) {
		// Check if there is a host for this virtual network.
		if (vNet.getHost() != null) {
			final String hostNameId = vNet.getHost().getName();
			vNet.getHost().getGuests().remove(vNet);

			for (final Node n : vNet.getNodess()) {
				if (n instanceof VirtualServer) {
					final VirtualServer vsrv = (VirtualServer) n;
					final SubstrateServer host = vsrv.getHost();
					if (host == null) {
						continue;
					}
					host.getGuestServers().remove(vsrv);
					host.setResidualCpu(host.getResidualCpu() + vsrv.getCpu());
					host.setResidualMemory(host.getResidualMemory() + vsrv.getMemory());
					host.setResidualStorage(host.getResidualStorage() + vsrv.getStorage());
				} else if (n instanceof VirtualSwitch) {
					final VirtualSwitch vsw = (VirtualSwitch) n;
					if (vsw.getHost() == null) {
						continue;
					}
					vsw.getHost().getGuestSwitches().remove(vsw);
				}
			}

			for (final Link l : vNet.getLinks()) {
				final VirtualLink vl = (VirtualLink) l;
				final SubstrateHostLink host = vl.getHost();
				if (host == null) {
					continue;
				}
				host.getGuestLinks().remove(vl);

				if (!ModelFacadeConfig.IGNORE_BW) {
					if (host instanceof SubstratePath) {
						final SubstratePath hostPath = (SubstratePath) host;
						hostPath.setResidualBandwidth(hostPath.getResidualBandwidth() + vl.getBandwidth());
						for (final Link hostPathLink : hostPath.getLinks()) {
							final SubstrateLink sl = (SubstrateLink) hostPathLink;
							sl.setResidualBandwidth(sl.getResidualBandwidth() + vl.getBandwidth());
						}
					}
				}
			}

			// Correct other substrate paths residual values
			updateAllPathsResidualBandwidth(hostNameId);
		}
	}

	/**
	 * Removes a substrate server with the given ID from the network and re-creates
	 * the consistency of the model afterwards.
	 *
	 * @param id Substrate server ID to remove.
	 */
	public void removeSubstrateServerFromNetwork(final String id) {
		removeSubstrateServerFromNetwork(id, true);
	}

	/**
	 * Removes a substrate server with the given ID from the network and does not
	 * re-create the consistency of the model afterwards.
	 *
	 * @param id Substrate server ID to remove.
	 */
	public void removeSubstrateServerFromNetworkSimple(final String id) {
		removeSubstrateServerFromNetwork(id, false);
	}

	/**
	 * Removes a substrate server with the given ID from the network.
	 *
	 * @param id               Substrate server ID to remove.
	 * @param removeEmbeddings True if embeddings must be removed. Otherwise, the
	 *                         substrate server will only be removed from the
	 *                         network together will all links and paths containing
	 *                         it.
	 */
	private void removeSubstrateServerFromNetwork(final String id, final boolean removeEmbeddings) {
		final Server srv = getServerById(id);
		if (srv instanceof VirtualServer) {
			throw new IllegalArgumentException("Given ID is from a virtual server.");
		}

		final SubstrateServer ssrv = (SubstrateServer) srv;

		if (removeEmbeddings) {
			// Remove embedding of all guests
			final Set<VirtualElement> guestsToRemove = new HashSet<>();
			for (final VirtualServer guestSrv : ssrv.getGuestServers()) {
				guestsToRemove.add(guestSrv);
			}
			for (final VirtualSwitch guestSw : ssrv.getGuestSwitches()) {
				guestsToRemove.add(guestSw);
			}
			for (final VirtualLink guestL : ssrv.getGuestLinks()) {
				guestsToRemove.add(guestL);
			}
			guestsToRemove.forEach(e -> {
				if (e instanceof VirtualServer) {
					((VirtualServer) e).setHost(null);
				} else if (e instanceof VirtualSwitch) {
					((VirtualSwitch) e).setHost(null);
				} else if (e instanceof VirtualLink) {
					((VirtualLink) e).setHost(null);
				} else {
					throw new UnsupportedOperationException("Removal of guest " + e + " not yet implemented.");
				}
			});
		}

		// Remove all paths
		final Set<SubstratePath> pathsToRemove = new HashSet<>();
		pathsToRemove.addAll(ssrv.getIncomingPaths());
		pathsToRemove.addAll(ssrv.getOutgoingPaths());
		pathsToRemove.forEach(p -> {
			removeSubstratePath(p, removeEmbeddings);
		});

		// Remove all links
		final Set<Link> linksToRemove = new HashSet<>();
		linksToRemove.addAll(ssrv.getIncomingLinks());
		linksToRemove.addAll(ssrv.getOutgoingLinks());
		linksToRemove.forEach(sl -> {
			removeSubstrateLink(sl, removeEmbeddings);
		});

		// Remove server itself
		getNetworkById(ssrv.getNetwork().getName()).getNodess().remove(ssrv);
		EcoreUtil.delete(ssrv);
	}

	/**
	 * Removes the given substrate link from the network. Does not check any guests
	 * or paths.
	 *
	 * @param link             Substrate link to remove from the network.
	 * @param removeEmbeddings True if embeddings must be removed. Otherwise, the
	 *                         substrate link will only be removed from the network.
	 */
	private void removeSubstrateLink(final Link link, final boolean removeEmbeddings) {
		if (!(link instanceof SubstrateLink)) {
			throw new IllegalArgumentException("Given link is not a substrate link.");
		}
		final SubstrateLink sl = (SubstrateLink) link;

		if (removeEmbeddings) {
			sl.getGuestLinks().forEach(gl -> {
				gl.setHost(null);
			});
		}

		sl.getSource().getOutgoingLinks().remove(sl);
		sl.getTarget().getIncomingLinks().remove(sl);

		if (sl.getPaths() != null && !sl.getPaths().isEmpty()) {
			final Set<SubstratePath> pathsToRemove = new HashSet<>();
			sl.getPaths().forEach(p -> {
				removeSubstratePath(p, removeEmbeddings);
				pathsToRemove.add(p);
			});
			sl.getPaths().removeAll(pathsToRemove);
		}

		getNetworkById(link.getNetwork().getName()).getLinks().remove(link);
		links.remove(sl.getName());
		EcoreUtil.delete(sl);
	}

	/**
	 * Removes the given substrate path from the network. Does not check any guests.
	 *
	 * @param path             Substrate path to remove from the network.
	 * @param removeEmbeddings True if embeddings must be removed. Otherwise, the
	 *                         substrate path will only be removed from the network.
	 */
	private void removeSubstratePath(final SubstratePath path, final boolean removeEmbeddings) {
		if (!(path instanceof SubstratePath)) {
			throw new IllegalArgumentException("Given path is not a substrate path.");
		}
		final SubstratePath sp = path;

		// Remove path from look-up data structures
		paths.remove(sp.getName());
		pathSourceMap.get(sp.getSource()).remove(sp);

		// Remove it from guest links
		if (removeEmbeddings) {
			final Set<VirtualLink> guestLinksToRemove = new HashSet<>();
			guestLinksToRemove.addAll(sp.getGuestLinks());
			guestLinksToRemove.forEach(l -> l.setHost(null));
		}

		if (sp.getSource() != null) {
			sp.getSource().getOutgoingPaths().remove(sp);
		}

		if (sp.getTarget() != null) {
			sp.getTarget().getIncomingPaths().remove(sp);
		}

		// Remove path from links
		final Set<SubstrateLink> linksToRemove = new HashSet<>();
		sp.getLinks().forEach(l -> linksToRemove.add(l));
		linksToRemove.forEach(l -> l.getPaths().remove(sp));

		// Remove path from nodes
		final Set<SubstrateNode> nodesToRemove = new HashSet<>();
		sp.getNodes().forEach(n -> nodesToRemove.add(n));
		nodesToRemove.forEach(n -> n.getPaths().remove(sp));

		sp.getNetwork().getPaths().remove(sp);
		EcoreUtil.delete(sp);
	}

	/**
	 * Validates the current state of the model, i.e. checks if: (1) Every virtual
	 * element of an embedded virtual network is embedded. (2) Every residual value
	 * of the substrate elements is equal to the total value minus the embedded
	 * values.
	 */
	public void validateModel() {
		for (final Network net : getRoot().getNetworks()) {
			if (net instanceof SubstrateNetwork) {
				validateSubstrateNetwork((SubstrateNetwork) net);
			} else if (net instanceof VirtualNetwork) {
				validateVirtualNetwork((VirtualNetwork) net);
			}
		}
	}

	/**
	 * Validates a given substrate network.
	 *
	 * @param sNet Substrate network to validate.
	 */
	private void validateSubstrateNetwork(final SubstrateNetwork sNet) {
		// Check embedded virtual networks
		sNet.getGuests().forEach(g -> {
			if (!networkExists(g.getName())) {
				throw new InternalError("Substrate network " + sNet.getName()
						+ " has embeddings from a virtual network that is not part of this model.");
			}
		});

		for (final Node n : sNet.getNodess()) {
			if (n instanceof SubstrateServer) {
				final SubstrateServer srv = (SubstrateServer) n;

				if (srv.getCpu() < 0 || srv.getMemory() < 0 || srv.getStorage() < 0) {
					throw new InternalError(
							"At least one of the normal resources of server " + srv.getName() + " was less than zero.");
				}

				if (srv.getResidualCpu() < 0 || srv.getResidualMemory() < 0 || srv.getResidualStorage() < 0) {
					throw new InternalError("At least one of the residual resources of server " + srv.getName()
							+ " was less than zero.");
				}

				int sumGuestCpu = 0;
				int sumGuestMem = 0;
				int sumGuestSto = 0;

				for (final VirtualServer gs : srv.getGuestServers()) {
					sumGuestCpu += gs.getCpu();
					sumGuestMem += gs.getMemory();
					sumGuestSto += gs.getStorage();
				}

				if (srv.getResidualCpu() != srv.getCpu() - sumGuestCpu) {
					throw new InternalError("Residual CPU value of server " + srv.getName() + " was incorrect.");
				}

				if (srv.getResidualMemory() != srv.getMemory() - sumGuestMem) {
					throw new InternalError("Residual memory value of server " + srv.getName() + " was incorrect.");
				}

				if (srv.getResidualStorage() != srv.getStorage() - sumGuestSto) {
					throw new InternalError("Residual storage value of server " + srv.getName() + " was incorrect.");
				}
			} else if (n instanceof SubstrateSwitch) {
				// Do nothing?
			}

			// Check links
			if (!sNet.getLinks().containsAll(n.getIncomingLinks())) {
				throw new InternalError("Incoming links of node " + n.getName() + " are missing in network.");
			}

			if (!sNet.getLinks().containsAll(n.getOutgoingLinks())) {
				throw new InternalError("Outgoing links of node " + n.getName() + " are missing in network.");
			}

			// Check paths
			if (!sNet.getPaths().containsAll(((SubstrateNode) n).getIncomingPaths())) {
				throw new InternalError("Incoming paths of node " + n.getName() + " are missing in network.");
			}

			if (!sNet.getPaths().containsAll(((SubstrateNode) n).getOutgoingPaths())) {
				throw new InternalError("Outgoing paths of node " + n.getName() + " are missing in network.");
			}

			if (!sNet.getPaths().containsAll(((SubstrateNode) n).getPaths())) {
				throw new InternalError("Contained paths of node " + n.getName() + " are missing in network.");
			}
		}

		// If ignoring of bandwidth is activated, no link or path has to be checked.
		if (ModelFacadeConfig.IGNORE_BW) {
			return;
		}

		// Check that no link bandwidth value is below zero
		for (final Link l : sNet.getLinks()) {
			final SubstrateLink sl = (SubstrateLink) l;

			if (sl.getBandwidth() < 0) {
				throw new InternalError("Normal bandwidth of link " + sl.getName() + " was smaller than zero.");
			}

			if (sl.getResidualBandwidth() < 0) {
				throw new InternalError("Residual bandwidth of link " + sl.getName() + " was smaller than zero.");
			}
		}

		// Check if virtual links are also embedded to substrate ones (additional to
		// substrate paths).
		if (ModelFacadeConfig.LINK_HOST_EMBED_PATH) {
			for (final Link l : sNet.getLinks()) {
				final SubstrateLink sl = (SubstrateLink) l;
				int sumGuestBw = 0;

				for (final VirtualLink gl : sl.getGuestLinks()) {
					sumGuestBw += gl.getBandwidth();
				}

				if (sl.getResidualBandwidth() != sl.getBandwidth() - sumGuestBw) {
					throw new InternalError("Residual bandwidth value of link " + sl.getName() + " was incorrect.");
				}
			}
		}

		// Check the paths residual bandwidths
		for (final SubstratePath sp : sNet.getPaths()) {
			if (sp.getBandwidth() < 0) {
				throw new InternalError("Normal bandwidth of path " + sp.getName() + " was smaller than zero.");
			}

			if (sp.getResidualBandwidth() < 0) {
				throw new InternalError("Residual bandwidth of path " + sp.getName() + " was smaller than zero.");
			}

			// Find sum of all embedded virtual links bandwidth
			int sumGuestBw = 0;
			for (final VirtualLink gl : sp.getGuestLinks()) {
				sumGuestBw += gl.getBandwidth();
			}

			// Find minimum of all contained substrate links bandwidth
			int maxBw = Integer.MAX_VALUE;
			for (final SubstrateLink l : sp.getLinks()) {
				if (maxBw > l.getResidualBandwidth()) {
					maxBw = l.getResidualBandwidth();
				}
			}

			final int globalMin = Math.min(maxBw, sp.getBandwidth() - sumGuestBw);

			if (sp.getResidualBandwidth() != globalMin) {
				throw new InternalError("Residual bandwidth value of path " + sp.getName() + " was incorrect.");
			}
		}

		// Check if the residual bandwidths of the paths are not greater than the
		// smallest available bandwidth on each of the substrate links
		for (final SubstratePath p : sNet.getPaths()) {
			int lowestBw = Integer.MAX_VALUE;
			for (final SubstrateLink l : p.getLinks()) {
				if (lowestBw > l.getResidualBandwidth()) {
					lowestBw = l.getResidualBandwidth();
				}
			}
			if (lowestBw < p.getResidualBandwidth()) {
				throw new InternalError("Residual bandwidth value of path " + p.getName()
						+ " was higher than the lowest available residual bandwidth of one of its substrate links.");
			}
		}

		// Check if links are contained in paths
		for (final Link l : sNet.getLinks()) {
			final SubstrateLink sl = (SubstrateLink) l;
			if (!sNet.getPaths().containsAll(sl.getPaths())) {
				throw new InternalError();
			}
		}

		// Check if paths are contained in links
		for (final SubstratePath p : sNet.getPaths()) {
			final SubstratePath sp = p;
			if (!sNet.getLinks().containsAll(sp.getLinks())) {
				throw new InternalError();
			}
		}
	}

	/**
	 * Validates a given virtual network.
	 *
	 * @param vNet Virtual network to validate.
	 */
	private void validateVirtualNetwork(final VirtualNetwork vNet) {
		// If virtual network is embedded, all of its elements have to be embedded.
		SubstrateNetwork host = null;
		if (vNet.getHost() != null) {
			host = vNet.getHost();
		} else {
			// If virtual network is not embedded, all of its elements must not be embedded.
			host = null;
		}

		for (final Node n : vNet.getNodess()) {
			if (n instanceof VirtualServer) {
				final VirtualServer vsrv = (VirtualServer) n;

				if (vsrv.getCpu() < 0 || vsrv.getMemory() < 0 || vsrv.getStorage() < 0) {
					throw new InternalError("At least one of the resources of virtual server " + vsrv.getName()
							+ " was less than zero.");
				}

				if (host == null && vsrv.getHost() == null) {
					continue;
				} else if (host == null || vsrv.getHost() == null) {
					// Do nothing to trigger exception
				} else if (host.equals(vsrv.getHost().getNetwork())) {
					continue;
				}
				throw new InternalError("Validation of virtual server " + vsrv.getName() + " was incorrect.");
			} else if (n instanceof VirtualSwitch) {
				final VirtualSwitch vsw = (VirtualSwitch) n;
				if (host == null) {
					if (vsw.getHost() == null) {
						continue;
					}
				} else {
					if (vsw.getHost() != null && host.equals(vsw.getHost().getNetwork())) {
						continue;
					}
				}
				throw new InternalError("Validation of virtual switch " + vsw.getName() + " was incorrect.");
			}
		}

		for (final Link l : vNet.getLinks()) {
			final VirtualLink vl = (VirtualLink) l;

			if (vl.getBandwidth() < 0) {
				throw new InternalError("Normal bandwidth of link " + vl.getName() + " was smaller than zero.");
			}

			if (!checkVirtualLinkSourceTargetEmbedding(vl)) {
				throw new InternalError(
						"Validation of the source or target embedding of link " + vl.getName() + " failed.");
			}

			if (host == null && vl.getHost() == null) {
				continue;
			} else {
				if (vl.getHost() instanceof SubstrateServer) {
					final SubstrateServer lHost = (SubstrateServer) vl.getHost();
					if (host.equals(lHost.getNetwork())) {
						continue;
					}
				} else if (vl.getHost() instanceof SubstratePath) {
					final SubstratePath lHost = (SubstratePath) vl.getHost();
					if (host.equals(lHost.getNetwork())) {
						continue;
					}
				}
			}
			throw new InternalError("Validation of virtual link " + vl.getName() + " was incorrect.");
		}
	}

	/**
	 * Validates that the source and target of a given virtual link are also
	 * embedded on the hosts of the virtual link. There are two cases: (1) The
	 * virtual link was embedded onto a substrate server. In this case, both, the
	 * source and the target of the virtual link must be embedded onto the same
	 * substrate server as the virtual link. (2) The virtual link was embedded onto
	 * a substrate path. In this case, the virtual link's source node must be
	 * embedded onto the source node of the substrate path and the virtual link's
	 * target node must be embedded onto the target node of the substrate path.
	 * 
	 * @param vLink Virtual link to check.
	 * @return True if all checks were successful.
	 */
	public boolean checkVirtualLinkSourceTargetEmbedding(final VirtualLink vLink) {
		final VirtualNode src = (VirtualNode) vLink.getSource();
		final VirtualNode trg = (VirtualNode) vLink.getTarget();

		if (vLink.getHost() instanceof SubstrateServer) {
			if (src instanceof VirtualServer) {
				final VirtualServer srcVSrv = (VirtualServer) src;
				if (!srcVSrv.getHost().equals(vLink.getHost())) {
					return false;
				}
			} else if (src instanceof VirtualSwitch) {
				final VirtualSwitch srcVSw = (VirtualSwitch) src;
				if (!srcVSw.getHost().equals(vLink.getHost())) {
					return false;
				}
			}

			if (trg instanceof VirtualServer) {
				final VirtualServer trgVSrv = (VirtualServer) trg;
				if (!trgVSrv.getHost().equals(vLink.getHost())) {
					return false;
				}
			} else if (trg instanceof VirtualSwitch) {
				final VirtualSwitch trgVSw = (VirtualSwitch) trg;
				if (!trgVSw.getHost().equals(vLink.getHost())) {
					return false;
				}
			}
		} else if (vLink.getHost() instanceof SubstratePath) {
			final SubstratePath vLinkHost = (SubstratePath) vLink.getHost();
			if (src instanceof VirtualServer) {
				final VirtualServer srcVSrv = (VirtualServer) src;
				if (!srcVSrv.getHost().equals(vLinkHost.getSource())) {
					return false;
				}
			} else if (src instanceof VirtualSwitch) {
				final VirtualSwitch srcVSw = (VirtualSwitch) src;
				if (!srcVSw.getHost().equals(vLinkHost.getSource())) {
					return false;
				}
			}

			if (trg instanceof VirtualServer) {
				final VirtualServer trgVSrv = (VirtualServer) trg;
				if (!trgVSrv.getHost().equals(vLinkHost.getTarget())) {
					return false;
				}
			} else if (trg instanceof VirtualSwitch) {
				final VirtualSwitch trgVSw = (VirtualSwitch) trg;
				if (!trgVSw.getHost().equals(vLinkHost.getTarget())) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks if a given virtual network (ID) is currently in a floating state. A
	 * floating state is given if a substrate server hosting at least one component
	 * of the virtual network was removed in a "dirty" way. (This method checks
	 * every virtual element for valid substrate hosting, BTW.)
	 *
	 * @param vNet The virtual network to check floating state for.
	 * @return True if virtual network is in a floating state.
	 */
	public boolean checkIfFloating(final VirtualNetwork vNet) {
		for (final Node n : vNet.getNodess()) {
			if (n instanceof VirtualServer) {
				final VirtualServer vsrv = (VirtualServer) n;
				if (vsrv.getHost() == null || vsrv.getHost().getNetwork() == null
						|| !vsrv.getHost().getNetwork().getNodess().contains(vsrv.getHost())) {
					return true;
				}
			} else if (n instanceof VirtualSwitch) {
				final VirtualSwitch vsw = (VirtualSwitch) n;
				if ((vsw.getHost().getNetwork() == null)
						|| !vsw.getHost().getNetwork().getNodess().contains(vsw.getHost())) {
					return true;
				}
			}
		}

		for (final Link l : vNet.getLinks()) {
			final VirtualLink vl = (VirtualLink) l;
			if (vl.getHost() instanceof SubstratePath) {
				final SubstratePath host = (SubstratePath) vl.getHost();
				if ((host.getNetwork() == null) || !host.getNetwork().getPaths().contains(host)) {
					return true;
				}
			} else if (vl.getHost() instanceof SubstrateServer) {
				final SubstrateServer host = (SubstrateServer) vl.getHost();
				if ((host.getNetwork() == null) || !host.getNetwork().getNodess().contains(host)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Iterates over all substrate paths of a given substrate network (id) and
	 * corrects the residual bandwidth value of each path if one of its contained
	 * substrate links has a residual bandwidth value that is smaller than the one
	 * of the substrate path. This may happen if a substrate link is part of at
	 * least to substrate paths and one of the substrate paths has at least one
	 * virtual link embedded.
	 * 
	 * @param subNetId Id of the substrate network to correct the residual bandwidth
	 *                 of all paths for.
	 */
	public void updateAllPathsResidualBandwidth(final String subNetId) {
		final Network net = getNetworkById(subNetId);
		if (net == null || !(net instanceof SubstrateNetwork)) {
			throw new IllegalArgumentException("Provided id does not resolve to a substrate network.");
		}
		final SubstrateNetwork subNet = (SubstrateNetwork) net;
		for (final SubstratePath p : subNet.getPaths()) {
			updatePathResidualBandwidth(p);
		}
	}

	/**
	 * Corrects the residual bandwidth value of a given substrate path to the
	 * minimum of its available bandwidth (due to embeddings of virtual links) or to
	 * the available bandwidth of its substrate links (to the the embedding of
	 * virtual links to other paths).
	 * 
	 * @param path Substrate path to correct residual value for.
	 */
	private void updatePathResidualBandwidth(final SubstratePath path) {
		// Find minimum residual bandwidth of all contained substrate links
		int minBw = Integer.MAX_VALUE;
		for (final SubstrateLink l : path.getLinks()) {
			if (minBw > l.getResidualBandwidth()) {
				minBw = l.getResidualBandwidth();
			}
		}

		// Sanity check
		if (!ModelFacadeConfig.IGNORE_BW && minBw < 0) {
			throw new InternalError("There was at least one substrate link with a residual bandwidth value < 0.");
		}

		// Find value of the embedded virtual links
		int embeddedBw = 0;
		for (final VirtualLink l : path.getGuestLinks()) {
			embeddedBw += l.getBandwidth();
		}

		// global minimum; limited by embeddings of virtual links or limited by
		// substrate links
		final int globalMin = Math.min(minBw, path.getBandwidth() - embeddedBw);

		if (path.getResidualBandwidth() != globalMin) {
			path.setResidualBandwidth(globalMin);
		}
	}

	private enum ElementType {
		NETWORK, SERVER, SWITCH, PATH, LINK, UNDEFINED
	}

}
