package ihtcvirtualgipssolution.hipe.engine;

import akka.actor.ActorRef;
import akka.actor.Props;

import ihtcvirtualgipssolution.hipe.engine.actor.NotificationActor;
import ihtcvirtualgipssolution.hipe.engine.actor.DispatchActor;
import ihtcvirtualgipssolution.hipe.engine.actor.edge.VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference;
import ihtcvirtualgipssolution.hipe.engine.actor.edge.VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference;
import ihtcvirtualgipssolution.hipe.engine.actor.node.Shift_object_SP0;
import ihtcvirtualgipssolution.hipe.engine.actor.node.Patient_object_SP0;
import ihtcvirtualgipssolution.hipe.engine.actor.node.VirtualShiftToWorkload_object_SP0;

import hipe.engine.IHiPEEngine;
import hipe.engine.message.InitGenReferenceActor;

import hipe.generic.actor.GenericObjectActor;
import hipe.generic.actor.GenericReferenceActor;
import hipe.generic.actor.GenericProductionActor;
import hipe.generic.actor.junction.*;

import hipe.network.*;

public class HiPEEngine extends IHiPEEngine{
	
	public HiPEEngine(HiPENetwork network) {
		super(network);
	}
	
	public HiPEEngine() {
		super();
	}
	
	@Override
	public String getClassLocation() {
		return getClass().getProtectionDomain().getCodeSource().getLocation().getPath().toString();
	}
	
	@Override
	public String getPackageName() {
		return getClass().getPackageName();
	}
	
	@Override
	protected ActorRef getDispatchActor() {
		return system.actorOf(
			Props.create(DispatchActor.class, () -> new DispatchActor(name2actor, incUtil)),
			"DispatchActor");
	}
	
	@Override
	protected ActorRef getNotificationActor(boolean cascadingNotifications) {
		return system.actorOf(
			Props.create(NotificationActor.class, () -> new NotificationActor(dispatcher, incUtil, cascadingNotifications)), 
			"NotificationActor");
	}
	
	@Override
	public void createProductionNodes() {
		classes.put("ageGroupsRoomDay_production", GenericProductionActor.class);
		productionNodes2pattern.put("ageGroupsRoomDay_production", "ageGroupsRoomDay");
		classes.put("genderRoomShift_production", GenericProductionActor.class);
		productionNodes2pattern.put("genderRoomShift_production", "genderRoomShift");
		classes.put("mandatoryPatients_production", GenericProductionActor.class);
		productionNodes2pattern.put("mandatoryPatients_production", "mandatoryPatients");
		classes.put("nursePatientTupel_production", GenericProductionActor.class);
		productionNodes2pattern.put("nursePatientTupel_production", "nursePatientTupel");
		classes.put("nurseRosterTupel_production", GenericProductionActor.class);
		productionNodes2pattern.put("nurseRosterTupel_production", "nurseRosterTupel");
		classes.put("nursetoWorkload_production", GenericProductionActor.class);
		productionNodes2pattern.put("nursetoWorkload_production", "nursetoWorkload");
		classes.put("optionalPatients_production", GenericProductionActor.class);
		productionNodes2pattern.put("optionalPatients_production", "optionalPatients");
		classes.put("otCapacityTupel_production", GenericProductionActor.class);
		productionNodes2pattern.put("otCapacityTupel_production", "otCapacityTupel");
		classes.put("patientForRoom_production", GenericProductionActor.class);
		productionNodes2pattern.put("patientForRoom_production", "patientForRoom");
		classes.put("roomDayTupel_production", GenericProductionActor.class);
		productionNodes2pattern.put("roomDayTupel_production", "roomDayTupel");
		classes.put("roomForShift_production", GenericProductionActor.class);
		productionNodes2pattern.put("roomForShift_production", "roomForShift");
		classes.put("selectExtendingShiftToFirstWorkload_production", GenericProductionActor.class);
		productionNodes2pattern.put("selectExtendingShiftToFirstWorkload_production", "selectExtendingShiftToFirstWorkload");
		classes.put("selectOperationDay_production", GenericProductionActor.class);
		productionNodes2pattern.put("selectOperationDay_production", "selectOperationDay");
		classes.put("selectShiftToFirstWorkload_production", GenericProductionActor.class);
		productionNodes2pattern.put("selectShiftToFirstWorkload_production", "selectShiftToFirstWorkload");
		classes.put("selectShiftToRoster_production", GenericProductionActor.class);
		productionNodes2pattern.put("selectShiftToRoster_production", "selectShiftToRoster");
		classes.put("surgeonOTForDay_production", GenericProductionActor.class);
		productionNodes2pattern.put("surgeonOTForDay_production", "surgeonOTForDay");
		classes.put("surgeonOptimeTupel_production", GenericProductionActor.class);
		productionNodes2pattern.put("surgeonOptimeTupel_production", "surgeonOptimeTupel");
		classes.put("virtualNodesForOccupant_production", GenericProductionActor.class);
		productionNodes2pattern.put("virtualNodesForOccupant_production", "virtualNodesForOccupant");
		
	}
	
