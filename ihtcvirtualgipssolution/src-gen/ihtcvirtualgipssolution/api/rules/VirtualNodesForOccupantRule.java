package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.VirtualNodesForOccupantMatch;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationRule;
import org.emoflon.ibex.gt.arithmetic.Probability;
import org.emoflon.ibex.gt.engine.GraphTransformationInterpreter;

/**
 * The rule <code>virtualNodesForOccupant()</code> which does the following:
 * Rule that selects all VirtualShiftToWorkload-Nodes to be adopted in the final model if they were imported (belong to an occupant)
 */
@SuppressWarnings("unused")
public class VirtualNodesForOccupantRule extends GraphTransformationRule<VirtualNodesForOccupantMatch, VirtualNodesForOccupantRule> {
	private static String patternName = "virtualNodesForOccupant";

	/**
	 * Creates a new rule virtualNodesForOccupant().
	 * 
	 * @param api
	 *            the API the rule belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	 
	/**
	 * The probability that the rule will be applied; if the rule has no probability,
	 * then the Optional will be empty
	 */

	public VirtualNodesForOccupantRule(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName, Optional.empty());
	}

	@Override
	public VirtualNodesForOccupantMatch convertMatch(final IMatch match) {
		return new VirtualNodesForOccupantMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("vsw");
		return names;
	}

	/**
	 * Binds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public VirtualNodesForOccupantRule bindVsw(final VirtualShiftToWorkload object) {
		parameters.put("vsw", Objects.requireNonNull(object, "vsw must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public VirtualNodesForOccupantRule unbindVsw() {
		parameters.remove("vsw");
		return this;
	}
	@Override
	public String toString() {
		String s = "rule " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	vsw --> " + parameters.get("vsw") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
