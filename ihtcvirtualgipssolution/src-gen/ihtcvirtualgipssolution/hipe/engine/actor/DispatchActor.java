package ihtcvirtualgipssolution.hipe.engine.actor;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EObject;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.*;
import static akka.pattern.Patterns.ask;

import hipe.engine.util.HiPEMultiUtil;
import hipe.engine.util.IncUtil;
import hipe.engine.message.NewInput;
import hipe.engine.message.NoMoreInput;
import hipe.engine.message.input.ObjectAdded;
import hipe.engine.message.input.ObjectDeleted;
import hipe.engine.message.input.ReferenceAdded;
import hipe.engine.message.input.ReferenceDeleted;		
import hipe.engine.message.input.AttributeChanged;
import hipe.engine.message.input.NotificationContainer;

import hipe.generic.actor.junction.util.HiPEConfig;

public class DispatchActor extends AbstractActor {
	
	private int counter = 0;
	public long time = 0;
				
	private Map<String, ActorRef> name2actor;
	
	private Map<Object, Consumer<Object>> type2addConsumer = HiPEMultiUtil.createMap();
	private Map<Object, Consumer<Notification>> feature2setConsumer = HiPEMultiUtil.createMap();
	private Map<Object, Consumer<Notification>> feature2addEdgeConsumer = HiPEMultiUtil.createMap();
	private Map<Object, Consumer<Notification>> feature2removeEdgeConsumer = HiPEMultiUtil.createMap();
	
	private IncUtil incUtil;
	
	private ActorMaterializer materializer;
	
	public DispatchActor(Map<String, ActorRef> name2actor, IncUtil incUtil) {
		this.name2actor = name2actor;
		this.incUtil = incUtil;
		
		initializeAdd();
		initializeSet();
		initializeAddEdge();
		initializeRemoveEdge();
	
		materializer = ActorMaterializer.create(getContext());
	}
	
