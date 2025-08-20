package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.PatientForRoomMatch;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.Workload;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationPattern;
import org.emoflon.ibex.gt.engine.GraphTransformationInterpreter;

/**
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@6780b31b (name: p), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@19cd5691 (name: r), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@291d34a3 (name: s), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@618ecad5 (name: vsw), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@617f87f2 (name: w)] which does the following:
 * Pattern that finds all patients that are assigned to a room -> first and following workloadsAlso holds the information which workload of a patient is assigned to which shift
 */
@SuppressWarnings("unused")
public class PatientForRoomPattern extends GraphTransformationPattern<PatientForRoomMatch, PatientForRoomPattern> {
	private static String patternName = "patientForRoom";

	/**
	 * Creates a new pattern patientForRoom().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public PatientForRoomPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public PatientForRoomMatch convertMatch(final IMatch match) {
		return new PatientForRoomMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("p");
		names.add("r");
		names.add("s");
		names.add("vsw");
		names.add("w");
		return names;
	}

	/**
	 * Binds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern bindP(final Patient object) {
		parameters.put("p", Objects.requireNonNull(object, "p must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern unbindP() {
		parameters.remove("p");
		return this;
	}

	/**
	 * Binds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern bindR(final Room object) {
		parameters.put("r", Objects.requireNonNull(object, "r must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern unbindR() {
		parameters.remove("r");
		return this;
	}

	/**
	 * Binds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern bindS(final Shift object) {
		parameters.put("s", Objects.requireNonNull(object, "s must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern unbindS() {
		parameters.remove("s");
		return this;
	}

	/**
	 * Binds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern bindVsw(final VirtualShiftToWorkload object) {
		parameters.put("vsw", Objects.requireNonNull(object, "vsw must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern unbindVsw() {
		parameters.remove("vsw");
		return this;
	}

	/**
	 * Binds the node w to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern bindW(final Workload object) {
		parameters.put("w", Objects.requireNonNull(object, "w must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node w to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public PatientForRoomPattern unbindW() {
		parameters.remove("w");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	p --> " + parameters.get("p") + java.lang.System.lineSeparator();
		s += "	r --> " + parameters.get("r") + java.lang.System.lineSeparator();
		s += "	s --> " + parameters.get("s") + java.lang.System.lineSeparator();
		s += "	vsw --> " + parameters.get("vsw") + java.lang.System.lineSeparator();
		s += "	w --> " + parameters.get("w") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
