package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.SurgeonOptimeTupelMatch;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Surgeon;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@2e192db3 (name: op), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@3392c9bc (name: s)] which does the following:
 * Pattern used in a mapping to save the information if a Surgeon operates on a specific Day and used to make sure the operation time of a surgeon is not exceeded on each dayop.maxOpTime > 0 is already enforced by the preprocessing because OpTime Objects with maxOpTime <= 0 are not created
 */
@SuppressWarnings("unused")
public class SurgeonOptimeTupelPattern extends GraphTransformationPattern<SurgeonOptimeTupelMatch, SurgeonOptimeTupelPattern> {
	private static String patternName = "surgeonOptimeTupel";

	/**
	 * Creates a new pattern surgeonOptimeTupel().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public SurgeonOptimeTupelPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public SurgeonOptimeTupelMatch convertMatch(final IMatch match) {
		return new SurgeonOptimeTupelMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("op");
		names.add("s");
		return names;
	}

	/**
	 * Binds the node op to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOptimeTupelPattern bindOp(final OpTime object) {
		parameters.put("op", Objects.requireNonNull(object, "op must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node op to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOptimeTupelPattern unbindOp() {
		parameters.remove("op");
		return this;
	}

	/**
	 * Binds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOptimeTupelPattern bindS(final Surgeon object) {
		parameters.put("s", Objects.requireNonNull(object, "s must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOptimeTupelPattern unbindS() {
		parameters.remove("s");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	op --> " + parameters.get("op") + java.lang.System.lineSeparator();
		s += "	s --> " + parameters.get("s") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
