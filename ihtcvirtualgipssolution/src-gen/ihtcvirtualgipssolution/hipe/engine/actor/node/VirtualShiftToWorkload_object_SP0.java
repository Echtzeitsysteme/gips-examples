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
		
public class VirtualShiftToWorkload_object_SP0 extends GenericObjectActor<ihtcvirtualmetamodel.VirtualShiftToWorkload> {
	
	@Override
	protected void initializePorts(Map<String, ActorRef> name2actor, ObjectNode node) {
		ports = new LinkedList<>();
		ports.add(new PortNodeRight<ihtcvirtualmetamodel.VirtualShiftToWorkload>(node.getPorts().getPort().get(0), getSelf(), name2actor.get("Shift_virtualWorkload_0_reference"), this::returnTrue   ));
		ports.add(new PortNodeLeft<ihtcvirtualmetamodel.VirtualShiftToWorkload>(node.getPorts().getPort().get(1), getSelf(), name2actor.get("VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference"), this::returnTrue   ));
		ports.add(new PortNodeRight<ihtcvirtualmetamodel.VirtualShiftToWorkload>(node.getPorts().getPort().get(2), getSelf(), name2actor.get("VirtualShiftToWorkload_requires_virtualShiftToWorkload_0_reference"), this::returnTrue   ));
		ports.add(new PortNodeMatch<ihtcvirtualmetamodel.VirtualShiftToWorkload>(node.getPorts().getPort().get(3), getSelf(), name2actor.get("virtualNodesForOccupant_production"), this::check_constraint_6 , 0   , node.getName() ));
	}
	
	public boolean check_constraint_6(ihtcvirtualmetamodel.VirtualShiftToWorkload vsw, int index) {
		return vsw.isWasImported()==true;
	}
	
}


