package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.SelectExtendingShiftToFirstWorkloadMatch;
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
 * The rule <code>selectExtendingShiftToFirstWorkload()</code> which does the following:
 * Rule that selects an extending VirtualShiftToWorkload-Node to be adopted in the final model.
 */
@SuppressWarnings("unused")
public class SelectExtendingShiftToFirstWorkloadRule extends GraphTransformationRule<SelectExtendingShiftToFirstWorkloadMatch, SelectExtendingShiftToFirstWorkloadRule> {
	private static String patternName = "selectExtendingShiftToFirstWorkload";

	/**
	 * Creates a new rule selectExtendingShiftToFirstWorkload().
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

	public SelectExtendingShiftToFirstWorkloadRule(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName, Optional.empty());
	}

	@Override
	public SelectExtendingShiftToFirstWorkloadMatch convertMatch(final IMatch match) {
		return new SelectExtendingShiftToFirstWorkloadMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("nextvsw");
		names.add("prevvsw");
		return names;
	}

	/**
	 * Binds the node nextvsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectExtendingShiftToFirstWorkloadRule bindNextvsw(final VirtualShiftToWorkload object) {
		parameters.put("nextvsw", Objects.requireNonNull(object, "nextvsw must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node nextvsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectExtendingShiftToFirstWorkloadRule unbindNextvsw() {
		parameters.remove("nextvsw");
		return this;
	}

	/**
	 * Binds the node prevvsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectExtendingShiftToFirstWorkloadRule bindPrevvsw(final VirtualShiftToWorkload object) {
		parameters.put("prevvsw", Objects.requireNonNull(object, "prevvsw must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node prevvsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectExtendingShiftToFirstWorkloadRule unbindPrevvsw() {
		parameters.remove("prevvsw");
		return this;
	}
	@Override
	public String toString() {
		String s = "rule " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	nextvsw --> " + parameters.get("nextvsw") + java.lang.System.lineSeparator();
		s += "	prevvsw --> " + parameters.get("prevvsw") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
