package pta.scenario.house;

import PTAConstraintConfigD.api.gips.PTAConstraintConfigDGipsAPI;

public class HouseConstructionBatchD extends HouseConstructionBatchGeneric<PTAConstraintConfigDGipsAPI>{

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
		api.getAom().applyNonZeroMappings();
		api.getProjectCost().applyNonZeroMappings();
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
