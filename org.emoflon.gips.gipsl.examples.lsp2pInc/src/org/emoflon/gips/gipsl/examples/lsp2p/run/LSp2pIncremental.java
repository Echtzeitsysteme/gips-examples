package org.emoflon.gips.gipsl.examples.lsp2p.run;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.gipsl.examples.lsp2pInc.api.gips.Lsp2pIncGipsAPI;

public class LSp2pIncremental {

	public static void main(String[] args) {
		Lsp2pIncGipsAPI gipsApi = new Lsp2pIncGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/lsp2p_10clients.xmi";
		URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblem(true);
		ILPSolverOutput output = gipsApi.solveILPProblem();
		
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
