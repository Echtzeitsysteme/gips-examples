package pta.scenario.house;

import PTAConstraintConfigE.api.gips.PTAConstraintConfigEGipsAPI;

public class HouseConstructionBatchE extends HouseConstructionBatchGeneric<PTAConstraintConfigEGipsAPI>{

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
}
