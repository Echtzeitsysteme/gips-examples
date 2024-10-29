package pta.evaluation.util;

import java.util.Map;

import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.util.Measurement;

import pta.scenario.ScenarioValidator;

public record EvaluationResult(String id, ScenarioValidator validator, ILPSolverOutput output, Map<String, Measurement> measurements) {
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("### ### ### Result of evaluation: "+id+"\n");
		sb.append("\t### ### Solver result:\n");
		sb.append(validator.getLog());
		sb.append("\t### ### Measurements:\n");
		for(var measurement : measurements.entrySet()) {
			sb.append("["+measurement.getKey()+": "+measurement.getValue().durationSeconds()+"]\t");
		}
		return sb.toString();
	}
}
