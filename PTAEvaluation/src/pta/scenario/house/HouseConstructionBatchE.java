package pta.scenario.house;

import PTAConstraintConfigE.api.gips.PTAConstraintConfigEGipsAPI;

public class HouseConstructionBatchE extends HouseConstructionGeneric<PTAConstraintConfigEGipsAPI>{

	final public static String TYPE = "BATCH-E"; 
	
	public HouseConstructionBatchE(String name) {
		super(name);
	}

	@Override
	public PTAConstraintConfigEGipsAPI newAPI() {
		return new PTAConstraintConfigEGipsAPI();
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
		return "PTAConstraintConfigE/api/gips/gips-model.xmi";
	}

	@Override
	public String getIbexModelPath() {
		return "PTAConstraintConfigE/api/ibex-patterns.xmi";
	}

	@Override
	public String getHiPEModelPath() {
		return "PTAConstraintConfigE/hipe/engine/hipe-network.xmi";
	}
	
	@Override
	public String getHiPEEngineFQN() {
		return "PTAConstraintConfigE.hipe.engine.HiPEEngine";
	}
}
