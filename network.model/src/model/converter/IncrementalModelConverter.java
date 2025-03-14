package model.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import facade.ModelFacade;
import model.Node;
import model.VirtualNetwork;
import model.VirtualServer;

/**
 * Incremental model converter that converts a JSON file with virtual or
 * substrate network information to the model (one virtual network by one).
 *
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IncrementalModelConverter extends BasicModelConverter {

	/**
	 * JSON array that stores all read networks.
	 */
	static JsonArray networks;

	/**
	 * Counter to increment and get next network from.
	 */
	static int counter = 0;

	/**
	 * Converts a JSON file from a given path to the model incrementally. On the
	 * first run, it only creates the first network and returns its name.
	 *
	 * @param path      Path to read JSON file from.
	 * @param isVirtual True if networks should be virtual.
	 * @return ID of the one incremental created network.
	 */
	public static String jsonToModelIncremental(final String path, final boolean isVirtual) {
		if (networks == null) {
			// Initial run
			final JsonObject json = FileUtils.readFileToJson(path);
			networks = (JsonArray) json.get("networks");
		}

		// Exit condition
		if (!(counter < networks.size())) {
			return null;
		}

		final JsonObject net = (JsonObject) networks.get(counter);
		final JsonElement name = net.get("id");
		final JsonArray switches = (JsonArray) net.get("switches");
		final JsonArray servers = (JsonArray) net.get("servers");
		final JsonArray links = (JsonArray) net.get("links");

		createNetwork(name, switches, servers, links, isVirtual);

		if (!isVirtual) {
			ModelFacade.getInstance().createAllPathsForNetwork(name.getAsString());
		} else {
			final VirtualNetwork vnet = (VirtualNetwork) ModelFacade.getInstance().getNetworkById(name.getAsString());
			long sumCpu = 0;
			long sumMem = 0;
			long sumSto = 0;
			for (final Node n : vnet.getNodess()) {
				if (n instanceof VirtualServer) {
					final VirtualServer vsrv = (VirtualServer) n;
					sumCpu += vsrv.getCpu();
					sumMem += vsrv.getMemory();
					sumSto += vsrv.getStorage();
				}
			}
			vnet.setCpu(sumCpu);
			vnet.setMemory(sumMem);
			vnet.setStorage(sumSto);
		}

		counter++;
		return name.getAsString();
	}

}
