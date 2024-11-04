package pta.evaluation.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;

public class SolverOutput {
	protected Map<Object, ILPSolverOutput> outputs = Collections.synchronizedMap(new LinkedHashMap<>());
	
	public SolverOutput(){};
	
	public SolverOutput(final ILPSolverOutput output) {
		this.outputs.put(null, output);
	}
	
	public SolverOutput(final Object problem, final ILPSolverOutput output) {
		this.outputs.put(problem, output);
	}
	
	public SolverOutput(final SolverOutput other, final ILPSolverOutput output) {
		this.outputs.putAll(other.outputs);
		this.outputs.put(null, output);
	}
	
	public SolverOutput(final Object problem, final SolverOutput other, final ILPSolverOutput output) {
		this.outputs.putAll(other.outputs);
		this.outputs.put(problem, output);
	}
	
	public void addOutput(final Object problem, final ILPSolverOutput output) {
		outputs.put(problem, output);
	}
	
	public boolean isOptimal() {
		return !outputs.values().stream().filter(o -> o.status() != ILPSolverStatus.OPTIMAL).findAny().isPresent();
	}
	
	public double optimality() {
		return outputs.values().stream()
				.filter(o -> o.status() == ILPSolverStatus.OPTIMAL)
				.map(o -> 1.0)
				.reduce(0.0, (sum, val) -> sum + val) / outputs.size();
	}
	
	public boolean noStaticConstraintViolation() {
		return !outputs.values().stream().filter(o -> o.validationLog().isNotValid()).findAny().isPresent();
	}
	
	public double getObjectiveValue() {
		return outputs.values().stream().map(o -> o.objectiveValue()).reduce(0.0, (sum, val) -> sum+val);
	}
	
	public String getSolverStatus() {
		StringBuilder sb = new StringBuilder();
		for(ILPSolverOutput out : outputs.values()) {
			sb.append(out.status()+"\n");
		}
		return sb.toString();
	}
	
	public Map<Object, ILPSolverOutput> getOutputs() {
		return outputs;
	}
}
