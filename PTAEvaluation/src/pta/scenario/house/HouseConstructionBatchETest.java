package pta.scenario.house;

import PTAConstraintConfigE.api.gips.PTAConstraintConfigEGipsAPI;

public class HouseConstructionBatchETest extends HouseConstructionBatchGenericTest<PTAConstraintConfigEGipsAPI>{

	public HouseConstructionBatchETest(String name) {
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


}
