package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.NursetoWorkloadMatch;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@5acf9d5a (name: s), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@122e8254 (name: vsr), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@31eebb11 (name: vsw)] which does the following:
 * Used in a mapping to calculate which nurse is assigned to which workload.
 */
@SuppressWarnings("unused")
public class NursetoWorkloadPattern extends GraphTransformationPattern<NursetoWorkloadMatch, NursetoWorkloadPattern> {
	private static String patternName = "nursetoWorkload";

	/**
	 * Creates a new pattern nursetoWorkload().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public NursetoWorkloadPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public NursetoWorkloadMatch convertMatch(final IMatch match) {
		return new NursetoWorkloadMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("s");
		names.add("vsr");
		names.add("vsw");
		return names;
	}

	/**
	 * Binds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursetoWorkloadPattern bindS(final Shift object) {
		parameters.put("s", Objects.requireNonNull(object, "s must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursetoWorkloadPattern unbindS() {
		parameters.remove("s");
		return this;
	}

	/**
	 * Binds the node vsr to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursetoWorkloadPattern bindVsr(final VirtualShiftToRoster object) {
		parameters.put("vsr", Objects.requireNonNull(object, "vsr must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vsr to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursetoWorkloadPattern unbindVsr() {
		parameters.remove("vsr");
		return this;
	}

	/**
	 * Binds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursetoWorkloadPattern bindVsw(final VirtualShiftToWorkload object) {
		parameters.put("vsw", Objects.requireNonNull(object, "vsw must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node vsw to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursetoWorkloadPattern unbindVsw() {
		parameters.remove("vsw");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	s --> " + parameters.get("s") + java.lang.System.lineSeparator();
		s += "	vsr --> " + parameters.get("vsr") + java.lang.System.lineSeparator();
		s += "	vsw --> " + parameters.get("vsw") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
