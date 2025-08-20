package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.SurgeonOTForDayMatch;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.OT;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@11af1917 (name: op), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@1c1669eb (name: ot), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@28235a46 (name: s)] which does the following:
 * Used in a mapping to save the information if a surgeon is operating in a specific OT for each day
 */
@SuppressWarnings("unused")
public class SurgeonOTForDayPattern extends GraphTransformationPattern<SurgeonOTForDayMatch, SurgeonOTForDayPattern> {
	private static String patternName = "surgeonOTForDay";

	/**
	 * Creates a new pattern surgeonOTForDay().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public SurgeonOTForDayPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public SurgeonOTForDayMatch convertMatch(final IMatch match) {
		return new SurgeonOTForDayMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("op");
		names.add("ot");
		names.add("s");
		return names;
	}

	/**
	 * Binds the node op to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOTForDayPattern bindOp(final OpTime object) {
		parameters.put("op", Objects.requireNonNull(object, "op must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node op to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOTForDayPattern unbindOp() {
		parameters.remove("op");
		return this;
	}

	/**
	 * Binds the node ot to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOTForDayPattern bindOt(final OT object) {
		parameters.put("ot", Objects.requireNonNull(object, "ot must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node ot to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOTForDayPattern unbindOt() {
		parameters.remove("ot");
		return this;
	}

	/**
	 * Binds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOTForDayPattern bindS(final Surgeon object) {
		parameters.put("s", Objects.requireNonNull(object, "s must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node s to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public SurgeonOTForDayPattern unbindS() {
		parameters.remove("s");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	op --> " + parameters.get("op") + java.lang.System.lineSeparator();
		s += "	ot --> " + parameters.get("ot") + java.lang.System.lineSeparator();
		s += "	s --> " + parameters.get("s") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
