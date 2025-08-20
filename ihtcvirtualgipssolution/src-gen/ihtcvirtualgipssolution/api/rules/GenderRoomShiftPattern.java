package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.GenderRoomShiftMatch;
import ihtcvirtualmetamodel.Gender;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@47a802d4 (name: g), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@226b1bac (name: r), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@21e52f97 (name: s)] which does the following:
 * Pattern used for a mapping to save the information which genders are present in a specific room for each shiftThis information is only needed for each morning shift
 */
@SuppressWarnings("unused")
public class GenderRoomShiftPattern extends GraphTransformationPattern<GenderRoomShiftMatch, GenderRoomShiftPattern> {
	private static String patternName = "genderRoomShift";

	/**
	 * Creates a new pattern genderRoomShift().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public GenderRoomShiftPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public GenderRoomShiftMatch convertMatch(final IMatch match) {
		return new GenderRoomShiftMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("g");
		names.add("r");
		names.add("s");
		return names;
	}

	/**
	 * Binds the node g to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public GenderRoomShiftPattern bindG(final Gender object) {
		parameters.put("g", Objects.requireNonNull(object, "g must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node g to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public GenderRoomShiftPattern unbindG() {
		parameters.remove("g");
		return this;
	}

	/**
	 * Binds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public GenderRoomShiftPattern bindR(final Room object) {
		parameters.put("r", Objects.requireNonNull(object, "r must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public GenderRoomShiftPattern unbindR() {
		parameters.remove("r");
		return this;
	}

	/**
	 * Binds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public GenderRoomShiftPattern bindS(final Shift object) {
		parameters.put("s", Objects.requireNonNull(object, "s must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public GenderRoomShiftPattern unbindS() {
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
		s += "	g --> " + parameters.get("g") + java.lang.System.lineSeparator();
		s += "	r --> " + parameters.get("r") + java.lang.System.lineSeparator();
		s += "	s --> " + parameters.get("s") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
