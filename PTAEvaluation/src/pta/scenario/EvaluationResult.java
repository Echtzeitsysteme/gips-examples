package pta.scenario;

import org.emoflon.gips.core.ilp.ILPSolverOutput;

public record EvaluationResult(ScenarioValidator validator, ILPSolverOutput output) {
	@Override
	public final String toString() {
		return validator.getLog();
	}
}
