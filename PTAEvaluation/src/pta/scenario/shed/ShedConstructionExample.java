package pta.scenario.shed;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.gips.core.util.IMeasurement;
import org.emoflon.gips.core.util.Observer;

import PTAProblem.api.gips.PTAProblemGipsAPI;

public class ShedConstructionExample {

	public static void main(String[] args) {

		PTAProblemGipsAPI gipsApi = new PTAProblemGipsAPI();
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		String file = instancesFolder + "/dissertation_full_example.xmi";
		URI uri = URI.createFileURI(file);
		gipsApi.init(uri);

		gipsApi.buildProblem(true);
		SolverOutput output = gipsApi.solveProblem();
		gipsApi.getAom().applyNonZeroMappings();
		gipsApi.getProjectCost().applyNonZeroMappings();

		final Observer measurements = gipsApi.getLatestMetrics().measurements();
		final Map<String, IMeasurement> measurementBuild = measurements.getStageMeasurements(Observer.STAGE_BUILD);
		final Map<String, IMeasurement> measurementSolve = measurements.getStageMeasurements(Observer.STAGE_SOLVE);

		System.out.println(String.format("PM: %s s.", //
				measurementBuild.get("PM").maxDurationSeconds()));
		System.out.println(String.format("BUILD_GIPS: %s s.", //
				measurementBuild.get("BUILD_GIPS").maxDurationSeconds()));
		System.out.println(String.format("BUILD_SOLVER: %s s.", //
				measurementBuild.get("BUILD_SOLVER").maxDurationSeconds()));
		System.out.println(String.format("BUILD: %s s.", //
				measurementBuild.get("BUILD").maxDurationSeconds()));
		System.out.println(String.format("SOLVE_PROBLEM: %s s.", //
				measurementSolve.get("SOLVE_PROBLEM").maxDurationSeconds()));

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
