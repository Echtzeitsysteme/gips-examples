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

public class HouseConstructionBatchF extends HouseConstructionBatchGeneric<PTAConstraintConfigFGipsAPI>{

	final public static String TYPE = "BATCH-F"; 
	
	public HouseConstructionBatchF(String name) {
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
			double cost = p.getSumSalary();
			p.setSumSalary(0.0);
			api.getAom().getGTRule().bindProject(p);
			api.getProjectCost().getGTRule().bindProject(p);
//			api.getEMoflonAPI().taskSequence().bindProject(p);
//			api.getEMoflonAPI().finalTaskSequence().bindProject(p);
			api.buildILPProblemTimed(true);
			ILPSolverOutput out = api.solveILPProblemTimed();
			if(out.status() == ILPSolverStatus.OPTIMAL) {
				executeGT();
				p.setSumSalary(p.getSumSalary()+cost);
			}
			output.addOutput(p, out);
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
		var aom = api.getAom().getNonZeroVariableMappings().stream().sorted((m1, m2) -> m1.getProject().getName().compareTo(m2.getProject().getName())).findFirst().get();
		api.getAom().getGTRule().apply(aom.getMatch());
		var pcm = api.getProjectCost().getNonZeroVariableMappings().stream().filter(pc -> pc.getProject().getName().equals(aom.getProject().getName())).findFirst().get();
		api.getProjectCost().getGTRule().apply(pcm.getMatch());
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
