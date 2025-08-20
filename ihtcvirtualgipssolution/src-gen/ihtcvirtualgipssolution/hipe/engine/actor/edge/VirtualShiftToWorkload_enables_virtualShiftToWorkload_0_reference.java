package ihtcvirtualgipssolution.hipe.engine.actor.edge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import hipe.engine.util.HiPESet;
import hipe.engine.actor.Port;
import hipe.engine.match.EdgeMatch;
import hipe.engine.message.NoMoreInput;
import hipe.engine.message.NewInput;
import hipe.engine.actor.edge.PortEdge;
import hipe.engine.actor.edge.PortEdgeLeft;
import hipe.engine.actor.edge.PortEdgeRight;

import hipe.network.ReferenceNode;

import hipe.generic.actor.GenericReferenceActor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

public class VirtualShiftToWorkload_enables_virtualShiftToWorkload_0_reference extends GenericReferenceActor<ihtcvirtualmetamodel.VirtualShiftToWorkload,ihtcvirtualmetamodel.VirtualShiftToWorkload> {

	@Override
	protected void initializePorts(Map<String, ActorRef> name2actor, ReferenceNode node) {
		ports = new LinkedList<>();
		ports.add(new PortEdgeLeft(node.getPorts().getPort().get(0), getSelf(), name2actor.get("selectExtendingShiftToFirstWorkload_43_junction"), this::check_constraint_4 , 0   ));
	}	

	public boolean check_constraint_4(EdgeMatch edge, int index) {
		ihtcvirtualmetamodel.VirtualShiftToWorkload prevvsw = (ihtcvirtualmetamodel.VirtualShiftToWorkload) edge.source();
		ihtcvirtualmetamodel.VirtualShiftToWorkload nextvsw = (ihtcvirtualmetamodel.VirtualShiftToWorkload) edge.target();
		boolean predicate = !nextvsw.equals(prevvsw);
		edge.setConstraintSatisfied(index, predicate);
		return predicate;
	}
	
}

