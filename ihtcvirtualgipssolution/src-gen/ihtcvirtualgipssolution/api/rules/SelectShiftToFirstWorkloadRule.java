package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.SelectShiftToFirstWorkloadMatch;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.Workload;
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
 * The rule <code>selectShiftToFirstWorkload()</code> which does the following:
 * GTRule that selects a VirtualShiftToWorkload-Node to be adopted in the final model.
 */
@SuppressWarnings("unused")
public class SelectShiftToFirstWorkloadRule extends GraphTransformationRule<SelectShiftToFirstWorkloadMatch, SelectShiftToFirstWorkloadRule> {
	private static String patternName = "selectShiftToFirstWorkload";

	/**
	 * Creates a new rule selectShiftToFirstWorkload().
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

	public SelectShiftToFirstWorkloadRule(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName, Optional.empty());
	}

	@Override
	public SelectShiftToFirstWorkloadMatch convertMatch(final IMatch match) {
		return new SelectShiftToFirstWorkloadMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("p");
		names.add("vsw");
		names.add("vwc");
		names.add("w");
		return names;
	}

	/**
	 * Binds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule bindP(final Patient object) {
		parameters.put("p", Objects.requireNonNull(object, "p must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule unbindP() {
		parameters.remove("p");
		return this;
	}

	/**
	 * Binds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule bindVsw(final VirtualShiftToWorkload object) {
		parameters.put("vsw", Objects.requireNonNull(object, "vsw must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule unbindVsw() {
		parameters.remove("vsw");
		return this;
	}

	/**
	 * Binds the node vwc to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule bindVwc(final VirtualWorkloadToCapacity object) {
		parameters.put("vwc", Objects.requireNonNull(object, "vwc must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vwc to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule unbindVwc() {
		parameters.remove("vwc");
		return this;
	}

	/**
	 * Binds the node w to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule bindW(final Workload object) {
		parameters.put("w", Objects.requireNonNull(object, "w must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node w to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectShiftToFirstWorkloadRule unbindW() {
		parameters.remove("w");
		return this;
	}
	@Override
	public String toString() {
		String s = "rule " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	p --> " + parameters.get("p") + java.lang.System.lineSeparator();
		s += "	vsw --> " + parameters.get("vsw") + java.lang.System.lineSeparator();
		s += "	vwc --> " + parameters.get("vwc") + java.lang.System.lineSeparator();
		s += "	w --> " + parameters.get("w") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
