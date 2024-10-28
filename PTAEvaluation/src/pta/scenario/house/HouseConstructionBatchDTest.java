package pta.scenario.house;

import PTAConstraintConfigD.api.gips.PTAConstraintConfigDGipsAPI;

public class HouseConstructionBatchDTest extends HouseConstructionBatchGenericTest<PTAConstraintConfigDGipsAPI>{

	public HouseConstructionBatchDTest(String name) {
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


}
