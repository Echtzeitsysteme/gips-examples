package pta.scenario.house;

import PTAConstraintConfigC.api.gips.PTAConstraintConfigCGipsAPI;

public class HouseConstructionBatchC extends HouseConstructionBatchGeneric<PTAConstraintConfigCGipsAPI>{

	final public static String TYPE = "BATCH-C"; 
	
	public HouseConstructionBatchC(String name) {
		super(name);
	}

	@Override
	public PTAConstraintConfigCGipsAPI newAPI() {
		return new PTAConstraintConfigCGipsAPI();
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
