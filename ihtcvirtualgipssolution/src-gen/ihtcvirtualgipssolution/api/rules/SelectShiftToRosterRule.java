package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.SelectShiftToRosterMatch;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
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
 * The rule <code>selectShiftToRoster()</code> which does the following:
 * Rule that selects an VirtualShiftToRoster-Node to be adopted in the final model.
 */
@SuppressWarnings("unused")
public class SelectShiftToRosterRule extends GraphTransformationRule<SelectShiftToRosterMatch, SelectShiftToRosterRule> {
	private static String patternName = "selectShiftToRoster";

	/**
	 * Creates a new rule selectShiftToRoster().
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

	public SelectShiftToRosterRule(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName, Optional.empty());
	}

	@Override
	public SelectShiftToRosterMatch convertMatch(final IMatch match) {
		return new SelectShiftToRosterMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("vsr");
		return names;
	}

	/**
	 * Binds the node vsr to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToRosterRule bindVsr(final VirtualShiftToRoster object) {
		parameters.put("vsr", Objects.requireNonNull(object, "vsr must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vsr to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToRosterRule unbindVsr() {
		parameters.remove("vsr");
		return this;
	}
	@Override
	public String toString() {
		String s = "rule " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	vsr --> " + parameters.get("vsr") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
