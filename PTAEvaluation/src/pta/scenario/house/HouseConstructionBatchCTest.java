package pta.scenario.house;

import PTAConstraintConfigC.api.gips.PTAConstraintConfigCGipsAPI;

public class HouseConstructionBatchCTest extends HouseConstructionBatchGenericTest<PTAConstraintConfigCGipsAPI>{

	public HouseConstructionBatchCTest(String name) {
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
}
