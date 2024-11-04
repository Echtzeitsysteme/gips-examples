package pta.scenario.house;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.emoflon.gips.core.ilp.ILPSolverOutput;
import org.emoflon.gips.core.ilp.ILPSolverStatus;
import org.emoflon.gips.core.util.Observer;

import PTAConstraintConfigF.api.gips.PTAConstraintConfigFGipsAPI;
import PersonTaskAssignments.*;
import pta.evaluation.util.EvaluationResult;
import pta.evaluation.util.SolverOutput;
import pta.scenario.ScenarioValidator;

public class HouseConstructionBatchG extends HouseConstructionBatchGeneric<PTAConstraintConfigFGipsAPI>{

	final public static String TYPE = "BATCH-G"; 
	
	public HouseConstructionBatchG(String name) {
		super(name);
	}

	@Override
	public PTAConstraintConfigFGipsAPI newAPI() {
		return new PTAConstraintConfigFGipsAPI();
	}
	
	@Override
	public EvaluationResult run(String outputFile) throws IOException {
		PersonTaskAssignmentModel model = (PersonTaskAssignmentModel) api.getEMoflonAPI().getModel().getResources().get(0).getContents().get(0);
		SolverOutput output = new SolverOutput();
		for(Project p : model.getProjects()) {
			for(Task t : p.getTasks()) {
				double cost = p.getSumSalary();
				p.setSumSalary(0.0);
				api.getAom().getGTRule().bindProject(p);
				api.getAom().getGTRule().bindTask(t);
				api.getProjectCost().getGTRule().bindProject(p);
				api.getEMoflonAPI().taskToRequirement().bindTask(t);
//				api.getEMoflonAPI().taskSequence().bindTask(t);
//				api.getEMoflonAPI().taskSequence().bindProject(p);
//				api.getEMoflonAPI().finalTaskSequence().bindFinalTask(t);
//				api.getEMoflonAPI().finalTaskSequence().bindProject(p);
				api.buildILPProblemTimed(true);
				ILPSolverOutput out = api.solveILPProblemTimed();
				if(out.status() == ILPSolverStatus.OPTIMAL) {
					executeGT();
					p.setSumSalary(p.getSumSalary()+cost);
				}
				output.addOutput(t, out);
			}
		}
		
		
		ScenarioValidator validator = new ScenarioValidator((PersonTaskAssignmentModel) api.getEMoflonApp().getModel().getResources().get(0).getContents().get(0), output);
		validator.validate();
		
		if(outputFile != null && !outputFile.isBlank() && !outputFile.isEmpty()) {
			api.saveResult(outputFile);
		}
		
		api.terminate();
		
		Observer obs = Observer.getInstance();
		return new EvaluationResult(obs.getCurrentSeries(), validator, output, obs.getMeasurements(obs.getCurrentSeries()));
	}
	
	@Override
	public void executeGT() {
		api.getAom().applyNonZeroMappings();
		api.getProjectCost().applyNonZeroMappings();
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String getGipsModelPath() {
		return "PTAConstraintConfigF/api/gips/gips-model.xmi";
	}

	@Override
	public String getIbexModelPath() {
		return "PTAConstraintConfigF/api/ibex-patterns.xmi";
	}

	@Override
	public String getHiPEModelPath() {
		return "PTAConstraintConfigF/hipe/engine/hipe-network.xmi";
	}
	
	@Override
	public String getHiPEEngineFQN() {
		return "PTAConstraintConfigF.hipe.engine.HiPEEngine";
	}
}