	@Override
	public void createJunctionNodes() {
		classes.put("ageGroupsRoomDay_3_junction", GenericJunctionActor.class);
		classes.put("ageGroupsRoomDay_1_junction", GenericJunctionActor.class);
		classes.put("genderRoomShift_6_junction", GenericJunctionActor.class);
		classes.put("nursePatientTupel_12_junction", GenericJunctionActor.class);
		classes.put("nursetoWorkload_18_junction", GenericJunctionActor.class);
		classes.put("patientForRoom_29_junction", GenericJunctionActor.class);
		classes.put("patientForRoom_30_junction", GenericJunctionActor.class);
		classes.put("patientForRoom_28_junction", GenericJunctionActor.class);
		classes.put("roomDayTupel_40_junction", GenericJunctionActor.class);
		classes.put("selectExtendingShiftToFirstWorkload_46_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_54_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_triangle_0_triangleJunction", GenericTriangleJunctionActor.class);
		classes.put("selectOperationDay_52_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_64_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_56_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_65_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_66_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_57_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_53_junction", GenericJunctionActor.class);
		classes.put("selectOperationDay_51_junction", GenericJunctionActor.class);
		classes.put("selectShiftToFirstWorkload_triangle_0_triangleJunction", GenericTriangleJunctionActor.class);
		classes.put("selectShiftToFirstWorkload_81_junction", GenericJunctionActor.class);
		classes.put("surgeonOTForDay_92_junction", GenericJunctionActor.class);
	}
	
	@Override
	public void createReferenceNodes() {
		classes.put("Room_shifts_0_reference",Room_shifts_0_reference.class);
		classes.put("Nurse_rosters_0_reference",Nurse_rosters_0_reference.class);
		classes.put("VirtualShiftToRoster_shift_0_reference",VirtualShiftToRoster_shift_0_reference.class);
		classes.put("Shift_virtualWorkload_0_reference",Shift_virtualWorkload_0_reference.class);
		classes.put("OT_capacities_0_reference",OT_capacities_0_reference.class);
		classes.put("VirtualShiftToWorkload_shift_0_reference",VirtualShiftToWorkload_shift_0_reference.class);
		classes.put("Room_shifts_1_reference",Room_shifts_1_reference.class);
		classes.put("VirtualShiftToWorkload_workload_0_reference",VirtualShiftToWorkload_workload_0_reference.class);
		classes.put("Patient_workloads_0_reference",Patient_workloads_0_reference.class);
		classes.put("VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference",VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference.class);
		classes.put("VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference",VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference.class);
		classes.put("Patient_firstWorkload_0_reference",Patient_firstWorkload_0_reference.class);
		classes.put("Patient_surgeon_0_reference",Patient_surgeon_0_reference.class);
		classes.put("VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_0_reference",VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_0_reference.class);
		classes.put("Workload_virtualCapacity_0_reference",Workload_virtualCapacity_0_reference.class);
		classes.put("Workload_virtualOpTime_0_reference",Workload_virtualOpTime_0_reference.class);
		classes.put("Surgeon_opTimes_0_reference",Surgeon_opTimes_0_reference.class);
		classes.put("VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_0_reference",VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_0_reference.class);
		classes.put("VirtualWorkloadToOpTime_opTime_0_reference",VirtualWorkloadToOpTime_opTime_0_reference.class);
		classes.put("VirtualWorkloadToCapacity_capacity_0_reference",VirtualWorkloadToCapacity_capacity_0_reference.class);
		classes.put("VirtualOpTimeToCapacity_capacity_0_reference",VirtualOpTimeToCapacity_capacity_0_reference.class);
		classes.put("VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_0_reference",VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_0_reference.class);
		classes.put("VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_0_reference",VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_0_reference.class);
		classes.put("VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_0_reference",VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_0_reference.class);
		classes.put("Workload_virtualShift_0_reference",Workload_virtualShift_0_reference.class);
		
	}
	