	private void initializeAdd() {
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getGender(), obj -> {
			ihtcvirtualmetamodel.Gender _gender = (ihtcvirtualmetamodel.Gender) obj;
			incUtil.newMessage();
			name2actor.get("Gender_object").tell(new ObjectAdded<ihtcvirtualmetamodel.Gender>(incUtil, _gender), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getOT(), obj -> {
			ihtcvirtualmetamodel.OT _ot = (ihtcvirtualmetamodel.OT) obj;
			incUtil.newMessage();
			name2actor.get("OT_object").tell(new ObjectAdded<ihtcvirtualmetamodel.OT>(incUtil, _ot), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualOpTimeToCapacity(), obj -> {
			ihtcvirtualmetamodel.VirtualOpTimeToCapacity _virtualoptimetocapacity = (ihtcvirtualmetamodel.VirtualOpTimeToCapacity) obj;
			incUtil.newMessage();
			name2actor.get("VirtualOpTimeToCapacity_object").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualOpTimeToCapacity>(incUtil, _virtualoptimetocapacity), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getDay(), obj -> {
			ihtcvirtualmetamodel.Day _day = (ihtcvirtualmetamodel.Day) obj;
			incUtil.newMessage();
			name2actor.get("Day_object").tell(new ObjectAdded<ihtcvirtualmetamodel.Day>(incUtil, _day), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getRoom(), obj -> {
			ihtcvirtualmetamodel.Room _room = (ihtcvirtualmetamodel.Room) obj;
			incUtil.newMessage();
			name2actor.get("Room_object").tell(new ObjectAdded<ihtcvirtualmetamodel.Room>(incUtil, _room), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getRoster(), obj -> {
			ihtcvirtualmetamodel.Roster _roster = (ihtcvirtualmetamodel.Roster) obj;
			incUtil.newMessage();
			name2actor.get("Roster_object").tell(new ObjectAdded<ihtcvirtualmetamodel.Roster>(incUtil, _roster), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getNurse(), obj -> {
			ihtcvirtualmetamodel.Nurse _nurse = (ihtcvirtualmetamodel.Nurse) obj;
			incUtil.newMessage();
			name2actor.get("Nurse_object").tell(new ObjectAdded<ihtcvirtualmetamodel.Nurse>(incUtil, _nurse), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getCapacity(), obj -> {
			ihtcvirtualmetamodel.Capacity _capacity = (ihtcvirtualmetamodel.Capacity) obj;
			incUtil.newMessage();
			name2actor.get("Capacity_object").tell(new ObjectAdded<ihtcvirtualmetamodel.Capacity>(incUtil, _capacity), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getOpTime(), obj -> {
			ihtcvirtualmetamodel.OpTime _optime = (ihtcvirtualmetamodel.OpTime) obj;
			incUtil.newMessage();
			name2actor.get("OpTime_object").tell(new ObjectAdded<ihtcvirtualmetamodel.OpTime>(incUtil, _optime), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload(), obj -> {
			ihtcvirtualmetamodel.VirtualShiftToWorkload _virtualshifttoworkload = (ihtcvirtualmetamodel.VirtualShiftToWorkload) obj;
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_object_SP0").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, _virtualshifttoworkload), getSelf());
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_object_SP1").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, _virtualshifttoworkload), getSelf());
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_object_SP2").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, _virtualshifttoworkload), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime(), obj -> {
			ihtcvirtualmetamodel.VirtualWorkloadToOpTime _virtualworkloadtooptime = (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) obj;
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_object_SP0").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil, _virtualworkloadtooptime), getSelf());
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_object_SP1").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil, _virtualworkloadtooptime), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToCapacity(), obj -> {
			ihtcvirtualmetamodel.VirtualWorkloadToCapacity _virtualworkloadtocapacity = (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) obj;
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_object_SP0").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil, _virtualworkloadtocapacity), getSelf());
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_object_SP1").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil, _virtualworkloadtocapacity), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToRoster(), obj -> {
			ihtcvirtualmetamodel.VirtualShiftToRoster _virtualshifttoroster = (ihtcvirtualmetamodel.VirtualShiftToRoster) obj;
			incUtil.newMessage();
			name2actor.get("VirtualShiftToRoster_object").tell(new ObjectAdded<ihtcvirtualmetamodel.VirtualShiftToRoster>(incUtil, _virtualshifttoroster), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getAgeGroup(), obj -> {
			ihtcvirtualmetamodel.AgeGroup _agegroup = (ihtcvirtualmetamodel.AgeGroup) obj;
			incUtil.newMessage();
			name2actor.get("AgeGroup_object").tell(new ObjectAdded<ihtcvirtualmetamodel.AgeGroup>(incUtil, _agegroup), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getSurgeon(), obj -> {
			ihtcvirtualmetamodel.Surgeon _surgeon = (ihtcvirtualmetamodel.Surgeon) obj;
			incUtil.newMessage();
			name2actor.get("Surgeon_object").tell(new ObjectAdded<ihtcvirtualmetamodel.Surgeon>(incUtil, _surgeon), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getShift(), obj -> {
			ihtcvirtualmetamodel.Shift _shift = (ihtcvirtualmetamodel.Shift) obj;
			incUtil.newMessage();
			name2actor.get("Shift_object_SP0").tell(new ObjectAdded<ihtcvirtualmetamodel.Shift>(incUtil, _shift), getSelf());
			incUtil.newMessage();
			name2actor.get("Shift_object_SP1").tell(new ObjectAdded<ihtcvirtualmetamodel.Shift>(incUtil, _shift), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload(), obj -> {
			ihtcvirtualmetamodel.Workload _workload = (ihtcvirtualmetamodel.Workload) obj;
			incUtil.newMessage();
			name2actor.get("Workload_object_SP0").tell(new ObjectAdded<ihtcvirtualmetamodel.Workload>(incUtil, _workload), getSelf());
			incUtil.newMessage();
			name2actor.get("Workload_object_SP1").tell(new ObjectAdded<ihtcvirtualmetamodel.Workload>(incUtil, _workload), getSelf());
		});
		type2addConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient(), obj -> {
			ihtcvirtualmetamodel.Patient _patient = (ihtcvirtualmetamodel.Patient) obj;
			incUtil.newMessage();
			name2actor.get("Patient_object_SP0").tell(new ObjectAdded<ihtcvirtualmetamodel.Patient>(incUtil, _patient), getSelf());
			incUtil.newMessage();
			name2actor.get("Patient_object_SP1").tell(new ObjectAdded<ihtcvirtualmetamodel.Patient>(incUtil, _patient), getSelf());
		});
	}
	
	private void initializeSet() {
		feature2setConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_WasImported(), notification -> {
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload) {
				incUtil.newMessage();
				name2actor.get("VirtualShiftToWorkload_object_SP1").tell(new AttributeChanged<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload) {
				incUtil.newMessage();
				name2actor.get("VirtualShiftToWorkload_object_SP0").tell(new AttributeChanged<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload) {
				incUtil.newMessage();
				name2actor.get("VirtualShiftToWorkload_object_SP2").tell(new AttributeChanged<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
		});
		
		feature2setConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getShift_ShiftNo(), notification -> {
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.Shift) {
				incUtil.newMessage();
				name2actor.get("Shift_object_SP1").tell(new AttributeChanged<ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.Shift) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.Shift) {
				incUtil.newMessage();
				name2actor.get("Shift_object_SP0").tell(new AttributeChanged<ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.Shift) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
		});
		
		feature2setConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_IsOccupant(), notification -> {
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.Patient) {
				incUtil.newMessage();
				name2actor.get("Patient_object_SP1").tell(new AttributeChanged<ihtcvirtualmetamodel.Patient>(incUtil, (ihtcvirtualmetamodel.Patient) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.Patient) {
				incUtil.newMessage();
				name2actor.get("Patient_object_SP0").tell(new AttributeChanged<ihtcvirtualmetamodel.Patient>(incUtil, (ihtcvirtualmetamodel.Patient) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
		});
		
		feature2setConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_Mandatory(), notification -> {
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.Patient) {
				incUtil.newMessage();
				name2actor.get("Patient_object_SP1").tell(new AttributeChanged<ihtcvirtualmetamodel.Patient>(incUtil, (ihtcvirtualmetamodel.Patient) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
			if(notification.getNotifier() instanceof ihtcvirtualmetamodel.Patient) {
				incUtil.newMessage();
				name2actor.get("Patient_object_SP0").tell(new AttributeChanged<ihtcvirtualmetamodel.Patient>(incUtil, (ihtcvirtualmetamodel.Patient) notification.getNotifier(), notification.getOldValue()), getSelf());
			}
		});
		
	}
	
	private void initializeAddEdge() {
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Requires_virtualShiftToWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil,(ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_requires_virtualShiftToWorkload_VirtualShiftToWorkload"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload_VirtualShift(), notification -> {
			incUtil.newMessage();
			name2actor.get("Workload_virtualShift_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil,(ihtcvirtualmetamodel.Workload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNewValue(), "ihtcvirtualmetamodel.Workload_virtualShift_VirtualShiftToWorkload"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getNurse_Rosters(), notification -> {
			incUtil.newMessage();
			name2actor.get("Nurse_rosters_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Nurse, ihtcvirtualmetamodel.Roster>(incUtil,(ihtcvirtualmetamodel.Nurse) notification.getNotifier(), (ihtcvirtualmetamodel.Roster) notification.getNewValue(), "ihtcvirtualmetamodel.Nurse_rosters_Roster"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getOT_Capacities(), notification -> {
			incUtil.newMessage();
			name2actor.get("OT_capacities_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.OT, ihtcvirtualmetamodel.Capacity>(incUtil,(ihtcvirtualmetamodel.OT) notification.getNotifier(), (ihtcvirtualmetamodel.Capacity) notification.getNewValue(), "ihtcvirtualmetamodel.OT_capacities_Capacity"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_Surgeon(), notification -> {
			incUtil.newMessage();
			name2actor.get("Patient_surgeon_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Surgeon>(incUtil,(ihtcvirtualmetamodel.Patient) notification.getNotifier(), (ihtcvirtualmetamodel.Surgeon) notification.getNewValue(), "ihtcvirtualmetamodel.Patient_surgeon_Surgeon"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_FirstWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("Patient_firstWorkload_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Workload>(incUtil,(ihtcvirtualmetamodel.Patient) notification.getNotifier(), (ihtcvirtualmetamodel.Workload) notification.getNewValue(), "ihtcvirtualmetamodel.Patient_firstWorkload_Workload"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime_OpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_opTime_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.OpTime>(incUtil,(ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNotifier(), (ihtcvirtualmetamodel.OpTime) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualWorkloadToOpTime_opTime_OpTime"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualOpTimeToCapacity_Enables_virtualWorkloadToOpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualOpTimeToCapacity, ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil,(ihtcvirtualmetamodel.VirtualOpTimeToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_VirtualWorkloadToOpTime"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getSurgeon_OpTimes(), notification -> {
			incUtil.newMessage();
			name2actor.get("Surgeon_opTimes_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Surgeon, ihtcvirtualmetamodel.OpTime>(incUtil,(ihtcvirtualmetamodel.Surgeon) notification.getNotifier(), (ihtcvirtualmetamodel.OpTime) notification.getNewValue(), "ihtcvirtualmetamodel.Surgeon_opTimes_OpTime"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToCapacity_Requires_virtualWorkloadToOpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualWorkloadToCapacity, ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil,(ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_VirtualWorkloadToOpTime"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Requires_virtualWorkloadToCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil,(ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_VirtualWorkloadToCapacity"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Enables_virtualShiftToWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil,(ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_enables_virtualShiftToWorkload_VirtualShiftToWorkload"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Shift(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_shift_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.Shift>(incUtil,(ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_shift_Shift"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload_VirtualCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("Workload_virtualCapacity_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil,(ihtcvirtualmetamodel.Workload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getNewValue(), "ihtcvirtualmetamodel.Workload_virtualCapacity_VirtualWorkloadToCapacity"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload_VirtualOpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("Workload_virtualOpTime_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil,(ihtcvirtualmetamodel.Workload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNewValue(), "ihtcvirtualmetamodel.Workload_virtualOpTime_VirtualWorkloadToOpTime"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getShift_VirtualWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("Shift_virtualWorkload_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Shift, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil,(ihtcvirtualmetamodel.Shift) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNewValue(), "ihtcvirtualmetamodel.Shift_virtualWorkload_VirtualShiftToWorkload"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Workload(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_workload_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.Workload>(incUtil,(ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.Workload) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_workload_Workload"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_Workloads(), notification -> {
			incUtil.newMessage();
			name2actor.get("Patient_workloads_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Workload>(incUtil,(ihtcvirtualmetamodel.Patient) notification.getNotifier(), (ihtcvirtualmetamodel.Workload) notification.getNewValue(), "ihtcvirtualmetamodel.Patient_workloads_Workload"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime_Requires_virtualOpTimeToCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.VirtualOpTimeToCapacity>(incUtil,(ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualOpTimeToCapacity) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_VirtualOpTimeToCapacity"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getRoom_Shifts(), notification -> {
			incUtil.newMessage();
			name2actor.get("Room_shifts_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Room, ihtcvirtualmetamodel.Shift>(incUtil,(ihtcvirtualmetamodel.Room) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getNewValue(), "ihtcvirtualmetamodel.Room_shifts_Shift"), getSelf());
			incUtil.newMessage();
			name2actor.get("Room_shifts_1_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.Room, ihtcvirtualmetamodel.Shift>(incUtil,(ihtcvirtualmetamodel.Room) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getNewValue(), "ihtcvirtualmetamodel.Room_shifts_Shift"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToRoster_Shift(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToRoster_shift_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualShiftToRoster, ihtcvirtualmetamodel.Shift>(incUtil,(ihtcvirtualmetamodel.VirtualShiftToRoster) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualShiftToRoster_shift_Shift"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToCapacity_Capacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_capacity_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualWorkloadToCapacity, ihtcvirtualmetamodel.Capacity>(incUtil,(ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.Capacity) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualWorkloadToCapacity_capacity_Capacity"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualOpTimeToCapacity_Capacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualOpTimeToCapacity_capacity_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualOpTimeToCapacity, ihtcvirtualmetamodel.Capacity>(incUtil,(ihtcvirtualmetamodel.VirtualOpTimeToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.Capacity) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualOpTimeToCapacity_capacity_Capacity"), getSelf());
		});
		feature2addEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime_Enables_virtual_WorkloadToCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_0_reference").tell(new ReferenceAdded<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil,(ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getNewValue(), "ihtcvirtualmetamodel.VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_VirtualWorkloadToCapacity"), getSelf());
		});
	}
	
	private void initializeRemoveEdge() {
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Requires_virtualShiftToWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_requires_virtualShiftToWorkload_VirtualShiftToWorkload"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload_VirtualShift(), notification -> {
			incUtil.newMessage();
			name2actor.get("Workload_virtualShift_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.Workload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getOldValue(), "ihtcvirtualmetamodel.Workload_virtualShift_VirtualShiftToWorkload"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getNurse_Rosters(), notification -> {
			incUtil.newMessage();
			name2actor.get("Nurse_rosters_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Nurse, ihtcvirtualmetamodel.Roster>(incUtil, (ihtcvirtualmetamodel.Nurse) notification.getNotifier(), (ihtcvirtualmetamodel.Roster) notification.getOldValue(), "ihtcvirtualmetamodel.Nurse_rosters_Roster"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getOT_Capacities(), notification -> {
			incUtil.newMessage();
			name2actor.get("OT_capacities_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.OT, ihtcvirtualmetamodel.Capacity>(incUtil, (ihtcvirtualmetamodel.OT) notification.getNotifier(), (ihtcvirtualmetamodel.Capacity) notification.getOldValue(), "ihtcvirtualmetamodel.OT_capacities_Capacity"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_Surgeon(), notification -> {
			incUtil.newMessage();
			name2actor.get("Patient_surgeon_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Surgeon>(incUtil, (ihtcvirtualmetamodel.Patient) notification.getNotifier(), (ihtcvirtualmetamodel.Surgeon) notification.getOldValue(), "ihtcvirtualmetamodel.Patient_surgeon_Surgeon"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_FirstWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("Patient_firstWorkload_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Workload>(incUtil, (ihtcvirtualmetamodel.Patient) notification.getNotifier(), (ihtcvirtualmetamodel.Workload) notification.getOldValue(), "ihtcvirtualmetamodel.Patient_firstWorkload_Workload"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime_OpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_opTime_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.OpTime>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNotifier(), (ihtcvirtualmetamodel.OpTime) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualWorkloadToOpTime_opTime_OpTime"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualOpTimeToCapacity_Enables_virtualWorkloadToOpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualOpTimeToCapacity, ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil, (ihtcvirtualmetamodel.VirtualOpTimeToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualOpTimeToCapacity_enables_virtualWorkloadToOpTime_VirtualWorkloadToOpTime"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getSurgeon_OpTimes(), notification -> {
			incUtil.newMessage();
			name2actor.get("Surgeon_opTimes_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Surgeon, ihtcvirtualmetamodel.OpTime>(incUtil, (ihtcvirtualmetamodel.Surgeon) notification.getNotifier(), (ihtcvirtualmetamodel.OpTime) notification.getOldValue(), "ihtcvirtualmetamodel.Surgeon_opTimes_OpTime"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToCapacity_Requires_virtualWorkloadToOpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualWorkloadToCapacity, ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualWorkloadToCapacity_requires_virtualWorkloadToOpTime_VirtualWorkloadToOpTime"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Requires_virtualWorkloadToCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_requires_virtualWorkloadToCapacity_VirtualWorkloadToCapacity"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Enables_virtualShiftToWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_enables_virtualShiftToWorkload_VirtualShiftToWorkload"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Shift(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_shift_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_shift_Shift"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload_VirtualCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("Workload_virtualCapacity_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil, (ihtcvirtualmetamodel.Workload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getOldValue(), "ihtcvirtualmetamodel.Workload_virtualCapacity_VirtualWorkloadToCapacity"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload_VirtualOpTime(), notification -> {
			incUtil.newMessage();
			name2actor.get("Workload_virtualOpTime_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Workload, ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil, (ihtcvirtualmetamodel.Workload) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getOldValue(), "ihtcvirtualmetamodel.Workload_virtualOpTime_VirtualWorkloadToOpTime"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getShift_VirtualWorkload(), notification -> {
			incUtil.newMessage();
			name2actor.get("Shift_virtualWorkload_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Shift, ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.Shift) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getOldValue(), "ihtcvirtualmetamodel.Shift_virtualWorkload_VirtualShiftToWorkload"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload_Workload(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_workload_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload, ihtcvirtualmetamodel.Workload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) notification.getNotifier(), (ihtcvirtualmetamodel.Workload) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualShiftToWorkload_workload_Workload"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient_Workloads(), notification -> {
			incUtil.newMessage();
			name2actor.get("Patient_workloads_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Patient, ihtcvirtualmetamodel.Workload>(incUtil, (ihtcvirtualmetamodel.Patient) notification.getNotifier(), (ihtcvirtualmetamodel.Workload) notification.getOldValue(), "ihtcvirtualmetamodel.Patient_workloads_Workload"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime_Requires_virtualOpTimeToCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.VirtualOpTimeToCapacity>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualOpTimeToCapacity) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualWorkloadToOpTime_requires_virtualOpTimeToCapacity_VirtualOpTimeToCapacity"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getRoom_Shifts(), notification -> {
			incUtil.newMessage();
			name2actor.get("Room_shifts_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Room, ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.Room) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getOldValue(), "ihtcvirtualmetamodel.Room_shifts_Shift"), getSelf());
			incUtil.newMessage();
			name2actor.get("Room_shifts_1_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.Room, ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.Room) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getOldValue(), "ihtcvirtualmetamodel.Room_shifts_Shift"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToRoster_Shift(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToRoster_shift_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualShiftToRoster, ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToRoster) notification.getNotifier(), (ihtcvirtualmetamodel.Shift) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualShiftToRoster_shift_Shift"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToCapacity_Capacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_capacity_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualWorkloadToCapacity, ihtcvirtualmetamodel.Capacity>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.Capacity) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualWorkloadToCapacity_capacity_Capacity"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualOpTimeToCapacity_Capacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualOpTimeToCapacity_capacity_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualOpTimeToCapacity, ihtcvirtualmetamodel.Capacity>(incUtil, (ihtcvirtualmetamodel.VirtualOpTimeToCapacity) notification.getNotifier(), (ihtcvirtualmetamodel.Capacity) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualOpTimeToCapacity_capacity_Capacity"), getSelf());
		});
		feature2removeEdgeConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime_Enables_virtual_WorkloadToCapacity(), notification -> {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_0_reference").tell(new ReferenceDeleted<ihtcvirtualmetamodel.VirtualWorkloadToOpTime, ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) notification.getNotifier(), (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) notification.getOldValue(), "ihtcvirtualmetamodel.VirtualWorkloadToOpTime_enables_virtual_WorkloadToCapacity_VirtualWorkloadToCapacity"), getSelf());
		});
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
	}

	@Override
	public void postStop() throws Exception {
		if(HiPEConfig.logWorkloadActivated) {
			DecimalFormat df = new DecimalFormat("0.#####");
	        df.setMaximumFractionDigits(5);
			System.err.println("DispatchNode" + ";"  + counter + ";" + df.format((double) time / (double) (1000 * 1000 * 1000)));
		}
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder() //
				.match(NotificationContainer.class, this::handleNotificationContainer)
				.match(NoMoreInput.class, this::sendFinished) //
				.build();
	}

	private void sendFinished(NoMoreInput m) {
		incUtil.allMessagesInserted();
	}
	
	private void handleNotificationContainer(NotificationContainer nc) {
		counter++;
		long tic = System.nanoTime();
		nc.notifications.parallelStream().forEach(this::handleNotification);
		time += System.nanoTime() - tic;
	}
	
	private void handleNotification(Notification notification) {
		switch (notification.getEventType()) {
		case Notification.ADD:
			handleAdd(notification);
			break;
		case Notification.REMOVE:
			handleRemove(notification);
			break;
		case Notification.REMOVING_ADAPTER:
			handleRemoveAdapter(notification);
			break;	
		case Notification.SET:
			handleSet(notification);
			break;
		}
	}

	private void handleAdd(Notification notification) {
		if(notification.getFeature() == null) 
			handleAddedNode(notification.getNewValue());
		else
			handleAddedEdge(notification);
	}

	private void handleAddedNode(Object node) {
		if(node == null) 
			return;
			
		EObject obj = (EObject) node;
		if(type2addConsumer.containsKey(obj.eClass())) {
			type2addConsumer.get(obj.eClass()).accept(node);
		}
	}
	
	private void handleSet(Notification notification) {
		Object feature = notification.getFeature();
		if(feature2setConsumer.containsKey(feature)) {
			feature2setConsumer.get(feature).accept(notification);
		}
	}

	private void handleAddedEdge(Notification notification) {
		//check for self-edges
		if(notification.getNotifier().equals(notification.getNewValue()))
			handleAddedNode(notification.getNewValue());
					
		Object feature = notification.getFeature();
		if(feature2addEdgeConsumer.containsKey(feature)) {
			feature2addEdgeConsumer.get(feature).accept(notification);
		}
	}

	private void handleRemove(Notification notification) {
		Object feature = notification.getFeature();
		if(feature2removeEdgeConsumer.containsKey(feature)) {
			feature2removeEdgeConsumer.get(feature).accept(notification);
		}
	}
	
	private void handleRemoveAdapter(Notification notification) {
		Object node = notification.getNotifier();
		if (node instanceof ihtcvirtualmetamodel.Room) {
			incUtil.newMessage();
			name2actor.get("Room_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.Room>(incUtil, (ihtcvirtualmetamodel.Room) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.AgeGroup) {
			incUtil.newMessage();
			name2actor.get("AgeGroup_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.AgeGroup>(incUtil, (ihtcvirtualmetamodel.AgeGroup) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Day) {
			incUtil.newMessage();
			name2actor.get("Day_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.Day>(incUtil, (ihtcvirtualmetamodel.Day) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Gender) {
			incUtil.newMessage();
			name2actor.get("Gender_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.Gender>(incUtil, (ihtcvirtualmetamodel.Gender) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Nurse) {
			incUtil.newMessage();
			name2actor.get("Nurse_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.Nurse>(incUtil, (ihtcvirtualmetamodel.Nurse) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Roster) {
			incUtil.newMessage();
			name2actor.get("Roster_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.Roster>(incUtil, (ihtcvirtualmetamodel.Roster) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualShiftToRoster) {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToRoster_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualShiftToRoster>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToRoster) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.OT) {
			incUtil.newMessage();
			name2actor.get("OT_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.OT>(incUtil, (ihtcvirtualmetamodel.OT) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Capacity) {
			incUtil.newMessage();
			name2actor.get("Capacity_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.Capacity>(incUtil, (ihtcvirtualmetamodel.Capacity) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Surgeon) {
			incUtil.newMessage();
			name2actor.get("Surgeon_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.Surgeon>(incUtil, (ihtcvirtualmetamodel.Surgeon) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.OpTime) {
			incUtil.newMessage();
			name2actor.get("OpTime_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.OpTime>(incUtil, (ihtcvirtualmetamodel.OpTime) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualOpTimeToCapacity) {
			incUtil.newMessage();
			name2actor.get("VirtualOpTimeToCapacity_object").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualOpTimeToCapacity>(incUtil, (ihtcvirtualmetamodel.VirtualOpTimeToCapacity) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Shift) {
			incUtil.newMessage();
			name2actor.get("Shift_object_SP0").tell(new ObjectDeleted<ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.Shift) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Shift) {
			incUtil.newMessage();
			name2actor.get("Shift_object_SP1").tell(new ObjectDeleted<ihtcvirtualmetamodel.Shift>(incUtil, (ihtcvirtualmetamodel.Shift) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Patient) {
			incUtil.newMessage();
			name2actor.get("Patient_object_SP0").tell(new ObjectDeleted<ihtcvirtualmetamodel.Patient>(incUtil, (ihtcvirtualmetamodel.Patient) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Patient) {
			incUtil.newMessage();
			name2actor.get("Patient_object_SP1").tell(new ObjectDeleted<ihtcvirtualmetamodel.Patient>(incUtil, (ihtcvirtualmetamodel.Patient) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload) {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_object_SP0").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload) {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_object_SP1").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualShiftToWorkload) {
			incUtil.newMessage();
			name2actor.get("VirtualShiftToWorkload_object_SP2").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualShiftToWorkload>(incUtil, (ihtcvirtualmetamodel.VirtualShiftToWorkload) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Workload) {
			incUtil.newMessage();
			name2actor.get("Workload_object_SP0").tell(new ObjectDeleted<ihtcvirtualmetamodel.Workload>(incUtil, (ihtcvirtualmetamodel.Workload) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.Workload) {
			incUtil.newMessage();
			name2actor.get("Workload_object_SP1").tell(new ObjectDeleted<ihtcvirtualmetamodel.Workload>(incUtil, (ihtcvirtualmetamodel.Workload) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualWorkloadToOpTime) {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_object_SP0").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualWorkloadToOpTime) {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToOpTime_object_SP1").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualWorkloadToOpTime>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToOpTime) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualWorkloadToCapacity) {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_object_SP0").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) node), getSelf());
		}
		if (node instanceof ihtcvirtualmetamodel.VirtualWorkloadToCapacity) {
			incUtil.newMessage();
			name2actor.get("VirtualWorkloadToCapacity_object_SP1").tell(new ObjectDeleted<ihtcvirtualmetamodel.VirtualWorkloadToCapacity>(incUtil, (ihtcvirtualmetamodel.VirtualWorkloadToCapacity) node), getSelf());
		}
	}
}

