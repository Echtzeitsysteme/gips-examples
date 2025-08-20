package ihtcvirtualgipssolution.hipe.engine.actor;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;

import akka.actor.ActorRef;

import hipe.engine.actor.GenericNotificationActor;
import hipe.engine.util.IncUtil;

public class NotificationActor extends GenericNotificationActor {
	
	public NotificationActor(ActorRef dispatchActor, IncUtil incUtil, boolean cascadingNotifications) {
		super(dispatchActor, incUtil, cascadingNotifications);
	}
	
	@Override
	protected void initializeExploration() {
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getRoot(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Root _root = (ihtcvirtualmetamodel.Root) obj;
			children.addAll(_root.getNurses());
			children.addAll(_root.getRooms());
			children.addAll(_root.getSurgeons());
			children.addAll(_root.getOts());
			if(_root.getWeight() != null)
				children.add(_root.getWeight());
			children.addAll(_root.getPatients());
			children.addAll(_root.getGenders());
			children.addAll(_root.getDays());
			children.addAll(_root.getAgeGroups());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getGender(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getOT(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.OT _ot = (ihtcvirtualmetamodel.OT) obj;
			children.addAll(_ot.getCapacities());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWeight(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getOrdered(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualOpTimeToCapacity(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getDay(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getRoom(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Room _room = (ihtcvirtualmetamodel.Room) obj;
			children.addAll(_room.getShifts());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getRoster(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Roster _roster = (ihtcvirtualmetamodel.Roster) obj;
			children.addAll(_roster.getVirtualShift());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getNurse(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Nurse _nurse = (ihtcvirtualmetamodel.Nurse) obj;
			children.addAll(_nurse.getRosters());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getCapacity(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Capacity _capacity = (ihtcvirtualmetamodel.Capacity) obj;
			children.addAll(_capacity.getVirtualOpTime());
			children.addAll(_capacity.getVirtualWorkload());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getOpTime(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.OpTime _optime = (ihtcvirtualmetamodel.OpTime) obj;
			children.addAll(_optime.getVirtualWorkload());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getNamedElement(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToWorkload(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToOpTime(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualWorkloadToCapacity(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getVirtualShiftToRoster(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getAgeGroup(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getSurgeon(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Surgeon _surgeon = (ihtcvirtualmetamodel.Surgeon) obj;
			children.addAll(_surgeon.getOpTimes());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getShift(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Shift _shift = (ihtcvirtualmetamodel.Shift) obj;
			children.addAll(_shift.getVirtualWorkload());
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getWorkload(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(ihtcvirtualmetamodel.IhtcvirtualmetamodelPackage.eINSTANCE.getPatient(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			ihtcvirtualmetamodel.Patient _patient = (ihtcvirtualmetamodel.Patient) obj;
			children.addAll(_patient.getWorkloads());
			return children;
		});
	}
}

