package ihtcvirtualgipssolution.api.gips.objective;

import org.emoflon.gips.core.milp.model.LinearFunction;
import org.emoflon.gips.core.GipsEngine;
import ihtcvirtualgipssolution.api.gips.objective.CalculateOPenOTs;
import ihtcvirtualgipssolution.api.gips.objective.CalculateOtsPerSurgeon;
import ihtcvirtualgipssolution.api.gips.objective.CalculateNotAssignedOptionalPatients;
import org.emoflon.gips.intermediate.GipsIntermediate.Objective;
import ihtcvirtualgipssolution.api.gips.objective.CalculateNurseWorkload;
import ihtcvirtualgipssolution.api.gips.objective.CalculateWaitTime;
import org.emoflon.gips.core.milp.model.Term;
import ihtcvirtualgipssolution.api.gips.objective.CalculateUnqualifiedNurses;
import ihtcvirtualgipssolution.api.gips.objective.CalculateContinuityOfCare;
import java.util.List;
import org.emoflon.gips.core.milp.model.Constant;
import java.util.LinkedList;
import org.emoflon.gips.core.milp.model.WeightedLinearFunction;
import org.emoflon.gips.core.GipsObjective;
import org.emoflon.gips.core.milp.model.NestedLinearFunction;
import ihtcvirtualmetamodel.Weight;
import ihtcvirtualgipssolution.api.gips.objective.CalculateAgeDifference;

public class IhtcvirtualgipssolutionGipsObjective extends GipsObjective{
	
	protected CalculateAgeDifference calculateAgeDifference;
	protected CalculateContinuityOfCare calculateContinuityOfCare;
	protected CalculateOPenOTs calculateOPenOTs;
	protected CalculateNurseWorkload calculateNurseWorkload;
	protected CalculateUnqualifiedNurses calculateUnqualifiedNurses;
	protected CalculateOtsPerSurgeon calculateOtsPerSurgeon;
	protected CalculateWaitTime calculateWaitTime;
	protected CalculateNotAssignedOptionalPatients calculateNotAssignedOptionalPatients;
	
	public IhtcvirtualgipssolutionGipsObjective(final GipsEngine engine, final Objective objective) {
		super(engine, objective);
	}
	
	@Override
	protected void initLocalObjectives() {
		calculateAgeDifference = (CalculateAgeDifference) engine.getLinearFunctions().get("calculateAgeDifference");
		calculateContinuityOfCare = (CalculateContinuityOfCare) engine.getLinearFunctions().get("calculateContinuityOfCare");
		calculateOPenOTs = (CalculateOPenOTs) engine.getLinearFunctions().get("calculateOPenOTs");
		calculateNurseWorkload = (CalculateNurseWorkload) engine.getLinearFunctions().get("calculateNurseWorkload");
		calculateUnqualifiedNurses = (CalculateUnqualifiedNurses) engine.getLinearFunctions().get("calculateUnqualifiedNurses");
		calculateOtsPerSurgeon = (CalculateOtsPerSurgeon) engine.getLinearFunctions().get("calculateOtsPerSurgeon");
		calculateWaitTime = (CalculateWaitTime) engine.getLinearFunctions().get("calculateWaitTime");
		calculateNotAssignedOptionalPatients = (CalculateNotAssignedOptionalPatients) engine.getLinearFunctions().get("calculateNotAssignedOptionalPatients");
	}
	
	@Override
	protected void buildTerms() {
		
		constantTerms.add(new Constant(0));
		weightedFunctions.add(new WeightedLinearFunction(calculateAgeDifference.getLinearFunctionFunction(), builder_0()));
		weightedFunctions.add(new WeightedLinearFunction(calculateUnqualifiedNurses.getLinearFunctionFunction(), builder_1()));
		weightedFunctions.add(new WeightedLinearFunction(calculateContinuityOfCare.getLinearFunctionFunction(), builder_2()));
		weightedFunctions.add(new WeightedLinearFunction(calculateNurseWorkload.getLinearFunctionFunction(), builder_3()));
		weightedFunctions.add(new WeightedLinearFunction(calculateOPenOTs.getLinearFunctionFunction(), builder_4()));
		weightedFunctions.add(new WeightedLinearFunction(calculateOtsPerSurgeon.getLinearFunctionFunction(), builder_5()));
		weightedFunctions.add(new WeightedLinearFunction(calculateWaitTime.getLinearFunctionFunction(), builder_6()));
		weightedFunctions.add(new WeightedLinearFunction(calculateNotAssignedOptionalPatients.getLinearFunctionFunction(), builder_7()));
		
	}
	
	protected double builder_1() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getRoomNurseSkill())).reduce(0.0, (sum, elt) -> sum + elt));
	}
	protected double builder_4() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getOpenOperatingTheater())).reduce(0.0, (sum, elt) -> sum + elt));
	}
	protected double builder_0() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getRoomMixedAge())).reduce(0.0, (sum, elt) -> sum + elt));
	}
	protected double builder_7() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getUnscheduledOptional())).reduce(0.0, (sum, elt) -> sum + elt));
	}
	protected double builder_6() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getPatientDelay())).reduce(0.0, (sum, elt) -> sum + elt));
	}
	protected double builder_3() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getNurseExcessiveWorkload())).reduce(0.0, (sum, elt) -> sum + elt));
	}
	protected double builder_5() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getSurgeonTransfer())).reduce(0.0, (sum, elt) -> sum + elt));
	}
	protected double builder_2() {
		return (1.0) * (indexer.getObjectsOfType("Weight").parallelStream()
								.map(type -> (Weight) type).map(elt -> (double)(elt.getContinuityOfCare())).reduce(0.0, (sum, elt) -> sum + elt));
	}
}