package org.emoflon.gips.gipsl.examples.lsp2p.run;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.lsp2p.api.gips.Lsp2pGipsAPI;

public class LSp2pBatch {

	public static void main(String[] args) {
		Lsp2pGipsAPI gipsApi = new Lsp2pGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/lsp2p_10clients.xmi";
		URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		ILPSolverOutput output = gipsApi.solveILPProblem();
		gipsApi.getRelay2Client().applyNonZeroMappings();
		gipsApi.getNode2Cfg().applyNonZeroMappings();

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

}
