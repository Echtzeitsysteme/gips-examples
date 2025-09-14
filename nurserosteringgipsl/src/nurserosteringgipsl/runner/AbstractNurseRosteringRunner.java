package nurserosteringgipsl.runner;

import java.io.File;
import java.io.IOException;

import org.emoflon.gips.core.milp.SolverOutput;

import nurserosteringgipsl.api.gips.NurserosteringgipslGipsAPI;

public abstract class AbstractNurseRosteringRunner {

	protected void checkIfFileExists(final String path) {
		final File xmiInputFile = new File(path);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("Input XMI file <" + path + "> could not be found.");
		}
	}

	protected double buildAndSolve(final NurserosteringgipslGipsAPI gipsApi) {
		gipsApi.buildProblem(true);
		final SolverOutput output = gipsApi.solveProblem();
		if (output.solutionCount() == 0) {
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());
		System.out.println("---");
		return output.objectiveValue();
	}

	protected void applySolution(final NurserosteringgipslGipsAPI gipsApi) {
		// Apply found solution
		gipsApi.getE2s().applyNonZeroMappings();
		gipsApi.getE2s().getMappings().forEach((s,m) -> {
			if(m.getValue() != 0) {
				System.out.println(m.getEmployee().getName() + " -> " + m.getShift().getName());
				
		}});
		
		System.out.println(gipsApi.getViolatedDaysOff().getMappings().size());
		
		gipsApi.getViolatedDaysOff().getMappings().forEach((s, m) ->  {
			
				//System.out.println(m.getDay().getDate() + m.getDayOffRequest().getDate());
				//System.out.println("date = " + m.getDay().getDate() + "number = " + String.valueOf(m.getDay().getNumber()));
			
	   
		});
		
	}

	protected void gipsSave(final NurserosteringgipslGipsAPI gipsApi, final String path) {
		try {
			gipsApi.saveResult(path);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
