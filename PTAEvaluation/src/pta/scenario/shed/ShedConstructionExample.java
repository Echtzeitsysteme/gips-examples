package pta.scenario.shed;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;
import org.emoflon.gips.core.milp.SolverOutput;

import PTAProblem.api.gips.PTAProblemGipsAPI;

public class ShedConstructionExample {

	public static void main(String[] args) {
		
		final Observer obs = Observer.getInstance();
		obs.setCurrentSeries("Eval");

		PTAProblemGipsAPI gipsApi = new PTAProblemGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/dissertation_full_example.xmi";
		URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildILPProblemTimed(true);
		SolverOutput output = gipsApi.solveProblemTimed();
		gipsApi.getAom().applyNonZeroMappings();
		gipsApi.getProjectCost().applyNonZeroMappings();
		
		final Map<String, IMeasurement> measurements = obs.getMeasurements("Eval");
		System.out.println("PM: " + measurements.get("PM").maxDurationSeconds());
		System.out.println("BUILD_GIPS: " + measurements.get("BUILD_GIPS").maxDurationSeconds());
		System.out.println("BUILD_SOLVER: " + measurements.get("BUILD_SOLVER").maxDurationSeconds());
		System.out.println("BUILD: " + measurements.get("BUILD").maxDurationSeconds());
		System.out.println("SOLVE_PROBLEM: " + measurements.get("SOLVE_PROBLEM").maxDurationSeconds());

		String outputFile = instancesFolder + "/dissertation_full_example_solved.xmi";
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
