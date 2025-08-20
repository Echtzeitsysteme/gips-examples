package ihtcvirtualgipssolution.hipe.engine.actor.node;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import akka.actor.ActorRef;

import hipe.engine.actor.Port;
import hipe.engine.actor.node.PortNode;
import hipe.engine.actor.node.PortNodeLeft;
import hipe.engine.actor.node.PortNodeRight;
import hipe.engine.actor.node.PortNodeMatch;
import hipe.engine.actor.node.PortNodeMatchLeft;
import hipe.engine.actor.node.PortNodeMatchRight;

import hipe.network.ObjectNode;

import hipe.generic.actor.GenericObjectActor;

import hipe.generic.actor.junction.util.HiPEConfig;
		
public class Patient_object_SP1 extends GenericObjectActor<ihtcvirtualmetamodel.Patient> {
	
	@Override
	protected void initializePorts(Map<String, ActorRef> name2actor, ObjectNode node) {
		ports = new LinkedList<>();
		ports.add(new PortNodeLeft<ihtcvirtualmetamodel.Patient>(node.getPorts().getPort().get(1), getSelf(), name2actor.get("Patient_firstWorkload_0_reference"), this::returnTrue   ));
		ports.add(new PortNodeMatch<ihtcvirtualmetamodel.Patient>(node.getPorts().getPort().get(0), getSelf(), name2actor.get("optionalPatients_production"), this::check_constraint_3 , 0   , node.getName() ));
	}
	
	public boolean check_constraint_3(ihtcvirtualmetamodel.Patient p, int index) {
		return p.isMandatory()==false && p.isIsOccupant()==false;
	}
	
}


