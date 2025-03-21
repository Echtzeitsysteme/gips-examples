package pta.evaluation.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SolverOutput {
	protected Map<Object, org.emoflon.gips.core.milp.SolverOutput> outputs = Collections
			.synchronizedMap(new LinkedHashMap<>());

	public SolverOutput() {
	};

	public SolverOutput(final org.emoflon.gips.core.milp.SolverOutput output) {
		this.outputs.put(null, output);
	}

	public SolverOutput(final Object problem, final org.emoflon.gips.core.milp.SolverOutput output) {
		this.outputs.put(problem, output);
	}

	public SolverOutput(final SolverOutput other, final org.emoflon.gips.core.milp.SolverOutput output) {
		this.outputs.putAll(other.outputs);
		this.outputs.put(null, output);
	}

	public SolverOutput(final Object problem, final SolverOutput other,
			final org.emoflon.gips.core.milp.SolverOutput output) {
		this.outputs.putAll(other.outputs);
		this.outputs.put(problem, output);
	}

	public void addOutput(final Object problem, final org.emoflon.gips.core.milp.SolverOutput output) {
		outputs.put(problem, output);
	}

	public boolean isOptimal() {
		return !outputs.values().stream().filter(o -> o.status() != org.emoflon.gips.core.milp.SolverStatus.OPTIMAL)
				.findAny().isPresent();
	}

	public double optimality() {
		return outputs.values().stream().filter(o -> o.status() == org.emoflon.gips.core.milp.SolverStatus.OPTIMAL)
				.map(o -> 1.0).reduce(0.0, (sum, val) -> sum + val) / outputs.size();
	}

	public boolean noStaticConstraintViolation() {
		return !outputs.values().stream().filter(o -> o.validationLog().isNotValid()).findAny().isPresent();
	}

	public double getObjectiveValue() {
		return outputs.values().stream().map(o -> o.objectiveValue()).reduce(0.0, (sum, val) -> sum + val);
	}

	public String getSolverStatus() {
		StringBuilder sb = new StringBuilder();
		for (org.emoflon.gips.core.milp.SolverOutput out : outputs.values()) {
			sb.append(out.status() + "\n");
		}
		return sb.toString();
	}

	public Map<Object, org.emoflon.gips.core.milp.SolverOutput> getOutputs() {
		return outputs;
	}
}
