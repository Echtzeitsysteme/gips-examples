package pta.scenario.house;

import PTAConstraintConfigB.api.gips.PTAConstraintConfigBGipsAPI;

public class HouseConstructionBatchBTest extends HouseConstructionBatchGenericTest<PTAConstraintConfigBGipsAPI>{
	
	public HouseConstructionBatchBTest(String name) {
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

}