	@Override
	public void createObjectNodes() {
		classes.put("AgeGroup_object",AgeGroup_object.class);
		classes.put("Day_object",Day_object.class);
		classes.put("Room_object",Room_object.class);
		classes.put("Gender_object",Gender_object.class);
		classes.put("Nurse_object",Nurse_object.class);
		classes.put("Roster_object",Roster_object.class);
		classes.put("VirtualShiftToRoster_object",VirtualShiftToRoster_object.class);
		classes.put("OT_object",OT_object.class);
		classes.put("Capacity_object",Capacity_object.class);
		classes.put("Surgeon_object",Surgeon_object.class);
		classes.put("OpTime_object",OpTime_object.class);
		classes.put("VirtualOpTimeToCapacity_object",VirtualOpTimeToCapacity_object.class);
		classes.put("Shift_object_SP0",Shift_object_SP0.class);
		classes.put("Shift_object_SP1",Shift_object_SP1.class);
		classes.put("Patient_object_SP0",Patient_object_SP0.class);
		classes.put("Patient_object_SP1",Patient_object_SP1.class);
		classes.put("VirtualShiftToWorkload_object_SP0",VirtualShiftToWorkload_object_SP0.class);
		classes.put("VirtualShiftToWorkload_object_SP1",VirtualShiftToWorkload_object_SP1.class);
		classes.put("VirtualShiftToWorkload_object_SP2",VirtualShiftToWorkload_object_SP2.class);
		classes.put("Workload_object_SP0",Workload_object_SP0.class);
		classes.put("Workload_object_SP1",Workload_object_SP1.class);
		classes.put("VirtualWorkloadToOpTime_object_SP0",VirtualWorkloadToOpTime_object_SP0.class);
		classes.put("VirtualWorkloadToOpTime_object_SP1",VirtualWorkloadToOpTime_object_SP1.class);
		classes.put("VirtualWorkloadToCapacity_object_SP0",VirtualWorkloadToCapacity_object_SP0.class);
		classes.put("VirtualWorkloadToCapacity_object_SP1",VirtualWorkloadToCapacity_object_SP1.class);
		
	}
	
