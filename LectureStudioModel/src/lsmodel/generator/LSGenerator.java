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

import LectureStudioModel.Client;
import LectureStudioModel.LectureStudioModelFactory;
import LectureStudioModel.LectureStudioModelPackage;
import LectureStudioModel.LectureStudioServer;
import LectureStudioModel.Network;

public class LSGenerator {
	
	public static final DecimalFormat df = new DecimalFormat("0.00");

	public static String projectFolder = System.getProperty("user.dir");
	public static String instancesFolder = projectFolder + "/instances";
	
	protected LectureStudioModelFactory factory = LectureStudioModelFactory.eINSTANCE;
	protected Random rnd;
	
	public static void initFileSystem() {
		File iF = new File(instancesFolder);
		if (!iF.exists()) {
			iF.mkdirs();
		}
	}

	public static void main(String[] args) {
		initFileSystem();
		simpleInitial();
	}
	
	public static Network simpleInitial() {
		LSGenerator gen = new LSGenerator("FunSeed123".hashCode());
		double fileSize = 500;
		double lsBW = 100;
		int clients = 10;
		double clientsUp = 20;
		double clientsDown = 100;
		
		Network net = gen.generateInitial(
				new GenParameter(GenDistribution.CONST, fileSize), 
				new GenParameter(GenDistribution.CONST, lsBW), 
				new GenParameter(GenDistribution.CONST, clients), 
				new GenParameter(GenDistribution.CONST, clientsUp),
				new GenParameter(GenDistribution.CONST, clientsDown));
		
		StringBuilder fileName = new StringBuilder();
		fileName.append("/LSBW@");
		fileName.append(df.format(lsBW).replace(",", "-"));
		fileName.append("_FS@");
		fileName.append(df.format(fileSize).replace(",", "-"));
		fileName.append("_CL@");
		fileName.append(clients);
		fileName.append("_CLUP@");
		fileName.append(df.format(clientsUp).replace(",", "-"));
		fileName.append("_CLDW@");
		fileName.append(df.format(clientsDown).replace(",", "-"));
		fileName.append("_EMPTY.xmi");

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
		rs.getPackageRegistry().put(LectureStudioModelPackage.eNS_URI, LectureStudioModelPackage.eINSTANCE);
		Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		r.unload();
	}
	
	public LSGenerator(long seed) {
		rnd = new Random(seed);
	}
	
	public Network generateInitial(GenParameter data, GenParameter lsBwUp, GenParameter nodes, GenParameter nodeBwUp, GenParameter nodeBwDown) {
		int id = 0;
		Network net = factory.createNetwork();
		net.setTime(0);
		
		LectureStudioServer ls = factory.createLectureStudioServer();
		ls.setData(data.getParam(rnd));
		ls.setId("LS"+id++);
		ls.setSendBandwidth(lsBwUp.getParam(rnd));
		ls.setIsLsServer(true);
		net.getLectureStudioServer().add(ls);
		
		List<Client> clients = new LinkedList<>();
		for(int i = (int)nodes.getParam(rnd); i>0; i--) {
			Client client = factory.createClient();
			client.setData(0.0);
			client.setDepth(-1);
			client.setId("CL"+id++);
			client.setIsRelayClient(false);
			client.setSendBandwidth(nodeBwUp.getParam(rnd));
			client.setReceiveBandwidth(nodeBwDown.getParam(rnd));
			client.setIsLsServer(false);
			clients.add(client);
		}
		ls.getWaitingClients().addAll(clients);
		
		return net;
	}
}


