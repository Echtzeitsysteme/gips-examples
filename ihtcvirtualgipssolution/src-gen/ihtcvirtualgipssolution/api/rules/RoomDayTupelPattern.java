package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.RoomDayTupelMatch;
import ihtcvirtualmetamodel.Day;
import ihtcvirtualmetamodel.Room;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@76b236d (name: d), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@1cba5c59 (name: r)] which does the following:
 * Used in Softconstraint S1 to check the assigned age groups for each room on each day
 */
@SuppressWarnings("unused")
public class RoomDayTupelPattern extends GraphTransformationPattern<RoomDayTupelMatch, RoomDayTupelPattern> {
	private static String patternName = "roomDayTupel";

	/**
	 * Creates a new pattern roomDayTupel().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public RoomDayTupelPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public RoomDayTupelMatch convertMatch(final IMatch match) {
		return new RoomDayTupelMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("d");
		names.add("r");
		return names;
	}

	/**
	 * Binds the node d to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomDayTupelPattern bindD(final Day object) {
		parameters.put("d", Objects.requireNonNull(object, "d must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node d to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomDayTupelPattern unbindD() {
		parameters.remove("d");
		return this;
	}

	/**
	 * Binds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomDayTupelPattern bindR(final Room object) {
		parameters.put("r", Objects.requireNonNull(object, "r must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomDayTupelPattern unbindR() {
		parameters.remove("r");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	d --> " + parameters.get("d") + java.lang.System.lineSeparator();
		s += "	r --> " + parameters.get("r") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
