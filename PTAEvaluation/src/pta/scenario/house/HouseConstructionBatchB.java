package pta.scenario.house;

import PTAConstraintConfigB.api.gips.PTAConstraintConfigBGipsAPI;

public class HouseConstructionBatchB extends HouseConstructionBatchGeneric<PTAConstraintConfigBGipsAPI>{
	
	final public static String TYPE = "BATCH-B"; 
	
	public HouseConstructionBatchB(String name) {
		super(name);
	}

	@Override
	public PTAConstraintConfigBGipsAPI newAPI() {
		return new PTAConstraintConfigBGipsAPI();
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
}
