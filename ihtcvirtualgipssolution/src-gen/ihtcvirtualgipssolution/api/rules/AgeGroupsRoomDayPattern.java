package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.AgeGroupsRoomDayMatch;
import ihtcvirtualmetamodel.AgeGroup;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@6d42ff89 (name: ag), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@2d6bfacb (name: d), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@3ea0f4fd (name: r)] which does the following:
 * Pattern used for a Mapping to calculate what age groups are present in a specific room for all shifts.Additionally the highest and lowest age groups are also saved in variables
 */
@SuppressWarnings("unused")
public class AgeGroupsRoomDayPattern extends GraphTransformationPattern<AgeGroupsRoomDayMatch, AgeGroupsRoomDayPattern> {
	private static String patternName = "ageGroupsRoomDay";

	/**
	 * Creates a new pattern ageGroupsRoomDay().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public AgeGroupsRoomDayPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public AgeGroupsRoomDayMatch convertMatch(final IMatch match) {
		return new AgeGroupsRoomDayMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("ag");
		names.add("d");
		names.add("r");
		return names;
	}

	/**
	 * Binds the node ag to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public AgeGroupsRoomDayPattern bindAg(final AgeGroup object) {
		parameters.put("ag", Objects.requireNonNull(object, "ag must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node ag to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public AgeGroupsRoomDayPattern unbindAg() {
		parameters.remove("ag");
		return this;
	}

	/**
	 * Binds the node d to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public AgeGroupsRoomDayPattern bindD(final Day object) {
		parameters.put("d", Objects.requireNonNull(object, "d must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node d to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public AgeGroupsRoomDayPattern unbindD() {
		parameters.remove("d");
		return this;
	}

	/**
	 * Binds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public AgeGroupsRoomDayPattern bindR(final Room object) {
		parameters.put("r", Objects.requireNonNull(object, "r must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node r to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public AgeGroupsRoomDayPattern unbindR() {
		parameters.remove("r");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	ag --> " + parameters.get("ag") + java.lang.System.lineSeparator();
		s += "	d --> " + parameters.get("d") + java.lang.System.lineSeparator();
		s += "	r --> " + parameters.get("r") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
