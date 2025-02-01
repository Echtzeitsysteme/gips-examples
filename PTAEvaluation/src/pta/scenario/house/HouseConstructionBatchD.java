package pta.scenario.house;

import PTAConstraintConfigD.api.gips.PTAConstraintConfigDGipsAPI;

public class HouseConstructionBatchD extends HouseConstructionGeneric<PTAConstraintConfigDGipsAPI>{

	final public static String TYPE = "BATCH-D"; 
	
	public HouseConstructionBatchD(String name) {
		super(name);
	}

	@Override
	public PTAConstraintConfigDGipsAPI newAPI() {
		return new PTAConstraintConfigDGipsAPI();
	}
	
	@Override
	public void executeGT() {
		api.getAom().applyNonZeroMappings(false);
		api.getProjectCost().applyNonZeroMappings(false);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public String getGipsModelPath() {
		return "PTAConstraintConfigD/api/gips/gips-model.xmi";
	}

	@Override
	public String getIbexModelPath() {
		return "PTAConstraintConfigD/api/ibex-patterns.xmi";
	}

	@Override
	public String getHiPEModelPath() {
		return "PTAConstraintConfigD/hipe/engine/hipe-network.xmi";
	}
	
	@Override
	public String getHiPEEngineFQN() {
		return "PTAConstraintConfigD.hipe.engine.HiPEEngine";
	}
}
