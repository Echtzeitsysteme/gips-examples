package pta.evaluation.util;

import java.util.Map;

import org.emoflon.gips.core.util.IMeasurement;

import pta.scenario.ScenarioValidator;

public record EvaluationResult(String id, ScenarioValidator validator, SolverOutput output, Map<String, IMeasurement> measurements) {
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("### ### ### Result of evaluation: "+id+"\n");
		sb.append("\t### ### Model statistics:\n");
		sb.append("Projects: "+validator.getNumberOfProjects()+", ");
		sb.append("Tasks: "+validator.getNumberOfTasks()+", ");
		sb.append("Requirments: "+validator.getNumberOfRequirements()+", ");
		sb.append("Persons: "+validator.getNumberOfPersons()+", ");
		sb.append("Offers: "+validator.getNumberOfOffers()+", ");
		sb.append("Weeks: "+validator.getNumberOfWeeks()+", ");
		sb.append("Total: "+
						(validator.getNumberOfProjects() + 
						validator.getNumberOfTasks() + 
						validator.getNumberOfRequirements() +
						validator.getNumberOfPersons() +
						validator.getNumberOfOffers() +
						validator.getNumberOfWeeks()) 
						+"\n");
		sb.append("\t### ### Solver result:\n");
		sb.append(validator.getLog());
		sb.append("\t### ### Measurements:\n");
		for(var measurement : measurements.entrySet()) {
			sb.append("["+measurement.getKey()+": "+measurement.getValue().totalDurationSeconds()+"s(TOTAL)]\t");
			sb.append("["+measurement.getKey()+": "+measurement.getValue().avgDurationSeconds()+"s(avg)]\t");
			sb.append("["+measurement.getKey()+": "+measurement.getValue().maxDurationSeconds()+"s(max)]\t");
			sb.append("["+measurement.getKey()+": "+measurement.getValue().minDurationSeconds()+"s(min)]\t");
			sb.append("["+measurement.getKey()+": "+measurement.getValue().avgMemoryMB()+"MB(avg)]\t");
			sb.append("["+measurement.getKey()+": "+measurement.getValue().maxMemoryMB()+"MB(max)]\t");
			sb.append("["+measurement.getKey()+": "+measurement.getValue().minMemoryMB()+"MB(min)]\t\n");
		}
		return sb.toString();
	}
}
