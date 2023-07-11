package org.emoflon.gips.gipsl.examples.lsp2p.run;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.lsp2pInc.api.gips.Lsp2pIncGipsAPI;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import LectureStudioModel.LectureStudioModelPackage;
import LectureStudioModel.Network;
import lsmodel.generator.GenDistribution;
import lsmodel.generator.GenParameter;
import lsmodel.generator.LSGenerator;

public class LSp2pIncremental {

	public static void main(String[] args) {
		Lsp2pIncGipsAPI gipsApi = new Lsp2pIncGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/lsp2p_10clients.xmi";
		
		URI uri = URI.createFileURI(file);
		ResourceSet rs = prepareResource(uri);
		
		LSGenerator gen = new LSGenerator("FunSeed123".hashCode());
		Network net = gen.generateInitial(
				new GenParameter(GenDistribution.CONST, 500), 
				new GenParameter(GenDistribution.CONST, 100), 
				new GenParameter(GenDistribution.CONST, 10), 
				new GenParameter(GenDistribution.UNI, 10, 50),
				new GenParameter(GenDistribution.CONST, 150));
		rs.getResources().get(0).getContents().add(net);
		
		gipsApi.init(rs);
		gipsApi.buildILPProblem(true);
		ILPSolverOutput output = gipsApi.solveILPProblem();
		
		gipsApi.getInitRoot2Client().applyNonZeroMappings();
		gipsApi.getRoot2Client().applyNonZeroMappings();
		gipsApi.getInitRelay2Client().applyNonZeroMappings();
		gipsApi.getRelay2Client().applyNonZeroMappings();
		gipsApi.getUpdateTT().applyNonZeroMappings();
		
		String outputFile = instancesFolder + "/lsp2p_10clients_solved.xmi";
		try {
			gipsApi.saveResult(outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gipsApi.terminate();
		System.exit(0);
	}
	
	public static ResourceSet prepareResource(final URI uri) {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(LectureStudioModelPackage.eNS_URI, LectureStudioModelPackage.eINSTANCE);
		rs.createResource(uri);
		return rs;
	}
}
