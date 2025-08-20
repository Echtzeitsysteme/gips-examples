package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.OtCapacityTupelMatch;
import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.OT;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@60f3322f (name: c), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@3110a6d0 (name: ot)] which does the following:
 * Pattern used in a mapping to save the information if an OT is used on a specific Day and used to make sure the Capacity of an OT is not exceeded on each dayc.maxCapacity > 0 is already enforced by the preprocessing because Capacity Objects with maxCapacity <= 0 are not created
 */
@SuppressWarnings("unused")
public class OtCapacityTupelPattern extends GraphTransformationPattern<OtCapacityTupelMatch, OtCapacityTupelPattern> {
	private static String patternName = "otCapacityTupel";

	/**
	 * Creates a new pattern otCapacityTupel().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public OtCapacityTupelPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public OtCapacityTupelMatch convertMatch(final IMatch match) {
		return new OtCapacityTupelMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("c");
		names.add("ot");
		return names;
	}

	/**
	 * Binds the node c to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public OtCapacityTupelPattern bindC(final Capacity object) {
		parameters.put("c", Objects.requireNonNull(object, "c must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node c to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public OtCapacityTupelPattern unbindC() {
		parameters.remove("c");
		return this;
	}

	/**
	 * Binds the node ot to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public OtCapacityTupelPattern bindOt(final OT object) {
		parameters.put("ot", Objects.requireNonNull(object, "ot must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node ot to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public OtCapacityTupelPattern unbindOt() {
		parameters.remove("ot");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	c --> " + parameters.get("c") + java.lang.System.lineSeparator();
		s += "	ot --> " + parameters.get("ot") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
