package lsmodel.generator;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import LectureStudioModelB.Client;
import LectureStudioModelB.Configuration;
import LectureStudioModelB.LectureStudioModelBFactory;
import LectureStudioModelB.LectureStudioModelBPackage;
import LectureStudioModelB.LectureStudioServer;
import LectureStudioModelB.Network;

public class LSGenerator {

	public static final DecimalFormat df = new DecimalFormat("0.00");

	public static String projectFolder = System.getProperty("user.dir");
	public static String instancesFolder = projectFolder + "/../org.emoflon.gips.gipsl.examples.lsp2p/instances/";

	protected LectureStudioModelBFactory factory = LectureStudioModelBFactory.eINSTANCE;
	protected Random rnd;

	public static void initFileSystem() {
		File iF = new File(instancesFolder);
		if (!iF.exists()) {
			iF.mkdirs();
		}
	}

	public static void main(String[] args) {
		initFileSystem();
		simpleInitialBatch(10);
	}

	public static Network simpleInitialBatch(int numOfConfigs) {
		LSGenerator gen = new LSGenerator("FunSeed123".hashCode());
		double fileSize = 10;
		double lsBW = 250;
		int clients = 5;
		double clientsUp = 50;
		double clientsDown = 150;

		Network net = gen.generateInitial(numOfConfigs, new GenParameter(GenDistribution.CONST, fileSize),
				new GenParameter(GenDistribution.CONST, lsBW), new GenParameter(GenDistribution.CONST, clients),
				new GenParameter(GenDistribution.UNI, 10, clientsUp),
				new GenParameter(GenDistribution.CONST, clientsDown));

		StringBuilder fileName = new StringBuilder();
//		fileName.append("/LSBW@");
//		fileName.append(df.format(lsBW).replace(",", "-"));
//		fileName.append("_FS@");
//		fileName.append(df.format(fileSize).replace(",", "-"));
//		fileName.append("_CL@");
//		fileName.append(clients);
//		fileName.append("_CLUP@");
//		fileName.append(df.format(clientsUp).replace(",", "-"));
//		fileName.append("_CLDW@");
//		fileName.append(df.format(clientsDown).replace(",", "-"));
//		fileName.append("_EMPTY.xmi");
		fileName.append("lsp2p_10clients.xmi");

		try {
			save(net, instancesFolder + fileName.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return net;
	}

	public static void save(Network model, String path) throws IOException {
		URI uri = URI.createFileURI(path);
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(LectureStudioModelBPackage.eNS_URI, LectureStudioModelBPackage.eINSTANCE);
		Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		r.unload();
	}

	public LSGenerator(long seed) {
		rnd = new Random(seed);
	}

	public Network generateInitial(int numOfConfigs, GenParameter data, GenParameter lsBwUp, GenParameter nodes,
			GenParameter nodeBwUp, GenParameter nodeBwDown) {
		int id = 0;
		Network net = factory.createNetwork();
		net.setTime(0);

		LectureStudioServer ls = factory.createLectureStudioServer();
		ls.setData(data.getParam(rnd));
		ls.setId("" + id++);
		ls.setTxBW(lsBwUp.getParam(rnd));
		ls.setMinTxBW(ls.getTxBW() / 20.0);
		// Rx is actually not needed.
		ls.setRxBW(lsBwUp.getParam(rnd));
		ls.setInvTxBW(1.0 / ls.getTxBW());
		// Rx is actually not needed.
		ls.setInvRxBW(1.0 / ls.getRxBW());
//		ls.setResidualTxBW(ls.getTxBW());
//		// Same here.
//		ls.setResidualRxBW(ls.getRxBW());
//		ls.setAllocatedTxBW(0);
//		// Same here.
//		ls.setAllocatedRxBW(0);
		ls.setTransferTime(0);
		ls.setIsRelayClient(1);
		ls.setIsHasRoot(1);
		ls.setClients(0);
		net.getLectureStudioServer().add(ls);

		List<Configuration> configs = new LinkedList<>();
		for (int i = 0; i < numOfConfigs; i++) {
			Configuration config = factory.createConfiguration();
			config.setClients(i);
			config.setSlowDown(i);
			config.setBwSplit(1.0 / i);
			configs.add(config);
		}
		net.getConfigurations().addAll(configs);

		List<Client> clients = new LinkedList<>();
		for (int i = (int) nodes.getParam(rnd); i > 0; i--) {
			Client client = factory.createClient();
			client.setData(ls.getData());
			client.setDepth(-1);
			client.setId("" + id++);
			client.setIsRelayClient(0);
			client.setTxBW(nodeBwUp.getParam(rnd));
			client.setMinTxBW(client.getTxBW() / 2.0);
			client.setRxBW(nodeBwDown.getParam(rnd));
//			client.setResidualTxBW(client.getTxBW());
//			client.setResidualRxBW(client.getRxBW());
//			client.setAllocatedTxBW(0);
//			client.setAllocatedRxBW(0);
			client.setInvTxBW(1.0 / client.getTxBW());
			client.setInvRxBW(1.0 / client.getRxBW());
			client.setTransferTime(0);
			client.setIsRelayClient(0);
			client.setIsHasRoot(0);
			client.setClients(0);
			clients.add(client);
		}
		ls.getWaitingClients().addAll(clients);
		net.setNextId(id);

		return net;
	}

	public void insertRndClients(final LectureStudioServer ls, GenParameter nodes, GenParameter nodeBwUp,
			GenParameter nodeBwDown) {
		Network network = (Network) ls.eContainer();
		int id = network.getNextId();
		List<Client> clients = new LinkedList<>();
		for (int i = (int) nodes.getParam(rnd); i > 0; i--) {
			Client client = factory.createClient();
			client.setData(ls.getData());
			client.setDepth(-1);
			client.setId("" + id++);
			client.setIsRelayClient(0);
			client.setTxBW(nodeBwUp.getParam(rnd));
			client.setMinTxBW(client.getTxBW() / 2.0);
			client.setRxBW(nodeBwDown.getParam(rnd));
//			client.setResidualTxBW(client.getTxBW());
//			client.setResidualRxBW(client.getRxBW());
//			client.setAllocatedTxBW(0);
//			client.setAllocatedRxBW(0);
			client.setInvTxBW(1.0 / client.getTxBW());
			client.setInvRxBW(1.0 / client.getRxBW());
			client.setTransferTime(0);
			client.setIsRelayClient(0);
			client.setIsHasRoot(0);
			client.setClients(0);
			clients.add(client);
		}
		ls.getWaitingClients().addAll(clients);
		network.setNextId(id);
	}
}
