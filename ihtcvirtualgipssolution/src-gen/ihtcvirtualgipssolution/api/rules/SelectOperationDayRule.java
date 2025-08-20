package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.SelectOperationDayMatch;
import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Surgeon;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToOpTime;
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
 * The rule <code>selectOperationDay()</code> which does the following:
 * Rule that selects an operation day for a patient.That includes three edges to be adopted in the final model.I. + II.: between the patient and opTime and between patient and capacity -> Selects a day and OT for the patientIII.: between opTime and Capacity to make sure the predefined surgeon of the patient is working in the correct OT on that day
 */
@SuppressWarnings("unused")
public class SelectOperationDayRule extends GraphTransformationRule<SelectOperationDayMatch, SelectOperationDayRule> {
	private static String patternName = "selectOperationDay";

	/**
	 * Creates a new rule selectOperationDay().
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

	public SelectOperationDayRule(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName, Optional.empty());
	}

	@Override
	public SelectOperationDayMatch convertMatch(final IMatch match) {
		return new SelectOperationDayMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("c");
		names.add("opTime");
		names.add("p");
		names.add("s");
		names.add("vopc");
		names.add("vwc");
		names.add("vwop");
		names.add("w");
		return names;
	}

	/**
	 * Binds the node c to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindC(final Capacity object) {
		parameters.put("c", Objects.requireNonNull(object, "c must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node c to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindC() {
		parameters.remove("c");
		return this;
	}

	/**
	 * Binds the node opTime to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindOpTime(final OpTime object) {
		parameters.put("opTime", Objects.requireNonNull(object, "opTime must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node opTime to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindOpTime() {
		parameters.remove("opTime");
		return this;
	}

	/**
	 * Binds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindP(final Patient object) {
		parameters.put("p", Objects.requireNonNull(object, "p must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindP() {
		parameters.remove("p");
		return this;
	}

	/**
	 * Binds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindS(final Surgeon object) {
		parameters.put("s", Objects.requireNonNull(object, "s must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindS() {
		parameters.remove("s");
		return this;
	}

	/**
	 * Binds the node vopc to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindVopc(final VirtualOpTimeToCapacity object) {
		parameters.put("vopc", Objects.requireNonNull(object, "vopc must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vopc to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindVopc() {
		parameters.remove("vopc");
		return this;
	}

	/**
	 * Binds the node vwc to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindVwc(final VirtualWorkloadToCapacity object) {
		parameters.put("vwc", Objects.requireNonNull(object, "vwc must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vwc to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindVwc() {
		parameters.remove("vwc");
		return this;
	}

	/**
	 * Binds the node vwop to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindVwop(final VirtualWorkloadToOpTime object) {
		parameters.put("vwop", Objects.requireNonNull(object, "vwop must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vwop to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindVwop() {
		parameters.remove("vwop");
		return this;
	}

	/**
	 * Binds the node w to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule bindW(final Workload object) {
		parameters.put("w", Objects.requireNonNull(object, "w must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node w to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SelectOperationDayRule unbindW() {
		parameters.remove("w");
		return this;
	}
	@Override
	public String toString() {
		String s = "rule " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	c --> " + parameters.get("c") + java.lang.System.lineSeparator();
		s += "	opTime --> " + parameters.get("opTime") + java.lang.System.lineSeparator();
		s += "	p --> " + parameters.get("p") + java.lang.System.lineSeparator();
		s += "	s --> " + parameters.get("s") + java.lang.System.lineSeparator();
		s += "	vopc --> " + parameters.get("vopc") + java.lang.System.lineSeparator();
		s += "	vwc --> " + parameters.get("vwc") + java.lang.System.lineSeparator();
		s += "	vwop --> " + parameters.get("vwop") + java.lang.System.lineSeparator();
		s += "	w --> " + parameters.get("w") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
