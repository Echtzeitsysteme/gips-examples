package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.RoomForShiftMatch;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Shift;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@6591db01 (name: r), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@6a56e88a (name: s)] which does the following:
 * Pattern used to map which rooms are occupied on a specific shift.Used for a mapping which counts the Patients over a specific room for a specific day -> enforce that the capacity of a room is not exceededAdditionally used to make sure nurses are not assigned to empty rooms and each non-empty room has an assigned nurse
 */
@SuppressWarnings("unused")
public class RoomForShiftPattern extends GraphTransformationPattern<RoomForShiftMatch, RoomForShiftPattern> {
	private static String patternName = "roomForShift";

	/**
	 * Creates a new pattern roomForShift().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public RoomForShiftPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public RoomForShiftMatch convertMatch(final IMatch match) {
		return new RoomForShiftMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("r");
		names.add("s");
		return names;
	}

	/**
	 * Binds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomForShiftPattern bindR(final Room object) {
		parameters.put("r", Objects.requireNonNull(object, "r must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomForShiftPattern unbindR() {
		parameters.remove("r");
		return this;
	}

	/**
	 * Binds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomForShiftPattern bindS(final Shift object) {
		parameters.put("s", Objects.requireNonNull(object, "s must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public RoomForShiftPattern unbindS() {
		parameters.remove("s");
		return this;
	}
	
	@Override
	public boolean isMatchValid(IMatch match){
		return  
			((Shift) match.get("s")).getShiftNo()%3.0==0.0;				
	}
	@Override
	public boolean containsArithmeticExpressions() {
		return true;
	}
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	r --> " + parameters.get("r") + java.lang.System.lineSeparator();
		s += "	s --> " + parameters.get("s") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
