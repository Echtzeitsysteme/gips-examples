package org.emoflon.gips.gipsl.examples.lsp2p.run;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;
import org.emoflon.gips.gipsl.examples.lsp2pInc.api.gips.Lsp2pIncGipsAPI;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import LectureStudioModelB.LectureStudioModelBPackage;
import LectureStudioModelB.Network;
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
		Network net = gen.generateInitial(81, new GenParameter(GenDistribution.CONST, 1),
				new GenParameter(GenDistribution.CONST, 500), new GenParameter(GenDistribution.CONST, 8),
				new GenParameter(GenDistribution.UNI, 10, 100), new GenParameter(GenDistribution.CONST, 150));
		rs.getResources().get(0).getContents().add(net);

		double tick = System.currentTimeMillis();

		gipsApi.init(rs);
		int i = 0;
		int limit = 10;
		ILPSolverOutput output = null;
		do {
			if (i != 0) {
				gen.insertRndClients(net.getLectureStudioServer().get(0), new GenParameter(GenDistribution.CONST, 8),
						new GenParameter(GenDistribution.UNI, 10, 100), new GenParameter(GenDistribution.CONST, 150));
			}

			do {
				gipsApi.buildILPProblem(true);
				output = gipsApi.solveILPProblem();

				gipsApi.getInitRoot2Client().applyNonZeroMappings(false);
				gipsApi.getRoot2Client().applyNonZeroMappings(false);
				gipsApi.getInitRelay2Client().applyNonZeroMappings(false);
				gipsApi.getRelay2Client().applyNonZeroMappings(false);
				gipsApi.getUpdateTT().applyNonZeroMappings(false);
			} while (gipsApi.getEMoflonAPI().ls2Waiting().hasMatches(true) && output != null
					&& output.status() == ILPSolverStatus.OPTIMAL);

			if (output == null || output.status() != ILPSolverStatus.OPTIMAL)
				break;

			i++;

		} while (i < limit);

		double tock = System.currentTimeMillis();

		System.out.println("\n\n****** Took: " + (tock - tick) + "ms");
		System.out.println(output);

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
		rs.getPackageRegistry().put(LectureStudioModelBPackage.eNS_URI, LectureStudioModelBPackage.eINSTANCE);
		rs.createResource(uri);
		return rs;
	}
}
