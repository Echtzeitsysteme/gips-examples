package org.emoflon.gips.gipsl.examples.mdvne;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.ResourceSet;

import model.Network;
import model.Root;
import model.SubstrateNetwork;

public class MdvneGipsIflyeAdapterUtil {

	private MdvneGipsIflyeAdapterUtil() {
	}

	/**
	 * Checks if the given model (resource set) contains more than one substrate
	 * network. If this is the case, log a warning to the console.
	 * 
	 * @param model ResourceSet to check.
	 */
	public static void checkMultipleSubstrateNetworks(final ResourceSet model) {
		final Root root = (Root) model.getResources().get(0).getContents().get(0);
		final Collection<Network> networks = root.getNetworks();
		final Set<SubstrateNetwork> substrateNetworks = new HashSet<>();

		for (final Network n : networks) {
			if (n instanceof SubstrateNetwork snet) {
				substrateNetworks.add(snet);
			}
		}

		if (substrateNetworks.size() > 1) {
			System.out.println(
					"=> GIPS adapter found more than 1 substrate network in the model (" + substrateNetworks.size()
							+ "). The GIPS-based VNE algorithm implementation does not filter the substrate "
							+ "networks, i.e., it will choose one out of all available substrate networks.");
		}
	}

}
