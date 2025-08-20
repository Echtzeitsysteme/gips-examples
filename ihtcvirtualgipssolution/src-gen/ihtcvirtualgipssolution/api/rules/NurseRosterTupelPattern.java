package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.NurseRosterTupelMatch;
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.Roster;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@4cfc3afb (name: n), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@20735e49 (name: ro)] which does the following:
 * Used in a mapping to calculate the workload for all nurses for each shift
 */
@SuppressWarnings("unused")
public class NurseRosterTupelPattern extends GraphTransformationPattern<NurseRosterTupelMatch, NurseRosterTupelPattern> {
	private static String patternName = "nurseRosterTupel";

	/**
	 * Creates a new pattern nurseRosterTupel().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public NurseRosterTupelPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public NurseRosterTupelMatch convertMatch(final IMatch match) {
		return new NurseRosterTupelMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("n");
		names.add("ro");
		return names;
	}

	/**
	 * Binds the node n to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NurseRosterTupelPattern bindN(final Nurse object) {
		parameters.put("n", Objects.requireNonNull(object, "n must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node n to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NurseRosterTupelPattern unbindN() {
		parameters.remove("n");
		return this;
	}

	/**
	 * Binds the node ro to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NurseRosterTupelPattern bindRo(final Roster object) {
		parameters.put("ro", Objects.requireNonNull(object, "ro must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node ro to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NurseRosterTupelPattern unbindRo() {
		parameters.remove("ro");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	n --> " + parameters.get("n") + java.lang.System.lineSeparator();
		s += "	ro --> " + parameters.get("ro") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
