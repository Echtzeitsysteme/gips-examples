package pta.scenario.house;

import PTAConstraintConfigC.api.gips.PTAConstraintConfigCGipsAPI;

public class HouseConstructionBatchC extends HouseConstructionGeneric<PTAConstraintConfigCGipsAPI> {

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
		api.getAom().applyNonZeroMappings(false);
		api.getProjectCost().applyNonZeroMappings(false);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getGipsModelPath() {
		return "PTAConstraintConfigC/api/gips/gips-model.xmi";
	}

	@Override
	public String getIbexModelPath() {
		return "PTAConstraintConfigC/api/ibex-patterns.xmi";
	}

	@Override
	public String getHiPEModelPath() {
		return "PTAConstraintConfigC/hipe/engine/hipe-network.xmi";
	}

	@Override
	public String getHiPEEngineFQN() {
		return "PTAConstraintConfigC.hipe.engine.HiPEEngine";
	}
}
