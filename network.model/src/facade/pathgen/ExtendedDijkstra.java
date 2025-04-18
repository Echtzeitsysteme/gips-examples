package facade.pathgen;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.Link;
import model.Node;
import model.SubstrateLink;
import model.SubstrateNetwork;
import model.SubstrateNode;

/**
 * Dijkstra path finding algorithm that is used to generate the paths for all
 * models. Heavily based on this Wikipedia article:
 * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 *
 * This is a slightly adapted version compared to the normal Dijkstra
 * implementation. It gets a set of nodes and links to ignore during the path
 * finding process. This is a must-have behavior, because Yen's algorithm needs
 * to delete nodes and links but the model itself should not be changed.
 *
 * @author Maximilian Kratz {@literal <maximilian.kratz@es.tu-darmstadt.de>}
 */
public class ExtendedDijkstra extends Dijkstra {

	/**
	 * Set of substrate nodes to ignore during the path finding process.
	 */
	private Set<SubstrateNode> ignoredNodes;

	/**
	 * Starts the whole algorithm for a given substrate network and one given
	 * substrate node as start.
	 *
	 * @param net          SubstrateNetwork to generate all paths for.
	 * @param start        SubstrateNode to start with.
	 * @param ignoredNodes Set of substrate nodes to ignore.
	 * @param ignoredLinks Set of substrate links to ignore.
	 */
	protected void dijkstra(final SubstrateNetwork net, final SubstrateNode start,
			final Set<SubstrateNode> ignoredNodes, final Set<SubstrateLink> ignoredLinks) {
		this.ignoredNodes = ignoredNodes;
		init(net, start);

		while (!nodes.isEmpty()) {
			final SubstrateNode u = popSmallestDistNode();

			// If no node with the smallest distance can be found, the graph is not fully
			// connected ->
			// Break the loop and return.
			if (u == null) {
				break;
			}

			for (final Link out : u.getOutgoingLinks()) {
				// Check that link gets ignored if its contained in the ignored links set
				if (ignoredLinks.contains(out)) {
					continue;
				}

				final SubstrateNode next = (SubstrateNode) out.getTarget();
				if (nodes.contains(next) && !ignoredNodes.contains(next)) {
					distanceUpdate(u, next);
				}
			}
		}
	}

	/**
	 * Initializes this Dijkstra algorithm class.
	 *
	 * @param net   SubstrateNetwork to use.
	 * @param start SubstrateNode to use as a start.
	 */
	private void init(final SubstrateNetwork net, final SubstrateNode start) {
		for (final Node n : net.getNodess()) {
			final SubstrateNode sn = (SubstrateNode) n;

			// Check if sn must be ignored
			if (ignoredNodes.contains(sn)) {
				continue;
			}

			dists.put(sn, Integer.MAX_VALUE);
			prevs.put(sn, null);
			nodes.add(sn);
			prioNodes.add(sn);
		}

		dists.replace(start, 0);
		// After updating a distance the corresponding node has to be re-added to the
		// priority queue to
		// update its priority
		prioNodes.remove(start);
		prioNodes.add(start);
	}

	/**
	 * Returns a list of substrate nodes that form the shortest path from the global
	 * start to a given target node.
	 *
	 * @param target Target node to calculate path for.
	 * @return List of substrate nodes that form the shortest path from start to
	 *         target.
	 */
	protected List<SubstrateNode> shortestPathNodes(final SubstrateNode target) {
		final List<SubstrateNode> nodes = new LinkedList<>();
		SubstrateNode u = target;
		if (prevs.get(u) != null) {
			nodes.add(0, u);
		}
		while (prevs.get(u) != null) {
			u = prevs.get(u);
			nodes.add(0, u);
		}

		return nodes;
	}

}