	@Override
	public void initializeReferenceNodes() {
		name2initRefGen.put("Room_shifts_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Room,ihtcvirtualmetamodel.Shift>(name2actor, name2node.get("Room_shifts_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Room, null, (o) -> o.getShifts(), false, prodUtil, incUtil));
		name2initRefGen.put("Nurse_rosters_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Nurse,ihtcvirtualmetamodel.Roster>(name2actor, name2node.get("Nurse_rosters_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Nurse, null, (o) -> o.getRosters(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualShiftToRoster_shift_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualShiftToRoster,ihtcvirtualmetamodel.Shift>(name2actor, name2node.get("VirtualShiftToRoster_shift_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualShiftToRoster, (o) -> o.getShift(), null, false, prodUtil, incUtil));
		name2initRefGen.put("Shift_virtualWorkload_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Shift,ihtcvirtualmetamodel.VirtualShiftToWorkload>(name2actor, name2node.get("Shift_virtualWorkload_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Shift, null, (o) -> o.getVirtualWorkload(), false, prodUtil, incUtil));
		name2initRefGen.put("OT_capacities_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.OT,ihtcvirtualmetamodel.Capacity>(name2actor, name2node.get("OT_capacities_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.OT, null, (o) -> o.getCapacities(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualShiftToWorkload_shift_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload,ihtcvirtualmetamodel.Shift>(name2actor, name2node.get("VirtualShiftToWorkload_shift_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload, (o) -> o.getShift(), null, false, prodUtil, incUtil));
		name2initRefGen.put("Room_shifts_1_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Room,ihtcvirtualmetamodel.Shift>(name2actor, name2node.get("Room_shifts_1_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Room, null, (o) -> o.getShifts(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualShiftToWorkload_workload_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload,ihtcvirtualmetamodel.Workload>(name2actor, name2node.get("VirtualShiftToWorkload_workload_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload, (o) -> o.getWorkload(), null, false, prodUtil, incUtil));
		name2initRefGen.put("Patient_workloads_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Patient,ihtcvirtualmetamodel.Workload>(name2actor, name2node.get("Patient_workloads_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Patient, null, (o) -> o.getWorkloads(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload,ihtcvirtualmetamodel.VirtualShiftToWorkload>(name2actor, name2node.get("VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload, (o) -> o.getEnables_virtualShiftToWorkload(), null, true, prodUtil, incUtil));
		name2initRefGen.put("VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload,ihtcvirtualmetamodel.VirtualShiftToWorkload>(name2actor, name2node.get("VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload, (o) -> o.getRequires_virtualShiftToWorkload(), null, true, prodUtil, incUtil));
		name2initRefGen.put("Patient_firstWorkload_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Patient,ihtcvirtualmetamodel.Workload>(name2actor, name2node.get("Patient_firstWorkload_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Patient, (o) -> o.getFirstWorkload(), null, false, prodUtil, incUtil));
		name2initRefGen.put("Patient_surgeon_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Patient,ihtcvirtualmetamodel.Surgeon>(name2actor, name2node.get("Patient_surgeon_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Patient, (o) -> o.getSurgeon(), null, false, prodUtil, incUtil));
		name2initRefGen.put("VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime,ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(name2actor, name2node.get("VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualWorkloadToOpTime, null, (o) -> o.getEnables_virtual_WorkloadToCapacity(), false, prodUtil, incUtil));
		name2initRefGen.put("Workload_virtualCapacity_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Workload,ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(name2actor, name2node.get("Workload_virtualCapacity_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Workload, null, (o) -> o.getVirtualCapacity(), false, prodUtil, incUtil));
		name2initRefGen.put("Workload_virtualOpTime_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Workload,ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(name2actor, name2node.get("Workload_virtualOpTime_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Workload, null, (o) -> o.getVirtualOpTime(), false, prodUtil, incUtil));
		name2initRefGen.put("Surgeon_opTimes_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Surgeon,ihtcvirtualmetamodel.OpTime>(name2actor, name2node.get("Surgeon_opTimes_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Surgeon, null, (o) -> o.getOpTimes(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualOpTimeToCapacity,ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(name2actor, name2node.get("VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualOpTimeToCapacity, null, (o) -> o.getEnables_virtualWorkloadToOpTime(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualWorkloadToOpTime_opTime_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime,ihtcvirtualmetamodel.OpTime>(name2actor, name2node.get("VirtualWorkloadToOpTime_opTime_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualWorkloadToOpTime, (o) -> o.getOpTime(), null, false, prodUtil, incUtil));
		name2initRefGen.put("VirtualWorkloadToCapacity_capacity_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToCapacity,ihtcvirtualmetamodel.Capacity>(name2actor, name2node.get("VirtualWorkloadToCapacity_capacity_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualWorkloadToCapacity, (o) -> o.getCapacity(), null, false, prodUtil, incUtil));
		name2initRefGen.put("VirtualOpTimeToCapacity_capacity_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualOpTimeToCapacity,ihtcvirtualmetamodel.Capacity>(name2actor, name2node.get("VirtualOpTimeToCapacity_capacity_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualOpTimeToCapacity, (o) -> o.getCapacity(), null, false, prodUtil, incUtil));
		name2initRefGen.put("VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime,ihtcvirtualmetamodel.VirtualOpTimeToCapacity>(name2actor, name2node.get("VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualWorkloadToOpTime, null, (o) -> o.getRequires_virtualOpTimeToCapacity(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToCapacity,ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(name2actor, name2node.get("VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualWorkloadToCapacity, null, (o) -> o.getRequires_virtualWorkloadToOpTime(), false, prodUtil, incUtil));
		name2initRefGen.put("VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload,ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(name2actor, name2node.get("VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload, null, (o) -> o.getRequires_virtualWorkloadToCapacity(), false, prodUtil, incUtil));
		name2initRefGen.put("Workload_virtualShift_0_reference", new InitGenReferenceActor<ihtcvirtualmetamodel.Workload,ihtcvirtualmetamodel.VirtualShiftToWorkload>(name2actor, name2node.get("Workload_virtualShift_0_reference"), (o) -> o instanceof ihtcvirtualmetamodel.Workload, null, (o) -> o.getVirtualShift(), false, prodUtil, incUtil));
	}
}

class AgeGroup_object extends GenericObjectActor<ihtcvirtualmetamodel.AgeGroup> { }
class Day_object extends GenericObjectActor<ihtcvirtualmetamodel.Day> { }
class Room_object extends GenericObjectActor<ihtcvirtualmetamodel.Room> { }
class Gender_object extends GenericObjectActor<ihtcvirtualmetamodel.Gender> { }
class Nurse_object extends GenericObjectActor<ihtcvirtualmetamodel.Nurse> { }
class Roster_object extends GenericObjectActor<ihtcvirtualmetamodel.Roster> { }
class VirtualShiftToRoster_object extends GenericObjectActor<ihtcvirtualmetamodel.VirtualShiftToRoster> { }
class OT_object extends GenericObjectActor<ihtcvirtualmetamodel.OT> { }
class Capacity_object extends GenericObjectActor<ihtcvirtualmetamodel.Capacity> { }
class Surgeon_object extends GenericObjectActor<ihtcvirtualmetamodel.Surgeon> { }
class OpTime_object extends GenericObjectActor<ihtcvirtualmetamodel.OpTime> { }
class VirtualOpTimeToCapacity_object extends GenericObjectActor<ihtcvirtualmetamodel.VirtualOpTimeToCapacity> { }
class Shift_object_SP1 extends GenericObjectActor<ihtcvirtualmetamodel.Shift> { }
class Patient_object_SP1 extends GenericObjectActor<ihtcvirtualmetamodel.Patient> { }
class VirtualShiftToWorkload_object_SP1 extends GenericObjectActor<ihtcvirtualmetamodel.VirtualShiftToWorkload> { }
class VirtualShiftToWorkload_object_SP2 extends GenericObjectActor<ihtcvirtualmetamodel.VirtualShiftToWorkload> { }
class Workload_object_SP0 extends GenericObjectActor<ihtcvirtualmetamodel.Workload> { }
class Workload_object_SP1 extends GenericObjectActor<ihtcvirtualmetamodel.Workload> { }
class VirtualWorkloadToOpTime_object_SP0 extends GenericObjectActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime> { }
class VirtualWorkloadToOpTime_object_SP1 extends GenericObjectActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime> { }
class VirtualWorkloadToCapacity_object_SP0 extends GenericObjectActor<ihtcvirtualmetamodel.VirtualWorkloadToCapacity> { }
class VirtualWorkloadToCapacity_object_SP1 extends GenericObjectActor<ihtcvirtualmetamodel.VirtualWorkloadToCapacity> { }

class Room_shifts_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Room, ihtcvirtualmetamodel.Shift> { }
class Nurse_rosters_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Nurse, ihtcvirtualmetamodel.Roster> { }
class VirtualShiftToRoster_shift_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualShiftToRoster, ihtcvirtualmetamodel.Shift> { }
class Shift_virtualWorkload_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Shift, ihtcvirtualmetamodel.VirtualShiftToWorkload> { }
class OT_capacities_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.OT, ihtcvirtualmetamodel.Capacity> { }
class VirtualShiftToWorkload_shift_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.Shift> { }
class Room_shifts_1_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Room, ihtcvirtualmetamodel.Shift> { }
class VirtualShiftToWorkload_workload_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.Workload> { }
class Patient_workloads_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Workload> { }
class Patient_firstWorkload_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Workload> { }
class Patient_surgeon_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Surgeon> { }
class VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.VirtualWorkloadToCapacity> { }
class Workload_virtualCapacity_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualWorkloadToCapacity> { }
class Workload_virtualOpTime_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualWorkloadToOpTime> { }
class Surgeon_opTimes_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Surgeon, ihtcvirtualmetamodel.OpTime> { }
class VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualOpTimeToCapacity, ihtcvirtualmetamodel.VirtualWorkloadToOpTime> { }
class VirtualWorkloadToOpTime_opTime_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.OpTime> { }
class VirtualWorkloadToCapacity_capacity_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToCapacity, ihtcvirtualmetamodel.Capacity> { }
class VirtualOpTimeToCapacity_capacity_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualOpTimeToCapacity, ihtcvirtualmetamodel.Capacity> { }
class VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.VirtualOpTimeToCapacity> { }
class VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualWorkloadToCapacity, ihtcvirtualmetamodel.VirtualWorkloadToOpTime> { }
class VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.VirtualWorkloadToCapacity> { }
class Workload_virtualShift_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualShiftToWorkload> { }

