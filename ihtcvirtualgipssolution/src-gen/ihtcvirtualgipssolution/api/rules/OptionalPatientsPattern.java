package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.OptionalPatientsMatch;
import ihtcvirtualmetamodel.Patient;
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
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@4876d731 (name: p)] which does the following:
 * Pattern to find all optional patients
 */
@SuppressWarnings("unused")
public class OptionalPatientsPattern extends GraphTransformationPattern<OptionalPatientsMatch, OptionalPatientsPattern> {
	private static String patternName = "optionalPatients";

	/**
	 * Creates a new pattern optionalPatients().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public OptionalPatientsPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public OptionalPatientsMatch convertMatch(final IMatch match) {
		return new OptionalPatientsMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("p");
		return names;
	}

	/**
	 * Binds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public OptionalPatientsPattern bindP(final Patient object) {
		parameters.put("p", Objects.requireNonNull(object, "p must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public OptionalPatientsPattern unbindP() {
		parameters.remove("p");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	p --> " + parameters.get("p") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
